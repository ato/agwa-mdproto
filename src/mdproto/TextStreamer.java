package mdproto;

import java.io.IOException;
import java.io.PrintWriter;

import droute.Request;

public interface TextStreamer {

	public void stream(Request request, PrintWriter out) throws IOException;
	
}
