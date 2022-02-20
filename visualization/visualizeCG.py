import graphviz
import os
os.environ["PATH"] += os.pathsep + 'D:\Documents\cs598\IntelliJ\call_graph_CS598\visualization'

#pipenv run python visualize.py

file = open('graph.txt')
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

print(vertices)
print(edges)

graph = graphviz.Graph()

for v in vertices:
    graph.node(v)

for edge in edges:
    graph.edge(edge[0], edge[1])

print(graph)

#doctest_mark_exe()
graph.render('doctest-output/round-table.gv').replace('\\', '/')
