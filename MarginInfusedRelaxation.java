package cs475;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;  
import java.math.*;
import java.util.Map.Entry;


public class MarginInfusedRelaxation extends Predictor {

//	private double online_learning_rate = 1; 
	private String preName = "mira";
	private int online_training_iterations = 5;
	FeatureVector weightVector = new FeatureVector();
	
	public MarginInfusedRelaxation(int numIteration){
//		this.online_learning_rate = online_learning_rate;
		this.online_training_iterations = numIteration;
	}
	
	public void train(List<Instance> instances){
		int allFtre = this.numAllFtre(instances);
		System.out.println(allFtre);
//		System.out.println(online_learning_rate);
		for(int k = 0; k <online_training_iterations;k++){
			System.out.println("-------"+ k + "th Iteration-------");
			for(int i = 0;i<instances.size();i++){
				double wx = .0;
				double xx = .0;
				Instance tmp = instances.get(i);
				ClassificationLabel label = (ClassificationLabel) tmp.getLabel();
				int yi = label.getLabelValue();
				if(yi == 0){
					yi = -1;
				}
//				for(int j =1; j<=allFtre; j++){
//					wx += weightVector.get(j)*tmp.getFeatureVector().get(j);
//					xx += Math.pow(tmp.getFeatureVector().get(j), 2);
//				}
				for(Entry<Integer,Double> entry:tmp.getFeatureVector().getVector().entrySet()){
					wx += entry.getValue()*weightVector.get(entry.getKey());
					xx += Math.pow(entry.getValue(), 2);
				}
				if ((yi*wx) < 1){
					double learning_rate = ((double) (1-(yi*wx)))/xx;
					for(int j = 1;j<=allFtre; j++){
						double update = 0.0;
						double oldvalue = 0.0;
						double newvalue = 0.0;
						
						update = learning_rate*yi*
								tmp.getFeatureVector().get(j);
						oldvalue = weightVector.get(j);
						newvalue = weightVector.get(j) + update;
						weightVector.set(j, newvalue);
					}
				}
			}
		}
	}
	
	public ClassificationLabel predict(Instance instance){
		HashMap<Integer, Double> vectormap = instance.getFeatureVector().getVector();
		int preLabel = 0;
		int allFtre = Collections.max(instance.getFeatureVector().getVector().keySet());
		double wx = .0;
		for (int i = 1; i<=allFtre;i++){
			if (this.weightVector.getVector().containsKey(i)){
				if(vectormap.containsKey(i)){
					wx += weightVector.get(i)*vectormap.get(i);
				}
			}
		}
		if(wx >= 0){
			preLabel = 1;
		}
		else preLabel = 0;
//		System.out.println("The predict label is: " + preLabel);
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

