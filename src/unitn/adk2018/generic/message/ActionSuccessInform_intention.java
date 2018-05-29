package unitn.adk2018.generic.message;

import unitn.adk2018.Environment;
import unitn.adk2018.intention.Intention;
import unitn.adk2018.pddl.PddlAction;

public class ActionSuccessInform_intention extends Intention<ActionSuccessInform_msg> {
	
	
	
	PddlAction action = null;
	Sensing_msg sensing = null;
	
	
	
	@Override
	public Next step0(IntentionInput in) {
		
		// get Java implementation of the Pddl action from the domain configured in the Environment
		action = Environment.getPddlDomain().generatePddlAction ( event.action );
		
		// apply or try to do sensing
		if ( action.checkPreconditionsAndApply( agent.getBeliefs(), event.args ) )
			return null; //success
		else {
			// do sensing
			sensing = new Sensing_msg( agent.getName(), Environment.getEnvironmentAgent().getName() );
			Environment.sendMessage( sensing );
			return waitUntil( this::step1, sensing.wasHandled() ); //when sensing handled
		}
	}
	
	
	
	public Next step1(IntentionInput in) {
		if ( sensing.wasHandledWithSuccess().isTrue() )
			return null; //success
		else
			return waitFor(null, 0); //fail
	}
	
	
	
	public String toString() {
		return super.toString() + "(" +  event.action + ")";
	}
	
}
