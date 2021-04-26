package com.calculator.impl;

import com.calculator.Market;
import com.calculator.MarketUpdate;
import com.calculator.TwoWayPrice;

/**
 * Simple implementation for {@link MarketUpdate} for test use
 */
public class MockedMarketUpdate implements MarketUpdate {

    Market market;
    MutableTwoWayPrice twoWayPrice;

    MockedMarketUpdate(Market market, MutableTwoWayPrice twoWayPrice){
        this.market = market;
        this.twoWayPrice = twoWayPrice;
    }

    @Override
    public Market getMarket() {
        return market;
    }

    @Override
    public TwoWayPrice getTwoWayPrice() {
        return twoWayPrice;
    }

    @Override
    public String toString() {
        return "MockedMarketUpdate{" +
                "market=" + market +
                ", twoWayPrice=" + twoWayPrice +
                "}";
    }
}
