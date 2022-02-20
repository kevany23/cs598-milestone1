import subprocess
import argparse

parser = argparse.ArgumentParser()
parser.add_argument('--representation')
parser.add_argument('--input')

args = parser.parse_args()
rep = args.representation
path = args.input
file = rep + ".jar"
file_out = rep + ".png"

subprocess.call(['java', '-jar', file, path])
subprocess.call(['dot', '-Tpng', 'AST.dot', '-o', file_out])