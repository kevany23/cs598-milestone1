The code is to be used as follows:

Download the script and jar file. In a command line, the script can be run with the following command:

python static_analysis.py --representation [AST/CG/CFG] --input [file path]

to generate the respective representation for the file given. Note that your file path should be relative to where the script is located - not the absolute path. Also note that path name should use '/' instead of '\'. 

Here's an example of the script and it's output:

python3 static_analysis.py --representation AST --input "commons-csv/src/main/java/org/apache/commons/csv/Token.java"
![AST](https://user-images.githubusercontent.com/50717720/154831968-ddc88e27-21c8-496d-a843-8f653c48dedb.png)

Note that the script produces both the .dot file and a .png file for easier viewing.
