package mdproto;

import static droute.Response.response;
import static droute.Route.GET;
import static droute.Route.routes;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import mdproto.scrapers.AgricultureScraper;
import mdproto.scrapers.EnvironmentScraper;
import mdproto.scrapers.HealthScraper;

import org.apache.commons.io.Charsets;

import droute.Handler;
import droute.Streamable;

public class PublicationsScrapers {
	static Handler routes = routes(
			GET("/health", listAsText(HealthScraper::listPublications)),
			GET("/environment", listAsText(EnvironmentScraper::listPublications)),
			GET("/agriculture", listAsText(AgricultureScraper::listPublications)));
	
	private interface Lister {
		void list(Output out) throws IOException;
	}
	
	private static Handler listAsText(Lister lister) {
		return (req) -> {
			return response((Streamable) (out) -> {
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, Charsets.UTF_8));
				try {
					lister.list(writer::println);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				writer.flush();
			}).withHeader("Content-Type", "text/plain; charset=utf-8");
		};
	}
	
	
}
