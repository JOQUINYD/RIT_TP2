package Model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class IndexHandler {
	
	// Attributes
	private Indexer indexer = new Indexer();
	private HtmlInfo htmlInfo = new HtmlInfo();
	private HtmlParser htmlParser = new HtmlParser();
	
	// Methods

	public void IndexCollection(String collectionPath) throws Exception {
		this.indexer.saveIndexInfo(collectionPath);		
        
		System.out.println("\nINDEXING...");
        long startTime = System.nanoTime();
        
        RandomAccessFile randomAccessFile = new RandomAccessFile(collectionPath, "r");
        RandomAccessFile rafToRead = new RandomAccessFile(collectionPath, "rw");
        BufferedReader brRafReader = new BufferedReader(new InputStreamReader(
        	    new FileInputStream(randomAccessFile.getFD()), "ISO-8859-1"));
        String line = null;
        
        long currentOffset = 0;
        long previousOffset = -1;
        long previousPosition = 0;
        long id = 1;        
        while ((line = brRafReader.readLine()) != null) {
            long fileOffset = randomAccessFile.getFilePointer();
            if (fileOffset != previousOffset) {
                if (previousOffset != -1) {
                    currentOffset = previousOffset;
                }
                previousOffset = fileOffset;
            }
            int bufferOffset = getOffset(brRafReader);
            long realPosition = currentOffset + bufferOffset;

          // Save starting byte position
          if(line.matches("^<!DOCTYPE html PUBLIC \\\"-//W3C//DTD XHTML 1\\.0 Transitional//EN\\\" \\\"http://www\\.w3\\.org/TR/xhtml1/DTD/xhtml1-transitional\\.dtd\\\">")) {
          	if(previousPosition != 0) {
            	this.htmlInfo.initByte = realPosition - (realPosition - previousPosition) + 1;
          	}
          	else {
          		this.htmlInfo.initByte = realPosition - (realPosition - previousPosition);
          	}
          }                        
          
          // if end of html index it       
          if (line.matches("</html>.*")) {
          	this.htmlInfo.length = realPosition - this.htmlInfo.initByte;
          	
          	rafToRead.seek(this.htmlInfo.initByte);
          	byte[] arr = new byte[(int) this.htmlInfo.length];
            rafToRead.readFully(arr);
            String html = new String(arr);
          	
	        // parse html information into htmlInfo
	        this.htmlParser.setDoc(html);
	        this.htmlInfo.body = this.htmlParser.getBodyText();
	        this.htmlInfo.headers = this.htmlParser.getHeadersText();
	        this.htmlInfo.title = this.htmlParser.geTitleText();
	        this.htmlInfo.aTags = this.htmlParser.getATagsText();
	        this.htmlInfo.links = this.htmlParser.getLinks();
	       
	        id++;
	        this.indexer.addDocument(htmlInfo);
          }
          previousPosition = realPosition;
        }
        randomAccessFile.close();
        rafToRead.close();
        this.indexer.close();
        
        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000000;
        System.out.println("INDEXING COMPLETED - " + id + " FILES WERE PROCESSED IN " + duration + " SECONDS");
    }
	
	public void setupIndexer(Boolean doStemming, String stopWordsPath, String indexPath) throws IOException {
		this.indexer.setUpIndexer(doStemming, stopWordsPath, indexPath);
	}

    private int getOffset(BufferedReader bufferedReader) throws Exception {
        Field field = BufferedReader.class.getDeclaredField("nextChar");
        int result = 0;
        try {
            field.setAccessible(true);
            result = (Integer) field.get(bufferedReader);
        } finally {
            field.setAccessible(false);
        }
        return result;
    }
}
