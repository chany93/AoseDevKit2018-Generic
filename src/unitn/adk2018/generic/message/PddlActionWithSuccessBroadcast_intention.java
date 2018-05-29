package unitn.adk2018.generic.message;

import unitn.adk2018.Agent;
import unitn.adk2018.Environment;
import unitn.adk2018.Logger;
import unitn.adk2018.event.Message;
import unitn.adk2018.intention.Intention;
import unitn.adk2018.pddl.PddlAction;

public class PddlActionWithSuccessBroadcast_intention extends Intention<PddlAction_msg> {
	
	PddlAction action = null;
	
	@Override
	public Next step0(IntentionInput in) {
		
		// get Java implementation of the Pddl action from the domain configured in the Environment
		action = Environment.getPddlDomain().generatePddlAction ( in.event.action );
		

		// wait before apply or fail if it is not possible to perform this action
		if ( action.checkPreconditions(agent.getBeliefs(), in.event.args) )
			return waitFor(this::stepAfterTimer, 3000); //continue after 3 seconds
		else
			return waitFor(null, 1000); //fail
	}
	
	public Next stepAfterTimer(IntentionInput in) {

		// check preconditions again and apply effects otherwise fail
		if ( action.checkPreconditionsAndApply ( agent.getBeliefs(), in.event.args ) ) {
			
			if (agent.debugOn)
				Logger.println( this, "Beliefs changed: " + agent.getBeliefs().pddlClauses() );
			
			// broadcast success to everyone
			for ( Agent to : Environment.getAgents().values() ) {

				// skip me (environment agent) and the acting agent
				if ( to == agent || to.getName().equals(event.getFrom()) )
					continue;
				
				// send ActionSuccessInform_msg
				Message reply = new ActionSuccessInform_msg( agent.getName(), to.getName(), event.action, event.args);
				Environment.sendMessage ( reply );
				Logger.println( this, "broadcasting success to " + to.getName() );
			}
			
			return null; //success
		}
		else
			return waitFor(null, 0); //fail
	}
	
	public String toString() {
		return super.toString() + "(" +  event.action + ")";
	}
}
