package hk.hku.cecid.edi.sfrm.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import hk.hku.cecid.edi.sfrm.handler.MessageStatusQueryHandler;
import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.piazza.commons.module.ActiveTaskModule;

/**
 * @author Patrick Yip
 * To regulate the execution interval of the collector, to improve the performance and use as least resource as possible to reach 
 * the maximum speed
 */
public class BandWidthOptimizer{
	
	private int curRound = 0;
	private int maxRound = 20;
	
	private long eiInc = 500;
	private long minEI = 500;
	private long maxEI = 8000;
	private TreeMap<Long, List> intervalSpeedMap = new TreeMap<Long, List>();
	private TreeMap<Long, Integer> intervalRetriedMap = new TreeMap<Long, Integer>();
	private TreeMap<Long, Double> intervalAvgSpeedMap = new TreeMap<Long, Double>();
	
	private ActiveTaskModule collector; 
	private long curEI;
	private boolean initialized = false;
	
	//The tolerance when the speed will concerned as stable in each round of trial run 
	private double speedGradientTolerance = 100; 
	//The tolerance when the transition point will determine
	private double eiSpeedGradientTolerance = 50;
	
	private long optimizedEI = -1;
	private double optimizedSpeed = -1.0;
	
	private boolean foundOptimized = false;
	
	public BandWidthOptimizer(ActiveTaskModule collector){
		this.collector = collector;
	}
	
	public void setMaxRound(int maxRound){
		this.maxRound = maxRound;
	}
	
	public void setEIIncrement(long increment){
		this.eiInc = increment;
	}
	
	public void setMinExecutionInterval(long minEI){
		this.minEI = minEI;
	}
	
	public void setMaxExecutionInterval(long maxEI){
		this.maxEI = maxEI;
	}
	
	public void setSpeedGradientTolerance(double tolerance){
		this.speedGradientTolerance = tolerance;
	}
	
	public void setEISpeedGradientTolerance(double tolerance){
		this.eiSpeedGradientTolerance = tolerance;
	}
		
	/**
	 * Reset the bandwidth optimizer
	 */
	public void reset(){
		intervalSpeedMap.clear();
		intervalRetriedMap.clear();
		intervalAvgSpeedMap.clear();
		initialized = false;
		foundOptimized = false;
		optimizedSpeed = -1.0;
		optimizedEI = -1;
	}
	
	public boolean findMaxSpeed(){
		if(!initialized){
			curEI = minEI;
			initialized = true;
			
			//Initialize the interval retried map
			incrementIntervalRetriedMap(curEI, intervalRetriedMap);
		}
				
		//Mark the speed and interval
		double totalSpeed = getTotalSpeed();
		if(totalSpeed <= 0)
			return false;
		
		updateIntervalSpeed(intervalSpeedMap, curEI, totalSpeed);
		
		//Stop to increment the EI, since the max EI was reached
		//Then set the EI to the min EI to reach the maximum speed in the rest of transmition
		if(curEI >= maxEI){
			foundOptimized = true;
			optimizedEI = minEI;
			optimizedSpeed = intervalAvgSpeedMap.get(optimizedEI);
			collector.setExecutionInterval(minEI);
			return true;
		}
		
		if(curRound < maxRound - 1){
			curRound ++;
		}else{
			curRound = 0;
			if(validateSpeedGradient(curEI, intervalSpeedMap, speedGradientTolerance)){
				updateIntervalAvgSpeedMap(curEI, intervalSpeedMap, intervalAvgSpeedMap);
//				printAvgSpeedMap(intervalAvgSpeedMap);
				foundOptimized = determineOptimizedValue(intervalAvgSpeedMap);
				curEI += eiInc;
			}else{
				intervalSpeedMap.get(curEI).clear();
			}
			incrementIntervalRetriedMap(curEI, intervalRetriedMap);
		}
		
		collector.setExecutionInterval(curEI);
		
		return foundOptimized;
	}
	
