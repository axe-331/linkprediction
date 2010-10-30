package com.zoozoobar.torajim.linkprediction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UtilKeywords {
	public String keywords = "";
	public int keywordsCount = 0;
	
	UtilKeywords(String keywords) {
		String keyword[] = keywords.split("\t");
		Set<String> keywordList = new HashSet<String>();		
		
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
		this.keywords = __keyword;
		this.keywordsCount = keywordList.size();
	}
	
	public static int getKeywordCount(String keywords) {
		String keyword[] = keywords.split("\t");
		Set<String> keywordList = new HashSet<String>();		
		
		for(int i=0; i<keyword.length; i++) {
			if(keyword[i]=="") continue;
			if(keywordList.contains(keyword[i])) continue;
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
		return keywordList.size();
	}
	
	private static boolean existKeyword(Set<String> keywordList, String keyword) {
		Object[] keywords = keywordList.toArray();
		
		for(int i=0; i<keywords.length; i++) {
			String temp = keywords[i].toString();
			if(temp.toLowerCase().equals(keyword.toLowerCase())) return true;
		}
		
		return false;
	}
	
	public int getKeywordCount() {
		return this.keywordsCount;
	}
	
	public String getKeywords() {
		return this.keywords;
	}
}
