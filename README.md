loaddir-maven
=============

Load a flat lib directory to maven format based on a properties list mapping artifact names to the full maven url

Example
============

groovy DeployDirMvnArtifacts.groovy /temp/mvnArtifacts.txt Downloads/openrdf-sesame-2.6.4/lib/ /temp/mvnout

This will load the /temp/mvnArtifacts.txt as the properties file, reading the libraries from Downloads/openrdf-sesame-2.6.4/lib, and output the maven directory structure at /temp/mvnout
