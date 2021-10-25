package Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlParser {
	
	// Attributes
	private String html;
	private Document doc;
	private Pattern pattern = Pattern.compile("[A-Za-zÁÉÍÓÚÜáéíóúüÑñ0-9_]*[A-Za-zÁÉÍÓÚÜáéíóúüÑñ][A-Za-zÁÉÍÓÚÜáéíóúüÑñ0-9_]*");
	private String aTagsText = "";
	private ArrayList<String> links = new ArrayList<String>();
	
	public String getBodyText() {
		Elements body = doc.select("body");
		if(!body.isEmpty()) {
			return getPermitedText(body.first().text());
		}
		return "";
	}
	
	public String geTitleText() {
		Elements title = doc.select("title");
		if(!title.isEmpty()) {
			return title.first().text();
		}
		else {
			return "";
		}
	}
		
	public String getATagsText() {
		return this.aTagsText;
	}

	public String getHeadersText() {
		Elements headers = doc.select("h1, h2, h3, h4, h5, h6, h7, h8, h9");
		String headersText = "";
		for (Element element : headers) {
			headersText += element.text() + " ";
		}
		return getPermitedText(headersText);
	}
	
	public ArrayList<String> getLinks() {
		return this.links;
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
	
	public void processATags() {
		Elements aTags = doc.select("a");
		this.aTagsText = "";
		String link = "";
		this.links = new ArrayList<String>();
		
		for (Element element : aTags) {
			this.aTagsText += element.text() + " ";
			link = element.attr("href");
			
			if (link.matches("\\.\\./\\.\\./\\.\\./\\.\\./articles/.*")) {
				String relativeLink = link.replaceAll("\\.\\./\\.\\./\\.\\./\\.\\./articles/", "");
				if (!this.links.contains(relativeLink)) {
					this.links.add(relativeLink);
				}
			}
		}
	}
	
	public void setDoc(String html) {
		this.html = html;
		this.doc = Jsoup.parse(html);
		this.processATags();
	}
}
