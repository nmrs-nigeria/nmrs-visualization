package org.openmrs.module.visualization.fragment.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.module.visualization.Model.JsonResult;
import org.openmrs.module.visualization.Utility.DBARTUtils;
import org.springframework.web.bind.annotation.RequestParam;

public class VisualizationFragmentController {
	
	public void getIndex() {
		
	}
	
	public String plotgetHIVPositveClientsGraph(@RequestParam("startDatePeriod") String startDatePeriod,
	        @RequestParam("endDatePeriod") String endDatePeriod) {
		try {
			
			DBARTUtils factoryUtils = new DBARTUtils();
			JsonResult jsonResult = factoryUtils.plotHIVPositveClientsGraph(startDatePeriod, endDatePeriod, 0, "");
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(jsonResult);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public String getTxNewAchievments(@RequestParam("target") Integer target) {
		try {
			System.out.println(target);
			DBARTUtils factoryUtils = new DBARTUtils();
			JsonResult jsonResult = factoryUtils.plotTxNewAchievmentsList(target, "2018-10-01", "2019-09-31");
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(jsonResult);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public String plotNewPatientVLCascade(@RequestParam("target") Integer target) {
		try {
			System.out.println(target);
			DBARTUtils factoryUtils = new DBARTUtils();
			JsonResult jsonResult = factoryUtils.plotNewPatientVLCascade("2018-10-01", "2019-09-31");
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(jsonResult);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public String plotNewPatientsCD4Analysis(@RequestParam("target") Integer target) {
		try {
			System.out.println(target);
			DBARTUtils factoryUtils = new DBARTUtils();
			JsonResult jsonResult = factoryUtils.plotNewPatientsCD4Analysis("2018-10-01", "2019-09-31");
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(jsonResult);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public String plotMissedAppointments(@RequestParam("target") Integer target) {
		try {
			System.out.println(target);
			DBARTUtils factoryUtils = new DBARTUtils();
			JsonResult jsonResult = factoryUtils.plotMissedAppointment("2018-10-01", "2019-09-31");
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(jsonResult);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
}
