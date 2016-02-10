/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testFindingTradeInData;

import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

/**
 *
 * @author reedvillanueva
 */

/*
CURRENT/MISC ISSUES:
1. (see javadoc note for TradeInRating.getLowestBuyPrice).

2.
*/

public class TestTradeInRating {
    /*
    testing goal:
    Go to the AMZ url and determine difference in 
    the item's trade-in value 
    and 3rd party used price.
    */
    
    private static final String USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10; rv:33.0) Gecko/20100101 Firefox/33.0";
    
    public static void main(String[] args) throws IOException {
        /*
        Infrequently throws some error that seems to be due to something in the setup (connection?) section; 
        seems to be resolved by just re-running the file or re-cleaning and building the project.
        Next time this happens, document the error and research cause.
        
        other errors:
        1. Exception in thread "main" org.jsoup.HttpStatusException: HTTP error fetching URL. Status=404, URL=http://www.amazon.com/Childrens-Books/b/ref=sv_b_4?ie=UTF8&amp;node=4
           when url = "http://www.amazon.com/Childrens-Books/b/ref=sv_b_4?ie=UTF8&node=4"
        2. org.jsoup.HttpStatusException: HTTP error fetching URL. Status=404, URL=http://www.amazon.com/Young-Adult-Teens-Books/b/ref=chp_manbrowse_YA?ie=UTF8&amp;node=28&amp;pf_rd_m=ATVPDKIKX0DER&amp;pf_rd_s=merchandised-search-leftnav&amp;pf_rd_r=09B0FX231PMQXP29ZVSH&amp;pf_rd_t=101&amp;pf_rd_p=2156615522&amp;pf_rd_i=4
           when url = "http://www.amazon.com/Young-Adult-Teens-Books/b/ref=chp_manbrowse_YA?ie=UTF8&node=28&pf_rd_m=ATVPDKIKX0DER&pf_rd_s=merchandised-search-leftnav&pf_rd_r=09B0FX231PMQXP29ZVSH&pf_rd_t=101&pf_rd_p=2156615522&pf_rd_i=4"
        **(For the first 2 exceptions, I think may be due to the USER_AGENT or css selcetors 
        (check stack trace that they occur before the exception) in any case really only need this to work on Amazon
        product pages).
        **http://stackoverflow.com/questions/14723338/404-error-when-parsing-url-using-jsoup
        **http://stackoverflow.com/questions/18292919/giving-an-url-that-redirected-is-a-url-with-spaces-to-jsoup-leads-to-an-error
        
        3.
        */
        
        //setting up for test
        String url = "http://www.amazon.com/Little-Hot-Dogs-John-Himmelman/dp/0761457976/ref=sr_1_241/182-6958137-6913236?s=textbooks-trade-in&ie=UTF8&qid=1439873166&sr=1-241";
                //"http://www.amazon.com/Clotilde-Olyff/dp/0395707366/ref=sr_1_50?s=textbooks-trade-in&ie=UTF8&qid=1439869381&sr=1-50";
                //"http://www.amazon.com/Epistles-Clement-Ignatius-Antioch-Christian/dp/080910038X/ref=sr_1_18?s=textbooks-trade-in&ie=UTF8&qid=1439858608&sr=1-18";
                //"http://www.amazon.com/00-Drawings-Barbara-Gladstone-Gallery/dp/0970342209/ref=sr_1_17?s=textbooks-trade-in&ie=UTF8&qid=1439858113&sr=1-17";
                //"http://www.amazon.com/Yookoso-Invitation-Contemporary-Japanese-Learning/dp/0072971207/ref=sr_1_3?s=books&ie=UTF8&qid=1439197005&sr=1-3&keywords=yookoso+an+invitation+to+contemporary+japanese";
                //"http://www.amazon.com/Discrete-Mathematics-Applications-Susanna-Epp/dp/0495391328/ref=sr_1_1?s=books&ie=UTF8&qid=1439202125&sr=1-1&keywords=discrete+mathematics+with+applications";
                //"http://www.amazon.com/gp/new-releases/books/465600/ref=zg_bsnr_nav_b_1_b";
                //"http://www.amazon.com/gp/new-releases/books/ref=sv_b_1";
                //"http://www.amazon.com/Semiconductor-Device-Fundamentals-Robert-Pierret/dp/0201543931/ref=sr_1_1?s=books&ie=UTF8&qid=1439200044&sr=1-1&keywords=semiconductor+device+fundamentals";
                //"http://www.amazon.com/Lamps-Louis-Comfort-Tiffany-smaller/dp/0865652961/ref=sr_1_1?s=books&ie=UTF8&qid=1439198038&sr=1-1&keywords=lamps";
         
        try{ 
        url = Jsoup.clean(url, Whitelist.basic());
        Connection conn = Jsoup.connect(url).userAgent(USER_AGENT);
        Document htmlDocument = conn.get();
        
        double rating = getRating(htmlDocument);
        System.out.println("rating: $"+rating);
        } catch(Exception e) {
            System.out.println(e.fillInStackTrace());
        }

    }
    
    /**
    * @param htmlDocument
    * a parsed html document of an amazon.com/ product page;
    * e.g. not product browsing page or gift-card redemption.
    * 
    * @return the price difference of the trade-in price minus the lowest 3rd-party seller price 
    * (if both prices are available, else returns 0.00), values may be negative.
    */
    public static double getRating(Document htmlDocument) {
        /*
        Can copy all unique css selectors using firefox inspect element and right-clicking on the desiered element.
        Need to see what happens if page formate from what the selectors are expecting;
        e.g. what happens on a non-textbook-selling page.
        */
        double buyPrice = getLowestBuyPrice(htmlDocument);
        double tradeInPrice = getTradeInPrice(htmlDocument);
        System.out.println("\nbuyPrice: $"+buyPrice);
        System.out.println("tradeInPrice: $"+tradeInPrice);
        
        if(buyPrice == -1 || tradeInPrice == -1) {
            return 0.00;
        } else {
            double rating = tradeInPrice - buyPrice;
            rating = Math.floor((rating)*100) / 100;
            System.out.println("rating: $"+rating);
            System.out.println("base uri:"+htmlDocument.location());
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
     * (Still does not correctly identify the used and new price elements when there are multiple formats
     * e.g. [kindle, paperback, audio]).
     */
    private static double getLowestBuyPrice(Document htmlDocument) {
        String cssQueryBuyAmz = "#singleLineOlp > span:nth-child(1) > span:nth-child(2)";
        
        String cssQueryBuyUsed3rdParty = ".olp-used > a:nth-child(1)" /* > span:nth-child(1)*/;
        String cssQueryBuyNew3rdParty = ".olp-new > a:nth-child(1)" /* > span:nth-child(1)*/;
                
        final double shippingEst = 3.99;
        Elements buyAmz = htmlDocument.select(cssQueryBuyAmz);
        if(! buyAmz.isEmpty()) {
            System.out.println("\n**getLowestPrice buyAmz selector: ("+ buyAmz.size()+")");
            for(Element elem : buyAmz) {
                System.out.println(" * attrs: <"+elem.attributes()+">  ("+elem.text()+")");
                System.out.println("value: $"+getMoneySubstring(elem.text()));
            }
            
            double buyPrice = getMoneySubstring(buyAmz.get(0).text()) + shippingEst;
            return buyPrice;
        } else {
            Elements buyUsed3rdParty = htmlDocument.select(cssQueryBuyUsed3rdParty);
            Elements buyNew3rdParty = htmlDocument.select(cssQueryBuyNew3rdParty);
            
            if(! buyUsed3rdParty.isEmpty()) {
                System.out.println("\n**getLowestPrice buyUsed selector: ("+ buyUsed3rdParty.size()+")");
                for(Element elem : buyUsed3rdParty) {
                    System.out.println(" * attrs: <"+elem.attributes()+">  ("+elem.text()+")");
                    System.out.println("value: $"+getMoneySubstring(elem.text()));
                }
            }
            if(! buyNew3rdParty.isEmpty()) {
                System.out.println("\n**getLowestPrice buyNew selector: ("+ buyNew3rdParty.size()+")");
                for(Element elem : buyNew3rdParty) {
                    System.out.println(" * attrs: <"+elem.attributes()+">  ("+elem.text()+")");
                    System.out.println("value: $"+getMoneySubstring(elem.text()));
                }
            }
                
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
            System.out.println("\n**getRating tradeIn selector: ("+ tradeIn.size()+")");
            for(Element elem : tradeIn) {
                System.out.println(" * attrs: <"+elem.attributes()+">  ("+elem.text()+")");
                System.out.println("value: $"+getMoneySubstring(elem.text()));
            }
        }
        
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
    * A 'money value string' is any string starting with $ followed by a number with exactly 2 deicaml places.
    * 
    * Note: Assumes that the currency value is the last sequence in the string (that is, no other characters come 
    * after the end of the money value). Money string may have commas (e.g. "$2,377.01")
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
