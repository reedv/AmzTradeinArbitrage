/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testCrawler.Pair;

/**
 *
 * @author reedvillanueva
 */

public class ratingUrlPair{
    private final double rating;
    private final String url;

    public ratingUrlPair(double left, String right) {
      this.rating = left;
      this.url = right;
    }

    public double getRating() { return rating; }
    public String getUrl() { return url; }

    public void display() {
        System.out.println(rating+" : "+url);
    }

}
