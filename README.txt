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

(README last updated Dec. 13, 2014)

Running current code:
This project is structured as a maven project which is supported by all the major IDEs by importing the yoda/yoda/pom.xml file .
It requires the Java 8 language level and SDK.

To build, go to yoda/yoda/ and run:
> mvn compile

To run the CommandLineYodaSystem program, go to /yoda/yoda/ and run:
> mvn exec:java

You may see messages such as:
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.

But you can ignore them and proceed to type your utterances into the command line.





Installing Theano:
follow directions for your operating system on Theano website.
For recent Ubuntu, http://deeplearning.net/software/theano/install_ubuntu.html#install-ubuntu
