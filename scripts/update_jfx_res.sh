#!/bin/bash

# TODO when updating resources don't forget to manually update fxvk.css as the font properties fail to parse :facepalm:

# shellcheck disable=SC2164
THIS="$(
  cd -- "$(dirname "$0")" >/dev/null 2>&1
  pwd -P
)"
# shellcheck disable=SC2164
ROOT="$(dirname "$THIS")"
RES_DIR="$ROOT/modules/resources/src/main/resources/io/github/palexdev/mfxresources/themes/javafx"
CASPIAN_DIR="$RES_DIR/caspian"
MODENA_DIR="$RES_DIR/modena"

# This script requires nmp and csso-cli and cssbeautify-cli to optimize the CSS files
if ! type npm >/dev/null; then
  echo "npm could not be found"
  exit
fi

if ! type csso >/dev/null; then
  echo "csso could not be found"
  exit
fi

if ! type cssbeautify-cli >/dev/null; then
  echo "cssbeautify could not be found"
  exit
fi

# Download the entire repository
tmpDir=$(mktemp -d)
svn export --force "https://github.com/openjdk/jfx.git/trunk/modules/javafx.controls/src/main/resources/com/sun/javafx/scene/control/skin" "$tmpDir"

# Move the required files to their respective directories
CASPIAN_TMP="$tmpDir/caspian"
MODENA_TMP="$tmpDir/modena"

for file in "$CASPIAN_TMP"/*; do
  if [[ $file == *.css ]]; then
    name=$(basename "$file")
    tempFile="$CASPIAN_DIR/$name.tmp"
    mv -f "$file" "$tempFile"
    csso "$tempFile" -o "$tempFile" --no-restructure
    cssbeautify-cli -a -i2 -f "$tempFile" -w "$CASPIAN_DIR/$name"
    rm "$tempFile"
  else
    mkdir -p "$CASPIAN_DIR/assets/"
    mv -f "$file" "$CASPIAN_DIR/assets/"
  fi
done

(cd "$CASPIAN_DIR/assets" && zip -r "$CASPIAN_DIR/assets.zip" .)
rm -rf "$CASPIAN_DIR/assets"

for file in "$MODENA_TMP"/*; do
  if [[ $file == *.css ]]; then
    name=$(basename "$file")
    tempFile="$MODENA_DIR/$name.tmp"
    mv -f "$file" "$tempFile"
    csso "$tempFile" -o "$tempFile" --no-restructure
    cssbeautify-cli -a -i2 -f "$tempFile" -w "$MODENA_DIR/$name"
    rm "$tempFile"
  else
    mkdir -p "$MODENA_DIR/assets/"
    mv -f "$file" "$MODENA_DIR/assets/"
  fi
done

(cd "$MODENA_DIR/assets" && zip -r "$MODENA_DIR/assets.zip" .)
rm -rf "$MODENA_DIR/assets"

rm -rf "$tmpDir"