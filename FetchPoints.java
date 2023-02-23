
///////////////////////////////////////////////////////////////////////////////
//
// Fetch Coding Exercise - Software Engineering Internship
// 
// Author:   Thomas Lesniak
// Email:    tjlesniak@wisc.edu
// 
///////////////////////////////////////////////////////////////////////////////
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * This class reads through the transactions from a CSV file containing a payer,
 * number of points, and timestamp of the transaction. The data is used
 * to assign a payer to each point earned by the user. Points are then spent in
 * the order of the oldest timestamp (oldest transactions have first priority).
 */
public class FetchPoints {
    // the number of points the user is spending
    private static int spending;
    // a map containing each payer's total
    protected static HashMap<String, Integer> payerBalance = new HashMap<>();
    // a queue which will keep track of the payer's in the order of their timestamps
    protected static PriorityQueue<Transaction> orderedTransactions = new PriorityQueue<>();

    /**
     * Main Method
     * 
     * @param args - user inputted command line arguments which include amount
     *             to spend and the CSV file to read from.
     */
    public static void main(String[] args) {
        // check for valid input
        if (args.length != 2) {
            System.out.println("Oops! Sorry I didn't understand that. Please enter \n" +
                    "the number of points you want to spend with the path to the CSV file \n" +
                    "containing the transactions.");
            return;
        }

        try {
            // ensures that user input is a valid integer
            spending = Integer.parseInt(args[0]);
        } catch (NumberFormatException e1) {
            System.out.println("Oops! Looks Like you didn't include a valid number." +
                    "Please Try Again");
        }

        String csvFile = args[1]; // file to read passed as second argument

        // parsing the CSV file
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            Loop: while ((line = br.readLine()) != null) {
                // splits up the columns of the CSV file into an array of strings
                String[] transactionData = line.split(",");
                // ensures proper format of csv file
                if (transactionData.length != 3 || !transactionData[1].matches(("-?\\d+"))) {
                    continue Loop;
                }
                // Stores Payer, Points, and Timestamp into an object called transaction
                Transaction transaction = new Transaction(transactionData[0],
                        transactionData[1], transactionData[2]);
                // input the the payer into the hashmap. If key already exists, then update
                // value with the sum of points
                payerBalance.merge(transaction.payer, transaction.points, Integer::sum);
                // The payer is added into a prioirty queue where the order is sorted
                // with the transaction with the oldest timestamp first
                orderedTransactions.add(transaction);

            }
            // now that the data is ready, we can spend points in the order they were
            // recieved
            spendPoints();
            // prints the payer along with the updated point balance after user spending
            for (Map.Entry<String, Integer> table : payerBalance.entrySet()) {
                System.out.println(table.getKey() + ": " + table.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method spends oldest points while making sure that no payer balance goes
     * negative
     */
    private static void spendPoints() {
        while (!orderedTransactions.isEmpty() && spending > 0) {
            // The oldest transaction in the queue comes first
            Transaction nextTransaction = orderedTransactions.poll();

            String payer = nextTransaction.payer; // payer of this transaction
            int points = nextTransaction.points; // the amount of points in transaction
            int balance = payerBalance.get(payer); // the payer's balance

            // case 1
            // the transaction is larger than the payer's balance but they can afford the
            // spending
            if (points > balance && spending > balance) {
                // spend only the amount of points the payer has
                payerBalance.put(payer, 0);
                spending -= balance;
            }
            // case 2
            // the transaction is larger than the payer's balance and they cannot afford the
            // spending
            else if (points > balance && spending < balance) {
                payerBalance.put(payer, balance - spending);
                spending = 0;
            }
            // case 3
            // the next transaction can pay off the rest of the spending
            else if (points > spending && balance > spending) {
                payerBalance.merge(payer, -spending, Integer::sum);
                spending -= spending;
            } else {
                payerBalance.merge(payer, -points, Integer::sum);
                spending -= points;
            }
        }
        return;
    }

    /**
     * Transaction class for storing data of the CSV file into a single object.
     * Implements the comparable interface for use in priority queue
     */
    private static class Transaction implements Comparable<Transaction> {
        protected String payer; // responsible for paying the points
        protected int points; // the amoint of points in the transaction
        protected String timestamp; // a timestamp of the transaction

        /**
         * Constructor of the Transaction class
         * 
         * @param payer
         * @param points    - need to be converted to an int
         * @param timestamp
         */
        public Transaction(String payer, String points, String timestamp) {
            this.payer = payer;
            this.points = Integer.parseInt(points);
            this.timestamp = timestamp;
        }

        /**
         * Overrides the compareTo method which is required for the comparable
         * interface. This allows Transactions to be sorted into a priority
         * queue with the oldest transaction first.
         */
        @Override
        public int compareTo(Transaction other) {
            if (this.timestamp.equals(other.timestamp)) {
                return 0;
            } else if (this.timestamp.compareTo(other.timestamp) < 0) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
