package unitn.adk2018.generic.message;

import unitn.adk2018.event.RequestMessage;


public class PddlAction_msg extends RequestMessage {

	public final String action;
	public final String[] args;
	
	public PddlAction_msg ( String _from, String _to, String _action, String[] _args ) {
		super(_from, _to);
		action = _action;
		args = _args;
	}
	
}
