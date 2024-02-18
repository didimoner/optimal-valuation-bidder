package auction;

import java.util.ArrayList;
import java.util.List;

import static auction.Constants.PRODUCT_QUANTITY_PER_ROUND;

/**
 * Custom implementation of the Bidder that uses strategy with valuation.
 */
public class OptimalValuationBidder implements Bidder {

    private static final int DEFAULT_SAVE_BID = 0;


    private boolean initialized = false;
    private int cashBalance;
    private int totalRounds;
    private int totalCash;
    private int baseValuation;
    private int optimalValuation;
    private int wonRounds;
    private int opponentWonRounds;
    private final List<Integer> opponentBids = new ArrayList<>();


    @Override
    public synchronized void init(int quantity, int cash) {
        if (quantity < PRODUCT_QUANTITY_PER_ROUND) {
            throw new IllegalArgumentException("Insufficient product quantity");
        }
        if (cash <= 0) {
            throw new IllegalArgumentException("Insufficient cash");
        }

        this.cashBalance = cash;
        this.totalCash = cash;

        this.opponentBids.clear();

        this.wonRounds = 0;
        this.opponentWonRounds = 0;
        this.totalRounds = quantity / PRODUCT_QUANTITY_PER_ROUND;
        this.baseValuation = BidderUtils.calculateBaseValuation(quantity, cash);
        this.optimalValuation = BidderUtils.calculateOptimalValuation(quantity, cash);

        this.initialized = true;
    }

    @Override
    public synchronized int placeBid() {
        checkInitialization();

        if (this.cashBalance == 0) {
            return 0;
        }

        if (this.opponentBids.isEmpty()) {
            return this.optimalValuation / 2;
        }

        int minRoundsToWin = getMinRoundsToWin();
        int valuation = this.wonRounds < minRoundsToWin
                ? this.optimalValuation
                : this.baseValuation;
        int opponentBidSum = this.opponentBids.stream()
                .mapToInt(Integer::intValue)
                .sum();
        int opponentCashBalance = this.totalCash - opponentBidSum;

        if (opponentCashBalance < valuation
                || this.wonRounds == minRoundsToWin - 1
                || this.opponentWonRounds == minRoundsToWin - 1) {
            return getBidOrSave(opponentCashBalance + 1, opponentCashBalance);
        }

        int averageOpponentBid = opponentBidSum / this.opponentBids.size();
        if (averageOpponentBid <= valuation) {
            int randomBid = BidderUtils.getRandomInt(averageOpponentBid, valuation + 1);
            return this.cashBalance >= randomBid ? randomBid : DEFAULT_SAVE_BID;
        }

        return 1;
    }

    @Override
    public synchronized void bids(int own, int other) {
        checkInitialization();

        if (own < 0 || other < 0) {
            throw new IllegalArgumentException("Bids cannot be negative");
        }

        if (own > other) {
            this.wonRounds++;
        } else {
            this.opponentWonRounds++;
        }

        this.cashBalance -= own;
        this.opponentBids.add(other);
    }


    private void checkInitialization() {
        if (!this.initialized) {
            throw new IllegalStateException("The bidder is not initialized");
        }
    }

    private int getMinRoundsToWin() {
        return this.totalRounds / 2 + 1;
    }

    private int getBidOrSave(int winBid, int tieBid) {
        if (this.cashBalance >= winBid) {
            return winBid;
        } else if (this.cashBalance >= tieBid) {
            return tieBid;
        }
        return DEFAULT_SAVE_BID;
    }

}
