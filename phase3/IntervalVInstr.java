import cs132.vapor.ast.*;

public class IntervalVInstr extends VInstr.VisitorR<IVIStruct, RuntimeException> {
	
	public IVIStruct visit(VAssign a) throws RuntimeException{
		return new IVIStruct(a.dest.toString(), a.source.toString());
	}
	
	public IVIStruct visit(VCall a) throws RuntimeException{
		String dest = null;
		if (a.dest != null) dest = a.dest.toString();

		return new IVIStruct(getVVarRef(a.dest), getVOperand(a.args, a.addr), true, null, null);
	}
	
	public IVIStruct visit(VBuiltIn a) throws RuntimeException{
		return new IVIStruct(getVVarRef(a.dest), getVOperand(a.args));
	}
	
	public IVIStruct visit(VMemWrite a) throws RuntimeException{
		VMemRef.Global d = (VMemRef.Global)a.dest;
		return new IVIStruct(d.base.toString(), a.source.toString());
	}
	
	public IVIStruct visit(VMemRead a) throws RuntimeException{
		VMemRef.Global s = (VMemRef.Global)a.source;
		return new IVIStruct(a.dest.toString(), s.base.toString());
	}
	
	public IVIStruct visit(VBranch a) throws RuntimeException{
		return new IVIStruct(null, a.value.toString(), false, a.target.ident, null);
	}
	
	public IVIStruct visit(VGoto a) throws RuntimeException{
		VAddr.Label s = (VAddr.Label)a.target;
		return new IVIStruct(null, new String[0], false, null, s.toString());
	}
	
	public IVIStruct visit(VReturn a) throws RuntimeException{
		return new IVIStruct(null, getVOperand(a.value));
	}

	private String getVVarRef(VVarRef v){
		if (v == null) return null;
		return v.toString();
	}
	
	private String[] getVOperand(VOperand o){
		if (o == null) return new String[0];
		return new String[] { o.toString() };
	}
	
	private String[] getVOperand(VOperand[] ops){
		String[] s = new String[ops.length];
		for(int i = 0; i < ops.length; ++i){
			s[i] = ops[i].toString();
		}
		return s;
	}
	
	private String[] getVOperand(VOperand[] ops, VAddr a){
		String[] s = new String[ops.length + 1];
		for(int i = 0; i < ops.length; ++i){
			s[i] = ops[i].toString();
		}
		s[ops.length] = a.toString();
		return s;
	}
	
}