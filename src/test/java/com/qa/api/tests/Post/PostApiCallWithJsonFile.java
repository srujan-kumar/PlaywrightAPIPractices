package com.qa.api.tests.Post;

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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class PostApiCallWithJsonFile {
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
    public void postAPICallTest() throws IOException {

        // API Request Body from JSON file is stored in byte array as we dont have setData() method which accepts Jason file
        byte[] fileBytes=null;
        File file=new File("./src/test/data/user.json");
        fileBytes=Files.readAllBytes(file.toPath());



        APIResponse response=requestContext.post("https://gorest.co.in/public/v2/users",
                RequestOptions.create()
                        .setHeader("Content-Type","application/json")
                        // *** bearer token of your gmail login of this website https://gorest.co.in
                        .setHeader("Authorization", "Bearer 5f3a2ecd13c6040d934e2b29319d768923a8e15400d32d2fdaefe14a5dd879ba")
                        .setData(fileBytes));

        System.out.println("Response status and status code"+response.status()+" ::"+response.statusText());
        System.out.println("response Body is"+response.text());

        ObjectMapper objectMapper=new ObjectMapper();
        JsonNode postJsonResponse=objectMapper.readTree(response.body());
        System.out.println("response json is"+postJsonResponse.toPrettyString());

        // **** fetch some attribute value from json Response
        String userid=postJsonResponse.get("id").asText();
        System.out.println("response User ID val is"+userid);

        // *** check the new user got created by using the get the call and validate the data
        APIResponse apiGetResponse= requestContext.get("https://gorest.co.in/public/v2/users/"+userid,
                RequestOptions.create()
                        .setHeader("Authorization", "Bearer 5f3a2ecd13c6040d934e2b29319d768923a8e15400d32d2fdaefe14a5dd879ba"));
        Assert.assertEquals(apiGetResponse.status(), 200);
        Assert.assertEquals(apiGetResponse.statusText(), "OK");
        Assert.assertTrue(apiGetResponse.text().contains(userid));
    }

    @AfterTest
    public void tearDown(){
        playwright.close();
    }


}



