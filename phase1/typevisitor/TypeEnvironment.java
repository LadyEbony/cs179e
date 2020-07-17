package typevisitor;
import java.util.*;

public class TypeEnvironment{
	
	private Hashtable<String, String> idTypeConversion;
	
	public TypeEnvironment(){
		idTypeConversion = new Hashtable<String, String>();
	}
	
	public boolean addIdType(String id, String type){
		if (idTypeConversion.containsKey(id)) return false;
		
		idTypeConversion.put(id, type);
		return true;
	}
	
	public String getIdType(String id){
		if (idTypeConversion.containsKey(id)){
			return idTypeConversion.get(id);
		}
		return null;
	}
	
}
