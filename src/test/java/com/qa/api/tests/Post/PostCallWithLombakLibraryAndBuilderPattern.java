package com.qa.api.tests.Post;

import com.api.data.UserClassCreationWithLombok;
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

// Lombak Library is used to avoid the boiler plate code (creating setters, getters, constructor) used in POJO class creation
public class PostCallWithLombakLibraryAndBuilderPattern {
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
    public void postAPICallWithPojoTest() throws IOException {

        // create Users obj : using builder pattern
        UserClassCreationWithLombok user= UserClassCreationWithLombok.builder()
                .name("Naveen459")
                .email("Naveen35@gmail.com")
                .gender("male")
                .status("active").build();


        APIResponse response=requestContext.post("https://gorest.co.in/public/v2/users",
                RequestOptions.create()
                        .setHeader("Content-Type","application/json")
                        // *** bearer token of your gmail login of this website https://gorest.co.in
                        .setHeader("Authorization", "Bearer 5f3a2ecd13c6040d934e2b29319d768923a8e15400d32d2fdaefe14a5dd879ba")
                        .setData(user));

        System.out.println("Response status and status code"+response.status()+" ::"+response.statusText());
        String responseText= response.text();
        System.out.println("response Body is"+response.text());

        // Convert response text/json to POJO --> Deserialization
        ObjectMapper objectMapper=new ObjectMapper();
        UserClassCreationWithLombok actUser=objectMapper.readValue(responseText, UserClassCreationWithLombok.class);
        System.out.println(actUser.getEmail());

        //****  Validating actual user obj data fetched from APi response with API Request User Object
        Assert.assertEquals(actUser.getName(), user.getName());
        Assert.assertEquals(actUser.getEmail(), user.getEmail());
        Assert.assertEquals(actUser.getGender(), user.getGender());
        Assert.assertEquals(actUser.getStatus(), user.getStatus());
        // *** Si nce user's ID created from server side and it is not passed as request body we can just verify user id is not empty or not
        Assert.assertNotNull(actUser.getId());

    }

    @AfterTest
    public void tearDown(){
        playwright.close();
    }

}

