package org.openmrs.module.visualization.Utility;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.visualization.Model.*;
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
			
			String str = "select * from\n"
			        + "(\n"
			        + "(select count(patient_id) as anc FROM patient_identifier where identifier_type = 6) as anc\n"
			        + "cross join\n"
			        + "(select count(distinct(ancx.patient_id)) as ancHts from\n"
			        + "(select patient_id FROM patient_identifier where identifier_type = 6) ancx\n"
			        + "join\n"
			        + "(select patient_id FROM patient_identifier where identifier_type = 8) hts on ancx.patient_id = hts.patient_id \n"
			        + "\t) ancHts      \n"
			        + "\t\n"
			        + "\tcross join     \n"
			        + "(select count(distinct(ps.patient_id)) as prevPositive from\n"
			        + "(SELECT patient_id FROM patient_identifier WHERE identifier_type = 6) ps \n"
			        + "\t\tjoin\n"
			        + "(SELECT person_id FROM obs where concept_id = 166030 and value_coded = 1065) obPrev on ps.patient_id = obPrev.person_id\n"
			        + "\t\t) prevPositive\n"
			        + "\n"
			        + "\t\tcross join\n"
			        + "\n"
			        + "\t(select count(distinct(p.patient_id)) as newPositive from\n"
			        + "(SELECT patient_id FROM patient_identifier WHERE identifier_type = 6) p join\n"
			        + "(SELECT person_id FROM obs where concept_id = 159427 and value_coded in (703)) obPrev on p.patient_id = obPrev.person_id\n"
			        + ") as newPositive\n"
			        + "\t\n"
			        + "\tcross join\n"
			        + "\t\n"
			        + "(select count(distinct(matCort.patient_id)) as newOnTx  from    \n"
			        + "(SELECT * FROM encounter WHERE form_id = 48 and encounter_type = 29) matCort\n"
			        + "join \n"
			        + "\t(SELECT * FROM obs WHERE concept_id = 165518 and value_coded in (165520,165521)) matCortOb on matCort.patient_id = matCortOb.person_id and \n"
			        + "\tmatCortOb.encounter_id = matCortOb.encounter_id  # Initiated ART during pregnancy < 36 weeks gestation period OR Initiated ART during pregnancy >= 36 weeks gestation period\n"
			        + "\t) as newOnTx     \n"
			        + "\n"
			        + "\tcross join    \n"
			        + "\t\n"
			        + "\t(select count(distinct(matCort.patient_id)) as oldOnTx from\n"
			        + "\t(SELECT * FROM encounter WHERE form_id = 48 and encounter_type = 29) matCort\n"
			        + "\tjoin \n"
			        + "\t(SELECT * FROM obs WHERE concept_id = 165518 and value_coded = 165519) matCortOb on matCort.patient_id = matCortOb.person_id and  matCortOb.encounter_id = matCortOb.encounter_id # Prior to this pregnancy\n"
			        + "\t) as oldOnTx)";
			
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
	
	public ArrayList<PmtctCohortRetentiModel> getPmtctCohortRetention(LocalDate targetDate)
	{
		ArrayList<PmtctCohortRetentiModel> chartModels = new ArrayList<PmtctCohortRetentiModel>();
		try
		{
			LocalDate lastYear = LocalDate.of(targetDate.minus(1, ChronoUnit.YEARS).getYear(), 10, 1);
			List<LocalDate> dates = getMonths(lastYear, targetDate);
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
	
	public ArrayList<ViralSuppressionModel> getPmtctCohortViralSuppression(LocalDate now)
	{
		ArrayList<ViralSuppressionModel> cohortSuppressions = new ArrayList<ViralSuppressionModel>();
		try
		{
			LocalDate lastYear = LocalDate.of(now.minus(1, ChronoUnit.YEARS).getYear(), 10, 1);
			List<LocalDate> cohorts = getMonths(lastYear, now);
			cohorts.forEach(d ->
			{
				ViralSuppressionModel ViralSuppressionModel = getCohortViralSuppression(d);
				cohortSuppressions.add(ViralSuppressionModel);
			});

			return cohortSuppressions;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return cohortSuppressions;
		}
	}
	
	public static List<LocalDate> getMonths(LocalDate startDate, LocalDate endDate)
	{
		long numOfMonthsBetween = ChronoUnit.MONTHS.between(startDate, endDate) + 1;
		return IntStream.iterate(0, i -> i + 1)
				.limit(numOfMonthsBetween)
				.mapToObj(i -> startDate.plusMonths(i))
				.collect(Collectors.toList());
	}
	
	/*
		#All PMTCT mothers with vl results released for samples collected after Last menstrual Period
	 */
	public ViralSuppressionModel getCohortViralSuppression(LocalDate date) {
		ViralSuppressionModel ViralSuppressionModel = new ViralSuppressionModel();
		try {
			Connection connection = DriverManager.getConnection(connResult.getUrl(), connResult.getUsername(),
			    connResult.getPassword());
			Statement logic = connection.createStatement();
			
			DateTimeFormatter fmtr = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String dateStr = date.format(fmtr);
			
			String str = "select * from\n"
			        + "(select count(CASE WHEN lofObR.value_numeric < 1000 THEN 1 ELSE 0 END) AS vl_sup, # test results < 1000\n"
			        + "count(CASE WHEN lofObR.value_numeric >= 1000 THEN 1 ELSE 0 END) AS vl_non_sup # test results >= 1000\n"
			        + "\tfrom\n"
			        + "    (select patient_id, encounter_datetime, encounter_id  FROM encounter WHERE (encounter_type = 10 AND form_id = 54)\n"
			        + "    and (year(encounter_datetime) = year(date(cast('"
			        + dateStr
			        + "' as datetime))) and month(encounter_datetime) = month(date(cast('"
			        + dateStr
			        + "' as datetime))))) pmtct #PMTCT HTS Register within cohort\n"
			        + "    join\n"
			        + "\t(select patient_id, encounter_datetime, encounter_id  FROM encounter WHERE (encounter_type = 10  AND form_id = 16)) anc on pmtct.patient_id = anc.patient_id # ANC form\n"
			        + "\tjoin\n"
			        + "    (SELECT * FROM obs WHERE concept_id = 1427 AND value_datetime IS NOT NULL) ancOb on pmtct.patient_id = ancOb.person_id and anc.encounter_id = ancOb.encounter_id # Last menstural period\n"
			        + "    join\n"
			        + "\t(SELECT patient_id, encounter_datetime, encounter_id FROM encounter WHERE (encounter_type = 11 AND form_id = 21)) lof on pmtct.patient_id = lof.patient_id  # Lab order form\n"
			        + "    join\n"
			        + "    (SELECT * FROM obs WHERE concept_id = 159951 AND value_datetime IS NOT NULL) lofObS on pmtct.patient_id = lofObS.person_id and lof.encounter_id = lofObS.encounter_id  #sample collection date for viral load test\n"
			        + "    join\n"
			        + "    (SELECT * FROM obs WHERE concept_id = 165987 AND value_datetime IS NOT NULL) lofObRd on pmtct.patient_id = lofObRd.person_id and lof.encounter_id = lofObRd.encounter_id # Date result was received at the facility\n"
			        + "    join\n"
			        + "    (SELECT * FROM obs WHERE concept_id = 856 AND value_numeric IS NOT NULL) lofObR on pmtct.patient_id = lofObR.person_id and lof.encounter_id = lofObR.encounter_id # Viral load result\n"
			        + "    \n"
			        + "    where lofObS.value_datetime >= ancOb.value_datetime # ensure sample was collected after LMP\n"
			        + "    and lofObRd.value_datetime >= ancOb.value_datetime # ensure test result was released after LMP\n"
			        + "    and lofObRd.value_datetime <= date(DATE_ADD(ancOb.value_datetime, INTERVAL 36 week)) # ensure test result was released after LMP but within GA\n"
			        + ")q";
			
			ResultSet res = logic.executeQuery(str);
			
			if (res.next()) {
				ViralSuppressionModel = new ViralSuppressionModel();
				ViralSuppressionModel.setSuppressed(res.getInt("vl_sup"));
				ViralSuppressionModel.setNonSuppressed(res.getInt("vl_non_sup"));
				
				DateTimeFormatter cohortFmtr = DateTimeFormatter.ofPattern("MMM-yyyy");
				String cohort = date.format(cohortFmtr);
				ViralSuppressionModel.setCohort(cohort);
			}
			return ViralSuppressionModel;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return ViralSuppressionModel;
		}
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
			        + "\t(select patient_id, encounter_datetime  FROM encounter WHERE (encounter_type = 10  AND form_id = 16) and (year(encounter_datetime) = year(date(cast('"
			        + dateString
			        + "' as datetime))) and month(encounter_datetime) = month(date(cast('"
			        + dateString
			        + "' as datetime))))) pmtct\n"
			        + "\tjoin\n"
			        + "\t(SELECT patient_id, encounter_datetime  FROM encounter WHERE (encounter_type = 14 AND form_id = 23) and (year(encounter_datetime) = year(date(cast('"
			        + dateString
			        + "' as datetime))) and month(encounter_datetime) = month(date(cast('"
			        + dateString
			        + "' as datetime))))) art on pmtct.patient_id = art.patient_id\n"
			        + "\tjoin\n"
			        + "\t(SELECT patient_id, encounter_datetime FROM encounter WHERE (encounter_type = 13 AND form_id = 27) and\n"
			        + "\t(month(encounter_datetime) = month(date(cast('"
			        + dateString
			        + "' as datetime))) and year(encounter_datetime) = year(date(cast('"
			        + dateString
			        + "' as datetime))))\n"
			        + "\t) pof on pmtct.patient_id = pof.patient_id\n"
			        + ") active_0\n"
			        + "\n"
			        + "cross join\n"
			        + "\n"
			        + "(select count(distinct(pmtct.patient_id)) active_3\n"
			        + "\tfrom\n"
			        + "\t(select patient_id, encounter_datetime  FROM encounter WHERE (encounter_type = 10  AND form_id = 16) and (year(encounter_datetime) = year(date(cast('"
			        + dateString
			        + "' as datetime))) and month(encounter_datetime) = month(date(cast('"
			        + dateString
			        + "' as datetime))))) pmtct\n"
			        + "\tjoin\n"
			        + "\t(SELECT patient_id, encounter_datetime  FROM encounter WHERE (encounter_type = 14 AND form_id = 23) and (year(encounter_datetime) = year(date(cast('"
			        + dateString
			        + "' as datetime))) and month(encounter_datetime) = month(date(cast('"
			        + dateString
			        + "' as datetime))))) art on pmtct.patient_id = art.patient_id\n"
			        + "\tjoin\n"
			        + "\t(SELECT patient_id, encounter_datetime FROM encounter WHERE (encounter_type = 13 AND form_id = 27) and\n"
			        + "\t(date(encounter_datetime) >= date(DATE_ADD(date(cast('"
			        + dateString
			        + "' as datetime)), INTERVAL 1 month)) and date(encounter_datetime) <= date(DATE_ADD(date(cast('"
			        + dateString
			        + "' as datetime)), INTERVAL 3 month)))\n"
			        + "\t) pof on pmtct.patient_id = pof.patient_id\n"
			        + ")active_3\n"
			        + "\n"
			        + "cross join\n"
			        + "\n"
			        + "(select count(distinct(pmtct.patient_id)) active_6\n"
			        + "\tfrom\n"
			        + "\t(select patient_id, encounter_datetime  FROM encounter WHERE (encounter_type = 10  AND form_id = 16) and (year(encounter_datetime) = year(date(cast('"
			        + dateString
			        + "' as datetime))) and month(encounter_datetime) = month(date(cast('"
			        + dateString
			        + "' as datetime))))) pmtct\n"
			        + "\tjoin\n"
			        + "\t(SELECT patient_id, encounter_datetime  FROM encounter WHERE (encounter_type = 14 AND form_id = 23) and (year(encounter_datetime) = year(date(cast('"
			        + dateString
			        + "' as datetime))) and month(encounter_datetime) = month(date(cast('"
			        + dateString
			        + "' as datetime))))) art on pmtct.patient_id = art.patient_id\n"
			        + "\tjoin\n"
			        + "\t(SELECT patient_id, encounter_datetime FROM encounter WHERE (encounter_type = 13 AND form_id = 27) and\n"
			        + "\t(date(encounter_datetime) > date(DATE_ADD(date(cast('"
			        + dateString
			        + "' as datetime)), INTERVAL 3 month)) and date(encounter_datetime <= DATE_ADD(date(cast('"
			        + dateString
			        + "' as datetime)), INTERVAL 6 month)))\n"
			        + "\t) pof on pmtct.patient_id = pof.patient_id\n"
			        + ")active_6\n"
			        + "\n"
			        + "cross join\n"
			        + "\n"
			        + "(select count(distinct(pmtct.patient_id)) active_12\n"
			        + "\tfrom\n"
			        + "\t(select patient_id, encounter_datetime  FROM encounter WHERE (encounter_type = 10  AND form_id = 16) and (year(encounter_datetime) = year(date(cast('"
			        + dateString
			        + "' as datetime))) and month(encounter_datetime) = month(date(cast('"
			        + dateString
			        + "' as datetime))))) pmtct\n"
			        + "\tjoin\n"
			        + "\t(SELECT patient_id, encounter_datetime  FROM encounter WHERE (encounter_type = 14 AND form_id = 23) and (year(encounter_datetime) = year(date(cast('"
			        + dateString
			        + "' as datetime))) and month(encounter_datetime) = month(date(cast('"
			        + dateString
			        + "' as datetime))))) art on pmtct.patient_id = art.patient_id\n"
			        + "\tjoin\n"
			        + "\t(SELECT patient_id, encounter_datetime FROM encounter WHERE (encounter_type = 13 AND form_id = 27) and\n"
			        + "\t(date(encounter_datetime) > date(DATE_ADD(date(cast('"
			        + dateString
			        + "' as datetime)), INTERVAL 6 month)) and date(encounter_datetime) <= date(DATE_ADD(date(cast('"
			        + dateString
			        + "' as datetime)), INTERVAL 12 month)))\n"
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
