package com.calculator.impl;

import com.calculator.Instrument;
import com.calculator.State;
import com.calculator.TwoWayPrice;

/**
 * An extended mutable interface from {@link TwoWayPrice} to allow modifying of the price properties.
 */
public interface MutableTwoWayPrice extends TwoWayPrice {

    void setInstrument(Instrument instrument);

    void setState(State state);

    void setBidPrice(double bidPrice);

    void setOfferPrice(double offerPrice);

    void setBidAmount(double bidAmount);

    void setOfferAmount(double offerAmount);

    void copy(TwoWayPrice copyFrom);
}
