package org.openmrs.module.visualization.Utility;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.visualization.Model.BarChartModel;
import org.openmrs.module.visualization.Model.ChartModel;
import org.openmrs.module.visualization.Model.DBConnection;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DbPmtctUtils {
	
	public void getPositivePatients() {
		List<Patient> patients = Context.getPatientService().getAllPatients();
	}
	
	public ArrayList<ChartModel> getAncPmtctArt() {
		try {
			DBConnection connResult = DbUtil.getNmrsConnectionDetails();
			
			ArrayList<ChartModel> chartModels = new ArrayList<ChartModel>();
			Connection conn = DriverManager.getConnection(connResult.getUrl(), connResult.getUsername(),
			    connResult.getPassword());
			Statement statement = conn.createStatement();
			
			//ANC: that was Tested, Previous known HIV+, New HIV+, already on Tx, Newly on Tx
			
			String str = "select *\n"
			        + "from\n"
			        + "(\n"
			        + "\t(select count(patient_id) as anc FROM patient_identifier where identifier_type = 6) as anc\n"
			        + "    \n"
			        + "    cross join\n"
			        + "    \n"
			        + "\t(select count(distinct(ancx.patient_id)) as ancHts from\n"
			        + "    (select patient_id FROM patient_identifier where identifier_type = 6) ancx\n"
			        + "\tjoin\n"
			        + "    (select patient_id FROM patient_identifier where identifier_type = 8) hts on ancx.patient_id = hts.patient_id\n"
			        + "    ) ancHts\n" + "\t\n" + "    cross join\n" + "    \n"
			        + "    (select count(distinct(ps.patient_id)) as prevPositive from\t\n"
			        + "    (SELECT patient_id FROM patient_identifier WHERE identifier_type = 6) ps join\n"
			        + "    (SELECT person_id FROM obs where concept_id = 166030 and value_coded = 1065) obPrev  \n"
			        + "    on ps.patient_id = obPrev.person_id\n" + "    ) prevPositive \n" + "    \n" + "    cross join\n"
			        + "\t\n" + "    (select count(distinct(p.patient_id)) as newPositive from\t\n"
			        + "    (SELECT patient_id FROM patient_identifier WHERE identifier_type = 6) p join\n"
			        + "    (SELECT person_id FROM obs where concept_id = 159427 and value_coded in (703)) obPrev  \n"
			        + "    on p.patient_id = obPrev.person_id\n" + "    ) as newPositive\n" + "    \n" + "    cross join\n"
			        + "\t\n" + "    (select count(distinct(pmtctHts.patient_id)) as newOnTx \n" + "    from    \n"
			        + "    (SELECT * FROM encounter WHERE encounter_type = 10 AND form_id = 54) pmtctHts\n" + "    join\n"
			        + "    (SELECT * FROM encounter WHERE encounter_type = 13 AND form_id = 27) pof  \n"
			        + "    on pmtctHts.patient_id = pof.patient_id\n"
			        + "    where pof.encounter_datetime > pmtctHts.encounter_datetime\n" + "    ) as newOnTx\n" + "    \n"
			        + "    cross join\n" + "\t\n" + "    (select count(distinct(pmtctHts.patient_id)) as oldOnTx from\t\n"
			        + "    (SELECT * FROM encounter WHERE encounter_type = 10 AND form_id = 54) pmtctHts\n" + "    join\n"
			        + "    (SELECT * FROM encounter WHERE encounter_type = 13 AND form_id = 27) pof  \n"
			        + "    on pmtctHts.patient_id = pof.patient_id\n"
			        + "    where pof.encounter_datetime < pmtctHts.encounter_datetime\n" + "    ) as oldOnTx\n" + ")";
			
			String sqlStatement = (str);
			ResultSet result = statement.executeQuery(sqlStatement);
			
			if (result.next()) {
				ChartModel anc = new ChartModel();
				anc.setValue(result.getInt("anc"));
				anc.setName("ANC");
				chartModels.add(anc);
				
				ChartModel ancHts = new ChartModel();
				ancHts.setValue(result.getInt("ancHts"));
				ancHts.setName("ancHts");
				chartModels.add(ancHts);
				
				ChartModel prevPositive = new ChartModel();
				prevPositive.setValue(result.getInt("prevPositive"));
				prevPositive.setName("prevPositive");
				chartModels.add(prevPositive);
				
				ChartModel newPositive = new ChartModel();
				newPositive.setValue(result.getInt("newPositive"));
				newPositive.setName("newPositive");
				chartModels.add(newPositive);
				
				ChartModel newOnTx = new ChartModel();
				newOnTx.setValue(result.getInt("newOnTx"));
				newOnTx.setName("newOnTx");
				chartModels.add(newOnTx);
				
				ChartModel oldOnTx = new ChartModel();
				oldOnTx.setValue(result.getInt("oldOnTx"));
				oldOnTx.setName("oldOnTx");
				chartModels.add(oldOnTx);
			}
			
			return chartModels;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
