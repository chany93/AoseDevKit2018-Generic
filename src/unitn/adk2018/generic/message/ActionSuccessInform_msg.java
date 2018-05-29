package unitn.adk2018.generic.message;

import unitn.adk2018.event.InformMessage;
import unitn.adk2018.pddl.PddlAction;
import unitn.adk2018.pddl.PddlClause;

public final class ActionSuccessInform_msg extends InformMessage {
	
	public final String action;
	public final String[] args;
	
	public ActionSuccessInform_msg(String _from, String _to, String _action, String[] _args) {
		super(_from, _to);
		action = _action;
		args = _args;
	}
	
	public String toString() {
		return super.toString() + "[" + action + "]";
	}
}
