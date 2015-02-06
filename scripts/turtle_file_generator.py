#!/usr/bin/python

import json, argparse, sys, random, traceback

generated_ids = {}
supported_categories = [u'Restaurants',
 u'Food',
 u'Bars',
 u'Mexican',
 u'American (Traditional)',
 u'Fast Food',
 u'Pizza',
 u'Hotels & Travel',
 u'Sandwiches',
 u'Coffee & Tea',
 u'American (New)',
 u'Italian',
 u'Chinese',
 u'Hotels',
 u'Burgers',
 u'Grocery',
 u'Breakfast & Brunch',
 u'Ice Cream & Frozen Yogurt',
 u'Specialty Food',
 u'Bakeries',
 u'Pubs',
 u'Japanese',
 u'Sports Bars',
 u'Convenience Stores',
 u'Delis',
 u'Sushi Bars',
 u'Steakhouses',
 u'Cafes',
 u'Seafood',
 u'Desserts',
 u'Buffets',
 u'Barbeque',
 u'Thai',
 u'Mediterranean',
 u'Beer, Wine & Spirits',
 u'Chicken Wings',
 u'Asian Fusion',
 u'Juice Bars & Smoothies',
 u'Greek',
 u'Indian',
 u'Tex-Mex',
 u'Donuts',
 u'Diners',
 u'Hot Dogs',
 u'Vietnamese',
 u'Wine Bars',
 u'Local Flavor',
 u'Salad',
 u'Dive Bars',
 u'Vegetarian',
 u'British',
 u'French',
 u'Bagels',
 u'Korean',
 u'Ethnic Food',
 u'Hawaiian',
 u'Caterers',
 u'Gluten-Free',
 u'Middle Eastern',
 u'Farmers Market',
 u'Gastropubs',
 u'Latin American',
 u'Food Trucks',
 u'Karaoke',
 u'Candy Stores',
 u'Breweries',
 u'Fish & Chips',
 u'Vegan',
 u'Gay Bars',
 u'Chocolatiers & Shops',
 u'Food Delivery Services',
 u'Pakistani',
 u'Shaved Ice',
 u'Food Stands',
 u'Filipino',
 u'Cocktail Bars',
 u'Southern',
 u'Hookah Bars',
 u'Cajun/Creole',
 u'Irish',
 u'Tea Rooms',
 u'Soul Food',
 u'Soup',
 u'Caribbean',
 u'Spanish',
 u'Tapas/Small Plates',
 u'Fruits & Veggies',
 u'Cheesesteaks',
 u'Tapas Bars',
 u'Sports Clubs',
 u'Dim Sum',
 u'Comfort Food',
 u'Modern European',
 u'Scottish',
 u'Creperies',
 u'Cheese Shops']

def open_files(file_in, file_out):
    try:
        return open(file_in, 'r'), open(file_out, 'w')
    except:
        print 'Error opening files'


def category_name_to_class_name(cat_name):
    """
    Generates a turtle category name given a yelp category name as input
    :param cat_name: Yelp category name
    :return: Turtle compatible category name
    """
    return cat_name.replace('-','').replace(' ','').replace('&','And').replace(',','').replace('/','').replace('(','').replace(')','')


def add_turtle_headers(file_out):
    """
    Writes the static Turtle headers to the file
    :param file_out:
    """
    file_out.write("""@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix base: <http://sv.cmu.edu/yoda#> .
""")


