package auction;

import java.util.Random;

import static auction.Constants.PRODUCT_QUANTITY_PER_ROUND;

/**
 * Set of useful utils for the bidder.
 */
public class BidderUtils {

    private static final Random RANDOM = new Random();


    /**
     * Calculates base valuation for a set of product according to total cash and product quantity.
     *
     * @param quantity product quantity
     * @param cash     available cash
     * @return valuation
     */
    public static int calculateBaseValuation(int quantity, int cash) {
        return cash * PRODUCT_QUANTITY_PER_ROUND / quantity;
    }

    /**
     * Calculates optimal valuation for a set of product based on minimal number of rounds to win.
     *
     * @param quantity product quantity
     * @param cash     available cash
     * @return valuation
     */
    public static int calculateOptimalValuation(int quantity, int cash) {
        return cash / (quantity / (PRODUCT_QUANTITY_PER_ROUND * 2) + 1);
    }

    /**
     * Gets a random integer number within min(inclusive) and max(exclusive).
     *
     * @param min min number (inclusive)
     * @param max max number (exclusive)
     * @return a random int
     */
    public static int getRandomInt(int min, int max) {
        if (max <= min) {
            throw new IllegalArgumentException("Max has to be greater than Min");
        }

        return RANDOM.nextInt(max - min) + min;
    }

}
