#!/bin/sh
cd ~/Git/pp-final-project
javadoc -sourcepath src src/antlr/ParseTreeView.java symbol -d docs -classpath lib/antlr4-4.7.1.jar -classpath lib/antlr-4.8-complete.jar