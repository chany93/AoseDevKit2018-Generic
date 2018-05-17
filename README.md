# AoseDevKit2018

The AoseDevKit2018 framework has been implemented for the course of Agent Oriented Software Engineering at the University Of Trento (2018).
Introductory slides on the framework are available in the doc folder in the AoseDevKit2018-MultiAgentSystem repository.
The framework includes three repositories. In the case of bugs, let us know or fix them and do a pull request. Start exploring by looking at readme files of the projects in this order:
- *AoseDevKit2018-Blocksworld: https://github.com/marcorobol/AoseDevKit2018-Blocksworld*
- *AoseDevKit2018-Generic: https://github.com/marcorobol/AoseDevKit2018-Generic*
- *AoseDevKit2018-MultiAgentSystem: https://github.com/marcorobol/AoseDevKit2018-MultiAgentSystem*

# AoseDevKit2018-Generic

This project contains implementations of goals, messages, intentions, and a generic agent with a generic control loop for the AoseDevKit2018 framework.

Everything in this project has to stay domain-independent and should not modify the main logic of the core framework provided in the AoseDevKit2018-MultiAgentSystem project.

## Quick start guide to the use and development of goals, messages, and intentions with the AoseDevKit2018 framework

### Goals and messages

Goals and messages are both events that the agent can deal with by starting an intention, which is selected on the basis of the method doBDImetaReasoning specific for the type of agent. Events by themselves do not specify any implementation for their achievement. Here an example of goal without any parameter:
```java
public class Postman_goal extends Goal {
}
```
Eventual parameters can be included. For example:
```java
public class ReachPddlGoal_goal extends Goal {
	public final PddlClause[] pddlGoal;
	public ReachPddlGoal_goal (PddlClause[] _pddlGoal) {
		pddlGoal = _pddlGoal;
	}
}
```

All goals or messages should either extend unitn.adk2018.event.Goal or unitn.adk2018.event.RequestMessage or unitn.adk2018.event.InformMessage, which are all unitn.adk2018.event.Event according to this hierarchy:

- unitn.adk2018.event.Event
    - unitn.adk2018.event.Message
        - unitn.adk2018.event.RequestMessage
        - unitn.adk2018.event.InformMessage
    - unitn.adk2018.event.Goal

Differences between Goal, RequestMessage, and InformMessage are:
- Goals can only be submmitted internally by the agent to himself and are immediately handled by the doBDImetaReasoning method of the agent, which, in the implementation of unitn.adk2018.generic.agent.General_agent, immediately schedules an intention.
- Messages instead can be posted to agents from outside or by other agents. However, the message is not handled immediately, instead it is only putted in the message queue of the agent. Postman intention monitors this message queue and when something is posted, postamn intention is woke up and can finally handle the message by scheduling an intention.
- RequestMessages could be handled differently with respect to InformMessages, depending on the postman intention used by the agent. PostmanEverythingInParallel_intention is the simplest implementation of a Postman and does not do any discrimination between inform and request messages. PostmanOneRequestAtTime_intention is instead a little bit more complicated.

### Intentions

Intentions implement how to handle an event (achieve a goal or process a message).
Depending on the configuration of agents, given an event, zero or more intentions can be available to handle it.
The algorithm to select the correct intention given the goal must be implemented in the method doBDImetaReasoning of the Agent. An example is available in unitn.adk2018.generic.agent.General_agent, where the first applicable intention is selected (given the evaluation of the contex method).

All intentions extend unitn.adk2018.intention.Intention and specify the type of event handled.
For example PddlStep_intention handles the goal PddlStep_goal:
```java
public class PddlStep_intention extends Intention<PddlStep_goal> {
	@Override
	public boolean context(Agent a, PddlStep_goal g) {
		return true;
	}
	RequestMessage msg = null;
	@Override
	public Next step0(IntentionInput in) {
		msg = new PddlAction_msg ( agent.getName(), Environment.getEnvironmentAgent().getName(),
				in.event.action, in.event.args );
		sendMessage( msg );
		//wait for the action in the message to be handled by envAgent and then reschedule next step
		return waitUntil( this::stepEnd, msg.wasHandled() );
	}
	public Next stepEnd(IntentionInput in) {
		if ( msg.wasHandledWithSuccess().isTrue() )
			return null; //success
		else
			return waitFor(null, 0); //fail
	}
}
```

#### Steps

Each intention provides its implementation in separate steps (implemented as methods like this: Next step(IntentionInput in)) that should not perform blocking code. The first step called when the intention is scheduled is the step0. Other steps can be defined as in the example of above.

Each step must return a Next object which can be conveniently created with the useful shortcut methods offered by the intention class:
```java
public final Next waitFor(final Function<IntentionInput, Next> nextstep, final long waitingTime);
public final Next waitUntil(final Function<IntentionInput, Next> nextstep, final MaintenanceCondition maintenanceCondition);
```
- return something like this to wait for a specific amount of time
	```java
	return waitFor(this::nameOfStepToScheduleAfter500ms, 500); // to wait for 500 ms
	```
- to wait for some condition to became true use the classes available in the package unitn.adk2018.condition in the AoseDevKit2018-MultiAgentSystem project 
	```java
	return waitUntil(this::nameOfStepToScheduleAsSoonAsMsgIsHandled, msg.wasHandled());
	```
	
#### Push goals or sending messages

Within an intention it is possible to submit a goal to myself by doing the following. It is also possible to specify a maintenance condition different from the default TrueCondition, so to force the termination of the intention handling the goal in the case something happens. The same can be done with messages.
```java
agent.pushGoal ( goal, new TrueCondition() );
Environment.sendMessage( msg );
```
At this point it is possible to let running this intention in parallel with the submitted goal, by continuing immediately with another step:
```java
return waitFor( this::step1, 0 );
```
Or wait for the submitted goal to be achieved before proceeding, by doing this:
```java
return waitUntil( this::step1, goal.wasHandled() );
```

#### Failure or success

When no more steps are needed it is possible to make the intention ( and so the handled event/goal/message) succeed or fail.
```java
return null; //success
return waitFor(null, 0); //fail
```

### Description of available intentions 

- Request_intention (Request_msg)
    1. pushes to himself the goal attached to the Request_msg message in the attribute Goal goal

- ReachPddlGoal_intention (ReachPddlGoal_goal)
    1. sends a Sensing_msg to env agent
    2. calls blackbox planner to generate a plan to achieve the PddlClause[] pddlGoal attached to the ReachPddlGoal_goal
    2. pushes a ExecutePddlPlan_goal to himself
    3. waits for its termination

- Sensing_intention (Sensing_msg)
    1. sends back a Clause_msg to the sender for each clause in its beliefset

- Clause_intention (Clause_msg)
    1. updates internal beliefset according to the received clause

- ExecutePddlPlan_intention (ExecutePddlPlan_goal)
    1. pushes a PddlStep_goal to himself for each step in the plan
    2. at each step waits for its termination then terminate

- PddlStep_intention (PddlStep_goal)
    1. sends PddlAction_msg to env agent
    2. waits for its termination

- PddlAction_intention (PddlAction_msg)
    1. checks preconditions of pddl action
    2. waits for 500ms
    3. applies effects to its beliefset and terminate

- Postman_intention (Postman_goal)

- PostmanEverythingInParallel_intention (Postman_goal)

- PostmanOneRequestAtTime_intention (Postman_goal)
