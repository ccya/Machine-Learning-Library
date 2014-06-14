package cs475;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.HashMap;
//import java.util.Scanner;
//import java.util.Iterator;
import java.util.*;  
import java.math.*;
//import java.lang.Math;
//import java.util.Collections;

public class LogisticRegression extends Predictor {

	private int gd_iterations = 20;
	private double gd_eta = .01; 
	private int numFeature = 0;
	private String preName = "logistic_regression";
	FeatureVector weightVector = new FeatureVector();
	
	public LogisticRegression(int numIteration, double eta,int numF){
		this.gd_eta = eta;
		this.gd_iterations = numIteration;
		this.numFeature = numF;
	}
	
	public void train(List<Instance> instances){
		
		int numFtre = 0;
		int allFtre = 0;
		System.out.println("eta: " + this.gd_eta);
		System.out.println("num_features_to_select: " + this.numFeature);

//**********  Get the featureMatrix based on which feature we want to use
		allFtre = this.numAllFtre(instances);
		ArrayList<Integer> usedFeatureNumber = new ArrayList<Integer>();
		if(this.numFeature == 0){
			numFtre = allFtre;   //If no number of using feature specified, use all the features 
			usedFeatureNumber = null;
			System.out.println("Select all feature to classify");
		}
		else {
			ArrayList<Instance> binstances = new ArrayList<Instance>();
			for(int i = 0; i<instances.size();i++){
				binstances.add(new Instance(instances.get(i).getFeatureVector(), instances.get(i).getLabel()));
			}
			ArrayList<FeatureItem> featureIG = this.sortFeature(binstances);
			numFtre = this.numFeature; //Else use the specified number
			if(numFtre>allFtre){
				numFtre = allFtre;
			}
			for(int i=0 ; i<numFtre ; i++){
				usedFeatureNumber.add(i, featureIG.get(i).getId());
//				System.out.println("Select feature " + featureIG.get(i).getId());
//				System.out.println("IG for this feature is: "+ featureIG.get(i).getValue());
			}
		}
 		HashMap<Integer,Double> wX = new HashMap<Integer,Double>();
 //**********  Compute the weight vector
 		for (int k = 0; k<gd_iterations; k++){
 			FeatureVector weightVectorNew = new FeatureVector();
 			wX.clear();
 			System.out.println("----"+k+ "th Iteration-----");
 			for(int j = 0; j<instances.size();j++){
 				double tmp = 0.0;
 				for(int i = 0; i<=allFtre;i++){
 					if( ((usedFeatureNumber!=null)&&(usedFeatureNumber.contains(i)))
 							||(usedFeatureNumber==null)){
 					tmp += weightVector.get(i)*instances.get(j).getFeatureVector().get(i);
 					}
 				}
 				wX.put(j, tmp);
 			}
			for(int j = 0; j<= allFtre; j++){
				if( ((usedFeatureNumber!=null)&&(usedFeatureNumber.contains(j)))
					||(usedFeatureNumber == null)){
					if(j == 0){weightVectorNew.add(0, 0.0);}
					else{
						double update = 0.0;
						for(int i = 0; i<instances.size();i++){
							ClassificationLabel label = (ClassificationLabel) instances.get(i).getLabel();
							double wx = wX.get(i);
							double gfunction = this.gFunction(-wx);
							update += label.getLabelValue()*gfunction*instances.get(i).getFeatureVector().get(j)
								+(1-label.getLabelValue())*(1-gfunction)*(0-instances.get(i).getFeatureVector().get(j));
						}
						double lastValue = weightVector.get(j);
						double newValue = lastValue+this.gd_eta*(update);
						weightVectorNew.add(j, newValue);	
					}
				}
			}
			weightVector = weightVectorNew;
 		}
	}
	
	private double gFunction(double pram){
		return (1.0/(1+Math.exp(-pram)));
	}

