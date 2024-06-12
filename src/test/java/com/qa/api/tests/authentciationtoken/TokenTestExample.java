package com.qa.api.tests.authentciationtoken;

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


// ref from https://www.youtube.com/watch?v=id-gp_gBMkA
public class TokenTestExample {
    Playwright playwright;
    APIRequest request;
    APIRequestContext requestContext;

    private static String TOKEN_ID=null;

    @BeforeTest
    public void setup(){
        playwright=Playwright.create();
        request=playwright.request();
        requestContext=request.newContext();
    }

    @Test
    public void getTokenTest() throws IOException {

        // API Request Body in String
        String reqJsonBody="{\n" +
                "    \"username\" : \"admin\",\n" +
                "    \"password\" : \"password123\"\n" +
                "}";

        // get Token
        APIResponse apiPostTokenResponse=requestContext.post("https://restful-booker.herokuapp.com/auth",
                RequestOptions.create()
                        .setHeader("Content-Type","application/json")
                        .setData(reqJsonBody));

        System.out.println("Response status and status code"+apiPostTokenResponse.status()+" ::"+apiPostTokenResponse.statusText());
        System.out.println("response Body is"+apiPostTokenResponse.text());

        ObjectMapper objectMapper=new ObjectMapper();
        JsonNode postJsonResponse=objectMapper.readTree(apiPostTokenResponse.body());
        System.out.println("response json is"+postJsonResponse.toPrettyString());

        // **** fetch token attribute value from json Response
        TOKEN_ID=postJsonResponse.get("token").asText();
        System.out.println("response Token ID val is"+TOKEN_ID);
    }

    @Test(priority = 1)
    public void updateBookingTestWithAuthToken()
    {
        String updateBookingJsonBody="{\n" +
                "    \"firstname\" : \"James\",\n" +
                "    \"lastname\" : \"Brown\",\n" +
                "    \"totalprice\" : 111,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2024-01-01\",\n" +
                "        \"checkout\" : \"2024-01-01\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Dinner\"\n" +
                "}";

        APIResponse putResponse=requestContext.put("https://restful-booker.herokuapp.com/booking/1",
                RequestOptions.create()
                        .setHeader("Content-Type","application/json")
                        .setHeader("Cookie", "token="+TOKEN_ID)
                        .setData(updateBookingJsonBody));
        System.out.println("response status for update booking is"+putResponse.status());
        String responseText1= putResponse.text();
        System.out.println("response Body for Update user is"+responseText1);



    }
    @AfterTest
    public void tearDown(){
        playwright.close();
    }

}



