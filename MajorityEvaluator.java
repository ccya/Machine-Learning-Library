package cs475;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Iterator;
import java.math.*;

public class MajorityEvaluator extends Evaluator {
	
	private double evaValue = 0.0;
	
	public double evaluate(List<Instance> instances, Predictor predictor){
		int correct = 0;
		int total = 0;
		ClassificationLabel label = (ClassificationLabel)predictor.getLabel();
		Iterator<Instance> it = instances.iterator();
	      while(it.hasNext()){
	        total = total + 1;
	    	Instance i = it.next();
	        ClassificationLabel realLabel = (ClassificationLabel)i.getLabel();
	        if(realLabel == null){
	        	break;
	        }
	        if (realLabel.getLabelValue() == label.getLabelValue()){
	        	correct = correct+1;
	        }
	      } 
//	     System.out.println(correct);
//	     System.out.println(total);
	    this.evaValue = ((double)correct / total);
	    return evaValue;
	}
}
	