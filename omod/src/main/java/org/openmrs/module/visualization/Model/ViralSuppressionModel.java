package org.openmrs.module.visualization.Model;

public class ViralSuppressionModel
{
	private String cohort;
	
	private int suppressed;
	private int nonSuppressed;
	
	public String getCohort() {
		return cohort;
	}
	public void setCohort(String cohort) {
		this.cohort = cohort;
	}
	
	public int getSuppressed() {
		return suppressed;
	}
	public void setSuppressed(int suppressed) {this.suppressed = suppressed;}

	public int getNonSuppressed() {
		return nonSuppressed;
	}
	public void setNonSuppressed(int nonSuppressed) {this.nonSuppressed = nonSuppressed;}
}
