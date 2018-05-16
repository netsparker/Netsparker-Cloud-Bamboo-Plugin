package com.netsparker.rest;

import com.netsparker.model.ScanReport;
import com.netsparker.tasks.NetsparkerCloudScanHelper;
import com.netsparker.utility.AppCommon;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/report/{scantaskid}")
public class Report{
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getMessage(@PathParam("scantaskid") String scanTaskID) {
		boolean isModelValid = AppCommon.IsGUIDValid(scanTaskID);
		
		if (!isModelValid) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		final NetsparkerCloudScanHelper netsparkerCloudScanHelper = new NetsparkerCloudScanHelper();
		try {
			final ScanReport scanReport = netsparkerCloudScanHelper.GetScanReport(scanTaskID);
			
			return Response.ok(scanReport.getContent()).build();
		} catch (Exception ex) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}
}
