
//package testing;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Client {

	static Map<String, Integer> hmap_streams = new HashMap<>();// not required for now
	static DataOutputStream dos[] = new DataOutputStream[6];
	static DataInputStream dis[] = new DataInputStream[6];
	static ObjectOutputStream oos[] = new ObjectOutputStream[7];
	static ObjectInputStream ois[] = new ObjectInputStream[7];
    static Map<Integer, Integer> hmap_2phase = new HashMap<>();
    static int responsetobeReceivedcount=0;
	 
	static int client_no;
	static Thread[] t;
	public boolean exit=false;
	Logger logger;

	public Client(String args[]) throws IOException {
		Socket s1,s2,s3,s4,s5,s6,s7;
		ConfigProperties prop = new ConfigProperties();
		client_no = Integer.parseInt(args[0]);
		String metadataServeraddress = prop.getPropValues("metadataServeraddress");
/* reading from config file*/
		String server1Address =  prop.getPropValues("Server1Address");
		String server2Address =  prop.getPropValues("Server2Address");
		String server3Address =  prop.getPropValues("Server3Address");
		String server4Address =  prop.getPropValues("Server4Address");
		String server5Address =  prop.getPropValues("Server5Address");

		int metadataServeraddressforclient1 = Integer.parseInt(prop.getPropValues("metadataServeraddressforclient1"));
		int metadataServeraddressforclient2 = Integer.parseInt(prop.getPropValues("metadataServeraddressforclient2"));

		// server ports for client 1 ->10001 client 2---> 10002
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

		try {
			//logger for client
			File logDir = new File("./logs/");
			if (!(logDir.exists()))
				logDir.mkdir();
			logger = Logger.getLogger(client_no + "client.log");
/*produce logs*/
			FileHandler fh = new FileHandler("logs/" + client_no + "-client.log");
			fh.setFormatter(new SimpleFormatter());
			// fh.setLevel(logLevel);
			logger.addHandler(fh);
			logger.info("Client" + client_no + "initialization");
			// waiting for incoming socket connections from clients

			//client no=1
			if ((client_no) == 6) 
			{
				//connect to metadata server
				s1 = new Socket(metadataServeraddress, metadataServeraddressforclient1);
				oos[0] = new ObjectOutputStream(s1.getOutputStream());
				Thread t = new Thread(new ChannelHandler(s1));
				//start thread for each server
				t.start();
				System.out.print("Started thread number 0");
				logger.info("Started thread number 0");

				// connecting to other servers
				for (int i = 1; i <=5; i++) 
				{
					String serveraddress="";
					int port =0;
					if(i==1)
					{
						serveraddress= 	server1Address;
						port = server1portforclient1;
					}
					if(i==2)
					{
						serveraddress= 	server2Address;
						port = server2portforclient1;
					}
					if(i==3)
					{
						serveraddress= 	server3Address;
						port = server3portforclient1;
					}
					if(i==4)
					{
						serveraddress= 	server4Address;
						port = server4portforclient1;
					}
					if(i==5)
					{
						serveraddress= 	server5Address;
						port = server5portforclient1;
					}
					s2 = new Socket(serveraddress,port);
                    oos[i]= new ObjectOutputStream(s2.getOutputStream());
					
					
					Thread t2 = new Thread(new ChannelHandler(s2));
					t2.start();
					System.out.print("Started thread number" + i);
					logger.info("Started thread number" + i);
				}



			}
			if ((client_no) == 7) 
			{
				//connect to metadata server
				s1 = new Socket(metadataServeraddress, metadataServeraddressforclient2);
				oos[0] = new ObjectOutputStream(s1.getOutputStream());
				Thread t = new Thread(new ChannelHandler(s1));
				//start thread for each server
				t.start();
				System.out.print("Started thread number 0");
				logger.info("Started thread number 0");

				// connecting to other servers
				for (int i = 1; i <=5; i++) 
				{
					String serveraddress="";
					int port =0;
					if(i==1)
					{
						serveraddress= 	server1Address;
						port = server1portforclient2;
					}
					if(i==2)
					{
						serveraddress= 	server2Address;
						port = server2portforclient2;
					}
					if(i==3)
					{
						serveraddress= 	server3Address;
						port = server3portforclient2;
					}
					if(i==4)
					{
						serveraddress= 	server4Address;
						port = server4portforclient2;
					}
					if(i==5)
					{
						serveraddress= 	server5Address;
						port = server5portforclient2;
					}
					s2 = new Socket(serveraddress,port);
                    oos[i]= new ObjectOutputStream(s2.getOutputStream());
					
					
					Thread t2 = new Thread(new ChannelHandler(s2));
					t2.start();
					System.out.print("Started thread number" + i);
					logger.info("Started thread number" + i);
				}



			}
			logger.info("connected to metadata server and servers");
		}catch (Exception e) {
			e.printStackTrace();

		}
		String message2 = "";
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader2 = new BufferedReader(input);
		System.out.println("Please enter operations  -1. CREATE 2. READ 3.APPEND 4.HBSERVER-STOP 5.HBSERVER-RESUME 6.EXIT");

		try {// continuously check for inputs
			logger.info("Listening to console");

			while (exit==false&&(message2 = reader2.readLine()) != null) {
// create file
				if (message2.equals("1")) {
					logger.info(message2);
					logger.info("create command to metadata server");
					System.out.println("Give <filename> and press enter");
					logger.info(	"Give <filename> and press enter");
					message2=reader2.readLine();
					Message m = new Message(client_no,MessageType.CREATE,message2);
					oos[0].writeObject(m);
					System.out.println("create command sent to MS");
					logger.info(	"create command sent to MS");
					//DataOutputStream 	out    = new DataOutputStream(s[0].getOutputStream());
					//out.writeUTF("CREATE" + "," +message2+","+ client_no);
				}


//read file
				if (message2.equals("2")) {

					logger.info("read command to metadata server");
					System.out.println("Give <filename> and press enter");
					logger.info(	"Give <filename> and press enter");
					String fileName = reader2.readLine();
					System.out.println("Give <offset> and press enter");
					logger.info(	"Give <offset> and press enter");
					String offset = reader2.readLine();
					int offset_num = Integer.parseInt(offset);
					//					pw5.write("READ" + "," + node_no + "," + fileName);
					Message m = new Message(client_no,MessageType.READ,fileName,offset_num);
					oos[0].writeObject(m);
					System.out.println("create command sent to MS");
					logger.info(	"create command sent to MS");


				}

//append toa file
				if (message2.equals("3")) {
					logger.info("append command to metadata server");
					System.out.println("Give <filename> and press enter");
					logger.info(	"Give <filename> and press enter");
					String fileName = reader2.readLine();
					//					pw5.write("READ" + "," + node_no + "," + fileName);

					Message m = new Message(client_no,MessageType.APPEND,fileName);
					oos[0].writeObject(m);
					System.out.println("create command sent to MS");
					logger.info(	"create command sent to MS");
				}
			//stop sending heartbeat msgs	
				if (message2.equals("4")) {
					logger.info("give server_no");
					System.out.println("give server_no");
					
					String server = reader2.readLine();
					//					pw5.write("READ" + "," + node_no + "," + fileName);
                     int serverno =Integer.parseInt(server);
					Message m = new Message(client_no,MessageType.HEARTBEAT,"STOP");
					oos[serverno].writeObject(m);
					System.out.println(" command sent to server to stop");
					logger.info(	"command sent to server to stop");
				}
				if (message2.equals("5")) {
					logger.info("give server_no");
					System.out.println("give server_no");
					
					String server = reader2.readLine();
					//					pw5.write("READ" + "," + node_no + "," + fileName);
                     int serverno =Integer.parseInt(server);
					Message m = new Message(client_no,MessageType.HEARTBEAT,"START");
					oos[serverno].writeObject(m);
					System.out.println(" command sent to server to start");
					logger.info(	"command sent to server to start");
				}
//exit from the program
				if (message2.equals("6")) {
					exit = true;
					logger.info("bye to everyone!");
					System.out.println("bye to everyone!");
				} 

			}
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
			logger.info(e.toString());
		}
	}
	//  threads handling each client
	class ChannelHandler implements Runnable {
		DataInputStream datainput;
		DataOutputStream dataoutput;
		ObjectOutputStream oostream;
		ObjectInputStream oistream;
		Socket socket;

		public ChannelHandler(Socket s) {
			try {
				// initializing data i/p and o/p streams
				socket =s ;
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		public void run() {
			try {
				System.out.println("Inside run");
				//oostream = new ObjectOutputStream(socket.getOutputStream());
				oistream = new ObjectInputStream(socket.getInputStream());
				System.out.print("after socket input  initialization" + socket);
				logger.info("after socket input  initialization" + socket);
				
				String message = "";
				logger.info("Inside run before while");
				while(!message.equals("Over"))
				{
					//logger.info("inside while");
				
					{
						logger.info("data available");
// metaserver to client for read request
						Message object = (Message) oistream.readObject();
						String messagetype =  object.getMsgtype().toString();
						if(messagetype.equals("READRESPONSE"))
						{
							//this.senderID = senderUID;
							//this.msgtype = Msgtype;
							//this.fileName = FileName;
							//this.server= server;
							//this.chunkname = chunkNameatServer;
							//this.chunkoffset = chunkoffset;

							String filename = object.getFileName();
							int server = object.getServer();
							String chunkname = object.getChunkname();
							int chunkoffset = object.getChunkoffset();
							// after retrieving the contents of the msg, send READFILE  to the corresponding server
							System.out.println("sent read request to server from client --- "+filename+" server id : "+server+
									" chunkoffset "+chunkoffset);

							Message m = new Message (client_no,MessageType.READFILE,chunkname,chunkoffset);
							oos[server].writeObject(m);
							logger.info("sent read request to server "+server);
							System.out.println("sent read request to server "+server);


						}// server to client after read file request
						if(messagetype.equals("READFILERESPONSE"))
						{

                              // name weird but its ok!
							String READRESPONSE = object.getReadcharacters();
							// print the output
							logger.info("reading contents of server "+READRESPONSE);
							System.out.println("reading contents of server "+READRESPONSE);


						}
						// in 2 phase commit server sent agreed
						if(messagetype.equals("AGREED"))
						{

                              
							int server = object.getSenderID();
							responsetobeReceivedcount--;
							hmap_2phase.put(server, 1);
							logger.info("server"+server+"agreed");
							System.out.println("server"+server+"agreed");


						}
						//server sent abort
						if(messagetype.equals("ABORT"))
						{

                              
                           int server = object.getSenderID();
                           responsetobeReceivedcount--;
							hmap_2phase.put(server, 0);
							logger.info("server"+server+"aborted");
							System.out.println("server"+server+"aborted");


						}
						// server sent appendresponse
						if(messagetype.equals("APPENDRESPONSE"))
						{
							//this.senderID = senderUID;
							//  this.msgtype = Msgtype;
							// this.fileName = FileName;
							// this.server1= Integer.parseInt(server1);
							// this.server2= Integer.parseInt(server2);
							// this.server3= Integer.parseInt(server3);
							//this.chunkname1= chunkname1;
							// this.chunkname2= chunkname2;
							//this.chunkname3= chunkname3; 
							// this.chunkoffset = chunkoffset; 
							

							//boolean result =	 initiate2Phase.initiate(object,ois,oos,client_no);
							//==================================================
							
							System.out.println("append received at client-- ");
							String filename = object.getFileName();
							int server1 = object.getServer1();
							int server2 = object.getServer2();
							int server3 = object.getServer3();
							
							String chunkname1 = object.getChunkname1();
							String chunkname2 = object.getChunkname2();
							String chunkname3 = object.getChunkname3();
							int chunkoffset = object.getChunkoffset();
							 String result="";

							boolean exit =false;
							

							// in 2 phase commit, the coordinator(client) first issues commit request to cohorts
							//then all the cohorts send agreed message, the coordinatorand cohorts are in wait in the meantime
							//once coordinator receives all agreed messages, it sends commit message after cohorts receive commit message , they commit

							
								System.out.println("====TWO PHASE INITIATION==="+server1+" "+server2+" "+server3);
								//this.senderID = senderUID;
								//this.msgtype = Msgtype;
								//this.fileName = chunkname;
								//this.chunkoffset=chunkoffset;  // no need of this, set to default value
								//this.sizeofappend= sizeofappend;
								// write code to generate a string <=1024
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
								result = buffer.toString();
								int lengthofresult = result.length();
								// create message objects and write to streams
							try { // send commit requests to servers
								Message m1 = new Message(client_no,MessageType.COMMITREQUEST,chunkname1,chunkoffset,lengthofresult);
								hmap_2phase.put(server1,0);
								
								oos[server1].writeObject(m1);
								Message m2 = new Message(client_no,MessageType.COMMITREQUEST,chunkname2,chunkoffset,lengthofresult);
								
								hmap_2phase.put(server2,0);
								oos[server2].writeObject(m2);
								if(server3!=0)
								{
								Message m3 = new Message(client_no,MessageType.COMMITREQUEST,chunkname3,chunkoffset,lengthofresult);
								
								hmap_2phase.put(server3,0);
								oos[server3].writeObject(m3);
								responsetobeReceivedcount=3;
								}
								else
								{
								responsetobeReceivedcount=2;
								}


								System.out.println("Sent Commmit requests to servers");
							} catch (UnknownHostException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							//waiting for sometime
									try {
										System.out.println("Sleeping");
										Thread.sleep(3000);
									} catch (Exception e) {
										System.out.println(e);
									}
									System.out.println("Waiting for responses");
									
									while(!exit)
									{
										if(responsetobeReceivedcount==0)
										{

										exit=true;
										// checking the contents of the hash map
										int abortflag=0;//checking if any server sent abort
										Iterator it = hmap_2phase.entrySet().iterator();
									    while (it.hasNext()) {
									        Map.Entry pair = (Map.Entry)it.next();
									        System.out.println("hashmap containing results contents:<server>:<response>"+pair.getKey() + " = " + pair.getValue());
									        
									       if(Integer.parseInt(pair.getValue().toString())==0)
									       {
									    	   abortflag=1;
									       }
									      
									            }
									    hmap_2phase.clear();
									    // if all servers sent agreed
										if(abortflag==0)
										{
											try {
												
											Message m1 = new Message(client_no,MessageType.COMMIT,chunkname1,chunkoffset,result);
											oos[server1].writeObject(m1);
											Message m2 = new Message(client_no,MessageType.COMMIT,chunkname2,chunkoffset,result);
											oos[server2].writeObject(m2);
											if(server3!=0)
											{
											Message m3 = new Message(client_no,MessageType.COMMIT,chunkname3,chunkoffset,result);
											oos[server3].writeObject(m3);
											}
											System.out.println("Successfully committed to all replicas");
											logger.info("2 phase commit implemented successfully");
											System.out.println("2 phase commit implemented successfully");	
											Message mserver = new Message(client_no,MessageType.APPENDCOMPLETE,filename) ;
											oos[0].writeObject(mserver);

											logger.info("sent append complete to metadataserver");
											System.out.println("sent append complete to metadataserver");	
											}
											catch (Exception e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											
								          }	
										
										else
										{
											
												logger.info("2 phase commit cannot be completed");
												System.out.println("2 phase commit cannot be completed");
												Message mserver = new Message(client_no,MessageType.APPENDNOTCOMPLETE,filename) ;
												oos[0].writeObject(mserver);
												logger.info("sent append incomplete to metadataserver");
											
										}
									}
						       }
							//=====================================================
							


						}// metadata server to client giving details of servers
						if(messagetype.equals("CREATERESPONSE"))
						{
							//this.senderID = senderUID;
							//this.msgtype = Msgtype;
							//this.fileName = FileName;
							//this.server1= Integer.parseInt(server1);
							//this.server2= Integer.parseInt(server2);
							//this.server3= Integer.parseInt(server3);
							//this.chunkname1= chunkname1;
							//this.chunkname2= chunkname2;
							//this.chunkname3= chunkname3; 
							
							String filename = object.getFileName();
							int server1=object.getServer1();
							int server2=object.getServer2();
							int server3=object.getServer3();
							String filename1 = object.getChunkname1();
							String filename2 = object.getChunkname2();
							String filename3 = object.getChunkname3();
							logger.info("file"+" "+filename+"created at servers"+server1+","+server2+","+server3+".");
							System.out.println("file"+" "+filename+"created at servers"+server1+","+server2+","+server3+".");
							logger.info("filenames are "+" "+filename1+","+server1+","+filename2+","+filename3+"respectively.");
							System.out.println("filenames are "+" "+filename1+","+server1+","+filename2+","+filename3+"respectively.");


						}


					}
				}

			}
			catch (Exception ex) {
				ex.printStackTrace();
			}

		}
	}

	/* client number to be given as argument while running */
	public static void main(String[] args) throws IOException {
		new Client(args);


	}

}
