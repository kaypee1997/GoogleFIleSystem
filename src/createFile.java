import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class createFile {
// code for file creation in ./files/ folder
	public static void filecreate(String filename) throws IOException
	{
		RandomAccessFile f;
		double bytes = 0;
		try {
			f = new RandomAccessFile("./files/"+filename, "rw");
			System.out.println("file creation  "+filename+" ------ ");
			
			f.setLength(4096);//4096*1024
		//	long l = f.getFilePointer();
		//	System.out.println("current offset"+l);
		    bytes = f.length();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		double kilobytes = (bytes / 1024);
		double megabytes = (kilobytes / 1024);
		System.out.println("file "+filename+"created with file size:"+megabytes);
		
	}
	//used to download string to a new file after creating new replica
	public static int filewithString(String filename,String filechars) throws IOException
	{
		RandomAccessFile f;
		double bytes = 0;
		int pointer = 0;
		try {
			f = new RandomAccessFile("./files/"+filename, "rw");
			f.setLength(4096);//4096*1024
			long l = f.getFilePointer();
			System.out.println("current offset"+l);
		    bytes = f.length();
		    System.out.println("size of write"+filechars.getBytes().length);
	        f.writeBytes(filechars);
		    System.out.println("offset after write"+f.getFilePointer());
		    long l2 = f.getFilePointer();
		     pointer =(int)l2;
			System.out.println("current offset after write"+l2);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return pointer;
	}
	public static int fileAppendString(String filename,String filechars,int offset) throws IOException
	{
		//boolean result=false; 
		RandomAccessFile f;
		double bytes = 0;
		int pointer=0;
		try {
			RandomAccessFile raf = new RandomAccessFile("./files/"+filename, "rw");
			raf.seek(offset);
			
		//	int leftspace = 4096 -(int)raf.length();
			if(!filechars.equals("NULL"))
			{
			raf.write(filechars.getBytes());
			System.out.println("append op done pointer at"+raf.getFilePointer());
			 pointer = (int)raf.getFilePointer();
			//result=true;
			}
			else
			{
				raf.write(0);
				pointer=offset;
				
			}
			System.out.println("offset for append----: "+ offset +" chars "+filechars +" fileName"+ filename+" pointer "+pointer);
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pointer;	
		
	}
	//during 2 phase commit to check whether the length of a write fits into a 4kb file
	public static boolean checkfileAppend(String filename,int lengthofappend, int fileoffset) throws IOException
	{
		boolean result=false; 
		RandomAccessFile f;
		double bytes = 0;
		try {
			System.out.println("randomaccessfile-- ");
			RandomAccessFile raf = new RandomAccessFile("./files/"+filename, "rw");
			System.out.println("random file access, file :"+raf);
			int remaining = 4096-(fileoffset+1);
			if(remaining >lengthofappend)
			{
				result=true;
				
			}
		else
		{
			result = false;
			
		}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;	
		
	}
	//renaming a file
	public static int renameFile(String filename,String newfile) throws IOException
	{
		
		boolean result=true;
		int pointer=0;
		try{
	        RandomAccessFile data = new RandomAccessFile("./files/"+filename,"rw");
	        RandomAccessFile datacopy = new RandomAccessFile("./files/"+newfile,"rw");

	        datacopy.seek(0);
	        for(int i = 0; i < data.length(); i++){
	            datacopy.write(data.read());
	            
	        }
	        long l2 = data.length();
	        System.out.println("data length"+l2+" file pointer "+data.getFilePointer()+" "+filename);
		     pointer =(int)l2;
	        data.close();
	        datacopy.close();
	    }catch(IOException e){
	    	result=false;
	    }
		
		
		File file = new File("./files/"+filename);

		//Use the try-with-resources to create the RandomAccessFile
		//Which takes care of closing the file once leaving the try block
		try(RandomAccessFile randomFile = new RandomAccessFile(file, "rw")){

		    //do some writing to the file...
			
			randomFile.close();
			file.delete();
		}
		catch(Exception ex){
			result=false;
		    ex.printStackTrace();
		}
		
		
		return pointer;
	}
	
	
}
