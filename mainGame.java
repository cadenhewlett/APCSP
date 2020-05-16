import java.util.Scanner;
/**
 * Welcome to Blackjack! 
 * The purpose is to simulate you and one other person at a table playing Blackjack.
 * There is a primary player - you - and a CPU player, both of whom are playing independently against a dealer.
 * This is the gameplay framework for a larger project.
 * Once this simulation is working perfectly, it will save data and optimize the decision-making CPU.
 * 
 * @author Caden Hewlett
 * Feel free to email me at cadenhewlett@gmail.com
 */
public class mainGame {
	

	public static void main(String[] args) {

		game mainSequence = new game(); //creates an instance of the Game Class
		
		player you = new player(true, "Author Caden Hewlett " + "Email me at cadenhewlett@gmail.com"); //creates a Player Object 
		
		player opponent = new player(false, "Opponent"); //creates a Player Object that is the CPU Opponent
		player dealer = new player(false, "The Dealer"); //creates a Player Object that is the Dealer
		
		Scanner userInput = new Scanner(System.in); //starts up a user input scanner
		
		boolean lost = false;
		boolean gameOver = false; //if the game ended completely
		
		int drawingRound = 1; //player's round of drawing cards
		
		String result; //local variable for user input
		String decisionString = "";
		
		System.out.println("Welcome to Blackjack! The purpose is to simulate you and "
				+ "one other person at a table playing Blackjack.");
		
		System.out.println("What's your name? "); //prompts user input

		you.name = userInput.nextLine().trim(); //Sets Player's name as input
		
		System.out.println("Hope you're happy with your name, " + you.name +"." +"\n");
		
		
		/*
		 * *****************
		 *  DEAL THE CARDS
		 * *****************
		 */
		
		mainSequence.drawCard(you); //dealer gives you a card
		mainSequence.drawCard(opponent); //then your opponent
		mainSequence.drawCard(dealer); //then itself
		
		mainSequence.drawCard(you); //then you
		mainSequence.drawCard(opponent); //then your opponent
		mainSequence.drawCard(dealer); //then itself

		//Summarizes the Deal
		mainSequence.giveSummary(you, opponent, dealer, dealer.doneDrawing); 
		
		
		while(!lost) { //currently a continuous loop
			
			/*
			 * *****************
			 *  PLAYER'S TURN
			 * *****************
			 */
			
			mainSequence.calculateHandSum(you); //calculates hand sum
			
			while(!you.doneDrawing && (you.handSum < constants.POINT_CAP)) {

				System.out.println("Would you like to draw a card? y/n"); //prompts user input
				
				result = userInput.nextLine(); //gets user input
				
				result = result.trim().substring(0, 1).toLowerCase(); //trim, cut & format input
				
				//System.out.println("received: " + result);
				
				if(result.equals("y")) { //if the main player decides to draw
					
					mainSequence.drawCard(you); //give the main player a card
					
					System.out.println(mainSequence.determineDrawnCard(you, drawingRound)); //tells the main player what card they drew
					
					drawingRound += 1; //increments round of drawing
					mainSequence.calculateHandSum(you); //recalculates hand sum
					
					System.out.println("Your hand total is now " + you.handSum + "\n"); //tells player their hand sum in the Console
					
				}
				
				else if(result.equals("n")) { //if the player doesn't want to draw
					
					System.out.println("Are you done drawing for good? y/n"); //ask them if they want to stand
					
					result = userInput.nextLine(); //gets user input
					
					result = result.trim().substring(0, 1).toLowerCase(); //trim, cut & format input
					
					if(result.equals("y")){
						you.doneDrawing = true; //ends player drawing
					}
					
				}

			}

			if((you.handSum > constants.POINT_CAP) && !you.donePlaying) { //determines if the main player is over the points cap
				you.donePlaying = true; //changes the main player's properties
				System.out.println("You're over 21 points! \n"); 
			}
			
			
			/*
			 * *****************
			 *  OPPONENT'S TURN
			 * *****************
			 */
			
			if(opponent.firstDraw) { //the hand sum needs to be calculated at least 1 time before the algorithm is called					
				mainSequence.calculateHandSum(opponent); //recalculates hand sum before first series of decision making
				opponent.firstDraw = false; //ensure this is not called again
			}
			
			if(opponent.makeChoice(0.40, you, dealer)){ //dealer makes his call at risk level given	
				
				mainSequence.drawCard(opponent); //gives CPU a card based on decision making algorithm
				System.out.println("Your opponent chooses to hit. \n");
					
			} 
			
			else if(!opponent.doneDrawing && !opponent.donePlaying){ //if CPU is not done drawing and not done playing 
				
				opponent.doneDrawing = true; //the CPU is done drawing
				System.out.println("Your opponent chooses to stand. \n"); 
				
			}
			
			mainSequence.calculateHandSum(opponent); //recalculates hand sum before next series of decision making - this can't be done prior to draw due to Ace logic
			
			if((opponent.handSum > constants.POINT_CAP) && ! opponent.donePlaying) { //if the CPU is over the point cap and isn't already done playing
				
				System.out.println("Opponent went over 21 points. \n");
				opponent.donePlaying = true; //the CPU has gone bust
				System.out.println(mainSequence.showCards(constants.ALL_CARDS, opponent)); //reveals the CPU's hand
					
			}
			
			
			/*
			 * *****************
			 *  DEALER'S TURN
			 * *****************
			 */

			if(opponent.donePlaying && you.donePlaying) {  //determines if the dealer even needs to draw	
				System.out.println("Both players are over 21! Dealer wins the whole pot."); 
			}
			
			if(dealer.firstDraw) { //the hand sum needs to be calculated at least 1 time before the algorithm is called					
				mainSequence.calculateHandSum(opponent); //recalculates hand sum before first series of decision making
				dealer.firstDraw = false; //ensure this is not called again
			}

			else if((opponent.doneDrawing || opponent.donePlaying) && (you.doneDrawing || you.donePlaying)) { //if at least 1 Player is still in the game
				
				if((dealer.handSum < 17)) { //if the dealer has less than 17
					
					if(!dealer.doneDrawing) { //needed to be added to prevent infinite looping
						mainSequence.drawCard(dealer); //the dealer must draw a card
						System.out.println("The Dealer draws a card.");
					}
					
				}
			
				else { //if the dealer has more than 17
				
					dealer.doneDrawing = true; //he is done drawing
					System.out.println("The Dealer is done drawing. He shows you he has " +dealer.handSum+ ". \n");
				
					if(dealer.handSum > constants.POINT_CAP) { //determines if the dealer is over point cap
						
						dealer.donePlaying = true; //the dealer has gone bust
						System.out.println("Game over! The Dealer is over 21! \n");
						
					}
				}
				
				mainSequence.calculateHandSum(dealer); //recalculates hand sum before next draw
			
			}
			
			
			/*
			 * ****************************
			 *   FINAL PHASE: Main Player
			 * ****************************
			 */
			
			if((you.doneDrawing && dealer.doneDrawing) && (!you.donePlaying) &&(!dealer.donePlaying)) { //if Main Player and the Dealer are both done drawing and neither have gone bust
				
				System.out.println(mainSequence.showCards(constants.ALL_CARDS, dealer)); //reveal the Dealer's hand
				
				System.out.println("The Dealer has " + dealer.handSum + " and you have " + you.handSum +".\n"); //shows hand totals to User
				
				if(dealer.handSum > you.handSum) { //if the Dealer has a higher hand total than Main Player
					decisionString = " beats ";	//changes the middle of the sentence 
				}
				
				else if (you.handSum > dealer.handSum) { //if the Main Player has a higher hand total than Dealer
					decisionString = " loses to "; //changes the middle of the sentence 
				}
				else { //if they have the same hand total
					decisionString = " ties "; //changes the middle of the sentence 
				}
				
				you.donePlaying = true; //Main Player is now done playing
				
				//Combine the handSums and decision String into a single sentence.
				//Should read: "The dealer's 17 beats your 14." - or something of the like.
				
				System.out.println("The dealer's "+ dealer.handSum + decisionString + "your " + you.handSum + ".\n");
				
				decisionString = ""; //reset decision string
			}
			
			/*
			 * ****************************
			 *   FINAL PHASE: CPU Player
			 * ****************************
			 */
			
			if((opponent.doneDrawing && dealer.doneDrawing) && (!opponent.donePlaying) && (!dealer.donePlaying)) {//if CPU and the Dealer are both done drawing and neither have gone bust
				

				System.out.println("Your opponent reveals their hand."); //keeps the Main Player informed as to what's going on
				System.out.println(mainSequence.showCards(constants.ALL_CARDS, opponent));
				System.out.println("\n");
				
				if(dealer.handSum > opponent.handSum) { 
					decisionString = " beats ";	
				}
				
				else if (opponent.handSum > dealer.handSum) {
					decisionString = " loses to ";
				}
				else {
					decisionString = " ties ";
				}
				
				
				opponent.donePlaying = true;
				
				System.out.println("The dealer's " + dealer.handSum + decisionString + "your opponent's " + opponent.handSum + ".");
			
			}
			
			
			//The game is over if Main Player and the Opponent have had a Final Phase or have gone Bust, or if the Dealer went Bust.
			gameOver = (you.donePlaying && opponent.donePlaying) || dealer.donePlaying;
			
			

			/*
			 * ****************************
			 *   IMPORTANT: PLEASE READ
			 * ****************************
			 * 
			 * In the primary version of this program, this is where the User is asked if they would like to submit data to be saved.
			 * However, since that requires referencing local files on my Computer, that part of the program has been removed for privacy/functionality.
			 * 
			 */

			if(gameOver) { //if the main game sequence has ended
				
				System.out.println("\nThat's the Game! Press any Key Except 'e' to end the game. "); //just a bit of fun at the end 
				System.out.println("Pressing 'e' does nothing.");
				
				result = userInput.nextLine(); //gets user input
				
				result = result.trim().substring(0, 1).toLowerCase(); //trim, cut & format input 
				
				userInput.close(); //closes system reader
				
				if(result.equals("e")) {
					System.out.println("Really? I'm quite dissapointed in you. The game is still over, but now I'm sad.");		
				}
				
				else {
					System.out.println("Thanks for playing! Feel free to restart the program to play again.");
				}
				
				System.exit(0);
			}
				
		}
		
	}

}
