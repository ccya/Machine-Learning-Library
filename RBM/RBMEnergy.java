package cs475.RBM;
import cs475.RBM.*;
import java.util.Random;
import java.util.*;

public class RBMEnergy {
	private RBMParameters _parameters;
	private int m = 0;
	private int n = 0;
	private int iterations = 0;
	private ArrayList<double[]> hsamples = new ArrayList<double[]>();
	private ArrayList<double[]> xsamples = new ArrayList<double[]>();

	public RBMEnergy(RBMParameters parameters, int numSamples) {
		this._parameters = parameters;
		this.m = _parameters.numVisibleNodes();
		this.n = _parameters.numHiddenNodes();
		this.iterations = numSamples;
	}
	
	public double computeMarginal() {
		double[] hbias = new double[n];
		double[] obias = new double[m];
		double[] hvector = new double[n];
		double[] xvector = new double[m];
		Random sample = new Random(0);
		
		for(int i =0; i<m;i++){
			xvector[i] = _parameters.visibleNode(i);
			obias[i] = _parameters.visibleBias(i);
		}
		for(int j = 0; j<n;j++){
			hbias[j] = _parameters.hiddenBias(j);
		}
		
		for(int k =0; k<iterations;k++){
			
//			System.out.println("-----------"+k+"th iteration ------------");
			double xexpression;
			double hexpression;
			double ph = 0.0;
			double px = 0.0;
			//Sample H vector 
			for(int j = 0; j< n;j++){
				xexpression = 0.0;
				for(int i = 0; i<m; i++){
					xexpression += xvector[i]*_parameters.weight(i, j);
				}
				xexpression += hbias[j];
//				System.out.println("xexpression: " + xexpression);
				ph = this.sigmoid(xexpression);
//				System.out.println("ph: " + ph);
				double u = sample.nextDouble();
//				System.out.println("u: " + u);
				hvector[j] = ph>u ? 0:1;
//				System.out.println("h." + j+ ": "+ hvector[j]);
			}
			double[] newh = new double[n];
			for(int j =0; j<n;j++){
				newh[j] = hvector[j];
			}
			hsamples.add(newh);
//Sample X vector 
			for(int i = 0; i < m;i++){
				hexpression = 0.0;
				for(int j = 0;j<n;j++){
					hexpression += hvector[j]*_parameters.weight(i, j);
				}
				hexpression += obias[i];
//				System.out.println("hexpression: " + hexpression);
				px = this.sigmoid(hexpression);
//				System.out.println("px: " + px);
				double u = sample.nextDouble();
//				System.out.println("u: " + u);
				xvector[i] = px>u ? 0:1;
//				System.out.println("x." + i+ ": "+ xvector[i]);
			}
			double[] newx = new double[m];
			for(int i =0; i<m;i++){
				newx[i] = xvector[i];
			}
			xsamples.add(newx);
		}
		
		int count = 0;
		for(int i = 0; i<xsamples.size();i++){
			boolean equal = true;
			double[] x = xsamples.get(i);
			for(int j = 0;j<x.length;j++){
				if(x[j] != _parameters.visibleNode(j)){
					equal = false;
				}
			}
			count = (equal== true)?(count+1):count;
		}
		double result = ((double) count)/iterations;
		return result;
	}
	public double sigmoid(double i){
		return 1-1/(1+Math.exp(-i));
	}
}
