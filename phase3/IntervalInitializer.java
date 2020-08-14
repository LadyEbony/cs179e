import cs132.vapor.ast.*;
import java.util.*;

class IntervalInitializer{
	
	public List<Interval> Initialize(VFunction function){
		IntervalVInstr visitor = new IntervalVInstr();
		
		Hashtable<String, Interval> set = new Hashtable<String, Interval>();
		VInstr[] body = function.body;

		// add live variables
		for(int line = 0; line < body.length; ++line){
			VInstr node = body[line];
			IVIStruct var = node.accept(visitor);
			
			// if contains any variables
			if (var != null){
				// args first
				for(String source: var.source){
					if (isVariable(source)){
						if (!set.containsKey(source)) {
							set.put(source, new Interval(source, line, line));
							System.out.println(source);
						} else { 
							Interval i = set.get(source);
							i.end = line;
						}
					}
				}
			}
		}
		
		// create list
		List<Interval> result = new ArrayList<Interval>();
		Enumeration e = set.elements();
		while(e.hasMoreElements())
			result.add((Interval)e.nextElement());
		
		
		
		return result;
	}
	
	private boolean isVariable(String s){
		if (s == null) return false;
		
		char c = s.charAt(0);
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}
	
}