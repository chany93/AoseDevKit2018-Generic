package unitn.adk2018.generic.goal;

import unitn.adk2018.Agent;
import unitn.adk2018.Environment;
import unitn.adk2018.event.RequestMessage;
import unitn.adk2018.generic.message.PddlAction_msg;
import unitn.adk2018.intention.Intention;

public class PddlStep_intention extends Intention<PddlStep_goal> {
	
	@Override
	public boolean context(Agent a, PddlStep_goal g) {
		return true;
	}
	
	RequestMessage msg = null;
	
	@Override
	public Next step0(IntentionInput in) {
		msg = new PddlAction_msg ( agent.getName(), Environment.getEnvironmentAgentName(),
				in.event.action, in.event.args );
		sendMessage( msg );
		return waitUntil( this::stepEnd, msg.wasHandled() ); //wait until action is done on environment then continue
	}
	
	public Next stepEnd(IntentionInput in) {
		if ( msg.wasHandledWithSuccess().isTrue() )
			return null; //success
		else
			return waitFor(null, 0); //fail
	}
	
}
