package org.openmrs.module.visualization.Model;

public class TxNewAchievements {
	
	String monthText;
	
	Integer monthNumber;
	
	Integer txNewPerMonth;
	
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
	
	public Integer getTxNewPerMonth() {
		return txNewPerMonth;
	}
	
	public void setTxNewPerMonth(Integer txNewPerMonth) {
		this.txNewPerMonth = txNewPerMonth;
	}
}
