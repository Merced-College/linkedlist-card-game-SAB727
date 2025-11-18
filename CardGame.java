//package linkedLists;

//package linkedLists;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class CardGame {

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
            System.out.println("Deck is empty. Ensure cards.txt is present and formatted correctly.");
            return;
        }

        Random rnd = new Random();

        // Draw random card for player
        Card playerCard = null;
        if (!deck.isEmpty()) {
            int idx = rnd.nextInt(deck.size());
            playerCard = deck.removeAt(idx);
        }

        System.out.println("Player drew:");
        if (playerCard != null) System.out.println(playerCard);
        else System.out.println("No card to draw (deck empty).");

        System.out.println();

        // Draw random card for dealer
        Card dealerCard = null;
        if (!deck.isEmpty()) {
            int idx = rnd.nextInt(deck.size());
            dealerCard = deck.removeAt(idx);
        }

        System.out.println("Dealer drew:");
        if (dealerCard != null) System.out.println(dealerCard);
        else System.out.println("No card to draw (deck empty).");
    }

}
