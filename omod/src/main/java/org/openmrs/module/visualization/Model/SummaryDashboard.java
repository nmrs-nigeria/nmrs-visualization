package org.openmrs.module.visualization.Model;

public class SummaryDashboard {
	
	Integer totalPatientsInFac;
	
	Integer countOfEncounter;
	
	Integer encounterTypeID;
	
	String encounterName;
	
	public Integer getTotalPatientsInFac() {
		return totalPatientsInFac;
	}
	
	public void setTotalPatientsInFac(Integer totalPatientsInFac) {
		this.totalPatientsInFac = totalPatientsInFac;
	}
	
	public Integer getCountOfEncounter() {
		return countOfEncounter;
	}
	
	public void setCountOfEncounter(Integer countOfEncounter) {
		this.countOfEncounter = countOfEncounter;
	}
	
	public Integer getEncounterTypeID() {
		return encounterTypeID;
	}
	
	public void setEncounterTypeID(Integer encounterTypeID) {
		this.encounterTypeID = encounterTypeID;
	}
	
	public String getEncounterName() {
		return encounterName;
	}
	
	public void setEncounterName(String encounterName) {
		this.encounterName = encounterName;
	}
}
