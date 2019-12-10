import java.io.Serializable;

public class heartbeat implements Serializable{

	
	//name of linux file ,version_num, offset, chunk index, filename setting in heartbeat
	
	    private int senderID;
	    private int chunkindex;
	    private int lastoffset;
	    private boolean full;
	    public boolean isFull() {
			return full;
		}
	    
		public void setFull(boolean full) {
			this.full = full;
		}
		public boolean getFull(){
			return this.full;
		}
		
		public int getLastoffset() {
			return lastoffset;
		}
		public void setLastoffset(int lastoffset) {
			this.lastoffset = lastoffset;
		}
		public heartbeat() {
			super();
		}

		private int offset;
	    private int version_num;
	    private String fileName;
	    private String linuxFileName;
	    public int getSenderID() {
			return senderID;
		}
		public void setSenderID(int senderID) {
			this.senderID = senderID;
		}
		public int getChunkindex() {
			return chunkindex;
		}
		public void setChunkindex(int chunkindex) {
			this.chunkindex = chunkindex;
		}
		public int getOffset() {
			return offset;
		}
		public void setOffset(int offset) {
			this.offset = offset;
		}
		public int getVersion_num() {
			return version_num;
		}
		public void setVersion_num(int version_num) {
			this.version_num = version_num;
		}
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public String getLinuxFileName() {
			return linuxFileName;
		}
		public void setLinuxFileName(String linuxFileName) {
			this.linuxFileName = linuxFileName;
		}
		public String getStringRepresentation() {
			return this.senderID+" "+this.fileName+" "+this.linuxFileName+" "+this.version_num+" "+this.offset+" "+this.chunkindex;
		}
		
		 public heartbeat( int senderID,   String fileName,  String linuxFileName,  int chunkindex,
		  int offset, int version_num, int lastoffset)
		 {
			    this.senderID = senderID;
		        this.fileName = fileName;
		        this.linuxFileName = linuxFileName;
		        this.chunkindex = chunkindex;
		        this.offset = offset; 
		        this.version_num = version_num; 
		        this.lastoffset=lastoffset;
		 }
		   
}
