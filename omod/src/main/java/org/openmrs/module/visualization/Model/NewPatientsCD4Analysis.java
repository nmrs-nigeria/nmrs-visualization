package org.openmrs.module.visualization.Model;

public class NewPatientsCD4Analysis {
	
	String monthofReview;
	
	Integer totalPatients;
	
	Integer cd4LessThan20;
	
	Integer cd4GreaterThanEqual200;
	
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
	
	public Integer getCd4LessThan20() {
		return cd4LessThan20;
	}
	
	public void setCd4LessThan20(Integer cd4LessThan20) {
		this.cd4LessThan20 = cd4LessThan20;
	}
	
	public Integer getCd4GreaterThanEqual200() {
		return cd4GreaterThanEqual200;
	}
	
	public void setCd4GreaterThanEqual200(Integer cd4GreaterThanEqual200) {
		this.cd4GreaterThanEqual200 = cd4GreaterThanEqual200;
	}
}
