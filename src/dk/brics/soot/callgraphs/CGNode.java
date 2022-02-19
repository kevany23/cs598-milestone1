package dk.brics.soot.callgraphs;

import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Targets;
import soot.options.Options;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CGNode {
    public int level;
    public SootMethod sootMethod;
    public CGNode(SootMethod sootMethod) {
        level = 0;
        this.sootMethod = sootMethod;
    }
    public CGNode(CGNode parent, SootMethod sootMethod) {
        level = parent.level + 1;
        this.sootMethod = sootMethod;
    }
}
