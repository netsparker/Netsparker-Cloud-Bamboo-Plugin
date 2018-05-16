package com.netsparker.rest;

import com.netsparker.model.TestConnectionModel;
import com.netsparker.model.WebsiteModelRequest;
import com.netsparker.utility.AppCommon;
import net.sf.corn.httpclient.HttpResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/testconnection")
public class TestConnection{
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getMessage(TestConnectionModel model) {
		boolean isModelValid = AppCommon.IsUrlValid(model.getApiURL()) &&
				!AppCommon.IsNullOrEmpty(model.getApiToken());
		
		if (!isModelValid) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		final WebsiteModelRequest websiteModelRequest = new WebsiteModelRequest(model.getApiURL(), model.getApiToken());
		final HttpResponse response = websiteModelRequest.getPluginWebSiteModels();
		
		if (websiteModelRequest.HasError()) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		return Response.ok("{ \"netsparkerCloudStatusCode\":\"" + response.getCode() + "\" }").build();
	}
}