	/**
	 *  Sort feature based on IG for future use
	 * @param instances
	 * @return list of sorted feature with its entropy
	 */
	public ArrayList<FeatureItem> sortFeature(List<Instance> instances){

		int total = instances.size();
		boolean isBinary = true;

		int allFtre = this.numAllFtre(instances);
		for(int i = 0; i<instances.size();i++){
			if(isBinary == false){
				break;
			}
			for (int j =0;j<=allFtre;j++)
			if((instances.get(i).getFeatureVector().get(j)!=0.0)
					&&(instances.get(i).getFeatureVector().get(j)!=1.0)){
				isBinary = false;
				break;
			}
		}
		
//******* get feature Matrix, binary the feature if it's continuous;
		if(!isBinary){
			System.out.println("this is not binary data");
//			ArrayList<Instance> newInstances = new ArrayList<Instance>();
			FeatureVector featureMean = this.getFeatureMean(instances);
			for(int i = 0;i<instances.size();i++){
				FeatureVector fv= instances.get(i).getFeatureVector();
				FeatureVector newFv = new FeatureVector();
				for(int j = 0; j <= allFtre; j++){
						double tmp = fv.get(j);
//						System.out.println("compare " + featureMean.get(j) + " with "+ tmp);
						if(tmp>=featureMean.get(j)){
							tmp = 1.0;
							newFv.add(j, 1.0);
						}
//				System.out.println("result is: " + newFv.get(j));
//				System.out.println("instance " + i + " feature " + j +" is: "+ newFv.get(j));
				}
				instances.get(i).setFeatureVector(newFv);
			}			
		}
		else {
			System.out.println("this is binary data");		
		}

//******* Compute conditional entropy;	

		ArrayList<FeatureItem> featureIG = new ArrayList<FeatureItem>();
		for(int i = 0; i<= allFtre; i++){
			int y1x1 = 0;
			int y1x0 = 0;
			int y0x1 = 0;
			int y0x0 = 0;
			int x0 = 0;
			int x1 = 0;
			double p_y0x0 = .0;
			double p_y0x1 = .0;
			double p_y1x0 = .0;
			double p_y1x1 = .0;
			double p_x0 = .0;
			double p_x1 = .0;
			double entropy = .0;
			double ig = .0;
			double log_y0x0; 
			double log_y0x1;
			double log_y1x0;
			double log_y1x1;
			if(i == 0){
				continue;
			}
			for(int j = 0; j<instances.size(); j++){
				ClassificationLabel label = (ClassificationLabel)instances.get(j).getLabel();
				if (instances.get(j).getFeatureVector().get(i)==0){
					x0 += 1;
					if (label.getLabelValue() == 1){
						y1x0 +=1;
					}
					else y0x0 +=1;
				}
				else{
					x1 +=1;
					if(label.getLabelValue() == 1){
						y1x1 +=1;
					}
					else y0x1 +=1;
				}		
			}
//			System.out.println("x0: "+ x0);
//			System.out.println("x1: "+ x1);
//			System.out.println("y1x0: "+ y1x0);
//			System.out.println("y0x0: "+ y0x0);
//			System.out.println("y1x1: "+ y1x1);
//			System.out.println("y0x1: "+ y0x1);
			p_y0x0 = ((double) y0x0)/total;
			p_y0x1 = ((double) y0x1)/total;
			p_y1x0 = ((double) y1x0)/total;
			p_y1x1 = ((double) y1x1)/total;
			p_x0 = ((double) x0)/total;
			p_x1 = ((double) x1)/total;
//			System.out.println("p_x0: "+ p_x0);
//			System.out.println("p_x1: "+ p_x1);
//			System.out.println("p_y1x0: "+ p_y1x0);
//			System.out.println("p_y0x0: "+ p_y0x0);
//			System.out.println("p_y1x1: "+ p_y1x1);
//			System.out.println("p_y0x1: "+ p_y0x1);
			if((p_y0x0!=0)&&(p_x0!=0)){
				log_y0x0 = Math.log(p_y0x0/p_x0);
			}
			else log_y0x0 = 0;
			if((p_y0x1!=0)&&(p_x1!=0)){
				log_y0x1 = Math.log(p_y0x1/p_x1);
			}
			else log_y0x1 = 0;
			if((p_y1x0!=0)&&(p_x0!=0)){
				log_y1x0 = Math.log(p_y1x0/p_x0);
			}
			else log_y1x0 = 0;
			if((p_y1x1!=0)&&(p_x1!=0)){
				log_y1x1 = Math.log(p_y1x1/p_x1);
			}
			else log_y1x1 = 0;
			ig = 0-((p_y0x0)*log_y0x0
					+ (p_y1x0)*log_y1x0
					+ (p_y0x1)*log_y0x1
					+ (p_y1x1)*log_y1x1);
			
			FeatureItem fi = new FeatureItem(i,ig);
			featureIG.add(fi);
		}
		Collections.sort(featureIG, new Comparator<FeatureItem>(){
			public int compare(FeatureItem f1, FeatureItem f2){
				return (f1.getValue()).compareTo(f2.getValue());
			}
		});
		return featureIG;
	}
	
