package com.narmnevis.postchi;

/**
 * Interface definition for postchi postal code services.
 * 
 * @author nobeh
 * @since 1.0
 */
public interface Postchi {

	/**
	 * @param postcode
	 * @return
	 */
	String getRegion(String postcode);

	/**
	 * @param postcode
	 * @return
	 */
	String getCity(String postcode);

	/**
	 * @param postcode
	 * @return
	 */
	String getStreet(String postcode);

	/**
	 * @param postcode
	 * @return
	 */
	Location getLocation(String postcode);

}
