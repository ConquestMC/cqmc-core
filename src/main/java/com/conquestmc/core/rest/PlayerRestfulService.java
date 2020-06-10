package com.conquestmc.core.rest;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.bson.Document;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class PlayerRestfulService {

    private final String basePath = "http://play.conquest-mc.com:8080/users";
    private final String userPath = "/user";

    private HttpClient client;

    public PlayerRestfulService() {
        this.client = new HttpClient();
    }

    public List<Document> getAllUserDocuments() {
        GetMethod method = new GetMethod(basePath);
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

        List<Document> players = Lists.newArrayList();

        try {
            int statusCode = client.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + method.getStatusLine());
            }

            byte[] responseBody = method.getResponseBody();
            String responseString = new String(responseBody);

            JsonArray array = new Gson().fromJson(responseString, JsonArray.class);

            for (int i = 0; i < array.size(); i++) {
                JsonObject object = array.get(i).getAsJsonObject();
                Document d = Document.parse(object.toString());
                players.add(d);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }
        return players;
    }

    public Document fetchUser(UUID uuid) {
        GetMethod method = new GetMethod(basePath + userPath);
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
        method.getParams().setParameter("uuid", uuid.toString());
        Document doc = null;

        try {
            int statusCode = client.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + method.getStatusLine());
            }

            byte[] responseBody = method.getResponseBody();
            String responseString = new String(responseBody);

            JsonObject fromString = new JsonParser().parse(responseString).getAsJsonObject();
            doc = Document.parse(fromString.toString());
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }
        return doc;
    }
}
