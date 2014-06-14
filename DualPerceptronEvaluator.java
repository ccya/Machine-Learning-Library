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

public class DualPerceptronEvaluator extends Evaluator {
	
	private double evaluatorValue = 0.0;
	public double evaluate(List<Instance> instances, Predictor predictor){
		int correct = 0;
		int total = instances.size();
		DualPerceptron dp = (DualPerceptron)predictor;
		double d = dp.dvalue();
		boolean isLinear = dp.isLinear();
		boolean isPoly = !isLinear;
		for(int i = 0; i< instances.size();i++){
			ClassificationLabel preLabel = dp.predict(instances.get(i),isLinear,isPoly,d);
			ClassificationLabel realLabel = (ClassificationLabel)instances.get(i).getLabel();
			if(realLabel == null){
				break;
			}
//			System.out.println(preLabel.getLabelValue());
			if(preLabel.getLabelValue() == realLabel.getLabelValue()){
				correct +=1;
			}
		}
		System.out.println("correct: " + correct);
		System.out.println("total: " + instances.size());
		evaluatorValue = ((double)correct / total);
		System.out.println("correct/total: " + evaluatorValue);
		
		return evaluatorValue;
	}
}