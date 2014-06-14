package cs475;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;  
import java.math.*;


public class FeatureItem implements Comparable<FeatureItem>{
	private Integer fid;
	private Double fvalue;
	
	public FeatureItem(int fid, double fvalue){
		this.fid = fid;
		this.fvalue = fvalue;
	}
	
	public Double getValue(){
		return this.fvalue;
	}
	
	public int getId(){
		return this.fid;
	}
	
	public int compareTo(FeatureItem fi){
		int value = this.fvalue.compareTo(fi.getValue());
		return value;
	}	
}