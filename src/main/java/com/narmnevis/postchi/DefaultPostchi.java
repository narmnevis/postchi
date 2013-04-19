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
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

/**
 * A default implementation of {@link Postchi}. It tries to find the source of
 * the post code files using either a system property or an environment variable
 * with name {@code postchi.source}.
 * 
 * @author nobeh
 * @since 1.0
 * 
 */
public class DefaultPostchi implements Postchi {

	public final static String POSTCHI_SOURCE = "postchi.source";
	public final static String POSTCHI_CITIES = "postchi.cities";

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final Map<String, Location> locations = new TreeMap<>();
	private final Map<String, String> cities = new TreeMap<>();

	/**
	 * Initiates postchi service using either system property or environment
	 * variable with name {@code postchi.source}.
	 * 
	 * @throws IOException
	 */
	public DefaultPostchi() throws IOException {
		this(getProperty(POSTCHI_SOURCE), getProperty(POSTCHI_CITIES));
	}

	/**
	 * @param source
	 *            The absolute path to the source file that contains post code
	 *            information
	 * @param citySource
	 * @throws IOException
	 */
	public DefaultPostchi(String source, String citySource) throws IOException {
		if (source == null) {
			throw new IllegalArgumentException(
					"Configure 'postchi.source' system property or environment variable. Or, use another constructor.");
		}
		Path sourcePath = Paths.get(source);
		if (!Files.exists(sourcePath)) {
			throw new IllegalArgumentException("Cannot read from 'postchi.source'. The file does not exist: "
					+ sourcePath);
		}
		Path cityPath = Paths.get(citySource);
		InputStream cityInputStream = Files.exists(cityPath) ? Files.newInputStream(cityPath) : null;
		logger.info("Using sources as [{}] and [{}].", source, citySource);
		load(Files.newInputStream(sourcePath), cityInputStream);
	}

	/**
	 * @param source
	 *            An instance of {@link File} that points to the source of post
	 *            code information.
	 * @param citySource
	 *            An instance of {@link File} that points to the source of
	 *            city/region information.
	 * @throws IOException
	 */
	public DefaultPostchi(File source, File citySource) throws IOException {
		if (!Files.exists(source.toPath())) {
			throw new IllegalArgumentException("Cannot read from 'postchi.source'. The file does not exist: "
					+ source.getAbsolutePath());
		}
		InputStream cityInputStream = Files.exists(citySource.toPath()) ? Files.newInputStream(citySource.toPath())
				: null;
		load(Files.newInputStream(source.toPath()), cityInputStream);
	}

	public DefaultPostchi(InputStream source, InputStream citySource) {
		load(source, citySource);
	}

	@Override
	public String getRegion(String postcode) {
		if (cities.isEmpty()) {
			return null;
		}
		postcode = refinePostCodeParameter(postcode);
		Location location = locations.get(postcode);
		if (location == null) {
			return null;
		}
		return cities.get(location.getCity());
	}

	@Override
	public String getCity(String postcode) {
		postcode = refinePostCodeParameter(postcode);
		Location location = locations.get(postcode);
		return location == null ? null : location.getCity();
	}

	@Override
	public String getStreet(String postcode) {
		postcode = refinePostCodeParameter(postcode);
		Location location = locations.get(postcode);
		return location == null ? null : location.getStreet();
	}

	@Override
	public Location getLocation(String postcode) {
		postcode = refinePostCodeParameter(postcode);
		return locations.get(postcode);
	}

	/**
	 * @param source
	 * @param citySource
	 */
	protected void load(InputStream source, InputStream citySource) {
		loadPostCodes(source);
		if (citySource != null) {
			loadCities(citySource);
		}
	}

	/**
	 * @param source
	 */
	protected Map<String, Location> loadPostCodes(InputStream source) {
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
			long end = System.nanoTime();
			logger.info("Loaded a number of {} post code locations in {} seconds.", locations.keySet().size() + "", ""
					+ TimeUnit.NANOSECONDS.toSeconds(end - start));
			reader.close();
			return locations;
		} catch (Exception e) {
			throw new IllegalStateException("Failed to load post codes into postchi using source: ", e);
		}
	}

	protected Map<String, String> loadCities(InputStream citySource) {
		try {
			ICsvMapReader reader = new CsvMapReader(new BufferedReader(new InputStreamReader(citySource)),
					CsvPreference.STANDARD_PREFERENCE);
			Map<String, String> row = null;
			do {
				try {
					row = reader.read("City", "Region");
					if (row != null) {
						cities.put(row.get("City"), row.get("Region"));
					}
				} catch (IOException e) {
					logger.error("Failed to fetch the next city: {}", e.getMessage());
				}
			} while (row != null);
			logger.info("Loaded {} cities and regions.", cities.size());
			reader.close();
			return cities;
		} catch (IOException e) {
			throw new IllegalStateException("Failed to load cities source: ", e);
		}
	}

	/**
	 * @param postcode
	 * @return
	 */
	protected String refinePostCodeParameter(String postcode) {
		return postcode.replaceAll("\\s", "").toUpperCase();
	}

	/**
	 * @param name
	 * @return
	 */
	protected static String getProperty(String name) {
		String value = System.getProperty(name);
		if (value != null) {
			return value;
		}
		return System.getenv(name);
	}

}
