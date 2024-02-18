# First-price auction optimal valuation bidder
This repository contains implementation of the **Bidder** interface provided as a test task by **Optimax Energy**.

## The strategy

### Legend
**M** - money;  
**Q** - product quantity;  
**V** - valuation;  
**AVG** - opponent's average bid *(b1+b2+...+bn)/n*.

### Explanation
The bidder is based on the concept of valuation of a product.  

V of a product pack (two items of Q) should be `2M/Q`, but there is a condition: get more product than the opponent to win.
Based on that, the strategy is to spend M to win **HALF+1 rounds** in order to get more Q that the opponent.
Since the product is auctioned always by 2 items, then total rounds number is `Q/2`.
So, I should distribute M for `Q/4+1` of Q/2 total rounds.  
**Optimal valuation** to win: `M/(Q/4 + 1)`  
*Decimal part if omitted (same as floor rounding).*

#### Main rules
1. Avoid bidding more than valuation in order to get profit.
2. Avoid unnecessary spending when possible, especially in situations with low chance to win.
3. Not enough money to make an optimal bid - save money (bid 0).

#### Bids
**The first bid** is `V/2`, because if win then net earning is V/2, if loss then I saved V/2 of M for future bids.

Then, define what valuation to use: optimal or base. **Optimal valuation** is used when target number of products is not yet
acquired. Otherwise, use  **base valuation** to get a chance to win product surplus and save money.

**The default bid** is a random number between AVG and V if `AVG < V`.  
Otherwise, bid 1 (just to counter opponent's possible zero bid).
If there is not enough cash to cover the bid, save money (bid 0).

**Counter bid** takes place when the opponent has less cash balance than my current valuation. It makes sense to bid 
`opponent's M + 1` in order to definitely win the round.  
If own M is equal to opponent's M, bid the whole amount to get a tie.
Finally, if there is not enough M to cover the bid, save money (bid 0).

**Last chance bid** is used when there is only one round left to win or lose. It secures victory in case of winning and 
avoids instant losing in the other case.  
The algorithm is the same as for the counter bid. 

## Implementation notes

### Synchronization
I decided to synchronize all bidder's methods because there is no information about the target environment.
If it's single-threaded - no problem, the app will work without issues (except slight performance because of locks), 
otherwise each method executes as an atomic operation which eliminates mid-state situations.

### Validation
There are multiple places where I've added validation checks for incoming arguments. There is no information about it in 
the document, but it's logical to have them (in my opinion) to exclude potential calculation problems.

I also assume that auction stops when the product is out of stock.

## Used tools
- **Java 17**;
- **Maven** to build the project and perform other useful operations from project's lifecycle;
- **JUnit 5** for testing;
- **AssertJ** for more advanced assertions in tests;
- **Checkstyle** for code style checks;
- **PMD** and **Spotbugs** for static code analysis.

## Building the project
The application is based on **Maven**.
Run the following code to fire all checks and tests in the project:  
`$ mvn clean verify`

To build a jar use this command:  
`$ mvn clean package`  
Once it's done, navigate to the "target" directory and search for the **.jar** file.