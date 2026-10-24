# Kata 3

## Objective

The objective of this kata is to transform external data into domain objects and serialize them as valid JSON, separating reading, parsing, validation, transformation, and writing.

The application reads a small videogame CSV dataset, discards invalid records, and generates a JSON document containing the valid videogames and metadata with the total, valid, and discarded record counts.

## Compilation and execution

Run the following commands from the project root using JDK 26:

```sh
mvn clean compile
mvn exec:java
```

The application reads `src/main/resources/videogames-example.csv` and creates or replaces `output.json` in the project root. The console shows the output location and the record counts.

## Technologies

- Java 26.
- Maven 3.9 or later.
- Gson 2.14.0 for JSON serialization.
- JUnit Jupiter 5.11.4 for automated tests.

Dependencies and Maven plugins are declared in `pom.xml`. Maven needs Internet access on the first run to download any dependencies that are not already available locally.

## Project Structure

The project uses the following base package: `software.ulpgc.katas`.

- `Main.java`: connects the reader and JSON writer and manages the input and output streams.
- `model/Videogame.java`: domain object representing a valid videogame.
- `model/Metadata.java`: stores the total, valid, and discarded record counts.
- `model/VideogameExport.java`: groups the videogame collection and its metadata.
- `io/VideogameReader.java`: accepts an `InputStream`, a `Reader`, or text lines and delegates their transformation.
- `io/UrlVideogameReader.java`: provides an alternative input source through a URL.
- `parser/VideogameParser.java`: validates a CSV record and converts it into a domain object.
- `service/VideogameTransformer.java`: skips the header, collects valid objects, and calculates the metadata.
- `io/JsonWriter.java`: serializes the prepared result to an `OutputStream` using Gson and UTF-8.
- `VideogamePipelineTest.java`: verifies the conversion and JSON output in `src/test/java`.

## Input and expected output

The repository includes:

- [Example CSV](src/main/resources/videogames-example.csv).
- [Expected JSON](src/test/resources/expected.json).

The CSV uses a header followed by records with 11 comma-separated fields. It supports simple fields without embedded commas or multiline quoted values. Name, platform, release year, and genre are required. The release year must be a positive integer.

The example contains Minecraft, Portal 2, and an invalid record with an empty genre. The output includes the two valid videogames and the following metadata:

```json
{
  "totalRecords": 3,
  "validRecords": 2,
  "discardedRecords": 1
}
```

The header is excluded from the record count. The complete expected document is stored in `src/test/resources/expected.json`.

## Verification

Run the automated tests from the project root:

```sh
mvn clean test
```

The test suite contains 15 test cases covering:

- Conversion of a valid record into a domain object.
- Comparison with the expected JSON by content, independently of whitespace and property order.
- Discarding incomplete or malformed records, empty required fields, and invalid years while continuing to process subsequent rows.
- Empty input and input containing only a header, producing an empty collection and zero counters.
- Parsing the generated JSON again, preserving quotes, line breaks, backslashes, and Unicode characters.
- Equivalent results when reading from a file, an in-memory stream, or a `Reader`.

The verified result is 15 tests passed, with no failures or errors. Tests use local data and do not require the generated `output.json`. Execution reports are written to `target/surefire-reports/`.

## Repetitions

The videogame implementation is the second repetition and is developed in the `videogames-version` branch. The two additional comparable repetitions are pending.

Each repetition must preserve the same reading, parsing, transformation, and JSON serialization structure, with only small variations. The final evaluable version is still pending integration into `master`.

## Video

Pending: add the link to the explanatory video, with a maximum duration of 7 minutes.
