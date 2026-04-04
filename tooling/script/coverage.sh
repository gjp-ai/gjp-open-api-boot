#!/bin/bash

# Ensure script is run from project root
cd "$(dirname "$0")/../.."

echo "🚀 Starting Unit Test Coverage Generation..."

# Run tests with JaCoCo and 'test' profile
./mvnw clean test -Dspring.profiles.active=test

if [ $? -eq 0 ]; then
    echo "✅ Tests passed! Generating JaCoCo report..."
    
    REPORT_PATH="target/site/jacoco/index.html"
    
    if [ -f "$REPORT_PATH" ]; then
        echo "📂 Opening coverage report: $REPORT_PATH"
        # Open in browser (Mac)
        open "$REPORT_PATH"
    else
        echo "❌ Report not found at $REPORT_PATH. Check if jacoco-maven-plugin is configured."
    fi
else
    echo "❌ Tests failed. Coverage report was not updated."
    exit 1
fi
