#!/bin/bash
set -e

echo "1. Updating build.gradle.kts..."
sed -i 's/namespace = "com.example"/namespace = "com.sprinthon.focusclock"/g' app/build.gradle.kts
sed -i 's/applicationId = "com.aistudio.focusclock.xvyqpt"/applicationId = "com.sprinthon.focusclock"/g' app/build.gradle.kts

echo "2. Restructuring directories..."
for source_set in main test androidTest; do
    if [ -d "app/src/$source_set/java/com/example" ]; then
        mkdir -p "app/src/$source_set/java/com/sprinthon/focusclock"
        # Move all contents, including hidden files if any exist
        mv app/src/$source_set/java/com/example/* "app/src/$source_set/java/com/sprinthon/focusclock/"
        # Clean up old directory (safely remove example if empty, then com if empty)
        rmdir "app/src/$source_set/java/com/example" || true
        # We leave 'com' alone since the new path also uses it.
    fi
done

echo "3. Updating packages and imports in source files..."
# Find all Kotlin and XML files and replace com.example with com.sprinthon.focusclock
find . -type f \( -name "*.kt" -o -name "*.xml" -o -name "*.pro" \) -exec sed -i 's/com\.example/com\.sprinthon\.focusclock/g' {} +

echo "Migration script completed successfully."
