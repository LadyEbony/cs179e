package typevisitor;
import syntaxtree.*;
import visitor.*;
import java.util.*;

public class Interval {
	private String intervalString;
	private int startInterval;
	private int endInterval;

	public Interval(String s, int a, int b) {
		intervalString = s;
		startInterval = a;
		endInterval = b;
	}

	public String getString() {
		return intervalString;
	}

	public int getStart() {
		return startInterval;
	}

	public int getEnd() {
		return endInterval;
	}

	public void setStart(int a) {
		startInterval = a;
	}

	public void setEnd(int a) {
		endInterval = a;
	}
	
}