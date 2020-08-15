import java.util.*;

public class IVIStruct {
	public Set<String> dest;
	public Set<String> source;
	
	public String ifLabel;
	public String gotoLabel;
	
	public IVIStruct(Set<String> d, Set<String> s, String ifl, String gotol){
		dest = d;
		source = s;
		
		ifLabel = ifl;
		gotoLabel = gotol;
	}
	
	public IVIStruct(Set<String> d, Set<String> s){
		dest = d;
		source = s;
	}
	
	public String toString(){
		return String.format("dest: %s | source: %s | %s", String.join(",", dest), String.join(",", source));
	}
	
}