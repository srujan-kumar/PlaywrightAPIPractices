package com.qa.api.tests.Delete;

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

public class DeleteAPICall {

    // Create User
    // delete the User
    // check user existence with Get call
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
                    .name("Naveen6259")
                    .email("Naveen0912@gmail.com")
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
            String userid= actUser.getId();

            //delete call

            APIResponse deleteResponse=requestContext.delete("https://gorest.co.in/public/v2/users/"+userid,
                    RequestOptions.create()
                            // *** bearer token of your gmail login of this website https://gorest.co.in
                            .setHeader("Authorization", "Bearer 5f3a2ecd13c6040d934e2b29319d768923a8e15400d32d2fdaefe14a5dd879ba"));
            System.out.println("Response status and status code after deletion "+deleteResponse.status()+" ::"+deleteResponse.statusText());
            Assert.assertEquals(deleteResponse.status(), 204);


            // Get call
            APIResponse getCallresponse=requestContext.get("https://gorest.co.in/public/v2/users/"+userid,
                    RequestOptions.create()
                            .setHeader("Authorization", "Bearer 5f3a2ecd13c6040d934e2b29319d768923a8e15400d32d2fdaefe14a5dd879ba"));
            System.out.println("Response status and status code after deletion "+getCallresponse.status()+" ::"+getCallresponse.statusText());
            Assert.assertEquals(getCallresponse.status(), 404);
            Assert.assertTrue(getCallresponse.text().contains("Resource not found"));


        }

        @AfterTest
        public void tearDown(){
            playwright.close();
        }





    }
