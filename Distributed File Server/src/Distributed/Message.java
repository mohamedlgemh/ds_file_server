package Distributed;
import java.io.Serializable;

public class Message implements Comparable<Message>, Serializable{
	private static final long serialVersionUID = 1L;
	
	String Id, sender, req ,fn;
	int lClock;
	
	public Message(String Id, String sender, String req, int lClock,String fn) {
		this.Id = Id;
		this.sender = sender;
		this.req = req;
		this.lClock = lClock;
                this.fn = fn;
	}

	@Override
	public String toString() {
		return sender + ": " + req;
	}

	@Override
	public int compareTo(Message o) {
            //Tie breaker
		if(this.lClock == o.lClock)
                    
			return this.Id.compareTo(o.Id);
		return this.lClock - o.lClock;
	}

	
}
