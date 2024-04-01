import java.io.*;
import java.net.URL;
import java.util.*;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author Antonio Terpin
 * @year 2016
 * Simple search engine
 */
@SuppressWarnings("removal")
public class SearchEngine {
	
	private double damping_costant = 0.8;
	private int time = 10;
	
	public Dictionary<String, Double> computePageRank(Dictionary<String, List<String>> page_graph) {
		Dictionary<String, Double> ranks = new Hashtable<>();
		int npages = page_graph.size();
		double d1 = 1.0 / npages, d2 = (1 - damping_costant) / npages;
		Enumeration<String> pages = page_graph.keys();
		while(pages.hasMoreElements()) {
			String k = pages.nextElement(); 
			ranks.put(k, new Double(d1));
		}
		for(int i = 0; i < time; i++) {
			Dictionary<String, Double> newranks = new Hashtable<>();
			pages = page_graph.keys();
			while(pages.hasMoreElements()) {
				newranks.put(pages.nextElement(), new Double(d2));
			}
			pages = page_graph.keys();
			while(pages.hasMoreElements()) {
				String page = pages.nextElement();
				List<String> outLinks = page_graph.get(page);
				for (String out : outLinks) {
					Double val = newranks.get(out);
					if (val == null) { val = new Double(0); }
					newranks.put(out, val + damping_costant * ranks.get(page) / outLinks.size());
				}
			}
			ranks = newranks;
		}
		return ranks;
	}
	
	public void orderIndex(Dictionary<String, List<String>> index, Dictionary<String, Double> ranks) {
		Enumeration<String> keys = index.keys();
		while(keys.hasMoreElements()) {
			String k = keys.nextElement();
			index.get(k).sort(new Comparator<String>() {
				@Override
				public int compare(String s1, String s2) {
					double r1 = ranks.get(s1), r2 = ranks.get(s2);
					return (r2 > r1) ? 1 : (r2 == r1) ? 0 : -1; // because doubles..
				}
			});
		}
	}
	
	public List<String> getAllLinks(String content, String seed) throws IOException {
		Document doc = Jsoup.parse(content);
		List<Element> links = doc.getElementsByTag("a");
		List<String> URLs = new LinkedList<>();
		String url;
		for(Element e : links) {
			if((url = e.attr("href")).compareTo("") != 0) { 
				if(url.charAt(0) == '/') { url = seed.concat(url.substring(1)); }
				URLs.add(url); 
			}
		}
		return URLs;
	}
	
	@SuppressWarnings("deprecation")
	public String getContent(String url) throws IOException {
		URL page = new URL(url);
		BufferedReader in = new BufferedReader(
				new InputStreamReader(page.openStream()));
		String line = "", content = "";
		while((line = in.readLine()) != null) { content += line; }
		return content;
	}
	
	private void addPageToIndex(Dictionary<String, List<String>> index, String content, String URL) {
		for(String word : content.split(" ")) {
			String w = word.toLowerCase();
			List<String> URLs;
			if((URLs = index.get(w)) == null) { index.put(w, (URLs = new LinkedList<>())); }
			if(!URLs.contains(URL.toString())) { URLs.add(URL); }
		}
	}
	
	private String getText(String htmlPage) {
		Document doc = Jsoup.parse(htmlPage);
		return doc.text();
	}
	
	public Pair<Dictionary<String, List<String>>, Dictionary<String, List<String>>> indexWeb(String urlSeed, int maxDepth) throws IOException {
		Dictionary<String, List<String>> graph = new Hashtable<String, List<String>>(), index = new Hashtable<String, List<String>>();
		Stack<Pair<String, Integer>> to_crawl = new Stack<>();
		List<String> crawled = new LinkedList<>();
		to_crawl.add(new Pair<String, Integer>(urlSeed, 0));
		while(!to_crawl.isEmpty()) {
			Pair<String, Integer> nextURLAndDepth = to_crawl.pop();
			String nextURL = nextURLAndDepth.getFirst();
			int depth = nextURLAndDepth.getSecond();
			if (depth > maxDepth) { continue; }
			System.out.println("(" + depth + ") " + nextURL.toString());
			try {
				if(!crawled.contains(nextURL.toString())) {
					crawled.add(nextURL);
					// check redirection
					Response response = Jsoup.connect(nextURL).followRedirects(false).execute();
					String redirect = null;
					if ((redirect = response.header("location")) != null) { 
						String old = nextURL;
						nextURL = redirect;
						redirect = old;
					}
					String content = getContent(nextURL);
					List<String> allLinks = getAllLinks(content, nextURL);
					for (String link : allLinks) {
						if(!crawled.contains(link) && depth + 1 < maxDepth) { to_crawl.add(new Pair<String, Integer>(link, depth + 1)); }
					}
					if(redirect != null) { nextURL = redirect; }
					graph.put(nextURL, allLinks);
					addPageToIndex(index, getText(content), nextURL); 
				}
			} catch (Exception e) { continue; }
		}
		return new Pair<Dictionary<String,List<String>>, Dictionary<String,List<String>>>(graph, index);
	}
	
	public String[] lookup(String keyword) {
		// TODO implement 
		return null;
	}
}
