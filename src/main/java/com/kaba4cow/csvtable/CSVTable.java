package com.kaba4cow.csvtable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Represents a table of CSV data. Provides methods to manipulate rows, columns, and the delimiter used for CSV formatting.
 */
public class CSVTable {

	private final List<CSVRow> rows;
	private int columns;
	private char delimiter;

	/**
	 * Creates an empty {@code CSVTable} with a default delimiter {@code ','}.
	 */
	public CSVTable() {
		this.rows = new ArrayList<>();
		this.columns = 0;
		this.delimiter = ',';
	}

	/**
	 * Creates a {@code CSVTable} by parsing the provided source string using a default delimiter {@code ','}.
	 *
	 * @param source the CSV source string
	 */
	public CSVTable(String source) {
		this(source, ',');
	}

	/**
	 * Creates a {@code CSVTable} by parsing the provided source string using the specified delimiter.
	 *
	 * @param source    the CSV source string
	 * @param delimiter the delimiter used for separating columns in the CSV
	 */
	public CSVTable(String source, char delimiter) {
		this.rows = new ArrayList<>();
		this.columns = 0;
		this.delimiter = delimiter;
		if (!source.isEmpty()) {
			List<String> lines = CSVParser.parseSource(source);
			for (String line : lines) {
				CSVRow row = new CSVRow(this, CSVParser.parseLine(line, delimiter));
				columns = Math.max(columns, row.columns());
				rows.add(row);
			}
		}
	}

	/**
	 * Returns the first row of the table (header row).
	 *
	 * @return the first row of the table
	 */
	public CSVRow header() {
		return getRow(0);
	}

	/**
	 * Gets the row at the specified index.
	 *
	 * @param rowIndex the index of the row to retrieve
	 * 
	 * @return the row at the specified index
	 * 
	 * @throws IndexOutOfBoundsException if the row index is out of range
	 */
	public CSVRow getRow(int rowIndex) {
		checkRowRange(rowIndex);
		return rows.get(rowIndex);
	}

	/**
	 * Inserts a new row at the specified index.
	 *
	 * @param rowIndex the index at which the row should be inserted
	 * 
	 * @return the newly inserted row
	 */
	public CSVRow insertRow(int rowIndex) {
		CSVRow row = new CSVRow(this);
		rows.add(rowIndex, row);
		return row;
	}

	/**
	 * Adds a new row at the end of the table.
	 *
	 * @return the newly added row
	 */
	public CSVRow addRow() {
		CSVRow row = new CSVRow(this);
		rows.add(row);
		return row;
	}

	/**
	 * Removes the row at the specified index.
	 *
	 * @param rowIndex the index of the row to remove
	 * 
	 * @return the removed row
	 * 
	 * @throws IndexOutOfBoundsException if the row index is out of range
	 */
	public CSVRow removeRow(int rowIndex) {
		checkRowRange(rowIndex);
		return rows.remove(rowIndex);
	}

	/**
	 * Clears all the rows in the table by setting all columns to null.
	 *
	 * @return a reference to this object
	 */
	public CSVTable clearRows() {
		for (CSVRow row : rows)
			row.clear();
		return this;
	}

	/**
	 * Swaps the positions of two rows in the table.
	 *
	 * @param rowIndex1 the index of the first row
	 * @param rowIndex2 the index of the second row
	 * 
	 * @return a reference to this object
	 * 
	 * @throws IndexOutOfBoundsException if either of the row indexes is out of range
	 */
	public CSVTable swapRows(int rowIndex1, int rowIndex2) {
		checkRowRange(rowIndex1);
		checkRowRange(rowIndex2);
		Collections.swap(rows, rowIndex1, rowIndex2);
		return this;
	}

	/**
	 * Removes the column at the specified index.
	 *
	 * @param columnIndex the index of the column to remove
	 * 
	 * @return a reference to this object
	 * 
	 * @throws IndexOutOfBoundsException if the column index is out of range
	 */
	public CSVTable removeColumn(int columnIndex) {
		checkColumnRange(columnIndex);
		for (CSVRow row : rows)
			row.remove(columnIndex);
		resizeTable(columnCount() - 1);
		return this;
	}

	/**
	 * Inserts a new column at the specified index.
	 *
	 * @param columnIndex The index at which to insert the column.
	 * 
	 * @return a reference to this object
	 * 
	 * @throws IndexOutOfBoundsException If the column index is out of range.
	 */
	public CSVTable insertColumn(int columnIndex) {
		checkColumnRange(columnIndex);
		for (CSVRow row : rows)
			row.insert(columnIndex);
		columns++;
		return this;
	}

