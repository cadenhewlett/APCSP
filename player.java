import java.util.ArrayList;
import java.util.List;

/**
 * This Class defines the 'player' object, whose characteristics are those of a player at the table.
 * There are also methods in this class for the decision-making of the CPU player.
 * 
 * @author Caden Hewlett
 *
 */
public class player {
	
	//Define Public Object Characteristics
	public List<Integer> hand = new ArrayList<Integer>(); //the player's hand, as an ArrayList
	public int handSum = 0; //the sum of cards in the player's hand
	public boolean isPlayer;  //if this is the non-CPU player
	public boolean doneDrawing; //if this player has decided to stand
	public boolean donePlaying; //if this player has gone bust
	public boolean overLimit; //if the player is over 21
	public boolean firstDraw; //if its the player's first draw
	public String name; //the player's name
	public String handString; //a string containing the player's hand

	
	private cardUtil utility = new cardUtil(); //creates an Instance of the Card Utility

	
	/**
	 * An Object defining a Player of the game.
	 * 
	 * @param mainPlayer if this Object is the Main Player
	 * @param ID the name of the Player.
	 */
	public player(boolean mainPlayer, String ID) {

		this.hand = hand;    
		this.name = ID; 
		this.handSum = 0; 
		this.isPlayer = mainPlayer;
		this.overLimit = false; 
		this.doneDrawing = false; 
		this.donePlaying = false; 
		this.firstDraw = true;
	}
	
	/**
	 * Determines the choice - hit or stand - of the CPU given a Risk Value. 
	 * This function is a simple decision-making algorithm, to be improved as game data is collected.
	 * 
	 * @param risk The percentage Risk the CPU is willing to take on its draws.
	 * @param opponent The main Player, whose hand will be referenced when revealed.
	 * @param dealer The Dealer, whose hand will be referenced when revealed.
	 * 
	 * @return True if drawing is worth the risk, False otherwise.
	 */
	public boolean makeChoice(double risk, player opponent, player dealer) {
		
		int cap = 21 - handSum; //determines the maximum value of card that can be drawn
		int goodCards = 0; //defines local variable
		double successChance = 0; //defines local variable
		
		for (int cardNum = 0; cardNum <= 10; cardNum++) { //for all cards in the deck
			if (cardNum < cap) { //if the card is within the acceptable limit calculated at the start
				goodCards += 4; //add all 4 Instances of that card to the 'good cards' pool.
			}
		}
		
		if(constants.DEBUG_DEALER) {
			System.out.println("CPU's Sum " + handSum);
			System.out.println("CPU's Cap " + cap);
			System.out.println("GoodCards for CPU " + goodCards); //debugging tool
		}
		
		for (int i = 0; i < hand.size(); i++) { //for all cards in the CPU's hand

			if (utility.determineValue(hand.get(i)) < cap) { //if a card in the 'good cards' pool is already in the CPU's hand
				goodCards -= 1; //remove the card from the 'good cards' pool
			}

		}
		
		if(constants.DEBUG_DEALER) {
			System.out.println("Goodcards after hand: " + goodCards); //debugging tool
		}
		
		if (utility.determineValue(dealer.hand.get(0)) < cap) { //if the dealer's one revealed card is in the 'good cards' pool
			goodCards -= 1; //remove that card from the 'good cards' pool
		}
		
		if(dealer.doneDrawing) { //if the dealer is done, meaning their hand is revealed
			
			for (int i = 1; i < (dealer.hand.size() - 1); i++) { //cycle through the rest of his hand, not the first card (note that i = 1)

				if (utility.determineValue(dealer.hand.get(i)) < cap) { //if any card in the rest of the dealer's hand is in the 'good cards' pool
					goodCards -= 1; //remove that card from the 'good cards' pool
				}

			}
			
		}
		
		if(opponent.donePlaying) { //if the opponent has gone bust, their hand will be revealed so their cards can be included in the counting algorithm
			
			for (int i = 0; i < (opponent.hand.size()); i++) { //cycle through the Main Player's revealed hand

				if (utility.determineValue(opponent.hand.get(i)) < cap) { //if any card in their hand is in the 'good cards' pool
					goodCards -= 1; //remove that card from the 'good cards' pool
				}

			}
		}
		
		successChance = ((double)goodCards / 52); //calculates Success Chance by dividing available 'good cards' by number of total cards
		
		if(constants.DEBUG_DEALER) { //if we're debugging
			//sends info to the system

			System.out.println("Final GoodCards for CPU " + goodCards);
			System.out.println("Success Chance on Next Draw " + successChance);
		}
		
	
		if(successChance > risk) { //if the play isn't too risky
			return true; //decide to draw another card
		}
		else {
			return false; //decide to stand
		}
	}
}
