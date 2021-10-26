package Model;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.Normalizer;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.index.IndexOptions;

public class Main {
	
	
	public static void main(String[] args) throws Exception {
		
		
		try (Scanner myObj = new Scanner(System.in)) {
			
			System.out.println("Welcome to Lucene Indexer.");
			String instruction = "";
			
			while(!instruction.equals("exit")) {
				System.out.println("\nInstructions example:");
				System.out.println("\tindex \"collection path\" steaming(true|false) \"folder path\" \"stopWords path\"");
				System.out.println("\tsearch \"index path\" steaming(true|false) \"query\"");			
				System.out.println("\texit");	
				System.out.println("\nPlease enter an instruction:");			
				instruction = myObj.nextLine();  // Read user input
			
				if(instruction.equals("exit")) {
					break;
				}
				
				Pattern indexPattern = Pattern.compile("index \"(.*)\" (true|false) \"(.*)\" \"(.*)\"");
				Pattern searchPattern = Pattern.compile("search \"(.*)\" (true|false) \"(.*)\"");
				
				Matcher indexMatcher = indexPattern.matcher(instruction);
				Matcher searchMatcher = searchPattern.matcher(instruction);
				
				if (indexMatcher.find()) {

					String collectionPath = indexMatcher.group(1); 
					boolean steaming = Boolean.parseBoolean(indexMatcher.group(2)); 
					String folderPath = indexMatcher.group(3);  
					String stopWordsPath = indexMatcher.group(4); 
					 
				    IndexHandler idx = new IndexHandler();
					idx.setupIndexer(steaming, stopWordsPath, folderPath);
					idx.IndexCollection(collectionPath);
					 
					 
				 } else if(searchMatcher.find()) {

					String indexPath = searchMatcher.group(1);  
					boolean steaming = Boolean.parseBoolean(searchMatcher.group(2)); 
					String query = searchMatcher.group(3); 	
					
					Searcher src = new Searcher();
					src.setupSearcher(indexPath, steaming);
					src.search(query, 20);
				 
				 }
				 else {
					 System.out.println("Invalid instruction.");		
				 }
				
				
			}
			
			
			
			
		}
		
		
		
		/*	
	    IndexHandler idx = new IndexHandler();
		idx.setupIndexer(true, "", "D:\\Joaquin library\\Documents\\RIT_TP2 Files\\index");
		idx.IndexCollection("D:\\Joaquin library\\Documents\\GitHub\\RIT_TP2\\src\\files\\h8.txt");
		
		Searcher src = new Searcher();
		src.setupSearcher("D:\\Joaquin library\\Documents\\RIT_TP2 Files\\index", true);
		src.search("titulo:Magnoel AND encab:", 20);
		
	RandomAccessFile raf = new RandomAccessFile("D:\\Joaquin library\\Documents\\GitHub\\RIT_TP2\\src\\files\\h8.txt", "rw");
		raf.seek(8262119);
		byte[] bytes = new byte[(int) 6409];
		raf.readFully(bytes);
		System.out.println(new String(bytes));*/
	}

}
