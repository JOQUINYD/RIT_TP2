package Model;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileHandler {
	public static void saveObject(Object obj, String path) throws Exception{
	    FileOutputStream fos = new FileOutputStream(path);
	    ObjectOutputStream oos = new ObjectOutputStream(fos);
	    oos.writeObject(obj);
	    oos.close();
	}

	public static Object loadObject(String path) throws Exception{
	   FileInputStream fin = new FileInputStream(path);
	   ObjectInputStream ois = new ObjectInputStream(fin);
	   Object obj = ois.readObject();
	   ois.close();
	   return obj;
	}
	
	public static void saveString(String str, String path) {

		try {
			FileWriter fWriter = new FileWriter(path);
			BufferedWriter writer = new BufferedWriter(fWriter);
		    writer.write(str);
		    writer.close(); //make sure you close the writer object 
		} catch (Exception e) {
			System.out.println();
		  //catch any exceptions here
		}
	}
	
}
