# Kata 3

## Objective

The objective of this kata is to transform external data into domain objects and serialize them as valid JSON, separating reading, parsing, validation, transformation, and writing.

The application retrieves a videogame CSV dataset from a public API, discards invalid records, and generates its own JSON document containing the valid videogames and metadata with the total, valid, and discarded record counts.

## Compilation and execution

Run the following commands from the project root using JDK 26:

```sh
mvn clean compile
mvn exec:java
```

The application requests `data/vgsales.csv` from the [GitHub Contents API](https://api.github.com/repos/algorithms-and-data-representation/Real-World-Data-Visualization/contents/data/vgsales.csv) and creates or replaces `output.json` in the project root. The dataset comes from [Real-World-Data-Visualization](https://github.com/algorithms-and-data-representation/Real-World-Data-Visualization).

The request uses `Accept: application/vnd.github.raw+json` to receive the raw CSV, as specified in the [GitHub API documentation](https://docs.github.com/en/rest/repos/contents#get-repository-content). Although the media type name includes `+json`, this option returns the file contents, not a JSON description of the file. The conversion performed by the application is CSV to Java objects to JSON.

Running `Main` requires Internet access. The console shows the output location and the actual record counts. Connection or API failures propagate as errors instead of being reported as an empty dataset; the output file is opened after the source has been read successfully.

## Technologies

- Java 26.
- Maven 3.9 or later.
- Gson 2.14.0 for JSON serialization.
- JUnit Jupiter 5.11.4 for automated tests.

Dependencies and Maven plugins are declared in `pom.xml`. Maven needs Internet access on the first run to download any dependencies that are not already available locally.

## Project Structure

The project uses the following base package: `software.ulpgc.katas`.

- `Main.java`: configures the API source and connects the reader to the JSON writer.
- `model/Videogame.java`: domain object representing a valid videogame.
- `model/Metadata.java`: stores the total, valid, and discarded record counts.
- `model/VideogameExport.java`: groups the videogame collection and its metadata.
- `io/VideogameReader.java`: accepts an `InputStream`, a `Reader`, or text lines and delegates their transformation.
- `io/UrlVideogameReader.java`: reads CSV through a URL, applies the configured request headers and timeouts, and closes its input stream.
- `parser/VideogameParser.java`: validates a CSV record and converts it into a domain object.
- `service/VideogameTransformer.java`: skips the header, collects valid objects, and calculates the metadata.
- `io/JsonWriter.java`: serializes the prepared result to an `OutputStream` or a file using Gson and UTF-8, closing the streams it opens itself.
- `VideogamePipelineTest.java`: verifies the conversion and JSON output in `src/test/java`.
- `UrlVideogameReaderTest.java`: verifies request configuration and source failures using test doubles without contacting the API.

## Input and expected output

The repository includes:

- [Example CSV](src/main/resources/videogames-example.csv).
- [Expected JSON](src/test/resources/expected.json).

The CSV uses a header followed by records with 11 comma-separated fields. Quoted fields may contain commas and escaped double quotes. Multiline values are not supported. Name, platform, release year, and genre are required. The release year must be a positive integer.

The local example is used by the reproducible tests. It contains Minecraft, Portal 2, and an invalid record with an empty genre. Its expected output includes the two valid videogames and the following metadata:

```json
{
  "totalRecords": 3,
  "validRecords": 2,
  "discardedRecords": 1
}
```

The header is excluded from the record count. The complete expected document for this local example is stored in `src/test/resources/expected.json`. The live API dataset contains different records, so its output and counters are not compared with this three-record example.

## Verification

Run the automated tests from the project root:

```sh
mvn clean test
```

The test suite contains 23 test cases covering:

- Conversion of a valid record into a domain object.
- Comparison with the expected JSON by content, independently of whitespace and property order.
- Discarding incomplete or malformed records, empty required fields, and invalid years while continuing to process subsequent rows.
- Preserving commas and escaped quotes in quoted titles and rejecting malformed quotes.
- Empty input and input containing only a header, producing an empty collection and zero counters.
- Parsing the generated JSON again, preserving quotes, line breaks, backslashes, and Unicode characters.
- Equivalent results when reading from a file, an in-memory stream, or a `Reader`.
- Writing the expected JSON to a file.
- Configuring API headers and timeouts, retaining support for ordinary URLs, and propagating source failures without contacting an external service.

The verified result is 23 tests passed, with no failures or errors. Tests use local data and test doubles, do not contact the API, and do not require the generated `output.json`. Execution reports are written to `target/surefire-reports/`.

## Repetitions

The videogame implementation is the second repetition and is developed in the `videogames-version` branch. The two additional comparable repetitions are pending.

Each repetition must preserve the same reading, parsing, transformation, and JSON serialization structure, with only small variations. The final evaluable version is still pending integration into `master`.

## Video

Pending: add the link to the explanatory video, with a maximum duration of 7 minutes.
