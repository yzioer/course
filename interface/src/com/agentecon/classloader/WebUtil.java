package com.agentecon.classloader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import com.agentecon.util.IOUtils;

public class WebUtil {

	private final static String API_ADDRESS = "https://api.github.com";
	private final static String ACCESS_SECRETS = loadSecrets();

	private static String loadSecrets() {
		Path path = FileSystems.getDefault().getPath("../..", "github-secret.txt");
		try {
			return Files.readAllLines(path, Charset.defaultCharset()).get(0);
		} catch (IOException e) {
			System.out.println("Could not find api secrets file on " + path.toAbsolutePath());
			return "";
		}
	}
	
	public static String addSecret(String address) {
		if (address.contains(API_ADDRESS)) {
			if (address.contains("?")) {
				return address + "&" + ACCESS_SECRETS.substring(1);
			} else {
				return address + ACCESS_SECRETS;
			}
		} else {
			return address;
		}
	}
	
	public static String readHttp(String address) throws FileNotFoundException, IOException {
		address = addSecret(address);
		String content = "";
		String nextPage = null;
		while (address != null) {
			long t0 = System.nanoTime();
			URL url = new URL(address);
			URLConnection conn = url.openConnection();
			InputStream stream = conn.getInputStream();
			try {
				content += new String(IOUtils.readData(stream));
				nextPage = getNextPageUrl(conn);
			} finally {
				stream.close();
			}
			long t1 = System.nanoTime();
			System.out.println((t1 - t0) / 1000000 + "ms spent reading " + address);
			address = nextPage;
		}
		return content;
	}

	protected static String getNextPageUrl(URLConnection conn) {
		String next = conn.getHeaderField("Link");
		if (next != null && next.contains("next")) {
			int open = next.indexOf('<');
			int close = next.indexOf('>');
			return next.substring(open + 1, close);
		} else {
			return null;
		}
	}

	public static String extract(String content, String what, int[] pos) {
		String item = "\"" + what + "\":\"";
		int pos2 = content.indexOf(item, pos[0]);
		if (pos2 >= 0) {
			int urlEnd = content.indexOf('"', pos2 + item.length());
			pos[0] = urlEnd;
			return content.substring(pos2 + item.length(), urlEnd);
		} else {
			return null;
		}
	}

	public static String readGitApi(String owner, String repo, String command, String path, String branch) throws IOException {
		String address = API_ADDRESS + "/repos/" + owner + "/" + repo + "/" + command + "/" + path + "?ref=" + branch;
		return readHttp(address);
	}

}
