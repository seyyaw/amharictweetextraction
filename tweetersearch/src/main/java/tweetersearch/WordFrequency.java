package tweetersearch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
/**
 * Create a word frequency from a given file. If there are already word list in a separate file, use the
 * getOldWordFrequency (...) method
 * @author seid Muhie Yimam
 *
 */
public class WordFrequency
{

    public static void main(String[] args)
        throws IllegalArgumentException, IOException
    {
        LineIterator it = new LineIterator(new FileReader(new File(args[0])));
        // if there are already a frequency list from other source, uncomment this line
        // Map<String, Integer> wordFreq = getOldWordFrequency("/tmp/freq.txt");
        Map<String, Integer> wordFreq = new HashMap<String, Integer>();

        while (it.hasNext()) {
            StringTokenizer st = new StringTokenizer(it.next(), " ");
            while (st.hasMoreTokens()) {
                String word = st.nextToken();
                if (wordFreq.get(word) != null) {
                    wordFreq.put(word, wordFreq.get(word) + 1);
                }
                else {
                    wordFreq.put(word, 1);
                }
            }
        }

        Map<String, Integer> sortedWordFreq = sortByComparator(wordFreq, false);
        FileOutputStream os = new FileOutputStream(new File(args[0]+".freq"));
        for (String word : sortedWordFreq.keySet()) {
            IOUtils.write(word + "\t" + sortedWordFreq.get(word) + "\n", os, "utf8");
        }

    }

    public static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap,
            final boolean order)
    {

        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(
                unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Integer>>()
        {
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2)
            {
                if (order) {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    static Map<String, Integer> getOldWordFrequency(String aFileName)
        throws IOException
    {
        String oldWordFreqContent = FileUtils.readFileToString(new File(aFileName));

        Map<String, Integer> freqMap = new HashMap<String, Integer>();
        StringTokenizer st = new StringTokenizer(oldWordFreqContent, "\n");
        while (st.hasMoreElements()) {
            String line = st.nextToken().trim();
            StringTokenizer lineSt = new StringTokenizer(line, " ");
            int freq = Integer.parseInt(lineSt.nextToken());
            String word = lineSt.nextToken();
            freqMap.put(word, freq);
        }
        return freqMap;
    }

}
