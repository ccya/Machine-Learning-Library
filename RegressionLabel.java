package cs475;

import java.io.Serializable;

public class RegressionLabel extends Label implements Serializable {

	double labvalue;
	public RegressionLabel(double label) {
		// TODO Auto-generated constructor stub
		labvalue = label;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
//		return null;
		String labstr;
		labstr = Double.toString(labvalue);
		return labstr;
	}

}
