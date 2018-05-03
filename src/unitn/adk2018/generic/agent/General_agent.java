package unitn.adk2018.generic.agent;


import java.util.List;

import unitn.adk2018.intention.Intention;
import unitn.adk2018.intention.ScheduledIntention;
import unitn.adk2018.Agent;
import unitn.adk2018.Logger;
import unitn.adk2018.MaintenanceCondition;
import unitn.adk2018.condition.TrueCondition;
import unitn.adk2018.event.Event;
import unitn.adk2018.generic.goal.Postman_goal;

public class General_agent extends Agent {
	
	public General_agent(String _name, boolean debugOn) {
		super(_name, debugOn);
	}
	
	@Override
	public void run () {
		
		pushGoal ( new Postman_goal(), new TrueCondition() );
		
		if (debugOn)  Logger.println( this, "thread started!");
		while ( true ) {
			/*
			 * A FSM to execute everything
			 */
			try {
				ScheduledIntention ss = waitingIntention.take();
				ss.execute(); /// If the intention is not completed, it has to reschedule itself
			} catch (Exception e) {
				System.err.println( getName() + " thread: " + e);
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Simplest intention selection algorithm:
	 * take the only one available and hope it will work!
	 */
	protected <E extends Event> boolean doBDImetaReasoning ( E event, MaintenanceCondition _asLongAs ) {
		
		/*
		 * select the only intention available
		 */
		Intention<E> intention = null;
		try {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			List<Class<? extends Intention<E>>> list = (List) eventDictionary.get ( event.getClass() );
			for (Class<? extends Intention<E>> intentionClass : list) {
				intention = intentionClass.newInstance();
				if (intention.context(this, event)) {
					intention.agent = this;
					intention.event = event;
					break;
				}
			}
		} catch (Exception e) {
			
		}
		if (intention==null) {
			System.err.println ( getName() + " ERROR"
					+ " Impossible to handle event " + event.getClass().getSimpleName()
					+ " Known events are: " + eventDictionary.keySet()
				);
			return false;
		}
		
		/*
		 * try it
		 */
		try {
			ScheduledIntention si = new ScheduledIntention ( this, event, intention, _asLongAs);
			event.status.syncWith( si.status );
			return this.rescheduleIntention ( si );
		} catch (ClassCastException e) {
			System.err.println ( getName() + " Agent.doBDImetaReasoning(): ERROR Invalid handler for event " + event);
		}
		
		return false;
	}
	
}
