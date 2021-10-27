package Model;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
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

public class Searcher {
	IndexSearcher searcher;
	QueryParser qp;

	public void setupSearcher(String indexPath, boolean stemmingActive) throws IOException {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
	    this.searcher = new IndexSearcher(reader);
	    
	    if (stemmingActive) {
			Map<String,Analyzer> analyzerPerField = new HashMap<>();
			analyzerPerField.put("texto", new SpanishAnalyzer());
			analyzerPerField.put("ref", new StandardAnalyzer());
			analyzerPerField.put("encab", new SpanishAnalyzer());
			analyzerPerField.put("titulo", new StandardAnalyzer());
			
			PerFieldAnalyzerWrapper aWrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);
			
			this.qp = new QueryParser("texto", aWrapper);
		} 
		else {
			StandardAnalyzer analyzer = new StandardAnalyzer();
			this.qp = new QueryParser("texto", analyzer);
		}
	}
	
	public void search(String query, int maxHits) throws Exception {
		query = stripPunctuation(query);
		Query parsedQuery = this.qp.parse(query);
		
		System.out.println("To search: " + parsedQuery);
		
		TopDocs hits = this.searcher.search(parsedQuery, maxHits);
				
		System.out.println("Total results:: " + hits.totalHits);
		
		int i = 1;
		for (ScoreDoc sd : hits.scoreDocs) {
			Document d = this.searcher.doc(sd.doc);
			System.out.println("Doc starts: " + d.get("initByte") + " length: " + d.get("length"));
			System.out.println(d.get("titulo"));
			for (IndexableField field : d.getFields("enlace")) {
				System.out.println(field.stringValue());
				
			}
			i++;
		}
	}
	
	private void doPaging(ScoreDoc[] scoreDocs, int pageSize) {
		
	}
	
	private void getLinks(Document d) {
		for (IndexableField field : d.getFields("enlace")) {
			System.out.println(field.stringValue());
		}
	}
	
	private void generateHtml() {
		
	}
	
	private String stripPunctuation(String s) {
		s = s.replace('ñ', '\001');
	    s = s.replace('Ñ', '\002');
	    s = Normalizer.normalize(s, Normalizer.Form.NFD);
	    s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
	    s = s.replace('\001', 'ñ');
	    s = s.replace('\002', 'Ñ');
	    return s;
	}
}
