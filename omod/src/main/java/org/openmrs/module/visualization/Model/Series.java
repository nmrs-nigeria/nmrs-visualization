package org.openmrs.module.visualization.Model;

import java.util.List;

public class Series {
	
	private String name;
	
	private List<Integer> data = null;
	
	private List<Double> dataDouble = null;
	
	public List<Double> getDataDouble() {
		return dataDouble;
	}
	
	public void setDataDouble(List<Double> dataDouble) {
		this.dataDouble = dataDouble;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Integer> getData() {
		return data;
	}
	
	public void setData(List<Integer> data) {
		this.data = data;
	}
}
