package tweetersearch;

import twitter4j.*;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class AmharicTweetExtraction
{

    // Set this to your actual CONSUMER KEY and SECRET for your application as given to you by
    // dev.twitter.com

    private static final String CONSUMER_KEY = "w4hESNxNtinR1pKEIaa4AjPq6";
    private static final String CONSUMER_SECRET = "WQKPUdDk2jq0iL9LrEqrKQaMRbnTCXSSWoGC62RKoUS0IQDxTr";

    // How many tweets to retrieve in every call to Twitter. 100 is the maximum allowed in the API
    private static final int TWEETS_PER_QUERY = 100;

    private static final int MAX_QUERIES = 100;

    /**
     * Replace newlines and tabs in text with escaped versions to making printing cleaner
     * 
     * @param text
     *            The text of a tweet, sometimes with embedded newlines and tabs
     * @return The text passed in, but with the newlines and tabs replaced
     */
    public static String cleanText(String text)
    {
        text = text.replace("\n", " ");
        text = text.replace("\t", " ");
        text = text.replace("'", "").replace("\\", "");
        text = text.replace("\"", "").replaceAll("\\p{C}", "?");
        ;
        return text;
    }

    public static OAuth2Token getOAuth2Token()
    {

        OAuth2Token token = null;
        ConfigurationBuilder cb;

        cb = new ConfigurationBuilder();
        cb.setApplicationOnlyAuthEnabled(true);

        cb.setOAuthConsumerKey(CONSUMER_KEY).setOAuthConsumerSecret(CONSUMER_SECRET);

        try {
            token = new TwitterFactory(cb.build()).getInstance().getOAuth2Token();
        }
        catch (Exception e) {
            System.out.println("Could not get OAuth2 token");
            e.printStackTrace();
            System.exit(0);
        }

        return token;
    }

    /**
     * Get a fully application-authenticated Twitter object useful for making subsequent calls.
     * 
     * @return Twitter4J Twitter object that's ready for API calls
     */
    public static Twitter getTwitter()
    {
        OAuth2Token token;

        // First step, get a "bearer" token that can be used for our requests
        token = getOAuth2Token();
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey("w4hESNxNtinR1pKEIaa4AjPq6");
        cb.setOAuthConsumerSecret("WQKPUdDk2jq0iL9LrEqrKQaMRbnTCXSSWoGC62RKoUS0IQDxTr");
        cb.setOAuthAccessToken("109802685-kXMNqDv8gLkT9ZscBVePxENZ9lTQ6KHr5e7cCtLt");
        cb.setOAuthAccessTokenSecret("8PKYROS6pbu9zX9YXgXuaV3x281zeMP2OxjBMJMw9DVlT");

        // And create the Twitter object!
        return new TwitterFactory(cb.build()).getInstance();

    }

    private static Connection connect = null;
    private static Statement statement = null;

    public static void main(String[] args)
        throws NumberFormatException, IOException, SQLException, ClassNotFoundException
    {
        init();
        
        OutputStream os = new FileOutputStream(new File("output"), true);
        int totalTweets = 0;

        ResultSet rs = statement.executeQuery("select max(id) as id from tweet");
        long maxPrevID = -1, maxID = -1;
        while (rs.next()) {
            maxPrevID = rs.getLong("id");
        }

        Twitter twitter = getTwitter();

        // Now do a simple search to show that the tokens work
        try {
            Map<String, RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus("search");

            // This finds the rate limit specifically for doing the search API call we use in this
            // program
            RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");

            // Always nice to see these things when debugging code...
            System.out.printf(
                    "You have %d calls remaining out of %d, Limit resets in %d seconds\n",
                    searchTweetsRateLimit.getRemaining(), searchTweetsRateLimit.getLimit(),
                    searchTweetsRateLimit.getSecondsUntilReset());

            // This is the loop that retrieve multiple blocks of tweets from Twitter
            for (int queryNumber = 0; queryNumber < MAX_QUERIES; queryNumber++) {
                System.out.printf("\n\n!!! Starting loop %d\n\n", queryNumber);

                // Do we need to delay because we've already hit our rate limits?
                if (searchTweetsRateLimit.getRemaining() == 0) {
                    // Yes we do, unfortunately ...
                    System.out.printf("!!! Sleeping for %d seconds due to rate limits\n",
                            searchTweetsRateLimit.getSecondsUntilReset());
                    Thread.sleep((searchTweetsRateLimit.getSecondsUntilReset() + 2) * 1000l);
                }

                Query q = new Query("lang:am"); // Search for tweets that contains this term
                q.setCount(TWEETS_PER_QUERY); // How many tweets, max, to retrieve
                q.setLang("am"); // English language tweets, please

                if (maxID == maxPrevID) {
                    System.out.println("Done. No more data");
                    break;
                }
                else if (maxID > maxPrevID) {
                    q.setMaxId(maxID-1);
                }
                else if (maxPrevID > 0) {
                    q.setSinceId(maxPrevID - 1);
                }

                // This actually does the search on Twitter and makes the call across the network
                QueryResult r = twitter.search(q);

                if (r.getTweets().size() == 0) {

                    break; // Nothing? We must be done
                }
                for (Status s : r.getTweets()) // Loop through all the tweets...
                {
                    // Increment our count of tweets retrieved
                    totalTweets++;
                    maxID = s.getId();
                    if (maxID <= maxPrevID) {
                        System.out.println("done! -- previous day data are in db already");
                       break;
                    }
                    // Do something with the tweet....
                    System.out.printf("At %s, @%-20s said:  %s\n", s.getCreatedAt().toString(), s
                            .getUser().getScreenName(), cleanText(s.getText()));
                    String user = s.getUser().getScreenName();
                    //String dayFormat = s.getCreatedAt().getYear()+"-"+s.getCreatedAt().getMonth()+1+"-"+s.getCreatedAt().getDate();
                   // String time = s.getCreatedAt().getHours()+"-"+s.getCreatedAt().getMinutes();
                    String text = cleanText(s.getText());
                    String location = s.getPlace() == null ? "NA" : s.getPlace().getCountry();

                    String dayFormat = new SimpleDateFormat("yyyy-MM-dd").format(s.getCreatedAt());
                    String time = new SimpleDateFormat("HH:mm:ss").format(s.getCreatedAt());
                    
                    rs = statement.executeQuery("select id as id from tweet where id = " + maxID);
                    if (rs.first()) {
                        System.out.println(text + " is in db already");
                        break;
                    }
                    String query = "insert into tweet (id,tweet,date,time, user, location) values('"
                            + s.getId() + "','" + text + "','" + dayFormat + "','" + time + "','" + user + "','"
                            + location + "')";
                    statement.addBatch(query);
                    IOUtils.write(user + "\t" + location + "\t" + dayFormat + "\t" + time + "\t" + text + "\n", os,
                            "UTF8");

                }

                statement.executeBatch();
                searchTweetsRateLimit = r.getRateLimitStatus();
            }

        }
        catch (Exception e) {
            // Catch all -- you're going to read the stack trace and figure out what needs to be
            // done to fix it
            System.out.println("That didn't work well...wonder why?");

            e.printStackTrace();
        }

        System.out.printf("\n\nA total of %d tweets retrieved\n", totalTweets);
        // That's all, folks!

    }

    private static void init()
        throws ClassNotFoundException, SQLException
    {
        Class.forName("com.mysql.jdbc.Driver");
        // setup the connection with the DB.
        connect = DriverManager.getConnection("jdbc:mysql://localhost/amtweet?"
                + "user=root&password=");

        // statements allow to issue SQL queries to the database
        statement = connect.createStatement();
    }
}
