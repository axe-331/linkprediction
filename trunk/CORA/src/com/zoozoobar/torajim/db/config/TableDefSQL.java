package com.zoozoobar.torajim.db.config;

/**
 * To inspect how many papers to which a certain keyword belongs
 * @author torajim
 *
 */
public interface TableDefSQL {
	public static final String TB_TRIMMED_KEYWORDS = "keywordlist";
	public static final String TB_TRIMMED_AUTHORS = "authorlist";
	public static final String TB_PAPERS = "papers";
	
	public static final String SQL_CREATE_TB_TRIMMED_KEYWORDS = "CREATE TABLE `" + DBInfo.DBNAME + "`.`" + TB_TRIMMED_KEYWORDS + "` ("
		+ "`keyword` VARCHAR(255),"
		+ "`papercount` INTEGER NOT NULL,"
		+ "PRIMARY KEY(`keyword`)"
		//+ ", INDEX `" + TB_TRIMMED_KEYWORDS + "_idx1`(`authors`)"
		+ ")ENGINE = MyISAM;";
	
	public static final String SQL_CREATE_TB_TRIMMED_AUTHORS = "CREATE TABLE `" + DBInfo.DBNAME + "`.`" + TB_TRIMMED_AUTHORS + "` ("
	+ "`author` VARCHAR(255),"
	+ "`papercount` INTEGER NOT NULL,"
	+ "PRIMARY KEY(`author`)"
	+ ")ENGINE = MyISAM;";
	
	public static final String SQL_DROP_BASE = "drop table if exists ";
}
