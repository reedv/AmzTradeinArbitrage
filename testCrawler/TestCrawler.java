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

/*
Goal:
Starting from http://www.amazon.com/s/ref=nb_sb_noss?url=rateAndStorePagesStartingAtUrl-alias%3Dtextbooks-tradein, 
crawl only through the links that point to other textbooks (and so on from those pages).
E.g. not to the giftcard redemptionp page, check cart page, ect.
*/

/*
TODO:
1. Problem with looping back to the begining while running (output snippet below):
    **Visiting** Received web page at http://www.amazon.com/Hammerfast-Dwarven-Outpost-Adventure-Site/dp/0786955341/ref=sr_1_4032/182-8538737-6681460?s=textbooks-trade-in&ie=UTF8&qid=1439950012&sr=1-4032
    Found (0) links in page.

    **Visiting** Received web page at http://www.amazon.com/s/ref=sr_pg_253/182-8538737-6681460?rh=n%3A283155%2Cn%3A%2144258011%2Cn%3A2205237011&page=253&sort=title-asc-rank&ie=UTF8&qid=1439950012
    Found (17) links in page.
    http://www.amazon.com/Neverwinter-Campaign-Setting-Dungeons-Supplement/dp/0786958146/ref=sr_1_4033/182-8538737-6681460?s=textbooks-trade-in&ie=UTF8&qid=1439950054&sr=1-4033
    http://www.amazon.com/Players-Handbook-4th-Core-Rulebook/dp/078695390X/ref=sr_1_4034/182-8538737-6681460?s=textbooks-trade-in&ie=UTF8&qid=1439950054&sr=1-4034
    http://www.amazon.com/Players-Option-Feywild-Dungeons-Supplement/dp/0786958367/ref=sr_1_4035/182-8538737-6681460?s=textbooks-trade-in&ie=UTF8&qid=1439950054&sr=1-4035
    http://www.amazon.com/Prince-Undeath-Adventure-Dungeons-Dragons/dp/0786952474/ref=sr_1_4036/182-8538737-6681460?s=textbooks-trade-in&ie=UTF8&qid=1439950054&sr=1-4036
    http://www.amazon.com/Slaying-Stone-Adventure-HS1-4th/dp/0786953888/ref=sr_1_4037/182-8538737-6681460?s=textbooks-trade-in&ie=UTF8&qid=1439950054&sr=1-4037
    http://www.amazon.com/Witchlight-Fens-Dungeon-Dungeons-Accessory/dp/0786958006/ref=sr_1_4038/182-8538737-6681460?s=textbooks-trade-in&ie=UTF8&qid=1439950054&sr=1-4038
    http://www.amazon.com/Tomb-Horrors-4th-Super-Adventure/dp/0786954914/ref=sr_1_4039/182-8538737-6681460?s=textbooks-trade-in&ie=UTF8&qid=1439950054&sr=1-4039
    http://www.amazon.com/4th-Siglers-Injectable-Drug-Cards/dp/1880579596/ref=sr_1_4040/182-8538737-6681460?s=textbooks-trade-in&ie=UTF8&qid=1439950054&sr=1-4040
    http://www.amazon.com/4th-Ammo-Encyclopedia-Michael-Bussard/dp/1936120224/ref=sr_1_4041/182-8538737-6681460?s=textbooks-trade-in&ie=UTF8&qid=1439950054&sr=1-4041
    http://www.amazon.com/WIE-Applied-Statistics-Probability-Engineers/dp/0471794732/ref=sr_1_4042/182-8538737-6681460?s=textbooks-trade-in&ie=UTF8&qid=1439950054&sr=1-4042
    http://www.amazon.com/Semantic-Web-Applications-Conference-Proceedings/dp/3540726667/ref=sr_1_4043/182-8538737-6681460?s=textbooks-trade-in&ie=UTF8&qid=1439950054&sr=1-4043
    http://www.amazon.com/4th-Fighter-Wing-Korean-War/dp/0764313150/ref=sr_1_4044/182-8538737-6681460?s=textbooks-trade-in&ie=UTF8&qid=1439950054&sr=1-4044
    http://www.amazon.com/Formal-Methods-Software-Engineering-International/dp/3540000291/ref=sr_1_4045/182-8538737-6681460?s=textbooks-trade-in&ie=UTF8&qid=1439950054&sr=1-4045
    http://www.amazon.com/Articulated-Motion-Deformable-Objects-International/dp/354036031X/ref=sr_1_4046/182-8538737-6681460?s=textbooks-trade-in&ie=UTF8&qid=1439950054&sr=1-4046
    http://www.amazon.com/Autonomic-Trusted-Computing-International-Proceedings/dp/3540735461/ref=sr_1_4047/182-8538737-6681460?s=textbooks-trade-in&ie=UTF8&qid=1439950054&sr=1-4047
    http://www.amazon.com/Cryptology-Network-Security-International-Proceedings/dp/3540308490/ref=sr_1_4048/182-8538737-6681460?s=textbooks-trade-in&ie=UTF8&qid=1439950054&sr=1-4048
    http://www.amazon.com/s/ref=sr_pg_254/182-8538737-6681460?rh=n%3A283155%2Cn%3A%2144258011%2Cn%3A2205237011&page=254&sort=title-asc-rank&ie=UTF8&qid=1439950054

    **Visiting** Received web page at http://www.amazon.com/4th-Siglers-Injectable-Drug-Cards/dp/1880579596/ref=sr_1_4040/182-8538737-6681460?s=textbooks-trade-in&ie=UTF8&qid=1439950054&sr=1-4040
    Found (0) links in page.

    **Visiting** Received web page at http://www.amazon.com/WIE-Applied-Statistics-Probability-Engineers/dp/0471794732/ref=sr_1_4042/182-8538737-6681460?s=textbooks-trade-in&ie=UTF8&qid=1439950054&sr=1-4042
    Found (0) links in page.
    pagesToVisit is empty (should only occur at start!)
    pagesToVisit is empty (should only occur at start!)
    pagesToVisit is empty (should only occur at start!)
    pagesToVisit is empty (should only occur at start!)

    **Visiting** Received web page at http://www.amazon.com/s/ref=lp_2205237011_st?rh=n%3A283155%2Cn%3A!44258011%2Cn%3A2205237011&qid=1439811667&sort=title-asc-rank
    Found (17) links in page.
    http://www.amazon.com/Think-Aloud-Talk-Aloud-Approach-Building-Language/dp/0807753939/ref=sr_1_1/186-9858114-3998835?s=textbooks-trade-in&ie=UTF8&qid=1439950161&sr=1-1
    http://www.amazon.com/Teacher-Breaking-Cycle-Struggling-Readers/dp/080775322X/ref=sr_1_2/186-9858114-3998835?s=textbooks-trade-in&ie=UTF8&qid=1439950161&sr=1-2
    http://www.amazon.com/Common-Core-Meets-Education-Reform/dp/0807754781/ref=sr_1_3/186-9858114-3998835?s=textbooks-trade-in&ie=UTF8&qid=1439950161&sr=1-3
    http://www.amazon.com/Common-Core-Meets-Education-Reform/dp/080775479X/ref=sr_1_4/186-9858114-3998835?s=textbooks-trade-in&ie=UTF8&qid=1439950161&sr=1-4
    http://www.amazon.com/Eyes-Math-0-Marian-Small/dp/0807753912/ref=sr_1_5/186-9858114-3998835?s=textbooks-trade-in&ie=UTF8&qid=1439950161&sr=1-5
    http://www.amazon.com/Good-Questions-Differentiate-Mathematics-Instruction/dp/0807753130/ref=sr_1_6/186-9858114-3998835?s=textbooks-trade-in&ie=UTF8&qid=1439950161&sr=1-6
    http://www.amazon.com/Managing-Legal-Risks-Childhood-Programs/dp/0807753777/ref=sr_1_7/186-9858114-3998835?s=textbooks-trade-in&ie=UTF8&qid=1439950161&sr=1-7
    http://www.amazon.com/Promoting-Racial-Literacy-Schools-Differences/dp/0807755044/ref=sr_1_8/186-9858114-3998835?s=textbooks-trade-in&ie=UTF8&qid=1439950161&sr=1-8
    http://www.amazon.com/Resilience-Begins-Beliefs-Building-Strengths/dp/0807754838/ref=sr_1_9/186-9858114-3998835?s=textbooks-trade-in&ie=UTF8&qid=1439950161&sr=1-9
    http://www.amazon.com/0-Lost-Literature-Vito-Acconci/dp/1933254203/ref=sr_1_10/186-9858114-3998835?s=textbooks-trade-in&ie=UTF8&qid=1439950161&sr=1-10
    http://www.amazon.com/Youth-Held-Border-Immigration-Education/dp/0807753890/ref=sr_1_11/186-9858114-3998835?s=textbooks-trade-in&ie=UTF8&qid=1439950161&sr=1-11
    http://www.amazon.com/0-174-Complete-Numbers-Gordon-Massman/dp/193552044X/ref=sr_1_12/186-9858114-3998835?s=textbooks-trade-in&ie=UTF8&qid=1439950161&sr=1-12
    http://www.amazon.com/Zero-Six-v-Youjung-Lee/dp/1600090249/ref=sr_1_13/186-9858114-3998835?s=textbooks-trade-in&ie=UTF8&qid=1439950161&sr=1-13
    http://www.amazon.com/00101-09-Basic-Safety-TG-NCCER/dp/0136098754/ref=sr_1_14/186-9858114-3998835?s=textbooks-trade-in&ie=UTF8&qid=1439950161&sr=1-14
    http://www.amazon.com/00103-04-Introduction-Hand-Tools-TG/dp/0131600060/ref=sr_1_15/186-9858114-3998835?s=textbooks-trade-in&ie=UTF8&qid=1439950161&sr=1-15
    http://www.amazon.com/00104-04-Introduction-Power-Tools-TG/dp/0131600079/ref=sr_1_16/186-9858114-3998835?s=textbooks-trade-in&ie=UTF8&qid=1439950161&sr=1-16
    http://www.amazon.com/s/ref=sr_pg_2/186-9858114-3998835?rh=n%3A283155%2Cn%3A%2144258011%2Cn%3A2205237011&page=2&sort=title-asc-rank&ie=UTF8&qid=1439950161
       **TRY: ...running main w/ starting url set to the url of the page that grabs the results that the loop occurs
       in the middle of (that is, the url of pg.254; see above).
          ***RESULTS: Loop problem occured again at while processing the results on pg_278. Used url:
          http://www.amazon.com/s/ref=sr_pg_253/182-8538737-6681460?rh=n%3A283155%2Cn%3A%2144258011%2Cn%3A2205237011&
             page=253&sort=title-asc-rank&ie=UTF8&qid=1439950012
          Multiple times running code results in the error occuring infrequently / during non-constant points 
          in the process.
          When starting from the exact product-result page that the loop began on, 0 links were found on that page 
          (as none should be) and the 'pagesToVisit is empty' message was displayed. However, this seems normal since 
          starting from this page gives no further links to crawl over.
             ****Conclusions: Still don't know what caused the bug.
       **TRY: Added print statement in the if-block that is called when the pagesToVisit-isEmpty conditional is entered.
          ***RESULTS: When the pagesToVisit-isEmpty conditional is erroneously entered mid-run, no elements are printed,
          so the list does appear to actually be empty.
       **OBSERVATION: During a test-run to find more instances of the error, a case was found were the 
       pagesToVisit-isEmpty block was wrongly entered the 2nd time while processing a next-page url:
       http://www.amazon.com/s/ref=sr_pg_303/180-6848013-1700418?rh=n%3A283155%2Cn%3A%2144258011%2Cn%3A2205237011&page=303&sort=title-asc-rank&ie=UTF8&qid=1439978545
       Using this as a starting url immediately causes the error agin. This makes it a good starting url to debug
       this problem with.
          ***Just tried running the code again and multiple times running code, observed that the error 
          occured infrequently / during non-constant points in the process. (So not actually a good seed).
          Maby the leg conection is timing out?
       **TRY: ...using a database to store Pages_To_Visit rather than a list. Can check for looping by letting
       the program run for some time (at least up to the pg. url the loop occured on last time (pg_254)), then
       checking the Page_Ratings db or the Pages_To_Visit db to see if there were actually more urls beyond the url
       at which the error occured. I suspect that the problem may have something to do 
       with how the pages to visit are currently stored or at least how nextUrl() is implemented
       (as it is apparently empty in the middle of running).

       **Could the problem be with TestLeg returning an unsuccessful crawl boolean?
       
       **Navigating the the url that the process loops at, it appears that Amazon is presenting a capcha robot check.
       This could be why the pagesToVisit-isEmpty conditional is getting reentered; we end up on a product page that
       has no product results. (This may also just be another unrelated problem).
          ***TRY: adding code to TestLeg to refresh the page if it meets a capcha page. 
          (This seemed to work in-browser).

       **Just ran program without cathcing this looping error (PagesToVisit-isEmpty block entered only once).
       IDK what has changed. (see file fisrtCrawlNoApparentErrors.txt). The only change I can think of that was
       related to the error was moving 'this.pagesVisited.add(startingUrl);' out of getNextUrl().
          ***THEORY: I think that amazon is randomly testing whether the crawler is a robot by presnting capcha 
          questions these pages dont have any of the knids of links the leg is programmed to look for so no new
          links are added and pagesToVisit stays empty.
             TRY: adding logic to leg crawl to re-connect to the given url if the page has specific captcha-page
             characteristics (use css selector #captchacharacters). Use a static boolean to test if this block is 
             ever entered and display it when run ends. 
             If it was used and the db shows that all results-pages were visited (count should be approx. 6400)
             (there may be some duplicate pages for random, non-starting-url pages), we can assume these were
             successfully reloaded captcha pages
             Else the run will just end prematurlly again and we can say this did not work.
       
       **OBSERVATION: There may be 2 seperate problems causing the pagesToVisit-isEmpty error:
       1. Some yet unknown exception in TestLeg causing unsuccessful crawls from a certain random product-result
          onward. The pagesToVisit empties without getting new links from the nextpage link (at the end of the list).
          This error looks like the current 'batch' of urls in pagesToVisit empties in some mid-point
          of the pagesToVisit list. Be sure that the leftover 'batch' of unsuccessful crawls at the end all come 
          from the most recent 'batch' of results (remember that all of the random unsuccessful crawls are displayed
          when the program ends).
             ***TRY: Added a error pop-up in the leg crawl() catch block
                ****RESULTS: Some conections cause a socket timeout exception. This does not seem to neccessarily 
                cause the run to end. I think mabye whole 'batches' from a certain url onward are having 
                their connections time out. The important part that may be causing the error in this case
                is the next-page link timing out.
       2. The leg connects to a captcha page, this seems to only happen when connecting to a next-page link.
          This error can be IDed by looking at the pages-visited output; it can be seen that all of the links of
          the prev. page have been successfully visited except for the nextpage link. Cutting and pasting this 
          next-page link in the browser, we are directed to a captcha page. Refreshing this page loads the desired
          next-page. Since the leg was not able to get the next 'batch' of results from the next-page link, the
          pagesToVisit list is empty and the run end prematurlly.
          Be cautious that the last link really leads to a captcha page. A type(1) error (see above) can sometimes
          looks like this type(2) when a socketTimeOut exception is throws while visiting a next-page link.

2. Find a better way to order the ratingsUrls
    **Use a SortedSet and implement Compare in the ratingUrlPair calss?
    **Merge sort the Set at the end?
    **use database?
       ***Linking TestCrawler (tcwdb) to MAMP's mypPhpAdmin, the correct number of urls appear, 
       but currently without their corresponding ratings.
          ****TENETIVLY RESOLVED, need more testing

3. Have page_ratings rating-value be NULL rather than 0 when 3rd party info or trade-in info could not be found.
   (would be slightly more discriptive?).
      **RESOLVED: did not implement, null rows would actually be less or equally descriptive since the cause of 
      the null could vary.

4. Would be faster to have pagesToVisit feed in from a database table?
      **Currently working on phasing out the getNextUrl() method with a db conection to Pages_To_Visit. Will need 
      to link this table to Pages_Visited in a similar manner as is currently implemented in getNextUrl().
         ***Need to look over the design and determine if this is even a good idea. Mabye having a Page_Ratings db
         is good enough.
            ****RESULTS: pagesToVisit is only ever as long as the number of results on each page (about 16), it is
            then emptied by getNextUrl(), so storing in a database would nt be worth round-trip. Still seems like a 
            good idea to use a db for pagesVisited.
      **TRY: ...beginning to phase out pagesVisited List in favor of a db implementation.
      **RESULTS: db transition done.

5. Need way to ensure that program self-terminates at some point (since its hard to tell whether the program is
   caught in a loop when crawling through a large number of pages)

6. Modify rating system so we only store urls in Page_Ratings db that have a profit=tradein/buyprice*100 > 50(%).

8. Connect program to a task manager/queue to run periodically.

9. The original Whitelist.basic cleaning was commented out because it was changing the url of the next-page link
   when it was passed into leg.crawl() as the url param. Look/test for a better whitelist that does not alter the 
   urls being passed in (they should all be from amazon.com and could probably be assumed safe, but I would think
   its better practice to have some kind of whitelist)

10. Add javaFX progress bar?
*/

