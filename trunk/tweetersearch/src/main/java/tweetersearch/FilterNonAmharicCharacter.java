package tweetersearch;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class FilterNonAmharicCharacter {

	public static void main(String[] args) throws IOException {
		String file = FileUtils.readFileToString(new File("/tmp/tweet.txt"), "utf8");

		file = file.replaceAll("([A-Za-z])+", "").replaceAll("[©@#~=፡×!-?~+.^:,\\|‹»«]", "")
				.replace("/", "").replace("<", "").replace("›", "").replace(">", "");
		FileUtils.writeStringToFile(new File("//tmp/tweet.cleaned.txt"), file);
	}
}
