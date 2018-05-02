package unitn.adk2018.generic.message;

import unitn.adk2018.intention.Intention;

public final class Request_intention extends Intention<Request_msg> {
	
	@Override
	public Next step0(IntentionInput in) {
		agent.pushGoal ( in.event.goal, in.event.wasNotHandled() );//new TimeoutCondition(agent, 500) );
		return waitUntil( this::step1, in.event.goal.wasHandled() );
	}
	
	public Next step1(IntentionInput in) {
		if (in.event.goal.status.get().isHandledWithSuccess())
			return null; //success
		else
			return waitFor(null, 0); //failure
	}

	@Override
	public void pass(IntentionInput in) {
	}

	@Override
	public void fail(IntentionInput in) {
	}
	
	public String toString() {
		return super.toString() + ": " +  event.goal.getClass().getSimpleName();
	}
	
}
