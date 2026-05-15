#Fichier Shell pour MACOS

#!/bin/bash
cd "$(dirname "$0")"
javac -cp ".:lib/*" **/*.java 
java -cp ".:lib/*" ui.MenuKYC 


#Pour WINDOWS voici le code shell (juste supprimez les # des lignes de codes et les mets pour la version MACOS)
#cd $PSScriptRoot
#javac -cp ".;lib/*" (Get-ChildItem -Recurse -Filter *.java | % { $_.FullName })
#java -cp ".;lib/*" ui.MenuKYC


