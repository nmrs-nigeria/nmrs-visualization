package org.openmrs.module.visualization.Model;

public class HIVPositveCLients {
	
	String monthText;
	
	Integer monthNumber;
	
	Integer total;
	
	public Integer getTotal() {
		return total;
	}
	
	public void setTotal(Integer total) {
		this.total = total;
	}
	
	Integer samDay;
	
	public Integer getUnknown() {
		return unknown;
	}
	
	public void setUnknown(Integer unknown) {
		this.unknown = unknown;
	}
	
	Integer unknown;
	
	Integer oneToSeven;
	
	Integer graterThanEqual8;
	
	public String getMonthText() {
		return monthText;
	}
	
	public void setMonthText(String monthText) {
		this.monthText = monthText;
	}
	
	public Integer getMonthNumber() {
		return monthNumber;
	}
	
	public void setMonthNumber(Integer monthNumber) {
		this.monthNumber = monthNumber;
	}
	
	public Integer getSamDay() {
		return samDay;
	}
	
	public void setSamDay(Integer samDay) {
		this.samDay = samDay;
	}
	
	public Integer getOneToSeven() {
		return oneToSeven;
	}
	
	public void setOneToSeven(Integer oneToSeven) {
		this.oneToSeven = oneToSeven;
	}
	
	public Integer getGraterThanEqual8() {
		return graterThanEqual8;
	}
	
	public void setGraterThanEqual8(Integer graterThanEqual8) {
		this.graterThanEqual8 = graterThanEqual8;
	}
}
