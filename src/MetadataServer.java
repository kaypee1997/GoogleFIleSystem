//package testing;
import javax.sound.midi.Soundbank;
import java.io.*;
        import java.net.*;
        import java.util.*;
        import java.util.logging.FileHandler;
        import java.util.logging.Logger;
        import java.util.logging.SimpleFormatter;
        import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class MetadataServer {

    static ObjectOutputStream dos[] = new ObjectOutputStream[7];
    static ObjectInputStream dis[] = new ObjectInputStream[7];
    static ServerSocket ss[] = new ServerSocket[7];
    static Socket s[] = new Socket[7];
    int file_server_no = 0;
    static Thread[] t;
    static int completioncounter = 0;
    static boolean stopflag = false;
    Logger logger;
    BlockingQueue<Object> msgQueue;

    private Map<String, List<String>> linuxfileToChunks;
    private Map<String,  List<String>> chunkToReplicas;
    private Map<String,  Integer> replicaToServer;
    private Map<String, String> replicaToChunk;
    private Map<String, Integer> chunkVersion;
    private Map<String, Integer> replicaVersion;
    private Map<String, Long> replicaSize;
    private Map<String ,Long> chunkSize;
    private Map<String, Integer> replicaOffset;
    private Map<Integer, Boolean> serversConnected;
    private Map<Integer, Long> lastHeartbeat;
    private Map<String, Boolean> chunkIsFull;
    private Map<String, Integer> fileNameMapping;
    static  Map<Integer,Socket> connections;


    public MetadataServer(String args[]) throws IOException {
        msgQueue = new LinkedBlockingQueue<Object>();
        linuxfileToChunks =Collections.synchronizedMap(new HashMap<>());
        replicaToServer = Collections.synchronizedMap(new HashMap<>());
        chunkVersion = Collections.synchronizedMap(new HashMap<>());
        chunkToReplicas = Collections.synchronizedMap(new HashMap<>());
        replicaToChunk = Collections.synchronizedMap(new HashMap<>());
        replicaVersion = Collections.synchronizedMap(new HashMap<>());
        replicaSize = Collections.synchronizedMap(new HashMap<>());
        replicaOffset = Collections.synchronizedMap(new HashMap<>());
        ChannelHandler ch;
        serversConnected = Collections.synchronizedMap(new HashMap<>());
        lastHeartbeat = Collections.synchronizedMap(new HashMap<>());
        chunkSize = Collections.synchronizedMap(new HashMap<>());
        fileNameMapping = Collections.synchronizedMap(new HashMap<>());
        connections = Collections.synchronizedMap(new HashMap<>());
        chunkIsFull = Collections.synchronizedMap(new HashMap<>());
        
        serversConnected.put(1,true);
        serversConnected.put(2,true);
        serversConnected.put(3,true);
        serversConnected.put(4,true);
        serversConnected.put(5,true);




        ConfigProperties prop = new ConfigProperties();
        int metadataServeraddressforserver1 =  Integer.parseInt(prop.getPropValues("metadataServeraddressforserver1"));

        try {
            //logger for file server
            File logDir = new File("./logs/");
            if (!(logDir.exists()))
                logDir.mkdir();
            logger = Logger.getLogger("MetadataServer.log");

            FileHandler fh = new FileHandler("logs/MetadataServer.log");
            fh.setFormatter(new SimpleFormatter());
            // fh.setLevel(logLevel);
            logger.addHandler(fh);
            logger.info("MetadataServer  initialization");


            for (int i = 0; i < 7; i++)
            {
                ss[i] = new ServerSocket(metadataServeraddressforserver1 + i);
                s[i] = ss[i].accept();
                
                
                ch = new ChannelHandler(s[i]);
                dos[i] = ch.getOutputStreamObject();
                dis[i] = ch.getInputStreamObject();
                Thread t = new Thread(ch);
                t.start();
                System.out.print("Starting thread number" + i);
                logger.info("Starting thread number" + i);
                connections.put(i+1,s[i]);

            }
            // accept connections from clients
        }
        catch (IOException e) {
            e.printStackTrace();
        }



        //reading from queue for new messages
        while(true){
        	
        	

            for(int serverID : serversConnected.keySet()){
                if (lastHeartbeat.containsKey(serverID) && ((int) ((int) (System.currentTimeMillis() / 1000) - lastHeartbeat.get(serverID)) >=15))
                  {
                	int cal = (int) ((int) (System.currentTimeMillis() / 1000) - lastHeartbeat.get(serverID)); // >= 15;
                	serversConnected.put(serverID, false);
                }
            }
            
            

            
            if(msgQueue.size() !=0){
                Random rand = new Random();
                //temp hasmap will be replaced with servers connected will store NodeID and true false
                Map<Integer,Boolean> temp = new HashMap<>();


                Object msgObj = msgQueue.peek();

                if(msgObj instanceof heartbeatMessage){
                    heartbeatMessage msg = (heartbeatMessage) msgObj;
                    

                    // processing of the heartbeat message
                    if(msg.getMsgtype() == MessageType.HEARTBEAT){

                        
                        heartbeatMessage heartbeatmsg = (heartbeatMessage) msg;
                        //System.out.println("heartBeat message received from  server id :"+ msg.getSenderID());
                        int serverID = msg.getSenderID();
                        //System.out.println("serverId "+serverID);
                        //int size = 0;
                        lastHeartbeat.put(serverID,System.currentTimeMillis() / 1000);
                        //todo data update as per sending
                        String linuxFileName;
                        String fileName, chunkName1; //not required
                        int senderID, chunkIndex, versionNo;
                        long offSet, oldOffSet;

                        heartbeat[] heartbeats = heartbeatmsg.getHeartBeats();
                        for(int i=0;i<heartbeats.length;i++){
                            
                        	//abc.txt
                        	fileName = heartbeats[i].getFileName()+".txt";
                            
                        	//abc_1_0_2.txt
                        	linuxFileName = heartbeats[i].getLinuxFileName();
                        	//2
                        	
                        	senderID = Integer.parseInt(linuxFileName.split("_")[3].split("\\.")[0]);
                        	//System.out.println("replica Name "+linuxFileName+" server ID "+senderID);
                        	
                        	versionNo = Integer.parseInt(linuxFileName.split("_")[2]);
                        	
                        	String[] chunkSplit= linuxFileName.split("_");
                        	
                        	//abc_1
                        	chunkName1 = chunkSplit[0]+"_"+chunkSplit[1];
                            
                            //todo check how server is naming chunk index
                        	//1
                            chunkIndex = heartbeats[i].getChunkindex();
                            
                            offSet = heartbeats[i].getLastoffset();
                            chunkSize.putIfAbsent(chunkName1, offSet);
                            oldOffSet = chunkSize.get(chunkName1);
                            if(oldOffSet < offSet) {
                            	chunkSize.put(chunkName1,offSet);
                            }
                            
                            linuxfileToChunks.putIfAbsent(fileName, new ArrayList<String>());
                            if(!linuxfileToChunks.get(fileName).contains(chunkName1)) {
                            	linuxfileToChunks.get(fileName).add(chunkName1);
                            }
                            //linuxfileToChunks.put(fileName, linuxfileToChunks.get(fileName).add(chunkName));
                            
                            List<String> chunks = linuxfileToChunks.get(fileName);
                            //System.out.println("chunk");
                            
                            
                            
                            
                            for(String chunk:chunks) {
                            	chunkIsFull.putIfAbsent(chunkName1, false);
                            	
                            	if(heartbeats[i].getFull() && chunk.equals(chunkName1)) {
                            		System.out.println("chunkFull" +chunkName1);
                            		//replicaIsFull create a new chunk for the file
                            		
                            		if(!chunkIsFull.get(chunkName1)) {
                            		chunkIsFull.put(chunk, true);
                            		List<Integer> serversSelected = randomServers();
                            		for(int randomServers : serversSelected) {
            						    /*it will call the function to send message to create chunk to the selected server where i
            						    is ID of server
                                        */
                                        if(serversConnected.get(randomServers)) {
                                        	//chunkName +"_"+ "0"+ "_" + i +"."+tempLinuxFileName[1];
                                            String[] chunkNames = chunkName1.split("_");
                                            int chunkID = Integer.parseInt(chunkNames[1]);
                                            
                                            String newChunkName = chunkNames[0]+"_"+(chunkID+1);
                                            System.out.println("chunkID for new chunk"+chunkID);
                                            String NewReplicaName = newChunkName+"_"+"0"+"_"+randomServers+".txt";
                                            System.out.println("replica name for new created file"+NewReplicaName);
                                            sendMessageToServer(randomServers-1, MessageType.CREATE, newChunkName, fileName, NewReplicaName,  (long) 0);
                                        }
                                        else{
                                            System.out.println("server is dead, ID:" + randomServers);
                                        }

                                    }}
                            		
                            	}else {
                            		chunkIsFull.put(chunk, false);
                            	}
                            	
                            	
                            	chunkToReplicas.putIfAbsent(chunk, new ArrayList<String>());
                            	chunkVersion.putIfAbsent(chunk, versionNo);
                            	int chunkVersionTemp =chunkVersion.get(chunk);
                            	//System.out.println("storedChunkVersion --1"+chunkVersionTemp+" versio came from heartbeat--1"+versionNo);
                             	
                            	if(chunkVersionTemp < versionNo) {
                            		chunkVersion.put(chunk, versionNo);
                            	}
                            	
                            	// this code is for when file name is stored in the mserver is the same that came in the heartbeat but the server was dead!
                            	// check for the replica to update
                            	if(chunkToReplicas.get(chunk).contains(linuxFileName) && !serversConnected.get(serverID)) {
                            		
                            		System.out.println( "server"+ serversConnected.get(serverID)+" "+serverID+" "+serversConnected.get(1)+""+
                            				serversConnected.get(2)+" "+serversConnected.get(3)+" "+serversConnected.get(4)+" "+serversConnected.get(5)+" ");
                            		List<String> replicas = Collections.synchronizedList(new ArrayList<String>(chunkToReplicas.get(chunk)));
                            		 for(String replica : replicas) {
                            			 int storedChunkVersion=chunkVersion.get(chunk);
                                         int updateServerID = 0;
                                         String latestReplica= null;
                                         Message message = new Message();
                                         
                                         /* getiing all the replicas form the file name got in the heartbeat and then compate the server id whichever replica 
                                          * have the same serverID and process that replica only!
                                          */
                                         for(String replicaTemp : replicas) {
                                        	 
                                         	if(storedChunkVersion > versionNo) {
                            				// System.out.println("storedChunkVersion "+storedChunkVersion+" versio came from heartbeat"+versionNo);
                                        			if(!replicaToServer.isEmpty()) {
                                        				updateServerID = replicaToServer.get(replicaTemp);
                                        				latestReplica = replica;
                                        			}//todo check get the replica here and compare theat with the replica in next step!
                                        		
                                        	 }
                                        	 /*latestReplica = replicaTemp;
                                        	 updateServerID = replicaToServer.get(replicaTemp);*/
                                        	 System.out.println(" outdated replica senedID"+senderID+"latest server "+updateServerID
                            						 +" latest chunk "+latestReplica+" file Name "+replica);
                                        	 
                                         }
                                         System.out.println(" ======= "+replicaToServer.get(replica)+" "+serverID
                        						 +"  "+replica+" "+linuxFileName +" "+!serversConnected.get(serverID)+" "+senderID);
                                         
                            			 if(replicaToServer.get(replica)==serverID && !latestReplica.equals(linuxFileName)
                            					 && !serversConnected.get(serverID) && senderID==serverID) {
                            				 
                            				 	System.out.println(" outdated replica --*###&*&*&*"+senderID+"latest server## "+updateServerID
                            						 +" latest chunk "+latestReplica+" ReplicaName at deadserver Name *&*&*&*------"+linuxFileName);
	                            				 message.setSenderID(0);
	                            				 message.setMsgtype(MessageType.UPDATEREPLICA);
	                            				 message.setServer1(updateServerID);
	                            				 message.setChunkname1(latestReplica);
	                            				 message.setFileName(linuxFileName);
	                            		         dos[serverID-1].writeObject(message);
                            				 	
                            			 }
                            		 }
                               		
                               	}
                            	
                            	//this code handles the case where file has be updated and that means name has bean changed!
                               	
                            	if(!chunkToReplicas.get(chunk).contains(linuxFileName)) {
                                	//chunkToReplicas.get(chunk).add(linuxFileName);
                                	
                                	List<String> replicas = Collections.synchronizedList(new ArrayList<String>(chunkToReplicas.get(chunk)));
                                	int tempVersion = 0;
                                    int storedChunkVersion=chunkVersion.get(chunk);
                                    int updateServerID = 0;
                                    Message message;
                                    
                                    for(String replica : replicas) {
                                    	
                                    	replicaVersion.putIfAbsent(replica, versionNo);
                                    	replicaToServer.putIfAbsent(replica, senderID);
                                    	//System.out.println(" server mepped to the replica "+replicaToServer.get(replica));
                                       	
                                    	if(senderID == replicaToServer.get(replica)){
                                             //when servers was dead and got connected again
                                         	serversConnected.putIfAbsent(senderID, true);
                                         	String newReplica = linuxFileName;                                         
                                         	//System.out.println("heartbeat meassage and replica stored "+replica+ " new replica "+newReplica);
                           
                                        	if(!replica.equals(newReplica)) {
	                                         	if(chunkToReplicas.get(chunk).contains(replica)) {
	                                         		//System.out.println("heartbeat meassage and the replica name is being changes-------- ");
	                                         		chunkToReplicas.get(chunk).remove(replica);
	                                         		chunkToReplicas.get(chunk).add(newReplica);
	                                         	}
	                                         	if(replicaToServer.containsKey(replica)) {
	                                         		replicaToServer.remove(replica);
	                                         		replicaToServer.put(newReplica, senderID);                                       
	                                         	}
	                                         	replicaToChunk.putIfAbsent(newReplica, chunk);
	                                         	if(replicaToChunk.containsKey(replica)) {
	                                         		replicaToChunk.remove(replica);
	                                         		replicaToChunk.put(newReplica, chunk);
	                                         	}
	                                         	if(replicaVersion.containsKey(replica)) {
	                                         		replicaVersion.remove(replica);
	                                         		replicaVersion.put(newReplica, versionNo);
	                                         	}
	                                         	if(chunkVersion.containsKey(replica)) {
	                                         		chunkVersion.remove(replica);
	                                         		chunkVersion.put(newReplica, versionNo);
	                                         	}
                                         	}
                                    	 
                                    	 }
                                    	
                                    }
	
                                }
                            }
                            
                        }
                       
                        if(!serversConnected.get(serverID)) {
                        	System.out.println("false");
                        }
                        serversConnected.put(serverID,true);
                    }

                    msgQueue.remove();

                }
                
                
                
                
                else if(msgObj instanceof Message) {
                    Message msg = (Message) msgObj;
                    msgQueue.remove();
                    System.out.println("message received at metadataServer"+msg);

                    if(msg.getMsgtype() == MessageType.CREATE){
                        System.out.println("Create message received from  Client id :"+ msg.getSenderID());
                        // 3 server selected to create a linux file with with chunk 1
                        List<Integer> serversSelected = randomServers();
                        serversSelected.forEach(i -> System.out.println("seleted server for create request"+i));

                        String linuxFilename = msg.getFileName();
                        System.out.println("file name"+msg.getFileName());
                        String[] tempLinuxFileName = linuxFilename.split("\\.");

                        String chunkName;
                        int tempVal;

                        if(fileNameMapping.containsKey(linuxFilename)){
                            tempVal= fileNameMapping.get(linuxFilename);
                            chunkName = tempLinuxFileName[0]+"_"+(tempVal++);
                            fileNameMapping.put(linuxFilename,tempVal);
                        }else {
                            tempVal = 1;
                            chunkName = tempLinuxFileName[0]+"_"+(tempVal++);
                            fileNameMapping.put(linuxFilename,tempVal);
                            System.out.println("chunk name for new created file"+chunkName);
                            linuxfileToChunks.put(linuxFilename,new ArrayList<String>());
                        }

                        //below code will be used for future purpose.
                        linuxfileToChunks.get(linuxFilename).add(chunkName);
                        
                        
                        
                        for(int i : serversSelected) {
						    /*it will call the function to send message to create chunk to the selected server where i
						    is ID of server
                            */
                            if(serversConnected.get(i)) {
                                String replicaName = chunkName +"_"+ "0"+ "_" + i +"."+tempLinuxFileName[1];
                                System.out.println("replica name for new created file"+replicaName);
                                sendMessageToServer(i-1, MessageType.CREATE, chunkName, linuxFilename, replicaName,  (long) 0);
                            }
                            else{
                                System.out.println("server is dead, ID:" + i);
                            }


                        }

                    }


                    if(msg.getMsgtype() == MessageType.APPEND){
                        
                        String linuxFileName = msg.getFileName();
                        List<String> chunksNames = linuxfileToChunks.get(linuxFileName);
                        String lastChunk = chunksNames.get(chunksNames.size()-1);
                        List<String> replicasName = chunkToReplicas.get(lastChunk);
                        List<Integer> replicasToServers = new ArrayList<>();
                        List<String> replicas = new ArrayList<>();
                        int clientId = msg.getSenderID();
                       /* System.out.println("Append message received from  Client id :"+ msg.getSenderID()+" linuxfileName: "+
                        		linuxFileName+" chunkName "+linuxfileToChunks.get(linuxFileName).get(0)+" replicaName "+
                        		chunkToReplicas.get(lastChunk).get(0));
                        */



                        
                        for(String replica : replicasName){
                            int serverID = replicaToServer.get(replica);
                            if(serversConnected.get(serverID)) {
                                replicasToServers.add(serverID);
                                replicas.add(replica);

                            }

                        }
                        Message message;
                        replicasToServers.forEach(i-> System.out.println("replicas to servers" + i));
                        //replicas.forEach(i-> System.out.println("replicas list:  "+i));
                        if(replicasToServers.size()<3) {
                        	System.out.println("one of the servers is dead!"+replicasToServers.get(0)+" "+replicasToServers.get(1));
                        	   message = new Message(0, MessageType.APPENDRESPONSE,
                              		replicasToServers.get(0),replicasToServers.get(1),0,
                                      replicas.get(0),replicas.get(1),null,linuxFileName);
                        	   dos[clientId-1].writeObject(message);	   
                        }
                        else {
                        	message = new Message(0, MessageType.APPENDRESPONSE,
                        		replicasToServers.get(0),replicasToServers.get(1),replicasToServers.get(2),
                                replicas.get(0),replicas.get(1),replicas.get(2),linuxFileName);
                        
                        System.out.println("sending message to do the append to client id: "+clientId);
                        dos[clientId-1].writeObject(message);
                        }

                    }


                    else if(msg.getMsgtype() == MessageType.READ){
                        System.out.println("Read message received from  Client id :"+ msg.getSenderID());
                        String linuxFileName = msg.getFileName();
                        int offset = msg.getOffset();
                        System.out.println(" offset : "+offset);
                        //check with KD
                        List<String> chunks = linuxfileToChunks.get(linuxFileName);
                        System.out.println("linux file name "+linuxFileName);
                        chunks.forEach(i-> System.out.println("chunk names : "+i));
                        Long size = (long)0;
                        String replicaToRead;
                        int serverID;
                        Message messageTemp;
                        int counter=0;

                        for(String chunk : chunks){
                        	//change this code.
                            size = size+chunkSize.get(chunk);
                            System.out.println("chunk size updated : "+size);
                            if(offset< size){
                            	counter++;
                                //return that file
                            	replicaToRead = chunkToReplicas.get(chunk).get(0);
                                System.out.println("replica name for the read ----1: "+replicaToRead);
                      
                                serverID = replicaToServer.get(replicaToRead);
                                if(!serversConnected.get(serverID)) {
                                	replicaToRead = chunkToReplicas.get(chunk).get(1);
                                	serverID = replicaToServer.get(replicaToRead);
                                }
                                System.out.println("server id that is being used+ "+serverID);
           
                                messageTemp = new Message(0,MessageType.READRESPONSE,serverID,linuxFileName,replicaToRead ,offset);
                                
                                dos[msg.getSenderID()-1].writeObject(messageTemp);
                                //sendMessageToServer(msg.getSenderID(),MessageType.READRESPONSE,chunk,linuxFileName,replicaToRead,
                                  //      (long)0);

                                break;
                            }
                            if(counter == 0) {
                            	replicaToRead = chunkToReplicas.get(chunk).get(0);
                            	System.out.println("replica name for the read -----2: "+replicaToRead);
                                
                                serverID = replicaToServer.get(replicaToRead);
                                if(!serversConnected.get(serverID)) {
                                	replicaToRead = chunkToReplicas.get(chunk).get(1);
                                	serverID = replicaToServer.get(replicaToRead);
                                }
                            	
                            	
                            	if(!serversConnected.get(serverID)) {
                                	replicaToRead = chunkToReplicas.get(chunk).get(1);
                                	serverID = replicaToServer.get(replicaToRead);
                                }
                            	replicaToRead = chunkToReplicas.get(chunk).get(1);
                                System.out.println("replica name for the read : "+replicaToRead);
                                serverID = replicaToServer.get(replicaToRead);
                                System.out.println("server id that is being used+ "+serverID);
           
                                messageTemp = new Message(0,MessageType.READRESPONSE,serverID,linuxFileName,replicaToRead ,offset);
                                
                                dos[msg.getSenderID()-1].writeObject(messageTemp);
                                //sendMessageToServer(msg.getSenderID(),MessageType.READRESPONSE,chunk,linuxFileName,replicaToRead,
                                  //      (long)0);

                                break;
                            	
                            }
                        }
                        
                    }
            
                }



            }
        }

    }



    class ChannelHandler implements Runnable {
        ObjectInputStream datainput;
        ObjectOutputStream dataoutput;

        Socket socket;

        public ChannelHandler(Socket s) {
            try {
                // initializing data i/p and o/p streams
                datainput = new ObjectInputStream(s.getInputStream());
                dataoutput = new ObjectOutputStream(s.getOutputStream());
                dataoutput.flush();
                dataoutput.reset();
                
        

                System.out.print("after socket initialization" + datainput + " " + dataoutput + " " + s);
                logger.info("After socket initialization" + datainput + " " + dataoutput + " " + s);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public ObjectOutputStream getOutputStreamObject() {
        	return dataoutput;
        }
        
        public ObjectInputStream getInputStreamObject() {
        	return datainput;
        }
        
        public void run() {
            try {
            	Thread.sleep(3000);

                while(true) {
                	
                    if (true)
                    {
                    	//System.out.println("message received --1 ");
                        Object msg = datainput.readObject();
                        //System.out.println("message received --2"+msg);
                        dataoutput.reset();

                        msgQueue.add(msg);


                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

        }


    }

    private void sendMessageToServer(int serverID, MessageType type, String chunkName, String linuxFileName ,
                                     String replicaName, Long size){


        try {
            System.out.println("sending message to server to create file ---- Create/Append/Read file step 1");
            Socket socket = connections.get(serverID);
            //ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
            if(type == MessageType.CREATE){
                linuxfileToChunks.putIfAbsent(linuxFileName, new ArrayList<>());
                updateMetaData(chunkName, serverID, linuxFileName, replicaName , size);
                type = MessageType.CREATECOMMAND;
            }


            Message message = new Message();
            message.setFileName(linuxFileName);
            message.setChunkname(replicaName);
            message.setMsgtype(type);
            message.setSenderID(1);
            message.setServer(serverID);
            dos[serverID].writeObject(message);
            dos[serverID].reset();
            
            //os.close();
            //socket.close();
            
            //dos[serverID].writeObject(message);

                
            System.out.println("sending message to server to create file ---- Create/Append/Read file step 2");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private void updateMetaData(String chunkName, int serverID ,String linuxFileName , String replicaName , Long dataSize){
        // update the meta data information

        /* already mapped the linuxfile name with chunk at sendMessageToServer, now first mapping the replica to chuck
            then replica to serverID  and then replica version.
        */
        chunkToReplicas.putIfAbsent(chunkName , new ArrayList<>());
        chunkToReplicas.get(chunkName).add(replicaName);
        replicaToServer.putIfAbsent(replicaName, serverID+1);
        replicaToChunk.putIfAbsent(replicaName, chunkName);
        chunkVersion.put(chunkName,0);
        replicaVersion.putIfAbsent(replicaName,0);
        replicaSize.put(replicaName, dataSize);
        System.out.println("update Information after creation replica Name--- "+replicaName+" servver ID: "+serverID);

    }

    private List<Integer> randomServers() {
        List<Integer> list = new ArrayList<>();

        // add 5 server IDS in ArrayList
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);

        Random randomNumber = new Random();
        int temp = 2;
        int rand;
        while (temp>0){
            rand = randomNumber.nextInt(list.size());
            list.remove(rand);
            temp--;
        }

        return  list;
    }

    /* server number to be given as argument while running */
    public static void main(String[] args) {
        try {
            new MetadataServer(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
