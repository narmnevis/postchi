package com.narmnevis.postchi.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import com.narmnevis.postchi.Postchi;

/**
 * The REST entry point for Postchi services.
 * 
 * @author behroozn
 * @since 1.0
 */
@Path("")
@Provider
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.TEXT_PLAIN)
public class PostchiResource {

	private final Postchi postchi;

	public PostchiResource(Postchi postchi) {
		this.postchi = postchi;
	}

	@GET
	@Path("/{postcode}/region")
	public Response getRegion(@PathParam("postcode") String postcode) {
		String region = postchi.getRegion(postcode);
		if (region == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(region).build();
	}

	@GET
	@Path("/{postcode}/city")
	public Response getCity(@PathParam("postcode") String postcode) {
		String city = postchi.getCity(postcode);
		if (city == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(city).build();
	}

	@GET
	@Path("/{postcode}/street")
	public Response getStreet(@PathParam("postcode") String postcode) {
		String street = postchi.getStreet(postcode);
		if (street == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(street).build();
	}

}
