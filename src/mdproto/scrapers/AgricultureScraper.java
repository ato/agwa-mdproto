package mdproto.scrapers;

import java.io.IOException;

import mdproto.Output;
import mdproto.Publication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AgricultureScraper {
	public static void listPublications(Output out) throws IOException {
		for (int i = 1; i <= 45; i++) {
			Document doc = Jsoup.connect("http://content.webarchive.nla.gov.au/gov/wayback/20140212120911/http://www.daff.gov.au/publications/by_date?result_4101_result_page=" + i).get();			
			for (Element row: doc.select("#page_content > ul > li")) {
				Elements link = row.select("a");
				Elements pubtext = row.select("span.pubtext");
				Publication pub = new Publication();
				pub.setTitle(link.text());
				pub.setUrl(link.attr("href"));
				pub.setDatePublished(pubtext.text().replaceAll("^Published: ", ""));
				pub.setPublisher("Australian Governement Department of Agriculture");
				out.emit(pub);
			}
		}
	}
}
