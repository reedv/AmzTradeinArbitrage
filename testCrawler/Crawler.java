/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testCrawler;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
import testDB.TestCrawlerDbInterface;

/**
 *
 * @author reedvillanueva
 */
public class Crawler {
    private final String ANSI_RED = "\u001B[31m";
    private final String ANSI_RESET = "\u001B[0m";
    
    private final int numPages=400;
    private final int resultsPerPage=16;
    private final int MAX_PAGES_TO_SEARCH = (numPages-1) * resultsPerPage;   //assuming last pg, not full
    
    private final TestCrawlerDbInterface db = new TestCrawlerDbInterface("textbook_arb_page_ratings");
    private final String pagesVisitedTable = "Pages_Visited";
    private final String pageRatingsTable = "Page_Ratings";
    
    private int toVisitIsEmptyCount=0;   //debugging
    private Set<String> pagesVisitedUnsuccessfully = new HashSet<String>();
    private List<String> pagesToVisit = new LinkedList<String>();

    /**
     * This is our test. It creates a spider (which creates spider legs) and crawls the web.
     * 
     * @param args
     *            - not used
     * @throws java.sql.SQLException
     */
    public static void main(String[] args) throws SQLException {  
        TestCrawler spider = new TestCrawler();
        spider.rateAndStorePagesStartingAtUrl(
                "http://www.amazon.com/s/ref=lp_2205237011_st?rh=n%3A283155%2Cn%3A!44258011%2Cn%3A2205237011&qid=1439811667&sort=title-asc-rank");
    }
    
    /**
     * Our main launching point for the Spider's functionality. 
     * Internally it creates spider legs that make an HTTP request and parse the response (the web page).
     * 
     * @param startingUrl
     *            - The starting point of the spider
     * @throws java.sql.SQLException
     */
    public void rateAndStorePagesStartingAtUrl(String startingUrl) throws SQLException {   
        db.runSql_bool("TRUNCATE "+this.pageRatingsTable+";");
        db.runSql_bool("TRUNCATE "+this.pagesVisitedTable+";");
        
        while(db.tableSize(this.pagesVisitedTable) < MAX_PAGES_TO_SEARCH) {
            String currentUrl;
            if(this.pagesToVisit.isEmpty()) {
                System.out.println(ANSI_RED+"pagesToVisit is empty (should only occur at start!)"+ANSI_RESET);
                if(this.toVisitIsEmptyCount > 1) return;
                
                currentUrl = startingUrl;
                db.storeUrlVisitedInTable(this.pagesVisitedTable, startingUrl);
            }
            else {
                currentUrl = this.getNextUrl();  
                db.storeUrlVisitedInTable(this.pagesVisitedTable, currentUrl);
            }
            
            
            TestLeg leg = new TestLeg();
            boolean crawlSuccessful = leg.crawl(currentUrl);
            if(crawlSuccessful) {
                System.out.println(ANSI_RED+"crawlSuccessful"+ANSI_RESET);
                this.pagesToVisit.addAll(leg.getLinks());

                //check if the given URL is already in database (may be redundant with getNextUrl())
                if(! db.tableContainsColumnValue(this.pageRatingsTable, "url", currentUrl)) {
                    db.storeRatingPageInTable(this.pageRatingsTable, leg.getPageRating(), currentUrl);
                }
                
            } else {
                this.pagesVisitedUnsuccessfully.add(currentUrl);
            }
        }

        JOptionPane.showMessageDialog(null, 
                            "Crawler has finished\nVisited "+db.tableSize(this.pagesVisitedTable)+" web page(s)", 
                            "Crawler run has ended", JOptionPane.INFORMATION_MESSAGE);
        System.out.println("\nUnsuccessful crawls:");
        for(String url : this.pagesVisitedUnsuccessfully) {
            System.out.println(url);
        }
        System.out.println(ANSI_RED+"robotCheckUsed in leg: "+TestLeg.robotCheckUsed+ANSI_RESET);
    
    }
    
    /** 
     * 
     * @return the next URL to visit (in the order that they were found). 
     * We also do a check to make sure this method doesn't return a URL that has already been visited.
     */
    private String getNextUrl() throws SQLException {
        String nextUrl;
        do {
            nextUrl = this.pagesToVisit.remove(0);
        } while(db.tableContainsColumnValue(this.pagesVisitedTable, "url", nextUrl));  
        return nextUrl;
    }  
}
