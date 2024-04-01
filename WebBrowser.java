import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author Antonio Terpin
 * @year 2016
 * Simple web browser
 */
public class WebBrowser {

	static SearchEngine engine = new SearchEngine();
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws URISyntaxException 
	 */
	public static void main(String[] args) throws MalformedURLException, IOException, URISyntaxException {
		/**
		 * TODO: Implement the lookup
		 * TODO: Allow to pick the depth from args
		 */
		String seed = "https://github.com/antonioterpin";
		Pair<Dictionary<String, List<String>>, Dictionary<String, List<String>>> index = engine.indexWeb(seed, 2);
		System.out.println("Currently, crawls and computes only page rank of seed page, max depth 2.");
		System.out.println(engine.computePageRank(index.getFirst()));
	}

}
