# Social-Media-Project

To run from a Windows command prompt:
Download all files, open a command prompt, navigate to the main directory containing all subfolders, and run the following:

javac -cp .:lib/slf4j-simple-1.7.36.jar:lib/slf4j-api-1.7.36.jar:lib/sqlite-jdbc-3.45.2.01.jar gui/Main.java && java -cp .:lib/slf4j-simple-1.7.36.jar:lib/slf4j-api-1.7.36.jar:lib/sqlite-jdbc-3.45.2.01.jar gui.Main

This compiles and runs the main file, Main.java, with the SQLite library files as dependencies. 

To run from an IDE, all files in the lib folder must be included as dependencies. Then, run Main.java in the gui folder.
