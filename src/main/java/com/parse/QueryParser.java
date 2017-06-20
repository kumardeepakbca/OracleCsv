package com.parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
			String strDateFormat = "EEEE";
			SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
			System.out.println("Current day of week in EEEE format : " + sdf.format(new Date()));
			String dayName=sdf.format(new Date());
			int year = Calendar.getInstance().get(Calendar.YEAR);
			int month = Calendar.getInstance().get(Calendar.MONTH);
			month=month+1;
			String m = "";
			if (month < 10) {
				m = "0" + month;
			}else{
				m=month+"";
			}
			int day = Calendar.getInstance().get(Calendar.DATE);
			String d="";
			if (day < 10) {
				d = "0" + day;
			}else{
				d=day+"";
			}
			properties.load(new FileInputStream(propertiesFilePath));
			properties.load(new FileInputStream(properties.getProperty("config.path")));
			properties.load(new FileInputStream(properties.getProperty("sql.path")));
			String fileType=properties.getProperty("file.type");
			if(fileType == null || "".equals(fileType.trim())){
				fileType="xls";
			}
			System.out.println("fileType :- " + fileType);
			System.out.println("dayName :- " + dayName);
			String fileName = "MT559_110_Locked_Report_" + m + d + year	+ "."+fileType;
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
				String query="";
				if(dayName != null && dayName.equalsIgnoreCase("Monday") ){
					System.out.println("Monday query will excute>>>>>>>>");
					query = properties.getProperty("app.monday.query");
				}else{
					query = properties.getProperty("app.query");
				}
				String reportLocation = properties.getProperty("report.path");
				String dateQuery= properties.getProperty("app.query.alter.date.session");
				
				String sessionQuery = properties
						.getProperty("app.query.init.session");
				System.out.println("driverClass :- " + driverClass);
				System.out.println("url :- " + url);
				System.out.println("username :- " + username);
				System.out.println("password :- " + password);
				System.out.println("Report Location :- " + reportLocation);
				System.out.println("dateQuery :---------- " + dateQuery);
				
				Class.forName(driverClass);
				Connection con = DriverManager.getConnection(url, username,
						password);
				Statement stmt = con.createStatement();
			
				if (sessionQuery != null && !"".equals(sessionQuery.trim())) {
					System.out
							.println("Session Init Query executing :---------------- ");
					stmt.executeUpdate(sessionQuery);
				}
				if (dateQuery != null && !"".equals(dateQuery.trim())) {
					System.out
							.println("Session Date Query executing :---------------- ");
					stmt.executeUpdate(dateQuery);
				}
				System.out.println("Query executing :---------------- ");
				System.out.println("Query:---------------- "+query);
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
				
				if(fileType != null){
					XSSFWorkbook wb=new XSSFWorkbook();
					XSSFSheet sheet= wb.createSheet(sheetName);
					for (int i=0;i< colsMapHeader.size();i++) {
						sheet.setColumnWidth(i, 5000);
					}
					sheet.setColumnWidth(2, 5000);
					sheet.setColumnWidth(3, 5000);
					sheet.setColumnWidth(6, 5000);
					sheet.setColumnWidth(5, 5000);
					sheet.setColumnWidth(8, 5000);
					sheet.setColumnWidth(9, 5000);	
					sheet.setColumnWidth(20, 8000);	
					sheet.setColumnWidth(21, 8000);	
					sheet.setColumnWidth(30, 8000);	
					XSSFSheet sheet1 = wb.createSheet("SQL");
					Iterator hiter = colsMapHeader.iterator();
					int coulmn = 0;
					int rowno = 0;
					
					
					CellStyle style=null;
					CellStyle styleData=null;
				     // Creating a font
				        XSSFFont font= wb.createFont();
				        font.setFontHeightInPoints((short)11);
				        font.setBold(true);
				        font.setItalic(false);

				        style=wb.createCellStyle();
				        style.setAlignment(CellStyle.ALIGN_CENTER);
						style.setBorderTop(XSSFCellStyle.BORDER_THIN);
				        style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
				        style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
				        style.setBorderRight(XSSFCellStyle.BORDER_THIN);
						style.setFont(font);
						
						styleData=wb.createCellStyle();
				        styleData.setAlignment(CellStyle.ALIGN_CENTER);
						styleData.setBorderTop(XSSFCellStyle.BORDER_THIN);
				        styleData.setBorderBottom(XSSFCellStyle.BORDER_THIN);
				        styleData.setBorderLeft(XSSFCellStyle.BORDER_THIN);
				        styleData.setBorderRight(XSSFCellStyle.BORDER_THIN);
					
					XSSFRow row = sheet.createRow(rowno++);
					while (hiter.hasNext()) {
						XSSFCell cell = row.createCell(coulmn++);
						cell.setCellStyle(style);
						cell.setCellValue((String) hiter.next());
					}
					XSSFRow rowSql = sheet1.createRow(0);
					XSSFCell cellSql = rowSql.createCell(0);
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
						CreationHelper createHelper = wb.getCreationHelper();
						while (diter.hasNext()) {
							XSSFCell cell = row.createCell(coulmn);
							if(coulmn == 0 || coulmn == 4){
								int val=Integer.parseInt((String)diter.next());
								cell.setCellStyle(styleData);
								cell.setCellValue(val);
							} else if(coulmn == 2 || coulmn == 3 || coulmn == 6 || coulmn == 8 || coulmn == 9){
								
								CellStyle cellStyle = wb.createCellStyle();
								cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy hh:mm:ss"));
								cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
								cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
								cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
								cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
								cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
								cell.setCellStyle(cellStyle);
								String dateVal=(String)diter.next(); 
								if(dateVal != null && !"".equals(dateVal.trim())){
									Date date=new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").parse(dateVal);  
									cell.setCellValue(date);
								}
								
							}else if(coulmn == 5 ){
								
								CellStyle cellStyle = wb.createCellStyle();
								cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy hh:mm:ss"));
								cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
								cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
								cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
								cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
								cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
								cell.setCellStyle(cellStyle);
								String dateVal=(String)diter.next(); 
								if(dateVal != null && !"".equals(dateVal.trim())){
									Date date=new SimpleDateFormat("MM/dd/yy hh:mm:ss").parse(dateVal);  
									cell.setCellValue(date);
								}
							}else{
								cell.setCellStyle(styleData);
								String val=(String)diter.next();
								val="Test";
							/*	if(val != null){
									String asFormula = "\"" + val + "\"";
									cell.setCellType(SXSSFCell.CELL_TYPE_FORMULA);
									cell.setCellFormula(asFormula);
								}else{*/
									cell.setCellValue(val);
								//}
								
							}
							coulmn=coulmn+1;
						}
						Thread.sleep(50);
					}
					System.out.println("Data added successfully !!!");
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
					
				}
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
