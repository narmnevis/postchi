package com.narmnevis.postchi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

/**
 * A default implementation of {@link Postchi}. It tries to find the source of
 * the post code files using either a system property or an environment variable
 * with name {@code postchi.source}.
 * 
 * @author behroozn
 * @since 1.0
 * 
 */
public class DefaultPostchi implements Postchi {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public final static String POSTCHI_SOURCE = "postchi.source";

	private final Map<String, Location> locations = new TreeMap<>();

	/**
	 * Initiates postchi service using either system property or environment
	 * variable with name {@code postchi.source}.
	 * 
	 * @throws IOException
	 */
	public DefaultPostchi() throws IOException {
		this(System.getProperty(POSTCHI_SOURCE, null) == null ? System.getenv(POSTCHI_SOURCE) : System
				.getProperty(POSTCHI_SOURCE));
	}

	/**
	 * @param source
	 *            The absolute path to the source file that contains post code
	 *            information
	 * @throws IOException
	 */
	public DefaultPostchi(String source) throws IOException {
		if (source == null) {
			throw new IllegalArgumentException(
					"Configure 'postchi.source' system property or environment variable. Or, use another constructor.");
		}
		Path sourcePath = Paths.get(source);
		if (!Files.exists(sourcePath)) {
			throw new IllegalArgumentException("Cannot read from 'postchi.source'. The file does not exist: "
					+ sourcePath);
		}
		load(Files.newInputStream(sourcePath));
	}

	/**
	 * @param source
	 *            An instance of {@link File} that points to the source of post
	 *            code information.
	 * @throws IOException
	 */
	public DefaultPostchi(File source) throws IOException {
		if (!Files.exists(source.toPath())) {
			throw new IllegalArgumentException("Cannot read from 'postchi.source'. The file does not exist: "
					+ source.getAbsolutePath());
		}
		load(Files.newInputStream(source.toPath()));
	}

	public DefaultPostchi(InputStream source) {
		load(source);
	}

	@Override
	public String getRegion(String postcode) {
		postcode = postcode.replaceAll("\\s", "").toUpperCase();
		Location location = locations.get(postcode);
		return location == null ? null : location.getRegion();
	}

	@Override
	public String getCity(String postcode) {
		postcode = postcode.replaceAll("\\s", "").toUpperCase();
		Location location = locations.get(postcode);
		return location == null ? null : location.getCity();
	}

	@Override
	public String getStreet(String postcode) {
		postcode = postcode.replaceAll("\\s", "").toUpperCase();
		Location location = locations.get(postcode);
		return location == null ? null : location.getStreet();
	}

	@Override
	public Location getLocation(String postcode) {
		postcode = postcode.replaceAll("\\s", "").toUpperCase();
		return locations.get(postcode);
	}

	/**
	 * @param source
	 */
	protected void load(InputStream source) {
		try {
			long start = System.nanoTime();
			ICsvBeanReader reader = new CsvBeanReader(new BufferedReader(new InputStreamReader(source)),
					CsvPreference.STANDARD_PREFERENCE);
			SimpleLocation location = null;
			do {
				try {
					location = reader.read(SimpleLocation.class, SimpleLocation.PROPERTIES);
					if (location != null && !location.getPostCode().equals("6PP")) {
						location.setPostCode(location.getPostCode().replaceAll("\\s", "").toUpperCase());
						locations.put(location.getPostCode(), location);
					}
				} catch (Exception e) {
					logger.error("Failed to fetch the next post code address record: {}", e.getMessage());
				}
			} while (location != null);
			reader.close();
			long end = System.nanoTime();
			logger.info("Loaded a number of {} post code locations in {} seconds.", locations.keySet().size() + "", ""
					+ TimeUnit.NANOSECONDS.toSeconds(end - start));
		} catch (IOException e) {
			throw new IllegalStateException("Failed to load post codes into postchi using source: ", e);
		}
	}

}
