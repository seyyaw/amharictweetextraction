package tweetersearch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterApp2
{
    public static void main(String[] args)
        throws InterruptedException, IOException
    {
        setup();
    }

    static void setup()
        throws InterruptedException, IOException
    {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey("w4hESNxNtinR1pKEIaa4AjPq6");
        cb.setOAuthConsumerSecret("WQKPUdDk2jq0iL9LrEqrKQaMRbnTCXSSWoGC62RKoUS0IQDxTr");
        cb.setOAuthAccessToken("109802685-kXMNqDv8gLkT9ZscBVePxENZ9lTQ6KHr5e7cCtLt");
        cb.setOAuthAccessTokenSecret("8PKYROS6pbu9zX9YXgXuaV3x281zeMP2OxjBMJMw9DVlT");

        Twitter twitter = new TwitterFactory(cb.build()).getInstance();
        Query query = new Query("lang:am");
        int numberOfTweets = 3946;
        long lastID = Long.MAX_VALUE;
        ArrayList<Status> tweets = new ArrayList<Status>();
        while (tweets.size() < numberOfTweets) {
            if (numberOfTweets - tweets.size() > 100)
                query.setCount(100);
            else
                query.setCount(numberOfTweets - tweets.size());
            try {
                QueryResult result = twitter.search(query);
                tweets.addAll(result.getTweets());
                System.out.println("Gathered " + tweets.size() + " tweets");
                for (Status t : tweets)
                    if (t.getId() < lastID)
                        lastID = t.getId();

            }

            catch (TwitterException te) {
                System.out.println("waiting....");
                Thread.sleep(1000 * 60 * 15);
                System.out.println("to continue....");
                continue;
            }
            query.setMaxId(lastID - 1);
        }

        OutputStream os = new FileOutputStream(new File("output"));

        for (int i = 0; i < tweets.size(); i++) {
            Status t = (Status) tweets.get(i);

            GeoLocation loc = t.getGeoLocation();

            String user = t.getUser().getScreenName();
            String msg = t.getText();
            String date = t.getCreatedAt().getYear() + "-" + t.getCreatedAt().getMonth() + "-"
                    + t.getCreatedAt().getDate();
            if (loc != null) {
                Double lat = t.getGeoLocation().getLatitude();
                Double lon = t.getGeoLocation().getLongitude();
                IOUtils.write(i + "\t" + user + "\t" + date + "\t" + msg.replace("\n", "").trim()
                        + "\n" + " located at " + lat + ", " + lon + "\n", os, "UTF8");
            }
            else
                IOUtils.write(i + "\t" + user + "\t" + date + "\t" + msg.replace("\n", "").trim()
                        + "\n", os, "UTF8");
        }
        os.close();
    }

}
