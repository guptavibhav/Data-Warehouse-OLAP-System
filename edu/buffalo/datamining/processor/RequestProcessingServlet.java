package edu.buffalo.datamining.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.buffalo.datamining.queries.CorrelationTest;
import edu.buffalo.datamining.queries.FTest;
import edu.buffalo.datamining.queries.TStatisticTest;
import edu.buffalo.datamining.queries.TestQuery1;
import edu.buffalo.datamining.queries.TestQuery2;
import edu.buffalo.datamining.services.DBOperations;
import edu.buffalo.datamining.wrapper.AttributesForTestQuery;
import edu.buffalo.datamining.wrapper.Diseases;
import edu.buffalo.datamining.wrapper.TableColumnNamesPair;

/**
 * Servlet implementation class RequestProcessingServlet
 */
@WebServlet("/RequestProcessingServlet")
public class RequestProcessingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ObjectMapper objectMapper = null;
	private PrintWriter writer = null;
	private DBOperations operations = new DBOperations();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RequestProcessingServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response)  {
		try{
		String action = request.getParameter("action");
		@SuppressWarnings("unused")
		Map<String, String[]> parameters = request.getParameterMap();
		if (action!=null){
		String resultString=null;
		int goIdValue=0;
		String goId=null;
		String disease1=null;
		String disease2=null;
		
		switch (action) {

		case "populate":
			ArrayList<String>types=operations.getDiseaseTypes();
			ArrayList<String>names=operations.getDiseaseNames();
			ArrayList<String>descriptions=operations.getDiseaseDescription();
			Diseases disease=new Diseases(names,types,descriptions);
			objectMapper = new ObjectMapper();
			// response.setContentType("application/json");
			writer = response.getWriter();
			objectMapper.writeValue(writer, disease);
			
			writer.flush();
			writer.close();
				
			break;

		case "ttestLoadInfo":

			AttributesForTestQuery values = operations.getTestInfo();
			objectMapper = new ObjectMapper();
			writer = response.getWriter();
			objectMapper.writeValue(writer, values);
			writer.flush();
			writer.close();
			break;
			
		case "patientCount":
		    TestQuery1 patientCount=new TestQuery1(request);
		    resultString=patientCount.evaluate();
		    System.out.println(resultString);
			writer.write(resultString);
			writer.flush();
			writer.close();
			break;

		case  "drugTypes":
			disease1=request.getParameter("disease");
			TestQuery2 drugTypes=new TestQuery2(disease1);
		    ArrayList<String>results=drugTypes.evaluate();
		    System.out.println(results);
		    objectMapper = new ObjectMapper();
			writer = response.getWriter();
			objectMapper.writeValue(writer, results);
			writer.flush();
			writer.close();
			break;
			
	    case "ttest":
			System.out.println("TTest being performed");
			disease1 = request.getParameter("Disease1");
		    disease2 = request.getParameter("Disease2");
			goId=request.getParameter("GO");
			int goValue=0;
			if(goId!=null){
				 goValue = Integer.valueOf(goId);
			}
			TStatisticTest test = new TStatisticTest(disease1, disease2, goValue);
			resultString=test.evaluate();
			System.out.println(resultString);
			writer = response.getWriter();
			writer.write(resultString);
			writer.flush();
			writer.close();
			
			
			break;

		case "ftest":
			System.out.println("FTest being performed");
			disease1 = request.getParameter("Disease1");
			disease2 = request.getParameter("Disease2");
			goId=request.getParameter("GO");
			goIdValue=0;
			if(goId!=null){
				 goIdValue = Integer.valueOf(goId);
			}
			FTest ftest = new FTest(disease1, disease2, goIdValue);
			resultString=ftest.evaluate();
			System.out.println(resultString);
			writer = response.getWriter();
			writer.write(resultString);
			writer.flush();
			writer.close();
		
			break;
		
		case "correlation":
			System.out.println("Average Correlation being calculated");
			disease1 = request.getParameter("Disease1");
			disease2 = request.getParameter("Disease2");
			goId=request.getParameter("GO");
			goIdValue=0;
			if(goId!=null){
				 goIdValue = Integer.valueOf(goId);
			}
			CorrelationTest ctest = new CorrelationTest(disease1, disease2, goIdValue);
			resultString=ctest.evaluateAverageCorrelation();
			System.out.println(resultString);
			writer = response.getWriter();
			writer.write(resultString);
			writer.flush();
			writer.close();
		
			break;	
			
		}
		;
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
}
