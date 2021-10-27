package Model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
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
}
