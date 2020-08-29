import cs132.util.ProblemException;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.VaporProgram;
import cs132.vapor.ast.VBuiltIn.Op;

import cs132.vapor.ast.VFunction;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.String;

import java.util.*;

public class VM2M {
	public static void main(String[] args) {
		try{
			VaporProgram program = parseVapor(System.in, System.err);
			VaporMConverter vmc = new VaporMConverter();
			String a = "";
			String az = "";
			int dotIndex = 0;
			String indent = "  ";
			String newline = "\n";


			for(VFunction fx: program.functions) { //prints Fac.ComputeFac
				a = vmc.GetHeader(fx);
				if(!a.equals("")) {
					dotIndex = a.indexOf(".");
					az = a.substring(0, dotIndex);
				}
			}
			//System.out.println(az);

		
			//String a = vmc.GetHeader(fx);
			
			System.out.println(".data\n");
			
			String b = "vmt_" + az;
			System.out.println(b);


			for(VFunction fx: program.functions) {
				a = vmc.GetHeader(fx);
				if(!a.equals("")) {
					System.out.println(indent + a);
				}
			}

			System.out.println("\n" + ".text\n");


			for(VFunction func: program.functions){
				String content = vmc.GetFunction(func);
				System.out.println(content);
			}
			
			String ext = vmc.GetExtensionFunctions();
			System.out.println(ext);
			
		} catch (IOException ex) {
			System.out.println(ex.toString());
		}
	}
   
	public static VaporProgram parseVapor(InputStream in, PrintStream err) throws IOException {
		Op[] ops = {
			Op.Add, Op.Sub, Op.MulS, Op.Eq, Op.Lt, Op.LtS,
			Op.PrintIntS, Op.HeapAllocZ, Op.Error,
		};
		boolean allowLocals = false;
		String[] registers = {
			"v0", "v1",
			"a0", "a1", "a2", "a3",
			"t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7",
			"s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7",
			"t8",
		};
		boolean allowStack = true;

		VaporProgram tree;
		try {
			tree = VaporParser.run(
				new InputStreamReader(in), 1, 1,
				java.util.Arrays.asList(ops),
				allowLocals, registers, allowStack
			);
		}
		catch (ProblemException ex) {
			err.println(ex.getMessage());
			return null;
		}

		return tree;
	}
   
}