import java.util.*;

public class IVIStruct {
	public String dest;
	public String[] source;
	
	public boolean isFunction;
	public String ifLabel;
	public String gotoLabel;
	
	public IVIStruct(String d, String[] s, boolean func, String ifl, String gotol){
		dest = d;
		source = s;
		
		isFunction = func;
		ifLabel = ifl;
		gotoLabel = gotol;
	}
	
	public IVIStruct(String d, String[] s){
		dest = d;
		source = s;
	}
	
	public IVIStruct(String d, String s, boolean func, String ifl, String gotol){
		dest = d;
		source = new String[]{ s };
		
		isFunction = func;
		ifLabel = ifl;
		gotoLabel = gotol;
	}
	
	public IVIStruct(String d, String s){
		dest = d;
		source = new String[]{ s };
	}
	
	public String toString(){
		return String.format("dest: %s | source: %s | %s", dest, Arrays.toString(source), Boolean.toString(isFunction));
	}
	
}