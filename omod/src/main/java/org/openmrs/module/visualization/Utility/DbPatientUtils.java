package org.openmrs.module.visualization.Utility;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.visualization.Model.BarChartModel;
import org.openmrs.module.visualization.Model.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DbPatientUtils {
	
	public void getPositivePatients() {
		List<Patient> patients = Context.getPatientService().getAllPatients();
	}
	
	public ArrayList<BarChartModel> getHtsCascadeBar() {
		try {
			DBConnection connResult = DbUtil.getNmrsConnectionDetails();
			
			ArrayList<BarChartModel> barChartModels = new ArrayList<BarChartModel>();
			
			Connection connection = DriverManager.getConnection(connResult.getUrl(), connResult.getUsername(),
			    connResult.getPassword());
			Statement statement = connection.createStatement();
			String sql = "SELECT * FROM (\n"
			        + "(SELECT COUNT(DISTINCT patient_id) AS patient_count FROM patient_identifier WHERE identifier_type = 8 AND MONTH(CURDATE())) AS d CROSS JOIN\n"
			        + "(SELECT COUNT(DISTINCT person_id) AS risk_assessment_done FROM obs WHERE concept_id IN (165800, 1063, 159218, 165803, 164809, 165806) AND MONTH(CURDATE())) AS b CROSS JOIN\n"
			        + "(SELECT COUNT(DISTINCT person_id) AS tested FROM obs WHERE concept_id = 165843 AND MONTH(CURDATE())) AS c CROSS JOIN\n"
			        + "(SELECT COUNT(DISTINCT person_id) AS recent_infection FROM obs WHERE concept_id = 165853 AND value_coded = 165852 AND MONTH(CURDATE())) AS a CROSS JOIN\n"
			        + "(SELECT COUNT(DISTINCT person_id) AS long_term FROM obs WHERE concept_id = 165853 AND value_coded = 165851 AND MONTH(CURDATE())) AS e CROSS JOIN\n"
			        + "(SELECT COUNT(DISTINCT person_id) AS viral_load_done FROM obs WHERE concept_id = 165855 AND value_text IS NOT NULL AND MONTH(CURDATE())) AS f\n"
			        + ")";
			String sqlStatement = (sql);
			ResultSet result = statement.executeQuery(sqlStatement);
			if (result.next()) {
				barChartModels = buildChartData(result);
			} else {}
			
			return barChartModels;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private ArrayList<BarChartModel> buildChartData(ResultSet result) {
		ArrayList<BarChartModel> barChartModels = new ArrayList<BarChartModel>();
		BarChartModel barChartModel = new BarChartModel();
		try {
			barChartModel.setY(result.getInt("patient_count"));
			barChartModel.setName("Client intake for test");
			barChartModels.add(barChartModel);
			//
			barChartModel = new BarChartModel();
			barChartModel.setY(result.getInt("risk_assessment_done"));
			barChartModel.setName("Risk assessment");
			barChartModels.add(barChartModel);
			//
			barChartModel = new BarChartModel();
			barChartModel.setY(result.getInt("tested"));
			barChartModel.setName("Test result");
			barChartModels.add(barChartModel);
			//
			barChartModel = new BarChartModel();
			barChartModel.setY(result.getInt("recent_infection"));
			barChartModel.setName("Positive (New infection)");
			barChartModels.add(barChartModel);
			//
			barChartModel = new BarChartModel();
			barChartModel.setY(result.getInt("long_term"));
			barChartModel.setName("Positive (Long Term)");
			barChartModels.add(barChartModel);
			//
			barChartModel = new BarChartModel();
			barChartModel.setY(result.getInt("viral_load_done"));
			barChartModel.setName("Viral load Result for client with new infection(Asante)");
			barChartModels.add(barChartModel);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return barChartModels;
	}
}
