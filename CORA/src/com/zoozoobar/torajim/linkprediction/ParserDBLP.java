package com.zoozoobar.torajim.linkprediction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ParserDBLP {
	private final String[] fHEADER_START = {
			"<incollection",
			"<inproceedings",
			"<proceedings",
			"<article",
			"<book"
	};
	
	private final String[] fHEADER_END = {
			"</incollection>",
			"</inproceedings>",
			"</proceedings>",
			"</article>",
			"</book>"
	};
	
	private final String fKEY_LEFT = "key=\"";
	private final String fKEY_RIGHT = "\">";
	private final String fAUTHOR_LEFT = "<author>";
	private final String fAUTHOR_RIGHT = "</author>";
	
	private final String fTITLE_LEFT = "<title>";
	private final String fTITLE_RIGHT = "</title>";
	
	private final String fYEAR_LEFT = "<year>";
	private final String fYEAR_RIGHT = "</year>";

	private static final Set<String> fCOMMON_WORDS = new HashSet<String>();
	private static final String[] __fCOMMON_WORDS = {
		"the","of","to","and","a","in","is","it","you","that",
		"he","was","for","on","are","with","as","i","his","they",
		"be","at","one","have","this","form","or","had","by","hot",
		"but","some","what","there","we","can","out","other","were","all",
		"your","when","up","use","word","how","said","an","each","she"
		// 50 words in http://www.world-english.org/english500.htm
	};
	
	ParserDBLP() {
		for(int i=0; i<__fCOMMON_WORDS.length; i++) {
			fCOMMON_WORDS.add(__fCOMMON_WORDS[i]);
		}
	}
	
	private String getContents(String string, 
			String leftDelimiter, 
			String rightDelimiter) {
		
		String startTemp[] = string.split (leftDelimiter);
		String endTemp[];
		String contents = "";
		if(startTemp.length < 2) return "";
		
		for(int i=1; i<startTemp.length; i++) {
			endTemp = startTemp[i].split(rightDelimiter);
			if(i != 1) contents = contents.concat("\t");
			contents = contents.concat(endTemp[0].trim());
		}
		return contents.replaceAll(",", "").replaceAll("\"", "");
	}
	
	private String getKey(String token) {
		return getContents(token, fKEY_LEFT, fKEY_RIGHT);
	}
	
	private String getAuthors(String token) {
		return getContents(token, fAUTHOR_LEFT, fAUTHOR_RIGHT);
	}
	
	private String getTitle(String token) {
		return getContents(token, fTITLE_LEFT, fTITLE_RIGHT);
	}
	
	private String getYear(String token) {
		String startTemp[] = token.split (fYEAR_LEFT);
		String endTemp[];
		String contents = "";
		if(startTemp.length < 2) return "0";
		
		endTemp = startTemp[1].split(fYEAR_RIGHT);
		return	contents = contents.concat(endTemp[0].trim());
//		return getContents(token, fYEAR_LEFT, fYEAR_RIGHT);
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
		
		return keyword;
	}
	
	private String getKeywords(String token) {
		String temp[] = token.split(" ");
		String keywords = "";
		String keyword;
		
		int notab = 0;
		if (token == null || token == "") return "";
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
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = "";
		String temp = "";
		String key = "";
		String authors = "";
		String title = "";
		String keywords = "";
		int year = 0;
		
		int a = 0;
		int num_line = 0;
		int elements = 0;
		boolean makeLine = true;
		long start = System.currentTimeMillis();
		
		while((temp = br.readLine()) != null) {
			num_line++;
			if(makeLine) {
				String[] tokens;
				for(int i=0; i < fHEADER_START.length; i++) {
					tokens = temp.split(fHEADER_START[i]);
					if(tokens.length == 1) continue;
//					System.out.println("HEADER_START:" + temp + "####### line:" + num_line);
					
					key = getKey(temp);
//					System.out.println("key:[" + key + "] line:" + temp);
					makeLine = false;
					break;
				}						
			} else {
				String[] tokens;
				//System.out.println("temp:" + temp + "#####line:" + line);
				for(int i=0; i < fHEADER_END.length; i++) {
					tokens = temp.split(fHEADER_END[i]);
					if(tokens.length == 1) continue;
					makeLine = true;
					break;
				}
				if(makeLine) {
					authors = getAuthors(line);
					title = getTitle(line);
					keywords = getKeywords(title);
					year = Integer.parseInt(getYear(line));
					
					if(key.compareTo("") == 0 || 
							authors.compareTo("") == 0 || 
							title.compareTo("") == 0 || 
							keywords.compareTo("") == 0 || year == 0) {
						;
					} else {
						TableDB.insertPapers(key, title, authors, keywords, year, "DBLP");
					}
//					System.out.println(
//							"key:" + key +
//							"##authors:"+authors +
//							"##title:" + title + 
//							"##keywords:" + keywords +
//							"##year:" + year);
//	
					line = "";
					elements ++;
					continue;
				}
				line = line.concat(temp);
			}
			
//			if(num_line > 100000) break;
		}
		long end = System.currentTimeMillis();
		System.out.println("");
		System.out.println("elements:" + elements + "\tline:" + num_line);
		System.out.println( "ParserDBLP 실행 시간 : " + ( end - start )/1000.0 );
	}
}
