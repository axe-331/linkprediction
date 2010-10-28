package com.zoozoobar.torajim.db.connection;

import java.sql.Connection;
import java.sql.DriverManager;

import com.zoozoobar.torajim.db.config.DBInfo;

public class DBManager implements DBInfo{
	private Connection con = null;
	private String host;
	private String port;
	private String dbname;
	private String user;
	private String pass;
	private String url;

	public DBManager(){
		host = HOST;
		port = PORT;
		dbname = DBNAME;
		user = USER;
		pass = PASS;
		connect();
	}
	
	public DBManager(String host, String port, String dbname, String user, String pass){
		this.host = host;
		this.port = port;
		this.dbname = dbname;
		this.user = user;
		this.pass = pass;
		connect();
	}

	public void connect(){
		try{
			url = "jdbc:mysql://" + host + ":" + port + "/" + dbname + "?blobSendChunkSize=20971520";

			Class.forName("org.gjt.mm.mysql.Driver");
			con = DriverManager.getConnection(url, user, pass);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public Connection getConnection(){
		try{
			if(con == null || con.isClosed()){
				con = DriverManager.getConnection(url, user, pass);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return con;
	}
}
