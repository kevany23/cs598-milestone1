import graphviz
import os
import sys

# python visualize.py [optional filename]
# or
# pipenv run python visualize.py [optional filename]
# By default, the program will read graph.txt

inputFile = sys.argv[1] if len(sys.argv) >= 2 else 'graph.txt'

file = open(inputFile)
text = file.read()
lines = text.split('\n')

vertices = set()
edges = set()


for line in lines:
    if line == '':
        break
    array = line.split(' , ')
    source = array[0]
    dest = array[1]
    vertices.add(source)
    vertices.add(dest)
    edges.add((source, dest))

graph = graphviz.Graph()

for v in vertices:
    graph.node(v)

for edge in edges:
    graph.edge(edge[0], edge[1])

#doctest_mark_exe()
graph.render('cg-representation.gv').replace('\\', '/')