	public ClassificationLabel predict(Instance instance){
		HashMap<Integer, Double> vectormap = instance.getFeatureVector().getVector();
		int preLabel = 0;
		int allFtre = Collections.max(instance.getFeatureVector().getVector().keySet());
		double probability = .0;
		double wx = .0;
		for (int i = 0; i<=allFtre;i++){
			if (this.weightVector.getVector().containsKey(i)){
				if(vectormap.containsKey(i)){
					wx += weightVector.get(i)*vectormap.get(i);
				}
			}
		}
		probability = this.gFunction(wx);
		if(probability >= 0.5){
			preLabel = 1;
		}
		else preLabel = 0;
//		System.out.println("The predict label is: " + preLabel);
		ClassificationLabel Label = new ClassificationLabel(preLabel);
		return Label;
	}
	
	/**
	 * Get how many features each instances can have
	 * @param instances
	 * @return # of features
	 */
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
	
	/**
	 * Get the feature matrix from all the instances, the 
	 * matrix will only contains the features that we selected if specify
	 * @param instances, selected feature number
	 * @return feature matrix
	 */
//	public ArrayList<ArrayList<Double>> computXMatrix
//				(List<Instance> instances, ArrayList<Integer> usedFeatureNumber){
//	
//		ArrayList<ArrayList<Double>> xMatrix = new ArrayList<ArrayList<Double>>();
//		int allFtre = 0;
//		allFtre = this.numAllFtre(instances);
////		System.out.println("allFtre: " + allFtre);
//		ArrayList<Double> xMatrix_row = new ArrayList<Double> ();
//		for(int i = 0;i<instances.size();i++){
//			xMatrix_row.clear();
//			FeatureVector fv= instances.get(i).getFeatureVector();
//			HashMap<Integer,Double> vectormap = fv.getVector();
//			if(usedFeatureNumber == null){
//				for(int j = 0; j <= allFtre; j++){
//					if(j == 0){
//						xMatrix_row.add(j,0.0);
//					}
//					else{
//						if(vectormap.containsKey(j)){
//							double tmp = vectormap.get(j);
//							xMatrix_row.add(j, tmp);
//						}
//						else xMatrix_row.add(j, .0);
//					}
//				}
//			xMatrix.add(i, xMatrix_row);
//			}
//			else{	
//				for(int j = 0; j <= allFtre; j++){
////					System.out.println("j: " + j);
//					if(j == 0){
//						xMatrix_row.add(j,0.0);
//					}
//					else{
//						if(usedFeatureNumber.contains(j)){
//							if(vectormap.containsKey(j)){
//								double tmp = vectormap.get(j);
//								xMatrix_row.add(j, tmp);
//							}
//							else xMatrix_row.add(j, .0);
//						}
//						else xMatrix_row.add(j, .0);
//					}
//				}
//			xMatrix.add(i, xMatrix_row);	
//			}
////			xMatrix.add(i, xMatrix_row);
////			System.out.println("size of one row: " + xMatrix_row.size());
//		}
//		return xMatrix;
//	}
	
	/**
	 *  Get the mean value for each feature if the instance is continuous
	 * @param instances
	 * @return mean value for each feature
	 */
	public FeatureVector getFeatureMean(List<Instance> instances){
		int allFtre = this.numAllFtre(instances);
		FeatureVector featureMean = new FeatureVector();
		for(int i = 0; i<=allFtre; i++){
			double sum = 0.0;
			for(int j = 0; j< instances.size();j++){
				Instance tmp = instances.get(j);
				HashMap<Integer,Double> vectormap = tmp.getFeatureVector().getVector();
				if(vectormap.containsKey(i)){
					sum += vectormap.get(i);
				}
			}
			featureMean.add(i, (double)(sum)/instances.size());
//			System.out.println("mean for "+ i + ": " + (double)(sum)/instances.size());
		}
		return featureMean;
	}

	public  ClassificationLabel getLabel(){
		return null;
	}
	
	public String getpreName(){
		return this.preName;
	}
	
	public void printWeight(){
		Iterator<Integer> it = this.weightVector.getVector().keySet().iterator();
		while (it.hasNext()){
			int key = it.next();
			System.out.println("weight for " + key + " is: " + weightVector.get(key));
		}
	}

	public int getnumFtre(){
		return this.numFeature;
	}
}

