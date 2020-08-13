package typevisitor;
import syntaxtree.*;
import visitor.*;
import java.util.*;

public class Allocator {
	LinkedList<String> s_Pool = new LinkedList<String>();
	LinkedList<String> t_Pool = new LinkedList<String>();

	LinkedList<String> memory = new LinkedList<String>();

	private List<Interval> active;
	private Map<String, Interval> testMap;

	generatePool(); //all regs free

	public void linearSearchAlloc(List<Interval> a) { //somehow need to pass in a List of generated intervals
		List<Interval> intervals = new ArrayList<Interval>(a);
		intervals.sort(Comparator.comparingInt(Interval::getStart)); 

		for(Iterator<Interval> iter = intervals.iterator(); iter.hasNext(); ) {
			expireOldInterval(iter);
			if(len(active) == 23) { //we have 23 registers
				spillAtInterval(iter); //once all 23 occupied we spill
			}
			else { //otherwise, "occupy" the reg
				active.add(iter);
				memory.push(s_Pool.getFirst());
				s_Pool.removeFirst();

				testMap.put(memory.getEnd(), iter.getString()); //going through which registers are occupied

				active.sort(Comparator.comparingInt(Interval::getEnd)); //sorts by endpoint
			}
		}
	}

	public void expireOldInterval(Interval curr) {
		active.sort(Comparator.comparingInt(Interval::getEnd)); 
		for(Iterator<Interval> iter = active.iterator(); iter.hasNext();) {
			String next = iter.next();
			if(next.getEnd() >= curr.getStart()) { //if the next interval's end is greater than our start, means overlap
				return;
			}
			Interval test = active.get(iter);
			active.remove(iter);
			registerPool[iter] =  test.getString(); //need to add to pool of free reg

		}
	}

	public void spillAtInterval(Interval curr) {
		Interval spill = null;
		active.sort(Comparator.comparingInt(Interval::getEnd)); 
		spill = active.get(active.size() - 1);
		if(spill.getEnd() > curr.getEnd()) {
			active.remove(spill);
			active.add(curr);
		}
		else {
			//doesn't matter for now
		}
	}

	public Map<String, Interval> getMap() { //store the map in some variable and then use map functions to grab values
		return testMap;
	}

	public void generatePool() {
		s_Pool.push("s0");
		s_Pool.push("s1");
		s_Pool.push("s2");
		s_Pool.push("s3");
		s_Pool.push("s4");
		s_Pool.push("s5");
		s_Pool.push("s6");
		s_Pool.push("s7");
		t_Pool.push("t0");
		t_Pool.push("t1");
		t_Pool.push("t2");
		t_Pool.push("t3");
		t_Pool.push("t4");
		t_Pool.push("t5");
		t_Pool.push("t6");
		t_Pool.push("t7");
		t_Pool.push("t8");
		if(!memory.isEmpty()) {
			while(!memory.isEmpty()) {
				memory.pop();
			}
		}
		return;
	}

}