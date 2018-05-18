package unitn.adk2018.generic.goal;

import unitn.adk2018.Logger;
import unitn.adk2018.MessageQueue;
import unitn.adk2018.condition.ChangedCondition;
import unitn.adk2018.condition.OrCondition;
import unitn.adk2018.condition.TrueCondition;
import unitn.adk2018.event.InformMessage;
import unitn.adk2018.event.Message;
import unitn.adk2018.event.RequestMessage;
import unitn.adk2018.intention.ElaborationStatus;
import unitn.adk2018.intention.Intention;

public class PostmanOneRequestAtTime_intention extends Intention<Postman_goal> {
	
	private Message request = null;
	
	@Override
	public Next step0(IntentionInput in) {
		if (agent.debugOn) Logger.println( this, "woke up" );
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
//			if (agent.debugOn) System.out.println( agent.getName() + " " + this + ": processing something: " + m );
			if (m instanceof InformMessage) {  /// process information before goal
				if (agent.debugOn) Logger.println( this, "processing InformMessage: " + m );
				agent.pushGoal ( m, new TrueCondition() );
			}
			else if (m instanceof RequestMessage) {
				if(lookForARequestMessageToProcess) {
//					if(request!=null) if (agent.debugOn)
//						Logger.println( this, "flushing older RequestMessage: " + request + " with " + m );
					request = m;   /// discard previous goal if null or terminated.
				}
				else {
					if (agent.debugOn)
						Logger.println( this, "Rejecting RequestMessage: " + m );
					m.status.set(ElaborationStatus.HANDLED_WITH_FAILURE);
				}
			}
			else
				if (agent.debugOn) Logger.println( this, "skipping unknown message" );
		}
		if (lookForARequestMessageToProcess && request!=null) {
			if (agent.debugOn) Logger.println( this, "processing RequestMessage: " + request );
			agent.pushGoal ( request, new TrueCondition() );
			return waitUntil( this::step0, new OrCondition( new ChangedCondition(mqueue), request.wasHandled() ) );
		}
		
		if ( request!=null && request.wasNotHandled().isTrue())
			return waitUntil( this::step0, new OrCondition( new ChangedCondition(mqueue), request.wasHandled() ) );
		return waitUntil( this::step0, new ChangedCondition(mqueue) );
	}
	
}
