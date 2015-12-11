@echo off
:title           :run.sh
:description     :Batch script to run Ontology Project.
:author		 	 :orbit software solutions
:date            :2015-12-11
:version         :1.0    
:usage		 	 :run.bat
:==============================================================================

java -Xmx128m -jar ./target/ontology-v1.0-jar-with-dependencies.jar tweets.txt ontology.json %*