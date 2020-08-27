import cs132.vapor.ast.*;
import java.util.*;

class VaporVInstr extends VInstr.VisitorPR<Hashtable<String, String>, String , RuntimeException> {
	private String returnString;

	public String visit(Hashtable<String,String> map, VAssign a) throws RuntimeException {
		String reg = map.get(a.source.toString());
		String s = reg;
		return s;
	}

	public String visit(Hashtable<String,String> map, VCall a) throws RuntimeException {
		String reg = map.get(a.addr.toString());
		return reg;
	}
	public String visit(Hashtable<String,String> map, VBuiltIn a) throws RuntimeException {
	/*
		String s = "Hi";
		for(VOperand v : a.args) {
		
		}
		String reg = map.get(v.toString());
		return reg;
	*/
		return "Hi";
	}

	public String visit(Hashtable<String,String> map, VMemWrite a) throws RuntimeException {
		//VMemRef.Global d= (VMemRed.GLobal)a.dest;
		String reg = map.get(a.source.toString());
		//System.out.println("Test: " + reg);
		return reg;
	}
	public String visit(Hashtable<String,String> map, VMemRead a) throws RuntimeException {
		String reg = map.get(a.source.toString());
		return reg;
	}
	public String visit(Hashtable<String,String> map, VBranch a) throws RuntimeException {
		String reg = map.get(a.value.toString());
		return reg;
	}
	public String visit(Hashtable<String,String> map, VGoto a) throws RuntimeException {
		VAddr.Label s = (VAddr.Label)a.target;
		String reg = map.get(s.label.ident);
		return reg;
	}
	public String visit(Hashtable<String,String> map, VReturn a) throws RuntimeException {
		String reg = "";
		if(a.value != null) {
			reg = map.get(a.value.toString());
		}
		return reg;
	}
}