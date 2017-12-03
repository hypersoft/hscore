/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package git.hsusa.core.io;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

/**
 * @author Attila Szegedi
 * @version $Id: SourceReader.java,v 1.2 2010/02/15 19:31:17 szegedia%freemail.hu Exp $
 */
public class SourceReader
{
    public static URL toUrl(String path) {
        // Assume path is URL if it contains a colon and there are at least
        // 2 characters in the protocol part. The later allows under Windows
        // to interpret paths with driver letter as file, not URL.
        if (path.indexOf(':') >= 2) {
            try {
                return new URL(path);
            } catch (MalformedURLException ex) {
                // not a URL
            }
        }
        return null;
    }

    private static byte[] readStream(InputStream is, int initialBufferCapacity) throws IOException {
        if (initialBufferCapacity <= 0) {
            throw new IllegalArgumentException("Bad initialBufferCapacity: " + initialBufferCapacity);
        } else {
            byte[] buffer = new byte[initialBufferCapacity];
            int cursor = 0;

            while(true) {
                int n = is.read(buffer, cursor, buffer.length - cursor);
                if (n < 0) {
                    if (cursor != buffer.length) {
                        byte[] tmp = new byte[cursor];
                        System.arraycopy(buffer, 0, tmp, 0, cursor);
                        buffer = tmp;
                    }

                    return buffer;
                }

                cursor += n;
                if (cursor == buffer.length) {
                    byte[] tmp = new byte[buffer.length * 2];
                    System.arraycopy(buffer, 0, tmp, 0, cursor);
                    buffer = tmp;
                }
            }
        }
    }

    public static Object readFileOrUrl(String path, boolean convertToString,
            String defaultEncoding, boolean allowRemote) throws IOException
    {

        URL url = toUrl(path);
        InputStream is = null;
        int capacityHint = 0;
        String encoding;
        final String contentType;
        byte[] data;
        try {
            if (url == null) {
                File file = new File(path);
                contentType = encoding = null;
                capacityHint = (int)file.length();
                is = new FileInputStream(file);
            } else {
                if (allowRemote == false && path.startsWith("file:") == false) {
                    throw new RuntimeException
                      ("remote content access is disabled for this method call");
                }
                URLConnection uc = url.openConnection();
                is = uc.getInputStream();
                if(convertToString) {
                    ParsedContentType pct = new ParsedContentType(uc.getContentType());
                    contentType = pct.getContentType();
                    encoding = pct.getEncoding();
                }
                else {
                    contentType = encoding = null;
                }
                capacityHint = uc.getContentLength();
                // Ignore insane values for Content-Length
                if (capacityHint > (1 << 20)) {
                    capacityHint = -1;
                }
            }
            if (capacityHint <= 0) {
                capacityHint = 4096;
            }

            data = readStream(is, capacityHint);
        } finally {
            if(is != null) {
                is.close();
            }
        }

        Object result;
        if (!convertToString) {
            result = data;
        } else {
            if(encoding == null) {
                // None explicitly specified in Content-type header. Use RFC-4329
                // 4.2.2 section to autodetect
                if(data.length > 3 && data[0] == -1 && data[1] == -2 && data[2] == 0 && data[3] == 0) {
                    encoding = "UTF-32LE";
                }
                else if(data.length > 3 && data[0] == 0 && data[1] == 0 && data[2] == -2 && data[3] == -1) {
                    encoding = "UTF-32BE";
                }
                else if(data.length > 2 && data[0] == -17 && data[1] == -69 && data[2] == -65) {
                    encoding = "UTF-8";
                }
                else if(data.length > 1 && data[0] == -1 && data[1] == -2) {
                    encoding = "UTF-16LE";
                }
                else if(data.length > 1 && data[0] == -2 && data[1] == -1) {
                    encoding = "UTF-16BE";
                }
                else {
                    // No autodetect. See if we have explicit value on command line
                    encoding = defaultEncoding;
                    if(encoding == null) {
                        // No explicit encoding specification
                        if(url == null) {
                            // Local files default to system encoding
                            encoding = System.getProperty("file.encoding");
                        }
                        else if(contentType != null && contentType.startsWith("application/")) {
                            // application/* types default to UTF-8
                            encoding = "UTF-8";
                        }
                        else {
                            // text/* MIME types default to US-ASCII
                            encoding = "US-ASCII";
                        }
                    }
                }
            }
            String strResult = new String(data, encoding);
            // Skip BOM
            if(strResult.length() > 0 && strResult.charAt(0) == '\uFEFF')
            {
                strResult = strResult.substring(1);
            }
            result = strResult;
        }
        return result;
    }

  public static final class ParsedContentType implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String contentType;
    private final String encoding;

    public ParsedContentType(String mimeType) {
      String contentType = null;
      String encoding = null;
      if (mimeType != null) {
        StringTokenizer tok = new StringTokenizer(mimeType, ";");
        if (tok.hasMoreTokens()) {
          contentType = tok.nextToken().trim();

          while(tok.hasMoreTokens()) {
            String param = tok.nextToken().trim();
            if (param.startsWith("charset=")) {
              encoding = param.substring(8).trim();
              int l = encoding.length();
              if (l > 0) {
                if (encoding.charAt(0) == '"') {
                  encoding = encoding.substring(1);
                }

                if (encoding.charAt(l - 1) == '"') {
                  encoding = encoding.substring(0, l - 1);
                }
              }
              break;
            }
          }
        }
      }

      this.contentType = contentType;
      this.encoding = encoding;
    }

    public String getContentType() {
      return this.contentType;
    }

    public String getEncoding() {
      return this.encoding;
    }
  }
}
