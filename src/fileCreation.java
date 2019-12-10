import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

public class fileCreation {

	public static void main(String[] args)
	{
	String filename ="abc.txt";
		
        try {
        	RandomAccessFile f = new RandomAccessFile(filename, "rw");
			f.setLength(1024*1024);//4096*1024
			long l = f.getFilePointer();
			System.out.println("current offset"+l);
			double bytes = f.length();
			double kilobytes = (bytes / 1024);
			double megabytes = (kilobytes / 1024);
			System.out.println("file size:"+megabytes);
			String charinput = "First";
			System.out.println("size of write"+charinput.getBytes().length);
	        f.writeBytes("First");
		    System.out.println("offset after write"+f.getFilePointer());
		    long l2 = f.getFilePointer();
			System.out.println("current offset"+l2);
			f.writeBytes("First");
			f.writeBytes("First");
			 System.out.println("offset after write"+f.getFilePointer());
		    //writeToRandomAccessFile(filename,1,"hi this is hema now");
			//f.close();
			//RandomAccessFile f2 = new RandomAccessFile(filename, "r");
			
			 //HOW TO WRITE A RANDOM SIZED STRING  TO A FILE, CREATION OF LINUX FILES WITH 4MB
			 //System.out.println("offset after write file in read mode"+f2.getFilePointer());
			Random m = new Random();
			int size = m.nextInt(1024);
			int leftLimit = 97; // letter 'a'
		    int rightLimit = 122;
			StringBuilder buffer = new StringBuilder(size);
			for (int i = 0; i < size; i++) {
				int randomLimitedInt = leftLimit + (int) 
				          (m.nextFloat() * (rightLimit - leftLimit + 1));
		        buffer.append((char) randomLimitedInt);
		    }
			System.out.println(buffer);
			String  result = buffer.toString();
			f.writeBytes(result);
			
			
			System.out.println("offset after write"+f.getFilePointer());
			
			
			String readres = readFromRandomAccessFile(filename,1,5);
			System.out.println("read string "+readres);
		   } 
        catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	public static void writeToRandomAccessFile(String file, int position, String record)
	{ 
		try {
			RandomAccessFile fileStore = new RandomAccessFile(file, "rw");
			String test = file;
			fileStore.seek(position);
		    fileStore.writeUTF(record); 
		    fileStore.close(); 
		    } 
	catch (IOException e)
		{ 
		e.printStackTrace();
		} 
	}
	
	public static String readFromRandomAccessFile(String file, int position, int num) { 
		String record = null; 
		try { 
			
		RandomAccessFile fileStore = new RandomAccessFile(file, "rw"); // moves file pointer to
		fileStore.seek(position); // reading String from RandomAccessFile
		byte[] b= new byte[1024];
		 fileStore.read(b, position, num); 
		 
		 String text = new String(b, "UTF-8");
		 char[] chars = text.toCharArray();
		 record = text;
		fileStore.close(); 
		} 
		catch (IOException e) 
		{ 
			e.printStackTrace();
			} 
		return record; }
}
