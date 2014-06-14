package cs475.chainMRF;
import cs475.chainMRF.*;
import java.util.ArrayList;

public class MaxSum {

	private ChainMRFPotentials potentials;
	private int[] assignments;
	private int nlength = 0;
	private int kvalue = 0;
	private SumProduct sp;

	public MaxSum(ChainMRFPotentials p) {
		this.potentials = p;
		assignments = new int[p.chainLength()+1];
		this.nlength = p.chainLength();
		this.kvalue = p.numXValues();
		this.sp = new SumProduct(p);
	}
	
	public int[] getAssignments() {
		return assignments;
	}
	
	public double maxProbability(int x_i) {
		int n = nlength;
		ArrayList<double[]> msgtable = new ArrayList<double[]>();
		ArrayList<double[]> lefttable = new ArrayList<double[]>();
		ArrayList<double[]> righttable = new ArrayList<double[]>();
		double[] result = new double[kvalue+1];
		result[0] = 0.0;
		double maxvalue = 0.0;
		int maxposition = 1;
		if(x_i == 1){
			msgtable = facVar(n+x_i,x_i);
			double[] tmp = msgtable.get(0);
			for(int m = 1;m<kvalue+1;m++){
				result[m] = Math.log(potentials.potential(x_i, m))+tmp[m];
			}
		}
		else if(x_i == n){
			msgtable = facVar(2*x_i-1,x_i);
			double[] tmp = msgtable.get(0);
			for(int m = 1;m<kvalue+1;m++){
				result[m] = Math.log(potentials.potential(x_i, m))+tmp[m];
			}
		}
		else{
			lefttable = facVar(n+x_i-1,x_i);
			righttable = facVar(n+x_i,x_i);
			double[] tmp1 = lefttable.get(0);
			double[] tmp2 = righttable.get(0);
			for(int m = 1;m<kvalue+1;m++){
				result[m] = Math.log(potentials.potential(x_i, m))+tmp1[m]+tmp2[m];
			}
		}
		maxvalue = result[1];
		double normCont = normalize(x_i);
		for(int m = 1; m<kvalue+1;m++){
//			System.out.println(result[m]);
			if(result[m]>maxvalue){
				maxvalue = result[m];
//				System.out.println("change maxp from " + maxposition + " to " + m);
				maxposition = m;
				
			}
		}
//		System.out.println("ts: " + msgtable.size());
//		System.out.println("ls: " + lefttable.size());
//		System.out.println("rs: " + righttable.size());
//		
		assignments[x_i] = maxposition;
		if((x_i!=1)&&(x_i!=n)){			
			for(int i = x_i-1;i>0;i--){
				int lastposition = assignments[i+1];
				assignments[i] = (int)lefttable.get(i)[lastposition];
			}
			for(int i = x_i+1; i<=n;i++){
				int lastposition = assignments[i-1];
				assignments[i] = (int)righttable.get(righttable.size()+x_i-i)[lastposition];
			}
		}
		else if(x_i ==1){
			for(int i = x_i+1; i<=n;i++){
				int lastposition = assignments[i-1];
				assignments[i] = (int)msgtable.get(msgtable.size()+x_i-i)[lastposition];
			}
		}
		else if(x_i == n){
			for(int i = x_i-1;i>0;i--){
				int lastposition = assignments[i+1];
				assignments[i] = (int)msgtable.get(i)[lastposition];
			}
		}		      
		double max = Math.exp(maxvalue)/normCont;
		return Math.log(max);
	}
	
//Get the ith leaf factor value
	private ArrayList<double[]> factor(int i){
//		System.out.println("return fac_"+i);
		ArrayList<double[]> retable = new ArrayList<double[]>();
		if(i>nlength){
			System.out.println("Warning: request factor "+i+" with total length = " + nlength);
		}
		double [] result = new double[kvalue+1];
		for(int j = 1; j <kvalue+1;j++){
			result[j] = Math.log(potentials.potential(i, j));
		}
		retable.add(result);
		return retable;
	}
	
//Get the message sent from ath factor to bth variable
	private ArrayList<double[]> facVar(int a, int b){
		double [] result = new double[kvalue+1];
		if(a == b){
			return factor(a);
		}
		else if(a-nlength!=b){
//			System.out.println("-> Returning from facVar " + a + " " + b );
			ArrayList<double[]> msgtable = varFac(a-nlength,a);
			double[] msg = msgtable.get(0);
			double tmp;
			double [] curstate = new double[kvalue+1];
			curstate[0] = b;
			for(int k =1;k<kvalue+1;k++){ //compute for variable b
				double max = Math.log(potentials.potential(a, 1, k))+msg[1];
				curstate[k] = 1;
				for(int m = 1;m<kvalue+1;m++){//to find max on vraiable a-n
					tmp = Math.log(potentials.potential(a, m, k))+msg[m];
					if(tmp > max){
						max = tmp;
						curstate[k] = m;
					}
				}
				result[k] = max;
			}
			msgtable.set(0, result);
			msgtable.add(curstate);
			return msgtable;
		}
		else if(a-nlength == b){
//			System.out.println("<- Returning from facVar " + a + " " + b );
			ArrayList<double[]> msgtable = varFac(b+1,a);
			double[] msg = msgtable.get(0);
			double tmp;
			double [] curstate = new double[kvalue+1];
			for(int k = 1; k <kvalue+1;k++){
				double max = Math.log(potentials.potential(a, k, 1))+msg[1];
				curstate[k] = 1;
				for(int m = 1; m<kvalue+1;m++){
					tmp = Math.log(potentials.potential(a, k, m))+msg[m];
					if(tmp > max){
						max = tmp;
						curstate[k] = m;
					}
				}
				result[k] = max;
			}
			msgtable.set(0,result);
			msgtable.add(curstate);
			return msgtable;
		}	
		return null;	
	}
	
	//Get the message sent from ath variable to bth factor
	private ArrayList<double[]> varFac(int a, int b){
		double result[] = new double[kvalue+1];
		ArrayList<double[]> msgtable = new ArrayList<double[]>();
		if((a==1)||(a==nlength)){
			msgtable = factor(a);
			return msgtable;
		}
		else if(a+nlength == b){
//			System.out.println(nlength);
//			System.out.println("-> Returning from varFac " + a + " " + b );
			msgtable = facVar(b-1,a);
			double[] tmp = msgtable.get(0);
			for(int m = 1; m< kvalue+1;m++){
				result[m] = Math.log(potentials.potential(a, m))+tmp[m];
			}
			msgtable.set(0, result);
			return msgtable;
		}
		else if(a+nlength != b){
//			System.out.println("<- Returning from varFac " + a + " " + b );
			msgtable = facVar(b+1,a);
			double[] tmp = msgtable.get(0);
			for(int m = 1;m<kvalue+1;m++){
				result[m] = Math.log(potentials.potential(a, m))+tmp[m];
			}
			msgtable.set(0, result);
			return msgtable;
		}
		return msgtable;
	}
	
	private double normalize(int xi){
		sp.marginalProbability(xi);
		double sum = sp.getUnnormed()[xi];
//		System.out.println(sum);
		return sum;
	}

	
	
}
