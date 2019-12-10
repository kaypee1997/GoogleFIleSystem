
//package testing;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Server {

	static Map<Integer, Integer> hmap = new HashMap<>();// mapping process to stream
	static DataOutputStream dos[] = new DataOutputStream[7];
	static DataInputStream dis[] = new DataInputStream[7];
	static ObjectOutputStream oos[] = new ObjectOutputStream[7];
	static ObjectInputStream ois[] = new ObjectInputStream[7];
	ServerSocket ss1,ss2,ss3,ss4,ss5,ss6,ss7 ;
	Socket s1,s2,s3,s4,s5,s6,s7 ;
	int server_no;
	static Thread[] t;
	static int filecount;
	static HashMap<String, Integer> hmap2 = new HashMap();//linux filename
	static HashMap<String, Integer> hmap3 = new HashMap();// offset
	static HashMap<String, Integer> hmap4 = new HashMap();// chunk index
	static HashMap<String, Integer> hmap5 = new HashMap();//version
	static HashMap<String, String> hmap6 = new HashMap();//actual filename
	static HashMap<String, Boolean> hmap7 = new HashMap();//is full/or not
	static StringBuilder commitchars;
	static HashMap<String,Integer> hmapfileoffset = new HashMap();
	Logger logger;
	public static boolean exit = false;
	boolean heartbeatflag1 = true;
	

	public Server(String args[]) throws IOException {
		ConfigProperties prop = new ConfigProperties();
		server_no = Integer.parseInt(args[0]);
		String metadataServeraddress = prop.getPropValues("metadataServeraddress");
		int metadataServeraddressforserver1 = Integer.parseInt(prop.getPropValues("metadataServeraddressforserver1"));
		int metadataServeraddressforserver2 = Integer.parseInt(prop.getPropValues("metadataServeraddressforserver2"));
		int metadataServeraddressforserver3 = Integer.parseInt(prop.getPropValues("metadataServeraddressforserver3"));
		int metadataServeraddressforserver4 = Integer.parseInt(prop.getPropValues("metadataServeraddressforserver4"));
		int metadataServeraddressforserver5 = Integer.parseInt(prop.getPropValues("metadataServeraddressforserver5"));

		String server1Address = prop.getPropValues("Server1Address");
		String server2Address = prop.getPropValues("Server2Address");
		String server3Address = prop.getPropValues("Server3Address");
		String server4Address = prop.getPropValues("Server4Address");
		String server5Address = prop.getPropValues("Server5Address");
		int server1portforserver2 = Integer.parseInt(prop.getPropValues("server1portforserver2"));
		int server1portforserver3 = Integer.parseInt(prop.getPropValues("server1portforserver3"));
		int server1portforserver4 = Integer.parseInt(prop.getPropValues("server1portforserver4"));
		int server1portforserver5 = Integer.parseInt(prop.getPropValues("server1portforserver5"));
		int server2portforserver3 = Integer.parseInt(prop.getPropValues("server2portforserver3"));
		int server2portforserver4 = Integer.parseInt(prop.getPropValues("server2portforserver4"));
		int server2portforserver5 = Integer.parseInt(prop.getPropValues("server2portforserver5"));
		int server3portforserver4 = Integer.parseInt(prop.getPropValues("server3portforserver4"));
		int server3portforserver5 = Integer.parseInt(prop.getPropValues("server3portforserver5"));
		int server4portforserver5 = Integer.parseInt(prop.getPropValues("server4portforserver5"));

		int server1portforclient1 = Integer.parseInt(prop.getPropValues("server1portforclient1"));
		int server1portforclient2 = Integer.parseInt(prop.getPropValues("server1portforclient2"));
		int server2portforclient1 = Integer.parseInt(prop.getPropValues("server2portforclient1"));
		int server2portforclient2 = Integer.parseInt(prop.getPropValues("server2portforclient2"));
		int server3portforclient1 = Integer.parseInt(prop.getPropValues("server3portforclient1"));
		int server3portforclient2 = Integer.parseInt(prop.getPropValues("server3portforclient2"));
		int server4portforclient1 = Integer.parseInt(prop.getPropValues("server4portforclient1"));
		int server4portforclient2 = Integer.parseInt(prop.getPropValues("server4portforclient2"));
		int server5portforclient1 = Integer.parseInt(prop.getPropValues("server5portforclient1"));
		int server5portforclient2 = Integer.parseInt(prop.getPropValues("server5portforclient2"));
		// given the process number, should be able to retrieve ois stream index
		if (server_no == 1) {
			hmap.put(0, 0);
			hmap.put(2, 1);
			hmap.put(3, 2);
			hmap.put(4, 3);
			hmap.put(5, 4);
			hmap.put(6, 5);
			hmap.put(7, 6);
		}
		if (server_no == 2) {
			hmap.put(0, 0);
			hmap.put(1, 1);
			hmap.put(3, 2);
			hmap.put(4, 3);
			hmap.put(5, 4);
			hmap.put(6, 5);
			hmap.put(7, 6);
		}
		if (server_no == 3) {
			hmap.put(0, 0);
			hmap.put(1, 1);
			hmap.put(2, 2);
			hmap.put(4, 3);
			hmap.put(5, 4);
			hmap.put(6, 5);
			hmap.put(7, 6);
		}
		if (server_no == 4) {
			hmap.put(0, 0);
			hmap.put(1, 1);
			hmap.put(2, 2);
			hmap.put(3, 3);
			hmap.put(5, 4);
			hmap.put(6, 5);
			hmap.put(7, 6);
		}
		if (server_no == 5) {
			hmap.put(0, 0);
			hmap.put(1, 1);
			hmap.put(2, 2);
			hmap.put(3, 3);
			hmap.put(4, 4);
			hmap.put(6, 5);
			hmap.put(7, 6);
		}

		try {
			// logger for server
			File logDir = new File("./logs/");
			if (!(logDir.exists()))
				logDir.mkdir();
			logger = Logger.getLogger(server_no + "server.log");

			FileHandler fh = new FileHandler("logs/" + server_no + "-server.log");
			fh.setFormatter(new SimpleFormatter());
			// fh.setLevel(logLevel);
			logger.addHandler(fh);
			logger.info("Server" + server_no + "initialization");
			// waiting for incoming socket connections from clients

			// server no=1
			if ((server_no) == 1) {
				// connect to metadata server
				s1 = new Socket(metadataServeraddress, metadataServeraddressforserver1);
				oos[0] = new ObjectOutputStream(s1.getOutputStream());
				Thread t = new Thread(new ChannelHandler(s1));
				// start thread for each server
				t.start();
				

				
				

				// connecting to other servers
				for (int i = 1; i < 5; i++) {
					int serverport = 0;
					if (i == 1) {
						serverport = server1portforserver2;
					}
					if (i == 2) {
						serverport = server1portforserver3;
					}
					if (i == 3) {
						serverport = server1portforserver4;
					}
					if (i == 4) {
						serverport = server1portforserver5;
					}
					System.out.println("----- "+ serverport);

					ss2 = new ServerSocket(serverport);
					s2 = ss2.accept();
					oos[i] = new ObjectOutputStream(s2.getOutputStream());
					Thread t2 = new Thread(new ChannelHandler(s2));
					t2.start();
					

					
					System.out.print("Starting thread number" + i);
					logger.info("Starting thread number" + i);
				}

				// accept client connections
				ss3 = new ServerSocket(server1portforclient1);
				s3 = ss3.accept();
				oos[5] = new ObjectOutputStream(s3.getOutputStream());
				

				Thread t3 = new Thread(new ChannelHandler(s3));
				t3.start();
				
				System.out.print("Starting thread number" + 5);
				logger.info("Starting thread number" + 5);

				ss4 = new ServerSocket(server1portforclient2);
				s4 = ss4.accept();
				oos[6] = new ObjectOutputStream(s4.getOutputStream());

				Thread t4 = new Thread(new ChannelHandler(s4));
				t4.start();
				
				System.out.print("Starting thread number" + 6);
				logger.info("Starting thread number" + 6);

			}
			// server no=2
			if ((server_no) == 2) {
				// connect to metadata server

				s1 = new Socket(metadataServeraddress, metadataServeraddressforserver2);
				oos[0] = new ObjectOutputStream(s1.getOutputStream());
				Thread t = new Thread(new ChannelHandler(s1));
				// start thread for each server
				t.start();
				

				s2 = new Socket(server1Address, server1portforserver2);
				oos[1] = new ObjectOutputStream(s2.getOutputStream());
				Thread t2 = new Thread(new ChannelHandler(s2));
				// start thread for each server
				t2.start();
				
				
				// connect to server1 first before accepting connections from other clients

				for (int i = 2; i < 5; i++) {
					int serverport = 0;

					if (i == 2) {
						serverport = server2portforserver3;
					}
					if (i == 3) {
						serverport = server2portforserver4;
					}
					if (i == 4) {
						serverport = server2portforserver5;
					}
					ss3 = new ServerSocket(serverport);
					s3 = ss3.accept();
					oos[i] = new ObjectOutputStream(s3.getOutputStream());
					System.out.print("Server port created at " + ss3 + "" + s3);
					logger.info("Server port created at " + ss3 + "" + s3);

					Thread t3 = new Thread(new ChannelHandler(s3));
					t3.start();
					
					System.out.print("Starting thread number" + i);
					logger.info("Starting thread number" + i);
				}
				// accept client connections
				ss4 = new ServerSocket(server2portforclient1);
				s4 = ss4.accept();
				oos[5] = new ObjectOutputStream(s4.getOutputStream());

				Thread t4 = new Thread(new ChannelHandler(s4));
				t4.start();
				
				System.out.print("Starting thread number" + 5);
				logger.info("Starting thread number" + 5);

				ss5 = new ServerSocket(server2portforclient2);
				s5 = ss5.accept();
				oos[6] = new ObjectOutputStream(s5.getOutputStream());

				Thread t5 = new Thread(new ChannelHandler(s5));
				t5.start();
				
				System.out.print("Starting thread number" + 6);
				logger.info("Starting thread number" + 6);

			}
			// server no=3
			if ((server_no) == 3) {

				// connect to metadata server
				s1 = new Socket(metadataServeraddress, metadataServeraddressforserver3);
				oos[0] = new ObjectOutputStream(s1.getOutputStream());
				Thread t = new Thread(new ChannelHandler(s1));
				// start thread for each server
				t.start();

				s2 = new Socket(server1Address, server1portforserver3);
				oos[1] = new ObjectOutputStream(s2.getOutputStream());
				Thread t2 = new Thread(new ChannelHandler(s2));
				// start thread for each server
				t2.start();

				s3 = new Socket(server2Address, server2portforserver3);
				oos[2] = new ObjectOutputStream(s3.getOutputStream());
				Thread t3 = new Thread(new ChannelHandler(s3));
				// start thread for each server
				t3.start();

				for (int i = 3; i < 5; i++) {
					int serverport = 0;

					if (i == 3) {
						serverport = server3portforserver4;
					}
					if (i == 4) {
						serverport = server3portforserver5;
					}
					ss4 = new ServerSocket(serverport);
					s4 = ss4.accept();
					oos[i] = new ObjectOutputStream(s4.getOutputStream());
					//System.out.print("Server port created at " + ss4 + "" + s4);
					//logger.info("Server port created at " + ss[i] + "" + s[i]);
					Thread t4 = new Thread(new ChannelHandler(s4));
					t4.start();
					System.out.print("Starting thread number" + i);
					logger.info("Starting thread number" + i);
				}
				// accept client connections
				ss5 = new ServerSocket(server3portforclient1);
				s5 = ss5.accept();
				oos[5] = new ObjectOutputStream(s5.getOutputStream());

				Thread t5 = new Thread(new ChannelHandler(s5));
				t5.start();
				System.out.print("Starting thread number" + 5);
				logger.info("Starting thread number" + 5);

				ss6 = new ServerSocket(server3portforclient2);
				s6 = ss6.accept();
				oos[6] = new ObjectOutputStream(s6.getOutputStream());

				Thread t6 = new Thread(new ChannelHandler(s6));
				t6.start();
				System.out.print("Starting thread number" + 6);
				logger.info("Starting thread number" + 6);
			}
			// server no=4
			if ((server_no) == 4) {

				s1 = new Socket(metadataServeraddress, metadataServeraddressforserver4);
				oos[0] = new ObjectOutputStream(s1.getOutputStream());
				Thread t = new Thread(new ChannelHandler(s1));
				// start thread for each server
				t.start();

				s2 = new Socket(server1Address, server1portforserver4);
				oos[1] = new ObjectOutputStream(s2.getOutputStream());
				Thread t2 = new Thread(new ChannelHandler(s2));
				// start thread for each server
				t2.start();
				s3 = new Socket(server2Address, server2portforserver4);
				oos[2] = new ObjectOutputStream(s3.getOutputStream());
				Thread t3 = new Thread(new ChannelHandler(s3));
				// start thread for each server
				t3.start();
				s4 = new Socket(server3Address, server3portforserver4);
				oos[3] = new ObjectOutputStream(s4.getOutputStream());
				Thread t4 = new Thread(new ChannelHandler(s4));
				// start thread for each server
				t4.start();
				for (int i = 4; i < 5; i++) {
					int serverport = 0;

					if (i == 4) {
						serverport = server4portforserver5;
					}
					ss5 = new ServerSocket(serverport);
					s5 = ss5.accept();
					
					System.out.print("Server port created at " + ss5 + "" + s5);
					logger.info("Server port created at " + ss5 + "" + s5);
					oos[i] = new ObjectOutputStream(s5.getOutputStream());
					Thread t5 = new Thread(new ChannelHandler(s5));
					t5.start();
					System.out.print("Starting thread number" + i);
					logger.info("Starting thread number" + i);
				}
				// accept client connections
				ss6 = new ServerSocket(server4portforclient1);
				s6 = ss6.accept();
				oos[5] = new ObjectOutputStream(s6.getOutputStream());
				

				Thread t6 = new Thread(new ChannelHandler(s6));
				t6.start();
				System.out.print("Starting thread number" + 5);
				logger.info("Starting thread number" + 5);

				ss7 = new ServerSocket(server4portforclient2);
				s7 = ss7.accept();
				oos[6] = new ObjectOutputStream(s7.getOutputStream());
				

				Thread t7 = new Thread(new ChannelHandler(s7));
				t7.start();
				System.out.print("Starting thread number" + 6);
				logger.info("Starting thread number" + 6);
			}
			// server no=5
			if ((server_no) == 5) {

				s1 = new Socket(metadataServeraddress, metadataServeraddressforserver5);
				oos[0] = new ObjectOutputStream(s1.getOutputStream());
				Thread t = new Thread(new ChannelHandler(s1));
				// start thread for each server
				t.start();

				s2 = new Socket(server1Address, server1portforserver5);
				oos[1] = new ObjectOutputStream(s2.getOutputStream());
				Thread t2 = new Thread(new ChannelHandler(s2));
				// start thread for each server
				t2.start();
				s3 = new Socket(server2Address, server2portforserver5);
				oos[2] = new ObjectOutputStream(s3.getOutputStream());
				Thread t3 = new Thread(new ChannelHandler(s3));
				// start thread for each server
				t3.start();
				s4 = new Socket(server3Address, server3portforserver5);
				oos[3] = new ObjectOutputStream(s4.getOutputStream());
				
				Thread t4 = new Thread(new ChannelHandler(s4));
				// start thread for each server
				t4.start();
				
				
				s5 = new Socket(server4Address, server4portforserver5);
				oos[4] = new ObjectOutputStream(s5.getOutputStream());
				Thread t5 = new Thread(new ChannelHandler(s5));
				// start thread for each server
				t5.start();

				ss6 = new ServerSocket(server5portforclient1);
				s6 = ss6.accept();
				oos[5] = new ObjectOutputStream(s6.getOutputStream());

				Thread t6 = new Thread(new ChannelHandler(s6));
				t6.start();
				System.out.print("Starting thread number" + 5);
				logger.info("Starting thread number" + 5);

				ss7 = new ServerSocket(server5portforclient2);
				s7 = ss7.accept();
				oos[6] = new ObjectOutputStream(s7.getOutputStream());
				Thread t7 = new Thread(new ChannelHandler(s7));
				t7.start();
				System.out.print("Starting thread number" + 6);
				logger.info("Starting thread number" + 6);
			}

			System.out.print("connected to all clients");
			logger.info("connected to all clients");
		} catch (Exception e) {
			e.printStackTrace();

		}

		// versionum
		
		hashmapInitialization();// used for initialization of hashmaps from the existing files info
		// generating heartbeat messages every 5 secs
		
		while (true) {
			// stop sending heartbeat msg to srver
			if(!heartbeatflag1) {
				try {
					System.out.println("heartbeat flag value"+heartbeatflag1);
					Thread.sleep(100000);
					heartbeatflag1 = true;
					System.out.println("heartbeatFlag1 value again changed to"+heartbeatflag1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(heartbeatflag1)
			{
			int iteration = 5;
			try {
				
					
				Thread.sleep(iteration * 1000);
				File[] files = getFiles();

				heartbeat[] hms = new heartbeat[files.length];
				for (int i = 0; i < files.length; i++) {
					File file4 = files[i];
					heartbeat hb = new heartbeat();
					hb.setSenderID(server_no);// server_no
					hb.setFileName(hmap6.get(file4.getName()));// linuxfile
					hb.setLinuxFileName(file4.getName());// chunkfilename
					// change 1997
					hmap5.putIfAbsent(file4.getName(),0);
					hb.setVersion_num(hmap5.get(file4.getName()));// version
					// change 1997
					hmap3.putIfAbsent(file4.getName(),0);
					hb.setLastoffset(hmap3.get(file4.getName()));// lastoffset
					// change 1997
					hmap4.putIfAbsent(file4.getName(),1);
					hb.setChunkindex(hmap4.get(file4.getName()));// chunkindex 
					// change 1997
					hmap7.putIfAbsent(file4.getName(), false);
					hb.setFull(hmap7.get(file4.getName()));
					//constructing heartbeat msgs
					
					// change 1
					//System.out.println(hb.getStringRepresentation());
					hms[i] = hb;
					// change 2
					//System.out.println(server_no+","+hmap6.get(file4.getName())+","+file4.getName()+","+hmap3.get(file4.getName())+","+hmap4.get(file4.getName())+","+hmap5.get(file4.getName()));
				}

				
				heartbeatMessage hbm = new heartbeatMessage(hms);
				hbm.setMsgtype(MessageType.HEARTBEAT);
				hbm.setSenderID(this.server_no);
				oos[0].writeObject(hbm);
				}
			 catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//change 3
			//System.out.println("i should appear every 5 secs");

		}}

	}
	// threads handling each client

	public void hashmapInitialization() {
		StringBuilder filenames = new StringBuilder();
		filenames.append("LINUX FILE NAMES" + ":" + "INDICES" + "\n");
		File[] allfiles1 = getFiles();
		for (int i = 0; i < allfiles1.length; i++) {
			File file2 = allfiles1[i];
			String test = file2.getName().split("_")[0];
			
			// hmap2.put(file.getName() , file.getName().split("_")[0]) ;
			hmap2.put(file2.getName(), i);// index to search
			filenames.append(file2.getName() + ":" + hmap2.get(file2.getName()) + "\n");

		}
		filenames.append("=======================================");
		filenames.append("LINUX FILE NAMES" + ":" + "ACTUAL FILE NAMES" + "\n");
		File[] allfiles2 = getFiles();
		for (int i = 0; i < allfiles2.length; i++) {
			File file2 = allfiles2[i];
			String test = file2.getName().split("_")[0];
			
			// hmap2.put(file.getName() , file.getName().split("_")[0]) ;
			hmap6.put(file2.getName(), file2.getName().split("_")[0]);
			filenames.append(file2.getName() + ":" + hmap6.get(file2.getName()) + "\n");

		} // file-> linux file mapping
		filenames.append("=======================================");
		filenames.append("LINUX FILE NAMES" + ":" + "OFFSET" + "\n");
		File[] allfiles3 = getFiles();
		for (int i = 0; i < allfiles3.length; i++) {
			File file2 = allfiles3[i];

			
			// hmap2.put(file.getName() , file.getName().split("_")[0]) ;
			hmap3.put(file2.getName(), 0);// offset
			filenames.append(file2.getName() + ":" + hmap3.get(file2.getName()) + "\n");

		}
		filenames.append("=======================================");
		filenames.append("LINUX FILE NAMES" + ":" + "CHUNK INDEX" + "\n");
		File[] allfiles4 = getFiles();
		for (int i = 0; i < allfiles4.length; i++) {
			File file2 = allfiles4[i];

		
			// hmap2.put(file.getName() , file.getName().split("_")[0]) ;
			hmap4.put(file2.getName(), Integer.parseInt(file2.getName().split("_")[1]));// chunk index
			filenames.append(file2.getName() + ":" + hmap4.get(file2.getName()) + "\n");
		}
		filenames.append("=======================================");
		filenames.append("LINUX FILE NAMES" + ":" + "VERSION NUMBER" + "\n");
		File[] allfiles5 = getFiles();
		for (int i = 0; i < allfiles5.length; i++) {
			File file2 = allfiles5[i];

			
			// hmap2.put(file.getName() , file.getName().split("_")[0]) ;
			hmap5.put(file2.getName(), Integer.parseInt(file2.getName().split("_")[2]));// version number
			filenames.append(file2.getName() + ":" + hmap5.get(file2.getName()) + "\n");
		}
		filenames.append("=====================" + "\n");
		filenames.append("=======================================");
		filenames.append("LINUX FILE NAMES" + ":" + "FULL/NOT FULL" + "\n");
		File[] allfiles6 = getFiles();
		for (int i = 0; i < allfiles6.length; i++) {
			File file2 = allfiles6[i];

			
			// hmap2.put(file.getName() , file.getName().split("_")[0]) ;
			hmap7.put(file2.getName(), false);// version number
			filenames.append(file2.getName() + ":" + hmap7.get(file2.getName()) + "\n");
		}
		filenames.append("=====================" + "\n");
		String filestowrite = filenames.toString();
		// Write Content

		try {
			FileWriter fileWriter = new FileWriter("filesAtServer.txt");
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.print(filestowrite);

			printWriter.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	// get all the files in the ./files/ directory
	public static File[] getFiles() {
		File dir = new File("./files/");

		File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
		//System.out.println("files -- - -"+files.length);
		for (File txtfile : files) {
			//change
			//System.out.println(txtfile);
			filecount++;

		}
		return files;
	}

	public static void writeToRandomAccessFile(String file, int position, String record) {
		try {
			RandomAccessFile fileStore = new RandomAccessFile("./files/"+file, "rw");
			
			// change code to seek!
			fileStore.seek(position);
			fileStore.writeUTF(record);
		    int fileoffset = 	(int)fileStore.getFilePointer();
			hmapfileoffset.put(file, fileoffset);
			hmap3.put(file, fileoffset);
			fileStore.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
//read from random access file
	public static String readFromRandomAccessFile(String file, int position) {

		// removed num -> third parameter should check its functionality now!
		
		String record = null;
		try {
			
			System.out.println("server got read resposed for +"+file);
			// change 1997
			RandomAccessFile fileStore = new RandomAccessFile("./files/"+file, "r"); // moves file pointer to
			System.out.println("server got file +"+fileStore);
			
			fileStore.seek(position); // reading String from RandomAccessFile
			byte[] b = new byte[4096];
			int fileoffsetpoint =hmapfileoffset.get(file);
			System.out.println("position "+ position +"fileoffsetpoint :"+fileoffsetpoint);
			// send the entire file in a string
			fileStore.read(b, position, 4096-position);//may be +1?
			
			String text = new String(b, "UTF-8");
			char[] chars = text.toCharArray();
			record = text;
			fileStore.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return record;
	}

	
	/*public void buildFileHashmap() {
		StringBuilder filenames = new StringBuilder();
		filenames.append("LINUX FILE NAMES" + ":" + "INDICES" + "\n");
		File[] allfiles1 = getFiles();
		for (int i = 0; i < allfiles1.length; i++) {
			File file2 = allfiles1[i];
			String test = file2.getName().split("_")[0];
			hmap2[i] = new HashMap();
			// hmap2.put(file.getName() , file.getName().split("_")[0]) ;
			hmap2[i].put(file2.getName(), i);// index to search
			filenames.append(file2.getName() + ":" + hmap2[i].get(file2.getName()) + "\n");

		}
		filenames.append("=======================================");
		filenames.append("LINUX FILE NAMES" + ":" + "ACTUAL FILE NAMES" + "\n");
		File[] allfiles2 = getFiles();
		for (int i = 0; i < allfiles2.length; i++) {
			File file2 = allfiles2[i];
			String test = file2.getName().split("_")[0];
			hmap6[i] = new HashMap();
			// hmap2.put(file.getName() , file.getName().split("_")[0]) ;
			hmap6[i].put(file2.getName(), file2.getName().split("_")[0]);
			filenames.append(file2.getName() + ":" + hmap6[i].get(file2.getName()) + "\n");

		} // file-> linux file mapping
		filenames.append("=======================================");
		filenames.append("LINUX FILE NAMES" + ":" + "OFFSET" + "\n");
		File[] allfiles3 = getFiles();
		for (int i = 0; i < allfiles3.length; i++) {
			File file2 = allfiles3[i];

			hmap3[i] = new HashMap();
			// hmap2.put(file.getName() , file.getName().split("_")[0]) ;
			hmap3[i].put(file2.getName(), 0);// offset
			filenames.append(file2.getName() + ":" + hmap3[i].get(file2.getName()) + "\n");

		}
		filenames.append("=======================================");
		filenames.append("LINUX FILE NAMES" + ":" + "CHUNK INDEX" + "\n");
		File[] allfiles4 = getFiles();
		for (int i = 0; i < allfiles4.length; i++) {
			File file2 = allfiles4[i];

			hmap4[i] = new HashMap();
			// hmap2.put(file.getName() , file.getName().split("_")[0]) ;
			hmap4[i].put(file2.getName(), Integer.parseInt(file2.getName().split("_")[1]));// chunk index
			filenames.append(file2.getName() + ":" + hmap4[i].get(file2.getName()) + "\n");
		}
		filenames.append("=======================================");
		filenames.append("LINUX FILE NAMES" + ":" + "VERSION NUMBER" + "\n");
		File[] allfiles5 = getFiles();
		for (int i = 0; i < allfiles5.length; i++) {
			File file2 = allfiles5[i];

			hmap5[i] = new HashMap();
			// hmap2.put(file.getName() , file.getName().split("_")[0]) ;
			hmap5[i].put(file2.getName(), Integer.parseInt(file2.getName().split("_")[2]));// version number
			filenames.append(file2.getName() + ":" + hmap5[i].get(file2.getName()) + "\n");
		}
		filenames.append("=====================" + "\n");
		String filestowrite = filenames.toString();
		// Write Content

		try {
			FileWriter fileWriter = new FileWriter("filesAtServer.txt");
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.print(filestowrite);

			printWriter.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}*/

	/* server number to be given as argument while running */
	public static void main(String[] args) throws IOException {
		new Server(args);
	}
//threads handling the server
	class ChannelHandler implements Runnable {
		ObjectInputStream objinput;
		ObjectOutputStream objoutput;
		Socket socket;

		public ChannelHandler(Socket s) {
			try {
				// initializing data i/p and o/p streams
				socket = s;
				// datainput = new DataInputStream(s.getInputStream());
				// dataoutput = new DataOutputStream(s.getOutputStream());
				//objinput = new ObjectInputStream(s.getInputStream());
				//objoutput = new ObjectOutputStream(s.getOutputStream());
				//System.out.print("after socket initialization" + objinput + " " + objoutput + " " + s);
				//logger.info("After socket initialization" + objinput + " " + objoutput + " " + s);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		@Override
		public void run() {
			
			
			try {
				
				System.out.println("Inside run");
			
				
				String message = "";
				logger.info("Inside run before while");
				String messagefromclient = "notover";
				logger.info("Inside run()");
				//objoutput = new ObjectOutputStream(socket.getOutputStream());
				objinput = new ObjectInputStream(socket.getInputStream());
				System.out.print("after socket input  initialization" + socket);
				logger.info("after socket input  initialization" + socket);
				
				// continuously read data

				while (!messagefromclient.equals("Over")) {

					// check for data from socket
					

						Message object = (Message) objinput.readObject();
						String messagetype = object.getMsgtype().toString();

						System.out
								.println("Message from the client " + object.getSenderID() + "of type " + messagetype);

						logger.info("Message from the client " + object.getSenderID() + "of type " + messagetype);

						// read the message from client and determine the type of message

						// CREATECOMMAND,// MS TO SERVER FOR NEW FILE CREATION to do

						if (messagetype.equals("CREATECOMMAND")) {
							//this.senderID = senderUID;
							//this.msgtype = Msgtype;
							//this.fileName = FileName;
							
							//change
							String filename = object.getChunkname();
							try {
								createFile.filecreate(filename);
								// adding entries in hashmaps for the newly created file
								
								File[] allfiles2 = getFiles();
								int totalfiles = allfiles2.length;
								
								hmap2.put(filename, totalfiles - 1);// index
								
								hmap6.put(filename, filename.split("_")[0]);// actual file name
								
								hmap3.put(filename, 0);// last offset
								hmapfileoffset.put(filename,0)	;	
								hmap4.put(filename, Integer.parseInt(filename.split("_")[1]));// chunk index
																										// index
								
								hmap5.put(filename, Integer.parseInt(filename.split("_")[2]));// version
								hmap7.put(filename, false);																				// number
							} catch (Exception e) {
								System.out.println("there was some issue with file creation"+e.getMessage());
							}

						}
// after the server comes up from dead,this msg sent from metaserver to server
						if (messagetype.equals("UPDATEREPLICA")) {

						//	this.senderID = senderUID;
						//	this.msgtype = Msgtype;
						//	this.fileName = FileName;
						//	this.server1= server1;
						//	this.chunkname1= chunkname1;
                            String fileName = object.getFileName();
							int server = object.getServer1();
							String chunkname = object.getChunkname1();
							// now issue a read request
							File file = new File("./files/"+fileName);
							// updating hmaps
							hmap2.remove(fileName);// index
							hmap6.remove(fileName);
							hmap3.remove(fileName);
							hmapfileoffset.remove(fileName)	;
							hmap4.remove(fileName);
							hmap5.remove(fileName);
							hmap7.remove(fileName);
							
							
							
// deleting the current file
							try(RandomAccessFile randomFile = new RandomAccessFile(file, "rw")){

							   
								
								randomFile.close();
								file.delete();
							}
							catch(Exception ex){
							    ex.printStackTrace();
							}
							// this.senderID = senderUID;
							// this.msgtype = Msgtype;
							// .fileName = FileName;
							// this.offset = offset;
							Message m = new Message(server_no, MessageType.READFILE, chunkname, 0);

							oos[hmap.get(server)].writeObject(m);
							System.out.println("read request sent to the server " + server);

						}
					//client sending signal for stopping heartbeat 
						if (messagetype.equals("HEARTBEAT")) {

							   if(object.getFileName().equals("STOP"))
							   {
                                heartbeatflag1=false;
								
								System.out.println("heartbeat received at the server to stop"+heartbeatflag1);
								
								//System.out.println("heartbeat received at the server to stop"+heartbeatflag1);
							   }
							   
							   if(object.getFileName().equals("START"))
							   {
                                heartbeatflag1=true;
								
								System.out.println("heartbeat received at the server to start"+heartbeatflag1);
							   }

							}
						// server sent update message to another server after replica update
						if (messagetype.equals("READFILERESPONSE")) {
							//this.senderID = senderUID;
							//this.msgtype = Msgtype;
							//this.fileName = FileName;
							//this.readcharacters = filescharacters;
							String filename = object.getFileName();
							String filecharacters = object.getReadcharacters();

							String filenameatmylocation = filename.split("_")[0] + "_" + filename.split("_")[1] + "_"
									+ filename.split("_")[2] + "_" + server_no+".txt";
							
							// DELETE THE FILE AND THEN CALL GETFILES() METHOD done
							File[] allfiles2 = getFiles();
							int totalfiles = allfiles2.length;
							hmap2.put(filename, totalfiles - 1);// index //impact on already existing file but hmap2 where used? looping!
							
							hmap6.put(filename, filename.split("_")[0]);// actual file name
							
							hmap3.put(filename, 0);// last offset
							hmapfileoffset.put(filename,0)	;	
							hmap4.put(filename, Integer.parseInt(filename.split("_")[1]));// chunk index
																									// index
							
							hmap5.put(filename, Integer.parseInt(filename.split("_")[2]));// version
							hmap7.put(filename, false);	
							
							
							try {
								int pointer=createFile.filewithString(filenameatmylocation, filecharacters);
								hmap3.put(filename, pointer);// last offset
								hmapfileoffset.put(filename,pointer)	;	
								System.out.println("Downloaded file!");
								logger.info("Downloaded file!");
							} catch (Exception e) {
								System.out.println("Error  while downloading file"+e);
							}
						}
						// server sends another server to download a given file
						if (messagetype.equals("READFILE")) {
							//this.senderID = senderUID;
							//this.msgtype = Msgtype;
							//this.fileName = FileName;
							//this.offset = offset;
							// (client_no,MessageType.READFILE,chunkname,chunkoffset);
							String fileName = object.getFileName();
							int offset = object.getOffset();
							int requestor = object.getSenderID();

							String filecharacters = readFromRandomAccessFile(fileName, offset);
							// this.senderID = senderUID;
							// this.msgtype = Msgtype;
							// this.fileName = FileName;
							// this.readcharacters = filescharacters;

							Message m = new Message(server_no, MessageType.READFILERESPONSE, fileName, filecharacters);
							System.out.println("Sending file"+filecharacters);
							logger.info("Sending file");
							oos[hmap.get(requestor)].writeObject(m);
							System.out.println("Sent file");
							logger.info("Sent file");

						}
//commit from client
						if (messagetype.equals("COMMIT")) {
							//this.senderID = senderUID;
							//this.msgtype = Msgtype;
							//this.fileName = chunkname;
							//this.chunkoffset=chunkoffset;// no need of this, set to default value
							//this.appendString= appendString;
							String chunkname = object.getFileName();
							int chunkoffset = object.getChunkoffset();
							int requestor = object.getSenderID();
							String stringtoappend = object.getAppendString();
							try {
								int pointer=createFile.fileAppendString(chunkname, stringtoappend,hmapfileoffset.get(chunkname));
								
								
							//	hmap3.put(chunkname, pointer); last offset update with new names!
							//	hmapfileoffset.put(chunkname,pointer)	
								
								
							//	hmap5.put(chunkname, Integer.parseInt(chunkname.split("_")[2]+1));// version
								// ADD CODE TO RENAME THE FILE! and update hmaps
								int version = Integer.parseInt(chunkname.split("_")[2])+1;
								String newname = chunkname.split("_")[0] + "_" + chunkname.split("_")[1] + "_"
										+ Integer.toString(version) + "_" + server_no+".txt";
								int createfilepointer=createFile.renameFile(chunkname,newname);
								int finalOffSet = hmap3.get(chunkname)+stringtoappend.length();;
								
								
								
								hmap2.remove(chunkname);// index
								hmap6.remove(chunkname);
								hmap3.remove(chunkname);
								hmapfileoffset.remove(chunkname);
								hmap4.remove(chunkname);
								hmap5.remove(chunkname);
								hmap7.remove(chunkname);
								
								File[] allfiles2 = getFiles();
								int totalfiles = allfiles2.length;
								hmap2.put(newname, totalfiles - 1);// index //impact on already existing file but hmap2 where used? looping!
								
								hmap6.put(newname, newname.split("_")[0]);// actual file name
								System.out.println("createfilepointer +"+finalOffSet);
								hmap3.put(newname,finalOffSet);// last offset
								hmapfileoffset.put(newname,finalOffSet)	;	
								hmap4.put(newname, Integer.parseInt(newname.split("_")[1]));// chunk index
																										// index
								
								hmap5.put(newname, Integer.parseInt(newname.split("_")[2]));// version
								hmap7.put(newname, false);	
								
								
							} catch (Exception e) {
								System.out.println("Some problem appending file");
							}

							System.out.println("Committed file");
							logger.info("Committed file");

						}

						/*
						 * if (messagetype.equals("COMMIT")) { //this.senderID = senderUID;
						 * //this.msgtype = Msgtype; //this.fileName = chunkname;
						 * //this.chunkoffset=chunkoffset;// no need of this, set to default value
						 * //this.appendString= appendString; String chunkname = object.getFileName();
						 * int chunkoffset = object.getChunkoffset(); int requestor =
						 * object.getSenderID(); String stringtoappend = object.getAppendString(); try {
						 * createFile.fileAppendString(chunkname,stringtoappend); } catch(Exception e) {
						 * System.out.println("Some problem appending file"); }
						 * 
						 * 
						 * System.out.println("Committed file"); logger.info("Committed file");
						 * 
						 * }
						 */
// client sends cmmit request for 2 phase commit
						if (messagetype.equals("COMMITREQUEST")) {
							
							//this.senderID = senderUID;
							//this.msgtype = Msgtype;
							//this.fileName = chunkname;
							//this.chunkoffset=chunkoffset;  // no need of this, set to default value
							//this.sizeofappend= sizeofappend;
							String chunkname = object.getFileName();
							int chunkoffset = object.getChunkoffset();
							int requestor = object.getSenderID();
							int sizeofappend = object.getSizeofappend();
							
							System.out.println("file/chunk for comimt "+chunkname+" chunkoffset "+chunkoffset+" requestor "
									+requestor+" sizeofappend");
							boolean checkresult = createFile.checkfileAppend(chunkname, sizeofappend,hmapfileoffset.get(chunkname));
							//boolean checkresult = true;
							//boolean result = createFile.fileAppendString(chunkname, "NULL");

							if (checkresult == false) {
								createFile.fileAppendString(chunkname, "NULL",hmapfileoffset.get(chunkname));
								//hmapfileoffset.put(chunkname, 4095);
								hmap7.put(chunkname, true);
								System.out.println("Cannot commit write as file size is not enough, added null char");
								logger.info("Cannot commit write as file size is not enough, added null char");

								// public Message(int senderUID, MessageType Msgtype, String FileName) {
								// this.senderID = senderUID;
								// this.msgtype = Msgtype;
								// this.fileName = FileName;
								Message m = new Message(server_no, MessageType.ABORT, "ABORT");
								oos[hmap.get(requestor)].writeObject(m);

							}

							else {
								Message m2 = new Message(server_no, MessageType.AGREED, "AGREE");
								oos[hmap.get(requestor)].writeObject(m2);

							}

						}

						/*
						 * if (messagetype.equals("COMMITREQUEST")) { String chunkname =
						 * object.getFileName(); int chunkoffset = object.getChunkoffset(); int
						 * requestor = object.getSenderID(); int sizeofappend =
						 * object.getSizeofappend(); boolean checkresult =
						 * createFile.checkfileAppend(chunkname,sizeofappend); boolean result =
						 * createFile.fileAppendString(chunkname,"NULL");
						 * 
						 * if(result==false) {
						 * 
						 * System.out.
						 * println("Cannot commit write as file size is not enough, added null char");
						 * logger.info("Cannot commit write as file size is not enough, added null char"
						 * );
						 * 
						 * //public Message(int senderUID, MessageType Msgtype, String FileName) {
						 * //this.senderID = senderUID; //this.msgtype = Msgtype; //this.fileName =
						 * FileName; Message m = new Message(server_no,MessageType.ABORT,"ABORT");
						 * oos[hmap.get(requestor)].writeObject(m);
						 * 
						 * }
						 * 
						 * 
						 * else { Message m2 = new Message(server_no,MessageType.AGREED,"AGREE");
						 * oos[hmap.get(requestor)].writeObject(m2);
						 * 
						 * 
						 * }
						 * 
						 * 
						 * }
						 */
					

				}
				System.out.println("Reached end of while");
				logger.info("Reached end of while");

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
	}
}
