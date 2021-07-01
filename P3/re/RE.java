package re;

import fa.State;
import fa.nfa.NFA;
import fa.nfa.NFAState;

/**
 * Class that will build a NFA from a regular expression
 * @author Colten Davis
 */
public class RE implements REInterface {

    private int count = 0; // will be our state count, current position indicator
    private String input; // string representing the input from the .txt file

    /**
     * Class constructor
     * 
     * @param s
     */
    public RE(String s) {

        this.input = s; // sets the given string as the input for our regular expression
    }

    @Override
    public NFA getNFA() {

        NFA term = term();

        if(more() && peek() == '|'){ // checks if there is more and there is another term after the | symbol

            eat('|'); 

            return choice(term,getNFA());

        } else {

            return term;
        }
    }

    /**
     * Looks at the next input, but doesn't use it
     * 
     * @return - returns the character at the initial index 0
     */
    private char peek() {

        return input.charAt(0);
    }

    /**
     * Looks at next input and parses it.
     * 
     * @param c - The character that is next in the input
     */
    private void eat(char c) {

        if (peek() == c){ // compares the next c to the passed in c

            this.input = this.input.substring(1);

        } else {

            throw new RuntimeException("Expected: " + c + "; got: " + peek());

        }    
    }

    /**
     * Looks at next input, parses it and uses it
     * 
     * @return - Returns the next character of the input
     */
    private char next() {

        char c = peek(); // looks ahead

        eat(c); // consumes next character

        return c; // returns the next character
    }

    /**
     * Checks if there is more input
     * 
     * @return - Returns true if there is more of the input, else false
     */
    private boolean more() {

        return input.length() > 0; // if length is more than 0, there is more input
    }


    /**
     * Check that it has not reached the boundary of a term or the end of the input
     * 
     * @return - Returns the newly created NFA as a result of concatenation
     */
    private NFA term() {

        NFA temp = new NFA(); // new method temporary NFA

        String input = Integer.toString(count++);
        
        temp.addStartState(input);

        temp.addFinalState(input);

        while (more() && peek() != ')' && peek() != '|'){ // if more input, and next symol isn't a parentheses, and not the | symbol

            NFA nextInput = factor(); // next input is kleene star

            temp =  sequence(temp, nextInput); // implements concatination between our two created NFAs
        }
        return temp;

    }

    /**
     * Checks for any Kleene stars
     * 
     * @return Returns the new NFA that is a result of a kleene star operation
     */
    private NFA factor() {

        NFA temp = base();

        while (more() && peek() == '*') { // if more and the next input is the kleene start
 
            eat('*') ; // consume the kleene star

            temp = repetition(temp) ;
          }
      
          return temp;
    }

    /**
     * Checks to see which of the three cases it has encountered
     * 
     * @return Returns a NFA depending on the case that is observed
     */
    private NFA base() {

        NFA temp;

        switch (peek()){ // switches on the character at input(0) via the peek method

            case '(':
                eat('(');
                temp = getNFA();
                eat(')');
                return temp;

            default:
            	return primitive(next());
        }

    }

    /**
     * Implements the 'or' operation, represented by '|'
     * 
     * @return - Returns a new NFA that is craeated as a result of a OR operation
     */
    private NFA choice(NFA first, NFA second) {

        NFAState origin = (NFAState)first.getStartState(); // our first NFA

        NFAState destination = (NFAState)second.getStartState(); // the second NFA

        first.addNFAStates(second.getStates()); // adding states from destination to the orign states

        first.addAbc(second.getABC()); // adding the second NFA language to the first NFA language

        String newState = Integer.toString(count++);

        first.addStartState(newState); // adding the new state

        first.addTransition(newState, 'e', origin.getName()); // adding empty string

        first.addTransition(newState, 'e', destination.getName()); // adding empty string

        return first;

    }

    /**
     * Implements concatenation
     * 
     * @return Returns a newly created NFA, from the result of a concatenation operation
     */
    private NFA sequence(NFA first, NFA second) {

        for (State currentState : first.getFinalStates()) { // for every state in the final states

            ((NFAState) currentState).setNonFinal(); // NOTE: had to cast to NFAState not State, unknown why since currentState is a State already

            ((NFAState) currentState).addTransition('e', (NFAState) second.getStartState()); // adding empty transition

        }

        first.addNFAStates(second.getStates()); // adding second NFA to first

		first.addAbc(second.getABC()); // adding second language to the first

		return first;

    }

    /**
     * Implements the Kleene Star operation, represented by '*'
     * 
     * @return Returns a newly created NFA as a result of a kleene star operation
     */
    private NFA repetition(NFA star) {

        NFAState temp = (NFAState)star.getStartState(); // gets all start states
		
		for(State state : star.getFinalStates()){ // from start state to final

		    star.addTransition(state.getName(), 'e', temp.getName());
        }

		String s = Integer.toString(count++); // new start state

		star.addStartState(s); // adding the start state

		star.addFinalState(s); // adding the final state

		star.addTransition(s, 'e', temp.getName());

		return star;

    }

    /**
     * Holds a individual character to assist the base() method
     * 
     * @return Returns a NFA after moving through our state count
     */
    private NFA primitive(char c) {

        NFA temp = new NFA();

        String string1 = Integer.toString(count++); // increases our count and parses it as a string

        temp.addStartState(string1); // adds the start state to our NFA

        String string2 = Integer.toString(count++); // increases our count again and parses it as a string

        temp.addFinalState(string2); // adds the final state to our NFA

        temp.addTransition(string1, c, string2); // adds our transition
        
        return temp;

    }

}
