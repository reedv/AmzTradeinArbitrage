/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testFindingTradeInData;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author reedvillanueva
 */
public class TradeInRating {
    /**
    * 
    * @param htmlDocument
    * @return the price difference of the trade-in price minus the lowest 3rd-party seller price 
    * (if both prices are available, else returns 0.00), values may be negative.
    */
    public static double getRating(Document htmlDocument) {
        double buyPrice = getLowestBuyPrice(htmlDocument);
        double tradeInPrice = getTradeInPrice(htmlDocument);
        
        if(buyPrice == -1 || tradeInPrice == -1) {
            return 0.00;
        } else {            
            double rating = tradeInPrice - buyPrice;
            rating = Math.floor((rating)*100) / 100;
            return rating;
        }
    }
    
    /**
     * The program may encounter two types of Amazon product result pages, products that are also sold by Amazon and
     * products that are only sold through Amazon by 3rd party sellers.The location of the 3rd party new and used
     * selling information is different for these two types of pages.(The lowest 3rd party selling info is 
     * automatically listed for products also sold by Amazon).
     * 
     * @return a double representing the new or used 3rd party price of the current page (depending on which is lower). 
     * Will return -1 if the information does not exist on the page or cannot be found. 
     * 
     * Note that some pages have multiple formats and this may only return the FIRST instance of the 
     * 'used' or 'new' price even if another format has a lower price.
     */
    private static double getLowestBuyPrice(Document htmlDocument) {
        String cssQueryBuyAmz = "#singleLineOlp > span:nth-child(1) > span:nth-child(2)";
        
        String cssQueryBuyUsed3rdParty = ".olp-used > a:nth-child(1)";
        String cssQueryBuyNew3rdParty = ".olp-new > a:nth-child(1)";
        
        final double shippingEst = 3.99;
        Elements buyAmz = htmlDocument.select(cssQueryBuyAmz);
        if(! buyAmz.isEmpty()) {
            double buyPrice = getMoneySubstring(buyAmz.get(0).text()) + shippingEst;
            return buyPrice;
        } else {
            Elements buyUsed3rdParty = htmlDocument.select(cssQueryBuyUsed3rdParty);
            Elements buyNew3rdParty = htmlDocument.select(cssQueryBuyNew3rdParty);
                
                if(! (buyUsed3rdParty.isEmpty() && buyNew3rdParty.isEmpty())) {
                    double usedPrice = (! buyUsed3rdParty.isEmpty()) ? 
                            getMoneySubstring(buyUsed3rdParty.get(0).text()) + shippingEst : -1;
                    double newPrice = (! buyNew3rdParty.isEmpty()) ? 
                            getMoneySubstring(buyNew3rdParty.get(0).text()) + shippingEst : -1;
                    double buyPrice = Math.min(usedPrice, newPrice);

                    return buyPrice;
                } else {
                    return -1;
                }
        }
    }
    
    /**
     * 
     * @param htmlDocument
     * @return the trade-in price for the page or -1 if no such value found
     */
    private static double getTradeInPrice(Document htmlDocument) {
        String cssQueryTradeIn = "#tradeInButton_tradeInValue";
        Elements tradeIn = htmlDocument.select(cssQueryTradeIn);
        
        if(! tradeIn.isEmpty()) {
            double tradeInPrice = getMoneySubstring(tradeIn.get(0).text());
            return tradeInPrice;
        } else {
            return -1;
        }        
    }
    
    /**
    @param String str
    * 
    @return the value of the first occurance of a money value in the string
    * A 'money value string' is any string starting with $ followed by a number with exactly 2 decimal places.
    * 
    * Note: Assumes that the currency value is the last sequence in the string (that is, no other characters come 
    * after the end of the money value).
    */
    private static double getMoneySubstring(String str) {
        String currenySymbol = "$";
        if(str.contains(currenySymbol)) {
            int startIndex = str.indexOf('$')+1;
            
            String moneyString = str.substring(startIndex);
            moneyString = moneyString.replaceAll(",", "");
            
            return Double.parseDouble(moneyString);
        }
        
        return 0;
    }
}
