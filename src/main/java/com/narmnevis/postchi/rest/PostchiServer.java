package com.narmnevis.postchi.rest;

import java.io.IOException;

import org.glassfish.jersey.server.ResourceConfig;

import com.narmnevis.postchi.DefaultPostchi;

/**
 * @author nobeh
 * @since 1.0
 * 
 */
public class PostchiServer extends JettyJerseyServer {

	public static class PostchiResourceConfig extends ResourceConfig {

		public PostchiResourceConfig() throws IOException {
			DefaultPostchi instance = null;
			try {
				instance = new DefaultPostchi();
			} catch (Exception e) {
				instance = new DefaultPostchi(ClassLoader.getSystemResourceAsStream("zipcodes.csv"),
						ClassLoader.getSystemResourceAsStream("cities.csv"));
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
