package com.qa.api.tests.Get;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

public class GetApiCall {
    Playwright playwright;
    APIRequest request;
    APIRequestContext requestContext;

    @BeforeTest
    public void setup(){
        playwright=Playwright.create();
        request=playwright.request();
        requestContext=request.newContext();
    }

    @Test
    public void getUsersDetailsTest() throws IOException {
        APIResponse response=requestContext.get("https://gorest.co.in/public/v2/users");
        Assert.assertEquals(response.status(),200);
        System.out.println("User Details are"+response.text());

        // Response Headers  storing in Map and iteration using foreach
        Map<String, String> headersMap= response.headers();
        System.out.println("Response Header Details are");
        headersMap.forEach((k,v)-> System.out.println(k+" : "+v));
        Assert.assertEquals(headersMap.get("content-type"), "application/json; charset=utf-8");

        //jackson API for converting API response to JSON Object and vice versa
        ObjectMapper mapper=new ObjectMapper();
        JsonNode jsonResponse=mapper.readTree(response.body());
        System.out.println("API Response Details are"+jsonResponse.toPrettyString());

        // dispose method will dispose the API Response body. if we try to do any operation on API Response body it will throw exception
        // but status code of the api response we will be able to get after dispose the response body
        response.dispose();
    }

    @Test(priority = 1)
    public void getSpecificUserDetailsTest() throws IOException {

        APIResponse response=requestContext.get("https://gorest.co.in/public/v2/users",
                RequestOptions.create()
                        .setQueryParam("gender", "male")
                        .setQueryParam("status", "active"));

        Assert.assertEquals(response.status(),200);
        System.out.println("User Details are"+response.text());
        Map<String, String> headersMap= response.headers();
        System.out.println("Response Header Details are"+headersMap);
        Assert.assertEquals(headersMap.get("content-type"), "application/json; charset=utf-8");

        //jackson API for converting API response to JSON Object and vice versa
        ObjectMapper mapper=new ObjectMapper();
        JsonNode jsonResponse=mapper.readTree(response.body());
        System.out.println("API Response Details of a user are"+jsonResponse.toPrettyString());
    }

    @AfterTest
    public void tearDown(){
        playwright.close();
    }
}
