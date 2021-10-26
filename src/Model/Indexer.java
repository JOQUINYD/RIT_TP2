package Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {
	
	private IndexWriter writer;
	private Boolean doStemming;
	
	public void setUpIndexer(Boolean doStemming, String stopWordsPath, String indexPath) throws IOException {
		this.doStemming = doStemming;
		
		Directory dir = FSDirectory.open(Paths.get(indexPath));
		IndexWriterConfig iwc;
		
		if (this.doStemming) {
			Map<String,Analyzer> analyzerPerField = new HashMap<>();
			analyzerPerField.put("texto", new SpanishAnalyzer());
			analyzerPerField.put("ref", new StandardAnalyzer());
			analyzerPerField.put("encab", new SpanishAnalyzer());
			analyzerPerField.put("titulo", new StandardAnalyzer());
			
			PerFieldAnalyzerWrapper aWrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);
			
			iwc = new IndexWriterConfig(aWrapper);
		} 
		else {
			StandardAnalyzer analyzer = new StandardAnalyzer();
			iwc = new IndexWriterConfig(analyzer);
		}

		iwc.setOpenMode(OpenMode.CREATE);

		this.writer = new IndexWriter(dir, iwc);
	}
	
	public void addDocument(HtmlInfo htmlInfo) throws IOException {
		if (!this.doStemming) {
			htmlInfo.body = stripPunctuation(htmlInfo.body);
			htmlInfo.headers = stripPunctuation(htmlInfo.headers);
		}
		htmlInfo.aTags = stripPunctuation(htmlInfo.aTags);
		htmlInfo.title = stripPunctuation(htmlInfo.title);
		
		Document doc = new Document();
	    doc.add(new StringField("initByte", Long.toString(htmlInfo.initByte), Field.Store.YES));
	    doc.add(new StringField("length", Long.toString(htmlInfo.length), Field.Store.YES));
	    doc.add(new TextField("texto", htmlInfo.body, Field.Store.NO));
	    doc.add(new TextField("ref", htmlInfo.aTags, Field.Store.NO));
	    doc.add(new TextField("encab", htmlInfo.headers, Field.Store.NO));
	    doc.add(new TextField("titulo", htmlInfo.title, Field.Store.YES));
	    
	    for (String link : htmlInfo.links) {
	    	doc.add(new StringField("enlace", link, Field.Store.YES));
		}
	    
	    this.writer.addDocument(doc);
	}
	
	public void close() throws IOException {
		this.writer.close();
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
