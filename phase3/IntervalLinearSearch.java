import java.util.*;

public class IntervalLinearSearch {

	String[] registers = new String[] { "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7", "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7" };

	List<String> registerPool;
	List<Interval> active;
	Hashtable<String, String> map;
	
	public Hashtable<String, String> linearScanRegisterAllocation(List<Interval> intervals) { //somehow need to pass in a List of generated intervals
		// booleans 
		registerPool = new ArrayList<String>((Arrays.asList(registers)));
		active = new LinkedList<Interval>();
		map = new Hashtable<String, String>();
		
		Collections.sort(intervals, new SortByStart());
		for(Interval i: intervals){
			expireOldInterval(i);
			
			if(registerPool.size() == 0) { 
				spillAtInterval(i); //once all 23 occupied we spill
			} else {
				String register = getRegister(i.calleeSaved);
				
				i.register = register;
				active.add(i);
				map.put(i.var, "$" + register);
			}
		}
		
		return map;
	}

	public void expireOldInterval(Interval cur) {
		Collections.sort(active, new SortByEnd());
		
		Iterator<Interval> next = active.iterator();
		while(next.hasNext()){
			Interval i = next.next();
			if(i.end >= cur.start) { //if the next interval's end is greater than our start, means overlap
				return;
			}
			
			// free pool index
			freeRegister(i.register);
			next.remove();
		}
	}
	
	public void spillAtInterval(Interval cur){
		System.out.println("TODO: Add spill");
	}

	private String getRegister(boolean callee){
		if (callee){
			for(int i = 0; i < registerPool.size(); ++i){
				if (registerPool.get(i).charAt(0) == 's'){
					return registerPool.remove(i);
				}
			}
			System.out.println("Error ran out of s registers");
		}
		return registerPool.remove(0);
	}
	
	private void freeRegister(String register){
		registerPool.add(register);
		Collections.sort(registerPool, new SortRegister());
	}
	

	class SortByStart implements Comparator<Interval>{
		public int compare(Interval a, Interval b){
			return a.start - b.start;
		}
	}
	
	class SortByEnd implements Comparator<Interval>{
		public int compare(Interval a, Interval b){
			return a.end - b.end;
		}
	}
	
	class SortRegister implements Comparator<String>{
		public int compare(String a, String b){
			char ac = a.charAt(0);
			char bc = b.charAt(0);
			if (ac == bc){
				return a.charAt(1) - b.charAt(1);
			}
			if (ac == 's')
				return 1;
			if (ac == 't')
				return -1;
			return 0;
		}
	}
	
}