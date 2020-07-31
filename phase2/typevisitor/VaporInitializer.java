package typevisitor;
import syntaxtree.*;
import visitor.*;
import java.util.*;

public class VaporInitializer extends GJDepthFirst<VaporReturnStruct, String>{
	
	ProgramEnvironment environment;
	ClassEnvironment classEnvironment;
	MethodEnvironment methodEnvironment;

    String parameters = "";

	int depth;
	int var;

    int label;
	
	String content;
	
	public VaporInitializer(ProgramEnvironment env){
		environment = env;

		depth = 0;
	}
	
	// Use this function to add a new line with proper tab 
	public void addContentLine(String c){
		for(int i = 0; i < depth; ++i) content += "  ";
		content += c + "\n";
	}
	
	public VaporReturnStruct visit(Goal n, String para){
		content = "";
		super.visit(n, null);
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
        label = 0;
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
        label = 0;
		n.f0.accept(this, null);
		classEnvironment = environment.getClass(n.f1.accept(this, null).identifier);
		
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
        label = 0;
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

	/**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
	public VaporReturnStruct visit(VarDeclaration n, String para) {
        //String varid = "t." + var;
		n.f0.accept(this, null);
		n.f1.accept(this, null);
        //addContentLine(varid + " = ");
		n.f2.accept(this, null);
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
		String methodId = n.f2.accept(this, null).identifier;
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
        label = 0;
	
		n.f3.accept(this, null);
		n.f4.accept(this, null);
		n.f5.accept(this, null);
		n.f6.accept(this, null);
		n.f7.accept(this, null);
		n.f8.accept(this, null);
		n.f9.accept(this, null);
		String test = n.f10.accept(this, null).identifier;
		n.f11.accept(this, null);
		n.f12.accept(this, null);
        addContentLine("ret " + test);  
		depth -= 1;
        
		addContentLine("");
		
		return null;
	}
	/**
    * f0 -> FormalParameter()
    * f1 -> ( FormalParameterRest() )*
    */

	public VaporReturnStruct visit(FormalParameterList n, String para) {
		n.f0.accept(this, null);
		n.f1.accept(this, null);
        return null;
	}

	/**
    * f0 -> Type()
    * f1 -> Identifier()
    */

	  public VaporReturnStruct visit(FormalParameter n, String para) {
      n.f0.accept(this, null);
      n.f1.accept(this, null);
      return null;
   }

   /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */

   public VaporReturnStruct visit(FormalParameterRest n, String para) {
      n.f0.accept(this, null);
      n.f1.accept(this, null);
      return null;
   }

   /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
   public VaporReturnStruct visit(Type n, String para) {
      n.f0.accept(this, null);
      return null;
   }

   /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
   public VaporReturnStruct visit(ArrayType n, String para) {
      n.f0.accept(this, null);
      n.f1.accept(this, null);
      n.f2.accept(this, null);
      return null;
   }

   /**
    * f0 -> "boolean"
    */
   public VaporReturnStruct visit(BooleanType n, String para) {
      n.f0.accept(this, null);
      return null;
   }

   /**
    * f0 -> "int"
    */
   public VaporReturnStruct visit(IntegerType n, String para) {
 
      n.f0.accept(this, null);
      return null;
   }

   /**
    * f0 -> Block()
    *       | AssignmentStatement()
    *       | ArrayAssignmentStatement()
    *       | IfStatement()
    *       | WhileStatement()
    *       | PrintStatement()
    */
   public VaporReturnStruct visit(Statement n, String para) {
      //n.f0.accept(this, null);
      //addContentLine("hi");
      return n.f0.accept(this, "");
   }


    /**
    * f0 -> "{"
    * f1 -> ( Statement() )*
    * f2 -> "}"
    */
   public VaporReturnStruct visit(Block n, String para) {
      
      n.f0.accept(this, null);
      n.f1.accept(this, null);
      n.f2.accept(this, null);
      return null;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   public VaporReturnStruct visit(AssignmentStatement n, String para) {
      
      String test = n.f0.accept(this, null).identifier;
      n.f1.accept(this, null);
      String test2 = n.f2.accept(this, null).identifier;
      addContentLine(test + " = " + test2);
      n.f3.accept(this, null);
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
      
      n.f0.accept(this, null);
      n.f1.accept(this, null);
      n.f2.accept(this, null);
      n.f3.accept(this, null);
      n.f4.accept(this, null);
      n.f5.accept(this, null);
      n.f6.accept(this, null);
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
      String test = "if" + label + "_else";
      String test2 = "if" + label + "_end";
      label++;
      n.f0.accept(this, null);
      //String varid = "t." + var;
      n.f1.accept(this, null);
      String varid = n.f2.accept(this, null).identifier;
      addContentLine("if0 " + varid + " goto :" + test);
      //addContentLine()
      n.f3.accept(this, null);
      depth++;
      n.f4.accept(this, null);
      addContentLine("goto :" + test2);
      depth--;
      n.f5.accept(this, null);
      addContentLine(test + ":");
      depth++;
      n.f6.accept(this, null);
      depth--;
      addContentLine(test2 + ":");
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
      
      n.f0.accept(this, null);
      n.f1.accept(this, null);
      n.f2.accept(this, null);
      n.f3.accept(this, null);
      n.f4.accept(this, null);
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
      String test = n.f2.accept(this, null).identifier; //what does this call
      String varid = "t." + var++;
      n.f3.accept(this, null);
      n.f4.accept(this, null);
      //addContentLine(varid + " = " + test);
      addContentLine("PrintIntS(" + test + ")");

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
       //addContentLine("im here");
      return n.f0.accept(this, "");
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "&&"
    * f2 -> PrimaryExpression()
    */
   public VaporReturnStruct visit(AndExpression n, String para) {
      
      n.f0.accept(this, null);
      n.f1.accept(this, null);
      n.f2.accept(this, null);
      return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
   public VaporReturnStruct visit(CompareExpression n, String para) {
      //@@@
      String varid = "t." + var++;
      String test = n.f0.accept(this, null).identifier;
      n.f1.accept(this, null);
      String test2 = n.f2.accept(this, null).identifier;
      addContentLine(varid + " = LtS(" + test + " " + test2 + ")");
      return new VaporReturnStruct(varid, null);
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
   public VaporReturnStruct visit(PlusExpression n, String para) {
      
      n.f0.accept(this, null);
      n.f1.accept(this, null);
      n.f2.accept(this, null);
      return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
   public VaporReturnStruct visit(MinusExpression n, String para) {
      
      String test = n.f0.accept(this, null).identifier;
      n.f1.accept(this, null);
      String test2 = n.f2.accept(this, null).identifier;
      //addContentLine("Sub(" + test + " " + test2 + ")");
      return new VaporReturnStruct("Sub(" + test + " " + test2 + ")", null);
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
   public VaporReturnStruct visit(TimesExpression n, String para) {
      
      String test = n.f0.accept(this, null).identifier;
      n.f1.accept(this, null);
      //n.f2.accept(this, null);
      String test2 = n.f2.accept(this, null).identifier;
      return new VaporReturnStruct("MulS(" + test + " " + test2 + ")");
   }
   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
   public VaporReturnStruct visit(ArrayLookup n, String para) {
      
      n.f0.accept(this, null);
      n.f1.accept(this, null);
      n.f2.accept(this, null);
      n.f3.accept(this, null);
      return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
   public VaporReturnStruct visit(ArrayLength n, String para) {
      
      n.f0.accept(this, null);
      n.f1.accept(this, null);
      n.f2.accept(this, null);
      return null;
   }

   

   /**
    * f0 -> Expression()
    * f1 -> ( ExpressionRest() )*
    */
   public VaporReturnStruct visit(ExpressionList n, String para) {
        //@@@ 
    //try in accept of f0 and f1
    /*
      VaporReturnStruct a = n.f0.accept(this, null);
      String test2 = a.identifier;
      //String test2 = n.f0.accept(this, null).identifier;
      String test = "t." + var++;
      parameters = test2;
     
      n.f1.accept(this, null);

      if(a.checkLiteral == false) {
       return new VaporReturnStruct(a.identifier, classEnvironment, true, true);
	  }
      else {
        addContentLine(test + " = " + test2);
	  }
      
	  

      return new VaporReturnStruct(test, classEnvironment, true, true);
      */
      //System.out.println("Or am i here");
      VaporReturnStruct exp = n.f0.accept(this, null);
      //System.out.println(exp.identifier);
      String id;
      if(exp.checkLiteral == true) {
        
        id = exp.identifier;
        
	  }
      else {
        id = "t." + var++;
       addContentLine(id + " = " + exp.identifier);
       //addContentLine("hi");
       
	  }

      parameters += id;
      n.f1.accept(this, null);
      return null;
   }

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
   public VaporReturnStruct visit(ExpressionRest n, String para) {
      //System.out.println("Am i here");
      n.f0.accept(this, null);
      VaporReturnStruct a = n.f1.accept(this, null);
      String id;
      //String test2 = a.identifier;
      //String test2 = n.f1.accept(this, null).identifier;
      //String test = "t." + var++;
      if(a.checkLiteral == true) {
       id = a.identifier;
	  }
      else {
       id = "t." + var++;
       addContentLine(id + " = " + a.identifier);
	  }
      
      //addContentLine(test + " = " + test2);
      //parameters = parameters + " " + test2;
      parameters += id;
      //n.f1.accept(this, null);
      return null;

      //return new VaporReturnStruct(test, classEnvironment, true, false);
   }


   public VaporReturnStruct visit(IntegerLiteral n, String para) {
        return new VaporReturnStruct(n.f0.toString(), null, false, true);
   }

   /**
    * f0 -> "true"
    */
   public VaporReturnStruct visit(TrueLiteral n, String para) {
      
      //n.f0.accept(this, null);
      return new VaporReturnStruct(n.f0.toString(), null, false, true);
   }

   /**
    * f0 -> "false"
    */
   public VaporReturnStruct visit(FalseLiteral n, String para) {
      
      //n.f0.accept(this, null);
      return new VaporReturnStruct(n.f0.toString(), null, false, true);
   }

   /**
    * f0 -> "this"
    */
   public VaporReturnStruct visit(ThisExpression n, String para) {
      n.f0.accept(this, null);
      return new VaporReturnStruct("this", classEnvironment, false);
   }

   /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
   public VaporReturnStruct visit(ArrayAllocationExpression n, String para) {
      
      n.f0.accept(this, null);
      n.f1.accept(this, null);
      n.f2.accept(this, null);
      String test = n.f3.accept(this, null).identifier;
      n.f4.accept(this, null);
      return new VaporReturnStruct(test, null);
   }


   /**
    * f0 -> "!"
    * f1 -> Expression()
    */
   public VaporReturnStruct visit(NotExpression n, String para) {
      
      n.f0.accept(this, null);
      String test = n.f1.accept(this, null).identifier;
      return new VaporReturnStruct(test, null);
   }

   /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
   public VaporReturnStruct visit(BracketExpression n, String para) {

      //---------------------------------
      n.f0.accept(this, null);
      String test2 = n.f1.accept(this, null).identifier;
      String test = "t." + var++;
      addContentLine(test + " = " + test2);
      n.f2.accept(this, null);

      return new VaporReturnStruct(test, null);

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
		//@@@@
		// TODO: Do a null check
        parameters = "";
        String varid = "t." + var;
		VaporReturnStruct exp = n.f0.accept(this, null);
		// This null check exists until we implement all expression visits
		if (exp == null) return null;
        if (exp.checkNull == true) {
            addContentLine("if " + varid + " goto :null1");
            depth++;
            addContentLine("Error(\"null pointer\")");
            depth--;
            addContentLine("null1: ");
		}

		varid = "t." + var++;
		addContentLine(varid + " = [" + exp.identifier + "]");
	
		// apply function table offset
		n.f1.accept(this, null);
		String methodId = n.f2.accept(this, null).identifier;
        //System.out.println(methodId);
        //System.out.println(exp.type.toString());
		MethodEnvironment menv = exp.type.getMethod(methodId);
		addContentLine(varid + " = [" + varid + "+" + menv.offset + "]");
		
		n.f3.accept(this, null);
        parameters = exp.identifier + " ";
		VaporReturnStruct exp2 = n.f4.accept(this, null);

		n.f5.accept(this, null);
		
		// TODO: Use this identifier for the assignment visit
		return new VaporReturnStruct("call " + varid + "(" + parameters + ")", environment.getClass(menv.returnType));
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
		// The "" parameter makes the variable identifier Vapor compliant 
		return n.f0.accept(this, "");
        //return new VaporReturnStruct(n.f0.toString(), null);
	}
	
    /**
    * f0 -> <IDENTIFIER>
    */

	public VaporReturnStruct visit(Identifier n, String para){
		// Using string as our parameter is honestly very stupid
		// But java doesn't allow generic types???????
		// It has to be a reference type??????????
		// So String it will be cause lazy
		
		// Use null if you just want the identifier (for like function identifiers)
		// Use "" if you want the id converted for variable use
		
		String id = n.f0.toString();
		if (para != null){
			// method fields
			String fieldType = methodEnvironment.getFieldType(id);		
			if (fieldType != null){
				return new VaporReturnStruct(id, environment.getClass(fieldType));
			}
			
			// parameters
			String parameterType = methodEnvironment.getIdType(id);
			if (parameterType != null){
				return new VaporReturnStruct(id, environment.getClass(parameterType));
			}			
			
			// class fields
			FieldEnvironment field = classEnvironment.getField(id);
			if (field != null){
				id = "[this+" + field.offset + "]";
				return new VaporReturnStruct(id, classEnvironment);
			}
			
		}
		return new VaporReturnStruct(id, null);
	}
	
    /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
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