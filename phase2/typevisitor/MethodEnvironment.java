package typevisitor;
import java.util.*;

public class MethodEnvironment{
	public String identifier;
	public String returnType;
	public String[] parameterIDs;
	public String[] parameterTypes;
	
	public int offset;
	
	private Hashtable<String, String> idTypeConversion;
	private Hashtable<String, String> fieldTypeConversion;
	
	public MethodEnvironment(String identifier, String returnType, String[] parameterIDs, String[] parameterTypes){
		this.identifier = identifier;
		this.returnType = returnType;
		this.parameterIDs = parameterIDs;
		this.parameterTypes = parameterTypes;
	
		idTypeConversion = new Hashtable<String, String>();
		for(int i = 0; i < parameterIDs.length; ++i){
			idTypeConversion.put(parameterIDs[i], parameterTypes[i]);
		}
		fieldTypeConversion = new Hashtable<String, String>();
	}
	
	public boolean addField(String id, String type){
		if (fieldTypeConversion.containsKey(id))
			return false;
		
		fieldTypeConversion.put(id, type);
		return true;
	}
	
	public String getFieldType(String id){
		if (fieldTypeConversion.containsKey(id)){
			return fieldTypeConversion.get(id);
		}
		return null;
	}
	
	public String getIdType(String id){
		if (idTypeConversion.containsKey(id)){
			return idTypeConversion.get(id);
		}
		return null;
	}
	
	public boolean compare(MethodEnvironment other){
		if (returnType != other.returnType) return false;
		if (parameterTypes.length != other.parameterTypes.length) return false;
		for(int i = 0; i < parameterTypes.length; ++i){
			if (parameterTypes[i] != other.parameterTypes[i]) return false;
		}
		return true;
	}
	
	public boolean compare(ProgramEnvironment env, String[] types){
		if (parameterTypes.length != types.length) return false;
		for(int i = 0; i < parameterTypes.length; ++i){
			String type = types[i];
			while(parameterTypes[i] != type){
				ClassEnvironment c = env.getClass(type);
				if (c.parent == null) return false;
				type = c.parent.identifier;
			}
		}
		return true;
	}
	
	public void debug(){
		System.out.println(identifier + "(" + offset + ") [" + String.join(", ", parameterIDs) + "],[" + String.join(", ", parameterTypes) + "] -> " + returnType);
	}
}
