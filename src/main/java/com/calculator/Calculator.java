package com.calculator;

/**
 *  The VWAP twp way price calculator for instrument of the {@link MarketUpdate} passed in
 */
public interface Calculator {

    /**
     * @param twoWayMarketPrice is the MarketUpdate input
     * @return The VWAP twp way price calculator for instrument of the input
     */
    TwoWayPrice applyMarketUpdate(final MarketUpdate twoWayMarketPrice);

}
