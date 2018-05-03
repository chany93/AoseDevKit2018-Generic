package unitn.adk2018.generic.goal;

import unitn.adk2018.MessageQueue;
import unitn.adk2018.condition.ChangedCondition;
import unitn.adk2018.condition.OrCondition;
import unitn.adk2018.condition.TrueCondition;
import unitn.adk2018.event.InformMessage;
import unitn.adk2018.event.Message;
import unitn.adk2018.event.RequestMessage;
import unitn.adk2018.intention.Intention;

public class PostmanOneRequestAtTime_intention extends Intention<Postman_goal> {
	
	private Message request = null;
	
	@Override
	public Next step0(IntentionInput in) {
		if (agent.debugOn) System.out.println( agent.getName() + " " + this + ": woke up" );
		final MessageQueue mqueue = in.agent.mqueue;
		/*
		 * empty the queue before processing. Take only the latest goal - all meta reasoning not supported!
		 * Process all information before proceeding with goal management.
		 */
		boolean lookForARequestMessageToProcess = false;
		if(request==null || request.wasHandled().isTrue()) {
			request = null;
			lookForARequestMessageToProcess = true;
		}
		Message m = null;
		while ((m = mqueue.getIfAny()) != null) {
			if (agent.debugOn) System.out.println( agent.getName() + " " + this + ": processing something: " + m );
			if (m instanceof InformMessage) {  /// process information before goal
				if (agent.debugOn) System.out.println( agent.getName() + " " + this + ": processing InformMessage: " + m );
				agent.pushGoal ( m, new TrueCondition() );
			}
			if (m instanceof RequestMessage)
				if(lookForARequestMessageToProcess)
					request = m;   /// discard previous goal if null or terminated.
		}
		if (lookForARequestMessageToProcess && request!=null) {
			if (agent.debugOn) System.out.println( agent.getName() + " " + this + ": processing RequestMessage: " + request );
			agent.pushGoal ( request, new TrueCondition() );
			return waitUntil( this::step0, new OrCondition( new ChangedCondition(mqueue), request.wasHandled() ) );
		}
		
		if ( request!=null && request.wasNotHandled().isTrue())
			return waitUntil( this::step0, new OrCondition( new ChangedCondition(mqueue), request.wasHandled() ) );
		return waitUntil( this::step0, new ChangedCondition(mqueue) );
	}
	
}
