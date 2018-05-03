package unitn.adk2018.generic.message;

import java.util.ArrayList;
import java.util.List;

import unitn.adk2018.Environment;
import unitn.adk2018.event.Message;
import unitn.adk2018.intention.Intention;
import unitn.adk2018.pddl.PddlClause;

public final class Sensing_intention extends Intention<Sensing_msg> {
	
	List<Message> messages = new ArrayList<Message>();
	
	@Override
	public Next step0(IntentionInput in) {
		for ( PddlClause c : agent.getBeliefs().getACopyOfDeclaredClauses().values() ) {
			Message m = new Clause_msg( agent.getName(), in.event.getFrom(), c );
			messages.add( m );
			Environment.sendMessage ( m );
		}
		
		return waitFor(this::step1, 0);
	}
	
	public Next step1(IntentionInput in) {
		for(Message m : messages) {
			if(m.wasNotHandled().isTrue())
				return waitUntil( this::step1, m.wasHandled() );
		}
		return null; //do success!
	}
	
}
