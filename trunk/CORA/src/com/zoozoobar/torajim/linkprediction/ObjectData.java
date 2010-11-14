package com.zoozoobar.torajim.linkprediction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class ObjectData {
//	public static Set<ObjectKeyword> KEYWORD_LIST = new HashSet<ObjectKeyword>();
	private static HashSet<ObjectPair> PAIR_LIST = new HashSet<ObjectPair>();
	private static HashMap<String, ObjectAuthor> AUTHOR_LIST = new HashMap<String, ObjectAuthor> (400000, (float)0.75);
//	public static Set<ObjectKeyword> KEYWORD_LIST = new HashSet<ObjectKeyword>();
	private static HashMap<String, ObjectKeyword> KEYWORD_PAPERCOUNT = new HashMap<String, ObjectKeyword>(400000, (float)0.75);
	private static HashMap<String, ObjectAuthorKeywords> AUTHOR_KEYWORDS = new HashMap<String, ObjectAuthorKeywords>(400000, (float) 0.75);

	public static void initData() {
		long start = System.currentTimeMillis();
		long end;
		
		TableDB.initDataAuthorKeywords(AUTHOR_KEYWORDS);
		end = System.currentTimeMillis();
		System.out.println("AuthorKeywords:" + (end-start)/1000.0 + "s");
		
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

	public static HashSet<ObjectPair> getPAIR_LIST() {
		return PAIR_LIST;
	}

	public static HashMap<String, ObjectAuthor> getAUTHOR_LIST() {
		return AUTHOR_LIST;
	}

	public static HashMap<String, ObjectKeyword> getKEYWORD_PAPERCOUNT() {
		return KEYWORD_PAPERCOUNT;
	}
	
	public static HashMap<String, ObjectAuthorKeywords> getAUTHOR_KEYWORDS() {
		return AUTHOR_KEYWORDS;
	}
	
	public static void printValidset() {
		Iterator<ObjectPair> iter = PAIR_LIST.iterator();
		while(iter.hasNext()) {
			ObjectPair ob = iter.next();
			ObjectAuthor A = ob.getAuthorA();
			ObjectAuthor B = ob.getAuthorB();
//			System.out.print("|" + A.getAuthor() + "|" + A.getCountL() + "|" + A.getCountT());
//			System.out.println("|" + B.getAuthor() + "|" + B.getCountL() + "|" + B.getCountT());
			if(A.getCountL() > 2 && A.getCountT() > 2) {
				System.out.print("|" + A.getAuthor() + "|");
			}
			if(B.getCountL() >2 && B.getCountT() > 2) {
				System.out.println("|" + B.getAuthor() + "|");
			}
		}
	}
	
	public static void printTest() {
		Iterator<ObjectPair> iter = PAIR_LIST.iterator();
		while(iter.hasNext()) {
			ObjectPair ob = iter.next();
			switch(ob.getId()) {
			case 3595:
			case 3599:
			case 4503:
			case 3596:
				if(Double.isInfinite(ob.keywordScoreD)) {
					System.out.println(ob.getSqlValues() + "////" + ob.keywordScoreD);
				}
			}
		}	
	}
	
	public static void printAuthorSize() {
		System.out.println(AUTHOR_LIST.size());
	}
}
