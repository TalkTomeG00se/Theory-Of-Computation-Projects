package fa.dfa;

import java.util.LinkedHashMap;
import fa.State;


/**
 * Class that represents a single DFA State. Extends the State class.
 * @author Colten Davis
 */
public class DFAState extends State {

    protected LinkedHashMap<Object,DFAState> transitionTable; // HashMap that will hold the transitions for this DFA
    protected boolean endState,startState; // Boolean values to indicate the beginning and end states of the DFA
    protected String stateName; // String that will hold the name of a DFA state

    /**
     * DFAState constructor that represents a single DFA state.
     * @param s - the name of the current state
     * @param x - boolean value representing that the current state is the end state
     * @param y - boolean value representing that the current state is the start state
     */
    public DFAState(String s, boolean x, boolean y){

        this.stateName = s;
        this.endState = x;
        this.startState = y;
        transitionTable = new LinkedHashMap<>();
    }

    /**
     * Method that will return the name of the state this method is called on
     * @return the name of the state
     */
    public String getStateName(){

        return stateName;
    }

    /**
     * Method that will move to the next state
     * @param state - the DFA object that will be passed to this method
     * @param currentState - current state
     */
    public void nextState(Object state, DFAState currentState){

        transitionTable.put(state,currentState);
    }

    /**
     * Method that will return the object from the transition table based on the object passed into it
     * @param state - the DFA object that will be passed into this method
     * @return the object from transition table
     */
    public DFAState getStatePath(Object state){

        return transitionTable.get(state);
    }
    
}
