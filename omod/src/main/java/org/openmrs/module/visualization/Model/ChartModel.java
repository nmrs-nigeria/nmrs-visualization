package org.openmrs.module.visualization.Model;

import java.util.ArrayList;

public class ChartModel {
	
	private String name;
	
	private float value;
	
	private String pos_name;
	
	private int pos_count;
	
	private String start_art_in;
	
	private int in_count;
	
	private String start_art_out;
	
	private int out_count;
	
	private int percentage;
	
	public String getPos_name() {
		return pos_name;
	}
	
	public void setPos_name(String pos_name) {
		this.pos_name = pos_name;
	}
	
	public int getPos_count() {
		return pos_count;
	}
	
	public void setPos_count(int pos_count) {
		this.pos_count = pos_count;
	}
	
	public String getStart_art_in() {
		return start_art_in;
	}
	
	public void setStart_art_in(String start_art_in) {
		this.start_art_in = start_art_in;
	}
	
	public int getIn_count() {
		return in_count;
	}
	
	public void setIn_count(int in_count) {
		this.in_count = in_count;
	}
	
	public String getStart_art_out() {
		return start_art_out;
	}
	
	public void setStart_art_out(String start_art_out) {
		this.start_art_out = start_art_out;
	}
	
	public int getOut_count() {
		return out_count;
	}
	
	public void setOut_count(int out_count) {
		this.out_count = out_count;
	}
	
	public int getPercentage() {
		return percentage;
	}
	
	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public float getValue() {
		return value;
	}
	
	public void setValue(float value) {
		this.value = value;
	}
}
