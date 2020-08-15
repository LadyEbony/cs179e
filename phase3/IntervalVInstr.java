import cs132.vapor.ast.*;
import java.util.*;

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