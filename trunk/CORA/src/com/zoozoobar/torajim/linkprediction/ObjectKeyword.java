package com.zoozoobar.torajim.linkprediction;

public class ObjectKeyword {
	public String keyword;
	public int papercount;
	ObjectKeyword (String keyword, int papercount) {
		this.keyword = keyword;
		this.papercount = papercount;
	}
	public String getKeyword() {
		return keyword;
	}
	public int getPapercount() {
		return this.papercount;
	}
}
