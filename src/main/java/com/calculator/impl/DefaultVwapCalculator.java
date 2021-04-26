package com.calculator.impl;

import com.calculator.*;

/**
 * Default implementation of VWAP two-way price for input {@link MarketUpdate}
 *
 * For any given market, only the most recent price should be included in the VWAP calculation.
 * The process calling applyMarketUpdate() must be single threaded.
 * If any one MarketUpdate used in deriving the VWAP is indicative, the calculated TwoWayPrice should also be marked as indicative, otherwise it is firm.
 *
 * The VWAP two-way price for an instrument is defined as:
 * Bid = Sum(Market Bid Price * Market Bid Amount)/ Sum(Market Bid Amount)
 * Offer = Sum(Market Offer Price * Market Offer Amount)/ Sum(Market Offer Amount)
 */
public class DefaultVwapCalculator implements Calculator {

    private final Instrument[] INSTRUMENTS = Instrument.values();
    private final SingleInstrumentVwapCalculator[] singleInstrumentCalculators = new SingleInstrumentVwapCalculator[INSTRUMENTS.length];
    private final StringBuilder errorBuilder = new StringBuilder();

    public DefaultVwapCalculator() {
        for (int i=0;i<singleInstrumentCalculators.length;i++) {
            singleInstrumentCalculators[i] = new SingleInstrumentVwapCalculator(INSTRUMENTS[i]);
        }
    }

    @Override
    public TwoWayPrice applyMarketUpdate(final MarketUpdate twoWayMarketPrice) {
        if (!isValidMarketUpdate(twoWayMarketPrice)){
            // Due to lack of context in this exercise so I simply handle the exception case with System.out.println().
            // And I assume the implementation of MarketUpdate has rewritten toString() properly.
            // In real world we can choose to log error/throw exception/raise system alert, etc., according to the context.
            errorBuilder.setLength(0);
            errorBuilder.append("Invalid market update - ").append(twoWayMarketPrice);
            System.out.println(errorBuilder.toString());
            return null;
        }
        Instrument instrument = twoWayMarketPrice.getTwoWayPrice().getInstrument();
        return singleInstrumentCalculators[instrument.ordinal()].calculate(twoWayMarketPrice);
    }

    /**
     * Validate the MarketUpdate. Consider price and amount shall always be positive. One-sided market is not valid.
     * @param twoWayMarketPrice
     * @return
     */
    boolean isValidMarketUpdate(MarketUpdate twoWayMarketPrice) {
        if (twoWayMarketPrice == null || twoWayMarketPrice.getMarket() == null) {
            return false;
        }

        TwoWayPrice price = twoWayMarketPrice.getTwoWayPrice();

        return price != null
                && price.getInstrument() != null
                && price.getState() != null
                && (!Double.isNaN(price.getBidPrice()) && price.getBidPrice() > 0.0d)
                && (!Double.isNaN(price.getOfferPrice()) && price.getOfferPrice() > 0.0d)
                && (!Double.isNaN(price.getBidAmount()) && price.getBidAmount() > 0.0d)
                && (!Double.isNaN(price.getOfferAmount()) && price.getOfferAmount() > 0.0d);
    }

    /**
     * The VWAP two-way price calculator for each instrument
     * Assume input {@link MarketUpdate} is validated and always on the same instrument
     */
    private class SingleInstrumentVwapCalculator {

        private final MutableTwoWayPrice result = new DefaultTwoWayPrice();
        // cache for market updates which are currently taking effect
        private final MutableTwoWayPrice[] marketPriceCache = new MutableTwoWayPrice[Market.values().length];

        private double bidPriceAmountSum = 0.0d;
        private double bidAmountSum = 0.0d;
        private double offerPriceAmountSum = 0.0d;
        private double offerAmountSum = 0.0d;
        // counter for indicative market, if positive then result state is INDICATIVE
        private int indicativeCounter = 0;

        SingleInstrumentVwapCalculator(Instrument instrument) {
            result.setInstrument(instrument);
            for (int i=0;i<marketPriceCache.length;i++) {
                marketPriceCache[i] = new DefaultTwoWayPrice();
            }
        }

        TwoWayPrice calculate(final MarketUpdate marketUpdate) {
            TwoWayPrice marketPriceUpdate = marketUpdate.getTwoWayPrice();
            MutableTwoWayPrice lastMarketPrice = marketPriceCache[marketUpdate.getMarket().ordinal()];

            bidAmountSum = bidAmountSum - lastMarketPrice.getBidAmount() + marketPriceUpdate.getBidAmount();
            offerAmountSum = offerAmountSum - lastMarketPrice.getOfferAmount() + marketPriceUpdate.getOfferAmount();
            bidPriceAmountSum = bidPriceAmountSum - lastMarketPrice.getBidPrice() * lastMarketPrice.getBidAmount()
                    + marketPriceUpdate.getBidPrice() * marketPriceUpdate.getBidAmount();
            offerPriceAmountSum = offerPriceAmountSum - lastMarketPrice.getOfferPrice() * lastMarketPrice.getOfferAmount()
                    + marketPriceUpdate.getOfferPrice() * marketPriceUpdate.getOfferAmount();
            if (marketPriceUpdate.getState() != lastMarketPrice.getState()) {
                indicativeCounter = marketPriceUpdate.getState() == State.INDICATIVE ? indicativeCounter + 1 : indicativeCounter - 1;
            }

            lastMarketPrice.copy(marketPriceUpdate);
            result.setBidPrice(bidPriceAmountSum/bidAmountSum);
            result.setOfferPrice(offerPriceAmountSum/offerAmountSum);
            result.setBidAmount(bidAmountSum);
            result.setOfferAmount(offerAmountSum);
            result.setState(indicativeCounter > 0 ? State.INDICATIVE : State.FIRM);
            return result;
        }


    }
}
