package com.narmnevis.postchi.rest;

import javax.ws.rs.core.Application;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import ch.qos.logback.classic.Level;

/**
 * A helper class to start an embedded Jetty with a Jersey JAX-RS application.
 * 
 * @author nobeh
 * @since 1.0
 * 
 */
public abstract class JettyJerseyServer {

	/**
	 * An enumerated type to specify the mode of development. A system property
	 * with name {@code mode} is looked upon initialization.
	 */
	public static enum Mode {
		DEVELOPMENT, PRODUCTION;
	}

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public JettyJerseyServer() throws Exception {
		init();
	}

	protected final void init() throws Exception {

		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();

		String modeProperty = System.getProperty("mode", Mode.DEVELOPMENT.name());
		final Mode mode = Mode.valueOf(modeProperty);

		if (mode == Mode.DEVELOPMENT) {
			ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
					.getLogger(Logger.ROOT_LOGGER_NAME);
			root.setLevel(Level.DEBUG);
		}

		Class<? extends ResourceConfig> app = getApplication();

		String contextPath = System.getProperty("contextPath", null);
		String appName = null;
		if (contextPath == null) {
			appName = app.getSimpleName().toLowerCase();
			if (appName.contains("resourceconfig")) {
				appName = appName.substring(0, appName.indexOf("resourceconfig"));
			}
			contextPath = "/" + appName;
		} else {
			if (!contextPath.startsWith("/")) {
				appName = contextPath;
				contextPath = "/" + contextPath;
			} else {
				appName = contextPath.substring(1);
			}
		}
		logger.info("Exposing application [" + appName + "] on [" + contextPath + "]");

		ServletContainer servletContainer = createJerseyServletContainer(app);
		ServletHolder servletHolder = createJettyServletHolder(servletContainer);

		configureServletHolderModeParameters(mode, servletHolder);

		ServletContextHandler contextHandler = createJettyServletContextHandler(contextPath, servletHolder);

		Server server = createServer(mode, appName);
		server.setHandler(contextHandler);

		server.start();
		server.join();
	}

	protected void configureServletHolderModeParameters(Mode mode, ServletHolder servletHolder) {
		if (mode == Mode.DEVELOPMENT) {
			servletHolder.setInitParameter("com.sun.jersey.config.feature.Debug", "true");
			servletHolder.setInitParameter("com.sun.jersey.config.feature.Trace", "true");
			servletHolder.setInitParameter("com.sun.jersey.spi.container.ContainerRequestFilters",
					"org.glassfish.jersey.filter.LoggingFilter");
			servletHolder.setInitParameter("com.sun.jersey.spi.container.ContainerResponseFilters",
					"org.glassfish.jersey.filter.LoggingFilter");
		}
	}

	protected ServletHolder createJettyServletHolder(ServletContainer servletContainer) {
		return new ServletHolder(servletContainer);
	}

	protected ServletContextHandler createJettyServletContextHandler(String contextPath, ServletHolder servletHolder) {
		ServletContextHandler contextHandler = new ServletContextHandler();
		contextHandler.setContextPath(contextPath);
		contextHandler.addServlet(servletHolder, "/*");
		return contextHandler;
	}

	/**
	 * @return Should return an instance of a JAX-RS {@link Application}.
	 * @throws Exception
	 */
	protected abstract Class<? extends ResourceConfig> getApplication() throws Exception;

	private ServletContainer createJerseyServletContainer(Class<? extends ResourceConfig> application) {
		try {
			return new ServletContainer(application.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	protected Server createServer(Mode mode, String appName) {
		switch (mode) {
		case DEVELOPMENT:
			return new Server(8080);
		case PRODUCTION:
			QueuedThreadPool pool = new QueuedThreadPool(256, 16, 60000);
			pool.setName(appName);
			return new Server(pool);
		}
		throw new IllegalArgumentException("No server can be started");
	}

}
