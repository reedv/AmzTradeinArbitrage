/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testCrawler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JOptionPane;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import testFindingTradeInData.TradeInRating;

/**
 *
 * @author reedvillanueva
 */

/*
TODO:

1. Add logic to reconnect to a page if it is a capcha page

2. For some pages, a connection exception is thrown and can cause an endless loop or error exit.
   Occurs on the line where: Document htmlDocument = connection.get();
   Exception in thread "main" org.jsoup.HttpStatusException: HTTP error fetching URL. Status=404.
   Can possibly be avoided by solving TODO(1), since in all cases so far, the program seems to be able to 
   run normally on those kinds of pages.
*/

public class TestLeg {
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";
        // We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
    private static final String USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:40.0) Gecko/20100101 Firefox/40.0";
    
    private List<String> links = new LinkedList<String>();
    private Connection conn;
    private Document doc;
    
    public static boolean robotCheckUsed;   //for debugging
    public static boolean exceptionCaught = false;  //for debugging


    /**
     * This performs all the work. 
     * It makes an HTTP request, 
     * checks the response, 
     * and then gathers up all the links on the page to visit next. 
     * Perform a searchForWord only after the successful crawl.
     * 
     * @param url
     *            - The URL to visit
     * @return whether or not the crawl was successful
     */
    public boolean crawl(String url) {
        boolean crawlStatus = false;
        this.exceptionCaught=false;
        try {
            //System.err.println("\nurl when passed in: "+url);
            //url = Jsoup.clean(url, Whitelist.basic());
            //System.err.println("url after Whitelist.basic() cleaning: "+url);
            this.getDocument(url);
            if(this.isRobotCheck(this.doc)) {  //try one more time
                this.getDocument(url);
                
                robotCheckUsed = true;
                JOptionPane.showMessageDialog(null, 
                        "Robot check enterd at: "+url, "Robot Check", JOptionPane.INFORMATION_MESSAGE);
            }
            
            if(! this.connectionToUrlOK(this.conn, url)) {
                return crawlStatus;
            }
            
            String cssQuery_toFindLinks = 
                    "li[id^=result_] > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > div:nth-child(1) > a:nth-child(1), "
                    + "#pagnNextLink";
            this.getDocumentLinks(cssQuery_toFindLinks);

            crawlStatus = true;
            return crawlStatus;
        } catch (Exception e) {
            exceptionCaught=true;
            return false;
        }
    }
    
    private void getDocument(String url) throws IOException {
        this.conn = Jsoup.connect(url).userAgent(USER_AGENT).timeout(20*1000);
        this.doc = this.conn.get();
    }
    
    private boolean isRobotCheck(Document htmlDocument) {
        String cssQuery_toFindIfCaptcha = "#captchacharacters";
        Elements captcha = htmlDocument.select(cssQuery_toFindIfCaptcha);
        return !captcha.isEmpty();
    }
    
    private boolean connectionToUrlOK(Connection conn, String url) {
        final int HTTP_OK_STATUS_CODE = 200;
        if(conn.response().statusCode() == HTTP_OK_STATUS_CODE) {
            System.out.println("\n**Visiting** Received web page at " + url);
        }
        if(!conn.response().contentType().contains("text/html")) {
            System.out.println("**Failure** Retrieved something other than HTML");
            JOptionPane.showMessageDialog(null, 
                        "In TestLeg, conectionToUrlOK failed at: "+url, "Bad connection", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    private void getDocumentLinks(String cssQuery) {
        Elements productLinks = this.doc.select(cssQuery);

        System.out.println("Found (" + productLinks.size() + ") links in page.");
        for(Element link : productLinks) {
            System.out.println(ANSI_RED + link.absUrl("href") + ANSI_RESET);
            this.links.add(link.absUrl("href"));
        }
    }
    
    
    /**
    * @param this.doc
    * a parsed html document of an amazon.com/ product page;
    * e.g. not product browsing page or gift-card redemption.
    * 
    * @return the price difference of the trade-in price minus the lowest 3rd-party seller price 
    * (if both prices are available, else returns 0.00), values may be negative.
    */
    public double getPageRating() {
        return TradeInRating.getRating(doc);
    }

    /**
     * 
     * @return 
     */
    public List<String> getLinks() {
        return this.links;
    }

}
