package cs475;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;  
import java.math.*;
import java.util.Collections;

public class SKA extends Predictor {
	private double lambdavalue = 0.0;
	private String prename = "ska";
	ArrayList<FeatureVector> means = new ArrayList<FeatureVector>();
	int numCluster = 3;
	int iterations = 10;
	
	public SKA(int numCluster , int numIteration){
		this.numCluster = numCluster;
		this.iterations = numIteration;
//		this.numFeature = numF;
	}
	
	public void train(List<Instance> instances){
		
		System.out.println("numCluster: " + numCluster);
// Initialize for means of each cluster
		for(int i =0; i<numCluster;i++){
			FeatureVector tmp = new FeatureVector();
			tmp = instances.get(i).getFeatureVector();
			means.add(tmp);
		}
//check for initialization		
	
//Start to train
		HashMap<Integer,ArrayList<Integer>> rMatrix = new HashMap<Integer,ArrayList<Integer>>();
		for(int k = 0; k< iterations;k++){
//			System.out.println("-----"+k+"th iteration-----");
			for(int i = 0; i<instances.size();i++){
//				System.out.println("--for instance " + i);
				int indicator = 0;
				int former = -1;
				Instance tmp = instances.get(i);
				double min = 0.0;
				FeatureVector cluster1 = means.get(0);
				Set<Integer> keyset = tmp.getFeatureVector().getVector().keySet();
				Set<Integer> union = new HashSet<Integer>(keyset);
				union.addAll(cluster1.getVector().keySet());
				for(Integer key: union){
//					System.out.println("key: " + key);
//					System.out.println("ownfeature: " + tmp.getFeatureVector().get(key));
//					System.out.println("meanfeature: "+cluster1.get(key));
					
					min = min + 
							(tmp.getFeatureVector().get(key)-cluster1.get(key))*(tmp.getFeatureVector().get(key)-cluster1.get(key));
				}
//				System.out.println("for 0: " + min);
//				min = Math.sqrt(min);
				for(int j = 1; j<means.size(); j++){
					double dist = 0.0;
					FeatureVector clustermean = means.get(j);
					Set<Integer> keys = tmp.getFeatureVector().getVector().keySet();
					Set<Integer> unionkeys = new HashSet<Integer>(keyset);
					unionkeys.addAll(clustermean.getVector().keySet());
					for(Integer key: unionkeys){
//						System.out.println("ownfeature: "+ tmp.getFeatureVector().get(key));
//						System.out.println("meanfeature: "+ clustermean.get(key));
						dist = dist + 
								Math.pow((tmp.getFeatureVector().get(key)-clustermean.get(key)),2);
					}
//					dist = Math.sqrt(dist);
//					System.out.println("for " + j+" : " + dist);
					if(dist < min){
						min = dist;
						indicator = j;
					}
				}

//Find the former cluster this instance belongs to
				for(Integer key : rMatrix.keySet()){
					if(rMatrix.get(key).contains(i)){
						former = key;
					}
				}
//				System.out.println("former: " + former);
//				System.out.println("indicator:"  + indicator);
//Put this instance into the new cluster and Update the means
				if(!rMatrix.containsKey(indicator)){
					ArrayList<Integer> row = new ArrayList<Integer>();
					row.add(i);
					rMatrix.put(indicator, row);
				}
				else{
					rMatrix.get(indicator).add(i);
				}
				FeatureVector updateMeanN = new FeatureVector();
				FeatureVector sumNew = new FeatureVector();
				for(int l= 0;l<rMatrix.get(indicator).size();l++){
					int index = rMatrix.get(indicator).get(l);
					Instance tmpins = instances.get(index);
					double tmpvalue = 0.0;
					for(Integer key: tmpins.getFeatureVector().getVector().keySet()){
						tmpvalue = sumNew.get(key)+tmpins.getFeatureVector().get(key);
						sumNew.add(key, tmpvalue);
					}
				}
				int numNew = rMatrix.get(indicator).size();
				for(Integer key: sumNew.getVector().keySet()){
					updateMeanN.add(key, sumNew.get(key)/numNew);
				}
				means.set(indicator, updateMeanN);
				
//Remove this instance from the former cluster and Update the former cluster mean;
				if(former!=-1){
					rMatrix.get(former).remove((Integer)i);				
					FeatureVector updateMeanP = new FeatureVector();
					FeatureVector sumPre = new FeatureVector();
					for(int l= 0;l<rMatrix.get(former).size();l++){
						int index = rMatrix.get(former).get(l);
						Instance tmpins = instances.get(index);
						double tmpvalue = 0.0;
						for(Integer key: tmpins.getFeatureVector().getVector().keySet()){
							tmpvalue = sumPre.get(key)+tmpins.getFeatureVector().get(key);
							sumPre.add(key, tmpvalue);
						}
					}
					int numPre = rMatrix.get(former).size();
					for(Integer key: sumPre.getVector().keySet()){
						updateMeanP.add(key, sumPre.get(key)/numPre);
					}
					means.set(former, updateMeanP);
				}
				
//				System.out.println("--updated mean");
//				for(int m = 0; m<means.size();m++){
//					FeatureVector tmpk = means.get(m);
//					for(Integer key: tmpk.getVector().keySet()){
//						System.out.println("key " + key + ": " + tmpk.get(key));
//					}
//				}
			}
		}
	}
	
	public ClassificationLabel predict(Instance instance){
		int labelValue = 0;
		double min = 0.0;
		FeatureVector mean = means.get(0);
		Set<Integer> keyset = instance.getFeatureVector().getVector().keySet();
		Set<Integer> union = new HashSet<Integer>(keyset);
		union.addAll(mean.getVector().keySet());
		for(Integer key: union){
			min = min + 
					(instance.getFeatureVector().get(key)-mean.get(key))*(instance.getFeatureVector().get(key)-mean.get(key));
			}
		for(int i = 1; i<means.size(); i++){
			double distance = 0.0;
			Set<Integer> keys = instance.getFeatureVector().getVector().keySet();
			Set<Integer> unionkeys = new HashSet<Integer>(keyset);
			unionkeys.addAll(means.get(i).getVector().keySet());
			for(Integer key: unionkeys){
				distance = distance + 
						(instance.getFeatureVector().get(key)-means.get(i).get(key))
						*(instance.getFeatureVector().get(key)-means.get(i).get(key));
			}
			if(distance<min){
				min = distance;
				labelValue = i;
			}			
		}
		ClassificationLabel label = new ClassificationLabel(labelValue);
		return label;
	}
	
	public String getpreName(){
		return this.prename;
	}

	public ClassificationLabel getLabel(){
		return null;
	}
}
