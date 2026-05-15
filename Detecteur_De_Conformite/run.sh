#!/bin/bash

cd "$(dirname "$0")"

javac -cp ".:lib/*" **/*.java 

java -cp ".:lib/*" ui.MenuKYC 
