package org.openmrs.module.visualization.Utility;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.visualization.Model.BarChartModel;
import org.openmrs.module.visualization.Model.ChartModel;
import org.openmrs.module.visualization.Model.DBConnection;
import org.openmrs.module.visualization.Model.PmtctCohortRetentiModel;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.time.Month;
import java.time.Period;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DbPmtctUtils {
	
	public void getPositivePatients() {
		List<Patient> patients = Context.getPatientService().getAllPatients();
	}
	
	private DBConnection connResult = DbUtil.getNmrsConnectionDetails();
	
	public ArrayList<ChartModel> getAncPmtctArt() {
		try {
			
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
			
			ResultSet result = statement.executeQuery(str);
			
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
	
	public ArrayList<PmtctCohortRetentiModel> getPmtctCohortRetention()
	{
		ArrayList<PmtctCohortRetentiModel> chartModels = new ArrayList<PmtctCohortRetentiModel>();
		try
		{
			LocalDate today = LocalDate.now();
			LocalDate lastYear = LocalDate.of(today.minus(1, ChronoUnit.YEARS).getYear(), 10, 1);
			List<LocalDate> dates = getMonths(lastYear, today);
			dates.forEach(d ->
			{
				PmtctCohortRetentiModel pmtctArtModel = getCohort(d);
				chartModels.add(pmtctArtModel);
			});

			return chartModels;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<LocalDate> getMonths(LocalDate startDate, LocalDate endDate)
	{
		long numOfMonthsBetween = ChronoUnit.MONTHS.between(startDate, endDate);
		return IntStream.iterate(0, i -> i + 1)
				.limit(numOfMonthsBetween)
				.mapToObj(i -> startDate.plusMonths(i))
				.collect(Collectors.toList());
	}
	
	/*
		All mothers Within a cohort that are Active in PMTCT and on ART 3, 6, 12 after ANC Booking
	 */
	public PmtctCohortRetentiModel getCohort(LocalDate date) {
		PmtctCohortRetentiModel pmtctArtModel = new PmtctCohortRetentiModel();
		try {
			Connection conn = DriverManager.getConnection(connResult.getUrl(), connResult.getUsername(),
			    connResult.getPassword());
			Statement statement = conn.createStatement();
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String dateString = date.format(formatter);
			
			String str = "select\n"
			        + "*\n"
			        + "from\n"
			        + "((select count(distinct(pmtct.patient_id)) active_0\n"
			        + "\tfrom\n"
			        + "\t(select patient_id, encounter_datetime  FROM encounter WHERE (encounter_type = 10  AND form_id = 16) and (year(encounter_datetime) = year(date(cast("
			        + dateString
			        + " as datetime))) and month(encounter_datetime) = month(date(cast("
			        + dateString
			        + " as datetime))))) pmtct\n"
			        + "\tjoin\n"
			        + "\t(SELECT patient_id, encounter_datetime  FROM encounter WHERE (encounter_type = 14 AND form_id = 23) and (year(encounter_datetime) = year(date(cast("
			        + dateString
			        + " as datetime))) and month(encounter_datetime) = month(date(cast("
			        + dateString
			        + " as datetime))))) art on pmtct.patient_id = art.patient_id\n"
			        + "\tjoin\n"
			        + "\t(SELECT patient_id, encounter_datetime FROM encounter WHERE (encounter_type = 13 AND form_id = 27) and\n"
			        + "\t(month(encounter_datetime) = month(date(cast("
			        + dateString
			        + " as datetime))) and year(encounter_datetime) = year(date(cast("
			        + dateString
			        + " as datetime))))\n"
			        + "\t) pof on pmtct.patient_id = pof.patient_id\n"
			        + ") active_0\n"
			        + "\n"
			        + "cross join\n"
			        + "\n"
			        + "(select count(distinct(pmtct.patient_id)) active_3\n"
			        + "\tfrom\n"
			        + "\t(select patient_id, encounter_datetime  FROM encounter WHERE (encounter_type = 10  AND form_id = 16) and (year(encounter_datetime) = year(date(cast("
			        + dateString
			        + " as datetime))) and month(encounter_datetime) = month(date(cast("
			        + dateString
			        + " as datetime))))) pmtct\n"
			        + "\tjoin\n"
			        + "\t(SELECT patient_id, encounter_datetime  FROM encounter WHERE (encounter_type = 14 AND form_id = 23) and (year(encounter_datetime) = year(date(cast("
			        + dateString
			        + " as datetime))) and month(encounter_datetime) = month(date(cast("
			        + dateString
			        + " as datetime))))) art on pmtct.patient_id = art.patient_id\n"
			        + "\tjoin\n"
			        + "\t(SELECT patient_id, encounter_datetime FROM encounter WHERE (encounter_type = 13 AND form_id = 27) and\n"
			        + "\t(date(encounter_datetime) >= date(DATE_ADD(date(cast("
			        + dateString
			        + " as datetime)), INTERVAL 1 month)) and date(encounter_datetime) <= date(DATE_ADD(date(cast("
			        + dateString
			        + " as datetime)), INTERVAL 3 month)))\n"
			        + "\t) pof on pmtct.patient_id = pof.patient_id\n"
			        + ")active_3\n"
			        + "\n"
			        + "cross join\n"
			        + "\n"
			        + "(select count(distinct(pmtct.patient_id)) active_6\n"
			        + "\tfrom\n"
			        + "\t(select patient_id, encounter_datetime  FROM encounter WHERE (encounter_type = 10  AND form_id = 16) and (year(encounter_datetime) = year(date(cast("
			        + dateString
			        + " as datetime))) and month(encounter_datetime) = month(date(cast("
			        + dateString
			        + " as datetime))))) pmtct\n"
			        + "\tjoin\n"
			        + "\t(SELECT patient_id, encounter_datetime  FROM encounter WHERE (encounter_type = 14 AND form_id = 23) and (year(encounter_datetime) = year(date(cast("
			        + dateString
			        + " as datetime))) and month(encounter_datetime) = month(date(cast("
			        + dateString
			        + " as datetime))))) art on pmtct.patient_id = art.patient_id\n"
			        + "\tjoin\n"
			        + "\t(SELECT patient_id, encounter_datetime FROM encounter WHERE (encounter_type = 13 AND form_id = 27) and\n"
			        + "\t(date(encounter_datetime) > date(DATE_ADD(date(cast("
			        + dateString
			        + " as datetime)), INTERVAL 3 month)) and date(encounter_datetime <= DATE_ADD(date(cast("
			        + dateString
			        + " as datetime)), INTERVAL 6 month)))\n"
			        + "\t) pof on pmtct.patient_id = pof.patient_id\n"
			        + ")active_6\n"
			        + "\n"
			        + "cross join\n"
			        + "\n"
			        + "(select count(distinct(pmtct.patient_id)) active_12\n"
			        + "\tfrom\n"
			        + "\t(select patient_id, encounter_datetime  FROM encounter WHERE (encounter_type = 10  AND form_id = 16) and (year(encounter_datetime) = year(date(cast("
			        + dateString
			        + " as datetime))) and month(encounter_datetime) = month(date(cast("
			        + dateString
			        + " as datetime))))) pmtct\n"
			        + "\tjoin\n"
			        + "\t(SELECT patient_id, encounter_datetime  FROM encounter WHERE (encounter_type = 14 AND form_id = 23) and (year(encounter_datetime) = year(date(cast("
			        + dateString
			        + " as datetime))) and month(encounter_datetime) = month(date(cast("
			        + dateString
			        + " as datetime))))) art on pmtct.patient_id = art.patient_id\n"
			        + "\tjoin\n"
			        + "\t(SELECT patient_id, encounter_datetime FROM encounter WHERE (encounter_type = 13 AND form_id = 27) and\n"
			        + "\t(date(encounter_datetime) > date(DATE_ADD(date(cast("
			        + dateString
			        + " as datetime)), INTERVAL 6 month)) and date(encounter_datetime) <= date(DATE_ADD(date(cast("
			        + dateString
			        + " as datetime)), INTERVAL 12 month)))\n"
			        + "\t) pof on pmtct.patient_id = pof.patient_id\n" + ")active_12)";
			
			ResultSet result = statement.executeQuery(str);
			
			if (result.next()) {
				pmtctArtModel = new PmtctCohortRetentiModel();
				pmtctArtModel.setActive_0(result.getInt("active_0"));
				pmtctArtModel.setActive_3(result.getInt("active_3"));
				pmtctArtModel.setActive_6(result.getInt("active_6"));
				pmtctArtModel.setActive_12(result.getInt("active_12"));
				
				DateTimeFormatter cohortFormater = DateTimeFormatter.ofPattern("MMM-yyyy");
				String cohort = date.format(cohortFormater);
				pmtctArtModel.setCohort(cohort);
			}
			return pmtctArtModel;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return pmtctArtModel;
		}
	}
}
