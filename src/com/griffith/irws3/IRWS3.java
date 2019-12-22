package com.griffith.irws3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IRWS3 {

	public static void main(String[] args) throws IOException {
		/* read input data */
		final List<String> inputData = getInputData(args[0]);
		Map<String, List<String>> groupedData = separateAB(inputData);
		int toSplit = inputData.get(0).split(";")[2].length() / Integer.parseInt(args[1]);
		processInputData(groupedData, toSplit, Integer.parseInt(args[1]));

	}

	/**
	 * Maps Engine name to queries Key : Engine name value : List of queries run on
	 * corresponding engine
	 */
	private static Map<String, List<String>> separateAB(final List<String> data) {
		return data.stream().collect(Collectors.groupingBy(str -> str.split(";")[1]));

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

	private static void processInputData(final Map<String, List<String>> data, final int partitions, final int splt) {
		Map<String, List<String>> engine_vstrings = new HashMap<>();
		data.entrySet().forEach(dt -> {
			List<String> dtList = dt.getValue();
			List<String> vStrings = splitToPortionsAndCalculate(dtList, partitions, splt);
			engine_vstrings.put(dt.getKey(), vStrings);
		});
		
		
		System.out.println(engine_vstrings);

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

	/*
	 * Loop through the string, get count of 'R'
	 */
	private static int getRCount(final String engine) {
		int count = 0;
		for (int i = 0; i < engine.length(); i++) {
			if (engine.charAt(i) == 'R') {
				++count;
			}
		}
		return count;
	}

}
