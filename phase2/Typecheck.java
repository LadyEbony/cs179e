/* Generated By:JavaCC: Do not edit this line. Typecheck.java */
import java.io.*;
import visitor.*;
import syntaxtree.*;
import typevisitor.*;
import java.util.Vector;

public class Typecheck {
   public static void main(String[] args) throws ParseException {

      InputStreamReader r = new InputStreamReader(System.in);
      MiniJavaParser parser = new MiniJavaParser(r);
      Goal g = parser.Goal();
      ProgramEnvironment environment = new ProgramEnvironment();
      String error;
      ProgramSetup ps = new ProgramSetup(environment);
      ps.visit(g);
      error = ps.getError();
      if ( error != null )
      {
         System.out.println("Type error");
	System.exit(1);
         return;
      }
      ProgramInitializer pi = new ProgramInitializer(environment);
      pi.visit(g,null);
      error = pi.getError();
      if ( error != null )
      {
         System.out.println("Type error");
	System.exit(1);
         return;
      }
      ProgramVerifier pv = new ProgramVerifier(environment);
      pv.verify();
      error = pv.getError();
      if ( error != null )
      {
         System.out.println("Type error");
	System.exit(1);
         return;
      }
      ProgramTypeCheck ptc = new ProgramTypeCheck(environment);
      ptc.visit(g,null);
      error = ptc.getError();
      if ( error != null )
      {
         System.out.println("Type error");
	System.exit(1);
         return;
      }
      System.out.println("Program type checked successfully");
	  
	  try{
		File file = new File("test.vapor");  
		file.delete();
		file.createNewFile();
		
		FileWriter fileWriter = new FileWriter("test.vapor");
		
		VaporSetup vs = new VaporSetup(environment);
		fileWriter.write(vs.initialize());
		
		VaporInitializer vi = new VaporInitializer(environment);
		fileWriter.write(vi.visit(g, null).identifier);
		
		fileWriter.close();
		
	  } catch (IOException e){
		  System.out.println("Fucking error");
	  }

      System.out.println("Vapor created successfully");
   }
}
