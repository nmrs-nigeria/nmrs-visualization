package org.openmrs.module.visualization.Model;

public class NewPatientVLCascade {
	
	String monthofReview;
	
	Integer totalPatients;
	
	Integer totalPatientsVl;
	
	Integer totalPatientsVlGreaterThanOrEqual1000;
	
	Integer totalPatientsVllessThen1000;
	
	public String getMonthofReview() {
		return monthofReview;
	}
	
	public void setMonthofReview(String monthofReview) {
		this.monthofReview = monthofReview;
	}
	
	public Integer getTotalPatients() {
		return totalPatients;
	}
	
	public void setTotalPatients(Integer totalPatients) {
		this.totalPatients = totalPatients;
	}
	
	public Integer getTotalPatientsVl() {
		return totalPatientsVl;
	}
	
	public void setTotalPatientsVl(Integer totalPatientsVl) {
		this.totalPatientsVl = totalPatientsVl;
	}
	
	public Integer getTotalPatientsVlGreaterThanOrEqual1000() {
		return totalPatientsVlGreaterThanOrEqual1000;
	}
	
	public void setTotalPatientsVlGreaterThanOrEqual1000(Integer totalPatientsVlGreaterThanOrEqual1000) {
		this.totalPatientsVlGreaterThanOrEqual1000 = totalPatientsVlGreaterThanOrEqual1000;
	}
	
	public Integer getTotalPatientsVllessThen1000() {
		return totalPatientsVllessThen1000;
	}
	
	public void setTotalPatientsVllessThen1000(Integer totalPatientsVllessThen1000) {
		this.totalPatientsVllessThen1000 = totalPatientsVllessThen1000;
	}
}