	/**
	 * Swaps the positions of two columns in the table.
	 *
	 * @param columnIndex1 The index of the first column.
	 * @param columnIndex2 The index of the second column.
	 * 
	 * @return a reference to this object
	 * 
	 * @throws IndexOutOfBoundsException If either of the column indexes is out of range.
	 */
	public CSVTable swapColumns(int columnIndex1, int columnIndex2) {
		if (columnIndex1 == columnIndex2)
			return this;
		for (CSVRow row : rows)
			row.swap(columnIndex1, columnIndex2);
		return this;
	}

	/**
	 * Resizes the table to the specified column count.
	 *
	 * @param columnCount The new number of columns.
	 * 
	 * @return a reference to this object
	 * 
	 * @throws IllegalArgumentException If the column count is less than 0.
	 */
	public CSVTable resizeTable(int columnCount) {
		if (columnCount <= -1)
			throw new IllegalArgumentException(String.format("Column count %s must be greater than -1", columnCount));
		columns = columnCount;
		for (CSVRow row : rows)
			row.resize(columnCount);
		return this;
	}

	/**
	 * Removes all empty columns from the left side of the table.
	 *
	 * @return a reference to this object
	 */
	public CSVTable trimLeft() {
		for (CSVRow row : rows)
			if (Objects.nonNull(row.first()))
				return this;
		removeColumn(0);
		return trimLeft();
	}

	/**
	 * Removes all empty columns from the right side of the table.
	 *
	 * @return a reference to this object
	 */
	public CSVTable trimRight() {
		for (CSVRow row : rows)
			if (Objects.nonNull(row.last()))
				return this;
		removeColumn(columnCount() - 1);
		return trimRight();
	}

	/**
	 * Removes empty columns from both sides of the table.
	 *
	 * @return a reference to this object
	 */
	public CSVTable trim() {
		return trimLeft().trimRight();
	}

	/**
	 * Sorts the rows of the table based on a comparator.
	 *
	 * @param comparator   The comparator used to compare rows.
	 * @param affectHeader Whether or not the header should be affected.
	 * 
	 * @return a reference to this object
	 */
	public CSVTable sort(Comparator<CSVRow> comparator, boolean affectHeader) {
		CSVRow header = header();
		if (!affectHeader)
			rows.remove(0);
		Collections.sort(rows, comparator);
		if (!affectHeader)
			rows.add(0, header);
		return this;
	}

	/**
	 * Returns the number of rows in the table.
	 *
	 * @return The number of rows.
	 */
	public int rowCount() {
		return rows.size();
	}

	/**
	 * Returns the number of columns in the table.
	 *
	 * @return The number of columns.
	 */
	public int columnCount() {
		return columns;
	}

	/**
	 * Sets the delimiter character used for parsing and formatting CSV data.
	 *
	 * @param delimiter The delimiter character.
	 * 
	 * @return a reference to this object
	 */
	public CSVTable delimiter(char delimiter) {
		this.delimiter = delimiter;
		return this;
	}

	/**
	 * Gets the delimiter character used for parsing and formatting CSV data.
	 *
	 * @return the delimiter character
	 */
	public char delimiter() {
		return delimiter;
	}

	/**
	 * Converts the table into a string with aligned columns.
	 *
	 * @return a string representing the table with aligned columns
	 */
	public String toAlignedString() {
		int[] widths = new int[columnCount()];
		for (CSVRow row : rows)
			for (int column = 0; column < columnCount(); column++)
				widths[column] = Math.max(widths[column], row.toString(column, delimiter()).length());
		StringBuilder builder = new StringBuilder();
		for (int row = 0; row < rowCount(); row++) {
			if (row > 0)
				builder.append('\n');
			builder.append(rows.get(row).toAlignedString(widths));
		}
		return builder.toString();
	}

	/**
	 * Converts the table into a CSV string representation.
	 *
	 * @return a string representing the table in CSV format
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int row = 0; row < rowCount(); row++) {
			if (row > 0)
				builder.append('\n');
			builder.append(rows.get(row).toString());
		}
		return builder.toString();
	}

	private void checkRowRange(int row) {
		if (row < 0 || row >= rowCount())
			throw new IndexOutOfBoundsException(String.format("Row %s is out of bounds [0, %s]", row, rowCount() - 1));
	}

	private void checkColumnRange(int column) {
		if (column < 0 || column >= columnCount())
			throw new IndexOutOfBoundsException(String.format("Column %s is out of bounds [0, %s]", column, columnCount() - 1));
	}

}
