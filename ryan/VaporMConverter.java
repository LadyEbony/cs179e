import cs132.vapor.ast.*;
import java.util.*;

public class VaporMConverter{
	
	StringBuilder content;
	
	boolean _print;
	boolean _error;
	boolean _heapAlloc;
	
	boolean _newline;
	boolean _str0;
	
	
	private void addContentLine(String item, int depth){
		for(int i = 0; i < depth; ++i){
			content.append("  ");
		}
		content.append(item);
		content.append('\n');
	}

	public String GetHeader(VFunction function) {
		if ((!function.ident.equals("Main")) && (!function.ident.equals("AllocArray"))) {
			return function.ident;
		}
		else {
			return "";
		}
	}

	public String GetFunction(VFunction function){
		VisitorInstr visitor = new VisitorInstr();
		VInstr[] body = function.body;
		VFunction.Stack stack = function.stack;
		int frame = stack.local + stack.out + 2;
		
		Hashtable<Integer, List<String>> labels = new Hashtable<Integer, List<String>>();
		for(VCodeLabel label: function.labels){
			List<String> list;
			int i = label.instrIndex;
			if (labels.containsKey(i)){
				list = labels.get(i);
			} else {
				list = new ArrayList<String>();
				labels.put(i, list);
			}
			list.add(label.ident);
		}
		
		content = new StringBuilder();
		addContentLine(function.ident + ":", 0);
		addContentLine("sw $fp -8($sp)", 1);
		addContentLine("move $fp $sp", 1);
		addContentLine("subu $sp $sp " + (frame * 4), 1);
		addContentLine("sw $ra -4($fp)", 1);
		
		for(int i = 0; i < body.length; ++i){
			if (labels.containsKey(i)){
				List<String> list = labels.get(i);
				for(String l: list){
					addContentLine(l + ":", 0);
				}
			}
			
			VInstr node = body[i];
			node.accept(visitor);
		}
		
		addContentLine("lw $ra -4($fp)", 1);
		addContentLine("lw $fp -8($fp)", 1);
		addContentLine("addu $sp $sp " + (frame * 4), 1);
		addContentLine("jr $ra", 1);
		
		return content.toString();
	}
	
	public String GetExtensionFunctions(){
		content = new StringBuilder();
			
		if (_print){
			addContentLine("_print:", 0);
			addContentLine("li $v0 1", 1);
			addContentLine("syscall", 1);
			addContentLine("la $a0 _newline", 1);
			addContentLine("li $v0 4", 1);
			addContentLine("syscall", 1);
			addContentLine("ja $ra\n", 1);
		}
			
		if (_error){
			addContentLine("_error:", 0);
			addContentLine("li $v0 4", 1);
			addContentLine("syscall", 1);
			addContentLine("li $v0 10", 1);
			addContentLine("syscall\n", 1);
		}
			
		if (_heapAlloc){
			addContentLine("_heapAlloc:", 0);
			addContentLine("li $v0 9", 1);
			addContentLine("syscall", 1);
			addContentLine("jr $ra\n", 1);
		}
		
		addContentLine(".data", 0);
		addContentLine(".align 0", 0);
			
		if (_newline){
			addContentLine("_newline: .asciiz \"\\n\"", 0);
		}
			
		if (_str0){
			addContentLine("_str0: .asciiz \"null pointer\\n\"", 0);
		}
		
		return content.toString();
	}
	
	public class VisitorInstr extends VInstr.Visitor<RuntimeException> {
	
		public void visit(VAssign a) throws RuntimeException{
			String dest = a.dest.toString();
			String source = a.source.toString();

			if (a.source instanceof VVarRef.Register){
				addContentLine(String.format("move %s %s", dest, source), 1);
			} else {
				addContentLine(String.format("li %s %s", dest, source), 1);
			}
		}
		
		public void visit(VCall a) throws RuntimeException{
			addContentLine(String.format("jalr %s", a.addr.toString()), 1);
			return;
		}
		
		public void visit(VBuiltIn a) throws RuntimeException{
			VBuiltIn.Op op = a.op;
			VOperand[] args = a.args;
			
			switch(op.name){
				case "HeapAllocZ":
					addContentLine(String.format("li $a0 %s", args[0].toString()), 1);
					addContentLine("jal _heapAlloc", 1);
					addContentLine(String.format("move %s $v0", a.dest.toString()), 1);
					_heapAlloc = true;
					break;
				case "PrintIntS":
					addContentLine(String.format("move $a0 %s", args[0].toString()), 1);
					addContentLine("jal _print", 1);
					_print = true;
					_newline = true;
					break;
				case "Error":
					addContentLine("la $a0 _str0", 1);
					addContentLine("j _error", 1);
					_error = true;
					_str0 = true;
					break;
				case "LtS":
					addContentLine(String.format("slti %s %s %s", a.dest.toString(), args[0].toString(), args[1].toString()), 1);
					break;
				case "Sub":
					addContentLine(String.format("subu %s %s %s", a.dest.toString(), args[0].toString(), args[1].toString()), 1);
					break;
				case "MulS":
					addContentLine(String.format("mul %s %s %s", a.dest.toString(), args[0].toString(), args[1].toString()), 1);
					break;
				default:
				
					break;
			}
			
			return;
		}
		
		public void visit(VMemWrite a) throws RuntimeException{
			String op;
			String dest;
			String source;
			int offset;
			
			if (a.dest instanceof VMemRef.Global){
				VMemRef.Global d = (VMemRef.Global)a.dest;
				op = "la";
				dest = d.base.toString();
				offset = d.byteOffset;
			} else {
				VMemRef.Stack d = (VMemRef.Stack)a.dest;
				op = "li";
				dest = "$sp";
				offset = d.index * 4;
			}
			 
			if (a.source instanceof VLabelRef){
				VLabelRef s = (VLabelRef)a.source;
				source = "$t9";
				addContentLine(String.format("%s $t9 %s", op, s.ident), 1);
			} else {
				VVarRef.Register s = (VVarRef.Register)a.source;
				source = a.source.toString();
			}
			 
			addContentLine(String.format("sw %s %d(%s)", source, offset, dest), 1);
			 
			return;
		}
		
		public void visit(VMemRead a) throws RuntimeException{
			String dest = a.dest.toString();
			String source;
			int offset;
			
			if (a.source instanceof VMemRef.Global){
				VMemRef.Global s = (VMemRef.Global)a.source;
				source = s.base.toString();
				offset = s.byteOffset;
			} else {
				VMemRef.Stack d = (VMemRef.Stack)a.source;
				source = "$sp";
				offset = d.index * 4;
			}
				
			addContentLine(String.format("lw %s %d(%s)", dest, offset, source), 1);
			
			return;
		}
		
		public void visit(VBranch a) throws RuntimeException{
			String op = a.positive ? "bnez" : "beqz";
			String value = a.value.toString();
			String branch = a.target.ident;
			
			addContentLine(String.format("%s %s %s", op, value, branch), 1);
			return;
		}
		
		public void visit(VGoto a) throws RuntimeException{
			VAddr.Label target = (VAddr.Label)a.target;
			addContentLine(String.format("j %s", target.label.ident), 1);
			return;
		}
		
		public void visit(VReturn a) throws RuntimeException{
			return;
		}

	}
}