package cs475;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;  
import java.math.*;
import java.util.Map.Entry;


public class MarginPerceptron extends Predictor {

	private double online_learning_rate = 1; 
	private String preName = "margin_perceptron";
	private int online_training_iterations = 5;
	FeatureVector weightVector = new FeatureVector();
	
	public MarginPerceptron(int numIteration, double online_learning_rate){
		this.online_learning_rate = online_learning_rate;
		this.online_training_iterations = numIteration;
	}
	
	public void train(List<Instance> instances){
		int allFtre = this.numAllFtre(instances);
//		System.out.println(allFtre);
//		System.out.println(online_learning_rate);
//		for(int i = 0;i<allFtre;i++){
//			System.out.println(i+": "+ this.weightVector.get(i));
//		}
		for(int k = 0; k <online_training_iterations;k++){
			System.out.println("-------"+ k + "th Iteration-------");
			for(int i = 0;i<instances.size();i++){
				double wx = .0;
				Instance tmp = instances.get(i);
				ClassificationLabel label = (ClassificationLabel) tmp.getLabel();
				int yi = label.getLabelValue();
				if(yi == 0){
					yi = -1;
				}
//				for(int j =1; j<=allFtre; j++){
//					wx += weightVector.get(j)*tmp.getFeatureVector().get(j);
//				}
				for(Entry<Integer,Double> entry:tmp.getFeatureVector().getVector().entrySet()){
					wx += entry.getValue()*weightVector.get(entry.getKey());
				}

				if ((yi*wx) < 1){
					for(int j = 1;j<=allFtre; j++){
						double update = 0.0;
						double oldvalue = 0.0;
						double newvalue = 0.0;
						update = online_learning_rate*yi*
								tmp.getFeatureVector().get(j);
						oldvalue = weightVector.get(j);
						newvalue = weightVector.get(j) + update;
						weightVector.set(j, newvalue);
					}
				}
			}
//			for(int i = 0;i<=allFtre;i++){
//				System.out.println(i+": "+ this.weightVector.get(i));
//			}
		}

	}
	
	public ClassificationLabel predict(Instance instance){
		HashMap<Integer, Double> vectormap = instance.getFeatureVector().getVector();
		int preLabel = 0;
		int allFtre = Collections.max(instance.getFeatureVector().getVector().keySet());
		double wx = .0;
		for(Entry<Integer,Double> entry:instance.getFeatureVector().getVector().entrySet()){
			wx += entry.getValue()*weightVector.get(entry.getKey());
		}
//		for (int i = 1; i<=allFtre;i++){
//			if (this.weightVector.getVector().containsKey(i)){
//				if(vectormap.containsKey(i)){
//					wx += weightVector.get(i)*vectormap.get(i);
//				}
//			}
//		}
//		System.out.println(wx);
		if(wx >= 0){
//			System.out.println(wx);
			preLabel = 1;
		}
		else {
//			System.out.println(wx);
			preLabel = 0;
		}
		ClassificationLabel Label = new ClassificationLabel(preLabel);
		return Label;	
		}
	
	
	
	public int numAllFtre(List<Instance> instances){
		int allFtre = 0;
		Iterator<Instance> it = instances.iterator();
		while(it.hasNext()){
			Instance tmp = it.next();
			int m = Collections.max(tmp.getFeatureVector().getVector().keySet());
			if (m>allFtre){
				allFtre = m; // get the how many features for a instance
			}
		}
		return allFtre;
	}
	
	public  Label getLabel(){
		return null;
	}
	public  String getpreName(){
		return this.preName;
	}
	
}

