import cs132.vapor.ast.*;
import java.util.*;

class VaporConverter{
	private String finalString = "";

	public void conversion(VFunction function, Hashtable<String, String> regMap) {
		VaporVInstr vvi = new VaporVInstr();
		VInstr[] body = function.body;
		for(int line = 0; line < body.length; ++line) {
			VInstr node = body[line];
			if(node.accept(regMap, vvi) == null) {
				finalString = finalString + "\n" + node.toString();
			}
			else {
				finalString = finalString + "\n" + node.accept(regMap, vvi);
			}
		}
		System.out.println(finalString);
	}
}