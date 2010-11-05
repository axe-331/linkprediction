package com.zoozoobar.torajim.linkprediction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ObjectData {
//	public static Set<ObjectKeyword> KEYWORD_LIST = new HashSet<ObjectKeyword>();
	private static HashSet<ObjectPair> PAIR_LIST = new HashSet<ObjectPair>();
	private static HashMap<String, ObjectAuthor> AUTHOR_LIST = new HashMap<String, ObjectAuthor> (400000, (float)0.75);
//	public static Set<ObjectKeyword> KEYWORD_LIST = new HashSet<ObjectKeyword>();
	private static HashMap<String, ObjectKeyword> KEYWORD_PAPERCOUNT = new HashMap<String, ObjectKeyword>(400000, (float)0.75);

	public static void initData() {
		long start = System.currentTimeMillis();
		long end;
		TableDB.initDataKeywordPapercount(KEYWORD_PAPERCOUNT);
		end = System.currentTimeMillis();
		System.out.println("KeywordPapercount:" + (end-start)/1000.0 + "s");
		
		
		TableDB.initDataAuthorList(AUTHOR_LIST);
		end = System.currentTimeMillis();
		System.out.println("AuthorList:" + AUTHOR_LIST.size() +"\t" +(end - start)/1000.0 + "s");
		
		TableDB.initDataPairList(PAIR_LIST);
		end = System.currentTimeMillis();
		System.out.println("PairList" + PAIR_LIST.size() + "\t" + (end - start)/1000.0 + "s");
	}

	public static Set<ObjectPair> getPAIR_LIST() {
		return PAIR_LIST;
	}

	public static HashMap<String, ObjectAuthor> getAUTHOR_LIST() {
		return AUTHOR_LIST;
	}

	public static HashMap<String, ObjectKeyword> getKEYWORD_PAPERCOUNT() {
		return KEYWORD_PAPERCOUNT;
	}
}
