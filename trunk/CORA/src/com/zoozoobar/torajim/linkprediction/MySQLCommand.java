package com.zoozoobar.torajim.linkprediction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLCommand {
	private static Connection conn;
	
	public static void connServer() {
		try {
			//load jdbc driver
			Class.forName("com.mysql.jdbc.Driver");

			//connect mysql server
			StringBuffer url = new StringBuffer("jdbc:mysql://torajim.kaist.ac.kr");

			//url.append("?characterEncoding=utf-8");
			
			String id = "ucu072";
			String passwd = "ucu072";

			conn = DriverManager.getConnection(url.toString(), id, passwd);

			Statement stmt = conn.createStatement();
			stmt.executeQuery("use whjang");
			stmt.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void executeUpdate(String sql) {
		Statement stmt = null;
		int result;
		try {
			stmt = conn.createStatement();
			result = stmt.executeUpdate(sql);
		//	System.out.println(result);
			if(stmt != null) stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Connection getConn() {
		return conn;
	}
}
