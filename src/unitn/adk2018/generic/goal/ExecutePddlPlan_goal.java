package unitn.adk2018.generic.goal;

import unitn.adk2018.event.Goal;
import unitn.adk2018.pddl.PddlPlan;


public class ExecutePddlPlan_goal extends Goal {

	public final PddlPlan plan;
	
	public ExecutePddlPlan_goal (PddlPlan _plan) {
		plan = _plan;
	}
	
}
