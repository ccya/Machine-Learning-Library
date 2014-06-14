package cs475;

import java.io.Serializable;

public class ClassificationLabel extends Label implements Serializable {
	
	private int labelvalue;
	public ClassificationLabel(int label) {
		// TODO Auto-generated constructor stub
		labelvalue = label;
	}
	
	public int getLabelValue(){
		return labelvalue;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
//		return null;
		String labstr;
		labstr = Integer.toString(labelvalue);
		return labstr;
	}

}
