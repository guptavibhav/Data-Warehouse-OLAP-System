package edu.buffalo.datamining.queries;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math3.stat.inference.OneWayAnova;

import edu.buffalo.datamining.services.DBOperations;

public class FTest {

	private String disease1;
	private String disease2;
	private int goIdValue;

	public FTest(String disease1, String disease2, int goIdValue) {
		this.disease1 = disease1;
		this.disease2 = disease2;
		this.goIdValue = goIdValue;
	}

	public static void main(String args[]) {
		FTest tst = new FTest("ALL", "AML", 7154);
		tst.evaluate();

	}

	public String evaluate() {

		String result = null;

		try {
			if (disease2 != null && disease2.equalsIgnoreCase("AllOthers")) {
				disease2 = " <> '" + disease1;
			} else {
				disease2 = " = '" + disease2;
			}

			DBOperations operations = new DBOperations();
			double[] expressionValues1 = operations
					.StatisticsQueryForTAndFTest(disease1, goIdValue);
			double[] expressionValues2 = operations
					.StatisticsQueryForTAndFTest(disease2, goIdValue);

			Collection<double[]> expressionValueCollection = new ArrayList<>();
			expressionValueCollection.add(expressionValues1);
			expressionValueCollection.add(expressionValues2);
			OneWayAnova anova = new OneWayAnova();
			double resultValue = anova.anovaFValue(expressionValueCollection);
			result = String.valueOf(resultValue);
			System.out.println(result);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
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
