package typevisitor;
import java.util.*;

public class ClassEnvironment{
	public String identifier;
	public ClassEnvironment parent;
	
	public List<FieldEnvironment> fields;
	public List<MethodEnvironment> methods;
	
	private Hashtable<String, FieldEnvironment> idFieldConversion;
	private Hashtable<String, MethodEnvironment> idMethodConversion;
	
	public ClassEnvironment(String id){
		identifier = id;
		parent = null;
		fields = new ArrayList<FieldEnvironment>();
		methods = new ArrayList<MethodEnvironment>();
		
		idFieldConversion = new Hashtable<String, FieldEnvironment>();
		idMethodConversion = new Hashtable<String, MethodEnvironment>();
	}
	
		
	public boolean addField(String id, String type){
		if (existsField(id)){
			return false;
		}
		
		FieldEnvironment field = new FieldEnvironment(id, type);
		fields.add(field);
		idFieldConversion.put(id, field);
		return true;
	}
	
	public boolean existsField(String id){
		return idFieldConversion.containsKey(id);
	}
	
	public FieldEnvironment getField(String id){
		if (idFieldConversion.containsKey(id)){
			return idFieldConversion.get(id);
		}
		if (parent != null){
			return parent.getField(id);
		}
		return null;
	}
	
	public boolean addMethod(String id, String returnType, String[] parameterIds, String[] parameterTypes){
		if (existsMethod(id)){
			return false;
		}
		
		MethodEnvironment method = new MethodEnvironment(id, returnType, parameterIds, parameterTypes);
		methods.add(method);
		idMethodConversion.put(id, method);
		return true;
	}
	
	public boolean existsMethod(String id){
		return idMethodConversion.containsKey(id);
	}
	
	public MethodEnvironment getMethod(String id){
		if (idMethodConversion.containsKey(id)){
			return idMethodConversion.get(id);
		}
		if (parent != null){
			return parent.getMethod(id);
		}
		return null;
	}
	
	public void debug(){
		if (parent == null)
			System.out.println(identifier);
		else
			System.out.println(identifier + " (" + parent.identifier + ")");
		
		for(FieldEnvironment field : fields)
			field.debug();
		for(MethodEnvironment method : methods)
			method.debug();
	}
}
