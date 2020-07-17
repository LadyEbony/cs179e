package typevisitor;
import syntaxtree.*;
import visitor.*;
import java.util.*;

public class ProgramTypeCheck extends GJDepthFirst<String, String>{
	
	ProgramEnvironment environment;
	ClassEnvironment classEnvironment;
	MethodEnvironment methodEnvironment;
	TypeEnvironment typeEnvironment;

	String errorMessage;
	
	int depth;
	List<String> parameterTypes;

	public ProgramTypeCheck(ProgramEnvironment env){
		environment = env;
		errorMessage = null;
		
		depth = 0;
		parameterTypes = new ArrayList<String>();
	}
	
	public String getError(){
		return errorMessage;
	}
	
	private String getIdType(String id){
		String result = typeEnvironment.getIdType(id);
		if (result != null) return result;
		if (methodEnvironment != null){
			result = methodEnvironment.getIdType(id);
			if (result != null) return result;
		}
		if (classEnvironment != null){
			FieldEnvironment field = classEnvironment.getField(id);
			if (field != null) return field.type;
		}
		
		return null;
	}
	
	public String visit(MainClass n, String para) {
		depth += 1;
		typeEnvironment = new TypeEnvironment();
		
		super.visit(n, null);
		
		depth -= 1;
		
		return null;
	}
	
	public String visit(ClassDeclaration n, String para) {
		n.f0.accept(this, null);
		classEnvironment = environment.getClass(n.f1.accept(this, null));
		
		n.f2.accept(this, null);
		n.f3.accept(this, null);
		n.f4.accept(this, null);
		n.f5.accept(this, null);
		
		return null;
	}
   
   public String visit(ClassExtendsDeclaration n, String para) {
		n.f0.accept(this, null);
		classEnvironment = environment.getClass(n.f1.accept(this, null));
		
		n.f2.accept(this, null);
		n.f3.accept(this, null);
		n.f4.accept(this, null);
		n.f5.accept(this, null);
		n.f6.accept(this, null);
		n.f7.accept(this, null);
		
		return null;
	}
	
	public String visit(VarDeclaration n, String para) {
		String type = n.f0.accept(this, null);
		String id = n.f1.accept(this, null);
		n.f2.accept(this, null);

		if(depth > 0 && !typeEnvironment.addIdType(id, type)){
			if (errorMessage == null){
				errorMessage = "Error: Duplicate fields identifiers in method statements \"" + id + "\"";
			}
		}
		return null;
	}
	
	public String visit(MethodDeclaration n, String para) {
		depth += 1;
		typeEnvironment = new TypeEnvironment();
		
		n.f0.accept(this, null);
		n.f1.accept(this, null);
		String methodId = n.f2.accept(this, null);
		methodEnvironment = classEnvironment.getMethod(methodId);

		n.f3.accept(this, null);
		n.f4.accept(this, null);
		n.f5.accept(this, null);
		n.f6.accept(this, null);
		n.f7.accept(this, null);
		n.f8.accept(this, null);
		n.f9.accept(this, null);
		String result = n.f10.accept(this, null);
		n.f11.accept(this, null);
		n.f12.accept(this, null);
		
		if (result != methodEnvironment.returnType){
			if (errorMessage == null){
				errorMessage = "Error: Function \"" + methodId + "\" is returning the wrong type. Expected \"" + methodEnvironment.returnType + "\". Got \"" + result + "\"";
			}
		}
		
		depth -= 1;
		
		return null;
	}
	
	public String visit(Type n, String para){
		return n.f0.accept(this, null);
	}
	
	public String visit(Identifier n, String para){
		// now listen here
		// string comparisons are very bad
		// but java only allows generics to be reference type
		// WHAT MONKEY DESIGNED THAT?
		// in any case, I refuse to make a wrapper class. I'm just doing strings
		
		String id = n.f0.toString();
		if (para == null)
			return id;
		return getIdType(id);
	}

	public String visit(ArrayType n, String para) {
		return "int[]";
	}	
	
	public String visit(BooleanType n, String para) {
		return "bool";
	}	
	
	public String visit(IntegerType n, String para) {
		return "int";
	}	
	
