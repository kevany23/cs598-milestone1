package dk.brics.soot.callgraphs;

import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Targets;
import soot.options.Options;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Hashtable;
import java.io.File;
import java.io.PrintStream;

public class CFGBuilder
{
	public static void main(String[] args) {
		// Soot classpath
		// where class files are
		String path = System.getProperty("user.dir")+ "/commons-csv-master/target/classes";

		// Setting the classpath programatically
		Options.v().set_prepend_classpath(true);
		Options.v().set_soot_classpath(path);

		// The second argument is the path to the main class of a project you want to analyze
		// (in this case, testers/ExampleCode.java)
		args = new String[]{"-w", "-process-dir", path, "-src-prec", "only-class"};
		PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", new SceneTransformer() {

			@Override
			protected void internalTransform(String phaseName, Map options) {
				CHATransformer.v().transform();

				// This line generates the call graph of the project you're going to analyze
				System.out.println("Getting call graph");
				File outputFile = new File("graph2.txt");
				File outputFile2 = new File("graph3.txt");
				PrintStream stream = null;
				PrintStream stream2 = null;
				try {
					stream = new PrintStream(outputFile);
					stream2 = new PrintStream(outputFile2);
				} catch (Exception e) {

				}
				CallGraph cg = Scene.v().getCallGraph();
				// Now that you have the call graph, you can start doing whatever type of
				// analysis needed for your problem. Below, we are going to traverse the
				// generated call graph and print caller/callee relationships (BFS)

				System.out.println("**********************************");
				Iterator<MethodOrMethodContext> iter = cg.sourceMethods();
				/*while (iter.hasNext()) {
					System.out.println(iter.next());
				}*/
				//SootMethod src = Scene.v().getMainClass().getMethodByName("main");
				//List<SootMethod> nodes = new ArrayList<>();
				List<CGNode> nodes = new ArrayList<>();
				while (iter.hasNext()) {
					System.out.println(new CGNode(iter.next().method()));
					nodes.add(new CGNode(iter.next().method()));
				}
				Hashtable<String, Integer> usedMethods = new Hashtable();
				//nodes.add(new CGNode(src));
				//usedMethods.put(src.getSignature(), 1);
				//System.out.println(nodes.size());
				int count = 0;
				while(!nodes.isEmpty()){
					CGNode cgnode = nodes.get(0);
					SootMethod parent = cgnode.sootMethod;

					String parentClassname = parent.getDeclaringClass().toString();
					String parentSignature = parent.getSignature();
					if (
							parentSignature.contains("init") ||
									usedMethods.containsKey(parentSignature)
					) {
						//continue;
					}
					usedMethods.put(parentSignature, 1);

					Body parentBody = parent.getActiveBody();
					UnitGraph unitGraph = new BriefUnitGraph(parentBody);
					Iterator<Unit> cfgNodes = unitGraph.iterator();
					while (cfgNodes.hasNext()) {
						Unit cfgNode = cfgNodes.next();
						ArrayList<String> succList = new ArrayList<String>();
						List<Unit> succs = unitGraph.getSuccsOf(cfgNode);
						Iterator<Unit> succIter = succs.iterator();
						while (succIter.hasNext()) {
							succList.add(succIter.next().toString());
						}
					}

					Iterator<MethodOrMethodContext> targets = new Targets(cg.edgesOutOf(parent));
					while (targets.hasNext()) {
						SootMethod child = (SootMethod) targets.next();
						String signature = child.getSignature();
						String childClassname = child.getDeclaringClass().toString();

						if (
								! isMethodFiltered(signature, childClassname)
								&& !usedMethods.containsKey(signature)
						) {
							nodes.add(new CGNode(cgnode, child));
							usedMethods.put(signature, 1);

							Body childBody = parent.getActiveBody();
							UnitGraph childUnitGraph = new BriefUnitGraph(childBody);
							List<Unit> entryPoints = childUnitGraph.getHeads();
							Iterator<Unit> entryIter = entryPoints.iterator();
							while (entryIter.hasNext()) {
								Unit entryPoint = entryIter.next();
								stream2.println(signature + " , " + entryPoint.toString());
							}
							Iterator<Unit> childCfgNodes = childUnitGraph.iterator();
							while (childCfgNodes.hasNext()) {
								Unit ccfgNode = childCfgNodes.next();
								ArrayList<String> succList = new ArrayList<String>();
								List<Unit> succs = unitGraph.getSuccsOf(ccfgNode);
								Iterator<Unit> succIter = succs.iterator();
								while (succIter.hasNext()) {
									Unit nextUnit = succIter.next();
									succList.add(nextUnit.toString());
									stream2.println(signature + " , " +
											ccfgNode.toString() + " , " +
											nextUnit.toString());
								}


							}
						} else {
							continue;
						}
						/*System.out.println(parent.getDeclaringClass()+"."+parent.getName()+
								" , " + child.getDeclaringClass()+"."+child.getName());*/
						stream.println(parentSignature +
								" , " + signature);
						//System.out.println(cgnode.level);
						//System.out.println();
						count++;
					}
					nodes.remove(0);
				}
				System.out.println("**********************************");
				System.out.println(count);
			}

		}));

		Main.main(args);
	}

	public static boolean isMethodFiltered(String signature, String className) {
		if (
				signature.contains("init")
				|| className.startsWith("java")
				|| className.startsWith("sun.")
				|| className.startsWith("jdk")
		) {
			return true;
		}
		return false;
	}
}
