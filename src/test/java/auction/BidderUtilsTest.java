package auction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BidderUtils should ")
public class BidderUtilsTest {

    @Test
    @DisplayName("calculate base valuation properly")
    void calculateBaseValuationTest() {
        assertThat(BidderUtils.calculateBaseValuation(10, 100)).isEqualTo(20);
        assertThat(BidderUtils.calculateBaseValuation(20, 500)).isEqualTo(50);
        assertThat(BidderUtils.calculateBaseValuation(6, 47)).isEqualTo(15);
    }

    @Test
    @DisplayName("calculate optimal valuation properly")
    void calculateOptimalValuationTest() {
        assertThat(BidderUtils.calculateOptimalValuation(10, 100)).isEqualTo(33);
        assertThat(BidderUtils.calculateOptimalValuation(30, 420)).isEqualTo(52);
        assertThat(BidderUtils.calculateOptimalValuation(8, 27)).isEqualTo(9);
    }

    @Test
    @DisplayName("generate a random integer")
    void getRandomIntTest() {
        int min = 1;
        int max = 10;

        for (int i = 0; i < 1000; i++) {
            assertThat(BidderUtils.getRandomInt(min, max))
                    .isGreaterThanOrEqualTo(min)
                    .isLessThan(max);
        }
    }

    @Test
    @DisplayName("not generate a random integer if min > max")
    void getRandomIntNegativeTest() {
        assertThatThrownBy(() -> BidderUtils.getRandomInt(5, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
