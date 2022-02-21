import subprocess
import argparse
import graphviz
import sys

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
    inputFile ='graph.txt'
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

    graph.render('cg-representation.gv').replace('\\', '/')
if rep == "CFG" or rep == "cfg":
    inputFile1 = 'graph2.txt'
    inputFile2 = 'graph3.txt'

    file = open(inputFile1)
    text = file.read()
    lines = text.split('\n')

    vertices = set()
    edges = set()
    methodNames = set()
    count = 0
    cfgId = {}

    for line in lines:
        if line == '':
            break
        array = line.split(' , ')
        source = array[0]
        dest = array[1]
        vertices.add(source)
        vertices.add(dest)

    file = open(inputFile2)
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
            methodNames.add(methodName)
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
            destText = dest
            dest = methodName + ' | ' + dest
            cfgId.setdefault(dest, str(count))
            count += 1
            dest = cfgId[dest]
            vertices.add(methodName)
            vertices.add(source)
            vertices.add(dest)
            for nextMethodName in methodNames:
                if nextMethodName in destText:
                    vertices.add(nextMethodName)
                    edges.add((source, nextMethodName))
                    continue
            edges.add((source, dest))

    graph = graphviz.Graph()


    for v in vertices:
        graph.node(v)


    for (u, v) in edges:
        graph.edge(u, v)

    # Write cfg statement -> number mapping to file
    file = open('cfg-mapping.txt', 'w')
    file.write('CFG statement, key number\n')
    for key in cfgId:
        file.write(cfgId[key] + ', ' + key + '\n')
    file.close()

    graph.render('cfg-output.gv').replace('\\', '/')