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
		ParserCORA cora = new ParserCORA();
		ParserDBLP dblp = new ParserDBLP();
		File file = new File("C:\\Users\\ucu\\workspace\\LinkPrediction\\trunk\\CORA\\papers");
		
		MySQLCommand.connServer();
	
		TableDB.dropTablePapers();
		TableDB.createTablePapers();
		cora.startParser(file);
				
		file = new File("C:\\Users\\ucu\\workspace\\LinkPrediction\\trunk\\DBLP\\dblp.xml");
		dblp.startParser(file);
		
		TableDB.dropTableObjectPair();
		TableDB.createTableObjectPair();
		TableDB.insertValuesObjectPair();
	}

}
