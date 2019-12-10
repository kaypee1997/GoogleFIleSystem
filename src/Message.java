import java.io.Serializable;

public class Message  implements Serializable{
// describes the messages exchanged between various processes
	private static final long serialVersionUID = 1L;
	private int senderID;
	private int offset;
	private int index;
	private String fileName;
	private MessageType msgtype;
	private int server;
	private int server1;
	private int server2;
	private int server3;
	private int chunkoffset;
	private int chunkoffset1;
	private int chunkoffset2;
	private int chunkoffset3;
	private String chunkname;// READRESPONSE chunk name at server  sent by metadata server to client
	private String chunkname1;// chunk name at server in first position in the list sent by metadata server to client(APPENDRESPONSE)
	private String chunkname2;// chunk name at server in second position in the list sent by metadata server to client(APPENDRESPONSE)
	private String chunkname3;// chunk name at server in third position in the list sent by metadata server to client(APPENDRESPONSE)
	private int sizeofappend;
	private String appendString;
	private String readcharacters;

	public Message() {
		
	}


	// READ CLIENT -> MS AND 
	// READFILE CLIENT  TO SERVER 
	public Message(int senderUID, MessageType Msgtype, String FileName, int offset) {
		this.senderID = senderUID;
		this.msgtype = Msgtype;
		this.fileName = FileName;
		this.offset = offset;
	}

	//  CREATE,   CLIENT -> MS 
	//CREATECOMMAND  MS -> SERVER ,
	//APPEND  CLIENT -> MS, 
	// AGREED FROM S->C, 
	// ABORT FROM S->C 
	//APPEND COMPLETE C->MS
	//APPEND INCOMPLETE C->MS
	
	// filename is the name decided by the metadata AND can be used for readfile response
	public Message(int senderUID, MessageType Msgtype, String FileName) {
		this.senderID = senderUID;
		this.msgtype = Msgtype;
		this.fileName = FileName;


	}
	//READFILE RESPONSE S->S , S->C
	public Message(int senderUID, MessageType Msgtype, String FileName,String filescharacters) {
		this.senderID = senderUID;
		this.msgtype = Msgtype;
		this.fileName = FileName;
		this.readcharacters = filescharacters;


	}
	public String getReadcharacters() {
		return readcharacters;
	}
	
// characters sent during read operation
	public void setReadcharacters(String readcharacters) {
		this.readcharacters = readcharacters;
	}

	//  READRESPONSE MS->C 
	public Message(int senderUID, MessageType Msgtype,int server, String FileName,String chunkNameatServer, int chunkoffset ) {
		this.senderID = senderUID;
		this.msgtype = Msgtype;
		this.fileName = FileName;
		this.server= server;
		this.chunkname = chunkNameatServer;
		this.chunkoffset = chunkoffset;
	}
	//APPENDRESPONSE from MS->C 
	public Message(int senderUID, MessageType Msgtype,int server1,int server2,int server3,String chunkname1,String chunkname2, String chunkname3, String FileName, int chunkoffset ) {
		this.senderID = senderUID;
		this.msgtype = Msgtype;
		this.fileName = FileName;
		this.server1= server1;
		this.server2= server2;
		this.server3= server3;
		this.chunkname1= chunkname1;
		this.chunkname2= chunkname2;
		this.chunkname3= chunkname3; 
		this.chunkoffset = chunkoffset;
	}
	// CREATERESPONSE MS->C 
	public Message(int senderUID, MessageType Msgtype,int server1,int server2,int server3,String chunkname1,String chunkname2, String chunkname3, String FileName) {
		this.senderID = senderUID;
		this.msgtype = Msgtype;
		this.fileName = FileName;
		this.server1= server1;
		this.server2= server2;
		this.server3= server3;
		this.chunkname1= chunkname1;
		this.chunkname2= chunkname2;
		this.chunkname3= chunkname3; 

	}
	//UPDATE REPLICA MS->S 
	public Message(int senderUID, MessageType Msgtype,int server1,String chunkname1,  String FileName) {
		this.senderID = senderUID;
		this.msgtype = Msgtype;
		this.fileName = FileName;
		this.server1= server1;
		this.chunkname1= chunkname1;

	}




	// COMMIT REQUEST FROM C->S
	public Message(int senderUID, MessageType Msgtype, String chunkname,int chunkoffset, int sizeofappend) {
		this.senderID = senderUID;
		this.msgtype = Msgtype;
		this.fileName = chunkname;
		this.chunkoffset=chunkoffset;  // no need of this, set to default value
		this.sizeofappend= sizeofappend;

	}

	// COMMIT FROM C->S
	public Message(int senderUID, MessageType Msgtype, String chunkname,int chunkoffset, String appendString) {
		this.senderID = senderUID;
		this.msgtype = Msgtype;
		this.fileName = chunkname;
		this.chunkoffset=chunkoffset;// no need of this, set to default value
		this.appendString= appendString;

	}

	public String getAppendString() {
		return appendString;
	}
	public void setAppendString(String appendString) {
		this.appendString = appendString;
	}
	public int getSizeofappend() {
		return sizeofappend;
	}
	public void setSizeofappend(int sizeofappend) {
		this.sizeofappend = sizeofappend;
	}
	public int getServer() {
		return server;
	}
	public void setServer(int server) {
		this.server = server;
	}
	public int getServer1() {
		return server1;
	}
	public void setServer1(int server1) {
		this.server1 = server1;
	}
	public int getServer2() {
		return server2;
	}
	public void setServer2(int server2) {
		this.server2 = server2;
	}
	public int getServer3() {
		return server3;
	}
	public void setServer3(int server3) {
		this.server3 = server3;
	}
	public int getChunkoffset() {
		return chunkoffset;
	}
	public void setChunkoffset(int chunkoffset) {
		this.chunkoffset = chunkoffset;
	}
	public int getChunkoffset1() {
		return chunkoffset1;
	}
	public void setChunkoffset1(int chunkoffset1) {
		this.chunkoffset1 = chunkoffset1;
	}
	public int getChunkoffset2() {
		return chunkoffset2;
	}
	public void setChunkoffset2(int chunkoffset2) {
		this.chunkoffset2 = chunkoffset2;
	}
	public int getChunkoffset3() {
		return chunkoffset3;
	}
	public void setChunkoffset3(int chunkoffset3) {
		this.chunkoffset3 = chunkoffset3;
	}
	public String getChunkname() {
		return chunkname;
	}
	public void setChunkname(String chunkname) {
		this.chunkname = chunkname;
	}
	public String getChunkname1() {
		return chunkname1;
	}
	public void setChunkname1(String chunkname1) {
		this.chunkname1 = chunkname1;
	}
	public String getChunkname2() {
		return chunkname2;
	}
	public void setChunkname2(String chunkname2) {
		this.chunkname2 = chunkname2;
	}
	public String getChunkname3() {
		return chunkname3;
	}
	public void setChunkname3(String chunkname3) {
		this.chunkname3 = chunkname3;
	}
	public void setSenderID(int senderID) {
		this.senderID = senderID;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setMsgtype(MessageType msgtype) {
		this.msgtype = msgtype;
	}


	public int getSenderID() {
		return senderID;
	}

	public int getOffset() {
		return offset;
	}

	public int getIndex() {
		return index;
	}

	public String getFileName() {
		return fileName;
	}

	public MessageType getMsgtype() {
		return msgtype;
	}
}
