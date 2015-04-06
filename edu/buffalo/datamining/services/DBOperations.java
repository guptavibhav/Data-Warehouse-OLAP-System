package edu.buffalo.datamining.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.primitives.Doubles;

import edu.buffalo.datamining.wrapper.AttributesForTestQuery;
import edu.buffalo.datamining.wrapper.TableColumnNamesPair;

public class DBOperations {

	private static Connection connection = null;

	public static Connection getConnection() {
		try {

			if (connection == null) {

				Class.forName("oracle.jdbc.driver.OracleDriver");

				connection = DriverManager
						.getConnection(
								"jdbc:oracle:thin:@aos.acsu.buffalo.edu:1521/aos.buffalo.edu",
								"keyurjos", "cse601");
				System.out.println("Connection Established successfully");
			}

		} catch (ClassNotFoundException e) {
			System.out.println("Oracle JDBC Driver not found");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out
					.println("Connection Failed Please verify your connectivity to host");
			e.printStackTrace();
		}
		return connection;
	}

	public ArrayList<TableColumnNamesPair> getAllColumnNames() {
		ArrayList<TableColumnNamesPair> names = new ArrayList<>();
		String getAllColumnNames = "Select TABLE_NAME,COLUMN_NAME from USER_TAB_COLUMNS";
		try {
			Connection conn = DBOperations.getConnection();
			if (conn != null) {
				PreparedStatement ps = conn.prepareStatement(getAllColumnNames);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					String tableName = rs.getString("TABLE_NAME");
					String columnName = rs.getString("COLUMN_NAME");
					TableColumnNamesPair pair = new TableColumnNamesPair(
							tableName, columnName);
					names.add(pair);

				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return names;
	}

	public AttributesForTestQuery getTestInfo(){
		ArrayList<String> names = new ArrayList<>();
		ArrayList<Integer>goValues=new ArrayList<>();
		AttributesForTestQuery info=null;
		String getDiseaseNames = "Select NAME from Disease";
		String getGoValues="Select GO_ID from GO UNION Select GO_ID from gene_fact";
		try {
			Connection conn = DBOperations.getConnection();
			if (conn != null) {
				PreparedStatement ps = conn.prepareStatement(getDiseaseNames);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					String diseaseName = rs.getString("NAME");
					names.add(diseaseName);

				}
				
				ps=conn.prepareStatement(getGoValues);
				rs=ps.executeQuery();
				while(rs.next()){
				int goValue=rs.getInt("GO_ID");	
				goValues.add(goValue);
				}
				
				info=new AttributesForTestQuery(goValues,names);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return info;
	}	
	
	
	public double[] StatisticsQueryForTAndFTest(String diseaseName,int goId) {

		double disease[] = null;
		try {
			Connection conn = DBOperations.getConnection();
			if (conn != null) {
				
				String sqlQueryDiseaseExpressionValues = "select exp from microarray_fact where s_id in (select s_id from clinicalsample,diagnosis where clinicalsample.s_id IS NOT null AND clinicalsample.p_id=diagnosis.p_id AND ds_id in (Select ds_id from DISEASE where name = '"+ diseaseName+"'))AND pb_id in (select pb_id from goannotation,probe where goannotation.go_id="+goId+" AND goannotation.uid1=probe.uid1)";
				System.out.println(sqlQueryDiseaseExpressionValues);
			
				PreparedStatement ps = conn.prepareStatement(sqlQueryDiseaseExpressionValues);
				ResultSet rs = ps.executeQuery();

				ArrayList<Double> diseaseExpressions = new ArrayList<>();
				while (rs.next()) {
					double value = rs.getDouble("EXP");
					diseaseExpressions.add(value);
				}

				// Second Disease
				disease = Doubles.toArray(diseaseExpressions);
				

			}
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return disease;
	}

	
	public HashMap<Integer,ArrayList<Integer>> getExpressionValuesPerPatient(String diseaseName,int goIdValue){
		
		HashMap<Integer,ArrayList<Integer>>expressionValuesPerPatient=new HashMap<>();
		String sqlQueryALL = "SELECT microarray_fact.EXP,diagnosis.P_ID FROM disease,diagnosis, clinicalsample, microarray_fact, goannotation, probe WHERE diagnosis.DS_ID =disease.DS_ID AND diagnosis.P_ID = clinicalsample.P_ID AND microarray_fact.PB_ID = probe.PB_ID AND clinicalsample.S_ID = microarray_fact.S_ID AND goannotation.UID1 = probe.UID1 AND disease.NAME='"+diseaseName+"' AND goannotation.GO_ID = "+goIdValue +" ORDER BY diagnosis.P_ID";
		try {
			Connection conn = DBOperations.getConnection();
			if (conn != null) {
				PreparedStatement ps = conn.prepareStatement(sqlQueryALL);
				ResultSet rs = ps.executeQuery();
				
				while (rs.next()) {
					
					int value=rs.getInt("EXP");
	                int person=rs.getInt("P_ID");
	                
	                if (!expressionValuesPerPatient.containsKey(person)){
	                	ArrayList<Integer>tmp=new ArrayList<>();
	                	tmp.add(value);
	                	expressionValuesPerPatient.put(person,tmp);
	                }else{
	                	ArrayList<Integer>tmp=expressionValuesPerPatient.get(person);
	                	tmp.add(value);
	                }
				}
				
			} 
		}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	finally{
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		return expressionValuesPerPatient;
	}
	
	
	public int patientCountByDiseaseDescription(String diseaseDescription){
		int count = 0;
		String sqlQueryPatientsByDescription="select count(p_id) AS count from diagnosis,disease where diagnosis.ds_id=disease.ds_id AND disease.DESCRIPTION='"+diseaseDescription+"'";
		try {
			Connection conn = DBOperations.getConnection();
			if (conn != null) {
				PreparedStatement ps = conn.prepareStatement(sqlQueryPatientsByDescription);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					count = rs.getInt("COUNT");
					
				}
				
			}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return count;
	}
	
	
	public int patientCountByDiseaseType(String diseaseType){
		int count = 0;
		String sqlQueryPatientsByDescription="select count(p_id) AS count from diagnosis,disease where diagnosis.ds_id=disease.ds_id AND disease.TYPE='"+diseaseType+"'";
		try {
			Connection conn = DBOperations.getConnection();
			if (conn != null) {
				PreparedStatement ps = conn.prepareStatement(sqlQueryPatientsByDescription);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					count = rs.getInt("COUNT");
					
				}
				
			}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return count;
	}
	
	public int patientCountByDiseaseName(String diseaseName){
		int count = 0;
		String sqlQueryPatientsByDescription="select count(p_id) AS count from diagnosis,disease where diagnosis.ds_id=disease.ds_id AND disease.NAME='"+diseaseName+"'";
		try {
			Connection conn = DBOperations.getConnection();
			if (conn != null) {
				PreparedStatement ps = conn.prepareStatement(sqlQueryPatientsByDescription);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					count = rs.getInt("COUNT");
					
				}
				
			}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return count;
	}

	public HashMap<Integer, ArrayList<Integer>> getDiseaseGroupValues(String diseaseName){
		HashMap<Integer,ArrayList<Integer>>expressionValues=new HashMap<>();
		String query= "SELECT microarray_fact.EXP,probe.UID1 from diagnosis, clinicalsample, microarray_fact, probe WHERE disease.NAME='"+diseaseName+"' AND diagnosis.DS_ID = disease.DS_ID AND diagnosis.P_ID = clinicalsample.P_ID AND microarray_fact.PB_ID = probe.PB_ID AND clinicalsample.S_ID = microarray_fact.S_ID  ORDER BY probe.UID1";
		try {
			Connection conn = DBOperations.getConnection();
			if (conn != null) {
				PreparedStatement ps = conn.prepareStatement(query);
				ResultSet rs = ps.executeQuery();
				
				while (rs.next()) {

					int value = rs.getInt("EXP");
					int geneId = rs.getInt("UID1");

					if (!expressionValues.containsKey(geneId)) {
						ArrayList<Integer> tmp = new ArrayList<>();
						tmp.add(value);
						expressionValues.put(geneId, tmp);
					} else {
						ArrayList<Integer> tmp = expressionValues.get(geneId);
						tmp.add(value);
					}
				}
				
			}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		return expressionValues;
	}
	
	public HashMap<Integer, ArrayList<Integer>> geControlGroupValues(String diseaseName){
		HashMap<Integer,ArrayList<Integer>>expressionValues=new HashMap<>();
		String query= "SELECT microarray_fact.EXP,probe.UID1 from diagnosis, clinicalsample, microarray_fact, probe WHERE disease.NAME<>'"+diseaseName+"' AND diagnosis.DS_ID = disease.DS_ID AND diagnosis.P_ID = clinicalsample.P_ID AND microarray_fact.PB_ID = probe.PB_ID AND clinicalsample.S_ID = microarray_fact.S_ID  ORDER BY probe.UID1";
		try {
			Connection conn = DBOperations.getConnection();
			if (conn != null) {
				PreparedStatement ps = conn.prepareStatement(query);
				ResultSet rs = ps.executeQuery();
				
				while (rs.next()) {

					int value = rs.getInt("EXP");
					int geneId = rs.getInt("UID1");

					if (!expressionValues.containsKey(geneId)) {
						ArrayList<Integer> tmp = new ArrayList<>();
						tmp.add(value);
						expressionValues.put(geneId, tmp);
					} else {
						ArrayList<Integer> tmp = expressionValues.get(geneId);
						tmp.add(value);
					}
				}
				
			}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		return expressionValues;
	}
	
	
	public ArrayList<String> getDiseaseTypes() {
		ArrayList<String>types=new ArrayList<>();
		String tmp=null;
		String diseaseType="select distinct(disease.TYPE) from disease";
		try {
			Connection conn = DBOperations.getConnection();
			if (conn != null) {
				PreparedStatement ps = conn.prepareStatement(diseaseType);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					tmp = rs.getString("TYPE");
					types.add(tmp);
					
				}
				
			}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		return types;
		
	}
	
	public ArrayList<String>getDrugTypes(String diseaseDescription){
		ArrayList<String>drugTypes=new ArrayList<>();
		String tmp=null;
		String drugType="select drug.TYPES from drug,diagnosis,disease where disease.DS_ID=diagnosis.DS_ID AND diagnosis.DR_ID=drugs.DR_ID AND disease.Description='"+diseaseDescription+"'";
		try {
			Connection conn = DBOperations.getConnection();
			if (conn != null) {
				PreparedStatement ps = conn.prepareStatement(drugType);
				ResultSet rs = ps.executeQuery();
				
				while (rs.next()) {
					tmp = rs.getString("TYPES");
					drugTypes.add(tmp);
					
				}
				
			}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		return drugTypes;
	}
	
	
	public ArrayList<String> getDiseaseNames() {
		ArrayList<String>names=new ArrayList<>();
		String tmp=null;
		String diseaseName="select disease.NAME from disease";
		try {
			Connection conn = DBOperations.getConnection();
			if (conn != null) {
				PreparedStatement ps = conn.prepareStatement(diseaseName);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					tmp = rs.getString("NAME");
					names.add(tmp);
					
				}
				
			}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		return names;
		
	}
	
	public ArrayList<String> getDiseaseDescription() {
		ArrayList<String>desc=new ArrayList<>();
		String tmp=null;
		String diseaseDescription="select distinct(disease.DESCRIPTION) from disease";
		try {
			Connection conn = DBOperations.getConnection();
			if (conn != null) {
				PreparedStatement ps = conn.prepareStatement(diseaseDescription);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					tmp = rs.getString("DESCRIPTION");
					desc.add(tmp);
					
				}
				
			}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		return desc;
		
	}
	
	
}