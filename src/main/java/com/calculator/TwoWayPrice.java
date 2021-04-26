package com.calculator;

/**
 * Two-way price for certain instrument
 */
public interface TwoWayPrice {

    Instrument getInstrument();

    State getState();

    double getBidPrice();

    double getBidAmount();

    double getOfferPrice();

    double getOfferAmount();

}
