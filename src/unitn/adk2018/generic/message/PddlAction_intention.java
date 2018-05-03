package unitn.adk2018.generic.message;

import unitn.adk2018.Environment;
import unitn.adk2018.intention.Intention;
import unitn.adk2018.pddl.PddlAction;

public class PddlAction_intention extends Intention<PddlAction_msg> {
	
	@Override
	public Next step0(IntentionInput in) {
//		if (agent.debugOn)
//			System.out.println(agent.getName() + " PddlAction_intention: processing action " + in.event.action);
		PddlAction action = Environment.getPddlDomain().generatePddlAction ( in.event.action );
		if ( action.checkPreconditions(agent.getBeliefs(), in.event.args) )
			return waitFor(this::stepAfterTimer, 2000); //continue after 2 seconds
		else
			return waitFor(null, 500); //fail
	}
	
	public Next stepAfterTimer(IntentionInput in) {
		PddlAction action = Environment.getPddlDomain().generatePddlAction ( in.event.action );
		action.checkPreconditionsAndApply ( agent.getBeliefs(), in.event.args );
		return null; //success
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
