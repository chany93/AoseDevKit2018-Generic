package unitn.adk2018.generic.goal;

import unitn.adk2018.event.Goal;
import unitn.adk2018.pddl.PddlStep;

public class PddlStep_goal extends Goal {
	
	public final PddlStep step;
	
	public PddlStep_goal ( PddlStep _step ) {
		step = _step;
	}
	
	public String toString() {
		String text = "";
		for(String a : step.getArgs())
			text = text + ", " + a;
		return super.toString() + ": " +  step.getAction() + text;
	}
}
