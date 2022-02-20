import graphviz
import os
os.environ["PATH"] += os.pathsep + 'D:\Documents\cs598\IntelliJ\call_graph_CS598\visualization'

#pipenv run python visualize.py

file = open('graph2.txt')
text = file.read()
lines = text.split('\n')

vertices = set()
edges = set()
cgVertices = set()
cgEdges = {}
methodCfg = {}
count = 0
cfgId = {}

for line in lines:
    if line == '':
        break
    array = line.split(' , ')
    source = array[0]
    dest = array[1]
    cgVertices.add(source)
    cgVertices.add(dest)
    vertices.add(source)
    vertices.add(dest)
    cgEdges.setdefault(source, set())
    methodCfg.setdefault(source, set())
    cgEdges[source].add(dest)

file = open('graph3.txt')
text = file.read()
lines = text.split('\n')

for line in lines:
    if line == '':
        break
    array = line.split(' , ')
    if len(array) == 2:
        methodName = array[0]
        dest = array[1]
        dest = methodName + ' | ' + dest
        cfgId.setdefault(dest, str(count))
        count += 1
        dest = cfgId[dest]
        vertices.add(methodName)
        vertices.add(dest)
        edges.add((methodName, dest))
    if len(array) == 3:
        methodName = array[0]
        source = array[1]
        source = methodName + ' | ' + source
        cfgId.setdefault(source, str(count))
        count += 1
        source = cfgId[source]
        dest = array[2]
        dest = methodName + ' | ' + dest
        cfgId.setdefault(dest, str(count))
        count += 1
        dest = cfgId[dest]
        vertices.add(methodName)
        vertices.add(source)
        vertices.add(dest)
        if methodName in methodCfg:
            methodCfg[methodName].add((source, dest))
        else:
            methodCfg[methodName] = set([(source, dest)])
        for nextMethodName in cgVertices:
            if nextMethodName in dest:
                vertices.add(nextMethodName)
                edges.add(source, nextMethodName)
                continue
        edges.add((source, dest))

graph = graphviz.Graph()


for v in vertices:
    graph.node(v)


for (u, v) in edges:
    graph.edge(u, v)

graph.render('cfg-output/round-table.gv').replace('\\', '/')
