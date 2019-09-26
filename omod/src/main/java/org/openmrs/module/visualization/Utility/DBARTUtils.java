package org.openmrs.module.visualization.Utility;

import org.apache.commons.lang.StringUtils;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.visualization.Model.DBConnection;

import org.openmrs.module.visualization.Model.*;

import java.sql.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DBARTUtils {
	
	private static String ENCOUNTER_TYPE_VIEW = "SELECT * FROM ENCOUNTER_TYPE_VIEW";
	
	private static String PATIENT_LINE_LIST = "SELECT * FROM PATIENT_LINE_LIST";
	
	/*This method does the utility connection for the patient*/
	/*	public void PatientUtils(Migration delegate) throws ParseException {
			
			try {
				Location location = LocationUtil.InsertLocation(delegate.getFacility());
				if (location != null) {
					//handle patient
					Patient patient = PatientUtil.InsertPatient(delegate, location);
					
					//handle encounters and obs
					EncounterUtils.InsertEncounter(delegate, location, patient);
				}
			}
			catch (Exception e) {
				throw e;
			}
		}*/
	
	public static List<EncounterType> getEncounterByEncounterTypeId(int HIV_Enrollment_Encounter_Type_Id) {
		return Context.getEncounterService().getAllEncounterTypes();
	}
	
	public ArrayList<SummaryDashboard> getEncounters() {
        try {
            DBConnection connResult = DbUtil.getNmrsConnectionDetails();

            Connection connection = DriverManager.getConnection(connResult.getUrl(), connResult.getUsername(),
                    connResult.getPassword());

            ArrayList<SummaryDashboard> summaryDashboardList = new ArrayList<>();


            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(ENCOUNTER_TYPE_VIEW);
            while (result.next()) {
                SummaryDashboard summaryDashboard = new SummaryDashboard();
                summaryDashboard.setEncounterTypeID(result.getInt(result.findColumn("EncounterTypeId")));
                summaryDashboard.setEncounterName(result.getString(result.findColumn("EncounterType")));
                summaryDashboard.setCountOfEncounter(result.getInt(result.findColumn("NumberOfEncounters")));
                summaryDashboardList.add(summaryDashboard);
            }
            return summaryDashboardList;
        } catch (SQLException e) {
            return null;
        }
    }
	
	public static List<HIVPositveCLients> getHIVPositveCLientsList(String date1, String date2, Integer ageBand, String sex) {
		try {
			DBConnection connResult = DbUtil.getNmrsConnectionDetails();
			
			Connection connection = DriverManager.getConnection(connResult.getUrl(), connResult.getUsername(),
			    connResult.getPassword());
			ArrayList<HIVPositveCLients> pateintLineList = new ArrayList<HIVPositveCLients>();
			
			List<String> months = YearMonthBetweenDates(date1, date2);
			int i = 0;
			String sql = "";
			
			for (String date : months) {
				String sqlUnioin = (i < months.size() - 1) ? "UNION" : "";
				String monthYear = monthYearDateString(date);
				
				sql += String
				        .format(
				            " SELECT  MONTHNAME('%s')  as month ,"
				                    + " COALESCE(SUM(IF( DATEDIFF(o.value_datetime , (SELECT value_datetime  FROM obs AS obs  WHERE obs.concept_id =160554 AND obs.person_id = o.person_id   LIMIT 1)) = 0, 1, 0)),0)  AS  'Sameday',"
				                    + " COALESCE(SUM(IF( DATEDIFF(o.value_datetime , (SELECT value_datetime  FROM obs AS obs  WHERE obs.concept_id =160554 AND obs.person_id = o.person_id   LIMIT 1)) BETWEEN 1 AND 7, 1, 0)),0)  AS  '1-7' ,"
				                    + " COALESCE(SUM(IF( DATEDIFF(o.value_datetime , (SELECT value_datetime  FROM obs AS obs  WHERE obs.concept_id =160554 AND obs.person_id = o.person_id   LIMIT 1)) >=8, 1, 0)),0)  AS  '>=8'"
				                    + " FROM  obs AS o WHERE o.concept_id = 159599  AND  value_datetime BETWEEN  '%s'  AND LAST_DAY('%s')  %s"
				                    + " ", date, date, date, sqlUnioin);
				i++;
			}
			
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while (result.next()) {
				HIVPositveCLients stats = new HIVPositveCLients();
				stats.setMonthText(result.getString(result.findColumn("month")));
				stats.setSamDay(result.getInt(result.findColumn("Sameday")));
				stats.setOneToSeven(result.getInt(result.findColumn("1-7")));
				stats.setGraterThanEqual8(result.getInt(result.findColumn(">=8")));
				pateintLineList.add(stats);
			}
			return pateintLineList;
		}
		catch (SQLException e) {
			return null;
		}
		
	}
	
	public static JsonResult plotHIVPositveClientsGraph(String start, String end, Integer ageBand, String sex) {

        JsonResult jsonResult = new JsonResult();
        List<HIVPositveCLients> resultList = getHIVPositveCLientsList(start, end, ageBand, sex);
        Series sr1 = new Series();
        Series sr2 = new Series();
        Series sr3 = new Series();
        List<Integer> seriesData1 = new ArrayList<>();
        List<Integer> seriesData2 = new ArrayList<>();
        List<Integer> seriesData3 = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        List<Series> seriesList = new ArrayList<>();

        for (HIVPositveCLients rs : resultList) {
            sr1.setName("SameDay");
            seriesData1.add(rs.getSamDay());
            sr2.setName("1-7");
            seriesData2.add(rs.getOneToSeven());
            sr3.setName(">=8");
            seriesData3.add(rs.getGraterThanEqual8());

            categories.add(rs.getMonthText());
        }
        sr1.setData(seriesData1);
        seriesList.add(sr1);
        sr2.setData(seriesData2);
        seriesList.add(sr2);
        sr3.setData(seriesData3);
        seriesList.add(sr3);
        jsonResult.setSeries(seriesList);
        jsonResult.setCategories(categories);
        return jsonResult;


    }
	
	public static List<TxNewAchievements> getTxNewAchievementsList(String date1, String date2) {
		try {
			ArrayList<TxNewAchievements> pateintLineList = new ArrayList<TxNewAchievements>();
			DBConnection connResult = DbUtil.getNmrsConnectionDetails();
			
			Connection connection = DriverManager.getConnection(connResult.getUrl(), connResult.getUsername(),
			    connResult.getPassword());
			List<String> months = YearMonthBetweenDates(date1, date2);
			int i = 0;
			String sql = "";
			
			for (String date : months) {
				String sqlUnioin = (i < months.size() - 1) ? "UNION" : "";
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
				LocalDate dateCal = LocalDate.parse(date1, formatter);
				
				String yearMont = sql += String
				        .format(
				            " SELECT  '%s'  as month ,"
				                    + "COALESCE(SUM(IF((SELECT COUNT(*) FROM obs WHERE obs.person_id = o.person_id AND obs.concept_id = 165766) >0 ,0 ,1)),0) AS txNewPerMonth  "
				                    + " FROM  obs AS o WHERE o.concept_id = 159599  AND  value_datetime BETWEEN  '%s'  AND LAST_DAY('%s')  %s"
				                    + " ", monthYearDateString(date), date, date, sqlUnioin);
				i++;
			}
			
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while (result.next()) {
				TxNewAchievements stats = new TxNewAchievements();
				stats.setMonthText(result.getString(result.findColumn("month")));
				stats.setTxNewPerMonth(result.getInt(result.findColumn("txNewPerMonth")));
				pateintLineList.add(stats);
			}
			return pateintLineList;
		}
		catch (SQLException e) {
			return null;
		}
		
	}
	
	public static JsonResult plotTxNewAchievmentsList(Integer target, String start, String end) {

        JsonResult jsonResult = new JsonResult();
        List<TxNewAchievements> resultList = getTxNewAchievementsList(start, end);
        int monthtlyTarget = Math.round(target / 12);
        int cumulative = 0;
        int txNewPerMonth = 0;
        Series sr1 = new Series();
        Series sr2 = new Series();
        Series sr3 = new Series();
        Series sr4 = new Series();
        List<Integer> seriesData1 = new ArrayList<>();
        List<Integer> seriesData2 = new ArrayList<>();
        List<Integer> seriesData3 = new ArrayList<>();
        List<Integer> seriesData4 = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        List<Series> seriesList = new ArrayList<>();

        for (TxNewAchievements rs : resultList) {
            cumulative += monthtlyTarget;
            txNewPerMonth += rs.getTxNewPerMonth();
            sr1.setName("Achievement");
            seriesData1.add(txNewPerMonth);

            sr2.setName("Target");
            seriesData2.add(cumulative);

            int percentage = (txNewPerMonth * 100) / target;

            sr3.setName("Percentage");
            seriesData3.add(percentage);

            sr4.setName("overallTarget");
            seriesData4.add(target);

            categories.add(rs.getMonthText());
        }
        sr1.setData(seriesData1);
        seriesList.add(sr1);

        sr2.setData(seriesData2);
        seriesList.add(sr2);

        sr3.setData(seriesData3);
        seriesList.add(sr3);

        sr4.setData(seriesData4);
        seriesList.add(sr4);


        jsonResult.setSeries(seriesList);
        jsonResult.setCategories(categories);
        return jsonResult;
    }
	
	public static List<NewPatientsCD4Analysis> newPatientsCD4Analysis(String date1, String date2) {
		try {
			ArrayList<NewPatientsCD4Analysis> pateintLineList = new ArrayList<NewPatientsCD4Analysis>();
			DBConnection connResult = DbUtil.getNmrsConnectionDetails();
			
			Connection connection = DriverManager.getConnection(connResult.getUrl(), connResult.getUsername(),
			    connResult.getPassword());
			List<String> months = YearMonthBetweenDates(date1, date2);
			int i = 0;
			String sql = "";
			
			for (String date : months) {
				String sqlUnioin = (i < 11) ? "UNION" : "";
				String monthYear = monthYearDateString(date);
				
				sql += String
				        .format(
				            " SELECT  '%s' as month ,"
				                    + "  COALESCE((SELECT  COALESCE(SUM(IF((SELECT COUNT(*) FROM obs WHERE obs.person_id = o.person_id AND obs.concept_id = 165766) >0 ,0 ,1)),0)"
				                    + " FROM  obs AS o WHERE o.concept_id = 159599  AND  value_datetime BETWEEN  DATE_SUB('%s', INTERVAL 6 MONTH)  AND LAST_DAY(DATE_SUB('%s', INTERVAL 6 MONTH))),0)  AS totalPatients, "
				                    + " COALESCE(SUM(IF(value_numeric >= 200 ,1,0)),0) AS cd4LessThan20 ,"
				                    + " COALESCE(SUM(IF(value_numeric < 20 ,1,0)),0) AS cd4GreaterThanEqual200 "
				                    + "   \n"
				                    + "   FROM  obs JOIN encounter  e  ON e.`encounter_id` = obs.`encounter_id`  WHERE  `concept_id` = 5497  AND    e.`encounter_datetime`   BETWEEN '%s'  AND LAST_DAY( '%s' ) "
				                    + " AND  person_id IN(\n"
				                    + "\n"
				                    + " SELECT DISTINCT person_id FROM  obs AS o WHERE o.concept_id = 159599  AND  value_datetime BETWEEN DATE_SUB('%s', INTERVAL 6 MONTH)   AND LAST_DAY(DATE_SUB('%s', INTERVAL 6 MONTH))) %s "
				                    + " ", monthYear, date, date, date, date, date, date, sqlUnioin);
				i++;
			}
			
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while (result.next()) {
				NewPatientsCD4Analysis stats = new NewPatientsCD4Analysis();
				stats.setMonthofReview(result.getString(result.findColumn("month")));
				stats.setTotalPatients(result.getInt(result.findColumn("totalPatients")));
				stats.setCd4LessThan20(result.getInt(result.findColumn("cd4LessThan20")));
				stats.setCd4GreaterThanEqual200(result.getInt(result.findColumn("cd4GreaterThanEqual200")));
				pateintLineList.add(stats);
			}
			return pateintLineList;
		}
		catch (SQLException e) {
			return null;
		}
		
	}
	
	public static JsonResult plotNewPatientsCD4Analysis(String start, String end) {

        JsonResult jsonResult = new JsonResult();
        List<NewPatientsCD4Analysis> resultList = newPatientsCD4Analysis(start, end);
        Series sr1 = new Series();
        Series sr2 = new Series();
        Series sr3 = new Series();
        Series sr4 = new Series();
        Series sr5 = new Series();
        Series sr6 = new Series();
        List<Integer> seriesData1 = new ArrayList<>();
        List<Integer> seriesData2 = new ArrayList<>();
        List<Integer> seriesData3 = new ArrayList<>();
        List<Integer> seriesData4 = new ArrayList<>();
        List<Integer> seriesData5 = new ArrayList<>();
        List<Integer> seriesData6 = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        List<Series> seriesList = new ArrayList<>();

        for (NewPatientsCD4Analysis rs : resultList) {
            seriesData1.add(rs.getTotalPatients());
            seriesData3.add(rs.getCd4GreaterThanEqual200());
            seriesData4.add(rs.getCd4LessThan20());
            int proportion = (rs.getTotalPatients() > 0) ? (((rs.getCd4GreaterThanEqual200() + rs.getCd4LessThan20()) * 100 / rs.getTotalPatients())) : 0;
            int percentageLessThan200 = ((rs.getCd4LessThan20() + rs.getCd4GreaterThanEqual200()) > 0) ?
                    ((rs.getCd4LessThan20() * 100 / (rs.getCd4LessThan20() + rs.getCd4GreaterThanEqual200()))) : 0;
            seriesData5.add(proportion);
            seriesData6.add(percentageLessThan200);
            categories.add(rs.getMonthofReview());
        }
        sr1.setData(seriesData1);
        seriesList.add(sr1);
        sr3.setData(seriesData3);
        seriesList.add(sr3);
        sr4.setData(seriesData4);
        seriesList.add(sr4);
        sr5.setData(seriesData5);
        seriesList.add(sr5);
        sr6.setData(seriesData6);
        seriesList.add(sr6);

        jsonResult.setSeries(seriesList);
        jsonResult.setCategories(categories);
        return jsonResult;

    }
	
	public static List<NewPatientVLCascade> newPatientVLCascade(String date1, String date2) {
		try {
			ArrayList<NewPatientVLCascade> pateintLineList = new ArrayList<NewPatientVLCascade>();
			DBConnection connResult = DbUtil.getNmrsConnectionDetails();
			
			Connection connection = DriverManager.getConnection(connResult.getUrl(), connResult.getUsername(),
			    connResult.getPassword());
			List<String> months = YearMonthBetweenDates(date1, date2);
			int i = 0;
			String sql = "";
			
			for (String date : months) {
				String sqlUnioin = (i < 11) ? "UNION" : "";
				String monthYear = monthYearDateString(date);
				
				sql += String
				        .format(
				            " SELECT  '%s' as month ,"
				                    + "  COALESCE((SELECT  COALESCE(SUM(IF((SELECT COUNT(*) FROM obs WHERE obs.person_id = o.person_id AND obs.concept_id = 165766) >0 ,0 ,1)),0)"
				                    + " FROM  obs AS o WHERE o.concept_id = 159599  AND  value_datetime BETWEEN  DATE_SUB('%s', INTERVAL 6 MONTH)  AND LAST_DAY(DATE_SUB('%s', INTERVAL 6 MONTH))),0)  AS totalPatients, "
				                    + " COALESCE(SUM(IF(`value_numeric` IS NOT NULL , 1, 0)),0) AS totalPatientsVl ,"
				                    + " COALESCE(SUM(IF(value_numeric >= 1000 ,1,0)),0) AS totalPatientsVlGreaterThan1000 ,"
				                    + " COALESCE(SUM(IF(value_numeric < 1000 ,1,0)),0) AS totalPatientsVlLessThen1000 "
				                    + "   \n"
				                    + "   FROM  obs JOIN encounter  e  ON e.`encounter_id` = obs.`encounter_id`  WHERE  `concept_id` = 856  AND    e.`encounter_datetime`   BETWEEN '%s'  AND LAST_DAY( '%s' ) "
				                    + " AND  person_id IN(\n"
				                    + "\n"
				                    + " SELECT DISTINCT person_id FROM  obs AS o WHERE o.concept_id = 159599  AND  value_datetime BETWEEN DATE_SUB('%s', INTERVAL 6 MONTH)   AND LAST_DAY(DATE_SUB('%s', INTERVAL 6 MONTH))) %s "
				                    + " ", monthYear, date, date, date, date, date, date, sqlUnioin);
				i++;
			}
			
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while (result.next()) {
				NewPatientVLCascade stats = new NewPatientVLCascade();
				stats.setMonthofReview(result.getString(result.findColumn("month")));
				stats.setTotalPatients(result.getInt(result.findColumn("totalPatients")));
				stats.setTotalPatientsVl(result.getInt(result.findColumn("totalPatientsVl")));
				stats.setTotalPatientsVlGreaterThanOrEqual1000(result.getInt(result
				        .findColumn("totalPatientsVlGreaterThan1000")));
				stats.setTotalPatientsVllessThen1000(result.getInt(result.findColumn("totalPatientsVlLessThen1000")));
				pateintLineList.add(stats);
			}
			return pateintLineList;
		}
		catch (SQLException e) {
			return null;
		}
		
	}
	
	public static JsonResult plotNewPatientVLCascade(String start, String end) {

        JsonResult jsonResult = new JsonResult();
        List<NewPatientVLCascade> resultList = newPatientVLCascade(start, end);
        Series sr1 = new Series();
        Series sr2 = new Series();
        Series sr3 = new Series();
        Series sr4 = new Series();
        Series sr5 = new Series();
        Series sr6 = new Series();
        List<Integer> seriesData1 = new ArrayList<>();
        List<Integer> seriesData2 = new ArrayList<>();
        List<Integer> seriesData3 = new ArrayList<>();
        List<Integer> seriesData4 = new ArrayList<>();
        List<Integer> seriesData5 = new ArrayList<>();
        List<Integer> seriesData6 = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        List<Series> seriesList = new ArrayList<>();

        for (NewPatientVLCascade rs : resultList) {

            seriesData1.add(rs.getTotalPatients());
            seriesData2.add(rs.getTotalPatientsVl());
            seriesData3.add(rs.getTotalPatientsVlGreaterThanOrEqual1000());
            seriesData4.add(rs.getTotalPatientsVllessThen1000());
            int viralCoverage = (rs.getTotalPatients() > 0) ? (((rs.getTotalPatientsVllessThen1000() + rs.getTotalPatientsVlGreaterThanOrEqual1000()) * 100 / rs.getTotalPatients())) : 0;
            int ViralLoadSuppression = ((rs.getTotalPatientsVllessThen1000() + rs.getTotalPatientsVlGreaterThanOrEqual1000()) > 0) ?
                    ((rs.getTotalPatientsVllessThen1000() * 100 / (rs.getTotalPatientsVllessThen1000() + rs.getTotalPatientsVlGreaterThanOrEqual1000()))) : 0;
            seriesData5.add(viralCoverage);
            seriesData6.add(ViralLoadSuppression);
            categories.add(rs.getMonthofReview());
        }
        sr1.setData(seriesData1);
        seriesList.add(sr1);
        sr2.setData(seriesData2);
        seriesList.add(sr2);
        sr3.setData(seriesData3);
        seriesList.add(sr3);
        sr4.setData(seriesData4);
        seriesList.add(sr4);
        sr5.setData(seriesData5);
        seriesList.add(sr5);
        sr6.setData(seriesData6);
        seriesList.add(sr6);

        jsonResult.setSeries(seriesList);
        jsonResult.setCategories(categories);
        return jsonResult;

    }
	
	public static JsonResult plotMissedAppointment(String date1, String date2) {

        List<String> months = YearMonthBetweenDates(date1, date2);
        JsonResult jsonResult = new JsonResult();
        int i = 0;
        String sql = "";
        List<Integer> pateints = new ArrayList<>();
        List<Integer> pateintsInAppointment = new ArrayList<>();

        Series sr1 = new Series();
        Series sr2 = new Series();
        Series sr3 = new Series();
        List<Integer> seriesData1 = new ArrayList<>();
        List<Integer> seriesData2 = new ArrayList<>();
        List<Integer> seriesData3 = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        List<Series> seriesList = new ArrayList<>();

        try {
            for (String date : months) {
                String monthYear = monthYearDateString(date);
                pateints = getPateintsInAppointment(date);
                pateintsInAppointment = getPatientInApointments(date, pateints);
                int pateintsWithMissedAppointMents = pateints.size() - pateintsInAppointment.size();
                System.out.println(pateintsWithMissedAppointMents);

                sr1.setName("Appointments");
                seriesData1.add(pateints.size());

                sr2.setName("missedAppointments");
                seriesData2.add(pateintsWithMissedAppointMents);

                categories.add(monthYear);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        sr1.setData(seriesData1);
        seriesList.add(sr1);

        sr2.setData(seriesData2);
        seriesList.add(sr2);

        jsonResult.setSeries(seriesList);
        jsonResult.setCategories(categories);

        return jsonResult;
    }
	
	public static List<Integer> getPateintsInAppointment(String chort) {
        try {
            List<Integer> patientLineList = new ArrayList<>();

            DBConnection connResult = DbUtil.getNmrsConnectionDetails();

            Connection connection = DriverManager.getConnection(connResult.getUrl(), connResult.getUsername(),
                    connResult.getPassword());

            int i = 0;
            String sql = String
                    .format(
                            " SELECT  "
                                    + " p.patient_id"
                                    + " FROM patient p JOIN encounter e ON e.patient_id = p.patient_id AND e.encounter_type = 13 "
                                    + "  WHERE  DATE_ADD((SELECT obs_datetime FROM obs WHERE person_id = p.patient_id AND obs_datetime = e.encounter_datetime AND concept_id IN (164506,164513,165702,164507,164514,165703) LIMIT 1), INTERVAL  "
                                    + " (SELECT value_numeric FROM obs WHERE person_id = p.patient_id AND obs_datetime = e.encounter_datetime AND concept_id IN (159368) LIMIT 1) DAY) BETWEEN '%s' AND LAST_DAY('%s')"
                                    + " GROUP BY p.patient_id"
                                    + " ", chort, chort);

            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                patientLineList.add(result.getInt(result.findColumn("patient_id")));
            }
            return patientLineList;
        } catch (SQLException e) {
            return  null;
        }
    }
	
	public static List<Integer> getPatientInApointments(String chort, List<Integer> patients) {
        try {
            List<Integer> patientLineList = new ArrayList<>();
            DBConnection connResult = DbUtil.getNmrsConnectionDetails();

            Connection connection = DriverManager.getConnection(connResult.getUrl(), connResult.getUsername(),
                    connResult.getPassword());

            int i = 0;
            String sql = String
                    .format("  SELECT  count(obs.person_id) as patient_ids  FROM obs AS obs JOIN encounter e ON e.patient_id = obs.person_id AND e.encounter_type = 13 AND concept_id IN (164506,164513,165702,164507,164514,165703)  AND  "
                            + " obs.`obs_datetime` BETWEEN '%s' AND LAST_DAY('%s') AND obs.`person_id` IN  ('%s') GROUP BY obs.person_id"
                            + " ", chort, chort, StringUtils.join(patients, ','));

            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                patientLineList.add(result.getInt(result.findColumn("patient_ids")));
            }
            return patientLineList;
        } catch (SQLException e) {
            return null;
        }

    }
	
	public static List<String> YearMonthBetweenDates(String date1, String date2) {

        List<String> months = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        LocalDate startDate = LocalDate.parse(date1, formatter);
        LocalDate endDate = LocalDate.parse(date2.toString(), formatter);

        while (startDate.isBefore(endDate)) {
            months.add(startDate.format(formatter));
            startDate = startDate.plusMonths(1);
        }
        return months;
    }
	
	public static String monthYearDateString(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
		;
		LocalDate yearDate = LocalDate.parse(date, formatter);
		
		return StringUtils.capitalize(Month.of(yearDate.getMonthValue()).toString().substring(0, 3).toLowerCase()) + "-"
		        + yearDate.getYear();
	}
}
