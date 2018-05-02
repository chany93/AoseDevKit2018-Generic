package unitn.adk2018.generic.goal;

import unitn.adk2018.MessageQueue;
import unitn.adk2018.condition.ChangedCondition;
import unitn.adk2018.condition.TrueCondition;
import unitn.adk2018.event.Message;
import unitn.adk2018.intention.Intention;

public class PostmanEverythingInParallel_intention extends Intention<Postman_goal> {
	
	@Override
	public Next step0(IntentionInput in) {
		final MessageQueue mqueue = in.agent.mqueue;
		Message m = null;
		while ((m = mqueue.getIfAny()) != null) {
			if (agent.debugOn)
				System.out.println( agent.getName() + " " + this + ": processing: " + m );
			agent.pushGoal ( m, new TrueCondition() );
		}
		return waitUntil(this::step0, new ChangedCondition(mqueue));
	}
	
}
