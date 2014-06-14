package cs475.chainMRF;
import cs475.chainMRF.*;

public class SumProduct {

	private ChainMRFPotentials potentials;
	// add whatever data structures needed
	private int nlength = 0;
	private int kvalue = 0;
	private double[] unnormed;
	
	public SumProduct(ChainMRFPotentials p) {
		this.potentials = p;
		this.nlength = p.chainLength();
		this.kvalue = p.numXValues();
		this.unnormed = new double[nlength+1];
	}
	public double[] getUnnormed(){
		return this.unnormed;
	}
	
	public double[] marginalProbability(int x_i) {
		int n = nlength;
		double[] result = new double[kvalue+1];
		result[0] = 0.0;
		if(x_i == 1){
			double[] tmp = facVar(n+x_i,x_i);
			for(int m = 1;m<kvalue+1;m++){
				result[m] = potentials.potential(x_i, m)*tmp[m];
			}
		}
		else if(x_i == n){
			double[] tmp = facVar(2*x_i-1,x_i);
			for(int m = 1;m<kvalue+1;m++){
				result[m] = potentials.potential(x_i, m)*tmp[m];
			}
		}
		else{
			double[] tmp1 = facVar(n+x_i-1,x_i);
			double[] tmp2 = facVar(n+x_i,x_i);
			for(int m = 1;m<kvalue+1;m++){
				result[m] = potentials.potential(x_i, m)*tmp1[m]*tmp2[m];
			}
		}
		double tmp = .0;
		for(int i = 1;i < result.length;i++){
			 tmp += result[i];
		}
		this.unnormed[x_i] = tmp;
		normalize(result);
		return result;
	}
	
	//Get the ith leaf factor value
	private double[] factor(int i){
		if(i>nlength){
			System.out.println("Warning: request factor i++");
		}
//		System.out.println("Get f_"+i);
		double [] result = new double[kvalue+1];
		for(int j = 1; j <kvalue+1;j++){
			result[j] = potentials.potential(i, j);
		}
		return result;
	}
	
	//Get the message sent from ath factor to bth variable
	private double[] facVar(int a, int b){
//		System.out.println("Fac " + a +" to Var " + b);
		double [] result = new double[kvalue+1];
		if(a == b){
//			System.out.println("leaf factor Returning from facVar " + a + " " + b );
			return factor(a);
		}
		else if(a-nlength!=b){
//			System.out.println("-> Returning from facVar " + a + " " + b );
			double[] tmp = varFac(a-nlength,a);
			for(int k =1;k<kvalue+1;k++){
				for(int m = 1;m<kvalue+1;m++){
					result[k] += potentials.potential(a, m, k)*tmp[m];
				}
			}
			return result;
		}
		else if(a-nlength == b){
//			System.out.println("<-Returning from facVar " + a + " " + b );
			double[] tmp = varFac(b+1,a);
			for(int k = 1; k <kvalue+1;k++){
				for(int m = 1; m<kvalue+1;m++){
					result[k] += potentials.potential(a, k, m)*tmp[m];
				}
			}
			return result;
		}	
		return result;	
	}
	
	//Get the message sent from ath variable to bth factor
	private double[] varFac(int a, int b){
//		System.out.println("Var " + a +" to Fac " + b);
		double result[] = new double[kvalue+1];
		if((a==1)||(a==nlength)){
			result = factor(a);
//			System.out.println("result from factor(a): " + result[1]+" " + result[2]);
			return result;
		}
		else if(a+nlength == b){
			double[] tmp = facVar(b-1,a);
			for(int m = 1; m< kvalue+1;m++){
				result[m] = facVar(a,a)[m]*tmp[m];
			}
//			System.out.println("a+n==b");
			return result;
		}
		else if(a+nlength != b){
			double[] tmp = facVar(b+1,a);
			for(int m = 1;m<kvalue+1;m++){
				result[m] = facVar(a,a)[m]*tmp[m];
			}
//			System.out.println("a+n!=b");
			return result;
		}
		return result;
	}
	
	private double[] normalize(double[] unnormed){
		double sum = 0.0;
		for(int i = 1; i<unnormed.length;i++){
			sum += unnormed[i];
		}
		for(int i = 1; i<unnormed.length;i++){
			double tmp = unnormed[i]/sum;
			unnormed[i] = tmp;
		}
		return unnormed;
	}
}