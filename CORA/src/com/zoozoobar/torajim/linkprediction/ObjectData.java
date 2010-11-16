package com.zoozoobar.torajim.linkprediction;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class ObjectData {
//	public static Set<ObjectKeyword> KEYWORD_LIST = new HashSet<ObjectKeyword>();
	private static HashSet<ObjectPair> PAIR_LIST = new HashSet<ObjectPair>();
	private static HashSet<ObjectPair> FALSESET_PAIR_LIST = new HashSet<ObjectPair>();
	private static HashMap<String, ObjectAuthor> AUTHOR_LIST = new HashMap<String, ObjectAuthor> (400000, (float)0.75);
//	public static Set<ObjectKeyword> KEYWORD_LIST = new HashSet<ObjectKeyword>();
	private static HashMap<String, ObjectKeyword> KEYWORD_PAPERCOUNT = new HashMap<String, ObjectKeyword>(400000, (float)0.75);
	private static HashMap<String, ObjectAuthorKeywords> AUTHOR_KEYWORDS = new HashMap<String, ObjectAuthorKeywords>(400000, (float) 0.75);

//	private static HashMap<String, ObjectPair> PAIR_HASH_MAP = new HashMap<String, ObjectPair>();
//	private static HashMap<String, ObjectPair> FALSESET_HASH_MAP = new HashMap<String, ObjectPair>();
	
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

	public static HashSet<ObjectPair> getFALSESET_PAIR_LIST() {
		return FALSESET_PAIR_LIST;
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
	
	public static void makeFalseset() {
		HashMap<String, ObjectPair> PAIR_HASH_MAP = new HashMap<String, ObjectPair>();
		HashMap<String, ObjectPair> FULLSET_HASH_MAP = new HashMap<String, ObjectPair>(40000000, (float)0.75);
		
		Iterator<ObjectPair> iter = PAIR_LIST.iterator();
		while(iter.hasNext()) {
			ObjectPair ob = iter.next();
			String key = ob.getAuthorA().getAuthor() + "\t" + ob.getAuthorB().getAuthor();
			PAIR_HASH_MAP.put(key, ob);
		}
		
		Collection<ObjectAuthor> co = AUTHOR_LIST.values();
		ObjectAuthor[] authorList = new ObjectAuthor[co.size()];
		Iterator<ObjectAuthor> it = co.iterator();
		
		int i = 0;
		while(it.hasNext()) {
			ObjectAuthor ob = it.next();
			authorList[i] = ob;
			i++;
		}
		
		Arrays.sort(authorList, new Comparator<ObjectAuthor>() {
			@Override
			public int compare(ObjectAuthor o1, ObjectAuthor o2) {
				// TODO Auto-generated method stub
				return o1.getAuthor().compareToIgnoreCase(o2.getAuthor());
			}
		});
		
		System.out.println("authorList.length:" + authorList.length);
		int id = 0;
		int j;
		int boolNeighbor = 0;
		while(true) {
			i = (int) (Math.random()*3653);
			j = (int) (Math.random()*3653);
			if(i >= j) continue;
			ObjectAuthor A = authorList[i];
			ObjectAuthor B = authorList[j];
			if(A.getNeighborcount() == 0 || B.getNeighborcount() == 0) continue;
			String key = A.getAuthor() + "\t" + B.getAuthor();
			if(PAIR_HASH_MAP.containsKey(key)) continue;
			if(FULLSET_HASH_MAP.containsKey(key)) continue;
			FULLSET_HASH_MAP.put(key, null);
			ObjectPair ob = new ObjectPair(id, A, B, "L", "cora");
			//if(ob.getNeighbor().equals("F") && boolNeighbor < 4223) continue; 
			FALSESET_PAIR_LIST.add(ob);
			id++;
			boolNeighbor++;
			if(id == 5189) break;
		}
	}
}
