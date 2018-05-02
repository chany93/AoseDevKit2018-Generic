package unitn.adk2018.generic.goal;

import unitn.adk2018.Agent;
import unitn.adk2018.MessageQueue;
import unitn.adk2018.condition.ChangedCondition;
import unitn.adk2018.condition.TrueCondition;
import unitn.adk2018.event.InformMessage;
import unitn.adk2018.event.Message;
import unitn.adk2018.event.RequestMessage;
import unitn.adk2018.intention.Intention;
import unitn.adk2018.intention.Intention.IntentionInput;

public class Postman_intention extends Intention<Postman_goal> {
	
	private final MessageQueue mqueue;
	private Message request = null;
	
	public Postman_intention(MessageQueue _mqueue) {
		mqueue = _mqueue;
	}

	@Override
	public Next step0(IntentionInput in) {
		/*
		 * empty the queue before processing. Take only the latest goal - all meta reasoning not supported!
		 * Process all information before proceeding with goal management.
		 */
		Message m = null;
		while ((m = mqueue.getIfAny()) != null) {
			if (m instanceof InformMessage) {  /// process information before goal
				if (agent.debugOn) System.out.println( agent.getName() + " " + this + ": processing InformMessage: " + m );
				agent.pushGoal ( m, new TrueCondition() );
			}
			if (m instanceof RequestMessage)
				request = m;   /// discard previous goal if null or terminated.
		}
		if (request!=null) {
			if (agent.debugOn) System.out.println( agent.getName() + " " + this + ": processing RequestMessage: " + request );
			agent.pushGoal ( request, new TrueCondition() );
		}
		
		return waitUntil ( this::step0, new ChangedCondition(mqueue) );
	}
	
}
