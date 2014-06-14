package cs475;


import java.util.*;  
import java.math.*;
import java.util.Collections;

public class LambdaMeans extends Predictor {
	private double lambdavalue = 0.0;
	private String prename = "lambda_means";
	ArrayList<FeatureVector> means = new ArrayList<FeatureVector>();
	int numCluster = 1;
	int iterations = 10;
	
	public LambdaMeans(double lambdavalue , int numIteration){
		this.lambdavalue = lambdavalue;
		this.iterations = numIteration;
	}
	
	public void train(List<Instance> instances){
		System.out.println("initialized lambda : " + lambdavalue);
		System.out.println("initialized iterations: " + iterations);
		FeatureVector totalmean = new FeatureVector();
		int allFtre = this.numAllFtre(instances);
		
// Compute total means of the dataset
		for(int i = 1; i<=allFtre;i++){
			double sum = 0.0;
			for(int j =0; j<instances.size();j++){
				Instance tmp = instances.get(j);
//				System.out.println("feature " + i + " instance "+ j +": "+ tmp.getFeatureVector().get(i));
				sum = sum + tmp.getFeatureVector().get(i);
			}
			double value = sum/instances.size();
			totalmean.add(i, value);
		}
		means.add(totalmean);

//Computer default lambda value;
		double lambda = 0.0;
		double distance = 0.0;
		for(int i =0; i<instances.size();i++){
			double squareDistance = 0.0;
			Instance tmp = instances.get(i);
			Set<Integer> keyset = tmp.getFeatureVector().getVector().keySet();
			Set<Integer> union = new HashSet<Integer>(keyset);
			union.addAll(totalmean.getVector().keySet());
			for(Integer key: union){
				squareDistance = squareDistance + 
						Math.pow((tmp.getFeatureVector().get(key)-totalmean.get(key)),2);
			}
//			distance = distance + Math.sqrt(squareDistance);
			distance = distance + squareDistance;
		}
		lambda = distance/instances.size();
		System.out.println("lambda : "+ lambda);

//Start to train		
		for(int k = 0; k<this.iterations; k++){
			ArrayList<FeatureVector> rMatrix = new ArrayList<FeatureVector>();
//			System.out.println("--------"+k+"th Iteration--------");		
// The E-step
			for(int i = 0; i<instances.size(); i++){
				int indicator = 0;
				Instance tmp = instances.get(i);
				double min = 0.0;
				FeatureVector cluster1 = means.get(0);
				Set<Integer> keyset = tmp.getFeatureVector().getVector().keySet();
				Set<Integer> union = new HashSet<Integer>(keyset);
				union.addAll(cluster1.getVector().keySet());
				for(Integer key: union){
					min = min + 
							Math.pow((tmp.getFeatureVector().get(key)-cluster1.get(key)),2);
				}
//				min = Math.sqrt(min);
				for(int j = 1; j<means.size(); j++){
					double dist = 0.0;
					FeatureVector clustermean = means.get(j);
					Set<Integer> keys = tmp.getFeatureVector().getVector().keySet();
					Set<Integer> unionkeys = new HashSet<Integer>(keyset);
					unionkeys.addAll(clustermean.getVector().keySet());
					for(Integer key: unionkeys){
						dist = dist + 
								Math.pow((tmp.getFeatureVector().get(key)-clustermean.get(key)),2);
					}
//					dist = Math.sqrt(dist);
					if(dist < min){
						min = dist;
						indicator = j;
					}
				}
//Initialize assignment R-martix of each cluster
				FeatureVector row = new FeatureVector();
				if(min <= lambda ){
					row.set(indicator, 1);
					rMatrix.add(row);
//					System.out.println("instance " + i + " dis: " + min + " belongs to class " + indicator);
				}
				else{
//					System.out.println("Create a new cluster because of "+ i);
//					System.out.println("min: " + min + " class " + indicator);
					means.add(tmp.getFeatureVector());
					row.add(means.size()-1,1);
					rMatrix.add(row);
				}
			}
//M-step
			for(int i = 0; i<means.size();i++){
				FeatureVector updateMean = new FeatureVector();
				FeatureVector sum = new FeatureVector();
				int numInstance = 0;
				for(int j = 0; j<instances.size();j++){
//					System.out.println("for " + j +" instance "+" " +i+" class" +rMatrix.get(j).get(i));
					if(rMatrix.get(j).get(i) != 0){
						numInstance = numInstance+1;
						Instance tmp = instances.get(j);
						double tmpvalue = 0.0;
						for(Integer key: tmp.getFeatureVector().getVector().keySet()){
							tmpvalue = sum.get(key)+tmp.getFeatureVector().get(key);
							sum.set(key, tmpvalue);
						}
					}
				}
//				System.out.println("for class " + i + " "+ numInstance);
				if(numInstance == 0){
					updateMean.getVector().clear();
				}
				else{
					for(Integer key: sum.getVector().keySet()){
						updateMean.add(key, sum.get(key)/numInstance);
					}
				}
				means.set(i, updateMean);
			}

//			for(int i =0; i<means.size();i++){
//				System.out.println("for class " + i);
//				for(Integer key : means.get(i).getVector().keySet()){
//					System.out.println(key + ": " + means.get(i).get(key));
//				}
//			}
			
		}
		numCluster = means.size();
	}
	
	public ClassificationLabel predict(Instance instance){

		
//		System.out.println("enter predict");
		int labelValue = 0;
//		ClassificationLabel label = new ClassificationLabel();
		double min = 0.0;
		
		FeatureVector mean = means.get(0);
		Set<Integer> keyset = instance.getFeatureVector().getVector().keySet();
		Set<Integer> union = new HashSet<Integer>(keyset);
		union.addAll(mean.getVector().keySet());
		for(Integer key: union){
//			System.out.println(instance.getFeatureVector().get(key));
//			System.out.println(mean.get(key));
			min = min + 
					(instance.getFeatureVector().get(key)-mean.get(key))*(instance.getFeatureVector().get(key)-mean.get(key));
			}
//		min = Math.sqrt(min);
//		System.out.println("for 0: " + min);
		for(int i = 1; i<means.size(); i++){
			double distance = 0.0;
			Set<Integer> keys = instance.getFeatureVector().getVector().keySet();
			Set<Integer> unionkeys = new HashSet<Integer>(keyset);
			unionkeys.addAll(means.get(i).getVector().keySet());
//			System.out.println(unionkeys.size());
			for(Integer key: unionkeys){
//				System.out.println(instance.getFeatureVector().get(key));
//				System.out.println(means.get(i).get(key));
				distance = distance + 
						(instance.getFeatureVector().get(key)-means.get(i).get(key))
						*(instance.getFeatureVector().get(key)-means.get(i).get(key));
//				System.out.println("dis: " + distance);
			}
//			distance = Math.sqrt(distance);
//			BufferedWriter out = null; 
//			try {
//				 FileWriter writer = new FileWriter("debug.txt", true); 
//				 String content = "for cluster" +  i + ": " + distance ;
//		         writer.write(content);
//		         writer.write("\n");
//		         writer.close();
//
//				  }
//			catch (Exception e){//Catch exception if any
//				  System.err.println("Error: " + e.getMessage());
//				  }
//			out.close();
//			System.out.println("for cluster" +  i + ": " + distance);
			if(distance<min){
				min = distance;
				labelValue = i;
			}
			
		}
//		System.out.println("label: " + labelValue);
		ClassificationLabel label = new ClassificationLabel(labelValue);
		return label;
	}
	public String getpreName(){
		return this.prename;
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
	
	public ClassificationLabel getLabel(){
		return null;
	}
}
