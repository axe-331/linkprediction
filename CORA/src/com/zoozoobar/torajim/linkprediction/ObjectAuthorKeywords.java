package com.zoozoobar.torajim.linkprediction;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

public class ObjectAuthorKeywords {
	private String author;
	
	private HashMap<String, ObjectKeyword> keywords = new HashMap<String, ObjectKeyword>();
	
	ObjectAuthorKeywords (String author) {
		this.author = author;
	}
	
	public void putKeyword(String _keyword) {
		String keyword = _keyword.toLowerCase();
		ObjectKeyword ob = keywords.get(keyword);
		if (ob == null) {
			ob = new ObjectKeyword(keyword, 1);
			keywords.put(keyword, ob);
		} else {
			ob.increasePapercount();
		}	
	}
	
	public void removeKeyword(String _keyword) {
		String keyword = _keyword.toLowerCase();
		keywords.remove(keyword);
	}
	
	public int getKeywordPapercount(String _keyword) {
		String keyword = _keyword.toLowerCase();
		ObjectKeyword ob = keywords.get(keyword);
		if(ob == null) {
			return 0;
		} else {
			return ob.getPapercount();
		}
	}
	
	public String[] getObjectKeywordList() {
		Collection <ObjectKeyword> collection = keywords.values();
		Iterator<ObjectKeyword> iter = collection.iterator();
		ObjectKeyword objects[] = new ObjectKeyword[collection.size()];
		
		int count=0;
		
		while(iter.hasNext()) {
			ObjectKeyword ob = iter.next();
			if(ob.getPapercount() > 1) {
				objects[count] = ob;
				count++;
			}
		}
		
		if(count != 0) {
			ObjectKeyword __objects[] = new ObjectKeyword[count];
			for(int i=0; i<count; i++) {
				__objects[i] = objects[i];
			}
			
			Arrays.sort(__objects, new Comparator<ObjectKeyword>() {
				@Override
				public int compare(ObjectKeyword o1, ObjectKeyword o2) {
					// TODO Auto-generated method stub
					return o2.getPapercount() - o1.getPapercount();
				}
			});
		}
		
		String returnList[] = new String[count];
		int i=0;
		for(i=0; i<count; i++) {
			returnList[i] = objects[i].getKeyword();
		}
		
		collection.clear();

		return returnList;
	}
	
	public static void main(String[] args) {
		// TEST
		ObjectAuthorKeywords ob = new ObjectAuthorKeywords("test1");
		ob.putKeyword("keyword1");
		ob.putKeyword("keyword2");
		ob.putKeyword("keyword3");
		ob.putKeyword("Keyword2");
		ob.putKeyword("keyword3");
		ob.putKeyword("KEyword3");
		
		System.out.println("author:" + ob.author);
		System.out.println("keyword1:" + ob.getKeywordPapercount("keyWORD1"));
		System.out.println("keyword2:" + ob.getKeywordPapercount("keyWORD2"));
		System.out.println("keyword3:" + ob.getKeywordPapercount("KEYWORD3"));
	}
}
