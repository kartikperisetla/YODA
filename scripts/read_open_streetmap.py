#!/usr/bin/env python 
# read_open_streetmap.py
# David Cohen
import xml.sax
import Queue
import collections as C
import pickle
import sys

osm_source = sys.argv[1]
database_file = sys.argv[2]

class observation:
    def __init__(self, lat, lon, time):
        self.lat = lat
        self.lon = lon
        self.time = time

class node(object):
    """
    Class to contain OSM nodes
    """
    def __init__(self, id, lat, lon):
        self.id = id
        self.lat = lat
        self.lon = lon
        self.connections = []

class OSMContentHandler(xml.sax.ContentHandler):
    def __init__(self):
        xml.sax.ContentHandler.__init__(self)
        self.nodes = dict()     # map node id to node object
        self.amenities = dict() # map node id to node object
        self.nodes_on_roads = set()
        # keep track of the relevant parse state
        self.current_node = None
        self.current_node_name = None
        self.current_node_is_amenity = False
        self.current_amenity_value = None
        self.inside_way_tag = False
        self.current_way_is_highway = False
        self.current_way_nodes_list = []

    def startElement(self, name, attrs):
        if name == 'node':
            self.nodes[attrs.getValue("id")] = node(attrs.getValue("id"),float(attrs.getValue("lat")),float(attrs.getValue("lon")))
            self.current_node = attrs.getValue("id")
        if name == 'way':
            self.inside_way_tag = True
        if name =='nd':
            ref = attrs.getValue('ref')
            if self.inside_way_tag:
                self.current_way_nodes_list.append(ref)
        if name == 'tag':
            if not self.current_node is None:
                if attrs.getValue('k') == 'amenity':
                    self.current_node_is_amenity = True
                    self.current_amenity_value = attrs.getValue('v')
                if attrs.getValue('k') == 'name':
                    self.current_node_name = attrs.getValue('v')
            if self.inside_way_tag:
                if attrs.getValue('k') == 'highway':
                    self.current_way_is_highway = True

    def endElement(self, name):
        if name == 'way':
            if self.current_way_is_highway:
                # add connections to the nodes listed in the way
                for i in range(len(self.current_way_nodes_list)):
                    self.nodes_on_roads.add(self.nodes[self.current_way_nodes_list[i]])
                    if i > 0:
                        self.nodes[self.current_way_nodes_list[i]].connections.append(self.current_way_nodes_list[i-1])
                    if i < len(self.current_way_nodes_list) - 1:
                        self.nodes[self.current_way_nodes_list[i]].connections.append(self.current_way_nodes_list[i+1])
            self.current_way_nodes_list = []
            self.current_way_is_highway = False
            self.inside_way_tag = False

        if name == 'node':
            if self.current_node_is_amenity and not self.current_node_name is None:
                self.amenities[self.current_node] = self.nodes[self.current_node]
                self.amenities[self.current_node].amenity_type = self.current_amenity_value
                self.amenities[self.current_node].name = self.current_node_name
                
            self.current_node_name = None
            self.current_node_is_amenity = False




