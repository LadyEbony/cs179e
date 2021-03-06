import cs132.vapor.ast.*;
import java.util.*;

class IntervalInitializer{
	
	public List<Interval> Initialize(VFunction function){
		IntervalVInstr visitor = new IntervalVInstr();
		ControlFlowGraph graph = new ControlFlowGraph();
		Hashtable<String, Integer> labels = new Hashtable<String, Integer>();
		VInstr[] body = function.body;
		
		// add base nodes
		for(int line = 0; line < body.length; ++line){
			VInstr node = body[line];
			IVIStruct var = node.accept(visitor);
			
			graph.AddNode(node, var.dest, var.source);
		}

		for(VCodeLabel label: function.labels){
			//System.out.println("Adding label " + label.ident + " to line " + label.instrIndex);
			labels.put(label.ident, label.instrIndex);
		}
		
		// add edges
		ControlFlowGraphNode prev = null;
		for(int line = 0; line < body.length; ++line){
			VInstr node = body[line];
			IVIStruct var = node.accept(visitor);
			
			ControlFlowGraphNode cur = graph.GetNode(line);
			
			if (prev != null)
				graph.AddEdge(prev, cur);
			
			// if label
			if (var.ifLabel != null){
				ControlFlowGraphNode temp = graph.GetNode(labels.get(var.ifLabel));
				graph.AddEdge(cur, temp);
			}
			
			// goto label
			if (var.gotoLabel != null){
				ControlFlowGraphNode temp = graph.GetNode(labels.get(var.gotoLabel));
				graph.AddEdge(cur, temp);
				prev = null;
				continue;
			} 
			
			prev = cur;
		}
		
		// perform analysis
		return graph.GetLiveness();
	}
	
	public class IVIStruct {
		public Set<String> dest;
		public Set<String> source;
		
		public String ifLabel;
		public String gotoLabel;
		
		public IVIStruct(Set<String> d, Set<String> s, String ifl, String gotol){
			dest = d;
			source = s;
			
			ifLabel = ifl;
			gotoLabel = gotol;
		}
		
		public IVIStruct(Set<String> d, Set<String> s){
			dest = d;
			source = s;
		}
		
	}
	
	public class IntervalVInstr extends VInstr.VisitorR<IVIStruct, RuntimeException> {
	
		public IVIStruct visit(VAssign a) throws RuntimeException{
			Set<String> dest = new HashSet<String>();
			addToSet(dest, a.dest.toString());
			
			Set<String> source = new HashSet<String>();
			addToSet(source, a.source.toString());
			
			return new IVIStruct(dest, source);
		}
		
		public IVIStruct visit(VCall a) throws RuntimeException{
			Set<String> dest = new HashSet<String>();
			if(a.dest != null) addToSet(dest, a.dest.toString());
			
			Set<String> source = new HashSet<String>();
			addToSet(source, a.addr.toString());
			for(VOperand v: a.args){
				addToSet(source, v.toString());
			}
			return new IVIStruct(dest, source);
		}
		
		public IVIStruct visit(VBuiltIn a) throws RuntimeException{
			Set<String> dest = new HashSet<String>();
			if(a.dest != null) addToSet(dest, a.dest.toString());
			
			Set<String> source = new HashSet<String>();
			for(VOperand v: a.args){
				addToSet(source, v.toString());
			}
			
			return new IVIStruct(dest, source);
		}
		
		public IVIStruct visit(VMemWrite a) throws RuntimeException{
			VMemRef.Global d = (VMemRef.Global)a.dest;
			
			Set<String> dest = new HashSet<String>();
			// [this + x] means that this is a use, not def
			
			Set<String> source = new HashSet<String>();
			addToSet(source, d.base.toString());
			addToSet(source, a.source.toString());
			
			return new IVIStruct(dest, source);
		}
		
		public IVIStruct visit(VMemRead a) throws RuntimeException{
			VMemRef.Global s = (VMemRef.Global)a.source;
			
			Set<String> dest = new HashSet<String>();
			addToSet(dest, a.dest.toString());
			
			Set<String> source = new HashSet<String>();
			addToSet(source, s.base.toString());
			
			return new IVIStruct(dest, source);
		}
		
		public IVIStruct visit(VBranch a) throws RuntimeException{
			Set<String> dest = new HashSet<String>();

			Set<String> source = new HashSet<String>();
			addToSet(source, a.value.toString());
			
			return new IVIStruct(dest, source, a.target.ident, null);
		}
		
		public IVIStruct visit(VGoto a) throws RuntimeException{
			VAddr.Label s = (VAddr.Label)a.target;

			Set<String> dest = new HashSet<String>();
			Set<String> source = new HashSet<String>();
			
			return new IVIStruct(dest, source, null, s.label.ident);
		}
		
		public IVIStruct visit(VReturn a) throws RuntimeException{
			Set<String> dest = new HashSet<String>();
			
			Set<String> source = new HashSet<String>();
			if(a.value != null) addToSet(source, a.value.toString());
			
			return new IVIStruct(dest, source);
		}
		
