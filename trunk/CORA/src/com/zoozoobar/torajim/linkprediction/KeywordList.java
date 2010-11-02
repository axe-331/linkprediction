package com.zoozoobar.torajim.linkprediction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class KeywordList {
	public static final int fMAX_PAPERCOUNT = 10;
	public static Set<String> keywordList = new HashSet<String>();
	
	public static void initKeywordList() {
		String sql = "select keyword from keywordlist where papercount < " + fMAX_PAPERCOUNT;
		Statement stmt = null;
		ResultSet rs = null;
		String keyword = "";
		try {
			stmt = MySQLCommand.getConn().createStatement();
			stmt.setFetchSize(1000);
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				keyword = rs.getString(1);
				if(keywordList.contains(keyword)) continue;
				keywordList.add(keyword); 
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("sql:" + sql);
			e.printStackTrace();
		}
		
		printKeywordList();
	}
	
	public static boolean isValidKeyword(String keyword) {
		return !keywordList.contains(keyword);
	}
	
	public static void printKeywordList() {
//		System.out.println("");
		System.out.println("keywordList size:" + keywordList.size());
	}
}
