package com.narmnevis.postchi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

/**
 * @author behroozn
 */
public class DefaultPostchiTest {

	private Postchi postchi;

	@Before
	public void setUp() throws Exception {
		postchi = new DefaultPostchi(ClassLoader.getSystemResourceAsStream("zipcodes.csv"),
				ClassLoader.getSystemResourceAsStream("cities.csv"));
	}

	@Test(expected = NullPointerException.class)
	public void getAddressWithNullZipCode() throws Exception {
		Location a = postchi.getLocation(null);
		assertNull(a);
	}

	@Test
	public void getAddressWithEmptyZipCode() throws Exception {
		Location a = postchi.getLocation("");
		assertNull(a);
	}

	@Test
	public void getAddressWithSpacedZipCode() throws Exception {
		String code = "7705 PM";
		Location a = postchi.getLocation(code);
		assertNotNull(a);
		assertEquals(code.replaceAll("\\s", ""), a.getPostCode());
	}

	@Test
	public void getAddress() throws Exception {
		String code = "7705PM";
		Location a = postchi.getLocation(code);
		assertNotNull(a);
		assertEquals(code, a.getPostCode());
	}

	@Test
	public void getRegionShouldReturnValueForAnExistingCity() {
		String region = postchi.getRegion("7705PM");
		assertNotNull(region);
	}

	@Test
	public void getRegionReturnsNullForANonExistentRequest() {
		String region = postchi.getRegion("7711PM");
		assertNull(region);
	}
	
	@Test
	public void getRegionReturnsNullWhenRegionInfoIsNotLoaded() throws Exception {
		postchi = new DefaultPostchi(ClassLoader.getSystemResourceAsStream("zipcodes.csv"), null);
		assertNull(postchi.getRegion("7705PM"));
	}
}
