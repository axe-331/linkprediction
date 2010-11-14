package com.zoozoobar.torajim.linkprediction;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;


public class ObjectAuthor {
	private String author;
	private int papercount;
	private int neighborcount;
	private int countL;
	private int countT;
	private HashSet<ObjectKeyword> keywordList = new HashSet<ObjectKeyword>();
	private HashSet<String> neighbors = new HashSet<String>();
	
	ObjectAuthor(String author, String keywords, int papercount) {
		this.author = author;
		this.papercount = papercount;
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
				if(keyword == null) {
					iter.remove();
					continue;
				}
				if(keyword.getPapercount() < 5) {
					iter.remove();
					ObjectData.getAUTHOR_KEYWORDS().get(author).removeKeyword(keyword.getKeyword());
				}
			}
		}
		
//		if(keywordList.size() > 10) {
//			Iterator<ObjectKeyword> iter = keywordList.iterator();
//			while(iter.hasNext()) {
//				ObjectKeyword keyword = iter.next();
//				if(keyword.getPapercount() < 10) {
//					iter.remove();
//					
//				}
//				
//			}
//		}
		
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
//			System.out.println("author:" +  author);
			String __keywords[] = ObjectData.getAUTHOR_KEYWORDS().get(author).getObjectKeywordList();
			
			if(__keywords.length > 10) {
				int count = 0;
				for(ObjectKeyword ob : objects) {
					if(count == 10) break;
					for(String temp: __keywords) {
						if(temp == "" || temp == null) break;
						if(ob.getKeyword().equals(temp)) {
							if(keywordList.add(ob)) {
								count++;
								break;
							}
						}
					}
				}
			} else {
				int count = 0;
				for(ObjectKeyword ob : objects) {
					for(String temp: __keywords) {
						if(temp == "" || temp == null) break;
						if(ob.getKeyword().equals(temp)) {
							if(keywordList.add(ob)) {
								count++;
								break;
							}
						}
					}	
				}
				
				for(int i=0; i<10; i++) {
					if(count == 10) break;
					if(keywordList.add(objects[i])) {
						count++;
					}
				}
			}
		}
	}
	
	public void setNeighborcount(int neighborcount) {
		this.neighborcount = neighborcount;
	}
	
	public void setNeighbors(String __neighbors) {
		String neighbors[] = __neighbors.split("\t");
		
		for(String temp : neighbors) {
			temp = temp.toLowerCase();
			this.neighbors.add(temp);
		}
	}

	public String getAuthor() {
		return this.author;
	}
	
	public int getPapercount() {
		return this.papercount;
	}
	
	public int getNeighborcount() {
		return this.neighborcount;
	}

	public HashSet<ObjectKeyword> getKeywordList() {
		return this.keywordList;
	}
	
	public HashSet<String> getNeighborList() {
		return this.neighbors;
	}
	
	public void increaseCount(String type) {
		if(type.compareToIgnoreCase("T") == 0) this.countT++;
		if(type.compareToIgnoreCase("L") == 0) this.countL++;
	}
	public void increaseCountL() {
		this.countL++;
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