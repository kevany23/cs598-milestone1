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
if rep == "AST" or rep == "ast":
    subprocess.call(['dot', '-Tpng', 'AST.dot', '-o', file_out])
if rep == "CG" or rep == "cg":
    # call CG output
    assert NotImplementedError
if rep == "CFG" or rep == "cfg":
    # call CFG output
    assert NotImplementedError