package dk.brics.soot.callgraphs;

import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Targets;
import soot.options.Options;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Hashtable;

public class CallGraphExample
{
	public static void main(String[] args) {
		// Soot classpath
		String path = System.getProperty("user.dir")+ "/commons-csv-master/target/classes";

		// Setting the classpath programatically
		System.out.println(path);
		Options.v().set_prepend_classpath(true);
		Options.v().set_soot_classpath(path);

		// The second argument is the path to the main class of a project you want to analyze
		// (in this case, testers/ExampleCode.java)
		args = new String[]{"-w", "-process-dir", path, "-src-prec", "only-class"};

		PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", new SceneTransformer() {

			@Override
			protected void internalTransform(String phaseName, Map options) {
				CHATransformer.v().transform();

				File outputFile = new File("graph.txt");
				PrintStream stream = null;
				try {
					stream = new PrintStream(outputFile);
				} catch (Exception e) {

				}

				// This line generates the call graph of the project you're going to analyze
				System.out.println("Getting call graph");
				CallGraph cg = Scene.v().getCallGraph();
				// Now that you have the call graph, you can start doing whatever type of
				// analysis needed for your problem. Below, we are going to traverse the
				// generated call graph and print caller/callee relationships (BFS)

				System.out.println("**********************************");
				Iterator<MethodOrMethodContext> iter = cg.sourceMethods();

				List<CGNode> nodes = new ArrayList<>();
				while (iter.hasNext()) {
					nodes.add(new CGNode(iter.next().method()));
				}
				Hashtable<String, Integer> usedMethods = new Hashtable();
				int count = 0;
				while(!nodes.isEmpty()){
					CGNode cgnode = nodes.get(0);
					SootMethod parent = cgnode.sootMethod;
					String parentClassname = parent.getDeclaringClass().toString();
					String parentSignature = parent.getSignature();
					if (
							usedMethods.contains(parentSignature)
							|| isMethodFiltered(parentSignature, parentClassname)
					) {
						nodes.remove(0);
						continue;
					}
					usedMethods.put(parentSignature, 1);
					Iterator<MethodOrMethodContext> targets = new Targets(cg.edgesOutOf(parent));
					while (targets.hasNext()) {
						SootMethod child = (SootMethod) targets.next();
						String signature = child.getSignature();
						String childClassname = child.getDeclaringClass().toString();
						if (
								! signature.contains("init")
								&& !childClassname.startsWith("java.")
								&& !childClassname.startsWith("sun.")
								&& !usedMethods.containsKey(signature)
						) {
							nodes.add(new CGNode(cgnode, child));
							usedMethods.put(signature, 1);
						} else {
							continue;
						}
						System.out.println(parent.getDeclaringClass()+"."+parent.getName()+
								" , " + child.getDeclaringClass()+"."+child.getName());
						stream.println(parent.getDeclaringClass()+"."+parent.getName()+
								" , " + child.getDeclaringClass()+"."+child.getName());
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
