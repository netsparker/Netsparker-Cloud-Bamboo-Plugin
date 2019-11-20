package com.netsparker.rest;

import com.netsparker.model.TestConnectionModel;
import com.netsparker.model.WebsiteModelRequest;
import com.netsparker.utility.AppCommon;
import org.apache.http.HttpResponse;
import org.json.simple.parser.ParseException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;

@Path("/testconnection")
public class TestConnection {

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getMessage(TestConnectionModel model) throws IOException, URISyntaxException, ParseException {
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

        return Response.ok("{ \"netsparkerCloudStatusCode\":\"" + response.getStatusLine().getStatusCode() + "\" }").build();
    }
}
