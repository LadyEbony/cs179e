package typevisitor;
import java.util.*;

public class ProgramVerifier {
	
	ProgramEnvironment environment;
	String errorMessage;
	
	public ProgramVerifier(ProgramEnvironment env){
		environment = env;
		errorMessage = null;
	}
	
	public String getError(){
		return errorMessage;
	}
	
	public void verify(){
		// check parents for circular pattern
		// not actually required but it's easy to check anyhow
		for(ClassEnvironment env : environment.classes){
			List<ClassEnvironment> loop = new ArrayList<ClassEnvironment>();
			ClassEnvironment cur = env;
			loop.add(cur);
			while(cur.parent != null){
				cur = cur.parent;
				// circular, error
				if (loop.contains(cur)){
					errorMessage = "Error: Circular class extenstion \"" + env.identifier + "\"";
					return;
				}
				loop.add(cur);
			}
		}
		
		// check parents for overloading
		for(ClassEnvironment env : environment.classes){
			ClassEnvironment par = env.parent;
			if (par != null){
				for(MethodEnvironment method : env.methods){
					MethodEnvironment methodPar = par.getMethod(method.identifier);
					if (methodPar != null && !method.compare(methodPar)){
						errorMessage = "Error: Overloading not supported for \"" + method.identifier + "\"";
						return;
					}
				}
			}
		}
		
		// set offset values
		for(ClassEnvironment env : environment.classes){
			List<FieldEnvironment> fields = env.fields;
			for(int i = 0; i < fields.size(); ++i){
				fields.get(i).offset = (i + 1) * 4;
			}
			
			List<MethodEnvironment> methods = env.methods;
			for(int i = 0; i < methods.size(); ++i){
				methods.get(i).offset = i * 4;
			}
			
			env.size = (fields.size() + 1) * 4;
			
			//env.debug();
		}
	}
	
}
