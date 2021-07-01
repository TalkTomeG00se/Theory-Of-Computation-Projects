package fa.nfa;

import java.util.*;
import fa.State;
import fa.dfa.DFA;

/**
 * Class that represents a NFA and it's various functions.
 * @author Colten Davis
 */
public class NFA implements NFAInterface{

	private Set<Character> alphabet; //alphabets
	private Set<NFAState> nfaStates; //sets
	private NFAState startState; //start state

	 /**
     * NFA constructor that creates a new NFA object
     */
	public NFA() {

		alphabet = new LinkedHashSet<Character>();

		nfaStates = new LinkedHashSet<NFAState>();
	}

	@Override
	public void addStartState(String name) {

		if (getNFAState(name) == null) { // null check, state doesn't exist in the state set

			startState = new NFAState(name,false); // new state

			nfaStates.add(startState); // add new state
		}
	}


	@Override
	public void addState(String name) {

		nfaStates.add(new NFAState(name, false)); // add new state

	}


	@Override
	public void addFinalState(String name) {

		nfaStates.add(new NFAState(name,true)); // add new state
	}


	@Override
	public void addTransition(String fromState, char onSymb, String toState) {
		
		NFAState origin = getNFAState(fromState); // origin state

        NFAState destination = getNFAState(toState); // destination state

        origin.moveToNextState(onSymb, destination); // moves from orign to the destination state

		if (!alphabet.contains(onSymb) && onSymb != 'e') { // symbol not in alphabet and not a closure state

			alphabet.add(onSymb); // add symbol to alphabet
		}

	}

	@Override
	public Set<State> getStates() { // had to change from <? extends state>, unknwon why it matters if it can be abstract

		Set<State> setOfStates = new LinkedHashSet<State>(); // new set of states

		setOfStates.addAll(nfaStates); // adds all states to temp set

		return setOfStates; // returns the temp set
	}

	@Override
	public Set<State> getFinalStates() { // had to change from <? extends state>, unknwon why it matters if it can be abstract

		Set<State> setOfFinalStates = new LinkedHashSet<State>(); // new temp set

		for (NFAState n : nfaStates) { // check all states

			if (n.checkFinal()) { // if final state

				setOfFinalStates.add(n); // add to temp set
			}
		}
		return setOfFinalStates; // return the temp set
	}

	@Override
	public State getStartState() {

		return startState;
	}

	@Override
	public Set<Character> getABC() {

		return alphabet;
	}

	/**
	 * Computes an equivalent DFA
	 */
	public DFA getDFA() {
	
		DFA newDFA = new DFA(); // new DFA

		List<Set<NFAState>> stateList = new LinkedList<>(); // list of NFAStates

		Set<Set<NFAState>> stateSet = new LinkedHashSet<>(); // set of NFAStates

		HashMap<Set<NFAState>, String> newDFAMap = new HashMap<>();	 // hashmap of NFAStates

		Set<NFAState> closureStartSet = eClosure(startState); // set of starts in the NFA
		
		stateList.add(closureStartSet); // add the start state of the NFA

		newDFA.addStartState(closureStartSet.toString()); // add the start state of teh NFA to the DFA

		newDFAMap.put(closureStartSet, closureStartSet.toString()); // adds start state of the NFA to the hashmap of the DFAstates

		if (hasFinalState(closureStartSet)) { // if set has final state in it

			newDFA.addFinalState(closureStartSet.toString()); // add final state to tempDFA
		}
				
		while (!stateList.isEmpty()) { // while list of states is not empty

			Set<NFAState> tempSet = stateList.remove(0); // step through list

			stateSet.add(tempSet); // add current state to set of NFAStates

			for (char c : alphabet) { // for every character in alphabet

				Set<NFAState> anotherTempSet = new LinkedHashSet<>(); // temp set for NFAStates

				for (NFAState n : tempSet) { // for every NFAState in teh set of NFAStates

					if (n.nfaTransition(c) != null) { // while the transition is not null

						for (NFAState m : n.nfaTransition(c)) { // for every state in those transitions

							anotherTempSet.addAll(eClosure(m)); // add all those closures to our temp set
						}
					}
				}
				
				if (!stateSet.contains(anotherTempSet)) { // if our destination is part of the finals

					if (hasFinalState(anotherTempSet)) { // final state check

						newDFAMap.put(anotherTempSet, anotherTempSet.toString()); // adds set to the DFA

						newDFA.addFinalState(newDFAMap.get(anotherTempSet)); // adds the final state to the DFA

						stateSet.add(anotherTempSet); // add to our set of states
					}
					
					else { // not final

						newDFAMap.put(anotherTempSet, anotherTempSet.toString()); // adds set to DFA

						newDFA.addState(newDFAMap.get(anotherTempSet)); // adds final state to DFA

						stateSet.add(anotherTempSet); // add to our set of states

					}
					
					if (!stateList.contains(anotherTempSet)){ // not in the state list

						stateList.add(anotherTempSet); // adds new state to list

					}
				}
				
				newDFA.addTransition(newDFAMap.get(tempSet), c, newDFAMap.get(anotherTempSet)); // new transition for new state
			}
		}
		return newDFA;

	}
	

	@Override
	public Set<NFAState> getToState(NFAState from, char onSymb) {

		return from.nfaTransition(onSymb); // NFAState transition
	}

	@Override
	public Set<NFAState> eClosure(NFAState s) {

		Set<NFAState> eStates = new LinkedHashSet<>(); // temp set of closure states

		List<NFAState> tempStates = new LinkedList<>(); // temp set of NFAStates

		tempStates.add(s); // adds the state to the list

		eStates.add(s); // add state to the closure set

		while (!tempStates.isEmpty()) { // while set is not empty

			NFAState temp = tempStates.remove(0); // loop through all

			eStates.add(temp); // adds the closure state
			
			if (temp.nfaTransition('e') == null) { // if the transition is null
				
				return eStates; // return the set of closure states
			}
			
			for (NFAState states : temp.nfaTransition('e')) { // for every empty transition

				if (!eStates.contains(states)) { // if closure set doesn't contain empty transitions

					tempStates.add(states); // add the states
				}
			}	
		}
		return eStates;
	}
	
	 /**
     * Gets a NFAState, given that the name exists within the set of NFAStates
     * @param name - Name of the NFAState 
     * @return - Returns the NFAState with the matching name, assuming it exists in the state set
     */
	private NFAState getNFAState(String name) {

		NFAState temp = null; // empty NFAState

		for (NFAState n : nfaStates) { // check all states
			
			if (n.getName().equals(name)) { // if the passed in name exists in the set

				temp = n; // our temp staet is that state now

				break; // break out of loop
			}
		}
		return temp; // return the NFAState
	}

	/**
	 * Boolean method to check if the set of states contains a final state
	 * @param statesToCheck - set of states to check
	 * @return - true if set contains final state, else false
	 */
	private boolean hasFinalState(Set<NFAState> statesToCheck) {
		
		boolean containsFinal = false; // initialize boolean as false

		for (NFAState state : statesToCheck) { // check all states

			if (state.checkFinal()) { // if final state

				containsFinal = true; // set boolean true

				break; // break out of loop
			}
		}
		return containsFinal; // return boolean
	}

}