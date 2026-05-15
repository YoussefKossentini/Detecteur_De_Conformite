#Fichier Shell pour MACOS

#!/bin/bash
cd "$(dirname "$0")"
javac -cp ".:lib/*" **/*.java 
java -cp ".:lib/*" ui.MenuKYC 


#Pour WINDOWS voici le code shell
#cd $PSScriptRoot
#javac -cp ".;lib/*" (Get-ChildItem -Recurse -Filter *.java | % { $_.FullName })
#java -cp ".;lib/*" ui.MenuKYC


