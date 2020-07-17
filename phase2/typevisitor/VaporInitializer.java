package typevisitor;
import syntaxtree.*;
import visitor.*;
import java.util.*;

public class VaporInitializer extends GJDepthFirst<VaporReturnStruct, String>{
	
	ProgramEnvironment environment;
	ClassEnvironment classEnvironment;
	MethodEnvironment methodEnvironment;

	int depth;
	int var;
	
	String content;
	
	public VaporInitializer(ProgramEnvironment env){
		environment = env;

		depth = 0;
	}
	
	public void addContentLine(String c){
		for(int i = 0; i < depth; ++i) content += "  ";
		content += c + "\n";
	}
	
	public VaporReturnStruct visit(Goal n, String para){
		content = "";
		super.visit(n, null);
		return new VaporReturnStruct(content, null);
	}
	
	public VaporReturnStruct visit(MainClass n, String para) {
		addContentLine("func Main()");
		depth += 1;
		
		n.f0.accept(this, null);
		n.f1.accept(this, null);
		n.f2.accept(this, null);
		n.f3.accept(this, null);
		n.f4.accept(this, null);
		n.f5.accept(this, null);
		n.f6.accept(this, null);
		n.f7.accept(this, null);
		n.f8.accept(this, null);
		n.f9.accept(this, null);
		n.f10.accept(this, null);
		n.f11.accept(this, null);
		n.f12.accept(this, null);
		n.f13.accept(this, null);
		n.f14.accept(this, null);
		n.f15.accept(this, null);
		n.f16.accept(this, null);
		n.f17.accept(this, null);
		
		depth -= 1;
		addContentLine("");
		
		return null;
   }
	
	public VaporReturnStruct visit(ClassDeclaration n, String para) {
		// getting class
		n.f0.accept(this, null);
		classEnvironment = environment.getClass(n.f1.accept(this, null).identifier);
		
		n.f2.accept(this, null);
		n.f3.accept(this, null);
		n.f4.accept(this, null);
		n.f5.accept(this, null);
		
		return null;
	}
   
   public VaporReturnStruct visit(ClassExtendsDeclaration n, String para) {
	    // getting class
		n.f0.accept(this, null);
		classEnvironment = environment.getClass(n.f1.accept(this, null).identifier);
		
		n.f2.accept(this, null);
		n.f3.accept(this, null);
		n.f4.accept(this, null);
		n.f5.accept(this, null);
		n.f6.accept(this, null);
		n.f7.accept(this, null);
		
		return null;
	}
	
	public VaporReturnStruct visit(MethodDeclaration n, String para) {
		n.f0.accept(this, null);
		n.f1.accept(this, null);
		
		String methodId = n.f2.accept(this, null).identifier;
		methodEnvironment = classEnvironment.getMethod(methodId);
		
		List<String> parameters = new ArrayList<String>();
		parameters.add("this");
		for(String p: methodEnvironment.parameterIDs)
			parameters.add(p);
		addContentLine("func " + classEnvironment.identifier + "." + methodEnvironment.identifier + "(" + String.join(" ", parameters) + ")");
		depth += 1;
		var = 0;
	
		n.f3.accept(this, null);
		n.f4.accept(this, null);
		n.f5.accept(this, null);
		n.f6.accept(this, null);
		n.f7.accept(this, null);
		n.f8.accept(this, null);
		n.f9.accept(this, null);
		n.f10.accept(this, null);
		n.f11.accept(this, null);
		n.f12.accept(this, null);

		depth -= 1;
		addContentLine("");
		
		return null;
	}
	
	public VaporReturnStruct visit(MessageSend n, String para) {
		
		// TODO: Do a null check
		VaporReturnStruct exp = n.f0.accept(this, null);
		if (exp == null || exp.type == null) return null;
		
		String varid = "t." + var++;
		addContentLine(varid + " = [" + exp.identifier + "]");
	
		n.f1.accept(this, null);
		String methodId = n.f2.accept(this, null).identifier;
		addContentLine(varid + " = [" + varid + "+" + exp.type.getMethod(methodId).offset + "]");
		
		n.f3.accept(this, null);
		n.f4.accept(this, null);
		n.f5.accept(this, null);
		
		return null;
	}
	
	public VaporReturnStruct visit(PrimaryExpression n, String para) {
		return n.f0.accept(this, "");
	}
	
	public VaporReturnStruct visit(Identifier n, String para){
		String id = n.f0.toString();
		if (para == null){
			return new VaporReturnStruct(id, null);
		} else { 
			String fieldType = methodEnvironment.getFieldType(id);
			if (fieldType == null)
				return new VaporReturnStruct(id, null);
			return new VaporReturnStruct(id, environment.getClass(fieldType));
		}
	}
	
	public VaporReturnStruct visit(AllocationExpression n, String para) {
		n.f0.accept(this, null);
		ClassEnvironment temp = environment.getClass(n.f1.accept(this, null).identifier);
		n.f2.accept(this, null);
		n.f3.accept(this, null);

		String v = "t." + var++;
		addContentLine(v + " = HeapAllocZ(" + temp.size + ")");
		addContentLine("[" + v + "] = :vmt_" + temp.identifier); 
		return new VaporReturnStruct(v, temp);
   }
	
}