package Model;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.lucene.analysis.CharArraySet;

public class IndexInfo implements Serializable{
	
	public Boolean stemmingActive;
	public String collectionPath;
	public ArrayList<String> stopwords;
	
	public IndexInfo(Boolean stemmingActive, String collectionPath, ArrayList<String> stopwords) {
		this.stemmingActive = stemmingActive;
		this.collectionPath = collectionPath;
		this.stopwords = stopwords;
	}

	
	public IndexInfo() {
		super();
		this.stemmingActive = true;
		this.collectionPath = "";
		this.stopwords = null;
	}	
}
