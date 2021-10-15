package Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class IndexHandler {
	
	// Attributes
	private Indexer indexer;
	private HtmlInfo htmlInfo = new HtmlInfo();
	
	// Methods

	public void IndexCollection(String fileName) throws Exception {
        
		String singleHtml = "";
        RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "r");
        RandomAccessFile rafToRead = new RandomAccessFile(fileName, "r");
        BufferedReader brRafReader = new BufferedReader(new InputStreamReader(
        	    new FileInputStream(randomAccessFile.getFD()), "ISO-8859-1"));
        String line = null;
        
        long currentOffset = 0;
        long previousOffset = -1;
        long previousPosition = 0;
                
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
//            System.out.println("Position : " + realPosition 
//                    + " bufferOffset " + bufferOffset
//                    + " and currentoffset " + currentOffset);
//          System.out.println(line.matches("<!DOCTYPE.*"));
//          System.out.println(line.matches("</html>.*"));
          
          singleHtml += line;
          
          // Save starting byte position
          if(line.matches("<!DOCTYPE html PUBLIC \\\"-//W3C//DTD XHTML 1\\.0 Transitional//EN\\\" \\\"http://www\\.w3\\.org/TR/xhtml1/DTD/xhtml1-transitional\\.dtd\\\">")) {
          	this.htmlInfo.initByte = realPosition - (realPosition - previousPosition);
          }                        
          
          // if end of html index it       
          if (line.matches("</html>.*")) {
          	this.htmlInfo.length = realPosition - this.htmlInfo.initByte;
          	// parse html information into htmlInfo
          	
          	
          	// clean singleHtml
          	singleHtml = "";
          }
          
          previousPosition = realPosition;
        }
        randomAccessFile.close();
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
