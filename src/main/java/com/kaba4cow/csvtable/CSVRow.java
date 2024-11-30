package com.kaba4cow.csvtable;

import java.util.List;
import java.util.Objects;

/**
 * Represents a single row of CSV data within a {@link CSVTable}. Provides methods to manipulate columns in the row.
 */
public class CSVRow {

	private final CSVTable table;
	private Object[] columns;

	CSVRow(CSVTable table) {
		this.table = table;
		this.columns = new Object[table.columnCount()];
	}

	CSVRow(CSVTable table, List<Object> columns) {
		this.table = table;
		this.columns = new Object[columns.size()];
		columns.toArray(this.columns);
	}

	/**
	 * Gets the value in the specified column of the row.
	 *
	 * @param columnIndex the index of the column to retrieve
	 * 
	 * @return the value in the specified column
	 * 
	 * @throws IndexOutOfBoundsException if the column index is out of range
	 */
	public Object get(int columnIndex) {
		checkRange(columnIndex);
		return columns[columnIndex];
	}

	/**
	 * Sets the value for the specified column in the row.
	 *
	 * @param columnIndex the index of the column to set
	 * @param columnData  the value to set for the specified column
	 * 
	 * @return a reference to this object
	 * 
	 * @throws IndexOutOfBoundsException if the column index is out of range
	 */
	public CSVRow set(int columnIndex, Object columnData) {
		checkRange(columnIndex);
		columns[columnIndex] = columnData;
		return this;
	}

	/**
	 * Adds a new column with the specified data to the row.
	 *
	 * @param columnData the data to add to the new column
	 * 
	 * @return a reference to this object
	 */
	public CSVRow add(Object columnData) {
		Object[] newColumns = new Object[columns.length + 1];
		System.arraycopy(columns, 0, newColumns, 0, columns.length);
		newColumns[columns.length] = columnData;
		columns = newColumns;
		table.resizeTable(columns.length);
		return this;
	}

	CSVRow remove(int columnIndex) {
		checkRange(columnIndex);
		System.arraycopy(columns, columnIndex + 1, columns, columnIndex, columns.length - columnIndex - 1);
		columns[columns.length - 1] = null;
		return this;
	}

	CSVRow insert(int columnIndex) {
		Object[] newColumns = new Object[columns.length + 1];
		System.arraycopy(columns, 0, newColumns, 0, columnIndex);
		System.arraycopy(columns, columnIndex, newColumns, columnIndex + 1, columns.length - columnIndex);
		columns = newColumns;
		return this;
	}

	CSVRow resize(int columnCount) {
		Object[] newColumns = new Object[columnCount];
		System.arraycopy(columns, 0, newColumns, 0, Math.min(columns.length, columnCount));
		columns = newColumns;
		return this;
	}

	/**
	 * Swaps the positions of two columns in the row.
	 *
	 * @param columnIndex1 the index of the first column to swap
	 * @param columnIndex2 the index of the second column to swap
	 * 
	 * @return a reference to this object
	 */
	public CSVRow swap(int columnIndex1, int columnIndex2) {
		if (columnIndex1 == columnIndex2)
			return this;
		checkRange(columnIndex1);
		checkRange(columnIndex2);
		Object columnData1 = columns[columnIndex1];
		Object columnData2 = columns[columnIndex2];
		columns[columnIndex1] = columnData2;
		columns[columnIndex2] = columnData1;
		return this;
	}

	/**
	 * Clears the value in the specified column.
	 *
	 * @param columnIndex the index of the column to clear
	 * 
	 * @return a reference to this object
	 * 
	 * @throws IndexOutOfBoundsException if the column index is out of range
	 */
	public CSVRow clear(int columnIndex) {
		checkRange(columnIndex);
		columns[columnIndex] = null;
		return this;
	}

	/**
	 * Clears all the values in the row.
	 *
	 * @return a reference to this object
	 */
	public CSVRow clear() {
		for (int column = 0; column < columns(); column++)
			columns[column] = null;
		return this;
	}

	/**
	 * Gets the value of the first column in the row.
	 *
	 * @return the value in the first column
	 */
	public Object first() {
		return get(0);
	}

	/**
	 * Gets the value of the last column in the row.
	 *
	 * @return the value in the last column
	 */
	public Object last() {
		return get(columns() - 1);
	}

	/**
	 * Returns the table that this row belongs to.
	 *
	 * @return the {@link CSVTable} that this row belongs to
	 */
	public CSVTable table() {
		return table;
	}

	/**
	 * Returns the number of columns in the row.
	 *
	 * @return the number of columns in the row
	 */
	public int columns() {
		return columns.length;
	}

	String toString(int columnIndex, char delimiter) {
		checkRange(columnIndex);
		Object data = get(columnIndex);
		if (Objects.isNull(data))
			return "";
		String string = data.toString();
		if (string.contains(Character.toString(delimiter)) || string.contains("\n") || string.contains("\r")
				|| string.contains("\""))
			return String.format("\"%s\"", string.replace("\"", "\"\""));
		else
			return string;
	}

	String toAlignedString(int[] widths) {
		StringBuilder builder = new StringBuilder();
		for (int column = 0; column < columns(); column++) {
			if (column > 0)
				builder.append(table.delimiter());
			String string = toString(column, table.delimiter());
			builder.append(string);
			for (int i = string.length(); i < widths[column]; i++)
				builder.append(' ');
		}
		return builder.toString();
	}

	/**
	 * Converts the row into a CSV string representation.
	 *
	 * @return A string representing the row in CSV format
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int column = 0; column < columns(); column++) {
			if (column > 0)
				builder.append(table.delimiter());
			builder.append(toString(column, table.delimiter()));
		}
		return builder.toString();
	}

	private void checkRange(int column) {
		if (column < 0 || column >= columns())
			throw new IndexOutOfBoundsException(String.format("Column %s is out of bounds [0, %s]", column, columns() - 1));
	}

}
