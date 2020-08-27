import cs132.vapor.ast.*;
import java.util.*;

public class VaporConvertor {
	
	int depth = 0;
	
	private void addContentLine(StringBuilder content, String item){
		for(int i = 0; i < depth; ++i){
			content.append('\t');
		}
		content.append(item);
		content.append('\n');
	}
	
	public String GetHeader(VaporProgram program){
		StringBuilder content = new StringBuilder();
		depth = 0;
		
		for(VDataSegment data: program.dataSegments){
			addContentLine(content, "const " + data.ident);
			depth += 1;
			for(VOperand.Static v: data.values){
				VLabelRef item = (VLabelRef)v;
				addContentLine(content, ":" + item.ident);
			}
			depth -= 1;
			addContentLine(content, "");
		}
		return content.toString(); 
	}
	
	public String GetFunction(VFunction function, Hashtable<String, String> map){
		VaporVInstr vvi = new VaporVInstr();
		StringBuilder content = new StringBuilder();
		VInstr[] body = function.body;
		VVarRef.Local[] params = function.params;
		Hashtable<Integer, String> labels = new Hashtable<Integer, String>();
		
		int depth = 0;
		
		// get labels
		for(VCodeLabel label: function.labels){
			labels.put(label.instrIndex, label.ident);
		}
		
		// calculate $s count
		Set<String> sregisters = new HashSet<String>();
		Enumeration e = map.elements();
		while(e.hasMoreElements()){
			String item = (String)e.nextElement();
			if (item.charAt(1) == 's')
				sregisters.add(item);
		}
		
		int local = sregisters.size();
		int in = Math.max(0, params.length - 4);
		int out = 0;
		
		depth += 1;
		
		// add $s saving
		for(int i = 0; i < local; ++i){
			addContentLine(content, "local[" + i + "] = $s" + i);
		}
		
		// add parameters
		for(int i = 0; i < params.length; ++i){
			String dest = map.get(params[i].ident);
			if (dest == null) continue;
			
			String source;
			if (i < 4){
				source = "$a" + i; 
			} else {
				source = "in[" + (i - 4) + "]";
			}
			addContentLine(content, dest + " = " + source);
		}
		
		// Add function contents
		for(int i = 0; i < body.length; ++i){
			if (labels.containsKey(i)){
				depth -= 1;
				addContentLine(content, ":" + labels.get(i));
				depth += 1;
			}
			
			VInstr node = body[i];
			VVIStruct item = node.accept(map, vvi);
			if (item != null){
				addContentLine(content, item.content);
			
				out = Math.max(out, item.parameters);
			}
		}
		
		// restore $s saving
		for(int i = 0; i < local; ++i){
			addContentLine(content, "$s" + i + " = local[" + i + "]");
		}
		addContentLine(content, "ret");
		
		String header = "func " + function.ident + "[in " + in + ", out " + out + ", local " + local + "]\n";
		content.insert(0, header);
		
		return content.toString();
	}
	
	public class VVIStruct{
		public String content;
		public int parameters;
		
		public VVIStruct(String c){
			content = c;
		}
		
		public VVIStruct(String c, int p){
			content = c;
			parameters = p;
		}
	}
	
	public class VaporVInstr extends VInstr.VisitorPR<Hashtable<String, String>, VVIStruct, RuntimeException> {
	
		public VVIStruct visit(Hashtable<String, String> map, VAssign a) throws RuntimeException{
			String dest = map.get(a.dest.toString());
			String source = a.source.toString();
			if (a.source instanceof VVarRef.Local){
				source = map.get(source);
			}
			
			return new VVIStruct(dest + " = " + source);
		}
		
		public VVIStruct visit(Hashtable<String, String> map, VCall a) throws RuntimeException{
			StringBuilder content = new StringBuilder();

			VOperand[] params = a.args;
			for(int i = 0; i < params.length; ++i){
				VOperand p = params[i];
				String dest = p.toString();
				if (p instanceof VVarRef.Local){
					dest = map.get(dest);
				}

				String source;
				if (i < 4){
					source = "$a" + i; 
				} else {
					source = "out[" + (i - 4) + "]";
				}
				addContentLine(content, source + " = " + dest);
			}
			
			String func = map.get(a.addr.toString());
			addContentLine(content, "call " + func);
			
			String ret = map.get(a.dest.toString());
			if (ret != null){
				addContentLine(content, ret + " = $v0");
			}
			
			String result = content.toString();
			return new VVIStruct(result.substring(0, result.length() - 1), params.length - 4);
		}
		
		public VVIStruct visit(Hashtable<String, String> map, VBuiltIn a) throws RuntimeException{
			String op = a.op.name;
			
			VOperand[] params = a.args;
			String[] paramStrings = new String[params.length];
			for(int i = 0; i < params.length; ++i){
				VOperand p = params[i];
				String item = p.toString();
				if (p instanceof VVarRef.Local){
					item = map.get(item);
				}
				paramStrings[i] = item;
			}
			
			String result = "";
			if (a.dest != null){
				result += map.get(a.dest.toString()) + " = ";
			}
			result += op + "(" + String.join(" ", paramStrings) +  ")";
			
			return new VVIStruct(result);
		}
		
		public VVIStruct visit(Hashtable<String, String> map, VMemWrite a) throws RuntimeException{
			VMemRef.Global dest = (VMemRef.Global)a.dest;
			String destName = map.get(dest.base.toString());
			int destOffset = dest.byteOffset;
			
			String source = a.source.toString();
			if (a.source instanceof VVarRef.Local){
				source = map.get(source);
			}
			
			String result = "[" + destName;
			if (destOffset > 0){
				result += " + " + destOffset;
			}
			result += "] = " + source;
			
			return new VVIStruct(result);
		}
		
		public VVIStruct visit(Hashtable<String, String> map, VMemRead a) throws RuntimeException{
			VMemRef.Global source = (VMemRef.Global)a.source;
			String sourceName = map.get(source.base.toString());
			int sourceOffset = source.byteOffset;
			
			String dest = a.dest.toString();
			if (a.dest instanceof VVarRef.Local){
				dest = map.get(dest);
			}
			
			String result = dest + " = [" + sourceName;
			if (sourceOffset > 0){
				result += " + " + sourceOffset;
			}
			result += "]";
			
			return new VVIStruct(result);
		}
		
		public VVIStruct visit(Hashtable<String, String> map, VBranch a) throws RuntimeException{
			String value = a.value.toString();
			if (a.value instanceof VVarRef.Local){
				value = map.get(value);
			}
			
			boolean pos = a.positive;
			String label = a.target.ident;
			return new VVIStruct("if" + (pos ? " " : "0 ") + value + " goto :" + label);
		}
		
		public VVIStruct visit(Hashtable<String, String> map, VGoto a) throws RuntimeException{
			VAddr.Label s = (VAddr.Label)a.target;
			return new VVIStruct("goto :" + s.label.ident);
		}
		
		public VVIStruct visit(Hashtable<String, String> map, VReturn a) throws RuntimeException{
			if (a.value == null) return null;
			String result = a.value.toString();
			if (a.value instanceof VVarRef.Local){
				result = map.get(result);
			}
			return new VVIStruct("$v0 = " + result);
		}
	}
	
}