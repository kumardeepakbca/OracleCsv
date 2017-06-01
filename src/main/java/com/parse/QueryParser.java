package com.parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class QueryParser {

	public static void main(String[] args) throws Exception {
		String outputPath = "";

		try {
			loadData(outputPath, args[0]);

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();

		}

	}

	private static LinkedHashMap getCSVHeaderData() {
		LinkedHashMap csvData = new LinkedHashMap();
		csvData.put("0", "LOAN_NUM");
		csvData.put("1", "WORKFLOW");
		csvData.put("2", "RFP_DT");
		csvData.put("3", "APP_DT");
		csvData.put("4", "MARKETTYPE");
		csvData.put("5", "CPIKEY");
		csvData.put("6", "BASERATEDATE");
		csvData.put("7", "ACTIONTAKEN");
		csvData.put("8", "ACTIONDATE");
		csvData.put("9", "LOCKDATE");
		csvData.put("10", "LOANAMOUNT");
		csvData.put("11", "LOANTERM");
		csvData.put("12", "LTV");
		csvData.put("13", "CLTV");
		csvData.put("14", "ACLTV");
		csvData.put("15", "FICOSCORE");
		csvData.put("16", "RATE");
		csvData.put("17", "POINTS");
		csvData.put("18", "LOCKPERIODDAYS");
		csvData.put("19", "LOCKSTATUS");
		csvData.put("20", "AGENCY");
		csvData.put("21", "PROGRAMDESC");
		csvData.put("22", "PROP_TYPE");
		csvData.put("23", "OCCUPANCY");
		csvData.put("24", "STATECODE");
		csvData.put("25", "LOANTYPE");
		csvData.put("26", "LOANPURPOSE");
		csvData.put("27", "AMORTIZATIONTYPE");
		csvData.put("28", "Channel");
		csvData.put("29", "HLDCUSTOMERTYPE");
		csvData.put("30", "BRANCH");
		csvData.put("31", "PRICEVALUE");
		csvData.put("32", "BRANCHDISCRETION");
		csvData.put("33", "DOCTYPE");

		return csvData;
	}

	static void loadData(String outputPath,
			String propertiesFilePath) {
		System.out.println("propertiesFilePath :- " + propertiesFilePath);
		ResultSet rs = null;
		Properties properties = new Properties();
		try {
			int year = Calendar.getInstance().get(Calendar.YEAR);
			int month = Calendar.getInstance().get(Calendar.MONTH);
			String m = "";
			if (month < 10) {
				m = "0" + month;
			}
			int day = Calendar.getInstance().get(Calendar.DATE);
			String d="";
			if (day < 10) {
				d = "0" + day;
			}
			String fileName = "MT559_110_Locked_Report_" + d + m + year
					+ ".xlsx";
			properties.load(new FileInputStream(propertiesFilePath));
			properties.load(new FileInputStream(properties.getProperty("config.path")));
			properties.load(new FileInputStream(properties.getProperty("sql.path")));

			int tableCount = 0;
			List colsData = new ArrayList();
			String timePeriod = "";
			Map csvData = new HashMap();

			try {
				List colsMapHeader = new ArrayList();
				int rowCount = 0;
				
				String driverClass = properties
						.getProperty("app.jdbc.driverClassName");
				String url = properties.getProperty("app.jdbc.url");
				String username = properties.getProperty("app.jdbc.username");
				String password = properties.getProperty("app.jdbc.password");
				String query = properties.getProperty("app.query");
				String reportLocation = properties.getProperty("report.path");
				
				
				String sessionQuery = properties
						.getProperty("app.query.init.session");
				System.out.println("driverClass :- " + driverClass);
				System.out.println("url :- " + url);
				System.out.println("username :- " + username);
				System.out.println("password :- " + password);
				//System.out.println("QUERY :- " + query);
				//System.out.println("Session init query :- " + sessionQuery);
				System.out.println("Report Location :- " + reportLocation);
				
				System.out.println("sql.path :---------- " + properties.getProperty("app.query"));
				System.out.println("config.path :----------- " + properties.getProperty("report.path"));
				
				Class.forName(driverClass);
				// step2 create the connection object
				Connection con = DriverManager.getConnection(url, username,
						password);

				// step3 create the statement object
				Statement stmt = con.createStatement();
				// step4 execute query
				// rs=stmt.executeQuery(Constants.query);
				if (sessionQuery != null && !"".equals(sessionQuery.trim())) {
					System.out
							.println("Session Init Query executing :---------------- ");
					stmt.executeUpdate(sessionQuery);
				}
				System.out.println("Query executing :---------------- ");
				rs = stmt.executeQuery(query);
				System.out.println("Result Set :- " + rs);
				// File
				ResultSetMetaData rsmd = rs.getMetaData();

				LinkedHashMap map = getCSVHeaderData();
				Iterator iter = map.entrySet().iterator();

				while (iter.hasNext()) {
					Map.Entry mEntry = (Map.Entry) iter.next();
					colsMapHeader.add(mEntry.getValue());
				}

				String sheetName = "MT559_110_Report";// name of sheet

				HSSFWorkbook wb = new HSSFWorkbook();
				HSSFSheet sheet = wb.createSheet(sheetName);
				HSSFSheet sheet1 = wb.createSheet("SQL");
				Iterator hiter = colsMapHeader.iterator();
				int coulmn = 0;
				int rowno = 0;
				
				HSSFRow row = sheet.createRow(rowno++);
				while (hiter.hasNext()) {
					HSSFCell cell = row.createCell(coulmn++);
					cell.setCellValue((String) hiter.next());
				}
				
				HSSFRow rowSql = sheet1.createRow(0);
				HSSFCell cellSql = rowSql.createCell(0);
				cellSql.setCellValue(query);
				int columnsNumber = rsmd.getColumnCount();
				while (rs.next()) {
					csvData = new LinkedHashMap();
					for (int i = 1; i <= columnsNumber; i++) {
						if (rs.getObject(i) != null) {
							csvData.put(i - 1 + "", rs.getObject(i).toString());
						} else {
							csvData.put(i - 1 + "", null);
						}
					}
					List colsMapData = new ArrayList();

					Iterator iter1 = csvData.entrySet().iterator();
					while (iter1.hasNext()) {
						Map.Entry mEntry = (Map.Entry) iter1.next();
						colsMapData.add(mEntry.getValue());
					}
					row = sheet.createRow(rowno++);
					Iterator diter = colsMapData.iterator();
					coulmn = 0;
					while (diter.hasNext()) {
						HSSFCell cell = row.createCell(coulmn);
						cell.setCellValue((String) diter.next());
						coulmn=coulmn+1;
					}
					Thread.sleep(50);
					System.out.println("Data added successfully !!!");

				}
				String rlocation=reportLocation+fileName;
				File directory = new File(String.valueOf(rlocation));
				if(!directory.exists()){
					directory.getParentFile().mkdirs();
				 }           
				FileOutputStream fileOut = new FileOutputStream(rlocation);

				// write this workbook to an Outputstream.
				wb.write(fileOut);
				fileOut.flush();
				fileOut.close();
				con.close();

			} catch (Exception e) {
				e.printStackTrace();// TODO: handle exception
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();

		}

	}

}
