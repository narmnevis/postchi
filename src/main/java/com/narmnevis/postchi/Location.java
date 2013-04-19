package com.narmnevis.postchi;

/**
 * An abstraction for location that is used by postchi.
 * 
 * @author nobeh
 * @since 1.0
 * 
 */
public interface Location {

	/**
	 * @return
	 */
	String getRegion();

	/**
	 * @return
	 */
	String getCity();

	/**
	 * @return
	 */
	String getStreet();

	/**
	 * @return
	 */
	String getPostCode();

}
