package com.test.matchmaker.util;


import javax.validation.constraints.NotNull;
import java.util.DoubleSummaryStatistics;
import java.util.LongSummaryStatistics;
import java.util.stream.DoubleStream;
import java.util.stream.LongStream;


public class MathUtils {

	public static double calculateVariation(@NotNull double[] array) {
		return calculateStandardDeviation(array) / calculateMean(array) * 100;
	}

	public static double calculateStandardDeviation(@NotNull double[] array) {
		double newSum = 0;
		double mean = calculateMean(array);

		for (double value : array) {
			newSum = newSum + ((value - mean) * (value - mean));
		}
		double squaredDiffMean = (newSum) / (array.length);
		return Math.sqrt(squaredDiffMean);
	}

	public static double calculateMean(@NotNull double[] array) {
		double sum = 0;
		for (double value : array) {
			sum = sum + value;
		}
		return  (sum) / (array.length);
	}

	public static DoubleSummaryStatistics calculateSummaryStatistics(@NotNull double[] array) {
		return DoubleStream.of(array).summaryStatistics();
	}

	public static LongSummaryStatistics calculateSummaryStatistics(@NotNull long[] array) {
		return LongStream.of(array).summaryStatistics();
	}
}


