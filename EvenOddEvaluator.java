package cs475;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Iterator;
import java.math.*;

public class EvenOddEvaluator extends Evaluator {
	
	private double evaluatorValue = 0.0; 
	
	public double evaluate(List<Instance> instances, Predictor predictor){
		int correct = 0;
		int total = instances.size();
//		ClassificationLabel label = (ClassificationLabel)predictor.getLabel();
//		ArrayList<ClassificationLabel> trainedLabelList = predictor.
		
		
		
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
			ClassificationLabel realLabel = (ClassificationLabel)i.getLabel();
	        if(realLabel == null){
	        	break;
	        }
			while(keyit.hasNext()) {
				int key = keyit.next();
				if (key % 2 == 0) {
					evensum += vectormap.get(key);
				}
				else {
					oddsum += vectormap.get(key);
				}
			}
			if((evensum > oddsum)||(evensum == oddsum)){				
				prelabel = 1;		
			}
			else{
				prelabel = 0;
			}
			if(prelabel == realLabel.getLabelValue()){
				correct= correct+1;
			}
		}
			
//		 System.out.println(correct);
//		 System.out.println(total);
	      this.evaluatorValue = (double)correct / total;
	      return this.evaluatorValue;
	}
}
	