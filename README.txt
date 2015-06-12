# -------------------------------------------------------------
# README.txt
# Yetanother Ontological Dialog Architecture - YODA
# Copyright (c) David Cohen, 2015
# david.cohen@sv.cmu.edu
# This file is part of Yetanother Ontological Dialog Architecture (YODA).

# YODA is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.

# YODA is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.

# You should have received a copy of the GNU General Public License
# along with YODA.  If not, see <http://www.gnu.org/licenses/>.
# -------------------------------------------------------------

README last updated on June 12, 2015

The YODA tutorial and detailed instructions can be found at:
http://davidogbodfog.bitbucket.org/yoda.html

YODA requires the Java 8 language level and SDK.
YODA uses Maven to manage its dependencies.
It can be opened in most IDEs by importing the pom.xml file as a Maven project.


To compile and build executables (this will be slow the first time, and Maven requires an internet connection), from yoda/yoda/ run:

> mvn clean
> mvn compile
> mvn package

To run in the command line, from yoda/yoda/:

> java -cp target/yoda-0.5.0.jar:target/lib/* edu.cmu.sv.domain.smart_house.SmartHouseCommandLineSystem
or
> java -cp target/yoda-0.5.0.jar:target/lib/* edu.cmu.sv.domain.yelp_phoenix.YelpPhoenixCommandLineSystem

To run the subProcess systems (for integration into a complete SDS), run from yoda/yoda/:

> java -cp target/yoda-0.5.0.jar:target/lib/* edu.cmu.sv.domain.smart_house.SmartHouseSubprocessSystem
or
> java -cp target/yoda-0.5.0.jar:target/lib/* edu.cmu.sv.domain.yelp_phoenix.YelpPhoenixSubprocessSystem




You may see messages such as:
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.

But you can ignore them and proceed to type your utterances into the command line as soon as it loads the domains.
When it is ready, it will give a message such as the following: 

loading domain spec ...YODA skeleton domain
loading domain spec ...Smart house domain
done loading domain

