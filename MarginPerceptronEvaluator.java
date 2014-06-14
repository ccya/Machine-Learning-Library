package cs475;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Iterator;
import java.math.*;
//import java.lang.Math;
import java.util.Collections;

public class MarginPerceptronEvaluator extends Evaluator {
	
	private double evaluatorValue = 0.0;
	public double evaluate(List<Instance> instances, Predictor predictor){
		int correct = 0;
		int total = instances.size();
		MarginPerceptron mp = (MarginPerceptron)predictor;
		for(int i = 0; i< instances.size();i++){
			ClassificationLabel preLabel = mp.predict(instances.get(i));
			System.out.println(preLabel.getLabelValue());
			ClassificationLabel realLabel = (ClassificationLabel)instances.get(i).getLabel();
			if(realLabel == null){
				System.out.println("These data dont have labels");
				break;
			}
			if(preLabel.getLabelValue() == realLabel.getLabelValue()){
				correct +=1;
			}
			else{
//				System.out.println(i+" "+ preLabel.getLabelValue()+ " "+ realLabel.getLabelValue());
			}
		}
		System.out.println("correct: " + correct);
		System.out.println("total: " + instances.size());
		evaluatorValue = ((double)correct / total);
		System.out.println("correct/total: " + evaluatorValue);
		
		return evaluatorValue;
	}
}