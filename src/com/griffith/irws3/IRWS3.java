package com.griffith.irws3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class IRWS3 {

	public static void main(String[] args) throws IOException {
		/* read input data */
		final List<String> inputData = getInputData(args[0]);
		final Map<String, List<String>> groupedData = separateAB(inputData);
		final int toSplit = inputData.get(0).split(";")[2].length() / Integer.parseInt(args[1]);
		final Map<String, List<Double>> engine_probfuse = processInputData(groupedData, toSplit, Integer.parseInt(args[1]));
		System.out.println(engine_probfuse);
	}

	/**
	 * Maps Engine name to queries Key : Engine name value : List of queries run on
	 * corresponding engine
	 */
	private static Map<String, List<String>> separateAB(final List<String> data) {
		return data.stream()
				.collect(Collectors.groupingBy(str -> str.split(";")[1], TreeMap::new, Collectors.toList()));

	}

	/*
	 * Read the input file from given file name Expects the file to be in the class
	 * path if not given the absolute path
	 */
	private static List<String> getInputData(final String arg) throws IOException {

		try (final FileReader fr = new FileReader(arg); final BufferedReader br = new BufferedReader(fr)) {
			final List<String> response = new ArrayList<>();
			String st;
			while ((st = br.readLine()) != null) {
				response.add(st);
			}
			return response;
		}
	}

	private static Map<String, List<Double>> processInputData(final Map<String, List<String>> data, final int partitions, final int splt) {
		Map<String, List<String>> engine_vstrings = new TreeMap<>();
		data.entrySet().forEach(dt -> {
			List<String> dtList = dt.getValue();
			List<String> vStrings = splitToPortionsAndCalculate(dtList, partitions, splt);
			engine_vstrings.put(dt.getKey(), vStrings);
		});
		return doCalculations(engine_vstrings, partitions, splt);

	}

	/**
	 * This function gets the result of the segments as double for both engine A and B
	 * Key => Engine
	 * Value => List of probfuse values for each input vertically sliced data 
	 * @return 
	 * */
	private static Map<String, List<Double>> doCalculations(final Map<String, List<String>> engine_vStrings, final int partitions,
			final int spltNum) {
		int queriesCount = engine_vStrings.size();
		Map<String, List<Double>> finalMap = new TreeMap<>();
		engine_vStrings.entrySet().forEach(ev -> {
			List<Double> finalValues = new ArrayList<>();
			List<String> vString = ev.getValue();

			int temp = 1;
			int totalRs = 0;
			for (int i = 0; i < vString.size(); ++i) {

				// calculate segments
				for (int j = 0; j < queriesCount; ++j) {
					if ('R' == vString.get(i).charAt(j)) {
						++totalRs;
					}

				}
				double result = 0.0;
				if (temp >= partitions) {
					result = (double) totalRs / (queriesCount * partitions);
					for (int t = 0; t < partitions; ++t) {
						finalValues.add(result);
					}
					temp = 1;
					totalRs = 0;

				} else {
					// increment partition count
					++temp;
				}

			}

			finalMap.put(ev.getKey(), finalValues);

		});

		return finalMap;

	}

	/* convertes string into vertically split strings */
	private static List<String> splitToPortionsAndCalculate(final List<String> qList, final int partitions,
			final int spltNum) {
		int queryLength = qList.get(0).split(";")[2].length();
		List<String> verticalStrings = new ArrayList<>();
		for (int i = 0; i < queryLength; ++i) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < qList.size(); ++j) {
				sb.append(qList.get(j).split(";")[2].charAt(i));
			}
			verticalStrings.add(sb.toString());
		}

		return verticalStrings;
	}

}
