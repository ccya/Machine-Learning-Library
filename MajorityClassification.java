package cs475;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Iterator;
import java.math.*;

public class MajorityClassification extends Predictor {
	
	private ClassificationLabel predictLabel = new ClassificationLabel(0);
	private String preName = "majority";
	public void train(List<Instance> instances){
		int preLabel =0;
		int counter1 = 0 ;
		int counter0 = 0;
		Iterator<Instance> it = instances.iterator();
	      while(it.hasNext()){
	        Instance i = it.next();
	        ClassificationLabel label = (ClassificationLabel)i.getLabel();
	        int labelvalue = label.getLabelValue();
	         if(labelvalue == 1) 
		       {counter1++;}
		     else
		        {counter0++;}
	         }
	    
		if (counter1 > counter0) 
		   {preLabel=1;}
		if (counter1 < counter0) 
		   {preLabel=0;}
		if (counter1 == counter0)
		   {preLabel = (int)Math.random()>0.5?1:0;}
		this.predictLabel = new ClassificationLabel(preLabel);
	}
	
	public ClassificationLabel predict(Instance instance){
		instance.setLabel(predictLabel);
		return predictLabel;
	}
	
	public ClassificationLabel getLabel(){
		return this.predictLabel;
	}
	public String getpreName(){
		return this.preName;
	}
}