	private void incrementIntervalRetriedMap(long EI, TreeMap<Long, Integer> retriedMap){
		//If exist, increment it
		if(retriedMap.containsKey(EI)){
			Integer retried = 0;
			retried = retriedMap.get(EI);
			retried++;
			retriedMap.put(EI, retried);
		//If not exist, initialize it
		}else{
			retriedMap.put(EI, 1);
		}
	}
	
	/**
	 * Validate whether this round of the test is valid by examine the gradient of this round of test
	 * On the other hand it is test whether the speed was stabilized for the given EI to the network speed
	 * @param ei
	 * @param intervalSpeed
	 * @param tolerance
	 * @return whether the current EI is valid (stabilized)
	 */
	private boolean validateSpeedGradient(long ei, TreeMap<Long, List> intervalSpeed, double tolerance){
		List<Double> speedList = intervalSpeed.get(ei);
		//Use the head tail gradient approach
		double gradient = Math.abs(speedList.get(speedList.size() - 1) - speedList.get(0));
		
		//Use the average gradient approach
//		double totalGradient = 0.0;
//		for(int i=1; speedList.size() > i;i++){
//			totalGradient += Math.abs(speedList.get(i) - speedList.get(i-1));
//		}
//		double gradient = totalGradient/(speedList.size()-1);
		
		
		if(tolerance >= gradient){
			return true;
		}else{
			return false;
		}
	}
	
	private void updateIntervalSpeed(TreeMap<Long, List> eISpeed, long ei, double totalSpeed){
		if(!eISpeed.containsKey(ei)){
			List<Double> speedList = new ArrayList<Double>();
			speedList.add(totalSpeed);
			eISpeed.put(ei, speedList);
		}else{
			List<Double> speedList = eISpeed.get(ei);
			speedList.add(totalSpeed);
		}
	}
	
	private double getTotalSpeed(){
		MessageStatusQueryHandler statusQueryHandler = SFRMProcessor.getInstance().getMessageSpeedQueryHandler();
		Iterator<String> msgIter = statusQueryHandler.getMessageList();
		double totalSpeed = 0.0;
		int counter = 0;
		while(msgIter.hasNext()){
			String msgID = msgIter.next();
			StatusQuery query = statusQueryHandler.getMessageSpeedQuery(msgID);
			totalSpeed += query.getCurrentSpeed();
			counter++;
		}
		if(counter == 0)
			return -1;
		
		return totalSpeed;
	}
		
	public void printIntervalSpeedMap(){
		Set eiSet = intervalSpeedMap.keySet();
		Iterator<Long> eiIter = eiSet.iterator();
				
		SFRMProcessor.getInstance().getLogger().debug(",SegISList,EI,Speeds,");
		while(eiIter.hasNext()){
			Long ei = eiIter.next();
			List speedList = intervalSpeedMap.get(ei);
			String speedStr = "";
			for(int i=0; speedList.size() > i;i++){
				speedStr += speedList.get(i) + ",";
			}
			SFRMProcessor.getInstance().getLogger().debug(",SegISList," + ei + "," + speedStr + ",");
		}
	}
	
	public void printIntervalRetriedMap(){
		Set eiSet = intervalRetriedMap.keySet();
		Iterator<Long> eiIter = eiSet.iterator();
		
		SFRMProcessor.getInstance().getLogger().debug(",IntervalRetried,EI,Retried,");
		while(eiIter.hasNext()){
			Long ei = eiIter.next();
			int retried = intervalRetriedMap.get(ei);
			SFRMProcessor.getInstance().getLogger().debug(",IntervalRetried," + ei + "," + retried);
		}
	}
	
	private void updateIntervalAvgSpeedMap(long ei, TreeMap<Long, List> intervalSpeedMap, TreeMap<Long, Double> intervalAvgSpeedMap){
		if(!intervalSpeedMap.containsKey(ei)){
			return;
		}
		List<Double> speeds = intervalSpeedMap.get(ei);
		double totalSpeed = 0.0;
		for(int i=0; speeds.size()>i;i++){
			totalSpeed += speeds.get(i);
		}
		double avgSpeed = totalSpeed / speeds.size();
		intervalAvgSpeedMap.put(ei, avgSpeed);
	}
	
