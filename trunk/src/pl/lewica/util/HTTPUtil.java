/*
 Copyright 2011 lewica.pl

 Licensed under the Apache Licence, Version 2.0 (the "Licence");
 you may not use this file except in compliance with the Licence.
 You may obtain a copy of the Licence at

    http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the Licence is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the Licence for the specific language governing permissions and
 limitations under the Licence. 
*/
package pl.lewica.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * A collection of utilities related to HTTP connections.
 * @author Krzysztof Kobrzak
 */
public class HTTPUtil {
	private final URL webURL;


	public HTTPUtil(String URL) {
		try {
			this.webURL = new URL(URL);
		} catch (MalformedURLException errMalformedURL) {
			throw new RuntimeException(errMalformedURL);
		}
	}


	/**
	 * Downloads a resource from the network.
	 * @return
	 * @throws IOException
	 */
	public InputStream getInputStream()
			throws IOException {		
		URLConnection	urlConn	= webURL.openConnection();
		// TODO This doesn't seem to work on Android
		urlConn.setConnectTimeout(5 * 1000);

		return urlConn.getInputStream();
	}
}
