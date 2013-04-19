package com.narmnevis.postchi.rest;

import java.io.IOException;

import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.narmnevis.postchi.DefaultPostchi;

/**
 * @author nobeh
 * @since 1.0
 * 
 */
public class PostchiServer extends JettyJerseyServer {

	public static class PostchiResourceConfig extends ResourceConfig {
		
		private final Logger logger = LoggerFactory.getLogger(getClass());

		public PostchiResourceConfig() throws IOException {
			DefaultPostchi instance = null;
			try {
				instance = new DefaultPostchi();
				logger.info("Initialized a postchi service with configured parameters: " + instance);
			} catch (Exception e) {
				instance = new DefaultPostchi(ClassLoader.getSystemResourceAsStream("zipcodes.csv"),
						ClassLoader.getSystemResourceAsStream("cities.csv"));
				logger.info("Initialized a postchi service with default values: " + instance);
			}
			PostchiResource postchiResource = new PostchiResource(instance);
			registerInstances(postchiResource);
		}

	}

	public PostchiServer() throws Exception {
	}

	@Override
	protected Class<PostchiResourceConfig> getApplication() throws Exception {
		return PostchiResourceConfig.class;
	}

	public static void main(String[] args) throws Exception {
		new PostchiServer();
	}

}
