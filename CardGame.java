//package linkedLists;

//package linkedLists;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class CardGame {

    // Helper to print a line then pause 1 second
    private static void printDelay(String s) {
        System.out.println(s);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        String fileName = "cards.txt";

        // Load cards into our LinkedList deck (preserve file order)
        LinkedList deck = new LinkedList();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details.length == 4) {
                    String suit = details[0].trim();
                    String name = details[1].trim();
                    int value = Integer.parseInt(details[2].trim());
                    String pic = details[3].trim();
                    Card card = new Card(suit, name, value, pic);
                    deck.addLast(card);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return;
        }

        if (deck.isEmpty()) {
            printDelay("Deck is empty. Ensure cards.txt is present and formatted correctly.");
            return;
        }

        Random rnd = new Random();
        Scanner scanner = new Scanner(System.in);

        // Track hands
        LinkedList playerHand = new LinkedList();
        LinkedList dealerHand = new LinkedList();

        // Draw initial card for player
        Card playerCard = null;
        // running player total (keeps cumulative value across hits)
        int playerTotal = 0;
        if (!deck.isEmpty()) {
            int idx = rnd.nextInt(deck.size());
            playerCard = deck.removeAt(idx);
            playerHand.addLast(playerCard);
            // initialize running total for player
            playerTotal = (playerCard != null) ? playerCard.getCardValue() : 0;
        }

        // Draw initial card for dealer
        Card dealerCard = null;
        // running dealer total
        int dealerTotal = 0;
        if (!deck.isEmpty()) {
            int idx = rnd.nextInt(deck.size());
            dealerCard = deck.removeAt(idx);
            dealerHand.addLast(dealerCard);
            dealerTotal = (dealerCard != null) ? dealerCard.getCardValue() : 0;
        }

        // Display initial cards
        printDelay("=== BLACKJACK ===");
        printDelay("Player's hand:");
        if (playerCard != null) {
            printDelay(String.valueOf(playerCard.getCardValue()));
            printDelay("Player total: " + playerTotal);
        }
        else System.out.println("No card to draw (deck empty).");

        printDelay("");
        printDelay("Dealer's card:");
            if (dealerCard != null) printDelay(String.valueOf(dealerCard.getCardValue()));
        else System.out.println("No card to draw (deck empty).");

        printDelay("");

        // Player hit/stand loop
        boolean playerStanding = false;
        while (!playerStanding && !deck.isEmpty()) {
            System.out.print("Do you want to (H)it or (S)tand? ");
            String choice = scanner.nextLine().trim().toUpperCase();

            if (choice.equals("H")) {
                int idx = rnd.nextInt(deck.size());
                Card newCard = deck.removeAt(idx);
                playerHand.addLast(newCard);
                printDelay("You drew: " + newCard.getCardValue());
                // add drawn card value to running player total and display updated total
                playerTotal += newCard.getCardValue();
                printDelay("Player total: " + playerTotal);
            } else if (choice.equals("S")) {
                playerStanding = true;
                printDelay("You stand.");
            } else {
                printDelay("Invalid choice. Please enter H or S.");
            }
        }

        // After player stands or deck empties, run dealer logic if player hasn't busted
        if (playerTotal <= 21) {
            printDelay("");
            printDelay("Dealer's turn:");
            printDelay("Dealer total: " + dealerTotal);
            // Dealer simple rule: hit until reaching 17 or higher
            while (dealerTotal < 17 && !deck.isEmpty()) {
                int idx = rnd.nextInt(deck.size());
                Card newCard = deck.removeAt(idx);
                dealerHand.addLast(newCard);
                printDelay("Dealer drew: " + newCard.getCardValue());
                dealerTotal += newCard.getCardValue();
                printDelay("Dealer total: " + dealerTotal);
            }

            // Decide winner
            printDelay("");
            printDelay("Final totals -> Player: " + playerTotal + " Dealer: " + dealerTotal);
            if (dealerTotal > 21) {
                printDelay("Dealer busts. You win!");
            } else if (playerTotal > dealerTotal) {
                printDelay("You win!");
            } else if (playerTotal < dealerTotal) {
                printDelay("Dealer wins.");
            } else {
                printDelay("It's a tie (push).");
            }
        } else {
            printDelay("");
            printDelay("You busted with " + playerTotal + ". Dealer wins.");
        }

        scanner.close();
    }

}
