package com.parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class QueryParser {

	/*
	 * public static void main(String[] args) throws IOException,
	 * InterruptedException { String outputPath="";
	 * 
	 * try { loadData(outputPath,args[0],args[1]);
	 * //loadData(outputPath,"C:\\data\\"
	 * ,"C:\\properties\\database.properties");
	 * 
	 * } catch (Exception e) { System.out.println(e.getMessage());
	 * e.printStackTrace();
	 * 
	 * }
	 * 
	 * }
	 */

	public static void main(String[] args) throws Exception {
		String outputPath = "";

		try {
			loadData(outputPath, args[0], args[1]);
			// loadData(outputPath,"C:\\data\\","C:\\properties\\database.properties");

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();

		}
		Properties properties = new Properties();
		 properties.load(new FileInputStream(args[1]));
		/*properties.load(new FileInputStream(
				"C:\\properties\\database.properties"));*/
		// Recipient's email ID needs to be mentioned.
		String to = properties.getProperty("mail.smtp.to");// change accordingly

		// Sender's email ID needs to be mentioned
		String from = properties.getProperty("mail.smtp.from");// change accordingly
		final String username = properties.getProperty("mail.smtp.username");// change
																				// accordingly
		final String password = properties.getProperty("mail.smtp.password");// change
																				// accordingly
		
		// Assuming you are sending email through relay.jangosmtp.net
		String host = properties.getProperty("mail.smtp.host");
		System.out.println("To email :- " + to);
		System.out.println("From email :- " + from);
		System.out.println("smpt username :- " + username);
		System.out.println("smpt password :- " + password);
		System.out.println("smpt host :- " + host);
		System.out.println("smpt port :- " + properties.getProperty("mail.smtp.port"));
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", properties.getProperty("mail.smtp.port"));

		// Get the Session object.
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});

		try {
			// Create a default MimeMessage object.
			Message message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));

			// Set Subject: header field
			message.setSubject("Testing Subject");

			// Now set the actual message
			message.setText("Hello, this is sample for to check send "
					+ "email using JavaMailAPI ");
			MimeBodyPart messageBodyPart2 = new MimeBodyPart();
			String csvLocation = args[1];
			int year = Calendar.getInstance().get(Calendar.YEAR);
			int month = Calendar.getInstance().get(Calendar.MONTH);
			int day = Calendar.getInstance().get(Calendar.DATE);
			String filename = "110_Report_" + day + month + year + ".csv";
			if (csvLocation == null || "".equals(csvLocation.trim())) {
				csvLocation = "C:\\data\\" + filename;
			} else {
				csvLocation = csvLocation + filename;
			}

			DataSource source = new FileDataSource(filename);
			messageBodyPart2.setDataHandler(new DataHandler(source));
			messageBodyPart2.setFileName(filename);
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart2);

			// 6) set the multiplart object to the message object
			message.setContent(multipart);
			// Send message
			Transport.send(message);

			System.out.println("Sent message successfully....");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	private static Map<Integer, String> getCSVHeaderData() {
		Map<Integer, String> csvData = new HashMap<Integer, String>();
		csvData.put(0, "LOAN_NUM");
		csvData.put(1, "WORKFLOW");
		csvData.put(2, "RFP_DT");
		csvData.put(3, "APP_DT");
		csvData.put(4, "MARKETTYPE");
		csvData.put(5, "CPIKEY");
		csvData.put(6, "BASERATEDATE");
		csvData.put(7, "ACTIONTAKEN");
		csvData.put(8, "ACTIONDATE");
		csvData.put(9, "LOCKDATE");
		csvData.put(10, "LOANAMOUNT");
		csvData.put(11, "LOANTERM");
		csvData.put(12, "LTV");
		csvData.put(13, "CLTV");
		csvData.put(14, "ACLTV");
		csvData.put(15, "FICOSCORE");
		csvData.put(16, "RATE");
		csvData.put(17, "POINTS");
		csvData.put(18, "LOCKPERIODDAYS");
		csvData.put(19, "LOCKSTATUS");
		csvData.put(20, "AGENCY");
		csvData.put(21, "PROGRAMDESC");
		csvData.put(22, "PROP_TYPE");
		csvData.put(23, "OCCUPANCY");
		csvData.put(24, "STATECODE");
		csvData.put(25, "LOANTYPE");
		csvData.put(26, "LOANPURPOSE");
		csvData.put(27, "AMORTIZATIONTYPE");
		csvData.put(28, "Channel");
		csvData.put(29, "HLDCUSTOMERTYPE");
		csvData.put(30, "BRANCH");
		csvData.put(31, "PRICEVALUE");
		csvData.put(32, "BRANCHDISCRETION");
		csvData.put(33, "DOCTYPE");

		return csvData;
	}

	static void loadData(String outputPath, String csvLocation,
			String propertiesFilePath) {
		System.out.println("csvLocation :- " + csvLocation);
		System.out.println("propertiesFilePath :- " + propertiesFilePath);
		ResultSet rs = null;
		Properties properties = new Properties();
		try {
			// String csvLocation = args[1];
			int year = Calendar.getInstance().get(Calendar.YEAR);
			int month = Calendar.getInstance().get(Calendar.MONTH);
			int day = Calendar.getInstance().get(Calendar.DATE);
			String fileName = "110_Report_" + day + month + year + ".csv";
			if (csvLocation == null || "".equals(csvLocation.trim())) {
				csvLocation = "C:\\data\\" + fileName;
			} else {
				csvLocation = csvLocation + fileName;
			}
			System.out.println("csvLocation full path :- " + csvLocation);
			properties.load(new FileInputStream(propertiesFilePath));

			int tableCount = 0;
			List colsData = new ArrayList();
			String timePeriod = "";
			Map<Integer, String> csvData = new HashMap<Integer, String>();
			FileWriter writer = null;
			File f = new File(csvLocation);
			if (!f.exists()) {
				List colsMapHeader = new ArrayList();

				for (Map.Entry<Integer, String> map : getCSVHeaderData()
						.entrySet()) {
					colsMapHeader.add(map.getValue());
				}
				writer = new FileWriter(csvLocation, true);
				CSVUtils.writeLine(writer, colsMapHeader);

			}else{
				writer = new FileWriter(csvLocation, true);
			}

			try {

				String driverClass = properties
						.getProperty("app.jdbc.driverClassName");
				String url = properties.getProperty("app.jdbc.url");
				String username = properties.getProperty("app.jdbc.username");
				String password = properties.getProperty("app.jdbc.password");
				String query = properties.getProperty("app.query");
				System.out.println("driverClass :- " + driverClass);
				System.out.println("url :- " + url);
				System.out.println("username :- " + username);
				System.out.println("password :- " + password);
				System.out.println("QUERY :- " + query);
				Class.forName(driverClass);
				// step2 create the connection object
				Connection con = DriverManager.getConnection(url, username,
						password);

				// step3 create the statement object
				Statement stmt = con.createStatement();
				// step4 execute query
				// rs=stmt.executeQuery(Constants.query);
				System.out.println("Query executing :---------------- ");
				rs = stmt.executeQuery(query);
				System.out.println("Result Set :- " + rs);
				// File
				ResultSetMetaData rsmd = rs.getMetaData();

				int columnsNumber = rsmd.getColumnCount();
				while (rs.next()) {
					csvData = new HashMap<Integer, String>();
					for(int i=1;i<= columnsNumber;i++){
						if(rs.getObject(i) != null){
							csvData.put(i-1, rs.getObject(i).toString());
						}else{
							csvData.put(i-1, "No Value");
						}
					}
					List colsMapData = new ArrayList();
					for (Map.Entry<Integer, String> map : csvData.entrySet()) {
						colsMapData.add(map.getValue());
					}
					CSVUtils.writeLine(writer, colsMapData);
					Thread.sleep(50);
					System.out.println("Data added successfully !!!");
					
				}
				writer.flush();
				writer.close();
				con.close();

			} catch (Exception e) {
				writer.flush();
				writer.close();
				e.printStackTrace();// TODO: handle exception
			}
			

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();

		}
		

	}

}
