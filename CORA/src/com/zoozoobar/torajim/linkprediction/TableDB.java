package com.zoozoobar.torajim.linkprediction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class TableDB {
//	private static final String fTABLE_NAME_PAPERS = "papers_temp";
	private static final String fTABLE_NAME_PAPERS = "papers";
	private static final String fTABLE_NAME_OBJECTPAIR = "objectpair";
	
	public static void dropTablePapers() {
		String sql = "drop table " + fTABLE_NAME_PAPERS;
		MySQLCommand.executeUpdate(sql);
	}
	
	public static void createTablePapers() {
		String sql = 
			"CREATE TABLE " + fTABLE_NAME_PAPERS +
			"(id varchar(512) PRIMARY KEY," +
			"title varchar(512) not null," +
			"authors varchar(512) not null, " +
			"keywords varchar(512) not null," +
			"year int(4) not null," +
			"origin varchar(8) not null" +
			")";
		MySQLCommand.executeUpdate(sql);
	}
	
	public static void insertPapers(
			String id, String title, 
			String authors, String keywords, 
			int year, String origin) {
		String sql = "insert into " + fTABLE_NAME_PAPERS + " values("+
		"\""+id +"\"," +
		"\""+title +"\"," +
		"\""+authors +"\"," +
		"\""+keywords +"\"," +
		"\""+year +"\"," +
		"\""+origin +"\")";
		
		MySQLCommand.executeUpdate(sql);
	}
		
	public static void createTableObjectPair() {
		String sql =
			"create table " + fTABLE_NAME_OBJECTPAIR +
			"(id int not null auto_increment," +
			"object_a varchar(128) not null," +
			"object_b varchar(128) not null," +
			"origin varchar(8) not null," +
			"year int not null," +
			"primary key (id)," +
			"unique (object_a, object_b)" +
			")";
	
		MySQLCommand.executeUpdate(sql);
	}
	
	public static void dropTableObjectPair() {
		String sql = "drop tables " + fTABLE_NAME_OBJECTPAIR;
		MySQLCommand.executeUpdate(sql);
	}
	
	private static boolean __updateYearObjectPair(String object_a, String object_b, int year) {
		String sql	=	"select object_a, object_b, year from " + fTABLE_NAME_OBJECTPAIR +
		" where object_a=\""+object_a +"\" and object_b=\"" + object_b + "\"";
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = MySQLCommand.getConn().createStatement();
			stmt.setFetchSize(1000);
			rs = stmt.executeQuery(sql);
			if(rs.next() == true) {
				if(year < rs.getInt(3)) {
					rs.close();
					stmt.close();
					MySQLCommand.executeUpdate("update "+ fTABLE_NAME_OBJECTPAIR + " set year="+year+
							" where object_a=\""+object_a +"\" and object_b=\"" + object_b + "\"");
					// System.out.println("year changed.");
					return true;
				} else {
					rs.close();
					stmt.close();
					return false;
				}
			}
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("sql:" + sql);
			e.printStackTrace();
		}
		
		return false;
	}
	
	private static int getSizeObjectPair() {
		Statement stmt = null;
		ResultSet rst = null;
		int result = 0;
		try {
			stmt = MySQLCommand.getConn().createStatement();
			rst = stmt.executeQuery("select id from "+ fTABLE_NAME_OBJECTPAIR);
			rst.last();
			result = rst.getRow();
			
			rst.close();
			stmt.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return result;
	}
	
	public static int getSizePapers() {
		Statement stmt = null;
		ResultSet rst = null;
		int result = 0;
		try {
			stmt = MySQLCommand.getConn().createStatement();
			rst = stmt.executeQuery("select authors, origin, year from "+ fTABLE_NAME_PAPERS);
			rst.last();
			result = rst.getRow();
			
			rst.close();
			stmt.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return result;
	}
	
	private static void __insertValuesObjectPair(String authors, String origin, int year) {
		String tokens[] = authors.split("\t");
		Arrays.sort(tokens, String.CASE_INSENSITIVE_ORDER);
		int len = tokens.length;
		String sql;
		
		Statement stmt = null;

		int result = 0;
		for(int i=0; i<len; i++) {
			for(int j=i+1; j<len; j++) {
				sql =	"insert into " + fTABLE_NAME_OBJECTPAIR + "(object_a, object_b, origin, year)" +
						"values (\"" + tokens[i] + "\", \""+tokens[j] + "\"," + "\""+ origin+"\""+"," + year +")";
				try {
					stmt = MySQLCommand.getConn().createStatement();
					result = stmt.executeUpdate(sql);
					stmt.close();
				} catch (SQLException e) {
					if(e.getErrorCode() != 1062) {
						// duplicate 에러가 아니면
						System.out.println(sql + e.getErrorCode());
						e.printStackTrace();
						System.exit(0);	
					}
					
					if(__updateYearObjectPair(tokens[i], tokens[j], year)) {
						//TEST
						//System.exit(0);
					}
				}
			}
		}
	}
	
	public static void insertValuesObjectPair() {
		Statement stmt = null;
		ResultSet rs = null;
		long start = System.currentTimeMillis();
		int tuples = 200000000;
		try {
			stmt = MySQLCommand.getConn().createStatement();
			stmt.setFetchSize(1000);
			//rs = stmt.executeQuery("select authors, origin, year from "+ fTABLE_NAME_PAPERS +" where origin=\"dblp\" limit " + tuples);
			rs = stmt.executeQuery("select authors, origin, year from "+ fTABLE_NAME_PAPERS +" limit " + tuples);
			for(int i=1; rs.next();i++) {
//				System.out.println(rs.getString("authors") + rs.getString("origin") + rs.getInt("year"));
				__insertValuesObjectPair(rs.getString(1), rs.getString(2), rs.getInt(3));
				if(i % 1000 == 0) {
					System.out.print(".");
					if (i%10000 == 0) System.out.println(" " + i +":" + (System.currentTimeMillis() - start)/1000.0 + "s");
				}
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		long end = System.currentTimeMillis();
		System.out.println("");
		System.out.println("ObjectPair table크기:" + getSizeObjectPair());
		System.out.println( "tuple개수: "+ tuples +"\tObjectPair 실행 시간 : " + ( end - start )/1000.0 );
	}
}
