package typevisitor;
import java.util.*;

public class ProgramEnvironment{
	public List<ClassEnvironment> classes;
	
	public ProgramEnvironment(){
		classes = new ArrayList<ClassEnvironment>();
	}
	
	public boolean addClass(String cls){
		if (existsClass(cls)){
			return false;
		}
		
		classes.add(new ClassEnvironment(cls));
		return true;
	}
	
	public boolean existsClass(String cls){
		for(ClassEnvironment env : classes){
			if (env.identifier == cls){
				return true;
			}
		}
		return false;
	}
	
	public ClassEnvironment getClass(String cls){
		for(ClassEnvironment env : classes){
			if (env.identifier == cls){
				return env;
			}
		}
		return null;
	}

}
