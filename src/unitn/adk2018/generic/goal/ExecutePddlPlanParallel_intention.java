package unitn.adk2018.generic.goal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import unitn.adk2018.condition.TrueCondition;
import unitn.adk2018.event.Event;
import unitn.adk2018.intention.Intention;
import unitn.adk2018.pddl.PddlStep;

public class ExecutePddlPlanParallel_intention extends Intention<ExecutePddlPlan_goal> {
	
	Iterator<PddlStep> it;
	Set<Event> currentStepEvents = new HashSet<Event>();
	PddlStep nextPddlStep;
	
	
	
	@Override
	public Next step0(IntentionInput in) {
		it = in.event.plan.getSteps().iterator();
		return waitFor(this::waitForAllStepTerminated, 0);
	}
	
	public Next waitForAllStepTerminated(IntentionInput in) {
		
		for ( Event e : currentStepEvents ) {
			if ( e.wasHandledWithFailure().isTrue() ) {
				return waitFor(null, 0); //do fail
			}
			if ( e.wasNotHandled().isTrue() ) {
				return waitUntil ( this::waitForAllStepTerminated, e.wasHandled() ); //do wait for the step
			}
		}
		// if all steps have been handled
		currentStepEvents.clear();
		
		if ( !it.hasNext() && nextPddlStep == null ) {
			return null; //success
		}
		
		return waitFor(this::executeNextSteps, 0); //continue
	}
	
	public Next executeNextSteps(IntentionInput in) {
		
		PddlStep pddlStep;
		while ( it.hasNext() || nextPddlStep != null ) {
			
			// take next step
			if ( nextPddlStep != null ) {
				pddlStep = nextPddlStep;
				nextPddlStep = null;
			}
			else {
				pddlStep = it.next();
			}
			
			//submit goal
			if ( currentStepEvents.size()==0 || pddlStep.isParallelizableWithPrevious() ) {
				PddlStep_goal stepGoal = new PddlStep_goal ( pddlStep );
				currentStepEvents.add(stepGoal);
				agent.pushGoal( stepGoal, new TrueCondition() );
			}
			else {
				nextPddlStep = pddlStep;
				break;
			}
		}
		
		return waitFor(this::waitForAllStepTerminated, 0); //step goal submitted, now wait for them to terminate
	}
	
}