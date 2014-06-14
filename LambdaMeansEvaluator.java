package cs475;

import java.util.List;
import java.util.HashMap;

public class LambdaMeansEvaluator extends Evaluator {
	
	private double evaluatorValue = 0.0;
	public double evaluate(List<Instance> instances, Predictor predictor){
		HashMap<Integer,Integer> countA = new HashMap<Integer, Integer>();
		HashMap<Integer,Integer> countB = new HashMap<Integer, Integer>();
		HashMap<Integer,HashMap<Integer,Integer>> countAB = new HashMap<Integer, HashMap<Integer,Integer>>();
		HashMap<Integer,Double> PA = new HashMap<Integer, Double>();
		HashMap<Integer,Double> PB = new HashMap<Integer, Double>();
		double ha = 0.0;
		double hb = 0.0;
		double mi = 0.0;
		LambdaMeans lm = (LambdaMeans)predictor;
		
		
		for(int i = 0; i< instances.size();i++){
//			System.out.println("--------------------instance " + i);
			Instance tmp = instances.get(i);
			ClassificationLabel reallabel = (ClassificationLabel)tmp.getLabel();
			ClassificationLabel predictlabel = lm.predict(tmp);
			if(reallabel == null){
				return 0.0;
			}
			int a = reallabel.getLabelValue();
			int b = predictlabel.getLabelValue();
			if(!countA.containsKey(a)){
				countA.put(a, 0);
			}
			if(!countB.containsKey(b)){
				countB.put(b, 0);
			}
			HashMap<Integer,Integer> tmphash = new HashMap<Integer,Integer>();
			if(!countAB.containsKey(a)){
				countAB.put(a, tmphash);
			}
			if(!countAB.get(a).containsKey(b)){
				countAB.get(a).put(b, 0);
			}
			int countavalue = countA.get(a) + 1;
			countA.put(a,countavalue);
			int countbvalue = countB.get(b) + 1;
			countB.put(b,countbvalue);
			int countabvalue = countAB.get(a).get(b) + 1;
			countAB.get(a).put(b, countabvalue);			
		}
//		out.close();
		for(Integer key : countA.keySet()){
			double tmp = ((double) countA.get(key))/instances.size();
			PA.put(key, tmp);
			if(tmp>0){
				ha = ha - tmp*Math.log(tmp);
			}
		}
		
		for(Integer key : countB.keySet()){
			double tmp = ((double) countB.get(key))/instances.size();
			PB.put(key, tmp);
			if(tmp>0){
				hb = hb - tmp*Math.log(tmp);
			}
		}

		for(Integer keya : countA.keySet()){
			for(Integer keyb: countB.keySet()){
				if(!countAB.get(keya).containsKey(keyb)){
					countAB.get(keya).put(keyb, 0);
				}
				double tmp = ((double)countAB.get(keya).get(keyb))/instances.size();
				if(tmp>0){
					mi = mi+ tmp*Math.log(tmp/PA.get(keya)*PB.get(keyb));
				}
			}
		}
		evaluatorValue = ha + hb - 2.0*mi;
//		System.out.println(evaluatorValue);
		return evaluatorValue;
	}
}