/*
CURRENT/MISC ISSUES:
Exception in thread "main" java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
	at java.util.ArrayList.rangeCheck(ArrayList.java:653)
	at java.util.ArrayList.get(ArrayList.java:429)
	at testCrawler.TestLeg.getLowestBuyPrice(TestLeg.java:180)
	at testCrawler.TestLeg.getPageRating(TestLeg.java:138)
	at testCrawler.TestCrawler.rateAndStorePagesStartingAtUrl(TestCrawler.java:94)
	at testCrawler.TestCrawler.main(TestCrawler.java:68)
Java Result: 1
**Occured after visiting url: http://www.amazon.com/Clotilde-Olyff/dp/0395707366/ref=sr_1_51/191-1346241-7507421?s=textbooks-trade-in&ie=UTF8&qid=1439866133&sr=1-51
   ***RESOLVED: was caused by the following page visited not have BOTH used AND new 3rd party seller options.

Exception in thread "main" java.lang.NumberFormatException: For input string: "2,377.01"
	at sun.misc.FloatingDecimal.readJavaFormatString(FloatingDecimal.java:2043)
	at sun.misc.FloatingDecimal.parseDouble(FloatingDecimal.java:110)
	at java.lang.Double.parseDouble(Double.java:538)
	at testCrawler.TestLeg.getMoneySubstring(TestLeg.java:224)
	at testCrawler.TestLeg.getLowestBuyPrice(TestLeg.java:182)
	at testCrawler.TestLeg.getPageRating(TestLeg.java:138)
	at testCrawler.TestCrawler.rateAndStorePagesStartingAtUrl(TestCrawler.java:107)
	at testCrawler.TestCrawler.main(TestCrawler.java:81)
Java Result: 1
**Occurs WHILE visiting url: http://www.amazon.com/Little-Hot-Dogs-John-Himmelman/dp/0761457976/ref=sr_1_241/182-6958137-6913236?s=textbooks-trade-in&ie=UTF8&qid=1439873166&sr=1-241
  on results-page 18.
**Need to add a way for getMoneySubstring to handle commas in money strings.
   ***RESOLVED

Note when debugging:
System.out and System.err may be different threads, so their print statements may not be synced 
and may look non non-sequiter, but are actually running correctly.
*/
public class TestCrawler {  
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
                /*"http://www.amazon.com/s/ref=sr_pg_303/180-6848013-1700418?rh=n%3A283155%2Cn%3A%2144258011%2Cn%3A2205237011&page=303&sort=title-asc-rank&ie=UTF8&qid=1439978545"
                /*"http://www.amazon.com/s/ref=sr_pg_253/182-8538737-6681460?rh=n%3A283155%2Cn%3A%2144258011%2Cn%3A2205237011&page=253&sort=title-asc-rank&ie=UTF8&qid=1439950012"*/
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
                
                //start purely for debugging----------------------
                toVisitIsEmptyCount++;
                if(toVisitIsEmptyCount > 1) {
                    JOptionPane.showMessageDialog(null, 
                            "toVisitIsEmpty, count > 1\nleg exceptionCaught: "+TestLeg.exceptionCaught, 
                            "Premature toVisit empty", JOptionPane.ERROR_MESSAGE);
                    System.out.println("pagesToVisit");
                    for(String url : pagesToVisit) {
                        System.out.println(url);
                    }
                    System.out.println("pages unsuccessful");
                    for(String url : pagesVisitedUnsuccessfully) {
                        System.out.println(url);
                    }
                    return;    
                }
                //end debugging-------------------------------------
                
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
