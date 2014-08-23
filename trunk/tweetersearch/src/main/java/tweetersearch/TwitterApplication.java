package tweetersearch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TweetEntity;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Twitter application using Twitter4J
 */
public class TwitterApplication
{
    private final Logger logger = Logger.getLogger(TwitterApplication.class.getName());

    public static void main(String[] args)
        throws TwitterException, IOException, InterruptedException
    {
        new TwitterApplication().retrieve();
    }

    public void retrieve()
        throws TwitterException, IOException, InterruptedException
    {
        logger.info("Retrieving tweets...");
        Twitter twitter = new TwitterFactory().getInstance();

        twitter.setOAuthConsumer("w4hESNxNtinR1pKEIaa4AjPq6",
                "WQKPUdDk2jq0iL9LrEqrKQaMRbnTCXSSWoGC62RKoUS0IQDxTr");

        AccessToken accessToken = new AccessToken(
                "109802685-kXMNqDv8gLkT9ZscBVePxENZ9lTQ6KHr5e7cCtLt",
                "8PKYROS6pbu9zX9YXgXuaV3x281zeMP2OxjBMJMw9DVlT");
        twitter.setOAuthAccessToken(accessToken);
        Query query = getQuery();

        /*
         * try { QueryResult result = twitter.search(query); System.out.println("Count : " +
         * result.getTweets().size()); int i = 0; for (Status tweet : result.getTweets()) {
         * System.out.println("Location : " + tweet.getGeoLocation() + "text :" + tweet.getText());
         * System.out.println(i++); } } catch (TwitterException e) { e.printStackTrace(); }
         */

        OutputStream os = new FileOutputStream(new File("output"));

        boolean finished = false;
        while (!finished) {
            try{
            final QueryResult result = twitter.search(query);

            final List<Status> statuses = result.getTweets();
            long lowestStatusId = Long.MAX_VALUE;
            for (Status tweet : statuses) {
                String text = tweet.getText();
                String user = tweet.getUser().getName() + "\t" + tweet.getUser().getLocation();
                String date = tweet.getCreatedAt() + "\t";
                IOUtils.write(user + "\t" + date + "\t" + text+"\n", os, "UTF8");
                lowestStatusId = Math.min(tweet.getId(), lowestStatusId);
                if(lowestStatusId <100){
                    System.out.println("No More tweets");
                    finished = true;
                    os.close();
                }
            }

            // Subtracting one here because 'max_id' is inclusive
            query.setMaxId(lowestStatusId - 1);
        }
            catch(TwitterException e){
                System.out.println("waiting....");
                Thread.sleep(1000*60*15);
                System.out.println("to continue....");
                continue;
            }
        }
        /*
         * long maxId = -1, sinceId; while (true) { try{ List<Status> tweets = result.getTweets();
         * System.out.println(tweets.size()); for (Status tweet : tweets) { maxId =
         * query.getMaxId(); String text = tweet.getText(); String user = tweet.getUser().getName()
         * + "\t" + tweet.getUser().getLocation(); String date = tweet.getCreatedAt() + "\t"; //
         * System.out.println(user + "\t" + date + "\t" + text); IOUtils.write(user + "\t" + date +
         * "\t" + text, os, "UTF8");
         * 
         * } System.out.println(query.getSinceId()); System.out.println(query.getMaxId()); query =
         * result.nextQuery(); if (query != null) result = twitter.search(query);
         * 
         * if(query == null){
         * 
         * query = getQuery(); query.setSinceId(maxId); } } catch(Exception e){ e.printStackTrace();
         * os.close(); } }
         */
    }

    private Query getQuery()
    {
        Query query = new Query("lang:am");
        query.setCount(100);
        query.setSince("2000-01-01");
        query.setLang("am");
        return query;
    }
}
