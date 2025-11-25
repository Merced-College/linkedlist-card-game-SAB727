//Shane Bettis
//11/25/2025

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

    // Calculate best hand total for Blackjack, treating Aces as 11 or 1
    public static int calculateHandTotal(LinkedList hand) {
        int total = 0;
        int aces = 0;
        Link current = hand.getFirstLink();
        while (current != null) {
            int v = current.cardLink.getCardValue();
            total += v;
            String name = current.cardLink.getCardName();
            if (name != null && name.equalsIgnoreCase("ace")) {
                aces++;
            }
            current = current.next;
        }
        // downgrade Aces from 11 to 1 as needed
        while (total > 21 && aces > 0) {
            total -= 10; // 11 -> 1 => -10
            aces--;
        }
        return total;
    }

    // Return a comma-separated string of card *values* in the hand
    public static String handValuesString(LinkedList hand) {
        StringBuilder sb = new StringBuilder();
        Link current = hand.getFirstLink();
        while (current != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(current.cardLink.getCardValue());
            current = current.next;
        }
        return sb.toString();
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

        // Deal two-card starting hands (Blackjack-style)
        int playerTotal = 0; // will be recalculated using the hand helper
        int dealerTotal = 0;

        // Give player two cards if possible
        for (int i = 0; i < 2; i++) {
            if (!deck.isEmpty()) {
                int idx = rnd.nextInt(deck.size());
                Card c = deck.removeAt(idx);
                playerHand.addLast(c);
            }
        }

        // Give dealer two cards if possible
        for (int i = 0; i < 2; i++) {
            if (!deck.isEmpty()) {
                int idx = rnd.nextInt(deck.size());
                Card c = deck.removeAt(idx);
                dealerHand.addLast(c);
            }
        }

        // compute totals applying Ace logic
        playerTotal = calculateHandTotal(playerHand);
        dealerTotal = calculateHandTotal(dealerHand);

        // Display initial cards
        printDelay("=== BLACKJACK ===");
        printDelay("Player's hand:");
        String playerVals = handValuesString(playerHand);
        if (!playerVals.isEmpty()) {
            printDelay(playerVals);
            printDelay("Player total: " + playerTotal);
        } else {
            printDelay("No card to draw (deck empty).");
        }

        printDelay("");
        printDelay("Dealer's card:");
        // show only the dealer's first card to start
        Link dealerFirst = dealerHand.getFirstLink();
        if (dealerFirst != null) printDelay(String.valueOf(dealerFirst.cardLink.getCardValue()));
        else printDelay("No card to draw (deck empty).");

        printDelay("");

        // Check for immediate Blackjack (21) after initial two-card deal
        if (playerTotal == 21 || dealerTotal == 21) {
            if (playerTotal == 21 && dealerTotal != 21) {
                printDelay("Blackjack! You win!");
            } else if (dealerTotal == 21 && playerTotal != 21) {
                printDelay("Dealer has Blackjack. Dealer wins.");
            } else {
                printDelay("Both have Blackjack — push (tie).");
            }
            scanner.close();
            return;
        }

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
                // recompute player total (handles Aces properly)
                playerTotal = calculateHandTotal(playerHand);
                printDelay("Player total: " + playerTotal);
                if (playerTotal > 21) {
                    // player busted — stop asking
                    break;
                }
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
            // reveal dealer hand and total
            String dealerVals = handValuesString(dealerHand);
            printDelay("Dealer hand: " + dealerVals);
            printDelay("Dealer total: " + dealerTotal);
            // Dealer simple rule: hit until reaching 17 or higher
            while (dealerTotal < 17 && !deck.isEmpty()) {
                int idx = rnd.nextInt(deck.size());
                Card newCard = deck.removeAt(idx);
                dealerHand.addLast(newCard);
                printDelay("Dealer drew: " + newCard.getCardValue());
                // recompute dealer total (handles Aces properly)
                dealerTotal = calculateHandTotal(dealerHand);
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
