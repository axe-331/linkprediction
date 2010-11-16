package com.zoozoobar.torajim.linkprediction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class AuthorList {
	public static final int fMAX_PAPERCOUNT = 3;
	public static Set<String> authorList = new HashSet<String>();
	
	public static void initAuthorList() {
		String sql = "select author from authorlist where papercount < " + fMAX_PAPERCOUNT;
		Statement stmt = null;
		ResultSet rs = null;
		String author = "";
		try {
			stmt = MySQLCommand.getConn().createStatement();
			stmt.setFetchSize(1000);
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				
				author = rs.getString(1).toLowerCase();
				if(authorList.contains(author)) continue;
				authorList.add(author); 
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("sql:" + sql);
			e.printStackTrace();
		}
		
		System.out.println("authorList size:" + authorList.size());
	}
	
	public static boolean isValidAuthor(String author) {
		return !authorList.contains(author.toLowerCase());
	}
}