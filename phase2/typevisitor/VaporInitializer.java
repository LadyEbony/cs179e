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
    int iflabel;
	int whilelabel;
	int nulllabel;
	int boundlabel;
	int andlabel;
	
	String content;
	String parameters;
	
	boolean allocarrayfunc;
	
	public VaporInitializer(ProgramEnvironment env){
		environment = env;

		depth = 0;
	}
	
	// Use this function to add a new line with proper tab 
	public void addContentLine(String c){
		for(int i = 0; i < depth; ++i) content += "  ";
		content += c + "\n";
	}
	
	public void addContentNullLine(String c){
		String nlabel = "null" + nulllabel++; 
		addContentLine("if " + c + " goto :" + nlabel);
		depth++;
		addContentLine("Error(\"null pointer\")");
		depth--;
		addContentLine(nlabel + ":");
	}
	
	public void addContentBoundsLine(String array, String expTemp, String compareTemp){
		String blabel = "bounds" + boundlabel++;
		
		addContentLine(compareTemp + " = [" + array + "]");
		addContentLine(compareTemp + " = Lt(" + expTemp + " " + compareTemp + ")");
		
		addContentLine("if " + compareTemp + " goto :" + blabel);
		depth++;
		addContentLine("Error(\"array index out of bounds\")");
		depth--;
		addContentLine(blabel + ":");
		
		addContentLine(compareTemp + " = MulS(" + expTemp + " 4)");
		addContentLine(compareTemp + " = Add(" + compareTemp + " " + array + ")");
	}
	
	public VaporReturnStruct visit(Goal n, String para){
		content = "\n";
		
		iflabel = 1;
		whilelabel = 1;
		nulllabel = 1;
		boundlabel = 1;
		andlabel = 1;
		
		allocarrayfunc = false;
		
		super.visit(n, null);
		
		if (allocarrayfunc){
			addContentLine("func AllocArray(size)");
			depth++;
			addContentLine("bytes = MulS(size 4)");
			addContentLine("bytes = Add(bytes 4)");
			addContentLine("v = HeapAllocZ(bytes)");
			addContentLine("[v] = size");
			addContentLine("ret v");
			depth--;
		}
		
		return new VaporReturnStruct(content, null);
	}
	
	/**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> "public"
    * f4 -> "static"
    * f5 -> "void"
    * f6 -> "main"
    * f7 -> "("
    * f8 -> "String"
    * f9 -> "["
    * f10 -> "]"
    * f11 -> Identifier()
    * f12 -> ")"
    * f13 -> "{"
    * f14 -> ( VarDeclaration() )*
    * f15 -> ( Statement() )*
    * f16 -> "}"
    * f17 -> "}"
    */

	public VaporReturnStruct visit(MainClass n, String para) {
		addContentLine("func Main()");
        var = 0;
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
		addContentLine("ret");
		depth -= 1;
		addContentLine("");

        
		
		return null;
   }

	/**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
	public VaporReturnStruct visit(ClassDeclaration n, String para) {
		// getting class
        var = 0;
		n.f0.accept(this, null);
		classEnvironment = environment.getClass(n.f1.accept(this, "0").identifier);
		
		n.f2.accept(this, null);
		n.f3.accept(this, null);
		n.f4.accept(this, null);
		n.f5.accept(this, null);
		
		return null;
	}
   
   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "extends"
    * f3 -> Identifier()
    * f4 -> "{"
    * f5 -> ( VarDeclaration() )*
    * f6 -> ( MethodDeclaration() )*
    * f7 -> "}"
    */

   public VaporReturnStruct visit(ClassExtendsDeclaration n, String para) {
        var = 0;
	    // getting class
		n.f0.accept(this, null);
		classEnvironment = environment.getClass(n.f1.accept(this, "0").identifier);
		
		n.f2.accept(this, null);
		n.f3.accept(this, null);
		n.f4.accept(this, null);
		n.f5.accept(this, null);
		n.f6.accept(this, null);
		n.f7.accept(this, null);
		
		return null;
	}

	/**
    * f0 -> "public"
    * f1 -> Type()
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( FormalParameterList() )?
    * f5 -> ")"
    * f6 -> "{"
    * f7 -> ( VarDeclaration() )*
    * f8 -> ( Statement() )*
    * f9 -> "return"
    * f10 -> Expression()
    * f11 -> ";"
    * f12 -> "}"
    */

	public VaporReturnStruct visit(MethodDeclaration n, String para) {
		n.f0.accept(this, null);
		n.f1.accept(this, null);
		
		// Get method
		String methodId = n.f2.accept(this, "0").identifier;
		methodEnvironment = classEnvironment.getMethod(methodId);

		// Get method parameters
		List<String> parameters = new ArrayList<String>();
		parameters.add("this");
		for(String p: methodEnvironment.parameterIDs)
			parameters.add(p);
		
		// Add function header
		// Increase depth by 1 for tab
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
		String result = n.f10.accept(this, "2").identifier;
		n.f11.accept(this, null);
		n.f12.accept(this, null);
        addContentLine("ret " + result);  
		depth -= 1;
        
		addContentLine("");
		
		return null;
	}

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   public VaporReturnStruct visit(AssignmentStatement n, String para) {
      VaporReturnStruct id = n.f0.accept(this, "1");
      n.f1.accept(this, null);
	  
	  String m;
	  if (id.isTemporary)
		m = "1";
	  else
		m = "2";
	  
      String exp = n.f2.accept(this, m).identifier;
      n.f3.accept(this, null);
	  
	  addContentLine(id.identifier + " = " + exp);
	  
      return null;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "["
    * f2 -> Expression()
    * f3 -> "]"
    * f4 -> "="
    * f5 -> Expression()
    * f6 -> ";"
    */
   public VaporReturnStruct visit(ArrayAssignmentStatement n, String para) {
		String id = n.f0.accept(this, "2").identifier;
		String idTemp = "t." + var++;
		addContentLine(idTemp + " = " + id);
		addContentNullLine(idTemp);
		
		n.f1.accept(this, null);
		String index = n.f2.accept(this, "2").identifier;
		String compareTemp = "t." + var++;
		addContentBoundsLine(idTemp, index, compareTemp);
		
		n.f3.accept(this, null);
		n.f4.accept(this, null);
		String exp = n.f5.accept(this, "2").identifier;
		n.f6.accept(this, null);
		
		addContentLine("[" + compareTemp + "+4] = " + exp);
		
		return null;
   }


	/**
    * f0 -> "if"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    * f5 -> "else"
    * f6 -> Statement()
    */
   public VaporReturnStruct visit(IfStatement n, String para) {
      String elselabel = "if" + iflabel + "_else";
      String endlabel = "if" + iflabel + "_end";
      iflabel++;
	  
      n.f0.accept(this, null);
      n.f1.accept(this, null);
      String exp = n.f2.accept(this, "2").identifier;
      addContentLine("if0 " + exp + " goto :" + elselabel);
      n.f3.accept(this, null);
	  
	  // if statements
      depth++;
      n.f4.accept(this, null);
      addContentLine("goto :" + endlabel);
      depth--;
      n.f5.accept(this, null);
      addContentLine(elselabel + ":");
	  
	  // else statements
      depth++;
      n.f6.accept(this, null);
      depth--;
      addContentLine(endlabel + ":");
	  
      return null;

   }

   /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
   public VaporReturnStruct visit(WhileStatement n, String para) {
      String toplabel = "while" + whilelabel + "_top";
      String endlabel = "while" + whilelabel + "_end";
      whilelabel++;
	  
	  addContentLine(toplabel + ":");
	  
      n.f0.accept(this, null);
      n.f1.accept(this, null);
      String exp = n.f2.accept(this, "2").identifier;
      addContentLine("if0 " + exp + " goto :" + endlabel);
      n.f3.accept(this, null);
	  
	  // if statements
      depth++;
      n.f4.accept(this, null);
      addContentLine("goto :" + toplabel);
      depth--;
      addContentLine(endlabel + ":");

      return null;
   }

   /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
   public VaporReturnStruct visit(PrintStatement n, String para) {
	  n.f0.accept(this, null);
      n.f1.accept(this, null);
      String exp = n.f2.accept(this, "2").identifier;
      n.f3.accept(this, null);
      n.f4.accept(this, null);
      addContentLine("PrintIntS(" + exp + ")");
      return null;
   }

   /**
    * f0 -> AndExpression()
    *       | CompareExpression()
    *       | PlusExpression()
    *       | MinusExpression()
    *       | TimesExpression()
    *       | ArrayLookup()
    *       | ArrayLength()
    *       | MessageSend()
    *       | PrimaryExpression()
    */
   public VaporReturnStruct visit(Expression n, String para) {
		VaporReturnStruct exp = n.f0.accept(this, para);
		
		// return base form
		if (para != "2"){
			return exp;
		}
		// return converted form if not already
		if (exp.isTemporary){
			return exp;
		}
		
		String id = "t." + var++;
		addContentLine(id + " = " + exp.identifier);
		
		return new VaporReturnStruct(id, exp.type, exp.isNullable, true);
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "&&"
    * f2 -> PrimaryExpression()
    */
   public VaporReturnStruct visit(AndExpression n, String para) {
		String result = "t." + var++;
		String elselabel = "ss" + andlabel + "_else";
		String endlabel = "ss" + andlabel + "_end";
		andlabel++;
		
		String a = n.f0.accept(this, "2").identifier;
		n.f1.accept(this, null);
		
		addContentLine("if0 " + a + " goto :" + elselabel);
		depth++;
		
		String b = n.f2.accept(this, "1").identifier;
		addContentLine(result + " = " + b);
		addContentLine("goto :" + endlabel);
		depth--;
		addContentLine(elselabel + ":");
		depth++;
		addContentLine(result + " = 0");
		depth--;
		addContentLine(endlabel + ":");
		
      return new VaporReturnStruct(result, null, true, true);
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
   public VaporReturnStruct visit(CompareExpression n, String para) {
      String a = n.f0.accept(this, "2").identifier;
      n.f1.accept(this, null);
      String b = n.f2.accept(this, "2").identifier;
      return new VaporReturnStruct("LtS(" + a + " " + b + ")");
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
   public VaporReturnStruct visit(PlusExpression n, String para) {
      String a = n.f0.accept(this, "2").identifier;
      n.f1.accept(this, null);
      String b = n.f2.accept(this, "2").identifier;
      return new VaporReturnStruct("Add(" + a + " " + b + ")");
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
   public VaporReturnStruct visit(MinusExpression n, String para) {
      String a = n.f0.accept(this, "2").identifier;
      n.f1.accept(this, null);
      String b = n.f2.accept(this, "2").identifier;
      return new VaporReturnStruct("Sub(" + a + " " + b + ")");
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
   public VaporReturnStruct visit(TimesExpression n, String para) {
      String a = n.f0.accept(this, "2").identifier;
      n.f1.accept(this, null);
      String b = n.f2.accept(this, "2").identifier;
      return new VaporReturnStruct("MulS(" + a + " " + b + ")");
   }
   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
   public VaporReturnStruct visit(ArrayLookup n, String para) {
      String array = n.f0.accept(this, "2").identifier;
	  n.f1.accept(this, null);
	  String expTemp = n.f2.accept(this, "2").identifier;
      n.f3.accept(this, null);
	  
	  addContentNullLine(array);
	  String compareTemp = "t." + var++;
	  addContentBoundsLine(array, expTemp, compareTemp);

      return new VaporReturnStruct("[" + compareTemp + "+4]", null);
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
   public VaporReturnStruct visit(ArrayLength n, String para) {
		VaporReturnStruct exp = n.f0.accept(this, "2");
		n.f1.accept(this, null);
		n.f2.accept(this, null);
		return new VaporReturnStruct("[" + exp.identifier + "]", null, true, false);
   }

   

   /**
    * f0 -> Expression()
    * f1 -> ( ExpressionRest() )*
    */
   public VaporReturnStruct visit(ExpressionList n, String para) {
		parameters += " " + n.f0.accept(this, "2").identifier;
		n.f1.accept(this, null);
		return null;
   }

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
   public VaporReturnStruct visit(ExpressionRest n, String para) {
		parameters += " " + n.f1.accept(this, "2").identifier;
		return null;
   }
	
    /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */

	public VaporReturnStruct visit(MessageSend n, String para) {
		VaporReturnStruct exp = n.f0.accept(this, "2");
		// This null check exists until we implement all expression visits
		if (exp == null) return null;
        if (exp.isNullable == true) {
			addContentNullLine(exp.identifier);
		}

		String varid = "t." + var++;
		addContentLine(varid + " = [" + exp.identifier + "]");
	
		// apply function table offset
		n.f1.accept(this, null);
		String methodId = n.f2.accept(this, "0").identifier;
		MethodEnvironment menv = exp.type.getMethod(methodId);
		addContentLine(varid + " = [" + varid + "+" + menv.offset + "]");
		
		n.f3.accept(this, null);
        parameters = exp.identifier;
		n.f4.accept(this, null);

		n.f5.accept(this, null);
		
		return new VaporReturnStruct("call " + varid + "(" + parameters + ")", environment.getClass(menv.returnType), true, false);
	}
	

    /**
    * f0 -> IntegerLiteral()
    *       | TrueLiteral()
    *       | FalseLiteral()
    *       | Identifier()
    *       | ThisExpression()
    *       | ArrayAllocationExpression()
    *       | AllocationExpression()
    *       | NotExpression()
    *       | BracketExpression()
    */
	public VaporReturnStruct visit(PrimaryExpression n, String para) {
		VaporReturnStruct exp = n.f0.accept(this, para);
		
		// return base form
		if (para != "2"){
			return exp;
		}
		// return converted form if not already
		if (exp.isTemporary){
			return exp;
		}
		
		String id = "t." + var++;
		addContentLine(id + " = " + exp.identifier);
		
		return new VaporReturnStruct(id, exp.type, exp.isNullable, true);
	}
	
	public VaporReturnStruct visit(IntegerLiteral n, String para) {
        return new VaporReturnStruct(n.f0.toString(), null, false, true);
	}

	public VaporReturnStruct visit(TrueLiteral n, String para) {
		return new VaporReturnStruct("1", null, false, true);
	}

	public VaporReturnStruct visit(FalseLiteral n, String para) {
		return new VaporReturnStruct("0", null, false, true);
	}

	public VaporReturnStruct visit(ThisExpression n, String para) {
		return new VaporReturnStruct("this", classEnvironment, false, true);
	}
	
	public VaporReturnStruct visit(Identifier n, String para){
		// Using string as our parameter is honestly very stupid
		// But java doesn't allow generic types???????
		// It has to be a reference type??????????
		// So String it will be cause lazy
		
		// Use null if you just want the identifier (for like function identifiers)
		// Use "1" or "2" if you want the id converted for variable use
		
		String id = n.f0.toString();
		if (para == "1" || para == "2"){
			// method fields
			String fieldType = methodEnvironment.getFieldType(id);		
			if (fieldType != null){
				return new VaporReturnStruct(id, environment.getClass(fieldType), true, true);
			}
			
			// parameters
			String parameterType = methodEnvironment.getIdType(id);
			if (parameterType != null){
				return new VaporReturnStruct(id, environment.getClass(parameterType), true, true);
			}			
			
			// class fields
			FieldEnvironment field = classEnvironment.getField(id);
			if (field != null){
				return new VaporReturnStruct("[this+" + field.offset + "]", environment.getClass(field.type), true, false);
			}
			System.out.println("Couldn't find " + id);
		}
		
		return new VaporReturnStruct(id);
	}
	
   /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
	public VaporReturnStruct visit(ArrayAllocationExpression n, String para) {
		allocarrayfunc = true;
		
		n.f0.accept(this, null);
		n.f1.accept(this, null);
		n.f2.accept(this, null);
		String exp = n.f3.accept(this, "2").identifier;
		n.f4.accept(this, null);
		
		String id = "t." + var++;
		addContentLine(id + " = " + "call :AllocArray(" + exp + ")");
		
		return new VaporReturnStruct(id, null, true, true);
	}
	
    /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
	public VaporReturnStruct visit(AllocationExpression n, String para) {
		n.f0.accept(this, null);
		ClassEnvironment temp = environment.getClass(n.f1.accept(this, "0").identifier);
		n.f2.accept(this, null);
		n.f3.accept(this, null);

		String v = "t." + var++;
		addContentLine(v + " = HeapAllocZ(" + temp.size + ")");
		addContentLine("[" + v + "] = :vmt_" + temp.identifier); 
		return new VaporReturnStruct(v, temp, true, true);
   }
   
      /**
    * f0 -> "!"
    * f1 -> Expression()
    */
   public VaporReturnStruct visit(NotExpression n, String para) {
      n.f0.accept(this, null);
      String id = n.f1.accept(this, "2").identifier;
      return new VaporReturnStruct("Sub(1 " + id + ")");
   }

   /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
   public VaporReturnStruct visit(BracketExpression n, String para) {
		return n.f1.accept(this, para);
   }
	
}