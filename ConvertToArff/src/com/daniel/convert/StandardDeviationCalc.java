package com.daniel.convert;

import java.io.IOException;
import java.util.List;

public class StandardDeviationCalc {

	/**
	 * @param args
	 * @throws IOException
	 */

	double powerSum1 = 0;
	double powerSum2 = 0;
	double stdev = 0;
	
	AccessPoint AccessPoint = new AccessPoint();

	// for i = 0 to total.length {
	// powerSum1 += total[i];
	// powerSum2 += Math.pow(total[i], 2);
	// stdev = Math.sqrt(i*powerSum2 - Math.pow(powerSum1, 2))/i;
	// System.out.println(total[i]); // You specified that you needed to print
	// each value of the array
	// }
	// System.out.println(stdev); // This could also be placed inside the loop
	// for updates with each array value.

	public static double stdDev(List<AccessPoint> valores) throws IOException {

		//double[] values = { 9, 2, 5, 4, 12, 7, 8, 11, 9, 3, 7, 4, 12, 5, 4, 10,
				//9, 6, 9, 4 };

		// change input values here
		double sum = 0;
		double finalsum = 0;
		double average = 0;
		
		double[] values = new double[valores.size()];
		
		for (int i = 0; i < values.length; i++) {
			values[i] = Double.parseDouble(valores.get(i).getRssi());
		}

		for (double i : values) {
			finalsum = (sum += i);
		}

		average = finalsum / (values.length);

		//System.out.println("Average: " + average);

		double sumX = 0;
		double finalsumX = 0;
		double[] x1_average = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			double fvalue = (Math.pow((values[i] - average), 2));

			x1_average[i] = fvalue;
			//System.out.println("test: " + fvalue);

		}

		for (double i : x1_average) {
			finalsumX = (sumX += i);
		}

		Double AverageX = finalsumX / (values.length);

		//System.out.println("E(X1-x1_average)^2/AverageX: " + AverageX);

		double SquareRoot = Math.sqrt(AverageX);

		//System.out.println("Standard Deviation: " + SquareRoot);
		
		return SquareRoot;

	}
}