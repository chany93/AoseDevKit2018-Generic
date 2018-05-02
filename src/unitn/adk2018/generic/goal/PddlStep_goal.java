package unitn.adk2018.generic.goal;

import unitn.adk2018.event.Goal;

public class PddlStep_goal extends Goal {
	
	public final String action;
	public final String[] args;
	
	public PddlStep_goal ( String _action, String[] _args ) {
		action = _action;
		args = _args;
	}

	public String toString() {
		String text = "";
		for(String a : args)
			text = text + ", " + a;
		return super.toString() + ": " +  action + text;
	}
}
