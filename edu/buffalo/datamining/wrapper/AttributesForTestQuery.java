package edu.buffalo.datamining.wrapper;

import java.io.Serializable;
import java.util.ArrayList;

public class AttributesForTestQuery implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Integer>goValues;
	private ArrayList<String>diseaseNames;
	
	public AttributesForTestQuery(ArrayList<Integer>goValues,ArrayList<String>diseaseNames) {
		this.goValues=goValues;
		this.diseaseNames=diseaseNames;
	}

	public ArrayList<String> getDiseaseNames() {
		return diseaseNames;
	}

	public void setDiseaseNames(ArrayList<String> diseaseNames) {
		this.diseaseNames = diseaseNames;
	}

	public ArrayList<Integer> getGoValues() {
		return goValues;
	}

	public void setGoValues(ArrayList<Integer> goValues) {
		this.goValues = goValues;
	}

}
