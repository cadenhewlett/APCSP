import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The Game Class:
 * 
 * This class consists of Utility functions, many of which have the object {@link player#player} as a parameter.
 * The methods in this class are responsible for primary game utilities that interact with a Player. 
 * This includes drawing cards, calculating hand sums and others.
 * 
 * @author Caden Hewlett
 *
 */

public class game {

	public List<Integer> drawnCards = new ArrayList<Integer>(); //A dynamic Array List of all drawn cards. Used to determine duplicates.
	private int index; //Index of card in hand
	cardUtil utility = new cardUtil(); //Generates an instance of the Card Utility class
	
	public game() {} 
	
	/**
	 * Gives a random card from the deck that is not already drawn.
	 * 
	 * @param drawer The player to receive a card. 
	 */
	public void drawCard(player drawer) {

		boolean validCard = true;
		Random rand = new Random();
		index = rand.nextInt(52); //random integer within range 0-51
		
		if(constants.DEBUGGING) {System.out.println("Card Index: " + index);}

		for (int i = 0; i < (drawnCards.size()); i++) {
			
			//Iterate through drawn cards
			if (index == drawnCards.get(i)) { //if our card is already drawn
				validCard = false; //it's not valid
				break;
			}
			
		}

		if (validCard) { //if the card is valid
			drawnCards.add(index); //add it to the drawn cards
			drawer.hand.add(index); //add it to the player's hand
			
			if(constants.DEBUGGING) {
				System.out.println("Card Drawn: " + utility.determineCard(index));
				System.out.println("Card Valid! Player's hand: " + drawer.hand.toString());
				System.out.println("Drawn Cards: " + drawnCards);
			}
		}
		
		else { 
			drawCard(drawer); //otherwise draw another
			if(constants.DEBUGGING) {
				System.out.println("Card Invalid! Drawing again.");
			}
		}

	}
	
	
	/**
	 * Shows the Card(s) of a Player at an Index.
	 * 
	 * @param num The index of the card to be revealed. Use {@link constants#ALL_CARDS} to reveal the whole hand.
	 * @param target The player whose cards will be revealed. 
	 * 
	 * @return A String containing the name of the player and the revealed cards. '???' If a card is not revealed.
	 */
	public String showCards(int num, player target) { //string utility function for showing hand info
		
		String handString = target.name + "'s Hand: ";
		String cardsString = "";
		
		for(int i = 0; i < target.hand.size(); i++) {
			
				if(num == constants.ALL_CARDS || i < num) {
					cardsString += utility.determineCard(target.hand.get(i));
				}
				else {
					cardsString += "???";
				}
			
				cardsString += ", ";
			
			}
	
		
		return handString + cardsString;
	}

	
	/**
	 * Calculates the Total of the Cards in Hand for the Player, and updates the player's designated handSum variable.
	 * 
	 * @param target The player whose hand will be calculated.
	 */
	public void calculateHandSum(player target) {
		
		
		int sum = 0;
		int cardValue = 0;
		boolean bigAce = false;
		
		for(int i = 0; i < target.hand.size(); i++) {
			
			cardValue = utility.determineValue(target.hand.get(i)); //determines value based on index
			
			cardValue = cardValue > 10 ? 10 : cardValue; //value cap for face cards
			
			if(cardValue == 1 && (sum + 11) < constants.POINT_CAP) {
				cardValue = 11; //counts an ace as 11 if that decision would not exceed the cap
				bigAce = true; //the ace is being treated as an 11
			}

			sum += cardValue; //accumulates sum

		}
		
		if(sum > constants.POINT_CAP && bigAce) { //if we exceed the cap with a previous ace as 11	
			sum -= 10; //treat the ace as a 1 instead
			bigAce = false;	//the ace is a 1 now 
		}
		
		target.handSum = sum;
	}
	
	
	
	/**
	 * Gives a Summary of the Round or Starting Deal
	 * 
	 * @param you The player interacting with the Game.
	 * @param opponent The CPU playing alongside the Player.
	 * @param dealer The dealer player variable.
	 * @param dealerDone If the dealer is done drawing cards.
	 */
	
	public void giveSummary(player you, player opponent, player dealer, Boolean dealerDone) {
		
		if(!you.name.equals("The Dealer")) { //if the selected player is not the dealer
			
			System.out.println(showCards(constants.ALL_CARDS, you)); //send all cards in hand to system
			calculateHandSum(you); //refreshes hand sum
			System.out.println("Currently you have  " + you.handSum +"."); //sends hand sum to system
			
			System.out.println("\n" + showCards(1, dealer) + "\n"); //show dealer's revealed card
		
		}

	}
	
	
	/**
	 * Determines the Card Drawn by the Player as a String.
	 * 
	 * @param you The player interacting with the Game.
	 * @param round The current drawing round.
	 * @return A String in the style of: 'You drew a ' with the ID of the Drawn Card
	 */
	
	public String determineDrawnCard(player you, int round) {
		
		String cardDrawn = utility.determineCard(you.hand.get(round + 1)); //gets the card at index of round incrementation
		
		return ("You drew a " + cardDrawn + "."); //combines cardDrawn string into a sentence for exporting
		
	}

}
