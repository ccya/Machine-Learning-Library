package cs475;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;  
import java.math.*;
import java.util.Map.Entry;


public class DualPerceptron extends Predictor {

	private double online_learning_rate = 1; 
	private String preName = "perceptron_linear_kernel";
	private int online_training_iterations = 1;
	private boolean isLinear = false;
	private boolean isPoly = false;
	private double d = 2;
	private List<Instance> trainingset = new ArrayList<Instance>();
	Double[][] gram;
	HashMap<Integer,Double> alphaVector = new HashMap<Integer,Double>();
	
	public DualPerceptron(int online_training_iterations, double online_learning_rate,
							boolean isLinear, boolean isPoly, double d, List<Instance> instances){
		this.online_learning_rate = online_learning_rate;
		this.online_training_iterations = online_training_iterations;	
		this.d = d;
		this.isLinear = isLinear;
		this.isPoly = isPoly;
		if((online_learning_rate>1)||(online_learning_rate<0)){
			System.out.println("The online_learning_rate must in (0,1]");
		}
		if(isLinear){
			gram = this.getLinearGram(instances);
		}
		else if(isPoly){
			gram = this.getPolyGram(instances,d);
		}
	}
	
	public void train(List<Instance> instances){
//		System.out.println(online_training_iterations);
		for(int i = 0 ;i <instances.size();i++){
			alphaVector.put(i, 0.0);
		}
		for(int i = 0; i< instances.size(); i++){
			this.trainingset.add(instances.get(i));
		}
//		System.out.println("trainingset: " + trainingset.size());
		for(int k = 0; k <online_training_iterations;k++){
//			System.out.println("---------"+ k+"th Iteration---------");
			for(int i = 0;i<instances.size();i++){
				double wx = .0;
				double alpha_i = alphaVector.get(i);
				ClassificationLabel labeli = (ClassificationLabel) instances.get(i).getLabel();
				int yi = (labeli.getLabelValue() == 0 ? -1 : 1);
				for(int j = 0;j<instances.size();j++){
					double alpha_j = alphaVector.get(j);
					ClassificationLabel label = (ClassificationLabel) instances.get(j).getLabel();
					int yj = label.getLabelValue();
					if(yj == 0){
						yj = -1;
					}
					wx += alpha_j*yj*gram[i][j];
				}
				if(wx*yi < 1){
					alpha_i +=1;
					alphaVector.put(i, alpha_i);
				}
			}
		}
	}
	
	private Double[][] getLinearGram(List<Instance> instances){
//		System.out.println("Computing G Matrix...");
		Double[][] gram = new Double[instances.size()][instances.size()];
		int numFtre = numAllFtre(instances);
		for(int i = 0; i < instances.size(); i++){
			Double[] row = new Double[instances.size()];
			for(int j = 0; j< instances.size(); j++){
				double value = 0.0;
				Instance tmpi = instances.get(i);
				Instance tmpj = instances.get(j);
				for(Entry<Integer,Double> entry:tmpi.getFeatureVector().getVector().entrySet()){
					value += entry.getValue()*tmpj.getFeatureVector().get(entry.getKey());
				}
//				for(int k = 1; k<=numFtre; k++){
//					value += tmpi.getFeatureVector().get(k)*tmpj.getFeatureVector().get(k);
//				}
				row[j]=(value);
			}
			gram[i] =(row);
		}
//		System.out.println("Finished computing");
		return gram;
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
	
	private Double[][] getPolyGram(List<Instance> instances, double d){
//	value = Math.pow((1+xx), d);
//		System.out.println("Computing G Matrix...");
		Double[][] lgram = this.getLinearGram(instances);
		Double[][] gram = new Double[instances.size()][instances.size()];
		int numFtre = numAllFtre(instances);
		for(int i = 0; i < instances.size(); i++){
			Double[] row = new Double[instances.size()];
			for(int j = 0; j< instances.size(); j++){
				double value = 0.0;
				value = Math.pow((1+lgram[i][j]), d);
				row[j] = value;
			}
			gram[i] = row;
		}
		return gram;	
	}
	
	public ClassificationLabel predict(Instance instance,
			boolean isLinear, boolean isPoly, double d
		){
		List<Instance> instances = this.trainingset;
		int preLabel = 0;
		double tmp = .0;
		int allFtre = this.numAllFtre(instances);
		Double[] gramp = new Double[instances.size()];

		
		if(isLinear){
//			System.out.println("Computin gram...");
			for(int i = 0; i<instances.size();i++){
				double value = 0.0;
				Instance ins = instances.get(i);
//				for(int j =1;j<=allFtre;j++){
//					value += ins.getFeatureVector().get(j)*instance.getFeatureVector().get(j);
//				}
				for(Entry<Integer,Double> entry:instance.getFeatureVector().getVector().entrySet()){
					value += entry.getValue()*ins.getFeatureVector().get(entry.getKey());
				}
				gramp[i] = value;
			}
		}
		else if(isPoly){
			for(int i = 0; i<instances.size();i++){
				double value = 0.0;
				Instance ins = instances.get(i);
				for(Entry<Integer,Double> entry:instance.getFeatureVector().getVector().entrySet()){
					value += entry.getValue()*ins.getFeatureVector().get(entry.getKey());
				}
				value = Math.pow((1+value), d);
				gramp[i] = value;
			}
		}		
		for (int i = 0; i< instances.size();i++){
			ClassificationLabel label = (ClassificationLabel)instances.get(i).getLabel();
			int yi = label.getLabelValue();
			if(yi ==0){
				yi = -1;
			}
			tmp += this.alphaVector.get(i)*gramp[i]*yi;
		}
		if(tmp >= 0){
			preLabel = 1;
		}
		else preLabel = 0;
		ClassificationLabel Label = new ClassificationLabel(preLabel);
		return Label;	
	}

	public Label predict(Instance instance){
		return null;
	}
	
	public  Label getLabel(){
		return null;
	}
	public  String getpreName(){
		return this.preName;
	}
	public boolean isLinear(){
		return this.isLinear;
	}
	
	public double dvalue(){
		return this.d;
	}
}

