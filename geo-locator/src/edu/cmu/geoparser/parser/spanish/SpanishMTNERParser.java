/**
 * 
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 * 
 * @author Wei Zhang,  Language Technology Institute, School of Computer Science, Carnegie-Mellon University.
 * email: wei.zhang@cs.cmu.edu
 * 
 */
package edu.cmu.geoparser.parser.spanish;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.nlp.ner.FeatureExtractor.FeatureGenerator;
import edu.cmu.geoparser.nlp.tokenizer.EuroLangTwokenizer;
import edu.cmu.geoparser.parser.NERTagger;
import edu.cmu.minorthird.classify.ClassLabel;
import edu.cmu.minorthird.classify.Example;
import edu.cmu.minorthird.classify.Feature;
import edu.cmu.minorthird.classify.MutableInstance;
import edu.cmu.minorthird.classify.sequential.SequenceClassifier;
import edu.cmu.minorthird.util.IOUtil;

public class SpanishMTNERParser implements NERTagger{

	SequenceClassifier model;
	FeatureGenerator fgen;
	/**
	 * Default protected constructor
	 * 
	 * @throws IOException
	 * 
	 */

	public SpanishMTNERParser(String modelname,FeatureGenerator fgen) {
		try {
			model = (SequenceClassifier) IOUtil.loadSerialized(new java.io.File(modelname));
			this.fgen = fgen;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Identifying location names by NER
	Example[] examples;

	public List<String> parse(Tweet tweet) {

		String[] t_tweet = (EuroLangTwokenizer.tokenize(tweet.getText())).toArray(new String[] {});

		examples = new Example[t_tweet.length];

		// instances that is converted to feature list.
		List<Feature[]> feature_instances = fgen.extractFeature(t_tweet);

		for (int i = 0; i < examples.length; i++) {

			ClassLabel label = new ClassLabel("NA");
			MutableInstance instance = new MutableInstance("0", Integer.toString(i));

			Feature[] features = feature_instances.get(i);

			for (int j = 0; j < features.length; j++) {
				instance.addBinary(features[j]);
			}
			examples[i] = new Example(instance, label);
			// System.out.println(examples[i].toString());
		}
		ClassLabel[] resultlabels = model.classification(examples);
		/*
		 * for (int i = 0; i < resultlabels.length; i++) {
		 * System.out.print(resultlabels[i].bestClassName() + " "); }
		 */
		List<String> matches = tweet.getMatches();
		if (matches == null)
			matches = new ArrayList<String>();

		String buffer = "";
		String previous = "", current = "";
		for (int k = 0; k < resultlabels.length; k++) {
			current = resultlabels[k].bestClassName();
			if (current.equals("O")) {
				if (previous.length() == 0)
					previous = current;
				else if (previous.equals("O")) {
					continue;
				} else {
					buffer = previous + "{" + buffer.trim() + "}" + previous;
					if (!matches.contains(buffer))
						matches.add(buffer);
					buffer = "";
					previous = current;
				}
			} else {
				if (previous.length() == 0 || previous.equals("O")) {
					previous = current;
					buffer += " " + t_tweet[k];
				} else {
					if (previous.equals(current))
						buffer += " " + t_tweet[k];
					else {
						buffer = previous + "{" + buffer.trim() + "}" + previous;
						if (!matches.contains(buffer))
							matches.add(buffer);
						previous = current;
					}
				}
			}
		}

		tweet.setMatches(matches);
		return matches;
	}


}
