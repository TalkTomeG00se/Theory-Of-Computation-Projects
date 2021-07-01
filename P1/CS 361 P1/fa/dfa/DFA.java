package fa.dfa;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import fa.State;

/**
* Class that represents a singluar DFA.
* @author Colten Davis
*/
public class DFA implements DFAInterface {

    protected LinkedHashSet<DFAState> allStates; // Set of all states
    protected LinkedHashSet<Character> givenLanguage; // The language provided

    /**
    * Constructor for the given DFA
    */
    public DFA(){

        allStates = new LinkedHashSet<>();

        givenLanguage = new LinkedHashSet<>();

    }

    @Override
    public void addStartState(String name) {
    
        allStates.add(new DFAState(name,false,true)); // adds the state as the start state

    }

    @Override
    public void addState(String name) {
       
       allStates.add(new DFAState(name,false,false)); // adds a state

    }

    @Override
    public void addFinalState(String name) {
       
       allStates.add(new DFAState(name,true,false)); // adds the state as a end state

    }

    @Override
    public void addTransition(String fromState, char onSymb, String toState) {
        
        DFAState origin = new DFAState("orign",false, false); // the beginning state of the transition

        DFAState destination = new DFAState("destination",false,false); // the destination of the transition

        Iterator<DFAState> originIterator = allStates.iterator(); // iterator looking for the origin in the allStates set

        Iterator<DFAState> destinationIterator = allStates.iterator(); // iterator looking for the destination in the allStates set

        while(!origin.getStateName().equals(fromState)){ // while origin is not equal to the passed in fromstate

            origin = originIterator.next();
        }

        while(!destination.getStateName().equals(toState)){ // while destination is not equal to the passed in toState

            destination = destinationIterator.next();
        }
        
        origin.nextState(onSymb, destination); // set the next state from the orgin as the destination DFAstate from the symbol given

        givenLanguage.add(onSymb); // add this passed in symbol to our language

    }

    @Override
    public Set<? extends State> getStates() {
        
        return allStates;
    }

    @Override
    public Set<? extends State> getFinalStates() {
        
    DFAState endState = new DFAState("endState", false, false); // temp state 

		LinkedHashSet<DFAState> endSet = new LinkedHashSet<>(); // new temp set to hold end states

		Iterator<DFAState> iterator = allStates.iterator(); // iterator to look over all states

		while(iterator.hasNext()){ // while more states in allStates

			endState = iterator.next();

			if(endState.endState){ // if our temp state is really an end state

				endSet.add(endState);

			}

		}

		return endSet;
    }

    @Override
    public DFAState getStartState() {
        
    DFAState startState = new DFAState("startState", false, false); // temp state

		Iterator<DFAState> iterator = allStates.iterator(); // iterator over all states

		while(!startState.startState){ // while our temp state is not a start state

			startState = iterator.next();

		}

		return startState;
    }

    @Override
    public Set<Character> getABC() {
        
        return givenLanguage;
    }

    @Override
    public boolean accepts(String s) {
        
        DFAState temp = new DFAState("accepted", false, false); // temp state

        char[] givenString = s.toCharArray(); // converting given string to a char array to index through

        temp = getStartState(); // our temp DFAState is a start state

        if(givenString[0] == 'e'){ // empty string check

            return temp.endState;

        } else {

            for(int i=0; i<givenString.length; i++){

                temp = temp.getStatePath(givenString[i]); // gets transition
            }

            return temp.endState; // reached an end state
        }
    }

    @Override
    public DFAState getToState(DFAState from, char onSymb) {
        
        DFAState origin = new DFAState("beginning", false, false);  // the start state of the transition

        Iterator<DFAState> iterator = allStates.iterator(); // iterator over all states

        while(!origin.equals(from)){ // while our temp state does not equal the DFAState passed in

            origin = iterator.next();
        }

        return origin.getStatePath(onSymb); // returns the DFAState object that we arrived at
    }
    
    /**
     * Q tuple string method
     * @return - returns a String of the Q tuple
     */
    public String qString(){

        StringBuilder string = new StringBuilder();

        string.append("Q = { ");

        Iterator<DFAState> iterator = (Iterator<DFAState>) getStates().iterator(); // had to cast in order to use the getSetName function. Still getting unchecked errors, unsure why.
        
        while(iterator.hasNext()){

            string.append(iterator.next().getStateName() + " "); // wasn't working with getName from FAInterface, had to cast iterator as DFAState to work
        }

        string.append("}\n");

        return string.toString();


    }
    
    /**
     * The sigma string tuple
     * @return - returns the string of the sigma tuple
     */
    public String sigmaString(){
    
        StringBuilder string = new StringBuilder();

        string.append("Sigma = { ");

        Iterator<Character> iterator = getABC().iterator(); // iterates over our language

        while(iterator.hasNext()){

            string.append(iterator.next().toString() + " ");
        }

        string.append("}\n");

        return string.toString();
    
    }
    
    /**
     * The delta tuple string
     * @return - returns the delta tuple values
     */
    public String deltaString(){

        StringBuilder string = new StringBuilder();

        string.append("delta = \n");

        String[][] table = tableString(); // calls the tablestring method to build the table

        for(int i = 0; i < getStates().size()+1; i++){

            for(int j = 0; j < getABC().size()+1; j++){

                string.append("\t" + table[i][j]);
            }

            string.append("\n");
        }

        string.append("\n");

        return string.toString();

    }

    /**
     * Creates the table for the delta string
     * @return - returns a table of values for the delta tuple
     */
    public String[][] tableString(){

   String[][] transitionTable = new String[getStates().size()+1][getABC().size()+1]; // initialize 2 dimensional string array for the table

		transitionTable[0][0] = "";

		Iterator<Character> iterator = getABC().iterator();

		for(int i = 1; i < getABC().size()+1; i++){

			transitionTable[0][i] = iterator.next().toString(); // fills the table with the symbols to transition on

		}

		Iterator<DFAState> iterator2 = allStates.iterator();

		DFAState state = new DFAState("state", false, false);

		for(int i = 1; i < getStates().size()+1; i++){

			state = iterator2.next();

			transitionTable[i][0] = state.getStateName(); // fills table with the beginning states

			for(int j = 1; j < getABC().size()+1; j++){

				transitionTable[i][j] = getToState(state, transitionTable[0][j].charAt(0)).getStateName(); // fills table with the transitions

			}

		}

		return transitionTable;

    }

    /**
     * The start states
     * @return - returns a string of start states
     */
    public String startString(){

        StringBuilder string = new StringBuilder();

        string.append("q0 = " + getStartState().getStateName() + "\n");

        return string.toString();


    }

     /**
     * The final states
     * @return - returns a string of final states
     */
    public String finalString(){

        StringBuilder string = new StringBuilder();

        string.append("F = { ");

        Iterator<DFAState> iterator = (Iterator<DFAState>) getFinalStates().iterator(); // had to cast in order to use the getSetName function

        while(iterator.hasNext()){

            string.append(iterator.next().getStateName() + " ");
        }

        string.append("}\n");

        return string.toString();

    }

    /**
     * Overridden toString method to print out the entire set of tuples for the DFA
     */
    public String toString(){

        StringBuilder string = new StringBuilder();

        string.append(qString());
        string.append(sigmaString());
        string.append(deltaString());
        string.append(startString());
        string.append(finalString());

        return string.toString();
         
    }
}