	public String visit(AssignmentStatement n, String para) {
		String id = n.f0.accept(this, null);
		String idtype = getIdType(id);
		
		n.f1.accept(this, null);
		String result = n.f2.accept(this, null);
		n.f3.accept(this, null);
		
		if (idtype == null){
			if (errorMessage == null){
				errorMessage = "Error: \"" + id + "\" does not exist";
			}
		}
		if (idtype != result){
			if (errorMessage == null){
				errorMessage = "Error: Trying to insert \"" + result + "\" into \"" + id + "\" which is type of \"" + idtype + "\"";
			}
		}
		return null;
	}
	
	public String visit(ArrayAssignmentStatement n, String para) {
		String id = n.f0.accept(this, null);
		String idtype = getIdType(id);
		
		n.f1.accept(this, null);
		String index = n.f2.accept(this, null);
		n.f3.accept(this, null);
		n.f4.accept(this, null);
		String result = n.f5.accept(this, null);
		n.f6.accept(this, null);
		
		if (idtype == null){
			if (errorMessage == null){
				errorMessage = "Error: \"" + id + "\" does not exist";
			}
		}
		if (idtype != "int[]"){
			if (errorMessage == null){
				errorMessage = "Error: \"" + id + "\" is not of type of int[]";
			}
		}
		if (index != "int"){
			if (errorMessage == null){
				errorMessage = "Error: Array index needs to be of type int. The expression resulted in \"" + index + "\"";
			}
		}
		if (result != "int"){
			if (errorMessage == null){
				errorMessage = "Error: Array index setter (right hand) needs to be of type int. The expression resulted in \"" + result + "\"";
			}
		}
		return null;
	}
   
	public String visit(IfStatement n, String para) {
		n.f0.accept(this, null);
		n.f1.accept(this, null);
		String exp = n.f2.accept(this, null);
		n.f3.accept(this, null);
		n.f4.accept(this, null);
		n.f5.accept(this, null);
		n.f6.accept(this, null);
		
		if (exp != "bool"){
			if (errorMessage == null){
				errorMessage = "Error: If statement must be of type bool. Got \"" + exp + "\"";
			}
		}
		
		return null;
	}
	
	public String visit(WhileStatement n, String para) {
		n.f0.accept(this, null);
		n.f1.accept(this, null);
		String exp = n.f2.accept(this, null);
		n.f3.accept(this, null);
		n.f4.accept(this, null);
		
		if (exp != "bool"){
			if (errorMessage == null){
				errorMessage = "Error: While statement must be of type bool. Got \"" + exp + "\"";
			}
		}
		
		return null;
	}
	
	public String visit(PrintStatement n, String para) {
		n.f0.accept(this, null);
		n.f1.accept(this, null);
		String exp = n.f2.accept(this, null);
		n.f3.accept(this, null);
		n.f4.accept(this, null);
		
		if (exp != "int"){
			if (errorMessage == null){
				errorMessage = "Error: Print statement must be of type int. Got \"" + exp + "\"";
			}
		}
		
		return null;
	}
	
	public String visit(Expression n, String para) {
		return n.f0.accept(this, null);
	}
	
	public String visit(AndExpression n, String para) {
		String a = n.f0.accept(this, null);
		n.f1.accept(this, null);
		String b = n.f2.accept(this, null);
		
		if (a != "bool" || b != "bool"){
			if (errorMessage == null){
				errorMessage = "Error: \"&&\" expression requires two bools. The expression resulted in \"" + a + "\" and \"" + b + "\"";
			}
		}
		return "bool";
	}
	
	public String visit(CompareExpression n, String para) {
		String a = n.f0.accept(this, null);
		n.f1.accept(this, null);
		String b = n.f2.accept(this, null);
		
		if (a != "int" || b != "int"){
			if (errorMessage == null){
				errorMessage = "Error: \"<\" expression requires two ints. The expression resulted in \"" + a + "\" and \"" + b + "\"";
			}
		}
		return "bool";
	}
	
	public String visit(PlusExpression n, String para) {
		String a = n.f0.accept(this, null);
		n.f1.accept(this, null);
		String b = n.f2.accept(this, null);
		
		if (a != "int" || b != "int"){
			if (errorMessage == null){
				errorMessage = "Error: \"+\" expression requires two ints. The expression resulted in \"" + a + "\" and \"" + b + "\"";
			}
		}
		return "int";
	}
	
	public String visit(MinusExpression n, String para) {
		String a = n.f0.accept(this, null);
		n.f1.accept(this, null);
		String b = n.f2.accept(this, null);
		
		if (a != "int" || b != "int"){
			if (errorMessage == null){
				errorMessage = "Error: \"-\" expression requires two ints. The expression resulted in \"" + a + "\" and \"" + b + "\"";
			}
		}
		return "int";
	}
	
