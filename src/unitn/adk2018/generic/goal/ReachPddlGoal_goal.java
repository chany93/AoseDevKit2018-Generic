package unitn.adk2018.generic.goal;

import unitn.adk2018.event.Goal;
import unitn.adk2018.pddl.PddlClause;


public class ReachPddlGoal_goal extends Goal {
	
	public final PddlClause[] pddlGoal;
	
	public ReachPddlGoal_goal (PddlClause[] _pddlGoal) {
		pddlGoal = _pddlGoal;
	}
	
	@Override
	public String toString() {
		String pddlGoalStr = super.toString() + " ( and "; 
		for(PddlClause c : pddlGoal) {
			pddlGoalStr += " (" + c + ")";
		}
		pddlGoalStr += " )";
		return pddlGoalStr;
	}
}
