import cs132.util.ProblemException;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.VaporProgram;
import cs132.vapor.ast.VBuiltIn.Op;

import cs132.vapor.ast.VFunction;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;

import java.util.*;

public class V2VM {
	public static void main(String[] args) {
		try{
			VaporProgram program = parseVapor(System.in, System.err);
			
			IntervalInitializer ii = new IntervalInitializer();
			IntervalLinearSearch ils = new IntervalLinearSearch();
			
			for(VFunction func: program.functions){
				
				List<Interval> intervals = ii.Initialize(func);
				//System.out.println("Function: " + func.index);
				//for(Interval i: intervals){
				//	System.out.println(i.var + " [" + i.start + "-" + i.end + "], " + Boolean.toString(i.calleeSaved));
				//}
				//System.out.println("-------------------");
				
				ils.linearScanRegisterAllocation(intervals);
				//System.out.println();
				//System.out.println();
				//System.out.println();
			}
			
		} catch (IOException ex) {
			System.out.println(ex.toString());
		}
	}
   
	public static VaporProgram parseVapor(InputStream in, PrintStream err) throws IOException {
		Op[] ops = {
			Op.Add, Op.Sub, Op.MulS, Op.Eq, Op.Lt, Op.LtS,
			Op.PrintIntS, Op.HeapAllocZ, Op.Error,
		};
		boolean allowLocals = true;
		String[] registers = null;
		boolean allowStack = false;

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