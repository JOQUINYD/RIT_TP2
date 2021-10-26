package Model;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.Normalizer;

public class Main {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		IndexHandler idx = new IndexHandler();
		idx.setupIndexer(true, "", "D:\\Joaquin library\\Documents\\RIT_TP2 Files\\index");
		idx.IndexCollection("D:\\Joaquin library\\Documents\\GitHub\\RIT_TP2\\src\\files\\h8.txt");
		
		Searcher src = new Searcher();
		src.setupSearcher("D:\\Joaquin library\\Documents\\RIT_TP2 Files\\index", true);
		src.search("titulo:Magnoel", 20);
		
		RandomAccessFile raf = new RandomAccessFile("D:\\Joaquin library\\Documents\\GitHub\\RIT_TP2\\src\\files\\h8.txt", "rw");
		raf.seek(8262119);
		byte[] bytes = new byte[(int) 6409];
		raf.readFully(bytes);
		System.out.println(new String(bytes));
	}

}