	private void printAvgSpeedMap(TreeMap<Long, Double> intervalAvgSpeedMap){
		Set<Long> eiSet = intervalAvgSpeedMap.keySet();
		Iterator <Long> eiIter = eiSet.iterator();
		SFRMProcessor.getInstance().getLogger().debug(",IntervalAvgSpeed,EI,Avg Speed,");
		while(eiIter.hasNext()){
			long ei = eiIter.next();
			double avgSpeed = intervalAvgSpeedMap.get(ei);
			SFRMProcessor.getInstance().getLogger().debug(",IntervalAvgSpeed," + ei + "," + avgSpeed + ",");
		}
	}
	
	/**
	 * Determine the optimized execution interval from the previous statistics
	 * @param intervalAvgSpeedMap statistics for execution interval and average speed
	 * @return execution interval that is corrspending to optimized speed
	 */
	private boolean determineOptimizedValue(TreeMap<Long, Double> intervalAvgSpeedMap){
		Set<Long> eiSet = intervalAvgSpeedMap.keySet();
		Iterator<Long> eiIter = eiSet.iterator();
		double preSpeed = -1;
		long preEI = -1;
		while(eiIter.hasNext()){
			long ei = eiIter.next();
			double curSpeed = intervalAvgSpeedMap.get(ei);
			if(preSpeed != -1){
				//It is expected that the speed is decreasing, so the curSpeed should be less than preSpeed
				if(preSpeed - curSpeed > eiSpeedGradientTolerance){
//					SFRMProcessor.getInstance().getLogger().debug(",Optimized," + preEI + "," + preSpeed + ",");
					optimizedEI = preEI;
					optimizedSpeed = preSpeed;
					return true;
				}
			}
			preSpeed = curSpeed;
			preEI = ei;
		}
//		SFRMProcessor.getInstance().getLogger().debug(",Optimized,didn't have optimized,");
		return false;
	}
	
	
	//Code for adjusting speed
	private double adjustSpeedTolerance = 500.0; 
	private long adjustEIDelta = 1000;
	private long adjustEIMin = 100;
	/**
	 * Adjust the current speed to the specified speed
	 * @param currentSpeed current speed
	 * @param expectedSpeed expected speed
	 * @return the adjusted execution interval
	 */
	public long adjustSpeed(double currentSpeed, double expectedSpeed, long currentExecutionInterval){
		//If the current speed is within the tolerance level, don't adjust the execution interval
		if(expectedSpeed - adjustSpeedTolerance <= currentSpeed && expectedSpeed + adjustSpeedTolerance >= currentSpeed){
			return currentExecutionInterval;
		}else{
			long newExecutionInterval = currentExecutionInterval;
			if(currentSpeed > expectedSpeed){
				newExecutionInterval += adjustEIDelta;
			}else{
				newExecutionInterval -= adjustEIDelta;
				if(newExecutionInterval < 0){
					newExecutionInterval = adjustEIMin;
				}
			}
			collector.setExecutionInterval(newExecutionInterval);
			curEI = newExecutionInterval;
			return newExecutionInterval;
		}
	}
	
	public double getOptimizedSpeed(){
		return optimizedSpeed;
	}
	
	public long getOptimizedEI(){
		return optimizedEI;
	}
	
	public boolean isFoundOptimized(){
		return this.foundOptimized;
	}
	
	public long getCurrentEI(){
		return curEI;
	}
	
	public void setAdjustSpeedTolerance(double tolerance){
		this.adjustSpeedTolerance = tolerance;
	}
	
	public void setAdjustEIDelta(long delta){
		this.adjustEIDelta = delta;
	}
	
	public void setAdjustEIMin(long min){
		this.adjustEIMin = min;
	}
}
