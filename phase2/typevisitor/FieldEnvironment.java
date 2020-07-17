package typevisitor;
import java.util.*;

public class FieldEnvironment{
	public String identifier;
	public String type;
	
	public int offset;
	
	public FieldEnvironment(String identifier, String type){
		this.identifier = identifier;
		this.type = type;
	}
	
	public void debug(){
		System.out.println(identifier + "(" + offset + ") -> " + type);
	}
}
