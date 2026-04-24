#!/bin/bash
set -e
export LD_LIBRARY_PATH="$(pwd)/GENIClib/lib:$LD_LIBRARY_PATH"
echo "Starting acceptance test..."
echo "Building full project..."
mvn clean install -DskipTests
echo "Running all tests..."
mvn clean test
echo "Acceptance test passed successfully!"
