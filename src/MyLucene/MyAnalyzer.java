package MyLucene;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.IOUtils;

public class MyAnalyzer extends Analyzer{
	CharArraySet stopwords;
		
	public MyAnalyzer(CharArraySet stopwords) {
		super();
		this.stopwords = stopwords;
	}

	@Override
	  protected TokenStreamComponents createComponents(String fieldName) {
	    final Tokenizer source = new StandardTokenizer();
	    TokenStream result = new LowerCaseFilter(source);
	    result = new StopFilter(result, stopwords);
	    result = new MyASCIIFoldingFilter(result);
	    return new TokenStreamComponents(source, result);
	  }
}
