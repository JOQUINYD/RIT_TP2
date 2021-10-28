package Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import MyLucene.MyAnalyzer;

public class Searcher {
	IndexSearcher searcher;
	QueryParser qp;
	IndexInfo indexInfo;

	public void setupSearcher(String indexPath, boolean stemmingActive) throws Exception {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
	    this.searcher = new IndexSearcher(reader);
	    this.indexInfo = (IndexInfo) FileHandler.loadObject(indexPath+"\\indexInfo.txt");

	    CharArraySet stopwords = new CharArraySet(this.indexInfo.stopwords, false);
	    
	    if (this.indexInfo.stemmingActive) {
			Map<String,Analyzer> analyzerPerField = new HashMap<>();
			analyzerPerField.put("texto", new SpanishAnalyzer(stopwords));
			analyzerPerField.put("ref", new MyAnalyzer(stopwords));
			analyzerPerField.put("encab", new SpanishAnalyzer(stopwords));
			analyzerPerField.put("titulo", new MyAnalyzer(stopwords));
			
			PerFieldAnalyzerWrapper aWrapper = new PerFieldAnalyzerWrapper(new MyAnalyzer(stopwords), analyzerPerField);
			
			this.qp = new QueryParser("texto", aWrapper);
		} 
		else {
			Analyzer analyzer = new MyAnalyzer(stopwords);
			this.qp = new QueryParser("texto", analyzer);
		}
	}
	
	public void search(String query, int maxHits) throws Exception {
		Query parsedQuery = this.qp.parse(query);
		
		System.out.println("To search: " + parsedQuery);
		
		TopDocs hits = this.searcher.search(parsedQuery, maxHits);
				
		System.out.println("Total results:: " + hits.totalHits + " - only " + maxHits + " can be shown");
		
		doPaging(hits.scoreDocs, 20);
	}
	
	private void doPaging(ScoreDoc[] scoreDocs, int pageSize) throws Exception {
		int numOfPages = (int) Math.ceil(scoreDocs.length / Double.valueOf(pageSize));
		int currentPage = 0;
		Boolean inPaging = true;
		
		while (inPaging) {
			System.out.println("\n------------- Page (" + (currentPage+1) + " / " + numOfPages + ") -------------\n");
			for (int i = 0; i < pageSize; i++) {
				int docPos = (currentPage * pageSize) + i;
				if (docPos >= scoreDocs.length) break;
				Document d = this.searcher.doc(scoreDocs[docPos].doc);
				System.out.println("Local ID:: " + docPos + " Title: " + d.get("titulo"));
			}
			System.out.println("\n------------- Page (" + (currentPage+1) + " / " + numOfPages + ") -------------" );
			System.out.println("\n(b)back (n)next (q)quit (gl)get links (gh)get html");
			System.out.println("Please enter paging instruction:");
			Scanner myObj1 = new Scanner(System.in);
			String instruction = myObj1.nextLine();  // Read user input
			int docPos;
			switch (instruction) {
				case "b":
					if (currentPage > 0) {
						currentPage--;
					}
					break;
				case "n":
					if (currentPage < numOfPages-1) {
						currentPage++;
					}
					break;
				case "q":
					inPaging = false;
					break;
				case "gl":
					System.out.println("\nEnter local ID:");
					docPos = myObj1.nextInt();
					if (docPos < scoreDocs.length) {
						printLinks(this.searcher.doc(scoreDocs[docPos].doc), "files/links.txt");
						Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + new File("files/links.txt").getAbsolutePath());
					}
					break;
				case "gh":
					System.out.println("\nEnter local ID:");
					docPos = myObj1.nextInt();
					if (docPos < scoreDocs.length) {
						generateHtml(this.searcher.doc(scoreDocs[docPos].doc), "files/file.html");
						Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + new File("files/file.html").getAbsolutePath());
					}
					break;
	
				default:
					inPaging = false;
					break;
			}
		

		}
	}
	
	private void printLinks(Document d, String path) {
		String links = "";
		for (IndexableField field : d.getFields("enlace")) {
			links += field.stringValue() + "\n";
		}
		FileHandler.saveString(links, path);
	}
	
	private void generateHtml(Document d, String path) throws Exception {
		long initByte = Long.parseLong(d.get("initByte"));
		long length = Long.parseLong(d.get("length"));
		
		RandomAccessFile raf = new RandomAccessFile(this.indexInfo.collectionPath, "rw");
		raf.seek(initByte);
		byte[] bytes = new byte[(int) length];
		raf.readFully(bytes);
		
		String html = new String(bytes);
		
		FileHandler.saveString(html, path);
		
		raf.close();
	}

}
