#!/bin/bash
#title           :run.sh
#description     :Shell script to run Ontology Project.
#author		 	 :orbit software solutions
#date            :2015-12-11
#version         :1.0    
#usage		 	 :bash run.sh
#==============================================================================
java -Xmx128m -jar ./target/ontology-v1.0-jar-with-dependencies.jar testtweets.txt testontology.json %*