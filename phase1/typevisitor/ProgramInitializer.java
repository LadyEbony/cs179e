package typevisitor;
import syntaxtree.*;
import visitor.*;
import java.util.*;

public class ProgramInitializer extends GJDepthFirst<String, ClassEnvironment>{

	ProgramEnvironment environment;
	String errorMessage;
	
	int depth;
	List<String> parameterIds;
	List<String> parameterTypes;
	
	public ProgramInitializer(ProgramEnvironment env){
		environment = env;
		errorMessage = null;
		
		// 
		depth = 0;
		parameterIds = new ArrayList<String>();
		parameterTypes = new ArrayList<String>();
	}
	
	public String getError(){
		return errorMessage;
	}
	
	void addField(ClassEnvironment env, String id, String type){
		if (!env.addField(id, type)){
			if (errorMessage == null){
				errorMessage = "Error: Duplicate class field identifiers \"" + id + "\"";
			}
		}
	}
	
	void addMethod(ClassEnvironment env, String id, String returnType, String[] parameterIds, String[] parameterTypes){
		if (!env.addMethod(id, returnType, parameterIds, parameterTypes)){
			if (errorMessage == null){
				errorMessage = "Error: Duplicate class method identifiers \"" + id + "\"";
			}
		}
	}
	
	public String visit(MainClass n, ClassEnvironment env){
		
		// Main Class has no variables or methods to declare
		depth += 1;
		super.visit(n, env);
		depth -= 1;
		
		return null;
	}
	
	public String visit(ClassDeclaration n, ClassEnvironment env){
		env = environment.getClass(n.f1.f0.toString());
		super.visit(n, env);
		//env.debug();
		
		return null;
	}
	
	public String visit(ClassExtendsDeclaration n, ClassEnvironment env){
		env = environment.getClass(n.f1.f0.toString());
		// inheritence
		env.parent = environment.getClass(n.f3.f0.toString());
		super.visit(n, env);
		//env.debug();
		
		return null;
	}
	
	public String visit(VarDeclaration n, ClassEnvironment env){
		// class variables have 0 depth
		// we only care about var declaration here
		if (depth == 0){
			String type = n.f0.accept(this, env);
			String id = n.f1.accept(this, env);
			addField(env, id, type);
		}
		return null;
	}
	
	public String visit(MethodDeclaration n, ClassEnvironment env){
		depth += 1;
		parameterIds.clear();
		parameterTypes.clear();
		
		n.f0.accept(this, env);
		String rtype = n.f1.accept(this, env);
		String id = n.f2.accept(this, env);
		n.f3.accept(this, env);
		n.f4.accept(this, env);
		n.f5.accept(this, env);
		n.f6.accept(this, env);
		n.f7.accept(this, env);
		n.f8.accept(this, env);
		n.f9.accept(this, env);
		n.f10.accept(this, env);
		n.f11.accept(this, env);
		n.f12.accept(this, env);
		String[] pids = parameterIds.toArray(new String[0]);
		String[] ptypes = parameterTypes.toArray(new String[0]);
	
		depth -= 1;

		addMethod(env, id, rtype, pids, ptypes);
		return null;
	}

	public String visit(FormalParameter n, ClassEnvironment env) {
		// we ain't returning shit, we building a list
		String id = n.f1.accept(this, env);
		String type = n.f0.accept(this, env);
		
		if (parameterIds.contains(id)){
			if (errorMessage == null){
				errorMessage = "Error: Duplicate method parameters identifiers \"" + id + "\"";
			}
		}
		
		parameterIds.add(id);
		parameterTypes.add(type);
		return null;
   }
	
	public String visit(Type n, ClassEnvironment env){
		return n.f0.accept(this, env);
	} 
	
	public String visit(Identifier n, ClassEnvironment env){
		return n.f0.toString();
	} 
	
	public String visit(ArrayType n, ClassEnvironment env){
		return "int[]";
	}
	
	public String visit(BooleanType n, ClassEnvironment env){
		return "bool";
	}
	
	public String visit(IntegerType n, ClassEnvironment env){
		return "int";
	}
	
}
