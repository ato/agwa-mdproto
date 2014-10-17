package mdproto.scrapers;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mdproto.Output;
import mdproto.Publication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EnvironmentScraper {
	public static void listPublications(Output out) throws IOException {
		for (int i = 0; i <= 9; i++) {
			Document doc = Jsoup.connect("http://content.webarchive.nla.gov.au/gov/wayback/20140221135119/http://www.environment.gov.au/resources" + (i > 0 ? "?page=" + i : "")).get();
			for (Element row : doc.select(".views-row")) {
				Elements link = row.select(".views-field-title a");
				Elements year = row.select(".view-resource-publish-year .date-display-single");
				Publication pub = new Publication();
				pub.setTitle(link.select("h2").first().ownText());
				pub.setUrl(link.attr("href"));
				pub.setSummary(row.select(".views-field-field-summary").text());
				pub.setPublisher("Australian Governement Department of the Environment");
				pub.setDatePublished(year.text());
				out.emit(pub);
			}
		}
	}
	
	private static final Pattern AUTHOR_YEAR_RE = Pattern.compile("(.*), (\\d{4})");
	
	public static void htmlEnrich(Publication pub) throws IOException {
		if (pub.getUrl() == null) {
			return;
		}
		Document doc = Jsoup.connect(pub.getUrl()).get();
		String authorYear = doc.select(".field-name-field-resources-author-year-display-ds-field").text();
		Matcher m = AUTHOR_YEAR_RE.matcher(authorYear);
		if (m.matches()) {
			pub.setAuthor(m.group(1));
		}
		Elements pdfLinks = doc.select(".field-name-field-resource-files a.file-pdf");
		if (!pdfLinks.isEmpty()) {
			pub.setPdfUrl(pdfLinks.first().attr("href"));
		}
	}
}
