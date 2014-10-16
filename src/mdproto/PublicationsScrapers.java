package mdproto;

import static droute.Response.response;
import static droute.Route.GET;
import static droute.Route.routes;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import droute.Handler;
import droute.Request;
import droute.Streamable;

public class PublicationsScrapers {
	static Handler routes = routes(
			GET("/health", textStream(PublicationsScrapers::health)),
			GET("/environment", textStream(PublicationsScrapers::environment)),
			GET("/agriculture", textStream(PublicationsScrapers::agriculture)));
	
	static void agriculture(Request request, PrintWriter out) throws IOException {
		String base = "http://content.webarchive.nla.gov.au/gov/wayback/20140212120911/http://www.daff.gov.au/publications/by_date";
		out.write("# extracted from " + base + "\n\n");
		for (int i = 1; i <= 45; i++) {
			Document doc = Jsoup.connect(base + "?result_4101_result_page="+i).get();			
			Elements rows = doc.select("#page_content > ul > li");
			for (Element row: rows) {
				Elements a = row.select("a");
				Elements pubtext = row.select("span.pubtext");
				out.println("Title: " + a.text());
				out.println(pubtext.text());
				out.println("URL: " + a.attr("href"));
				out.write("\n");
			}
		}
	}

	static void environment(Request request, PrintWriter out) throws IOException {
		String base = "http://content.webarchive.nla.gov.au/gov/wayback/20140221135119/http://www.environment.gov.au/resources";
		out.println("# extracted from " + base);
		out.println("# INCOMPLETE: only the first 9 pages were captured by the crawl");
		out.println();
		for (int i = 0; i <= 9; i++) {
			Document doc = Jsoup.connect(base + (i > 0 ? "?page=" + i : "")).get();
			for (Element row : doc.select(".views-row")) {
				Elements link = row.select(".views-field-title a");
				Elements year = row.select(".view-resource-publish-year .date-display-single");
				out.println("Title: " + link.select("h2").first().ownText());
				if (year.hasText()) {
					out.println("Published: " + year.text());
				}
				out.println("URL: " + link.attr("href"));
				out.println("Preview: "	+ row.select(".views-field-field-summary").text());
				out.println();
			}
		}
	}

	
	static void health(Request request, PrintWriter out) throws IOException {
		String listUrl = "http://content.webarchive.nla.gov.au/gov/wayback/20140801071538/http://www.health.gov.au/internet/main/publishing.nsf/Content/publications-all";
		out.write("# extracted from " + listUrl + "\n\n");
		Document doc = Jsoup.connect(listUrl).get();
		Elements rows = doc.select("#content > div > div > div > table > tbody > tr");
		for (Element row : rows) {
			Elements p = row.select("td:nth-child(1) > p");
			Elements anchor = row.select("td:nth-child(1) > h3 > a");
			Elements year = row.select("td:nth-child(2)");

			out.println("Title: " + anchor.attr("title"));
			out.println("Published: " + year.text());
			out.println("URL: " + anchor.attr("href"));
			if (p.hasText()) {
				out.println("Summary: " + p.text());
			}
			out.println();
		}
	}
	
	private static final Charset UTF8 = Charset.forName("utf-8");
	private static Handler textStream(TextStreamer streamer) {
		return (req) -> {
			return response((Streamable) (out) -> {
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, UTF8));
				try {
					streamer.stream(req, writer);
					writer.flush();
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}).withHeader("Content-Type", "text/plain; charset=utf-8");
		};
	}
}
