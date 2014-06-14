package cs475;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Iterator;
import java.math.*;
import java.util.HashMap;
import java.util.Map;

public class EvenOddClassification extends Predictor {
	
	private ArrayList<ClassificationLabel> predictLabList = new ArrayList<ClassificationLabel>();
	private String preName = "even_odd";
	
	public void train(List<Instance> instances){
		Iterator<Instance> it = instances.iterator();
		while(it.hasNext()){
			int prelabel = 0;
			float evensum = 0;
			float oddsum = 0;
			Instance i = it.next();
			ClassificationLabel tmplab;
			tmplab = (ClassificationLabel)i.getLabel();
			FeatureVector fv  = i.getFeatureVector();
			HashMap<Integer,Double> vectormap = new HashMap<Integer,Double>();
			vectormap = fv.getVector();
			Iterator<Integer> keyit = vectormap.keySet().iterator(); 			
			while(keyit.hasNext()) {
				int key = keyit.next();
				if (key % 2 == 0) {
					evensum += vectormap.get(key);
				}
				else {
					oddsum += vectormap.get(key);
				}
			}
			if(evensum > oddsum){
				prelabel = 1;		
			}
			else{
				prelabel = 0;
			}
			ClassificationLabel tmp = new ClassificationLabel(prelabel);
			this.predictLabList.add(tmp);
		}
				
	}
			
	public ClassificationLabel predict(Instance instance){
		FeatureVector fv  = instance.getFeatureVector();
		float evensum = 0;
		float oddsum = 0;
		int prelabel;
		HashMap<Integer,Double> vectormap = new HashMap<Integer,Double>();
		vectormap = fv.getVector();
		Iterator<Integer> keyit = vectormap.keySet().iterator(); 			
		while(keyit.hasNext()) {
			int key = keyit.next();
			if (key % 2 == 0) {
				evensum += vectormap.get(key);
			}
			else {
				oddsum += vectormap.get(key);
			}
		}
		if(evensum > oddsum){		
			prelabel = 1;		
		}
		else{
			prelabel = 0;
		}
		ClassificationLabel tmp = new ClassificationLabel(prelabel);
		return tmp;
	}
	
	public ClassificationLabel getLabel(){
		return null;
	}
	public String getpreName(){
		return this.preName;
	}
	
}