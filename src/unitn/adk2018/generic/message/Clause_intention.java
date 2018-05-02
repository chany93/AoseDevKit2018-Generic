package unitn.adk2018.generic.message;

import unitn.adk2018.intention.Intention;

public final class Clause_intention extends Intention<Clause_msg> {
	
	@Override
	public Intention<Clause_msg>.Next step0(IntentionInput in) {
		agent.getBeliefs().declare(in.event.getClause());
		return null;
	}
	
	@Override
	public void pass(IntentionInput in) {
	}
	
	@Override
	public void fail(IntentionInput in) {
	}
	
}
