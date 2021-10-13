package Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HtmlParser {
	
	// Attributes
	private Document doc;
	private Pattern pattern = Pattern.compile("[A-Za-zÁÉÍÓÚÜáéíóúüÑñ0-9_]*[A-Za-zÁÉÍÓÚÜáéíóúüÑñ][A-Za-zÁÉÍÓÚÜáéíóúüÑñ0-9_]*");
	
	public String getBodyText() {
		Element body = doc.getElementsByTag("body").first();
		return getPermitedText(body.text());
	}
	
	public String geTitleText() {
		Element title = doc.getElementsByTag("title").first();
		return title.text();
	}
		
	public String getATagsText() {
		ArrayList<Element> aTags = doc.getElementsByTag("a");
		String aTagsText = "";
		for (Element element : aTags) {
			aTagsText += element.text() + " ";
		}
		return aTagsText;
	}

	public String getHeadersText() {
		
		return "";
	}
	
	public ArrayList<String> getLinks() {
		ArrayList<String> links = new ArrayList<>();
		
		return links;
	}

	private String getPermitedText(String originalText) {
		String permitedText = "";
	    Matcher matcher = pattern.matcher(originalText);

	    // Find all matches
	    while (matcher.find()) {
	      // Get the matching string
	      permitedText += matcher.group() + " ";
	    }
		
		return permitedText;
	}
	
	public void setDoc(String html) {
		this.doc = Jsoup.parse(html);
	}
}
