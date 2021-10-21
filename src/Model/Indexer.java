package Model;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class Indexer {
	
	private IndexWriter writer;
	
	public void setUpIndexer(Boolean doStemming, String stopWordsPath, String indexPath) throws IOException {
		Directory dir = FSDirectory.open(Paths.get(indexPath));
		
		 Map<String,Analyzer> analyzerPerField = new HashMap<>();
		 if (doStemming) {
			 analyzerPerField.put("texto", new SpanishAnalyzer());
			 analyzerPerField.put("ref", new StandardAnalyzer());
			 analyzerPerField.put("encab", new SpanishAnalyzer());
			 analyzerPerField.put("titulo", new StandardAnalyzer());
		 }
		 PerFieldAnalyzerWrapper aWrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);
		
		IndexWriterConfig iwc = new IndexWriterConfig(aWrapper);
		iwc.setOpenMode(OpenMode.CREATE);

		this.writer = new IndexWriter(dir, iwc);
	}
	
	public void addDocument(HtmlInfo htmlInfo) {
		
	}
	
}
