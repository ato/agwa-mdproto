package mdproto;

import com.mongodb.ReflectionDBObject;

public class Publication extends ReflectionDBObject {
	
	String title;
	String url;
	String datePublished;
	String summary;
	String publisher;
	String pdfUrl;
	String isbn;
	String issn;
	
	public String getIssn() {
		return issn;
	}

	public void setIssn(String issn) {
		this.issn = issn;
	}

	private String author;

	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDatePublished() {
		return datePublished;
	}

	public void setDatePublished(String datePublished) {
		this.datePublished = datePublished;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getPdfUrl() {
		return pdfUrl;
	}

	public void setPdfUrl(String pdfUrl) {
		this.pdfUrl = pdfUrl;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String toString() {
		StringBuffer s = new StringBuffer();
		for (String key: keySet()) {
			Object value = get(key);
			if (value != null) {
				s.append(key);
				s.append(": ");
				s.append(get(key));
				s.append("\n");
			}
		}
		return s.toString();
	}
}
