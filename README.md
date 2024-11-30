# CSV Table Library

A lightweight Java library for parsing, manipulating, and managing **CSV** data with advanced table operations.

## Features

- Easy **CSV** parsing and creation
- Flexible table manipulation methods
- Support for custom delimiters
- Advanced row and column operations
- Convenient data alignment and formatting

## Usage

### Creating a CSV Table

- Creating empty table

```java
CSVTable table = new CSVTable();
```

- Parsing table from source

```java
String source = "Name,Age,City\nJohn,30,New York\nAlice,25,San Francisco";
CSVTable table = new CSVTable(source);
```

### Using Operations

 - Adding and removing rows

```java
CSVRow newRow = table.addRow();
newRow.set(0, "Bob").set(1, 35).set(2, "Chicago");

table.removeRow(1);
```

 - Column manipulation

```java
table.insertColumn(1);

table.removeColumn(2);

table.swapColumns(0, 1);
```

 - Sorting

```java
table.sort((row1, row2) -> {
    String value1 = (String) row1.get(0);
    String value2 = (String) row2.get(0);
    return value1.compareTo(value2);
}, false);
```

 - Trimming empty columns

```java
table.trim();
```

 - Formatting

```java
String alignedTable = table.toAlignedString();
String csvString = table.toString();
```

 - Custom delimiter support

```java
CSVTable table = new CSVTable(csvData, ';');
```

## Error Handling

The library provides clear error messages with:
- `IndexOutOfBoundsException` - for invalid row or column indices
- `IllegalArgumentException` - for invalid configuration