package unitn.adk2018.generic.goal;

import java.util.Iterator;

import unitn.adk2018.condition.TrueCondition;
import unitn.adk2018.event.Event;
import unitn.adk2018.intention.Intention;
import unitn.adk2018.pddl.PddlStep;

public class ExecutePddlPlan_intention extends Intention<ExecutePddlPlan_goal> {
	
	Iterator<PddlStep> it;
	PddlStep pddlStep;
	/*
	 * TODO gestire non uno ma più messaggi inviati in parallelo nei casi di "isParallelizableWithPrevious"
	 * Solo al completamento aggiornare i beliefs!
	 */
	Event currentActionEvent;

	@Override
	public Next step0(IntentionInput in) {
		it = in.event.plan.getSteps().iterator();
		return waitFor(this::step1, 0);
	}
	
	public Next step1(IntentionInput in) {

		if(!it.hasNext()) {
			if(currentActionEvent==null) {
				return waitFor(this::endLoop, 0);
			}
			else {
				if (currentActionEvent.wasHandledWithFailure().isTrue())
					return waitFor(null, 0); // failure
				return waitUntil ( this::endLoop, currentActionEvent.wasHandled() );
			}
		}
		
		pddlStep = it.next();
		
		if(currentActionEvent==null) {
			return waitFor(this::submitGoal, 0);
		}
//		else if(pddlStep.isParallelizableWithPrevious()) {
//			return waitFor(this::submitGoal, 0);
//		}
		else {
			if (currentActionEvent.wasHandledWithFailure().isTrue())
				return waitFor(null, 0); // failure
			return waitUntil ( this::submitGoal, currentActionEvent.wasHandled() );
		}
	}
	
	public Next submitGoal(IntentionInput in) {
		currentActionEvent = new PddlStep_goal ( pddlStep );
		agent.pushGoal( currentActionEvent, new TrueCondition() );
		return waitFor(this::step1, 0); //continue
	}
	
	public Next endLoop(IntentionInput in) {
//		if (agent.debugOn)
//			System.out.println( agent.getName()
//					+ " plan execution " + (planResult?"COMPLETED":"NOT COMPLETED") + "."
//					+ " Actions performed are: " + msg.getCompleted().size()
//				);
		if (currentActionEvent.wasHandledWithFailure().isTrue())
			return waitFor(null, 0); // failure
		return null;
	}
	
	@Override
	public void pass(IntentionInput in) {
	}
	
	@Override
	public void fail(IntentionInput in) {
	}
	
}