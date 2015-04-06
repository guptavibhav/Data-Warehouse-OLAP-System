package edu.buffalo.datamining.queries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import com.google.common.math.DoubleMath;
import com.google.common.primitives.Doubles;

import edu.buffalo.datamining.services.DBOperations;

public class CorrelationTest {

	private String disease1;
	private String disease2;
	private int goIdValue;
	
	public CorrelationTest(String disease1, String disease2, int goIdValue) {
		this.disease1=disease1;
		this.disease2=disease2;
		this.goIdValue=goIdValue;
	}
	
	
	public static void main(String args[]){
		CorrelationTest tst=new CorrelationTest("ALL","AML",7154);
		tst.evaluateAverageCorrelation();
	}

	public String evaluateAverageCorrelation(){
		String averageCorrelation=null;
		DBOperations operations=new DBOperations();
		ArrayList<Double>corr=new ArrayList<>();
		double correlation=-2;
		try{
		
		HashMap<Integer,ArrayList<Integer>>disease1Values=operations.getExpressionValuesPerPatient(disease1, goIdValue);
		HashMap<Integer,ArrayList<Integer>>disease2Values=null;
		if (disease1.equalsIgnoreCase(disease2)){
			disease2Values=operations.getExpressionValuesPerPatient(disease2, goIdValue);
		}
		;
		
		for(Map.Entry<Integer,ArrayList<Integer>> disease1Value : disease1Values.entrySet()){
			ArrayList<Integer>values1=disease1Value.getValue();
			double[]geneExpressionValuesDisease1=Doubles.toArray(values1);
			for(Map.Entry<Integer,ArrayList<Integer>> disease2Value : disease2Values.entrySet()){
				if ( (disease1.equalsIgnoreCase(disease2)) && (disease1Value.getKey().equals(disease2Value.getKey()))){
					continue;
				}
				
				ArrayList<Integer>values2=disease2Value.getValue();
				double[]geneExpressionValuesDisease2=Doubles.toArray(values2);
				PearsonsCorrelation correlations=new PearsonsCorrelation();
				double val=correlations.correlation(geneExpressionValuesDisease1, geneExpressionValuesDisease2);
				corr.add(Double.valueOf(val));
			}
		}
		
		double cor[]=Doubles.toArray(corr);
		correlation=DoubleMath.mean(cor);
		averageCorrelation=String.valueOf(correlation);
		System.out.println(averageCorrelation);
		}catch(Exception e){
			e.printStackTrace();
		}
		return averageCorrelation;
	}
	
	
	public String getDisease1() {
		return disease1;
	}

	public void setDisease1(String disease1) {
		this.disease1 = disease1;
	}

	public String getDisease2() {
		return disease2;
	}

	public void setDisease2(String disease2) {
		this.disease2 = disease2;
	}

	public int getGoIdValue() {
		return goIdValue;
	}

	public void setGoIdValue(int goIdValue) {
		this.goIdValue = goIdValue;
	}


}
