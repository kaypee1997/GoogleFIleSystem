import java.io.Serializable;

public class heartbeatMessage extends Message implements Serializable
{
	 private heartbeat[] heartBeats;

	public heartbeat[] getHeartBeats() {
		return heartBeats;
	}

	public void setHeartBeats(heartbeat[] heartBeats) {
		this.heartBeats = heartBeats;
	}


	public heartbeatMessage(heartbeat[] hm)
	{
	this.heartBeats=hm;
	}
}
