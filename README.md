# Reverser of String Encoders

## Overview
This repository contains tools and libraries for symbolic automata and reversing string encoders. It consists of multiple Maven modules (`symbolicautomata`, `GENIClib`, and `TestGENIC`).

## Requirements
* Java Development Kit (JDK) 1.8
* Maven 3.x

## Build Instructions

To build the entire project cleanly, simply use the acceptance script or standard Maven commands from the root of the repository:

```bash
./acceptance_test.sh
```

Or manually run:

```bash
# Clean and compile the project and its modules
mvn clean install -DskipTests
```

## Running Tests

To run the tests for the project, run the following from the root directory:

```bash
mvn clean test
```

*Note:* Z3 Native bindings are already shipped under `GENIClib/lib/`. The Maven configuration automatically configures the library path (`LD_LIBRARY_PATH` or equivalent java arguments) during the test phase. You no longer need manual configuration via Eclipse to set up or run tests.
