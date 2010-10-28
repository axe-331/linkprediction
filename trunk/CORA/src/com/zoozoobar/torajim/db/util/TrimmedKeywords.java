package com.zoozoobar.torajim.db.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.zoozoobar.torajim.db.config.TableDefSQL;
import com.zoozoobar.torajim.db.connection.DBManager;

public class TrimmedKeywords implements TableDefSQL{
	private DBManager dbm;

	public TrimmedKeywords(){
		dbm = new DBManager();
		try{
			initTable();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}

	public void initTable() throws SQLException{
		Connection con = dbm.getConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate(SQL_DROP_BASE + TB_TRIMMED_KEYWORDS);
		stmt.executeUpdate(SQL_CREATE_TB_TRIMMED_KEYWORDS);
		stmt.close();
	}

	public void process() throws SQLException{
		Connection con = dbm.getConnection();
		Statement stmt = con.createStatement();
		Statement stmt2 = con.createStatement();
		int totalCount = 0;
		int counter = 0;
		ResultSet rset = stmt.executeQuery("select count(*) from " + TB_PAPERS);
		while(rset.next()){
			totalCount = rset.getInt("count(*)");
		}
		rset = stmt.executeQuery("select keywords from " + TB_PAPERS);
		while(rset.next()){
			System.out.println(++counter + " / " + totalCount + " is being processed.");
			String keywordStr = rset.getString("keywords");
			String[] keywords = keywordStr.split("\t");
			for(String tempKey : keywords){
				tempKey = tempKey.replaceAll("\\p{Punct}", "");
				String updateSQL = "insert into " + TB_TRIMMED_KEYWORDS + " values(\"" + tempKey + 
				"\", 1) on duplicate key update papercount = papercount + 1";
				stmt2.executeUpdate(updateSQL);
			}	
		}
		stmt2.close();
		stmt.close();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TrimmedKeywords app = new TrimmedKeywords();
		try{
			app.process();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
