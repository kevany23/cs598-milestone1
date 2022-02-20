import subprocess
import argparse

path = "commons-csv/src/main/java/org/apache/commons/csv/Token.java"
parser = argparse.ArgumentParser()
parser.add_argument('--representation')
parser.add_argument('--input')

args = parser.parse_args()
rep = args.representation
path = args.input
file = rep + ".jar"
file_out = rep + ".png"

subprocess.run(['java', '-jar', file], input=path, encoding='ascii')
subprocess.call(['dot', '-Tpng', 'AST.dot', '-o', file_out])