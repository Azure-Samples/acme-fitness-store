#!/bin/sh
INITIAL_WORKING_DIRECTORY=$(pwd)
cd "$(dirname "$0")"
cp ./data/migrations/* ../acme-catalog/src/main/resources/db/migration
cp ./data/images/* ../acme-catalog/src/main/resources/static/
cd $INITIAL_WORKING_DIRECTORY