package com.zoozoobar.torajim.debug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class basicParser {
	private HashMap paperIdMap = new HashMap();
	public basicParser(){
		try {
			startParser();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startParser() throws IOException{
		File file = new File("papers");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String temp = "";
		while((temp = br.readLine()) != null){
			String[] tokens = temp.split("\t");
			if(tokens.length >= 3){
				paperIdMap.put(tokens[0], tokens[2]);
			}
		}
		System.out.println(paperIdMap.size());
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		basicParser app = new basicParser();
	}

}
