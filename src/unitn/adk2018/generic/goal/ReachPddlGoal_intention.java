package unitn.adk2018.generic.goal;

import unitn.adk2018.Environment;
import unitn.adk2018.generic.message.Sensing_msg;
import unitn.adk2018.intention.Intention;
import unitn.adk2018.pddl.PddlClause;
import unitn.adk2018.pddl.PddlPlan;
import unitn.adk2018.utils.BlackboxUtils;

public class ReachPddlGoal_intention extends Intention<ReachPddlGoal_goal> {
	
	@Override
	public Next step0(Intention<ReachPddlGoal_goal>.IntentionInput in) {
		/*
		 * Do sensing
		 */
		Sensing_msg sensing = new Sensing_msg( agent.getName(), Environment.getEnvironmentAgent().getName() );
		Environment.sendMessage( sensing );
		return waitUntil( this::step1, sensing.wasHandled() );
	}
	
	ExecutePddlPlan_goal g;
	
	public Next step1(Intention<ReachPddlGoal_goal>.IntentionInput in) {
		
		String pddlDomainFile = Environment.getPddlDomain().domainFile;
		PddlClause[] goal = in.event.pddlGoal;
		
		/*
		 * Generate Pddl Goal
		 */
		String pddlGoal = "( and "; 
		for(PddlClause c : goal) {
			pddlGoal += " (" + c + ")";
		}
		pddlGoal += " )";
		
		/*
		 * Generate Pddl plan
		 */
		PddlPlan plan = BlackboxUtils.doPlan(agent, pddlDomainFile, agent.getBeliefs(), pddlGoal);
		
		if (agent.debugOn)
			System.out.println( agent.getName() + " planned? " + (plan!=null) );
		if (plan==null)
			return waitFor ( null, 0 );
			//System.exit(1);
		
		/*
		 * Execute generated plan
		 */
		g = new ExecutePddlPlan_goal ( plan );
		agent.pushGoal ( g, in.event.wasNotHandled() );  
		        /// note that the PDDL plan will fail if its goal is retracted (possibly forcely because of a meta-decision)
//		GoalMsg_msg msg = new GoalMsg_msg( agent.getName(), Environment.getEnvironmentAgentName(), g );
//		Environment.getEnvironment().sendMessage( msg );
		return waitUntil ( this::step2, g.wasHandled() );
	}
	
	public Next step2(Intention<ReachPddlGoal_goal>.IntentionInput in) {
		if (g.wasHandledWithSuccess().isTrue()){
			return null; // success
		}
		else
			return waitFor(this::step0, 0); // fail
	}


	@Override
	public void pass(IntentionInput in) {
	}

	@Override
	public void fail(IntentionInput in) {
	}
}
