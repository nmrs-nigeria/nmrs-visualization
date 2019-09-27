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
				            " SELECT  '%s'  as month ,"
				                    + " count(*)  AS  'total',"
				                    + " COALESCE(SUM(IF( DATEDIFF(o.value_datetime , (SELECT value_datetime  FROM obs AS obs  WHERE obs.concept_id =160554 AND obs.person_id = o.person_id  AND  value_datetime BETWEEN  '%s'  AND LAST_DAY('%s')  LIMIT 1)) is NULL, 1, 0)),0)  AS  'unknown',"
				                    + " COALESCE(SUM(IF( DATEDIFF(o.value_datetime , (SELECT value_datetime  FROM obs AS obs  WHERE obs.concept_id =160554 AND obs.person_id = o.person_id AND  value_datetime BETWEEN  '%s'  AND LAST_DAY('%s')  LIMIT 1)) = 0, 1, 0)),0)  AS  'Sameday',"
				                    + " COALESCE(SUM(IF( DATEDIFF(o.value_datetime , (SELECT value_datetime  FROM obs AS obs  WHERE obs.concept_id =160554 AND obs.person_id = o.person_id  AND  value_datetime BETWEEN  '%s'  AND LAST_DAY('%s') LIMIT 1)) BETWEEN 1 AND 7, 1, 0)),0)  AS  '1-7' ,"
				                    + " COALESCE(SUM(IF( DATEDIFF(o.value_datetime , (SELECT value_datetime  FROM obs AS obs  WHERE obs.concept_id =160554 AND obs.person_id = o.person_id AND  value_datetime BETWEEN  '%s'  AND LAST_DAY('%s')  LIMIT 1)) >=8, 1, 0)),0)  AS  '>=8'"
				                    + " FROM  obs AS o WHERE o.concept_id = 159599  AND value_datetime BETWEEN  '%s'  AND LAST_DAY('%s')  %s"
				                    + " ", monthYearDateString(date), date, date, date, date, date, date, date, date, date,
				            date, sqlUnioin);
				i++;
			}
			
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while (result.next()) {
				HIVPositveCLients stats = new HIVPositveCLients();
				stats.setMonthText(result.getString(result.findColumn("month")));
				stats.setTotal(result.getInt(result.findColumn("total")));
				stats.setUnknown(result.getInt(result.findColumn("unknown")));
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
        Series sr4 = new Series();
        List<Double> seriesData1 = new ArrayList<>();
        List<Double> seriesData2 = new ArrayList<>();
        List<Double> seriesData3 = new ArrayList<>();
        List<Double> seriesData4 = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        List<Series> seriesList = new ArrayList<>();

        for (HIVPositveCLients rs : resultList) {
            sr1.setName("SameDay");
             double samedayPercent = (rs.getTotal() == 0) ?0 :(rs.getSamDay()*100)/rs.getTotal();
            seriesData1.add(samedayPercent);
            sr2.setName("1-7");
            double oneToSevenPercent = (rs.getTotal() == 0) ?0 : (rs.getOneToSeven()*100)/rs.getTotal();
            seriesData2.add(oneToSevenPercent);
            sr3.setName(">=8");
            double graterThanEqual8Percent = (rs.getTotal() == 0) ?0 : (rs.getGraterThanEqual8()*100)/rs.getTotal();
            seriesData3.add(graterThanEqual8Percent);

            sr4.setName("Unknown");
            double unknowPercentage = (rs.getTotal() == 0) ?0 : (rs.getUnknown()*100)/rs.getTotal();
            seriesData4.add(unknowPercentage);

            categories.add(rs.getMonthText());
        }
        sr1.setDataDouble(seriesData1);
        seriesList.add(sr1);
        sr2.setDataDouble(seriesData2);
        seriesList.add(sr2);
        sr3.setDataDouble(seriesData3);
        seriesList.add(sr3);
        sr4.setDataDouble(seriesData4);
        seriesList.add(sr4);
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
				                    + " COALESCE(SUM(IF((SELECT COUNT(*)  FROM obs AS obs  WHERE  obs.person_id = o.person_id  AND   voided =0 AND  concept_id = 160563 LIMIT 1 )= 0, 1, 0)),0)    AS txNewPerMonth  "
				                    + " FROM  obs AS o WHERE o.concept_id = 159599    AND value_datetime BETWEEN  '%s'  AND LAST_DAY('%s')  %s"
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
        int i =0;
        int cumulative = 0;
        int txNewPerMonth = 0;
        Series sr1 = new Series();
        Series sr2 = new Series();
        Series sr3 = new Series();
        Series sr4 = new Series();
        List<Integer> seriesData1 = new ArrayList<>();
        List<Integer> seriesData2 = new ArrayList<>();
        List<Double> seriesData3 = new ArrayList<>();
        List<Integer> seriesData4 = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        List<Series> seriesList = new ArrayList<>();

        for (TxNewAchievements rs : resultList) {
            cumulative += monthtlyTarget;
            txNewPerMonth += rs.getTxNewPerMonth();
            sr1.setName("Achievement");
            seriesData1.add(txNewPerMonth);

            sr2.setName("Target");
            cumulative = (i == resultList.size()-1) ? target :cumulative;
            seriesData2.add(cumulative);

            double percentage = (txNewPerMonth * 100) / target;

            sr3.setName("Percentage");
            seriesData3.add(percentage);

            sr4.setName("overallTarget");
            seriesData4.add(target);

            categories.add(rs.getMonthText());
            i++;
        }
        sr1.setData(seriesData1);
        seriesList.add(sr1);

        sr2.setData(seriesData2);
        seriesList.add(sr2);

        sr3.setDataDouble(seriesData3);
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
				            "SELECT '%s' as month , COALESCE(SUM(IF((SELECT COUNT(*)  FROM obs AS obs  WHERE  obs.person_id = o.person_id  AND   voided =0 AND  concept_id = 160563 LIMIT 1 )= 0, 1, 0)),0)    AS totalPatients,"
				                    + "COALESCE(SUM(IF("
				                    + "(SELECT MAX(value_numeric) FROM obs WHERE obs.person_id = o.person_id AND obs.concept_id = 5497 AND   voided =0  ) >=200, 1, 0 )),0) AS cd4GreaterThanEqual200, "
				                    + "COALESCE(SUM(IF("
				                    + "(SELECT MAX(value_numeric) FROM obs WHERE obs.person_id = o.person_id AND obs.concept_id = 5497 AND   voided =0 ) <200, 1, 0 )),0) AS cd4LessThan20  "
				                    + "FROM  obs AS o WHERE o.concept_id = 159599    AND value_datetime BETWEEN  '%s'  AND LAST_DAY('%s')    %s"
				                    + " ", monthYear, date, date, sqlUnioin);
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
            sr2.setName("TotalPatients");
            seriesData1.add(rs.getTotalPatients());
            sr2.setName("Cd4GreaterThanEqual200");
            seriesData2.add(rs.getCd4GreaterThanEqual200());
            sr3.setName("Cd4LessThan20");
            seriesData3.add(rs.getCd4LessThan20());
            int proportion = (rs.getTotalPatients() > 0) ? (((rs.getCd4GreaterThanEqual200() + rs.getCd4LessThan20()) * 100 / rs.getTotalPatients())) : 0;
            int percentageLessThan200 = ((rs.getCd4LessThan20() + rs.getCd4GreaterThanEqual200()) > 0) ?
                    ((rs.getCd4LessThan20() * 100 / (rs.getCd4LessThan20() + rs.getCd4GreaterThanEqual200()))) : 0;
            seriesData4.add(proportion);
            seriesData5.add(percentageLessThan200);
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
				            "SELECT '%s' as month , COALESCE(SUM(IF((SELECT COUNT(*)  FROM obs AS obs  WHERE  obs.person_id = o.person_id  AND   voided =0 AND  concept_id = 160563 LIMIT 1 )= 0, 1, 0)),0)    AS totalPatients,"
				                    + "COALESCE(SUM(IF("
				                    + "(SELECT MAX(value_numeric) FROM obs WHERE obs.person_id = o.person_id AND obs.concept_id = 856 AND   voided =0 AND   obs.obs_datetime >= DATE_ADD('%s', INTERVAL 6 MONTH)) IS NOT NULL, 1, 0 )),0) AS totalPatientsVl, "
				                    + "COALESCE(SUM(IF("
				                    + "(SELECT MAX(value_numeric) FROM obs WHERE obs.person_id = o.person_id AND obs.concept_id = 856 AND   voided =0 AND   obs.obs_datetime >= DATE_ADD('%s', INTERVAL 6 MONTH)) >=1000, 1, 0 )),0) AS totalPatientsVlGreaterThan1000, "
				                    + "COALESCE(SUM(IF("
				                    + "(SELECT MAX(value_numeric) FROM obs WHERE obs.person_id = o.person_id AND obs.concept_id = 856 AND   voided =0 AND   obs.obs_datetime >= DATE_ADD('%s', INTERVAL 6 MONTH)) <1000, 1, 0 )),0) AS totalPatientsVlLessThen1000 "
				                    + "FROM  obs AS o WHERE o.concept_id = 159599  AND value_datetime BETWEEN  '%s'  AND   voided =0 AND  LAST_DAY('%s')    %s"
				                    
				                    + " ", monthYear, date, date, date, date, date, sqlUnioin);
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
        List<Integer> pateintsInAppointment =new ArrayList<>();

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
                int pateintsWithMissedAppointMents  = 0;
                if(pateintsInAppointment != null &&  pateints != null){
                    pateintsWithMissedAppointMents = (pateintsInAppointment.size() == 0 || pateintsInAppointment.size()==0 )? 0 : (pateints.size() - pateintsInAppointment.size());
                }else{
                    pateintsWithMissedAppointMents = 0;
                }



                System.out.println("M"+pateintsWithMissedAppointMents +"|" + "T"+pateints.size()+"|"+date);

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

                            " SELECT obs.person_id,"
                                    +" duration.value_numeric,"
                                    +" obs.`encounter_id`,"
                                    +" duration.obs_datetime, "
                                    + " DATE_ADD(duration.obs_datetime, INTERVAL duration.value_numeric DAY) AS next_appointment"
                                    + "  FROM obs INNER JOIN (SELECT value_numeric AS value_numeric , encounter_id ,`obs_datetime` FROM obs WHERE concept_id = 159368) AS duration  ON duration.encounter_id = obs.encounter_id "
                                    + " WHERE  DATE_ADD(duration.obs_datetime, INTERVAL duration.value_numeric DAY)  BETWEEN '%s' AND LAST_DAY('%s') "
                                    + " AND concept_id = 165708 GROUP BY person_id"
                                    + "", chort, chort);

            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                patientLineList.add(result.getInt(result.findColumn("person_id")));
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
                    .format(" SELECT DISTINCT  patient_id as missed_appointments FROM encounter JOIN obs ON obs.`encounter_id` = encounter.`encounter_id`  "
                            + " WHERE encounter_datetime BETWEEN '%s' AND LAST_DAY('%s') AND patient_id IN  "
                            + "(%s) AND encounter_type =13 AND obs.concept_id IN (165708)  "
                            + " ", chort, chort, StringUtils.join(patients, ','));

            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                patientLineList.add(result.getInt(result.findColumn("missed_appointments")));
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