	public String visit(TimesExpression n, String para) {
		String a = n.f0.accept(this, null);
		n.f1.accept(this, null);
		String b = n.f2.accept(this, null);
		
		if (a != "int" || b != "int"){
			if (errorMessage == null){
				errorMessage = "Error: \"*\" expression requires two ints. The expression resulted in \"" + a + "\" and \"" + b + "\"";
			}
		}
		return "int";
	}
	
	public String visit(ArrayLookup n, String para) {
		String a = n.f0.accept(this, null);
		n.f1.accept(this, null);
		String b = n.f2.accept(this, null);
		n.f3.accept(this, null);
		
		if (a != "int[]" || b != "int"){
			if (errorMessage == null){
				errorMessage = "Error: \"[]\" expression requires int[] and int. The expression resulted in \"" + a + "\" and \"" + b + "\"";
			}
		}
		return "int";
	}
	
   public String visit(ArrayLength n, String para) {
		String a = n.f0.accept(this, null);
		n.f1.accept(this, null);
		n.f2.accept(this, null);

		if (a != "int[]"){
			if (errorMessage == null){
				errorMessage = "Error: \".length\" expression requires int[]. The expression resulted in \"" + a + "\"";
			}
		}
		return "int";
	}
	
	
	public String visit(MessageSend n, String para) {
		String classType = n.f0.accept(this, null);
		ClassEnvironment cenv = environment.getClass(classType);
		if (cenv == null){
			if (errorMessage == null){
				errorMessage = "Error: Attemping to call function on \"" + classType + "\"";
			}
			// normally I don't escape early, but there's nothing i can do here
			return null;
		}
		
		n.f1.accept(this, null);
		String methodId = n.f2.accept(this, null);
		MethodEnvironment menv = cenv.getMethod(methodId);
		if (menv == null){
			if (errorMessage == null){
				errorMessage = "Error: Function \"" + methodId + "\" does not exist on \"" + classType + "\"";
			}
			// normally I don't escape early, but there's nothing i can do here
			return null;
		}
		n.f3.accept(this, null);
		parameterTypes.clear();
		n.f4.accept(this, null);
		n.f5.accept(this, null);
		
		// compares
		if (!menv.compare(environment, parameterTypes.toArray(new String[0]))){
			if (errorMessage == null){
				errorMessage = "Error: Function \"" + methodId + "\" has the wrong parameters";
			}
		}
		
		return menv.returnType;
	}
	
	public String visit(ExpressionList n, String para) {
		parameterTypes.add(n.f0.accept(this, null));
		n.f1.accept(this, null);
		return null;
	}
	
	public String visit(ExpressionRest n, String para) {
		n.f0.accept(this, null);
		parameterTypes.add(n.f1.accept(this, null));
		return null;
	}
	
	public String visit(PrimaryExpression n, String para) {
		return n.f0.accept(this, "");
	}
	
	public String visit(IntegerLiteral n, String para) {
		return "int";
	}
	
	public String visit(TrueLiteral n, String para) {
		return "bool";
	}
	
	public String visit(FalseLiteral n, String para) {
		return "bool";
	}
	
	public String visit(ThisExpression n, String para) {
		return classEnvironment.identifier;
	}
	
	public String visit(ArrayAllocationExpression n, String para) {
		n.f0.accept(this, null);
		n.f1.accept(this, null);
		n.f2.accept(this, null);
		String result = n.f3.accept(this, null);
		n.f4.accept(this, null);
		
		if (result != "int"){
			if (errorMessage == null){
				errorMessage = "Error: Allocating an array requires int. The expression resulted in \"" + result + "\"";
			}
		}
		return "int[]";
	}
	
	public String visit(AllocationExpression n, String para) {
		n.f0.accept(this, null);
		String id = n.f1.accept(this, null);
		n.f2.accept(this, null);
		n.f3.accept(this, null);
		
		return id;
   }
	
	public String visit(NotExpression n, String para) {
		n.f0.accept(this, null);
		String result = n.f1.accept(this, null);
		
		if (result != "bool"){
			if (errorMessage == null){
				errorMessage = "Error: Not requires bool. The expression resulted in \"" + result + "\"";
			}
		}
		return "bool";
   }
   
   public String visit(BracketExpression n, String para) {
		n.f0.accept(this, null);
		String result = n.f1.accept(this, null);
		n.f2.accept(this, null);
		
		return result;
   }
	
}
