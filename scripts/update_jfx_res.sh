#!/bin/bash

cd ../modules/resources || exit

# Download the entire repository
tmpDir=$(mktemp -d)
resDir="src/main/resources/io/github/palexdev/mfxresources/themes/javafx"
svn export --force "https://github.com/openjdk/jfx.git/trunk/modules/javafx.controls/src/main/resources/com/sun/javafx/scene/control/skin" "$tmpDir"

# Move the required files to their respective directories
caspianDir="$tmpDir/caspian"
modenaDir="$tmpDir/modena"

caspianNames=("caspian.css" "caspian-no-transparency.css" "fxvk.css" "two-level-focus.css")
for file in "${caspianNames[@]}"; do
  mv "$caspianDir/$file" "$resDir/caspian/"
done

modenaNames=("modena.css" "modena-no-transparency.css" "touch.css" "two-level-focus.css")
for file in "${modenaNames[@]}"; do
  mv "$modenaDir/$file" "$resDir/modena/"
done

# Clean up the temporary directory
rm -rf "$tmpDir"