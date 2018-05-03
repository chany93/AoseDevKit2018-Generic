package unitn.adk2018.generic.message;

import unitn.adk2018.event.Goal;
import unitn.adk2018.event.RequestMessage;

public final class Request_msg extends RequestMessage {

	public final Goal goal;
	
	public Request_msg(String _from, String _to, Goal _goal) {
		super(_from, _to);
		goal = _goal;
	}
	
	public String toString() {
		return super.toString() + ": " + goal;
	}
	
}
