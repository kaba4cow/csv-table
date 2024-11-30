package com.kaba4cow.csvtable;

import java.util.ArrayList;
import java.util.List;

class CSVParser {

	private CSVParser() {}

	static List<String> parseSource(String source) {
		List<String> list = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		boolean quotes = false;
		char[] chars = source.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c == '\"') {
				if (quotes && i < chars.length - 1 && chars[i + 1] == '\"') {
					builder.append('\"');
					i++;
				} else
					quotes = !quotes;
			} else if (c == '\n' || c == '\r' && (i >= chars.length - 1 || chars[i + 1] != '\n')) {
				if (!quotes) {
					if (!builder.toString().trim().isEmpty())
						list.add(builder.toString());
					builder.setLength(0);
				} else
					builder.append(c);
			} else
				builder.append(c);
		}
		if (builder.length() > 0 && !builder.toString().trim().isEmpty())
			list.add(builder.toString());
		return list;
	}

	static List<Object> parseLine(String line, char delimiter) {
		List<Object> list = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		boolean quotes = false;
		char[] chars = line.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (quotes) {
				if (c == '\"') {
					if (i < chars.length - 1 && chars[i + 1] == '\"') {
						builder.append('\"');
						i++;
					} else
						quotes = false;
				} else
					builder.append(c);
			} else if (c == '\"')
				quotes = true;
			else if (c == delimiter) {
				list.add(parseColumn(builder.toString()));
				builder.setLength(0);
			} else
				builder.append(c);
		}
		list.add(parseColumn(builder.toString()));
		return list;
	}

	private static Object parseColumn(String column) {
		if (column.startsWith("\"") && column.endsWith("\"") && column.length() >= 2)
			return column.substring(1, column.length() - 1).replace("\"\"", "\"");
		else if (column.trim().isEmpty())
			return null;
		else
			return column.trim();
	}

}
