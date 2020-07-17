package typevisitor;
import syntaxtree.*;
import visitor.*;
import java.util.*;

public class VaporSetup {
	
	ProgramEnvironment environment;
	
	public VaporSetup(ProgramEnvironment env){
		environment = env;
	}
	
	public String initialize(){
		String content = "";
		for(ClassEnvironment env : environment.classes){
			if (env.main) {
				content += "\n";
				continue;
			}
			
			content += "const vmt_" + env.identifier + "\n";
			
			HashSet<String> funcset = new HashSet<String>();
			while(env != null){
				for(MethodEnvironment menv : env.methods){
					if (!funcset.contains(menv.identifier)){
						content += "  :" + env.identifier + "." + menv.identifier + "\n";
						funcset.add(menv.identifier);
					}
				}
				env = env.parent;
			}
			content += "\n";
		}
		return content;
	}
	
}