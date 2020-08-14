import java.util.*;

public class IntervalLinearSearch {

	boolean[] sPool;
	boolean[] tPool;

	List<Interval> active;
	Hashtable<String, String> map;
	
	public void linearScanRegisterAllocation(List<Interval> intervals) { //somehow need to pass in a List of generated intervals
		// booleans 
		sPool = new boolean[8];
		tPool = new boolean[8];

		active = new LinkedList<Interval>();
		map = new Hashtable<String, String>();
		
		// intervals is automatically in start point order
		for(Interval i: intervals){
			// ignore variables that's never used
			if (i.start == i.end) continue;
			
			expireOldInterval(i);
			
			if(active.size() == 16) { //we have 23 registers
				System.out.println("we need to spill");
				//spillAtInterval(iter); //once all 23 occupied we spill
			} else {
				char r;
				int rindex;
				if (i.aliveAfterFunction){
					r = 's';
					rindex = 0;
					while(sPool[rindex]) rindex++;
					sPool[rindex] = true;
				} else {
					r = 't';
					rindex = 0;
					while(tPool[rindex]) rindex++;
					tPool[rindex] = true;
				}
				
				i.register = r;
				i.registerIndex = rindex;
				active.add(i);
				
				System.out.println(i.var + ": " + r + rindex);
			}
		}
		
	}

	public void expireOldInterval(Interval cur) {
		Collections.sort(active, new SortByEnd());
		
		Iterator<Interval> next = active.iterator();
		while(next.hasNext()){
			Interval i = next.next();
			if(i.endValue() >= cur.startValue()) { //if the next interval's end is greater than our start, means overlap
				return;
			}
			
			// free pool index
			char register = i.register;
			if (register == 't'){
				tPool[i.registerIndex] = false;
			} else if (register == 's'){
				sPool[i.registerIndex] = false;
			} else {
				System.out.println("BIG ERROR");
			}
			System.out.println("Freeing " + i.register + i.registerIndex);
			System.out.println("Freeing " + i.register + i.registerIndex + "(" + i.endValue() + " < " + cur.startValue() + ")");
			
			next.remove();
		}
	}

	/*
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
	*/

	class SortByEnd implements Comparator<Interval>{
		public int compare(Interval a, Interval b){
			return a.end - b.end;
		}
	}
	
}