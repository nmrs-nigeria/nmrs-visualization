package org.openmrs.module.visualization.Model;

import java.util.List;

public class JsonResult {
	
	private List<String> categories = null;
	
	private List<Series> series = null;
	
	public List<String> getCategories() {
		return categories;
	}
	
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}
	
	public List<Series> getSeries() {
		return series;
	}
	
	public void setSeries(List<Series> series) {
		this.series = series;
	}
}
