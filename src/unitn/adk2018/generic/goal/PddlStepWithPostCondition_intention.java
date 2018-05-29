package unitn.adk2018.generic.goal;

import unitn.adk2018.Agent;
import unitn.adk2018.Environment;
import unitn.adk2018.generic.message.PddlAction_msg;
import unitn.adk2018.intention.Intention;
import unitn.adk2018.pddl.PddlAction;

public class PddlStepWithPostCondition_intention extends Intention<PddlStep_goal> {
	
	@Override
	public boolean context(Agent a, PddlStep_goal g) {
		return true;
	}
	
	
	
	PddlAction action = null;
	PddlAction_msg msg = null;
	
	
	
	@Override
	public Next step0(IntentionInput in) {
		
		// get Java implementation of the Pddl action from the domain configured in the Environment
		action = Environment.getPddlDomain().generatePddlAction ( event.step.action );
		
		// fail immediately if I believe that it is not possible to perform this action given my own beliefs
		if( !action.checkPreconditions( agent.getBeliefs(), event.step.args ) ) {
			return waitFor(null, 0); // do fail
		}
		
		// execute the action on the environment by sending a PddlAction_msg to the env agent
		msg = new PddlAction_msg ( agent.getName(), Environment.getEnvironmentAgent().getName(),
				event.step.action, event.step.args );
		sendMessage( msg );
		return waitUntil( this::stepEnd, msg.wasHandled() ); //wait until action is done on environment then continue
	}
	
	
	
	public Next stepEnd(IntentionInput in) {
		
		// if PddlAction_msg handled
		if ( msg.wasHandledWithSuccess().isTrue() ) {
			
			// check preconditions of the action and apply effects
			if ( action.checkPreconditionsAndApply ( agent.getBeliefs(), event.step.args ) )
				return null; // success
		}
		
		// if PddlAction_msg not handled or it is not possible to apply effect on myself something has gone wrong
		return waitFor(null, 0); // do fail
	}
	
}
