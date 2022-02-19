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
import java.util.Hashtable;

public class CallGraphExample
{
	public static void main(String[] args) {
		// Soot classpath
		String path = System.getProperty("user.dir")+"/out/production/call_graph";

		// Setting the classpath programatically
		Options.v().set_prepend_classpath(true);
		Options.v().set_soot_classpath(path);

		// The second argument is the path to the main class of a project you want to analyze
		// (in this case, testers/ExampleCode.java)
		args = new String[]{"-w", "hello.HelloWorld", "-src-prec", "only-class"};

		PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", new SceneTransformer() {

			@Override
			protected void internalTransform(String phaseName, Map options) {
				CHATransformer.v().transform();

				// This line generates the call graph of the project you're going to analyze
				CallGraph cg = Scene.v().getCallGraph();
				// Now that you have the call graph, you can start doing whatever type of
				// analysis needed for your problem. Below, we are going to traverse the
				// generated call graph and print caller/callee relationships (BFS)

				System.out.println("**********************************");
				SootMethod src = Scene.v().getMainClass().getMethodByName("main");
				//List<SootMethod> nodes = new ArrayList<>();
				List<CGNode> nodes = new ArrayList<>();
				Hashtable<String, Integer> usedMethods = new Hashtable();
				nodes.add(new CGNode(src));
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
					usedMethods.put(src.getSignature(), 1);
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
								" calls " + child.getDeclaringClass()+"."+child.getName());
						System.out.println(cgnode.level);
						System.out.println();
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
}
