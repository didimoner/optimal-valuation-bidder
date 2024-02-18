package auction;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static auction.Constants.PRODUCT_QUANTITY_PER_ROUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BidderBot should ")
public class OptimalValuationBidderTest {

    private Bidder bidder;


    @BeforeEach
    void beforeEach() {
        bidder = new OptimalValuationBidder();
    }

    @Test
    @DisplayName("require initialization to work")
    void noInitTest() {
        assertThatThrownBy(bidder::placeBid)
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> bidder.bids(1, 1))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("fail to init if product quantity is less that the default stack size")
    void initNoProductNegativeTest() {
        assertThatThrownBy(() -> bidder.init(PRODUCT_QUANTITY_PER_ROUND - 1, 100))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("fail to init if cash is 0")
    void initNoCacheNegativeTest() {
        assertThatThrownBy(() -> bidder.init(10, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("bid half of the valuation on the first bid")
    void firstBidTest() {
        bidder.init(10, 100);
        assertThat(bidder.placeBid()).isEqualTo(16);
    }

    @Test
    @DisplayName("bid zero if balance is zero")
    void zeroBalanceBidTest() {
        bidder.init(10, 100);
        bidder.bids(100, 20);

        assertThat(bidder.placeBid()).isEqualTo(0);
    }

    @Test
    @DisplayName("return a random bid between opponent's avg bid and own valuation")
    void randomBidTest() {
        int initialProductQuantity = 10;
        int initialCash = 100;
        bidder.init(initialProductQuantity, initialCash);

        List<Integer> opponentBids = Arrays.asList(20, 15, 18);
        opponentBids.forEach(b -> bidder.bids(1, b));
        int opponentAvgBid = (int) opponentBids.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0d);
        int ownValuation = BidderUtils.calculateOptimalValuation(initialProductQuantity, initialCash);

        for (int i = 0; i < 100; i++) {
            assertThat(bidder.placeBid())
                    .isGreaterThanOrEqualTo(opponentAvgBid)
                    .isLessThanOrEqualTo(ownValuation);
        }
    }

    @Test
    @DisplayName("bid zero if not enough cash to make a random bid")
    void zeroRandomBidTest() {
        int initialProductQuantity = 10;
        int initialCash = 100;
        bidder.init(initialProductQuantity, initialCash);

        List<Integer> opponentBids = Arrays.asList(20, 15, 18);
        opponentBids.forEach(b -> bidder.bids(1, b));
        bidder.bids(90, 0);

        for (int i = 0; i < 5; i++) {
            assertThat(bidder.placeBid()).isEqualTo(0);
        }
    }

    @Test
    @DisplayName("bid 1 if opponent's avg bid is greater than own valuation")
    void oneRandomBidTest() {
        bidder.init(10, 100);
        bidder.bids(1, 50);

        for (int i = 0; i < 5; i++) {
            assertThat(bidder.placeBid()).isEqualTo(1);
        }
    }

    @Test
    @DisplayName("use base valuation for bids if more than half rounds are already won")
    void baseValuationBidsTest() {
        int initialProductQuantity = 10;
        int initialCash = 100;
        bidder.init(initialProductQuantity, initialCash);

        List<Integer> opponentBids = Arrays.asList(2, 4, 3);
        opponentBids.forEach(b -> bidder.bids(5, b));
        int opponentAvgBid = (int) opponentBids.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0d);
        int ownValuation = BidderUtils.calculateBaseValuation(initialProductQuantity, initialCash);

        for (int i = 0; i < 100; i++) {
            assertThat(bidder.placeBid())
                    .isGreaterThanOrEqualTo(opponentAvgBid)
                    .isLessThanOrEqualTo(ownValuation);
        }
    }

    @Test
    @DisplayName("bid opponent's cash + 1 if their balance is less than own valuation")
    void lowOpponentCashBalanceBidTest() {
        bidder.init(10, 100);
        bidder.bids(1, 70);
        assertThat(bidder.placeBid()).isEqualTo(31);

        bidder.bids(15, 10);
        assertThat(bidder.placeBid()).isEqualTo(21);

        bidder.bids(20, 15);
        assertThat(bidder.placeBid()).isEqualTo(6);
    }

    @Test
    @DisplayName("bid exactly opponent's cash if their balance is less than own valuation " +
            "and own balance equals to opponent's cash balance")
    void lowOpponentCashBalanceTieBidTest() {
        bidder.init(10, 100);
        bidder.bids(70, 70);
        assertThat(bidder.placeBid()).isEqualTo(30);
    }

    @Test
    @DisplayName("bid opponent's cash + 1 if it is the last round to win")
    void ownLastRoundToWinBidTest() {
        bidder.init(10, 100);
        bidder.bids(17, 15);
        bidder.bids(20, 60);
        bidder.bids(15, 10);

        for (int i = 0; i < 5; i++) {
            assertThat(bidder.placeBid()).isEqualTo(16);
        }
    }

    @Test
    @DisplayName("bid opponent's cash + 1 if it is the last round for the opponent to win")
    void opponentLastRoundToWinBidTest() {
        bidder.init(10, 100);
        bidder.bids(15, 17);
        bidder.bids(15, 10);
        bidder.bids(20, 60);

        for (int i = 0; i < 5; i++) {
            assertThat(bidder.placeBid()).isEqualTo(14);
        }
    }

}
