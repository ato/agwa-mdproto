package mdproto;

import static droute.Response.render;
import static droute.Response.response;
import static droute.Route.GET;
import static droute.Route.notFound;
import static droute.Route.resources;
import static droute.Route.routes;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.input.ReaderInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ParsingReader;
import org.xml.sax.ContentHandler;

import droute.Csrf;
import droute.FreeMarkerHandler;
import droute.Handler;
import droute.Request;
import droute.Response;
import droute.Streamable;
import freemarker.template.Configuration;

public class MdProto implements Handler {

	Handler routes = routes(
			resources("/webjars", "META-INF/resources/webjars"),
			GET("/extract", this::extract), GET("/text", this::text),
			GET("/xml", this::xml),
			PublicationsScrapers.routes,
			GET("/", (request) -> {
				return render("index.ftl");
			}), notFound("404 Not foundx"));

	Handler app;

	public Response extract(Request request) {
		return render("extract.ftl");
	}

	private ContentHandler getXmlContentHandler(Writer writer)
			throws TransformerConfigurationException {
		SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory
				.newInstance();
		TransformerHandler handler = factory.newTransformerHandler();
		handler.getTransformer().setOutputProperty(OutputKeys.METHOD, "xml");
		handler.setResult(new StreamResult(writer));
		return handler;
	}

	public Response xml(Request request) {
		try {
			Parser parser = new AutoDetectParser();
			URL url = new URL(request.param("url"));
			if (!url.getProtocol().equals("http")) {
				return response(400, "http urls only");
			}
			Metadata metadata = new Metadata();
			return Response.response(new Streamable() {

				@Override
				public void writeTo(OutputStream out) {
					Writer w = new OutputStreamWriter(out, Charset
							.forName("utf-8"));
					try {
						ContentHandler handler = getXmlContentHandler(w);
						parser.parse(url.openStream(), handler, metadata,
								new ParseContext());
						w.flush();
						w.close();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}

			}).withHeader("Content-Type",
					"application/xhtml+xml; charset=utf-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Response text(Request request) {
		try {
			URL url = new URL(request.param("url"));
			if (!url.getProtocol().equals("http")) {
				return response(400, "http urls only");
			}
			ParsingReader rdr = new ParsingReader(url.openStream());
			return response(new ReaderInputStream(rdr, "utf-8"))
					.withHeader("Content-Type", "text/plain; charset=utf-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public MdProto() throws IOException {
		Configuration config = FreeMarkerHandler.defaultConfiguration(
				MdProto.class, "/views");
		config.addAutoInclude("layout.ftl");
		app = new FreeMarkerHandler(config, routes);
		app = Csrf.protect(app);
	}

	@Override
	public Response handle(Request request) {
		return app.handle(request);
	}
}
