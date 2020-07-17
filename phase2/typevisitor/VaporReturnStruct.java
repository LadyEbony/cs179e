package typevisitor;

public class VaporReturnStruct{
	public String identifier;
	public ClassEnvironment type;
	
	public VaporReturnStruct(String id){
		identifier = id;
		type = null;
	}
	
	public VaporReturnStruct(String id, ClassEnvironment env){
		identifier = id;
		type = env;
	}
}