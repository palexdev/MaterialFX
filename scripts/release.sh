#!/bin/bash

#set -x
args=("$@")

function printHelp() {
  echo 'Shell Script utility to publish the modules of this project to Maven using the Gradle Wrapper'
  echo 'You can decide what to publish with the following arguments:'
  echo '  1) all : tells the script to publish all the modules'
  echo '  2) A list of modules (ex. components, core,...)'
  echo ''
  echo 'Options:'
  echo '-r to also automatically release the artifacts once they are published'
  echo '-c to clean the project before everything else'
  echo ''
  echo ''
  echo 'Before running this script makes sure that your JAVA_HOME environment variable is set and correct'
}

function checkArgs() {
  for var in "${args[@]}"; do
    if [ "$var" == "$1" ]; then
      ret="true"
      return
    fi
  done
  ret="false"
}

function checkRelease() {
  local ret=""
  checkArgs '-r'

  if [ "$ret" == "true" ]; then
    rel_ret='closeAndReleaseRepository'
  else
    rel_ret=''
  fi
}

function publishModules() {
  local rel_ret=""
  local tmp=""

  checkRelease
  if [ "$rel_ret" != "" ]; then
    echo 'Publishing and RELEASING specified modules!'
  else
    echo 'Publishing specified modules!'
  fi

  for var in "${args[@]}"; do
    tmp=$var # I don't fucking know why, it just works this way
    if [ "$var" != "all" ] && [ "$var" != "-h" ] && [ "$var" != "-r" ] && [ "$var" != "-c" ]; then
      ./gradlew "$tmp:publish"
      if [ "$rel_ret" != "" ]; then
        ./gradlew "$tmp:closeAndReleaseRepository"
      fi
    fi
  done
}

function main() {
  local ret=""

  if [ $# == 0 ]; then
    echo "No args provided"
    exit 1
  else
    cd ..
  fi

  checkArgs '-h'
  if [ "$ret" == "true" ]; then
    printHelp
    exit 1
  fi

  checkArgs '-c'
  if [ "$ret" == "true" ]; then
    ./gradlew clean
  fi

  checkArgs 'all'
  if [ "$ret" == "true" ]; then
    checkRelease
    ./gradlew publish "$rel_ret"
    exit 0
  fi

  publishModules
}

main "$@"
