package Model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
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
	private IndexInfo indexInfo;
	private String indexPath;
	
	public void setUpIndexer(Boolean doStemming, String stopWordsPath, String indexPath) throws IOException {
		this.doStemming = doStemming;
		
		Directory dir = FSDirectory.open(Paths.get(indexPath));
		IndexWriterConfig iwc;
		
		ArrayList<String> stopwordsList = getStopwords(stopWordsPath);
		CharArraySet stopwords = new CharArraySet(stopwordsList, false);
		
		Map<String,Analyzer> analyzerPerField = new HashMap<>();
		if (this.doStemming) {
			analyzerPerField.put("texto", new SpanishAnalyzer(stopwords));
			analyzerPerField.put("encab", new SpanishAnalyzer(stopwords));
		} 
		else {
			analyzerPerField.put("texto", new StandardAnalyzer(stopwords));
			analyzerPerField.put("encab", new StandardAnalyzer(stopwords));
		}
		analyzerPerField.put("ref", new StandardAnalyzer(stopwords));
		analyzerPerField.put("titulo", new StandardAnalyzer(stopwords));

		PerFieldAnalyzerWrapper aWrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);
		
		iwc = new IndexWriterConfig(aWrapper);

		iwc.setOpenMode(OpenMode.CREATE);

		this.writer = new IndexWriter(dir, iwc);
		this.indexPath = indexPath;
		this.indexInfo = new IndexInfo(doStemming, "", stopwordsList);
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
	
	private ArrayList<String> getStopwords(String stopwordsPath) {
		try (BufferedReader br = new BufferedReader(new FileReader(stopwordsPath))) {
		    String line;
		    ArrayList<String> stopwords = new ArrayList<>();
		    while ((line = br.readLine()) != null) {
		       stopwords.add(line);
		    }
		    return stopwords;
		    
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public void saveIndexInfo(String collectionPath) throws Exception {
		this.indexInfo.collectionPath = collectionPath;
		FileHandler.saveObject(this.indexInfo, this.indexPath+"\\indexInfo.txt");
	}
	
}
