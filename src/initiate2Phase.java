import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class initiate2Phase {


	public static boolean initiate(Message object, ObjectInputStream ois[], ObjectOutputStream oos[],int client_num)
	{
		String filename = object.getFileName();
		int server1 = object.getServer1();
		int server2 = object.getServer2();
		int server3 = object.getServer3();
		String chunkname1 = object.getChunkname1();
		String chunkname2 = object.getChunkname2();
		String chunkname3 = object.getChunkname3();
		int chunkoffset = object.getChunkoffset();
		 String result;

		boolean exit =false;
		boolean responseReceived = false;
		boolean goahead = false;
		boolean returnval =false;

		// in 2 phase commit, the coordinator(client) first issues commit request to cohorts
		//then all the cohorts send agreed message, the coordinatorand cohorts are in wait in the meantime
		//once coordinator receives all agreed messages, it sends commit message after cohorts receive commit message , they commit

		
			System.out.println("====TWO PHASE INITIATION===");
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
		try {
			Message m1 = new Message(client_num,MessageType.COMMITREQUEST,chunkname1,chunkoffset,lengthofresult);
			oos[server1].writeObject(m1);
			Message m2 = new Message(client_num,MessageType.COMMITREQUEST,chunkname2,chunkoffset,lengthofresult);
			oos[server2].writeObject(m2);
			Message m3 = new Message(client_num,MessageType.COMMITREQUEST,chunkname3,chunkoffset,lengthofresult);
			oos[server3].writeObject(m3);


			System.out.println("Sent Commmit requests to servers");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		while(!exit)
		{



			while(!responseReceived)
			{
				try {
					System.out.println("Sleeping");
					Thread.sleep(1000);
				} catch (Exception e) {
					System.out.println(e);
				}
				System.out.println("Waiting for responses");
				
					Message object1 = null;
					Message object2 = null;
					Message object3 = null;
					try {
						 object1 = (Message) ois[server1].readObject();
						 object2 = (Message) ois[server2].readObject();
						 object3 = (Message) ois[server3].readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					



					if(object1.getMsgtype().equals("AGREED")&& object2.getMsgtype().equals("AGREED")&&object3.getMsgtype().equals("AGREED"))
					{
						responseReceived=true;
						goahead=true;
						System.out.println("All servers agreed!");

					}
					else
					{
						responseReceived=true;
						goahead=false;
						System.out.println("One/more than one servers sent abort");

					}


					if(goahead)
					{

						//this.senderID = senderUID;
						//this.msgtype = Msgtype;
						//this.fileName = chunkname;
						//this.chunkoffset=chunkoffset;  // no need of this, set to default value
						//this.sizeofappend= sizeofappend;
						try {
						Message m1 = new Message(client_num,MessageType.COMMIT,chunkname1,chunkoffset,result);
						oos[server1].writeObject(m1);
						Message m2 = new Message(client_num,MessageType.COMMIT,chunkname2,chunkoffset,result);
						oos[server2].writeObject(m2);
						Message m3 = new Message(client_num,MessageType.COMMIT,chunkname3,chunkoffset,result);
						oos[server3].writeObject(m3);
						System.out.println("Successfully committed to all replicas");
						returnval=true;
						exit=true;
						}
						catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}

					else
					{
						returnval=false;
						exit=true;
					}

				



			}	
		}
		
		return returnval;
	}
}
