package com.zoozoobar.torajim.linkprediction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TableDB {
//	private static final String fTABLE_NAME_PAPERS = "papers_temp";
	private static final String fTABLE_NAME_PAPERS = "papers";
	private static final String fTABLE_NAME_OBJECTPAIR = "objectpair";
//	private static final String fTABLE_NAME_OBJECTPAIR = "objectpair_temp";
	private static final String fTABLE_NAME_KEYWORDS = "keywords";
	private static final String fTABLE_NAME_NEIGHBORS = "neighbors";
	private static final String fTABLE_NAME_OBJECTPAIR_COMPLETE = "objectpair_temp";
	private static final String fTABLE_NAME_SCORES = "scores";
	
	private static final String fTABLE_NAME_KEYWORDLIST = "keywordlist";
	private static final String fTABLE_NAME_AUTHORLIST = "authorlist";
	
	public static void dropTablePapers() {
		String sql = "drop table if exists " + fTABLE_NAME_PAPERS;
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
	
	private static int getSizeTable(String table) {
		Statement stmt = null;
		ResultSet rst = null;
		int result = 0;
		try {
			stmt = MySQLCommand.getConn().createStatement();
			rst = stmt.executeQuery("select count(*) from "+ table);
			rst.next();
			result = rst.getInt(1);		
			
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
	
	public static void fillTableObjectPair() {
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
		System.out.println("ObjectPair table크기:" + getSizeTable(fTABLE_NAME_OBJECTPAIR));
		System.out.println( "tuple개수: "+ tuples +"\tObjectPair 실행 시간 : " + ( end - start )/1000.0 );
	}
	

	static private Set<String> authorList = new HashSet<String>();
	
	private static int selectAuthorList(String origin) {
		String sql = "select object_a from " + fTABLE_NAME_OBJECTPAIR + 
		" where origin=\"" + origin + "\"";
		
		Statement stmt = null;
		ResultSet rs = null;
		
		System.out.println("list size:"+authorList.size());
		long start = System.currentTimeMillis();
		try {
			stmt = MySQLCommand.getConn().createStatement();
			stmt.setFetchSize(1000);
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				String author = rs.getString(1); 
				if(authorList.contains(author)) continue;
				authorList.add(author);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("sql:" + sql);
			e.printStackTrace();
		}
		
		sql = "select object_b from " + fTABLE_NAME_OBJECTPAIR + 
		" where origin=\"" + origin + "\"";
		System.out.println("list size:"+authorList.size());
		System.out.println("실행 시간 : " + (System.currentTimeMillis() - start )/1000.0 );
		try {
			stmt = MySQLCommand.getConn().createStatement();
			stmt.setFetchSize(1000);
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				String author = rs.getString(1); 
				if(authorList.contains(author)) continue;
				authorList.add(author);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("sql:" + sql);
			e.printStackTrace();
		}
		
		long end = System.currentTimeMillis();
		System.out.println("");
		System.out.println("list size:"+authorList.size());
		System.out.println("실행 시간 : " + ( end - start )/1000.0 );
		
		return authorList.size();
	}
	
	private static boolean existKeyword(Set<String> keywordList, String keyword) {
		Object[] keywords = keywordList.toArray();
		
		
		for(int i=0; i<keywords.length; i++) {
			String temp = keywords[i].toString();
			if(temp.toLowerCase().equals(keyword.toLowerCase())) return true;
		}
		
		return false;
	}
	
	private static void __insertTableKeywords(String author, String keywords, String origin, int count) {
		String sql = "insert into " + fTABLE_NAME_KEYWORDS + " values(" +
				"\"" + author + "\"," +
				"\"" + keywords + "\"," +
				"\"" + count + "\"," +
				"1," +
				"\"" + origin + "\")";
		
		MySQLCommand.executeUpdate(sql);	
	}
	
	private static void __insertTableKeywords(String author, String keywords, String origin) {
		String keyword[] = keywords.split("\t");
		
		Set<String> keywordList = new HashSet<String>();
		
//		String keywordList[] = new String[keywords.length()];
		//String sql = "insert into ";		
		
		for(int i=0; i<keyword.length; i++) {
			if(keyword[i]=="") continue;
			if(existKeyword(keywordList, keyword[i])) continue;
			keywordList.add(keyword[i]);
		}
		
		String[] __keywords = new String[keywordList.size()];
		System.arraycopy(keywordList.toArray(), 0, __keywords, 0, keywordList.size());
		Arrays.sort(__keywords, String.CASE_INSENSITIVE_ORDER);
		String __keyword = "";
		for(int i=0; i<__keywords.length; i++) {
			if(i != 0) __keyword = __keyword.concat("\t");
			__keyword = __keyword.concat(__keywords[i]);
		}
//		System.out.println("count:" + keywordList.size() + "\tkeywords:" + __keyword);
		__insertTableKeywords(author, __keyword, origin, keywordList.size());
	}
	
	public static void insertTableKeywords(String origin) {
		int size = selectAuthorList(origin);
		
		Iterator<String> itr = authorList.iterator();
		int i = 0;
		long start = System.currentTimeMillis();
		
		while(itr.hasNext()) {
			i++;
			if(i%100 == 0) {
				System.out.println("size="+size + "\tcurrent:"+i + "\ttime:"+ (System.currentTimeMillis() - start) / 1000.0);
			}
			String author = itr.next();
			String sql = "select keywords from " + fTABLE_NAME_PAPERS + " where " +
					"origin=\"" + origin + "\" and (authors like \""+ author +"\\t%\" or " +
					"authors like \"%\\t"+ author +"\\t%\" or " +
					"authors like \"%\\t"+ author +"\")";
			Statement stmt = null;
			ResultSet rs = null;
			String keywords = "";
			
			try {
				stmt = MySQLCommand.getConn().createStatement();
				stmt.setFetchSize(1000);
				rs = stmt.executeQuery(sql);
				int j = 0;
				while(rs.next()) {
					j++;
					keywords = keywords.concat(rs.getString(1)+"\t");
					
					//System.out.println("keywords:"+keywords +"\t\tstring:"+keyword);
				}
				
//				if(j>1) {
//					System.out.println(i+ "sql:"+sql);
//					System.out.println("keywords:"+keywords);
//				}
				
				__insertTableKeywords(author, keywords, origin);
				
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				System.out.println("sql:" + sql);
				e.printStackTrace();
			}
		}
		
		System.out.println("end. " + (System.currentTimeMillis() - start)/1000.0);
	}
	
	private static void __updateTableKeywords(String author, String keywords, int keywordCount, int paperCount) {
		String sql = "update " + fTABLE_NAME_KEYWORDS + 
		" set keywords=\"" + keywords +"\"" +
		", keywordcount=" + keywordCount + ", papercount=" + paperCount +
		" where author=\"" + author +"\"";
		
		MySQLCommand.executeUpdate(sql);
//		System.out.println("__updateTableKeywords:" + sql);
	}
	
	public static void insertTableKeywords(String authors, String keywords, String origin) {
		String authorList[] = authors.split("\t");
		Arrays.sort(authorList, String.CASE_INSENSITIVE_ORDER);
		
		for(int i=0; i<authorList.length; i++) {
			if(!AuthorList.isValidAuthor(authorList[i])) continue;
			String sql = "select keywords,papercount from " + fTABLE_NAME_KEYWORDS + 
			" where author=\"" + authorList[i] +"\"";
			Statement stmt = null;
			ResultSet rs = null;
			String oldKeywords = "";
			try {
				stmt = MySQLCommand.getConn().createStatement();
				stmt.setFetchSize(1000);
				rs = stmt.executeQuery(sql);
				if(rs.next()) {
					oldKeywords = rs.getString(1);
					UtilKeywords util = new UtilKeywords(oldKeywords.concat("\t" + keywords));
					__updateTableKeywords(
							authorList[i], util.getKeywords(), 
							util.getKeywordCount(), rs.getInt(2)+1);
				} else {
					UtilKeywords util = new UtilKeywords(keywords);
					__insertTableKeywords(authorList[i], util.getKeywords(), origin, util.getKeywordCount());
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				System.out.println("sql:" + sql);
				e.printStackTrace();
			}
		}
	}
	
	public static void fillTableKeywords(String origin) {
		String sql = "select authors, keywords from " + fTABLE_NAME_PAPERS + 
		" where origin=\""+ origin + "\"";
		Statement stmt = null;
		ResultSet rs = null;
		String authors = "";
		String keywords = "";
		
		int i=0;
		long start = System.currentTimeMillis();
		
		try {
			stmt = MySQLCommand.getConn().createStatement();
			stmt.setFetchSize(1000);
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				i++;
				if(i%100 == 0) {
					System.out.println("current:"+i + "\ttime:"+ (System.currentTimeMillis() - start) / 1000.0);
				}
				authors = rs.getString(1);
				keywords = rs.getString(2);
				
				insertTableKeywords(authors, keywords, origin);
			}			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("sql:" + sql);
			e.printStackTrace();
		}
	}
	
	public static void createTableKeywords() {
		String sql = 
			"create table " + fTABLE_NAME_KEYWORDS + 
			"(author varchar(128) not null," +
			"keywords varchar(8192) not null," +
			"keywordcount int(8) not null," +
			"papercount int(8) not null," +
			"origin varchar(8) not null," +
			"primary key(author))";
		MySQLCommand.executeUpdate(sql);
		System.out.println("CREATE TABLE KEYWORDS!!");
	}
	
	public static void dropTableKeywords() {
		String sql = "drop table if exists " + fTABLE_NAME_KEYWORDS;
		MySQLCommand.executeUpdate(sql);
		System.out.println("DROP TABLE KEYWORDS!!");
	}
	
	private static void __insertTableNeighbors(String author, String neighbors, int count, String origin){
		String sql = "insert into " + fTABLE_NAME_NEIGHBORS + " values(" +
		"\"" + author + "\"," +
		"\"" + neighbors + "\"," +
		"\"" + count + "\"," +
		"\"" + origin + "\")";

		MySQLCommand.executeUpdate(sql);
	}
	
	private static void __updateTableNeighbors(String author, String neighbors, int count, String origin){
		String sql = "update " + fTABLE_NAME_NEIGHBORS + 
		" set neighbors=\"" + neighbors + "\"" +
		", count=" + count + 
		" where author=\"" + author + "\"";
		MySQLCommand.executeUpdate(sql);
	}
	
	private static void insertTableNeighbors(String author, String neighbors, String origin) {
		String sql = "select neighbors from " + fTABLE_NAME_NEIGHBORS +
		" where author=\"" + author +"\"" + " and origin=\"" + origin + "\"";
		Statement stmt = null;
		ResultSet rs = null;
		String oldNeighbors = "";
		try {
			stmt = MySQLCommand.getConn().createStatement();
			stmt.setFetchSize(1000);
			rs = stmt.executeQuery(sql);
			if(rs.next()) {
				oldNeighbors = rs.getString(1);
				UtilKeywords util = new UtilKeywords(oldNeighbors.concat("\t" + neighbors));
				__updateTableNeighbors(author, util.getKeywords(), util.getKeywordCount(), origin);
			} else {
				__insertTableNeighbors(author, neighbors, UtilKeywords.getKeywordCount(neighbors), origin);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("sql:" + sql);
			e.printStackTrace();
		}
	}
	
	public static void fillTableNeighbors(String origin) {
		String sql = "select authors " +
				"from " + fTABLE_NAME_PAPERS + " " +
				"where origin=\"" + origin + "\"";

		Statement stmt = null;
		ResultSet rs = null;
		
		String author = "";
		String neighbors = "";
		int a=0;
		long start = System.currentTimeMillis();
		
		try {
			stmt = MySQLCommand.getConn().createStatement();
			stmt.setFetchSize(1000);
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				a++;
				if(a%1000 == 0) {
					System.out.println("current:"+ a + "\ttime:"+ (System.currentTimeMillis() - start) / 1000.0);
				}
				String authorList[] = rs.getString(1).split("\t");
				Arrays.sort(authorList, String.CASE_INSENSITIVE_ORDER);
				int LEN = authorList.length;
				
				for(int i=0; i<LEN; i++) {
					author = authorList[i];
					if(!AuthorList.isValidAuthor(author)) continue;
					neighbors = "";
					for(int j=0; j<LEN; j++) {
						if(i == j) continue;
						if(!AuthorList.isValidAuthor(authorList[j])) continue;
						neighbors += authorList[j]+"\t";
					}
					if(neighbors.equals("")) continue;
					
					insertTableNeighbors(author, neighbors, origin);
				}
			}			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("sql:" + sql);
			e.printStackTrace();
		}
		
		System.out.println("end. " + (System.currentTimeMillis() - start)/1000.0);
	}
	
	public static void createTableNeighbors() {
		String sql = 
			"create table " + fTABLE_NAME_NEIGHBORS + 
			"(author varchar(128) not null," +
			"neighbors varchar(8192) not null," +
			"count int(8) not null," +
			"origin varchar(8) not null," +
			"primary key(author))";
		MySQLCommand.executeUpdate(sql);
	}
	
	public static void dropTableNeightbors() {
		String sql = "drop table if exists " + fTABLE_NAME_NEIGHBORS;
		MySQLCommand.executeUpdate(sql);
	}
	
	public static void createTableObjectpairComplete() {
		String sql =
			"create table " + fTABLE_NAME_OBJECTPAIR_COMPLETE +
			"(id int not null auto_increment," +
			"object_a varchar(128) not null," +
			"object_b varchar(128) not null," +
			"type varchar(2) not null," +
			"origin varchar(8) not null," +
			"year int not null," +
			"primary key (id)," +
			"unique (object_a, object_b, type)" +
			")";
	
		MySQLCommand.executeUpdate(sql);
		System.out.println("create table " + fTABLE_NAME_OBJECTPAIR_COMPLETE);
	}
	
	public static void dropTableObjectpairComplete() {
		String sql = "drop table if exists " + fTABLE_NAME_OBJECTPAIR_COMPLETE;
		MySQLCommand.executeUpdate(sql);
		System.out.println("drop table " + fTABLE_NAME_OBJECTPAIR_COMPLETE);
	}
	
	private static boolean __updateYearObjectpairComplete(String object_a, String object_b, String type, int year) {
		String sql	=	"select year from " + fTABLE_NAME_OBJECTPAIR_COMPLETE +
		" where object_a=\""+object_a +"\" and object_b=\"" + object_b + "\"" +
		"and type=\"" + type + "\"";
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = MySQLCommand.getConn().createStatement();
			stmt.setFetchSize(1000);
			rs = stmt.executeQuery(sql);
			if(rs.next() == true) {
				if(year < rs.getInt(1)) {
					rs.close();
					stmt.close();
					MySQLCommand.executeUpdate("update "+ fTABLE_NAME_OBJECTPAIR_COMPLETE + " set year="+year+
							" where object_a=\""+object_a +"\" and object_b=\"" + object_b + "\"" + 
							"and type=\"" + type + "\"");
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
	
	private static void __insertValuesObjectpairComplete(String authors, String origin, int year) {
		String tokens[] = authors.split("\t");
		Arrays.sort(tokens, String.CASE_INSENSITIVE_ORDER);
		int len = tokens.length;
		String sql;
		
		Statement stmt = null;

//		int result = 0;
		String type = year<1997?"L":"T";
		for(int i=0; i<len; i++) {
			if(!AuthorList.isValidAuthor(tokens[i])) continue;
			for(int j=i+1; j<len; j++) {
				if(!AuthorList.isValidAuthor(tokens[j])) continue;
				sql =	"insert into " + fTABLE_NAME_OBJECTPAIR_COMPLETE + "(object_a, object_b, type, origin, year)" +
						"values (\"" + tokens[i] + "\", \""+tokens[j] + "\"," + 
						"\"" + type + "\"," +
						"\""+ origin+"\""+"," + year +")";
				try {
					stmt = MySQLCommand.getConn().createStatement();
					stmt.executeUpdate(sql);
					stmt.close();
				} catch (SQLException e) {
					if(e.getErrorCode() != 1062) {
						// duplicate 에러가 아니면
						System.out.println(sql + e.getErrorCode());
						e.printStackTrace();
						System.exit(0);	
					}
					
					if(__updateYearObjectpairComplete(tokens[i], tokens[j], type, year)) {
						//TEST
						//System.exit(0);
					}
				}
			}
		}
	}
	public static void fillTableObjectpairComplete(String origin) {
		Statement stmt = null;
		ResultSet rs = null;
		long start = System.currentTimeMillis();
		int tuples = 200000000;
		try {
			stmt = MySQLCommand.getConn().createStatement();
			stmt.setFetchSize(1000);
			rs = stmt.executeQuery("select authors, origin, year from "+ fTABLE_NAME_PAPERS +" " +
					"where origin=\"" + origin +"\" limit " + tuples);
			for(int i=1; rs.next();i++) {
				__insertValuesObjectpairComplete(rs.getString(1), rs.getString(2), rs.getInt(3));
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
		
		Set<String> authorL = new HashSet<String>();
		String authorA = "";
		String authorB = "";
		try {
			stmt = MySQLCommand.getConn().createStatement();
			stmt.setFetchSize(1000);
			rs = stmt.executeQuery("select object_a, object_b " +
					"from "+ fTABLE_NAME_OBJECTPAIR_COMPLETE +" " +
					"where type=\"L\"");
			for(int i=1; rs.next();i++) {
				authorA = rs.getString(1).toLowerCase();
				authorB = rs.getString(2).toLowerCase();
				if(!authorL.contains(authorA)) authorL.add(authorA);
				if(!authorL.contains(authorB)) authorL.add(authorB);
				if(i % 1000 == 0) {
					System.out.print(".");
					if (i%10000 == 0) System.out.println(" " + i +":" + (System.currentTimeMillis() - start)/1000.0 + "s");
				}
			}
			System.out.println(" :" + (System.currentTimeMillis() - start)/1000.0 + "s");
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			stmt = MySQLCommand.getConn().createStatement(
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			stmt.setFetchSize(1000);
			rs = stmt.executeQuery("select id, object_a, object_b, type " +
					"from "+ fTABLE_NAME_OBJECTPAIR_COMPLETE +" " +
					"where type=\"T\"");
			int count = 0;
			for(int i=1; rs.next();i++) {
				authorA = rs.getString(2).toLowerCase();
				authorB = rs.getString(3).toLowerCase();
				if(!authorL.contains(authorA)) {
					System.out.println("update id:"+ rs.getInt(1));
					rs.updateString("type", "L");
					rs.updateRow();
					count++;
					authorL.add(authorA);
					if(!authorL.contains(authorB)) authorL.add(authorB);
				} else if(!authorL.contains(authorB)) {
					System.out.println("update id:"+ rs.getInt(1));
					rs.updateString("type", "L");
					rs.updateRow();
					count++;
					authorL.add(authorB);
					if(!authorL.contains(authorA)) authorL.add(authorA);
				} 
				
				if(i % 1000 == 0) {
					System.out.print(".");
					if (i%10000 == 0) System.out.println(" " + i +":" + (System.currentTimeMillis() - start)/1000.0 + "s");
				}
			}
			System.out.println(" :" + (System.currentTimeMillis() - start)/1000.0 + "s" + 
					"\tupdate:" + count);
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void printPaperCount(String origin) {
		for(int year=1900; year<2011; year++) {
			String sql = "select count(id) from " + fTABLE_NAME_PAPERS + 
			" where origin=\"" + origin +"\" and year="+year;
			int paperCount = 0;
			Statement stmt = null;
			ResultSet rs = null;
			
			try {
				stmt = MySQLCommand.getConn().createStatement();
				stmt.setFetchSize(1000);
				rs = stmt.executeQuery(sql);
				if(rs.next()) {
					paperCount = rs.getInt(1); 
				}			
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				System.out.println("sql:" + sql);
				e.printStackTrace();
			}
			
			System.out.println(year + "\t" + paperCount);
		}
	}
	
	public static void printAuthorCount(String origin) {
		int count = 0;
		int sum = 0;
		for(int year=1900; year<2011; year++) {
			String sql = "select object_a, object_b from " + fTABLE_NAME_OBJECTPAIR + 
			" where origin=\"" + origin +"\" and year="+year;
			
			String authors = "";
			
			Statement stmt = null;
			ResultSet rs = null;
			
			try {
				stmt = MySQLCommand.getConn().createStatement();
				stmt.setFetchSize(1000);
				rs = stmt.executeQuery(sql);
				while(rs.next()) {
					authors += rs.getString(1).toLowerCase().concat("\t"+rs.getString(2).toLowerCase()+"\t"); 
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				System.out.println("sql:" + sql);
				e.printStackTrace();
			}
			if(authors.equals("")) {
				System.out.println(year + "\t" + 0);
				continue;
			}
			
			count = UtilKeywords.getKeywordCount(authors);
						
			System.out.println(year + "\t" + count);
			sum += count;
		}
		System.out.println("sum\t" + sum);
	}
	
	public static void printKeywordCount(String origin) {
		int count = 0;
		int sum = 0;
		for(int year=1960; year<2001; year++) {
			String sql = "select keywords from " + fTABLE_NAME_PAPERS + 
			" where origin=\"" + origin +"\" and year="+year;
			
			String keywords = "";
			
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = MySQLCommand.getConn().createStatement();
				stmt.setFetchSize(1000);
				rs = stmt.executeQuery(sql);
				while(rs.next()) {
					keywords += rs.getString(1).toLowerCase().concat("\t"); 
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				System.out.println("sql:" + sql);
				e.printStackTrace();
			}
			if(keywords.equals("")) {
				System.out.println(year + "\t" + 0);
				continue;
			}
			UtilKeywords util = new UtilKeywords(keywords);
			count = util.getKeywordCount();
			
//			count = UtilKeywords.getKeywordCount(keywords);
			System.out.println(year + "\t" + count);
			sum += count;
		}
		System.out.println("sum\t" + sum);
	}
	
	public static void printAuthorPaper(String origin) {
		int count = 0;
		for(int paper=1; paper<40; paper++) {
			String sql = "select count(author) from " + fTABLE_NAME_KEYWORDS + 
			" where origin=\"" + origin +"\" and papercount="+paper;
			
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = MySQLCommand.getConn().createStatement();
				stmt.setFetchSize(1000);
				rs = stmt.executeQuery(sql);
				if(rs.next()) {
					count = rs.getInt(1); 
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				System.out.println("sql:" + sql);
				e.printStackTrace();
			}
			System.out.println(paper + "\t" + count);
		}
	}
	
	public static void printAuthorKeyword(String origin) {
		int count = 0;
		for(int keyword=1; keyword<160; keyword++) {
			String sql = "select count(author) from " + fTABLE_NAME_KEYWORDS + 
			" where origin=\"" + origin +"\" and keywordcount="+keyword;
			
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = MySQLCommand.getConn().createStatement();
				stmt.setFetchSize(1000);
				rs = stmt.executeQuery(sql);
				if(rs.next()) {
					count = rs.getInt(1); 
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				System.out.println("sql:" + sql);
				e.printStackTrace();
			}
			System.out.println(keyword + "\t" + count);
		}
	}
	
	public static void printAuthorNeighbor(String origin) {
		int count = 0;
		for(int neighbor=1; neighbor<100; neighbor++) {
			String sql = "select count(author) from " + fTABLE_NAME_NEIGHBORS + 
			" where origin=\"" + origin +"\" and count="+neighbor;
			
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = MySQLCommand.getConn().createStatement();
				stmt.setFetchSize(1000);
				rs = stmt.executeQuery(sql);
				if(rs.next()) {
					count = rs.getInt(1);
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				System.out.println("sql:" + sql);
				e.printStackTrace();
			}
			System.out.println(neighbor + "\t" + count);
		}
	}
	
	public static void initDataKeywordPapercount(HashMap<String, ObjectKeyword> keywordPapercount) {
		String sql = "select keyword, papercount from " + fTABLE_NAME_KEYWORDLIST; 
		int i = 0;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			i++;
			stmt = MySQLCommand.getConn().createStatement();
			stmt.setFetchSize(1000);
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				String keyword = rs.getString(1).toLowerCase();
				ObjectKeyword object = new ObjectKeyword(keyword, rs.getInt(2));
				if(keywordPapercount.put(keyword, object) == null) {
					//System.out.println("keyword:" + keyword + "\t");
				}
				i++;
//				System.out.println("keywordPapercount:" + keywordPapercount.size() + "\ti:" + i);
			}
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("sql:" + sql);
			e.printStackTrace();
		}
		
	}
	
	public static void initDataAuthorList(HashMap<String, ObjectAuthor> authorList) {
		String sql = "select author, keywords from " + fTABLE_NAME_KEYWORDS; 
		int i= 0;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = MySQLCommand.getConn().createStatement();
			stmt.setFetchSize(1000);
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				i++;
				String author = rs.getString(1);
				String keywords = rs.getString(2);
				ObjectAuthor object = new ObjectAuthor(author.toLowerCase(), keywords);
				authorList.put(author.toLowerCase(), object);
//				if(i == 1) System.out.println(object.getKeywordList().toString());
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("sql:" + sql);
			e.printStackTrace();
		}
		System.out.println(authorList.size() +"/"+ i);
		
		sql = "select author, count from " + fTABLE_NAME_NEIGHBORS;
		try {
			stmt = MySQLCommand.getConn().createStatement();
			stmt.setFetchSize(1000);
			rs = stmt.executeQuery(sql);
			i = 0;
			while(rs.next()) {
				i++;
				authorList.get(rs.getString(1).toLowerCase()).setNeighborcount(rs.getInt(2));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("sql:" + sql);
			e.printStackTrace();
		} catch (NullPointerException e) {
			try {
				System.out.println(rs.getString(1) +"/" +rs.getInt(2));
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		System.out.println(i);
	}
	
	public static void initDataPairList(HashSet<ObjectPair> pairList) {
		String sql = "select object_a, object_b, type from " + fTABLE_NAME_OBJECTPAIR_COMPLETE;
		int i=0;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = MySQLCommand.getConn().createStatement();
			stmt.setFetchSize(1000);
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				i++;
				String type = rs.getString(3);
				ObjectData.getAUTHOR_LIST().get(rs.getString(1).toLowerCase()).increaseCount(type);
				ObjectData.getAUTHOR_LIST().get(rs.getString(2).toLowerCase()).increaseCount(type);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("sql:" + sql);
			e.printStackTrace();
		}
		
		
		sql = "select id, object_a, object_b, type from " + fTABLE_NAME_OBJECTPAIR_COMPLETE;
		try {
			stmt = MySQLCommand.getConn().createStatement();
			stmt.setFetchSize(1000);
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				i++;
				int id = rs.getInt(1);
				ObjectAuthor authorA = ObjectData.getAUTHOR_LIST().get(rs.getString(2).toLowerCase());
				ObjectAuthor authorB = ObjectData.getAUTHOR_LIST().get(rs.getString(3).toLowerCase());
				String type = rs.getString(4);
				
				ObjectPair objectPair = new ObjectPair(id, authorA, authorB, type);
				pairList.add(objectPair);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("sql:" + sql);
			e.printStackTrace();
		} 
//		catch (NullPointerException e) {
//			
//		}
		System.out.println(i);
	}
	
	public static void createTableScore() {
		String sql =
			"create table " + fTABLE_NAME_SCORES +
			"(id int not null," +
			"object_a varchar(128) not null," +
			"object_b varchar(128) not null," +
			"type varchar(2) not null," +
			"neighbor varchar(2) not null," +
			"validset varchar(2) not null," +
			"scoreA int," +
			"scoreB double," +
			"scoreC double," +
			"scoreD double," +
			"primary key (id)," +
			"unique (object_a, object_b, type)" +
			")";
	
		MySQLCommand.executeUpdate(sql);
		System.out.println("create table " + fTABLE_NAME_SCORES);
	}
	
	public static void dropTableScore() {
	
	}
	
	public static void fillTableScore() {
		
	}
}