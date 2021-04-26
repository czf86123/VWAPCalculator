package com.calculator.impl;

import com.calculator.Instrument;
import com.calculator.State;
import com.calculator.TwoWayPrice;

public class DefaultTwoWayPrice implements MutableTwoWayPrice {

    private Instrument instrument;
    private State state = State.FIRM;
    private double bidPrice = 0.0d;
    private double offerPrice = 0.0d;
    private double bidAmount = 0.0d;
    private double offerAmount = 0.0d;

    @Override
    public Instrument getInstrument() {
        return instrument;
    }

    @Override
    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }

    @Override
    public double getBidPrice() {
        return bidPrice;
    }

    @Override
    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }

    @Override
    public double getOfferPrice() {
        return offerPrice;
    }

    @Override
    public void setOfferPrice(double offerPrice) {
        this.offerPrice = offerPrice;
    }

    @Override
    public double getBidAmount() {
        return bidAmount;
    }

    @Override
    public void setBidAmount(double bidAmount) {
        this.bidAmount = bidAmount;
    }

    @Override
    public double getOfferAmount() {
        return offerAmount;
    }

    @Override
    public void setOfferAmount(double offerAmount) {
        this.offerAmount = offerAmount;
    }

    @Override
    public void copy(TwoWayPrice copyFrom) {
        this.instrument = copyFrom.getInstrument();
        this.state = copyFrom.getState();
        this.bidPrice = copyFrom.getBidPrice();
        this.offerPrice = copyFrom.getOfferPrice();
        this.bidAmount = copyFrom.getBidAmount();
        this.offerAmount = copyFrom.getOfferAmount();
    }

    @Override
    public String toString() {
        return "DefaultTwoWayPrice{" +
                "instrument=" + instrument +
                ", state=" + state +
                ", bidPrice=" + bidPrice +
                ", offerPrice=" + offerPrice +
                ", bidAmount=" + bidAmount +
                ", offerAmount=" + offerAmount +
                "}";
    }
}
