package com.calculator.impl;

import com.calculator.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestDefaultVwapCalculator {

    private static final double EPSILON = 0.000001d;
    private DefaultVwapCalculator vwapCalculator;

    @Before
    public void setUp() {
        vwapCalculator = new DefaultVwapCalculator();
    }

    @Test
    public void testCalculatingVwapPriceFollowTheFormula() {
        // First update for instrument0 from market0
        MarketUpdate marketUpdate = createMarketDataUpdate(Market.MARKET0, Instrument.INSTRUMENT0, State.FIRM, 0.1, 0.2, 1000, 1000);
        TwoWayPrice twoWayPrice = vwapCalculator.applyMarketUpdate(marketUpdate);
        verifyTwoWayPrice(twoWayPrice, Instrument.INSTRUMENT0, State.FIRM, 0.1, 0.2, 1000, 1000);
        // Second update for instrument0 from market1
        marketUpdate = createMarketDataUpdate(Market.MARKET1, Instrument.INSTRUMENT0, State.FIRM, 0.1, 0.2, 1000, 1000);
        twoWayPrice = vwapCalculator.applyMarketUpdate(marketUpdate);
        verifyTwoWayPrice(twoWayPrice, Instrument.INSTRUMENT0, State.FIRM, 0.1, 0.2, 2000, 2000);
        // Third update for instrument0 from market49
        marketUpdate = createMarketDataUpdate(Market.MARKET49, Instrument.INSTRUMENT0, State.FIRM, 0.11, 0.21, 1000, 1000);
        twoWayPrice = vwapCalculator.applyMarketUpdate(marketUpdate);
        verifyTwoWayPrice(twoWayPrice, Instrument.INSTRUMENT0, State.FIRM, 0.103333, 0.203333, 3000, 3000);

        // First update for instrument19 from market10
        marketUpdate = createMarketDataUpdate(Market.MARKET10, Instrument.INSTRUMENT19, State.FIRM, 0.1, 0.2, 1000, 1000);
        twoWayPrice = vwapCalculator.applyMarketUpdate(marketUpdate);
        verifyTwoWayPrice(twoWayPrice, Instrument.INSTRUMENT19, State.FIRM, 0.1, 0.2, 1000, 1000);
        // Second update for instrument19 from market11
        marketUpdate = createMarketDataUpdate(Market.MARKET11, Instrument.INSTRUMENT19, State.FIRM, 0.1, 0.2, 1000, 1000);
        twoWayPrice = vwapCalculator.applyMarketUpdate(marketUpdate);
        verifyTwoWayPrice(twoWayPrice, Instrument.INSTRUMENT19, State.FIRM, 0.1, 0.2, 2000, 2000);
        // Third update for instrument19 from market41
        marketUpdate = createMarketDataUpdate(Market.MARKET41, Instrument.INSTRUMENT19, State.FIRM, 0.11, 0.21, 1000, 1000);
        twoWayPrice = vwapCalculator.applyMarketUpdate(marketUpdate);
        verifyTwoWayPrice(twoWayPrice, Instrument.INSTRUMENT19, State.FIRM, 0.103333, 0.203333, 3000, 3000);
    }

    @Test
    public void testOnlyTheLatestMarketUpdateTakesEffect() {
        // First update for instrument0 from market0
        MarketUpdate marketUpdate = createMarketDataUpdate(Market.MARKET0, Instrument.INSTRUMENT0, State.FIRM, 0.1, 0.2, 1000, 1000);
        TwoWayPrice twoWayPrice = vwapCalculator.applyMarketUpdate(marketUpdate);
        verifyTwoWayPrice(twoWayPrice, Instrument.INSTRUMENT0, State.FIRM, 0.1, 0.2, 1000, 1000);

        // Second update for instrument0 from market1
        marketUpdate = createMarketDataUpdate(Market.MARKET1, Instrument.INSTRUMENT0, State.FIRM, 0.1, 0.2, 1000, 1000);
        twoWayPrice = vwapCalculator.applyMarketUpdate(marketUpdate);
        verifyTwoWayPrice(twoWayPrice, Instrument.INSTRUMENT0, State.FIRM, 0.1, 0.2, 2000, 2000);

        // third update for instrument0 from market0 again, the first update shall no longer take effect
        marketUpdate = createMarketDataUpdate(Market.MARKET0, Instrument.INSTRUMENT0, State.FIRM, 0.11, 0.21, 1000, 1000);
        twoWayPrice = vwapCalculator.applyMarketUpdate(marketUpdate);
        verifyTwoWayPrice(twoWayPrice, Instrument.INSTRUMENT0, State.FIRM, 0.105, 0.205, 2000, 2000);
    }

    @Test
    public void testResultIsIndicativeIfAnyMarketUpdateIsIndicative() {
        // First update for instrument0 from market0, state is firm
        MarketUpdate marketUpdate = createMarketDataUpdate(Market.MARKET0, Instrument.INSTRUMENT0, State.FIRM, 0.1, 0.2, 1000, 1000);
        TwoWayPrice twoWayPrice = vwapCalculator.applyMarketUpdate(marketUpdate);
        verifyTwoWayPrice(twoWayPrice, Instrument.INSTRUMENT0, State.FIRM, 0.1, 0.2, 1000, 1000);

        // Second update for instrument0 from market1, state is indicative
        marketUpdate = createMarketDataUpdate(Market.MARKET1, Instrument.INSTRUMENT0, State.INDICATIVE, 0.1, 0.2, 1000, 1000);
        twoWayPrice = vwapCalculator.applyMarketUpdate(marketUpdate);
        verifyTwoWayPrice(twoWayPrice, Instrument.INSTRUMENT0, State.INDICATIVE, 0.1, 0.2, 2000, 2000);

        // third update for instrument0 from market0 again, state is indicative
        marketUpdate = createMarketDataUpdate(Market.MARKET0, Instrument.INSTRUMENT0, State.INDICATIVE, 0.11, 0.21, 1000, 1000);
        twoWayPrice = vwapCalculator.applyMarketUpdate(marketUpdate);
        verifyTwoWayPrice(twoWayPrice, Instrument.INSTRUMENT0, State.INDICATIVE, 0.105, 0.205, 2000, 2000);

        // Fourth update for instrument0 from market1, state is firm, but expect the result is still indicative
        marketUpdate = createMarketDataUpdate(Market.MARKET1, Instrument.INSTRUMENT0, State.FIRM, 0.11, 0.21, 1000, 1000);
        twoWayPrice = vwapCalculator.applyMarketUpdate(marketUpdate);
        verifyTwoWayPrice(twoWayPrice, Instrument.INSTRUMENT0, State.INDICATIVE, 0.11, 0.21, 2000, 2000);

        // third update for instrument0 from market0 again, state is firm, expect the result state changes back to firm
        marketUpdate = createMarketDataUpdate(Market.MARKET0, Instrument.INSTRUMENT0, State.FIRM, 0.1, 0.2, 1000, 1000);
        twoWayPrice = vwapCalculator.applyMarketUpdate(marketUpdate);
        verifyTwoWayPrice(twoWayPrice, Instrument.INSTRUMENT0, State.FIRM, 0.105, 0.205, 2000, 2000);
    }

    /**
     * Just for reference of the exercise, System output is:
     * Invalid market update - MockedMarketUpdate{market=null, twoWayPrice=DefaultTwoWayPrice{instrument=INSTRUMENT0, state=FIRM, bidPrice=0.1, offerPrice=0.2, bidAmount=1000.0, offerAmount=1000.0}}
     * Invalid market update - MockedMarketUpdate{market=MARKET0, twoWayPrice=null}
     * Invalid market update - MockedMarketUpdate{market=MARKET0, twoWayPrice=DefaultTwoWayPrice{instrument=null, state=FIRM, bidPrice=0.1, offerPrice=0.2, bidAmount=1000.0, offerAmount=1000.0}}
     * Invalid market update - MockedMarketUpdate{market=MARKET0, twoWayPrice=DefaultTwoWayPrice{instrument=INSTRUMENT0, state=null, bidPrice=0.1, offerPrice=0.2, bidAmount=1000.0, offerAmount=1000.0}}
     * Invalid market update - MockedMarketUpdate{market=MARKET0, twoWayPrice=DefaultTwoWayPrice{instrument=INSTRUMENT0, state=FIRM, bidPrice=NaN, offerPrice=0.2, bidAmount=1000.0, offerAmount=1000.0}}
     * Invalid market update - MockedMarketUpdate{market=MARKET0, twoWayPrice=DefaultTwoWayPrice{instrument=INSTRUMENT0, state=FIRM, bidPrice=0.1, offerPrice=0.0, bidAmount=1000.0, offerAmount=1000.0}}
     * Invalid market update - MockedMarketUpdate{market=MARKET0, twoWayPrice=DefaultTwoWayPrice{instrument=INSTRUMENT0, state=FIRM, bidPrice=0.1, offerPrice=0.2, bidAmount=-1000.0, offerAmount=1000.0}}
     * Invalid market update - MockedMarketUpdate{market=MARKET0, twoWayPrice=DefaultTwoWayPrice{instrument=INSTRUMENT0, state=FIRM, bidPrice=0.1, offerPrice=0.2, bidAmount=1000.0, offerAmount=NaN}}
     */
    @Test
    public void testInvalidMarketUpdateReturnNull() {
        // Input market is null
        MarketUpdate marketUpdate = createMarketDataUpdate(null, Instrument.INSTRUMENT0, State.FIRM, 0.1, 0.2, 1000, 1000);
        TwoWayPrice twoWayPrice = vwapCalculator.applyMarketUpdate(marketUpdate);
        assertNull(twoWayPrice);

        // Input TwoWayPrice is null
        marketUpdate = new MockedMarketUpdate(Market.MARKET0, null);
        twoWayPrice = vwapCalculator.applyMarketUpdate(marketUpdate);
        assertNull(twoWayPrice);

        // Input instrument is null
        marketUpdate = createMarketDataUpdate(Market.MARKET0, null, State.FIRM, 0.1, 0.2, 1000, 1000);
        twoWayPrice = vwapCalculator.applyMarketUpdate(marketUpdate);
        assertNull(twoWayPrice);

        // Input state is null
        marketUpdate = createMarketDataUpdate(Market.MARKET0, Instrument.INSTRUMENT0, null, 0.1, 0.2, 1000, 1000);
        twoWayPrice = vwapCalculator.applyMarketUpdate(marketUpdate);
        assertNull(twoWayPrice);

        // Input bidPrice is NaN
        marketUpdate = createMarketDataUpdate(Market.MARKET0, Instrument.INSTRUMENT0, State.FIRM, Double.NaN, 0.2, 1000, 1000);
        twoWayPrice = vwapCalculator.applyMarketUpdate(marketUpdate);
        assertNull(twoWayPrice);

        // Input offerPrice is 0
        marketUpdate = createMarketDataUpdate(Market.MARKET0, Instrument.INSTRUMENT0, State.FIRM, 0.1, 0.0, 1000, 1000);
        twoWayPrice = vwapCalculator.applyMarketUpdate(marketUpdate);
        assertNull(twoWayPrice);

        // Input bidAmount is negative
        marketUpdate = createMarketDataUpdate(Market.MARKET0, Instrument.INSTRUMENT0, State.FIRM, 0.1, 0.2, -1000, 1000);
        twoWayPrice = vwapCalculator.applyMarketUpdate(marketUpdate);
        assertNull(twoWayPrice);

        // Input offerAmount is NaN
        marketUpdate = createMarketDataUpdate(Market.MARKET0, Instrument.INSTRUMENT0, State.FIRM, 0.1, 0.2, 1000, Double.NaN);
        twoWayPrice = vwapCalculator.applyMarketUpdate(marketUpdate);
        assertNull(twoWayPrice);
    }

    private MarketUpdate createMarketDataUpdate(Market market,
                                                Instrument instrument,
                                                State state,
                                                double bidPrice,
                                                double offerPrice,
                                                double bidAmount,
                                                double offerAmount) {
        DefaultTwoWayPrice twoWayPrice = new DefaultTwoWayPrice();
        twoWayPrice.setInstrument(instrument);
        twoWayPrice.setState(state);
        twoWayPrice.setBidPrice(bidPrice);
        twoWayPrice.setOfferPrice(offerPrice);
        twoWayPrice.setBidAmount(bidAmount);
        twoWayPrice.setOfferAmount(offerAmount);

        return new MockedMarketUpdate(market, twoWayPrice);
    }

    private void verifyTwoWayPrice(TwoWayPrice twoWayPrice,
                                   Instrument instrument,
                                   State state,
                                   double bidPrice,
                                   double offerPrice,
                                   double bidAmount,
                                   double offerAmount) {
        assertEquals(instrument, twoWayPrice.getInstrument());
        assertEquals(state, twoWayPrice.getState());
        assertEquals(bidPrice, twoWayPrice.getBidPrice(), EPSILON);
        assertEquals(offerPrice, twoWayPrice.getOfferPrice(), EPSILON);
        assertEquals(bidAmount, twoWayPrice.getBidAmount(), EPSILON);
        assertEquals(offerAmount, twoWayPrice.getOfferAmount(), EPSILON);
    }
}
