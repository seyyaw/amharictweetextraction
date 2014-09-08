package tweetersearch;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class Ngram {

	public static Map<String, Integer> ngrams(int n, String str) {
		Map<String, Integer> ngrams = new HashMap<String, Integer>();
		String[] words = str.split(" ");
		for (int i = 0; i < words.length - n + 1; i++) {
			String ngram = concat(words, i, i + n).trim();
			if (ngram.split(" ").length < 2) {
                continue;
            }
			if (ngrams.containsKey(ngram)) {
				ngrams.put(ngram, ngrams.get(ngram) + 1);
			} else {
				ngrams.put(ngram, 1);
			}
		}
		return ngrams;
	}

	public static String concat(String[] words, int start, int end) {
		StringBuilder sb = new StringBuilder();
		for (int i = start; i < end; i++) {
            sb.append((i > start ? " " : "") + words[i]);
        }
		return sb.toString();
	}

	public static void main(String[] args) throws IOException {
		String tweet = FileUtils
				.readFileToString(new File(args[0])).trim()
				.replaceAll("\n", " ");
		 tweet = normalize(tweet);
		Map<String, Integer> ngramsMap = ngrams(3, tweet);
		ngramsMap = WordFrequency.sortByComparator(ngramsMap, false);
		StringBuffer sb = new StringBuffer();

		for (String ngram : ngramsMap.keySet()) {
            sb.append(ngram + "\t" + ngramsMap.get(ngram) + "\n");
        }

		FileUtils
				.writeStringToFile(new File(args[0]+".ngram"), sb.toString());
	}

	public static String normalize(String qwords) throws IOException {
		StringBuffer sb = new StringBuffer();
		sb.append(qwords.replace("ሥ", "ስ").replace("ሃ", "ሀ").replace("ሓ", "ሀ")
				.replace("ሐ", "ሀ").replace("ቼ", "ቸ").replace("ዓ", "አ")
				.replace("ኣ", "አ").replace("ዐ", "አ").replace("ዉ", "ው")
				.replace("ኅ", "ሀ").replace("ኀ", "ሀ").replace("ዬ", "የ")
				.replace("ጸ", "ፀ").replace("ሠ", "ሰ").replace("ሡ", "ሱ")
				.replace("ሢ", "ሲ").replace("ሣ", "ሳ").replace("ሤ", "ሴ")
				.replace("ሦ", "ሶ").replace("ኁ", "ሁ").replace("ኂ", "ሂ")
				.replace("ኄ", "ሄ").replace("ኆ", "ሆ").replace("ኅ", "ህ")
				.replace("ሑ", "ሁ").replace("ሒ", "ሂ").replace("ሔ", "ሄ")
				.replace("ሕ", "ህ").replace("ሖ", "ሆ").replace("ዑ", "ኡ")
				.replace("ዒ", "ኢ").replace("ዔ", "ኤ").replace("ዕ", "እ")
				.replace("ዖ", "ኦ").replace("ጽ", "ፅ").replace("ጹ", "ፁ")
				.replace("ጺ", "ፂ").replace("ጻ", "ፃ").replace("ጼ", "ፄ")
				.replace("ጾ", "ፆ").replace("ጪ", "ጭ").replace("ዲ", "ድ"));
		return sb.toString();
	}
}