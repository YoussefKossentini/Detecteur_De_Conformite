#!/bin/bash

cd "$(dirname "$0")"

javac -cp ".:lib/*" **/*.java || { echo " Échec de la compilation. Vérifiez que icu4j.jar est dans le dossier lib/."; exit 1; }

java -cp ".:lib/*" ui.MenuKYC 