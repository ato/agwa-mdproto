package mdproto.scrapers;

import java.io.IOException;

import mdproto.Output;
import mdproto.Publication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HealthScraper {
	public static void listPublications(Output out) throws IOException {
		Document doc = Jsoup.connect("http://content.webarchive.nla.gov.au/gov/wayback/20140801071538/http://www.health.gov.au/internet/main/publishing.nsf/Content/publications-all").get();
		for (Element row : doc.select("#content > div > div > div > table > tbody > tr")) {
			Elements link = row.select("td:nth-child(1) > h3 > a");
			Publication pub = new Publication();
			pub.setTitle(link.attr("title"));
			pub.setUrl(link.attr("href"));
			pub.setSummary(row.select("td:nth-child(1) > p").text());
			pub.setDatePublished(row.select("td:nth-child(2)").text());
			pub.setPublisher("Australian Governement Department of Health");
			out.emit(pub);			
		}
	}
	
	public static void enrichHtml(Publication pub) throws IOException {
		Document pubdoc = Jsoup.connect(pub.getUrl()).get();
		Elements pdfLinks = pubdoc.select("a[href$=.pdf]");
		if (!pdfLinks.isEmpty()) {
			pub.setUrl(pdfLinks.first().attr("href"));
		}
	}
}
