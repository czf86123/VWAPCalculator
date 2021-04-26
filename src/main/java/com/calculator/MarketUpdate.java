package com.calculator;

/**
 * Market two-way price update for certain instrument
 */
public interface MarketUpdate {

    Market getMarket();

    TwoWayPrice getTwoWayPrice();

}
