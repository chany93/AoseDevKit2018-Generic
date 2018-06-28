package unitn.adk2018.generic.message;

import unitn.adk2018.Environment;
import unitn.adk2018.Logger;
import unitn.adk2018.intention.Intention;
import unitn.adk2018.pddl.PddlAction;

public class PddlAction_intention extends Intention<PddlAction_msg> {
	
	@Override
	public Next step0(IntentionInput in) {
//	if (agent.debugOn)
//		System.out.println(agent.getName() + " PddlAction_intention: processing action " + in.event.action);
		PddlAction action = Environment.getPddlDomain().generatePddlAction ( in.event.action );
		//System.out.println("Action_intention stepo0 :   " + agent.getBeliefs().pddlClauses() + "   "  + in.event.args);
		if ( action.checkPreconditions(agent.getBeliefs(), in.event.args) )
			return waitFor(this::stepAfterTimer, 2000); //continue after 3 seconds
		else
			return waitFor(null, 1000); //fail
	}
	
	public Next stepAfterTimer(IntentionInput in) {
		PddlAction action = Environment.getPddlDomain().generatePddlAction ( in.event.action );
		 //System.out.println("Action_intentiom step1 :   " + agent.getBeliefs().pddlClauses() + "   "  + in.event.args);
		if ( action.checkPreconditionsAndApply ( agent.getBeliefs(), in.event.args ) ) {
			if (agent.debugOn)
				Logger.println( this, "Beliefs changed: " + agent.getBeliefs().pddlClauses() );
			return null; //success
		}
		else
			return waitFor(null, 0); //fail
	}
	
	@Override
	public void pass(Intention<PddlAction_msg>.IntentionInput in) {
	}
	
	@Override
	public void fail(Intention<PddlAction_msg>.IntentionInput in) {
	}

	public String toString() {
		return super.toString() + "(" +  event.action + ")";
	}
}
