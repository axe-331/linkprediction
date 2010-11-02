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
		Set<String> lowercaseKeywordList = new HashSet<String>();
		String lowercaseKeyword = "";
		
		for(int i=0; i<keyword.length; i++) {
			if(keyword[i]=="") continue;
			if(!KeywordList.isValidKeyword(keyword[i])) continue;
			
			lowercaseKeyword = keyword[i].toLowerCase();
			if(lowercaseKeywordList.contains(lowercaseKeyword)) continue;
			keywordList.add(keyword[i]);
			lowercaseKeywordList.add(lowercaseKeyword);
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
			if(keywordList.contains(keyword[i].toLowerCase())) continue;
			keywordList.add(keyword[i].toLowerCase());
		}
		
//		String[] __keywords = new String[keywordList.size()];
//		System.arraycopy(keywordList.toArray(), 0, __keywords, 0, keywordList.size());
//		Arrays.sort(__keywords, String.CASE_INSENSITIVE_ORDER);
//		String __keyword = "";
//		for(int i=0; i<__keywords.length; i++) {
//			if(i != 0) __keyword = __keyword.concat("\t");
//			__keyword = __keyword.concat(__keywords[i]);
//		}
		return keywordList.size();
	}
	
	public int getKeywordCount() {
		return this.keywordsCount;
	}
	
	public String getKeywords() {
		return this.keywords;
	}
}
