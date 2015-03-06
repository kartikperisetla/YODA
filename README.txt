# -------------------------------------------------------------
# README.txt
# Yetanother Ontological Dialog Architecture - YODA
# Copyright (c) David Cohen, 2014
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

README last updated on Mar 5, 2015

YODA requires the Java 8 language level and SDK.
I'm currently experiencing issues using maven to build executables and run them from the command line.
Until then, I suggest opening the project in an IDE and using the IDE's 'play' button.
I have used Intellij IDEA 14, I import the pom.xml file as a Maven project, and I can run both of the demo systems.

To run the old version of the Yelp system from the command line, do:
> git checkout 9b14c44

To build, go to yoda/yoda/ and run:
> mvn compile

To run the CommandLineYodaSystem program, go to /yoda/yoda/ and run:
> mvn exec:java

You may see messages such as:
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.

But you can ignore them and proceed to type your utterances into the command line.
