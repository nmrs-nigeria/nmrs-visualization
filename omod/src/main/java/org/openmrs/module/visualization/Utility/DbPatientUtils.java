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
	
	public HtsCharts getHtsCharts(String startDate, String endDate) {
		HtsCharts htsCharts = new HtsCharts();
		htsCharts.setBarChartModels(getHtsCascadeBar(startDate, endDate));
		htsCharts.setChartModel(getHtsStackBar());
		return htsCharts;
	}
	
	public ArrayList<BarChartModel> getHtsCascadeBar(String startDate, String endDate) {
		try {
			DBConnection connResult = DbUtil.getNmrsConnectionDetails();
			
			ArrayList<BarChartModel> barChartModels = new ArrayList<BarChartModel>();
			
			Connection connection = DriverManager.getConnection(connResult.getUrl(), connResult.getUsername(),
			    connResult.getPassword());
			Statement statement = connection.createStatement();
			String sql = "SELECT * FROM (\n"
			        + "(SELECT COUNT(DISTINCT(enc.patient_id)) AS patient_count FROM patient_identifier pid\n"
			        + "JOIN encounter enc ON enc.patient_id = pid.patient_id\n"
			        + "WHERE enc.encounter_type = 2 AND pid.identifier_type = 8\n" + "AND form_id = 10\n"
			        + "AND DATE(enc.encounter_datetime) BETWEEN '"
			        + startDate
			        + "' AND '"
			        + endDate
			        + "') AS a CROSS JOIN\n"
			        + "\n"
			        + "(SELECT COUNT(DISTINCT(enc.patient_id)) AS risk_assessment FROM encounter enc\n"
			        + "JOIN obs ob ON ob.encounter_id = enc.encounter_id\n"
			        + "WHERE concept_id IN (165800, 1063, 159218, 165803, 164809, 165806)\n"
			        + "AND enc.encounter_type = 2 AND DATE(enc.encounter_datetime) BETWEEN '"
			        + startDate
			        + "' AND '"
			        + endDate
			        + "') AS b CROSS JOIN\n"
			        + "\n"
			        + "(SELECT COUNT(DISTINCT(enc.patient_id)) AS tested FROM encounter enc\n"
			        + "JOIN obs ob ON ob.encounter_id = enc.encounter_id\n"
			        + "WHERE concept_id = 165843\n"
			        + "AND enc.encounter_type = 2 AND DATE(enc.encounter_datetime) BETWEEN '"
			        + startDate
			        + "' AND '"
			        + endDate
			        + "') AS c CROSS JOIN\n"
			        + "\n"
			        + "(SELECT COUNT(DISTINCT(enc.patient_id)) AS positive FROM encounter enc\n"
			        + "JOIN obs ob ON ob.encounter_id = enc.encounter_id\n"
			        + "WHERE concept_id = 165843 AND value_coded = 703\n"
			        + "AND enc.encounter_type = 2 AND DATE(enc.encounter_datetime) BETWEEN '"
			        + startDate
			        + "' AND '"
			        + endDate
			        + "') AS d CROSS JOIN\n"
			        + "\n"
			        + "(SELECT COUNT(DISTINCT(enc.patient_id)) AS recent_infection FROM encounter enc\n"
			        + "JOIN obs ob ON ob.encounter_id = enc.encounter_id\n"
			        + "WHERE concept_id = 165853 AND value_coded = 165852\n"
			        + "AND enc.encounter_type = 2 AND DATE(enc.encounter_datetime) BETWEEN '"
			        + startDate
			        + "' AND '"
			        + endDate
			        + "') AS e CROSS JOIN\n"
			        + "\n"
			        + "(SELECT COUNT(DISTINCT(enc.patient_id)) AS long_term FROM encounter enc\n"
			        + "JOIN obs ob ON ob.encounter_id = enc.encounter_id\n"
			        + "WHERE concept_id = 165853 AND value_coded = 165851\n"
			        + "AND enc.encounter_type = 2 AND DATE(enc.encounter_datetime) BETWEEN '"
			        + startDate
			        + "' AND '"
			        + endDate
			        + "') AS f CROSS JOIN\n"
			        + "\n"
			        + "(SELECT COUNT(DISTINCT(enc.patient_id)) AS viral_load_done FROM encounter enc\n"
			        + "JOIN obs ob ON ob.encounter_id = enc.encounter_id\n"
			        + "WHERE concept_id IN (165855, 165853) AND (value_text IS NOT NULL || value_coded = 165851)\n"
			        + "AND enc.encounter_type = 2 AND DATE(enc.encounter_datetime) BETWEEN '"
			        + startDate
			        + "' AND '"
			        + endDate + "') AS g\n" + ");";
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
			/*String sql = "SELECT * FROM ((SELECT COUNT(DISTINCT person_id) AS new_positive FROM obs WHERE concept_id = 165843 AND value_coded = 703 AND MONTH(CURDATE())) AS a CROSS JOIN "
			        + "(SELECT COUNT(DISTINCT person_id) AS started_art_in FROM obs WHERE concept_id = 160540 AND value_coded IN (160542,160536,160539,160541,160546,160538,160545,5622) AND MONTH(CURDATE())) AS b CROSS JOIN "
			        + "(SELECT COUNT(DISTINCT(pid.identifier)) AS started_art_out FROM patient_identifier pid  JOIN encounter e ON pid.patient_id = e.patient_id JOIN obs ob ON e.encounter_id = ob.encounter_id  WHERE (pid.identifier_type = 8) AND e.encounter_type = 22 AND ob.concept_id IN (165508,165501) AND MONTH(CURDATE())) AS c)";*/
			String sql = "SELECT * FROM (\n"
			        + "(SELECT COUNT(DISTINCT(patient_id)) AS new_positive FROM encounter WHERE encounter_type = 2 AND DATE(encounter_datetime) BETWEEN '2017-04-30' AND '2019-09-30') AS a CROSS JOIN\n"
			        + "(SELECT COUNT(DISTINCT(patient_id)) AS started_art_in FROM encounter WHERE encounter_type = 25 AND form_id = 56 AND DATE(encounter_datetime) BETWEEN '2017-04-30' AND '2019-09-30') AS b CROSS JOIN\n"
			        + "(SELECT COUNT(DISTINCT(patient_id)) AS started_art_out FROM encounter WHERE encounter_type = 22 AND form_id = 52 AND DATE(encounter_datetime) BETWEEN '2017-04-30' AND '2019-09-30') AS c\n"
			        + ")";
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
	
	public ArrayList<ChartModel> getPmtctFollowUp() {
		try {
			DBConnection connResult = DbUtil.getNmrsConnectionDetails();
			
			ArrayList<ChartModel> barChartModels = new ArrayList<ChartModel>();
			
			Connection connection = DriverManager.getConnection(connResult.getUrl(), connResult.getUsername(),
			    connResult.getPassword());
			Statement statement = connection.createStatement();
			String sql = "SELECT * FROM ((SELECT COUNT(DISTINCT(person_id)) AS pos_linked FROM obs WHERE concept_id = 165035 AND value_coded = 165552 AND MONTH(CURDATE())) AS a CROSS JOIN"
			        + " (SELECT COUNT(DISTINCT(person_id)) AS pos_not_linked FROM obs WHERE concept_id = 165035 AND value_coded = 165553 AND MONTH(CURDATE())) AS b CROSS JOIN"
			        + " (SELECT COUNT(DISTINCT(person_id)) AS neg_breastfeed FROM obs WHERE concept_id = 165035 AND value_coded = 165554 AND MONTH(CURDATE())) AS c CROSS JOIN"
			        + " (SELECT COUNT(DISTINCT(person_id)) AS neg_not_breastfeed FROM obs WHERE concept_id = 165035 AND value_coded = 1404 AND MONTH(CURDATE())) AS d CROSS JOIN"
			        + " (SELECT COUNT(DISTINCT(person_id)) AS died FROM obs WHERE concept_id = 165035 AND value_coded = 165556 AND MONTH(CURDATE())) AS e CROSS JOIN"
			        + " (SELECT COUNT(DISTINCT(person_id)) AS lost_to_follow FROM obs WHERE concept_id = 165035 AND value_coded = 165557 AND MONTH(CURDATE())) AS f CROSS JOIN"
			        + " (SELECT COUNT(DISTINCT(person_id)) AS transfered_out FROM obs WHERE concept_id = 165035 AND value_coded = 165558 AND MONTH(CURDATE())) AS g)";
			String sqlStatement = (sql);
			ResultSet result = statement.executeQuery(sqlStatement);
			if (result.next()) {
				barChartModels = buildPieData(result);
			} else {}
			
			connection.close();
			return barChartModels;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<BarChartModel> getPmtctEid(String start_date, String end_date) {
		try {
			DBConnection connResult = DbUtil.getNmrsConnectionDetails();
			
			ArrayList<BarChartModel> chartModels = new ArrayList<BarChartModel>();
			
			Connection connection = DriverManager.getConnection(connResult.getUrl(), connResult.getUsername(),
			    connResult.getPassword());
			Statement statement = connection.createStatement();
			//PreparedStatement preparedStatement = null;
			String sql = "SELECT * FROM ((SELECT COUNT(DISTINCT(pid.patient_id)) AS hei_cohort FROM patient_identifier pid JOIN person per ON per.person_id = pid.patient_id WHERE pid.identifier_type = 7 AND DATE(per.birthdate) BETWEEN "
			        + start_date
			        + " AND "
			        + end_date
			        + ") AS a CROSS JOIN "
			        + "(SELECT COUNT(DISTINCT(person_id)) AS rec_nvp FROM obs WHERE concept_id = 164971 AND value_coded = 164970 AND person_id IN (SELECT pid.patient_id FROM patient_identifier pid JOIN person per ON per.person_id = pid.patient_id WHERE pid.identifier_type = 7 AND DATE(per.birthdate) BETWEEN "
			        + start_date
			        + " AND "
			        + end_date
			        + ")) b CROSS JOIN "
			        + "(SELECT COUNT(DISTINCT(person_id)) AS dbs_coll FROM obs WHERE concept_id = 165868 AND value_coded = 165865 AND person_id IN (SELECT pid.patient_id FROM patient_identifier pid JOIN person per ON per.person_id = pid.patient_id WHERE pid.identifier_type = 7 AND DATE(per.birthdate) BETWEEN "
			        + start_date
			        + " AND "
			        + end_date
			        + ")) c CROSS JOIN "
			        + "(SELECT COUNT(DISTINCT(person_id)) AS pos_pcr FROM obs WHERE concept_id = 165872 AND value_coded = 703 AND person_id IN (SELECT pid.patient_id FROM patient_identifier pid JOIN person per ON per.person_id = pid.patient_id WHERE pid.identifier_type = 7 AND DATE(per.birthdate) BETWEEN "
			        + start_date
			        + " AND "
			        + end_date
			        + ")) d CROSS JOIN "
			        + "(SELECT COUNT(DISTINCT(person_id)) AS pos_linked FROM obs WHERE concept_id = 165035 AND value_coded = 165552 AND person_id IN (SELECT pid.patient_id FROM patient_identifier pid JOIN person per ON per.person_id = pid.patient_id WHERE pid.identifier_type = 7 AND DATE(per.birthdate) BETWEEN "
			        + start_date + " AND " + end_date + ")) e)";
			
			//preparedStatement = connection.prepareStatement(sql);
			ResultSet result = statement.executeQuery(sql);
			//preparedStatement.setInt(1, month);
			if (result.next()) {
				chartModels = buildEIDData(result);
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
			barChartModel.setY(result.getInt("risk_assessment"));
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
			barChartModel.setY(result.getInt("positive"));
			barChartModel.setName("HIV Positive");
			barChartModels.add(barChartModel);
			//
			barChartModel = new BarChartModel();
			barChartModel.setY(result.getInt("recent_infection"));
			barChartModel.setName("Positive (Recent infection)");
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
	
	private ArrayList<ChartModel> buildPieData(ResultSet result) {
		ArrayList<ChartModel> chartModels = new ArrayList<ChartModel>();
		ChartModel chart = new ChartModel();
		try {
			chart.setName("HIV postive-Linked to ART");
			chart.setValue(result.getInt("pos_linked"));
			chartModels.add(chart);
			//
			chart = new ChartModel();
			chart.setName("HIV postive-Not Linked to ART");
			chart.setValue(result.getInt("pos_not_linked"));
			chartModels.add(chart);
			//
			chart = new ChartModel();
			chart.setName("HIV neg-No longer breastfeeding");
			chart.setValue(result.getInt("neg_breastfeed"));
			chartModels.add(chart);
			//
			chart = new ChartModel();
			chart.setName("HIV neg-Still breastfeeding");
			chart.setValue(result.getInt("neg_not_breastfeed"));
			chartModels.add(chart);
			//
			chart = new ChartModel();
			chart.setName("HIV status unknown Died");
			chart.setValue(result.getInt("died"));
			chartModels.add(chart);
			//
			chart = new ChartModel();
			chart.setName("HIV status unknown lost to follow up");
			chart.setValue(result.getInt("lost_to_follow"));
			chartModels.add(chart);
			//
			chart = new ChartModel();
			chart.setName("HIV status unknown Transfer out");
			chart.setValue(result.getInt("transfered_out"));
			chartModels.add(chart);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return chartModels;
	}
	
	private ArrayList<BarChartModel> buildEIDData(ResultSet result) {
		ArrayList<BarChartModel> barChartModels = new ArrayList<BarChartModel>();
		BarChartModel barChartModel = new BarChartModel();
		try {
			barChartModel.setY(result.getInt("hei_cohort"));
			barChartModel.setName("Birth HEI Cohort");
			barChartModels.add(barChartModel);
			//
			barChartModel = new BarChartModel();
			barChartModel.setY(result.getInt("rec_nvp"));
			barChartModel.setName("Received NVP");
			barChartModels.add(barChartModel);
			//
			barChartModel = new BarChartModel();
			barChartModel.setY(result.getInt("dbs_coll"));
			barChartModel.setName("DBS Collected for EID");
			barChartModels.add(barChartModel);
			//
			barChartModel = new BarChartModel();
			barChartModel.setY(result.getInt("pos_pcr"));
			barChartModel.setName("HIV Positive (PCR)");
			barChartModels.add(barChartModel);
			//
			barChartModel = new BarChartModel();
			barChartModel.setY(result.getInt("pos_linked"));
			barChartModel.setName("Linked to ART");
			barChartModels.add(barChartModel);
			//
			barChartModels.add(barChartModel);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return barChartModels;
	}
}
