package unitn.adk2018.generic.message;

import unitn.adk2018.event.InformMessage;
import unitn.adk2018.pddl.PddlClause;

public final class Clause_msg extends InformMessage {
	
	public final PddlClause clause;
	
	public Clause_msg(String _from, String _to, PddlClause _clause) {
		super(_from, _to);
		clause = _clause;
	}
	
	public PddlClause getClause() {
		return clause;
	}
	
}
