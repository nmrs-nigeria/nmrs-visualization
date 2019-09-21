package org.openmrs.module.visualization.Model;

import java.util.ArrayList;

public class HtsCharts {
	
	private ArrayList<BarChartModel> barChartModels;
	
	private ChartModel chartModel;
	
	public ArrayList<BarChartModel> getBarChartModels() {
		return barChartModels;
	}
	
	public void setBarChartModels(ArrayList<BarChartModel> barChartModels) {
		this.barChartModels = barChartModels;
	}
	
	public ChartModel getChartModel() {
		return chartModel;
	}
	
	public void setChartModel(ChartModel chartModel) {
		this.chartModel = chartModel;
	}
}
