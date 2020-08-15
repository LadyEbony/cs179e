
public class Interval {
	public String var;
	public int start;
	public int end;
	
	public char register;
	public int registerIndex;
	public boolean calleeSaved;

	public Interval(String s, int a, int b) {
		var = s;
		start = a;
		end = b;
	}

}