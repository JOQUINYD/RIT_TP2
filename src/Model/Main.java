package Model;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.Normalizer;

import org.apache.commons.lang3.StringUtils;

public class Main {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		RandomAccessFile raf = new RandomAccessFile("D:\\Joaquin library\\Documents\\GitHub\\RIT_TP2\\src\\files\\h8.txt", "r");
		raf.seek(0);
      	byte[] arr = new byte[(int) 1000];
      	raf.readFully(arr);
        String html = new String(arr);
        System.out.println(html);
		
		
		IndexHandler idx = new IndexHandler();
		idx.setupIndexer(true, "", "D:\\Joaquin library\\Documents\\RIT_TP2 Files\\index");
		idx.IndexCollection("D:\\Joaquin library\\Documents\\GitHub\\RIT_TP2\\src\\files\\h8.txt");
	}

}
