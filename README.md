# AoseDevKit2018

The AoseDevKit2018 framework has been implemented for the course of Agent Oriented Software Engineering at the University Of Trento (2018).
The framework consists of three repositories:
- *AoseDevKit2018-MultiAgentSystem: https://github.com/marcorobol/AoseDevKit2018-MultiAgentSystem*
- *AoseDevKit2018-Generic: https://github.com/marcorobol/AoseDevKit2018-Generic*
- *AoseDevKit2018-Blocksworld: https://github.com/marcorobol/AoseDevKit2018-Blocksworld*

Introductory slides on the framework are available in the doc folder in the AoseDevKit2018-MultiAgentSystem repository.

In the case of bugs to any of these, let us know or fix them and do a pull request.

## Installing and running

Prerequisites: Git + Eclipse + JDK 1.8

This repository depends on *AoseDevKit2018-Generic* and *AoseDevKit2018-MultiAgentSystem*
so be sure to import all of them into your Eclipse workspace.
To do so follow these steps:

1. Fork and clone all the 3 repositories:
    > $ git clone xxx
2. Import the projects in Eclipse:
    > File -> Import -> Existing Projects into Workspace
3. **[Only for Linux/Mac users]** Give execution permission to blackbox executable in 2018-AoseDevKit-Blocksworld/blackbox:
    > $ chmod +x blackbox
4. **[Only if you have multiple versions of Java]** In the case Eclipse detects errors in the projects
    - in the Eclipse preferences set default compiler to Java 1.8
    - or right click on each of them and select:
      > Properties -> Java Compiler -> Enable project specific setting -> Compiler 1.8
  or 
5. To run right click on file unitn.adk2018.blocksworld.BlocksworldLauncher and select:
    > Run as -> Java Application

# AoseDevKit2018-Generic

This project contains implementations of goals, messages, intentionis, and a generic agent with a generic control loop for the AoseDevKit2018 framework.

Everything in this project has to stay domain-independent and should not modify the main logic of the core framework provided in the AoseDevKit2018-MultiAgentSystem project.

## Quick start guide to the use and development of goals, messages, and intentions with the AoseDevKit2018 framework

### Goals and messages

Goals and messages represent an objective that an agent want to achieve, without the implementation on how to do it. For example:
```java
public class Postman_goal extends Goal {
}
```
They could eventually include parameters. For example:
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

Goals differs from messages because they can be submitted to agents differently:
- goals can only be submmitted internally by the agent to himself
- messages instead can be posted to agents from outside
    - request could be handled differently with respect to inform messages, depending on the postman intention used by the agent

### Intentions

Intentions are the implementation on how to achieve a goal or a message.
Depending on the configuration of agents, given a goal/message, zero or more intentions can be available to achieve the goal/message.
A selection algorithm is available in unitn.adk2018.generic.agent.General_agent, where the first applicable intention is selected (given expression in the contex method).

All intentions extend unitn.adk2018.intention.Intention by specifying the type of event that they handle.
For example:
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
		return waitUntil( this::stepEnd, msg.wasHandled() ); //wait until action is done on environment then continue
	}
	public Next stepEnd(IntentionInput in) {
		if ( msg.wasHandledWithSuccess().isTrue() )
			return null; //success
		else
			return waitFor(null, 0); //fail
	}
}
```

Each intention provides its implementation in step0(IntentionInput in). Other steps can be defined as in the example of above. Each step must return a Next object which can be conveniently created with the useful shortcut methods offered by the intention class:
```java
public final Next waitFor(final Function<IntentionInput, Next> nextstep, final long waitingTime);
public final Next waitUntil(final Function<IntentionInput, Next> nextstep, final MaintenanceCondition maintenanceCondition);
```

### Description of available intentions 

- Request_intention (Request_msg)
    1. pushes to himself the goal attached to the message

- ReachPddlGoal_intention (ReachPddlGoal_goal)
    1. sends a Sensing_msg to env agent
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
