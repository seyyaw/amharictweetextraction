package tweetersearch;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class FilterNonAmharicCharacter {

	public static void main(String[] args) throws IOException {
		String fileContent = FileUtils.readFileToString(new File(args[0]), "utf8");

		fileContent = fileContent.replaceAll("([A-Za-z])+", "").replaceAll("[©@#~=፡×!-?~+.^:,\\|‹»«]", "")
				.replace("/", "").replace("<", "").replace("›", "").replace(">", "");
		FileUtils.writeStringToFile(new File(args[0]+".cleaned"), fileContent);
	}
}