def write_turtle_line(json_data, file_out):
    """
    Parses a single object of json_data and writes it to the turtle file
    :param json_data: Data to be parsed and written
    :param file_out: File object of the output turtle file
    """
    poi_id = generate_poi_id()
    entry_prefix = 'base:POI_' + str(poi_id) + ' '
    # TODO: Add type
    try:
        turtle_label = generate_label(json_data.get('categories'))
        if turtle_label:
            file_out.write(entry_prefix + 'rdf:type base:' + turtle_label + ' .\n')
            # Label
            file_out.write(entry_prefix + 'rdfs:label "' + json_data.get('name').encode('utf-8').strip().replace('"', '') + '"^^xsd:string .\n')
            # Stars
            if json_data.get('stars'):
                file_out.write(entry_prefix + 'base:yelp_stars ' + str(json_data.get('stars')) + ' .\n')
            # Review count
            if json_data.get('review_count'):
                file_out.write(entry_prefix + 'base:yelp_review_count ' + str(json_data.get('review_count')) + ' .\n')
            # GPS info
            if json_data.get('latitude') and json_data.get('longitude'):
                file_out.write(entry_prefix + 'base:gps_lat ' + str(json_data.get('latitude')) + ' .\n')
                file_out.write(entry_prefix + 'base:gps_lon ' + str(json_data.get('longitude')) + ' .\n')
            # Add finer attributes
            if json_data.get('attributes'):
                if json_data.get('attributes').get('Price Range'):
                    file_out.write(entry_prefix + 'base:PriceRange ' + str(json_data.get('attributes').get('Price Range')) + ' .\n')
                # if json_data.get('attributes').get('Attire'):
                #     file_out.write(entry_prefix + 'base:Attire "' + json_data.get('attributes').get('Attire') + '"^^xsd:string .\n')
                # if json_data.get('attributes').get('Take-out'):
                #     file_out.write(entry_prefix + 'base:TakeOut "' + str(json_data.get('attributes').get('Take-out')) + '"^^xsd:boolean .\n')
                # if json_data.get('attributes').get('Accepts Credit Cards'):
                #     file_out.write(entry_prefix + 'base:AcceptsCreditCards "' + str(json_data.get('attributes').get('Accepts Credit Cards')) + '"^^xsd:boolean .\n')
                # if json_data.get('attributes').get('Good for Kids'):
                #     file_out.write(entry_prefix + 'base:GoodForKids "' + str(json_data.get('attributes').get('Good for Kids')) + '"^^xsd:boolean .\n')
                # if json_data.get('attributes').get('Wheelchair Accessible'):
                #     file_out.write(entry_prefix + 'base:WheelchairAccessible "' + str(json_data.get('attributes').get('Wheelchair Accessible')) + '"^^xsd:boolean .\n')
                # if json_data.get('attributes').get('Outdoor Seating'):
                #     file_out.write(entry_prefix + 'base:OutdoorSeating "' + str(json_data.get('attributes').get('Outdoor Seating')) + '"^^xsd:boolean .\n')
                # if json_data.get('attributes').get('Good For Groups'):
                #     file_out.write(entry_prefix + 'base:GoodForGroups "' + str(json_data.get('attributes').get('Good For Groups')) + '"^^xsd:boolean .\n')
                # if json_data.get('attributes').get('Waiter Service'):
                #     file_out.write(entry_prefix + 'base:WaiterService "' + str(json_data.get('attributes').get('Waiter Service')) + '"^^xsd:boolean .\n')
                # if json_data.get('attributes').get('Alcohol'):
                #     if json_data.get('attributes').get('Alcohol') == 'none':
                #         file_out.write(entry_prefix + 'base:Alcohol "false"^^xsd:boolean .\n')
                #     else:
                #         file_out.write(entry_prefix + 'base:Alcohol "true"^^xsd:boolean .\n')
                # if json_data.get('attributes').get('Good For'):
                #     for key, value in json_data.get('attributes').get('Good For').iteritems():
                #         file_out.write(entry_prefix + 'base:GoodFor' + key.capitalize() + ' "' + str(value) + '"^^xsd:boolean .\n')
            sys.stdout.write('.')
            return True
        else:
            sys.stdout.write('R')
            return None
    except:
        traceback.print_exc(file=sys.stdout)


def generate_label(categories):
    """
    Given a list of categories for a n object, returns the rdf type to be used
    :param categories:
    :return:
    """
    for category in categories:
        try:
            supported_categories.index(category)
            # Stop looking if found
            return category_name_to_class_name(category)
        except:
            # Category not found in supported
            continue
    # If nothing found, say so
    return None


def generate_poi_id():
    """
    Generates a unique ID for a POI entry to use in the turtle file
    :return: Unique ID in String format
    """
    while True:
        candidate = random.randint(100000000, 100000000000)
        if not generated_ids.get(candidate):
            # Found an unused ID
            generated_ids[candidate] = True
            return candidate


def generate_turtle_file(file_in, file_out):
    """
    Parses the JSON data from the input file and generates a turtle POI file
    :param file_in: File object to get input JSON data
    :param file_out: File object to write output file
    """
    if not (file_in and file_in.mode == 'r' and file_out and file_out.mode == 'w'):
        print 'ERROR: File object null or incorrect mode'

    add_turtle_headers(file_out)
    unparsed = 0
    parsed = 0
    rejected = 0
    written = 0

    for line in file_in:
        json_data = None
        try:
            if write_turtle_line(json.loads(line), file_out):
                written += 1
            else:
                rejected += 1
        except:
            sys.stdout.write('X')
            unparsed += 1
            if unparsed <= 10:
                print 'ERROR: Could not parse line \'' + line + '\''
        else:
            parsed += 1

    print '\n\n' + '='*50
    print "Parsed objects: " + str(parsed) + "\t Unparsed lines: " + str(unparsed)
    print "Written objects: " + str(written) + "\t Rejected objects: " + str(rejected)
    print '=' * 50

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('file_in', help='Input filename containing JSON data')
    parser.add_argument('file_out', help='Output turtle filename')
    args = parser.parse_args()

    file_in, file_out = open_files(args.file_in, args.file_out)
    generate_turtle_file(file_in, file_out)

