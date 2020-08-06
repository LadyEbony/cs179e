package typevisitor;

public class VaporReturnStruct{
	public String identifier;
	public ClassEnvironment type;
	
	public boolean isNullable, isTemporary;
	
	public VaporReturnStruct(String id){
		identifier = id;
		type = null;
		
		isNullable = true;
		isTemporary = false;
	}
	
	public VaporReturnStruct(String id, ClassEnvironment env){
		identifier = id;
		type = env;
		
		isNullable = true;
		isTemporary = false;
	}
	
	public VaporReturnStruct(String id, ClassEnvironment env, boolean n, boolean t){
		identifier = id;
		type = env;
		
		isNullable = n;
		isTemporary = t;
	}
}