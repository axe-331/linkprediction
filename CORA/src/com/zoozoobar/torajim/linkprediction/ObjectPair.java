package com.zoozoobar.torajim.linkprediction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ObjectPair {
	private int id;
	private ObjectAuthor authorA;
	private ObjectAuthor authorB;
	
	private String type;			// "L" or "T"
	private boolean neighbor;		// author 의 neighbor가 모두 2<= count <= 10 일 경우 true
	private boolean validset;		// author 가 L과 T 모두에 3번 이상 나타났을 경우 true
	private int scoreA;	// keyword 교집합
	private double scoreB;	// 교집합 / 합집합
	private double scoreC;	// |keyword(A)| * |keyword(B)|
	private double scoreD;	// 시그마 1/log(papercount)
	
	ObjectPair(int id, ObjectAuthor authorA, ObjectAuthor authorB, String type) {
		this.id = id;
		this.authorA = authorA;
		this.authorB = authorB;
		this.type = type;
		this.neighbor = true;
		this.validset = true;
		
		int nbcount = authorA.getNeighborcount();
		if(nbcount < 2 || nbcount > 10) this.neighbor = false;
		nbcount = authorB.getNeighborcount();
		if(nbcount < 2 || nbcount > 10) this.neighbor = false;
		if(authorA.getCountL() < 3 || authorA.getCountT() < 3) this.validset = false;
		if(authorB.getCountL() < 3 || authorB.getCountT() < 3) this.validset = false;
		
		HashSet<ObjectKeyword> keywordA = authorA.getKeywordList();
		HashSet<ObjectKeyword> keywordB = authorB.getKeywordList();
		
		calcScoreA(keywordA, keywordB);
		calcScoreB(keywordA, keywordB);
		calcScoreC(keywordA, keywordB);
		calcScoreD(keywordA, keywordB);
	}
	
	private void calcScoreA(Set<ObjectKeyword> keywordA, Set<ObjectKeyword> keywordB) {
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
		
		this.scoreA = scoreA;
//		System.out.println(scoreA);
	}
	
	private void calcScoreB(Set<ObjectKeyword> keywordA, Set<ObjectKeyword> keywordB) {
		int sum = 0;
		sum = 0;
		sum += keywordA.size();
		sum += keywordB.size();
		sum -= scoreA;
		this.scoreB = scoreA / sum;
//		System.out.println(scoreB);
	}
	
	private void calcScoreC(Set<ObjectKeyword> keywordA, Set<ObjectKeyword> keywordB) {
		this.scoreC = keywordA.size() * keywordB.size();
//		System.out.println(scoreC);
	}
	
	private void calcScoreD(Set<ObjectKeyword> keywordA, Set<ObjectKeyword> keywordB) {
		double sum = 0;
		Iterator<ObjectKeyword> iter = keywordA.iterator();
		while(iter.hasNext()) {
			ObjectKeyword temp = iter.next();
			if(temp == null) continue;
		//	System.out.println(temp.getKeyword() + "/" + temp.getPapercount());
			if(keywordB.contains(temp)) {
				int papercount = 1;
				temp.getPapercount();
				temp.getPapercount();
				sum += 1/Math.log10(papercount);
			}
		}
		
		this.scoreD = sum;
	}
}
