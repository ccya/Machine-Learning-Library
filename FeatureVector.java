package cs475;

import java.io.Serializable;
import java.util.HashMap;

public class FeatureVector implements Serializable {

	private HashMap<Integer, Double> vectorMap = new HashMap<Integer,Double>();

	
	public void add(int index, double value) {
		// TODO Auto-generated method stub
		if(!(value == 0)){
			this.vectorMap.put(index, value);
		}
	}
	
	public double get(int index) {
		// TODO Auto-generated method stub
		if(vectorMap.containsKey(index)){
			return this.vectorMap.get(index);
		}
		else return 0;
		
	}
	
	public void set(int index, double value){
		this.vectorMap.put(index, value);
	}
	
	public HashMap<Integer,Double> getVector(){
		return this.vectorMap; 
	}

}
