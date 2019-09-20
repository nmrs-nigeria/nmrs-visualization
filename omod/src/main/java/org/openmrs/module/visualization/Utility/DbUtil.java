package org.openmrs.module.visualization.Utility;

import org.openmrs.module.visualization.Model.DBConnection;
import org.openmrs.util.OpenmrsUtil;

import java.util.Properties;

public abstract class DbUtil {
	
	public static DBConnection getNmrsConnectionDetails() {
		
		DBConnection result = new DBConnection();
		
		try {
			Properties props = new Properties();
			props = OpenmrsUtil.getRuntimeProperties("openmrs");
			if (props == null) {
				props = OpenmrsUtil.getRuntimeProperties("openmrs-standalone");
			}
			
			result.setUsername(props.getProperty("connection.username"));
			result.setPassword(props.getProperty("connection.password"));
			result.setUrl(props.getProperty("connection.url"));
		}
		catch (Exception ex) {
			
		}
		return result;
	}
}
