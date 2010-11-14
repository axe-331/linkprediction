package com.zoozoobar.torajim.linkprediction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ObjectPair {
	private int id;
	public int getId() {
		return id;
	}

	private ObjectAuthor authorA;
	public ObjectAuthor getAuthorA() {
		return authorA;
	}

	public ObjectAuthor getAuthorB() {
		return authorB;
	}

	private ObjectAuthor authorB;
	private String type;			// "L" or "T"
	private String origin;
	
	private String neighbor;		// author 의 neighbor가 모두 2<= count <= 10 일 경우 true
	private String validset;		// author 가 L과 T 모두에 3번 이상 나타났을 경우 true
	
	private int keywordScoreA;		// keyword 교집합
	private double keywordScoreB;	// 교집합 / 합집합
	private int keywordScoreC;		// |keyword(A)| * |keyword(B)|
	public double keywordScoreD;	// 시그마 1/log(papercount)
	
	private int neighborScoreA;
	private double neighborScoreB;
	private int neighborScoreC;
	private double neighborScoreD;
	
	
	
	ObjectPair(int id, ObjectAuthor authorA, ObjectAuthor authorB, String type, String origin) {
		this.id = id;
		this.authorA = authorA;
		this.authorB = authorB;
		this.type = type;
		this.origin = origin;
		
		this.neighbor = "T";
		this.validset = "T";
		
		int nbcount = authorA.getNeighborcount();
		if(nbcount < 2 || nbcount > 10) this.neighbor = "F";
		nbcount = authorB.getNeighborcount();
		if(nbcount < 2 || nbcount > 10) this.neighbor = "F";
		if(authorA.getCountL() < 2 || authorA.getCountT() < 2) this.validset = "F";
		if(authorB.getCountL() < 2 || authorB.getCountT() < 2) this.validset = "F";
		
		HashSet<ObjectKeyword> keywordA = authorA.getKeywordList();
		HashSet<ObjectKeyword> keywordB = authorB.getKeywordList();
		
		calcKeywordScoreA(keywordA, keywordB);
		calcKeywordScoreB(keywordA, keywordB);
		calcKeywordScoreC(keywordA, keywordB);
		calcKeywordScoreD(keywordA, keywordB);
		
//		if(this.neighbor.equals("T")) {
//			this.neighborScoreA = 0;
//			this.neighborScoreB = 0;
//			this.neighborScoreC = 0;
//			this.neighborScoreD = 0;			
//		} else {
			HashSet<String> authorANeighbors = authorA.getNeighborList();
			HashSet<String> authorBNeighbors = authorB.getNeighborList();
			
			calcNeighborScoreA(authorANeighbors, authorBNeighbors);
			calcNeighborScoreB(authorANeighbors, authorBNeighbors);
			calcNeighborScoreC(authorANeighbors, authorBNeighbors);
			calcNeighborScoreD(authorANeighbors, authorBNeighbors);
//		}
		
	}
	
	private void calcNeighborScoreA(HashSet<String> authorANeighbors, HashSet<String> authorBNeighbors) {
		Iterator<String> iterA = authorANeighbors.iterator();
		while(iterA.hasNext()) {
			String __A = iterA.next();
			Iterator<String> iterB = authorBNeighbors.iterator();
			while(iterB.hasNext()) {
				String __B = iterB.next();
				if(__A.equals(__B)) this.neighborScoreA++;
			}
		}
	}
	
	private void calcNeighborScoreB(HashSet<String> authorANeighbors, HashSet<String> authorBNeighbors) {
		int sum = authorANeighbors.size() + authorBNeighbors.size() - this.neighborScoreA;
		this.neighborScoreB = (double)this.neighborScoreA / (double)sum; 
	}
	
	private void calcNeighborScoreC(HashSet<String> authorANeighbors, HashSet<String> authorBNeighbors) {
		this.neighborScoreC = authorANeighbors.size() * authorBNeighbors.size();
	}
	
	private void calcNeighborScoreD(HashSet<String> authorANeighbors, HashSet<String> authorBNeighbors) {
		Iterator<String> iterA = authorANeighbors.iterator();
		double sum = 0;
		while(iterA.hasNext()) {
			String author = iterA.next();
			if(author.equals("") || author == null) continue;
			if(authorBNeighbors.contains(author)) {
				try {
					int papercount = ObjectData.getAUTHOR_LIST().get(author).getPapercount();
					if(papercount == 1) continue;
					double oldsum=sum;
					sum += 1/Math.log10(papercount);
					if(Double.isInfinite(sum)) {
						System.out.println("author:"+ ObjectData.getAUTHOR_LIST().get(author).getAuthor()+"/"+papercount + "/" + oldsum+"/"+Math.log10(papercount));
					}
				} catch(Exception e) {
					System.out.println("Exception !!!!!!![" + author+"]");
					System.exit(0);
				}
			}
		}
		this.neighborScoreD = sum;
	}
	
	private void calcKeywordScoreA(Set<ObjectKeyword> keywordA, Set<ObjectKeyword> keywordB) {
		int scoreA = 0;
		
		Iterator<ObjectKeyword> iter = keywordA.iterator();
		while(iter.hasNext()) {
			ObjectKeyword temp = iter.next();
			if(temp == null) {
				continue;
			}
//			System.out.println(temp.getKeyword() + "/" + temp.getPapercount());
			if(keywordB.contains(temp)) scoreA++;
		}
		
		this.keywordScoreA = scoreA;
//		System.out.println(scoreA);
	}
	
	private void calcKeywordScoreB(Set<ObjectKeyword> keywordA, Set<ObjectKeyword> keywordB) {
		int sum = 0;
		sum = 0;
		sum += keywordA.size();
		sum += keywordB.size();
		sum -= this.keywordScoreA;
		this.keywordScoreB = (double)this.keywordScoreA / (double)sum;
//		System.out.println("scoreA:"+ keywordScoreA + "\tsum:" + sum + "\tscoreB:" + keywordScoreB);
	}
	
	private void calcKeywordScoreC(Set<ObjectKeyword> keywordA, Set<ObjectKeyword> keywordB) {
		this.keywordScoreC = keywordA.size() * keywordB.size();
//		System.out.println(scoreC);
	}
	
	private void calcKeywordScoreD(Set<ObjectKeyword> keywordA, Set<ObjectKeyword> keywordB) {
		double sum = 0;
		Iterator<ObjectKeyword> iter = keywordA.iterator();
		while(iter.hasNext()) {
			ObjectKeyword temp = iter.next();
			if(temp == null) continue;

			if(keywordB.contains(temp)) {
				int papercount = temp.getPapercount();

				sum += 1/Math.log10(papercount);
			}
		}
		
		this.keywordScoreD = sum;
	}
	
	public String getSqlValues() {
		String result = 
			id + "," + 
			"\"" + authorA.getAuthor() + "\"," + 
			"\"" + authorB.getAuthor() + "\"," +
			"\"" + type + "\"," +
			"\"" + origin + "\"," +
			"\"" + neighbor + "\"," +
			"\"" + validset + "\"," +
			keywordScoreA + "," +
			keywordScoreB + "," +
			keywordScoreC + "," +
			keywordScoreD + "," +
			neighborScoreA + "," +
			neighborScoreB + "," +
			neighborScoreC + "," +
			neighborScoreD
			;
		
		return result;
		
	}
}
