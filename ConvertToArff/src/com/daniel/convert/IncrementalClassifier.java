/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    IncrementalClassifier.java
 *    Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 *
 */

package com.daniel.convert;

import java.io.File;
import java.util.Random;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 * This example trains NaiveBayes incrementally on data obtained from the
 * ArffLoader.
 * 
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 5628 $
 */
public class IncrementalClassifier {

	/**
	 * Expects an ARFF file as first argument (class attribute is assumed to be
	 * the last attribute).
	 * 
	 * @param args
	 *            the commandline arguments
	 * @throws Exception
	 *             if something goes wrong
	 */
	public static BayesNet treinar(String[] args) throws Exception {
		// load data
		ArffLoader loader = new ArffLoader();
		loader.setFile(new File(args[0]));
		Instances structure = loader.getStructure();
		structure.setClassIndex(structure.numAttributes() - 1);

		// train NaiveBayes
		BayesNet BayesNet = new BayesNet();

		Instance current;
		while ((current = loader.getNextInstance(structure)) != null) {
			structure.add(current);
		}
		BayesNet.buildClassifier(structure);

		// output generated model
		// System.out.println(nb);

		// test set
		BayesNet BayesNetTest = new BayesNet();

		// test the model
		Evaluation eTest = new Evaluation(structure);
		// eTest.evaluateModel(nb, structure);
		eTest.crossValidateModel(BayesNetTest, structure, 15, new Random(1));

		// Print the result à la Weka explorer:
		String strSummary = eTest.toSummaryString();
		System.out.println(strSummary);
		
		return BayesNet;
	}
}
