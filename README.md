geo locator 
=========

This is a public release version of the geo-locator, which includes the geoparser, which is a location entity recognizer, and a location resolver, which tries to attach the correct geo-location to the location entites.

Running: 1. Build a folder "GeoNames" in the project folder, and put http://download.geonames.org/export/dump/allCountries.txt into it.

Run the GazIndexer.java in edu.cmu.geoparser.resource.gazIndexing, and set the parameters of the java program parameter as: -write GeoNames/allCountries.txt. Then run the program. It will generate a folder GazIndex in the project folder, which is the index of the gazetteer.

Goto the edu.cmu.geoparser.ui.CmdinputParser.java. Set the java virtual machine parameter to -Xmx3072m, and then run the program, after waiting sometime loading the resource, you will then able to test the parser in the commandline. If you are using eclipse, you can input the test sentences into the Console directly.