		private void addToSet(Set<String> set, String sample){
			if (isVariable(sample))
				set.add(sample);
		}
		
		private boolean isVariable(String s){
			if (s == null) return false;
			
			char c = s.charAt(0);
			return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
		}
		
	}
	
	public class ControlFlowGraphNode {
		public VInstr instr;
		public int index;
		public boolean function;
		
		public Set<String> def;
		public Set<String> use;
		
		public List<ControlFlowGraphNode> next;
		public List<ControlFlowGraphNode> prev;
		
		// analysis data
		public Set<String> in;
		public Set<String> out;
		
		public ControlFlowGraphNode(VInstr i, int ind, Set<String> d, Set<String> u){
			instr = i;
			index = ind;
			function = i instanceof VCall;
			
			def = d;
			use = u;
			
			next = new ArrayList<ControlFlowGraphNode>();
			prev = new ArrayList<ControlFlowGraphNode>();
		}
		
	}
	
	public class ControlFlowGraph {
		List<ControlFlowGraphNode> nodes;
		
		public ControlFlowGraph(){
			nodes = new ArrayList<ControlFlowGraphNode>();
		}
		
		public ControlFlowGraphNode AddNode(VInstr instr, Set<String> d, Set<String> u){
			ControlFlowGraphNode n = new ControlFlowGraphNode(instr, nodes.size(), d, u);
			nodes.add(n);
			return n;
		}

		public void AddEdge(ControlFlowGraphNode a, ControlFlowGraphNode b){
			a.next.add(b);
			b.prev.add(a);
		}
		
		public ControlFlowGraphNode GetNode(int index){
			return nodes.get(index);
		}
		
		public int GetIndex(ControlFlowGraphNode node){
			return node.index;
		}
		
		public List<Interval> GetLiveness(){
			for(ControlFlowGraphNode node: nodes){
				node.in = new HashSet<String>();
				node.out = new HashSet<String>();
			}
			boolean notcomplete = false;
			
			do{
				notcomplete = false;
				
				// https://www.cs.colostate.edu/~mstrout/CS553/slides/lecture03.pdf
				for(ControlFlowGraphNode node: nodes){
					// in'[n] = in[n]
					Set<String> in = new HashSet<String>(node.in);
					
					// out'[n] = out[n]
					Set<String> out = new HashSet<String>(node.out);
					
					// in[n] = use[n] U (out[n] - def[n])
					Set<String> in2 = new HashSet<String>(node.use);
					Set<String> diff = new HashSet<String>(out);
					diff.removeAll(node.def);
					in2.addAll(diff);
					
					// out[n] = (for each s in n.next) U in[s]
					Set<String> out2 = new HashSet<String>();
					for(ControlFlowGraphNode next: node.next){
						out2.addAll(next.in);
					}
					
					node.in = in2;
					node.out = out2;
					
					// in'[n] = in[n] and out'[n] = out[n] for all n
					if (!in2.equals(in) || !out2.equals(out))
						notcomplete = true;
				}
				
			} while(notcomplete);
						
			// get final result
			Hashtable<String, Interval> intervals = new Hashtable<String, Interval>(); 
			
			// get interval range
			for(int i = 0; i < nodes.size(); ++i){
				ControlFlowGraphNode node = nodes.get(i);
				
				// active[n] = def[n] U in[n]
				Set<String> active = new HashSet<String>(node.def);
				active.addAll(node.in);
				
				for(String use: active){
					if (intervals.containsKey(use)){
						Interval temp = intervals.get(use);
						temp.start = Math.min(temp.start, i);
						temp.end = Math.max(temp.end, i);
					} else {
						Interval temp = new Interval(use, i, i);
						intervals.put(use, temp);
					}
				}
			}
			
			// get $s status of variable
			for(int i = 0; i < nodes.size(); ++i){
				ControlFlowGraphNode node = nodes.get(i);
				if (!node.function) continue;
				
				// valid[n] = out[n] - def[n]
				Set<String> valid = new HashSet<String>(node.out);
				valid.removeAll(node.def);
				
				for(String use: valid){
					Interval temp = intervals.get(use);
					temp.calleeSaved = true;
				}
			}
			
			List<Interval> result = new ArrayList<Interval>();
			Enumeration e = intervals.elements();
			while(e.hasMoreElements())
				result.add((Interval)e.nextElement());
			
			return result;
		}
		
		public void Debug(){
			int i = 0;
			for(ControlFlowGraphNode node: nodes){
				List<String> prev = new ArrayList<String>();
				for(ControlFlowGraphNode p: node.prev){
					prev.add(Integer.toString(GetIndex(p)));
				}
				
				List<String> next = new ArrayList<String>();
				for(ControlFlowGraphNode n: node.next){
					next.add(Integer.toString(GetIndex(n)));
				}
				
				System.out.println(i + ":\tprev[" + String.join(", ", prev) + "]\tnext[" + String.join(", ", next) + "]");
				++i;
			}
		}
	}
	
	
	
	
}