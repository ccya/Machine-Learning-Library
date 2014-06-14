package cs475.RBM;

import java.io.BufferedReader;
import java.io.FileReader;

public class RBMParameters {

	private double[][] _weights;
	private double[] _visibleNodes;
	private double[] _visibleBias;
	private double[] _hiddenBias;
	private int m, n;

	public RBMParameters(String data_file) {
		int line_num = 1;
		try {
			BufferedReader br = new BufferedReader(new FileReader(data_file));
			String line = br.readLine();
			String[] ar = line.split("\\s+");
			m = Integer.parseInt(ar[0]);
			n = Integer.parseInt(ar[1]);
			_weights = new double[m][n];
			_visibleNodes = new double[m];
			_visibleBias = new double[m];
			_hiddenBias = new double[n];
			
			for(int i=0; i<m; i++) { 
				_visibleNodes[i] = 0.0;
				_visibleBias[i] = 0.0;
			}
			for(int i=0; i<n; i++) { 
				_hiddenBias[i] = 0.0;
			}
			for(int i=0; i<m; i++) { 
				for(int j=0; j<n; j++) { 
					_weights[i][j] = 0.0;
				}
			}
			line_num++;

			while((line = br.readLine()) != null) {
				ar = line.split("\\s+");
				//System.out.println(ar[0]);
				if(ar.length == 1){
					if(line_num < m+2){
						_visibleNodes[line_num-2] = Double.parseDouble(ar[0]);
					}
					else if(line_num >= m+2 && line_num < 2*m+2){
						_visibleBias[line_num-m-2] = Double.parseDouble(ar[0]);
					}
					else if(line_num >= 2*m+2 && line_num < 2*m+n+2){
						_hiddenBias[line_num-2*m-2] = Double.parseDouble(ar[0]);
					}
					else{
						throw new Exception("Unexpected value at line number: " + line_num);
					}
				}
				else if(ar.length == n) {
					int i = line_num-2*m-n-2;
					for(int j=0; j<n; j++) { 
						_weights[i][j] = Double.parseDouble(ar[j]);
					}
				}
				else {
					continue;
				}
				line_num++;
			
			}
			br.close();
		} catch(Exception e) {
			throw new RuntimeException("error while reading parameter file: " + data_file
				+ " on line " + line_num + " [" + e.getMessage() + "]");
		}

	}
	
	public int numHiddenNodes() { return n; }

	public int numVisibleNodes() { return m; }

	
	public double visibleNode(int index) {
		if(index < 0 || index >= m)
			throw new RuntimeException("given m=" + m + ", illegal value for index: " + index);
		return _visibleNodes[index];
	}

	public double visibleBias(int index) {
		if(index < 0 || index >= m)
			throw new RuntimeException("given m=" + m + ", illegal value for index: " + index);
		return _visibleBias[index];
	}
	
	public double hiddenBias(int index) {
		if(index < 0 || index >= n)
			throw new RuntimeException("given n=" + n + ", illegal value for index: " + index);
		return _hiddenBias[index];
	}
	
	public double weight(int i, int j) {
		if(i < 0 || i >= m)
			throw new RuntimeException("given m=" + m + ", illegal value for i: " + i);
		if(j < 0 || j >= n)
			throw new RuntimeException("given n=" + n + ", illegal value for j: " + j);
		return _weights[i][j];
	}
}
