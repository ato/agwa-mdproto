package mdproto.scrapers;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mdproto.Publication;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

public class IsxnEnricher {
	private static final Pattern ISBN_RE = Pattern.compile(
			"^ISBN:\\s*([0-9-]{10,})", Pattern.MULTILINE);
	private static final Pattern ISSN_RE = Pattern.compile(
			"^ISSN:\\s*([0-9-]{10,})", Pattern.MULTILINE);

	public static void enrich(Publication pub) throws IOException {
		Tika tika = new Tika();
		tika.setMaxStringLength(4000);
		String s;
		try {
			s = tika.parseToString(new URL(pub.getPdfUrl()).openStream());
		} catch (TikaException e) {
			throw new RuntimeException(e);
		}
		{
			Matcher m = ISBN_RE.matcher(s);
			if (m.find()) {
				pub.setIsbn(m.group(1));
			}
		}
		{
			Matcher m = ISSN_RE.matcher(s);
			if (m.find()) {
				pub.setIssn(m.group(1));
			}
		}
	}
}
