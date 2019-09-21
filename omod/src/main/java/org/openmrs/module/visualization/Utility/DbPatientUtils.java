package org.openmrs.module.visualization.Utility;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.visualization.Model.BarChartModel;
import org.openmrs.module.visualization.Model.ChartModel;
import org.openmrs.module.visualization.Model.DBConnection;
import org.openmrs.module.visualization.Model.HtsCharts;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DbPatientUtils {
	
	public void getPositivePatients() {
		List<Patient> patients = Context.getPatientService().getAllPatients();
	}
	
	public HtsCharts getHtsCharts() {
		HtsCharts htsCharts = new HtsCharts();
		htsCharts.setBarChartModels(getHtsCascadeBar());
		htsCharts.setChartModel(getHtsStackBar());
		return htsCharts;
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
			connection.close();
			return barChartModels;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ChartModel getHtsStackBar() {
		try {
			DBConnection connResult = DbUtil.getNmrsConnectionDetails();
			
			ChartModel chartModels = new ChartModel();
			
			Connection connection = DriverManager.getConnection(connResult.getUrl(), connResult.getUsername(),
			    connResult.getPassword());
			Statement statement = connection.createStatement();
			String sql = "SELECT * FROM ((SELECT COUNT(DISTINCT person_id) AS new_positive FROM obs WHERE concept_id = 165843 AND value_coded = 703 AND MONTH(CURDATE())) AS a CROSS JOIN "
			        + "(SELECT COUNT(DISTINCT person_id) AS started_art_in FROM obs WHERE concept_id = 160540 AND value_coded IN (160542,160536,160539,160541,160546,160538,160545,5622) AND MONTH(CURDATE())) AS b CROSS JOIN "
			        + "(SELECT COUNT(DISTINCT(pid.identifier)) AS started_art_out FROM patient_identifier pid  JOIN encounter e ON pid.patient_id = e.patient_id JOIN obs ob ON e.encounter_id = ob.encounter_id  WHERE (pid.identifier_type = 8) AND e.encounter_type = 22 AND ob.concept_id IN (165508,165501) AND MONTH(CURDATE())) AS c)";
			String sqlStatement = (sql);
			ResultSet result = statement.executeQuery(sqlStatement);
			if (result.next()) {
				chartModels = buildStackData(result);
			} else {}
			
			connection.close();
			return chartModels;
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
			//barChartModel.setY(20);
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
	
	private ChartModel buildStackData(ResultSet result) {
		ChartModel chartModel = new ChartModel();
		try {
			chartModel.setPos_name("New HIV Positive Client");
			chartModel.setPos_count(result.getInt("new_positive"));
			chartModel.setStart_art_in("Started on ART within the facility");
			chartModel.setStart_art_out("Started on ART outside the facility");
			chartModel.setIn_count(result.getInt("started_art_in"));
			chartModel.setOut_count(result.getInt("started_art_out"));
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return chartModel;
	}
}
