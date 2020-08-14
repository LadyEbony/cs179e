
public class Interval {
	public String var;
	public char register;
	public int registerIndex;
	
	public int start;
	public int end;
	public int bonus;
	
	public boolean aliveAfterFunction;

	public Interval(String s, int a, int b) {
		var = s;
		register = 0;
		registerIndex = 0;
		
		start = a;
		end = b;
		
		aliveAfterFunction = false;
	}
	
	public int startValue(){
		return start * 2 + bonus;
	}
	
	public int endValue(){
		return end * 2;
	}
	
}