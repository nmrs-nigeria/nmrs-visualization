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
	
	public Set<BarChartModel> getHtsCascadeBar() {
		try {
			DBConnection connResult = DbUtil.getNmrsConnectionDetails();
			
			Set<BarChartModel> barChartModels = new HashSet<BarChartModel>();
			BarChartModel barChartModel = new BarChartModel();
			
			Connection connection = DriverManager.getConnection(connResult.getUrl(), connResult.getUsername(),
			    connResult.getPassword());
			Statement statement = connection.createStatement();
			String sqlStatement = ("SELECT COUNT(DISTINCT patient_id) as patient_count FROM patient_identifier WHERE identifier_type = 8");
			ResultSet result = statement.executeQuery(sqlStatement);
			if (result.next()) {
				barChartModel.setY(result.getInt("patient_count"));
			} else {
				barChartModel.setY(0);
			}
			barChartModel.setName("Tested");
			barChartModels.add(barChartModel);
			
			return barChartModels;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
