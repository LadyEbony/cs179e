package typevisitor;
import syntaxtree.*;
import visitor.*;
import java.util.*;

public class ProgramSetup extends DepthFirstVisitor{

	ProgramEnvironment environment;
	String errorMessage;
	
	public ProgramSetup(ProgramEnvironment env){
		environment = env;
		errorMessage = null;
	}
	
	public String getError(){
		return errorMessage;
	}
	
	void addClass(String cls) {
		if (!environment.addClass(cls)){
			if (errorMessage == null){
				errorMessage = "Error: Duplicate class identifiers \"" + cls + "\"";
			}
		}
	}

	public void visit(MainClass n){
		addClass(n.f1.f0.toString());
		super.visit(n);
	}
	
	public void visit(ClassDeclaration n){
		addClass(n.f1.f0.toString());
		super.visit(n);
	}
	
	public void visit(ClassExtendsDeclaration n){
		addClass(n.f1.f0.toString());
		super.visit(n);
	}
	
}
