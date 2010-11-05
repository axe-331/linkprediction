package com.zoozoobar.torajim.linkprediction;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;


public class ObjectAuthor {
	private String author;
	private int neighborcount;
	private int countL;
	private int countT;
	private HashSet<ObjectKeyword> keywordList = new HashSet<ObjectKeyword>();
	
	
	ObjectAuthor(String author, String keywords) {
		this.author = author;
		this.neighborcount = 0;
		this.countL = 0;
		this.countT = 0;
		String tokens[] = keywords.toLowerCase().split("\t");

		for(String temp: tokens) {
			temp = temp.replaceAll("\\p{Punct}", "");
			keywordList.add(ObjectData.getKEYWORD_PAPERCOUNT().get(temp));
		}
		
		if(keywordList.size() > 10) {
			Iterator<ObjectKeyword> iter = keywordList.iterator();
			while(iter.hasNext()) {
				ObjectKeyword keyword = iter.next();
				if(keyword.getPapercount() < 5) iter.remove();
			}
		}
		if(keywordList.size() > 10) {
			Iterator<ObjectKeyword> iter = keywordList.iterator();
			while(iter.hasNext()) {
				ObjectKeyword keyword = iter.next();
				if(keyword.getPapercount() < 10) iter.remove();
			}
		}
		
		if(keywordList.size() > 10) {
			ObjectKeyword objects[] = keywordList.toArray(new ObjectKeyword[0]);
			
			Arrays.sort(objects, new Comparator<ObjectKeyword>() {
				@Override
				public int compare(ObjectKeyword o1, ObjectKeyword o2) {
					// TODO Auto-generated method stub
					return o2.getPapercount() - o1.getPapercount();
				}
			});
			
			keywordList.clear();
			for(int i=0; i<10; i++) {
				keywordList.add(objects[i]);
//				System.out.print(objects[i].papercount + "\t");
			}
//			System.out.println("");

		}
		
//		if(keywordList.size() == 10) {
//			ObjectKeyword objects[] = keywordList.toArray(new ObjectKeyword[0]);
//			for(ObjectKeyword object : objects) {
//				System.out.print(object.papercount + "\t");
//			}
//			System.out.println("");
//		}
	}
	
	public void setNeighborcount(int neighborcount) {
		this.neighborcount = neighborcount;
	}

	public String getAuthor() {
		return this.author;
	}
	
	public int getNeighborcount() {
		return this.neighborcount;
	}

	public HashSet<ObjectKeyword> getKeywordList() {
		return this.keywordList;
	}
	
	public void increaseCount(String type) {
		if(type.compareToIgnoreCase("T") == 0) this.countT++;
		if(type.compareToIgnoreCase("F") == 0) this.countL++;
	}
	public void increaseCountL() {
		this.countL ++;
	}
	
	public void increaseCountT() {
		this.countT++;
	}
	
	public int getCountL() {
		return this.countL;
	}
	
	public int getCountT() {
		return this.countT;
	}
}