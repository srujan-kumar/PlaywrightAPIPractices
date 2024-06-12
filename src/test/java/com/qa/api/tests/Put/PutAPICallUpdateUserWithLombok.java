package com.qa.api.tests.Put;

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

// PUT is idempotent in nature, post is not
// i.e. when a PUT req sent it is used to update existing User, if no user is there then only it creates new entry
// diff b/n Put vs Patch --> For Put call Though we are updating a portion of user we have to pass entire Object in Request
// for Patch call --> an exact portion of the required object data is fine to pass through Request
public class PutAPICallUpdateUserWithLombok {

    // Post --> Create a user
    // Put --> Update the User
    // Get --> Validate the User Details Updation
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
                .name("Naveen574")
                .email("Naveen368@gmail.com")
                .gender("male")
                .status("active").build();

        // Post Call --> User Created
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

        String userid= actUser.getId();
        // Updating user details and pass it in Put call
        user.setStatus("inactive");
        user.setGender("female");

        // Put Call --> Update User
        APIResponse putResponse=requestContext.put("https://gorest.co.in/public/v2/users/"+ userid,
                RequestOptions.create()
                        .setHeader("Content-Type","application/json")
                        // *** bearer token of your gmail login of this website https://gorest.co.in
                        .setHeader("Authorization", "Bearer 5f3a2ecd13c6040d934e2b29319d768923a8e15400d32d2fdaefe14a5dd879ba")
                        .setData(user));

        String responseText1= putResponse.text();
        System.out.println("response Body for Update user is"+responseText1);

        // Convert response text/json to POJO --> Deserialization


        UserClassCreationWithLombok actUser1=objectMapper.readValue(responseText, UserClassCreationWithLombok.class);
        System.out.println(actUser1.getEmail());

        //****  Validating actual user obj data fetched from APi response with API Request User Object
        Assert.assertEquals(actUser1.getName(), actUser.getName());
        Assert.assertEquals(actUser1.getEmail(), actUser.getEmail());
        Assert.assertEquals(actUser1.getGender(), actUser.getGender());
        Assert.assertEquals(actUser1.getStatus(), actUser.getStatus());

        // Get call
        APIResponse getCallresponse=requestContext.get("https://gorest.co.in/public/v2/users/"+userid,
                RequestOptions.create()
                                 .setHeader("Authorization", "Bearer 5f3a2ecd13c6040d934e2b29319d768923a8e15400d32d2fdaefe14a5dd879ba"));
        System.out.println("Get response after update is"+getCallresponse.text());
        Assert.assertTrue(getCallresponse.text().contains("female"));
        Assert.assertTrue(getCallresponse.text().contains("inactive"));





    }

    @AfterTest
    public void tearDown(){
        playwright.close();
    }

}
