package com.zoozoobar.torajim.linkprediction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashSet;
import java.util.Set;

public class ParserCORA {
	
	ParserCORA() {
		for(int i=0; i<__fCOMMON_WORDS.length; i++) {
			fCOMMON_WORDS.add(__fCOMMON_WORDS[i]);
		}
	}
	private static final Set<String> fCOMMON_WORDS = new HashSet<String>();
	private static final String[] __fCOMMON_WORDS = {
		"the","of","to","and","a","in","is","it","you","that",
		"he","was","for","on","are","with","as","i","his","they",
		"be","at","one","have","this","form","or","had","by","hot",
		"but","some","what","there","we","can","out","other","were","all",
		"your","when","up","use","word","how","said","an","each","she"
		// 50 words in http://www.world-english.org/english500.htm
	};
	
	private final String fAUTHOR_LEFT = "<author>";
	private final String fAUTHOR_RIGHT = "</author>";
	private final String[] AUTHOR_NAME_DELIMETER = {",", "and ", "&"};
	
	private final String fTITLE_LEFT = "<title>";
	private final String fTITLE_RIGHT = "</title>";
	
	private final String fYEAR_LEFT = "<year>";
	private final String fYEAR_RIGHT = "</year>";
	private final String fBOOKTITLE_LEFT = "<booktitle>";
	private final String fBOOKTITLE_RIGHT = "</booktitle>";
	
	private boolean isValidLine(String[] tokens) {
		return (tokens.length >= 3);
	}
	
	private boolean __authorParseEnd(String string) {
		String authors[];
		int i;
	
		for(i=0; i<AUTHOR_NAME_DELIMETER.length; i++) {
			authors = string.split(AUTHOR_NAME_DELIMETER[i]);
			if(authors.length == 1) continue;
			return false;
		}
		return true;
	}
	
	private String getAuthor(String string) {
		String authors[];
		String author; 
		String returnString = "";
		int i,j;
		if (string == null) return null;
		if (__authorParseEnd(string)) return string.trim();
		
		for(i=0; i<AUTHOR_NAME_DELIMETER.length; i++) {
			authors = string.split(AUTHOR_NAME_DELIMETER[i]);
			if(authors.length == 1) continue;
			
			for(j=0; j<authors.length; j++) {
				author = getAuthor(authors[j]);
				if(author.compareTo("") == 0) continue;
				returnString += author.replaceAll(",", "").replaceAll("&","") + "\t";
				//System.out.println("returnString:" + returnString);
			}
			return returnString.substring(0, returnString.lastIndexOf("\t"));
		}
		
		System.out.println("Unexpected case...");
		return returnString;
	}
	
	private String __extractContent(String string, String startDelimeter, String endDelimeter) {
		String temp_start[] = string.split(startDelimeter);
		if (temp_start.length < 2) {
			//System.out.println("ERROR\ttoken:"+string);
			return null;
		}
		String temp_end[] = temp_start[1].split(endDelimeter);
		return temp_end[0].trim().replaceAll(",", "").replaceAll("\"", "");
	}
	
	private String getAuthors(String string) {
		//String authorToken = __extractContent(string, fAUTHOR_LEFT, fAUTHOR_RIGHT);
		//return getAuthor(authorToken);
		
		String temp_start[] = string.split(fAUTHOR_LEFT);
		if (temp_start.length < 2) {
			//System.out.println("ERROR\ttoken:"+string);
			return null;
		}
		String temp_end[] = temp_start[1].split(fAUTHOR_RIGHT);
		return getAuthor(temp_end[0].trim().replaceAll("\"", ""));
		
	}
	
	private String getTitle(String string){
		return __extractContent(string, fTITLE_LEFT, fTITLE_RIGHT);
	}
	
	private int getYear(String string){
		String year = __extractContent(string, fYEAR_LEFT, fYEAR_RIGHT);
		if(year == null) {
			year = __extractContent(string, fBOOKTITLE_LEFT, fBOOKTITLE_RIGHT);
			if(year == null) {
				year = __extractContent(string, fTITLE_LEFT, fTITLE_RIGHT);
				if (year == null) return 0;
			}
			
		}
		StringCharacterIterator iter = new StringCharacterIterator(year);
		int startIndex = -1;
		int endIndex = -1;
		for(char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) {
			if(startIndex == -1 && Character.isDigit(c)){
				startIndex = iter.getIndex();
				continue;
			}
			if(startIndex != -1 && !Character.isDigit(c)) { 
				break;
			}
		}
		if(startIndex == -1) return 0;
		endIndex = iter.getIndex();
		year = year.substring(startIndex, endIndex);
		
		int result = Integer.parseInt(year);
		if(result < 1000 || result >= 10000) return 0; 
		return result;
	}
	
	private String __extractKeyword(String keyword) {
		if(keyword.length() == 0) return "";
		switch(keyword.charAt(keyword.length()-1)) {
		case '.':
		case ':':
		case ',':
			keyword = keyword.substring(0,keyword.length() -1);
			break;
		default:
			break;
		}
		
		if(fCOMMON_WORDS.contains(keyword.toLowerCase())) return "";
		
		//System.out.println("keywords:"+keyword);
		//System.out.println("keywords2:"+keyword.replaceAll("\"", ""));
		
//		return keyword.replaceAll("\"", "");
		
		return keyword;
	}
	
	private String getKeywords(String token) {
		String temp[] = token.split(" ");
		String keywords = "";
		String keyword;
		
		int notab = 0;
		
		for(int i=0; i<temp.length; i++) {
			keyword = __extractKeyword(temp[i].trim());
			if(keyword == "") {
				notab++;
				continue;
			}
			if(i != notab) keywords = keywords.concat("\t");
			keywords = keywords.concat(keyword);
		}
		
		return keywords;
	}
	
	public void startParser(File file) throws IOException {
		//File file = new File(file_name);
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		
		String temp = "";
		String key = "";
		String author = "";
		String title = "";
		String keywords = "";
		int year = 0;
		
		int a = 0;
		
		long start = System.currentTimeMillis();

		while((temp = br.readLine()) != null) {
			String[] tokens = temp.split("\t");
			if (!isValidLine(tokens)) continue;
			author = getAuthors(tokens[2]);
			if (author == null || author.compareTo("") == 0) {
				//System.out.println("No author tokens:["+tokens+"]");
				continue;
			}
			
			if(key.compareTo(tokens[0]) == 0) continue;	// same key
			
			key = tokens[0];
			if(key.compareTo("") == 0) continue;
			title = getTitle(tokens[2]);
			if(title == null || title.compareTo("") == 0) continue;
			keywords = getKeywords(title);
			if(keywords.compareTo("") == 0) continue;
			year = getYear(tokens[2]);
			if(year == 0) continue;
			TableDB.insertPapers(key, title, author, keywords, year, "cora");
//			System.out.println("key:["+ key + "]\tauthor:["+author+"]\ttitle:["+title+"]\t" +
//					"year:["+year+"]\tkeywords:[" + keywords + "]");
//			if(a > 20) break;
			a++;
		}
	
		long end = System.currentTimeMillis();
		System.out.println("");
		System.out.println( "ParserCORA 실행 시간 : " + ( end - start )/1000.0 );
	}
}