#### Define Map class
class Map:
    """
    Stores the node, way, and amenity data for a map
    Also contains pathfinding functions and maintains appropriate caches for those functions

    nodes : a dict mapping OSM node id to the node objects for all the nodes in the map
    ways : list of all the ways
    roads : list of all the ways if 'highway' is in the tags
    amenities : a dict mapping name to node for any named amenity
    ... others
    """
    def __init__(self, map_filename):
        a = OSMContentHandler()
        xml.sax.parse(open(map_filename,'r'), a)
        self.nodes = a.nodes
        self.amenities = a.amenities
        self.nodes_on_roads = a.nodes_on_roads
        self.path_cache = dict()
        self.nearest_road_node_cache = dict()

    def path_length(self, path):
        """
        points in the path can be nodes, observations, or gps data point tuples (lat, lon)
        """
        ans = 0
        if path is None:
            return 0
        for i in range(len(path) - 1):
            if isinstance(path[i], node) or isinstance(path[i], observation):
                lat1 = path[i].lat
                lon1 = path[i].lon
            else:
                lat1 = path[i][0]
                lon1 = path[i][1]
            if isinstance(path[i+1], node) or isinstance(path[i+1], observation):
                lat2 = path[i+1].lat
                lon2 = path[i+1].lon
            else:
                lat2 = path[i+1][0]
                lon2 = path[i+1][1]
            ans += pow(pow((lat1 - lat2), 2) + pow((lon1 - lon2), 2), .5)
        return ans
            
    def nearest_road_node(self, target):
        """
        Find the road node nearest to the given lat lon position
        """
        if target in self.nearest_road_node_cache.keys():
            return self.nearest_road_node_cache[target]
        self.nearest_road_node_cache[target] = min(self.nodes_on_roads, key=lambda x: self.distance(target, x))
        return self.nearest_road_node_cache[target]

    def travel_time(self, path):
        return 10000 * self.path_length(path)
            
    def external_travel_time(self, dest1, dest2):
        n1 = self.nodes[dest1]
        n2 = self.nodes[dest2]
        return self.travel_time(self.pathfind(self.nearest_road_node(n1), 
                                              self.nearest_road_node(n2)))

    def external_distance(self, dest1, dest2):
        n1 = self.nodes[dest1]
        n2 = self.nodes[dest2]
        return self.path_length(self.pathfind(self.nearest_road_node(n1), 
                                              self.nearest_road_node(n2)))

    def pathfind(self, n1, n2):
        """
        find a path from n1 to n2 using a*
        """ 
        if (n1, n2) in self.path_cache.keys():
            return self.path_cache[(n1, n2)]

        already_expanded = set()
        Q = Queue.PriorityQueue()
        Q.put((0, [n1]))

        while not Q.empty():
            item = Q.get()
            if item[1][len(item[1])-1] in already_expanded:
                continue
            if item[1][len(item[1])-1] == n2:
                self.path_cache[(n1, n2)] = item[1]
                return item[1]
            already_expanded.add(item[1][len(item[1])-1])
            for c in item[1][len(item[1])-1].connections:
                c = self.nodes[c]
                if not c in already_expanded:
                    Q.put((item[0]+self.distance(item[1][len(item[1])-1], c), item[1]+[c]))

        print "no path found"

    def distance(self, n1, n2):
        """
        Inputs can be nodes or observations (or any objects with lat and lon defined)
        This is currently euclidean distance, which is way wrong.
        Will soon replace with haversin.
        """
        return pow(pow(n1.lat - n2.lat, 2) + pow(n1.lon - n2.lon, 2), .5)

    def distance_from_segment(self, obs, endpoint_node_1, endpoint_node_2):
        ans = self.distance(obs, endpoint_node_1)
        ans += self.distance(obs, endpoint_node_2)
        ans -= self.distance(endpoint_node_1, endpoint_node_2)
        return ans


# Map amenity tag -> yoda class name
amenity_tag_yoda_class_map = dict()
amenity_tag_yoda_class_map['parking'] = 'Parking'
amenity_tag_yoda_class_map['place_of_worship'] = "PlaceOfWorship"
amenity_tag_yoda_class_map['school'] = "School"
amenity_tag_yoda_class_map['bench'] = "Bench"
amenity_tag_yoda_class_map['restaurant'] = "Restaurant"
amenity_tag_yoda_class_map['fuel'] = "GasStation"
amenity_tag_yoda_class_map['post_box'] = "MailBox"
amenity_tag_yoda_class_map['bank'] = "Bank"
amenity_tag_yoda_class_map['grave_yard'] = "GraveYard"
amenity_tag_yoda_class_map['fast_food'] = "FastFood"
amenity_tag_yoda_class_map['cafe'] = "Cafe"
amenity_tag_yoda_class_map['recycling'] = "Recycling"
amenity_tag_yoda_class_map['kindergarten'] = "Kindergarten"
amenity_tag_yoda_class_map['pharmacy'] = "Pharmacy"
amenity_tag_yoda_class_map['hospital'] = "Hospital"
amenity_tag_yoda_class_map['post_office'] = "PostOffice"
amenity_tag_yoda_class_map['public_building'] = "PublicBuilding"
amenity_tag_yoda_class_map['bicycle_parking'] = "BicycleParking"
amenity_tag_yoda_class_map['pub'] = "Bar"
amenity_tag_yoda_class_map['toilets'] = "Restroom"
amenity_tag_yoda_class_map['waste_basket'] = "GarbageCan"
amenity_tag_yoda_class_map['shelter'] = "Shelter"
amenity_tag_yoda_class_map['telephone'] = "PublicTelephone"


if __name__ == '__main__':
    nav_map = Map(osm_source)

    import codecs
    f = codecs.open(database_file, encoding='utf-8', mode='w')
    prefixes = """\
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix base: <http://sv.cmu.edu/yoda#> .
"""
    f.write(prefixes)

    poi_cls = "base:PointOfInterest"

    converted_amenities = set()
    for amenity in set(nav_map.amenities.values()):
        if amenity.id in converted_amenities:
            continue
        if '"' in amenity.name:
            print "this name contains a quote character!", amenity.name
        if amenity.amenity_type in amenity_tag_yoda_class_map.keys():
            f.write("base:POI_"+amenity.id+" rdf:type base:"+amenity_tag_yoda_class_map[amenity.amenity_type]+" . \n")
            f.write("base:POI_"+amenity.id+" rdfs:label "+'"'+amenity.name+'"^^xsd:string'+" . \n")
            converted_amenities.add(amenity.id)

    f.close()

    print "Number of converted amenities:", len(converted_amenities)
    print "Total number of amenities:", len(nav_map.amenities)



        # print amenity.id, amenity.name, amenity.amenity_type

        
