# Kata 3

## Objective

The objective of this kata is to read external CSV data, convert valid records into domain objects, and generate a JSON document with a collection and calculated metadata.

This repetition uses books from Internet Archive. Reading, CSV parsing, domain validation, transformation, and JSON writing have separate responsibilities.

## Compilation and execution

Use JDK 26 and run these commands from the project root:

```sh
mvn clean compile
mvn exec:java
```

The application requests CSV from the API and creates or replaces `output.json` in the project root. Running `Main` requires Internet access.

## Request parameters

`Main` creates the reader and selects the requested number of records:

```java
BookReader reader = new ArchiveBookReader(new UrlCsvSource());
BookExport result = reader.read(10);
```

Changing `10` to `20` changes the API request to `rows=20`. Recompile after editing `Main`:

```sh
mvn compile exec:java
```

The count must be positive. It limits the records requested from the server, rather than downloading the entire dataset and applying a local limit. The server may return fewer matches, and validation may discard some returned records, so the valid collection may contain fewer than the requested count.

The reader searches `collection:internetarchivebooks AND creator:* AND date:*`, selects `identifier`, `title`, `creator`, and `date`, and requests `output=csv`. See the [example API request](https://archive.org/advancedsearch.php?q=collection%3Ainternetarchivebooks%20AND%20creator%3A*%20AND%20date%3A*&fl%5B%5D=identifier&fl%5B%5D=title&fl%5B%5D=creator&fl%5B%5D=date&rows=10&page=1&output=csv) and [Internet Archive advanced search](https://archive.org/advancedsearch.php).

A source failure propagates as an error. `Main` opens the output file only after reading and transformation have succeeded.

## Technologies

- Java 26.
- Maven 3.9 or later.
- Gson 2.14.0 for JSON serialization.
- JUnit Jupiter 5.11.4 for tests.

Dependencies and build plugins are declared in `pom.xml`. The first Maven run may need Internet access to download dependencies.

## Project Structure

The base package is `software.ulpgc.katas`.

- `Main.java`: creates and connects the source, reader, and writer.
- `model/Book.java`: immutable domain object with identifier, title, author, and year; its constructor enforces required fields and a positive year.
- `model/Metadata.java`: total, valid, and discarded record counts.
- `model/BookExport.java`: immutable collection and metadata.
- `io/BookReader.java`: reader contract with `read(int count)` and input methods accepting an `InputStream` or `Reader`.
- `io/ArchiveBookReader.java`: builds the parameterized CSV request and closes the input stream it owns.
- `io/CsvSource.java`: contract for obtaining an input stream from a URI; the caller closes the returned stream.
- `io/UrlCsvSource.java`: URL adapter with CSV request headers and connection/read timeouts.
- `parser/CsvParser.java`: separates CSV fields, including quoted commas and escaped quotes.
- `parser/BookParser.java`: converts fields into a valid domain object.
- `service/BookTransformer.java`: checks the header, collects valid objects, and calculates metadata.
- `io/JsonWriter.java`: serializes prepared objects to a stream or file using Gson and UTF-8.

The domain has no dependency on the API or Gson. `Main` injects the `CsvSource` implementation into the API reader, allowing tests to substitute the external source.

## Input and expected output

The repository includes a small [example CSV](src/main/resources/books-example.csv) and its [expected JSON](src/test/resources/expected.json).

The input header must contain exactly `identifier,title,creator,date` in that order, with optional CSV quoting. Each record occupies one line. Commas and doubled quotes inside quoted fields are supported; multiline values are not supported.

All four values are required. The API date must be a valid ISO instant, such as `2000-01-01T00:00:00Z`. The parser extracts its UTC year and the domain requires that year to be positive. The output maps `title` to `title` and `creator` to `author`.

The example contains two valid records and one with an empty creator. Its expected counters are:

```json
{
  "totalRecords": 3,
  "validRecords": 2,
  "discardedRecords": 1
}
```

The header is excluded from the count. Metadata describes the returned records, not the total number of matches in Internet Archive. The live response can differ from this fixed local example.

## Verification

```sh
mvn clean test
```

There are 35 passing test cases covering:

- Comparison of the generated JSON with the fixed expected document by content.
- Conversion of the API date to the domain year.
- Incomplete rows, empty fields, invalid dates, and malformed CSV.
- Empty input, header-only input, and unexpected headers.
- Quoted commas, escaped quotes, and JSON round-trip with special characters.
- Equivalent results from files, memory streams, and readers.
- JSON file writing, domain invariants, and defensive collection copies.
- Requests with `rows=1`, `rows=3`, `rows=10`, and `rows=20`, verified by an injected source.
- Rejection of non-positive counts before contacting the source.
- Propagation of external failures and closure of owned streams after success or invalid input.

The tests use local resources and source doubles, with no live API requests. They do not require or modify the root `output.json`. Reports are generated in `target/surefire-reports/`.

## Repetitions

The three implementations use videogames, books, and movies while preserving the same CSV reading, parsing, validation, transformation, and JSON serialization flow.

- `videogames-version`: videogames from the MS-DOS games collection.
- `book-version`: books from the Internet Archive books collection.
- `movie-version`: movies from the feature films collection, with identifier, title, creator, and year.

The small variations are the collection requested from the API, the domain fields, and the example data. Selecting and integrating the final evaluable version into `master` are pending.

## Video

[![Kata 3 - Video Explanation](https://img.youtube.com/vi/6FDf0B_LG3M/maxresdefault.jpg)](https://youtu.be/6FDf0B_LG3M)

The video demonstrates the movie implementation and briefly presents the videogame and book repetitions.
