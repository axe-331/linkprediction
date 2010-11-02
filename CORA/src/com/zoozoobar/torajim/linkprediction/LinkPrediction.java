package com.zoozoobar.torajim.linkprediction;

import java.io.File;
import java.io.IOException;

public class LinkPrediction {

	LinkPrediction() {
		
	}
		/**
	}
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		ParserCORA cora = new ParserCORA();
//		ParserDBLP dblp = new ParserDBLP();
//		File file = new File("C:\\Users\\ucu\\workspace\\LinkPrediction\\trunk\\CORA\\papers");
		
		MySQLCommand.connServer();
	
		long start = System.currentTimeMillis();
		AuthorList.initAuthorList();
		System.out.println("authorlist time:" + (System.currentTimeMillis()-start) / 1000.0);
		KeywordList.initKeywordList();
		System.out.println("keywordlist time:" + (System.currentTimeMillis()-start) / 1000.0);
//		
		
//		TableDB.dropTablePapers();
//		TableDB.createTablePapers();
//		cora.startParser(file);
////				
//		file = new File("C:\\Users\\ucu\\workspace\\LinkPrediction\\trunk\\DBLP\\dblp.xml");
//		dblp.startParser(file);
		
//		TableDB.dropTableObjectPair();
//		TableDB.createTableObjectPair();
//		TableDB.insertValuesObjectPair();
	
//		TableDB.dropTableKeywords();
//		TableDB.createTableKeywords();
//		
//		TableDB.fillTableKeywords("cora");
//		TableDB.fillTableKeywords("DBLP");
////		
//		TableDB.dropTableNeightbors();
//		TableDB.createTableNeighbors();
//		TableDB.fillTableNeighbors("cora");
		
		TableDB.dropTableObjectpairComplete();
		TableDB.createTableObjectpairComplete();
		TableDB.fillTableObjectpairComplete("cora");
		
//		TableDB.printPaperCount("cora");
//		TableDB.printAuthorCount("cora");
//		TableDB.printKeywordCount("cora");
//		TableDB.printAuthorPaper("cora");
//		TableDB.printAuthorKeyword("cora");
//		TableDB.printAuthorNeighbor("cora");
	}
}
