package fa.nfa;

import java.util.*;
import fa.State;

/**
 * Class that represents a single NFAState and its various abilities.
 * @author Colten Davis
 */
public class NFAState extends State{

	private boolean isFinalState; // boolean for checking if a state is a final one
	private HashMap<Character,Set<NFAState>> nfaDelta; // hashmap to hold the delta values
	
	 /**
     * Constructor for an NFA
     * @param name - Name of the NFAState
     * @param isFinalState - true for final state
     */
	public NFAState(String name, boolean isFinal){

		this.name = name; // state name

		nfaDelta = new HashMap<Character, Set<NFAState>>(); // new hashmap for the delta output

		this.isFinalState = isFinal; // true if final state, else false
	}

	  /**
     * Checks if the current state is the final state
     * @return - true if state is final, else false
     */
	public boolean checkFinal(){

		return isFinalState;
	}

	/**
     * Represents a NFA transition from a given symbol
     * @param symb - The current symbol in the alphabet that is the start
     * @return - returns the new state of the NFA after the transition
     */
	public Set<NFAState> nfaTransition(char symb){

		return nfaDelta.get(symb);
	}

	  /**
     * Moves from the current NFAState to the next NFAState
     * @param onSymb - Symbol that is the origin of the transition
     * @param destination - Next NFAState after the transition
     */
	public void moveToNextState(char onSymb, NFAState nextState){

		Set<NFAState> temp = nfaDelta.get(onSymb);

		if(temp==null){ // null check

			Set<NFAState> transitions = new LinkedHashSet<NFAState>(); // new hash set of NFAStates

			transitions.add(nextState); // adds the new NFAstate

			nfaDelta.put(onSymb, transitions); // adds the the new NFAstate, and its orginating symbol into the delta

		}

		Set<NFAState> result = nfaDelta.get(onSymb); // set for the result

		result.add(nextState); // add next state

	}

}