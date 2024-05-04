package okhttp;

import com.google.gson.Gson;
import dto.ContactDTO;
import dto.ErrorDTO;
import dto.MessageDTO;
import okhttp3.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Random;

public class DeleteContactByIdOkhttp {

    String token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwic3ViIjoidGVzdF90ZWxyYW5AZ21haWwuY29tIiwiaXNzIjoiUmVndWxhaXQiLCJleHAiOjE3MTUxNzg0MzMsImlhdCI6MTcxNDU3ODQzM30.tkZ3zysUKTMe4U9Yge09L9nCdk_kbiJ1jrFmwLLnrK8";
    OkHttpClient client = new OkHttpClient();
    Gson gson = new Gson();
    public static final MediaType JSON = MediaType.get("application/json;charset=utf-8");
    private String id;

    @BeforeMethod
    public void preCondition() throws IOException {
        int i = new Random().nextInt(100) + 100;

        ContactDTO contact = ContactDTO.builder()
                .name("Valentin")
                .lastName("Simson")
                .address("Tel Aviv")
                .email("valentin" + i + "@gmail.com")
                .phone("05558951" + i)
                .description("coworker")
                .build();

        RequestBody body = RequestBody.create(gson.toJson(contact), JSON);

        Request request = new Request.Builder()
                .url("https://contactapp-telran-backend.herokuapp.com/v1/contacts")
                .post(body)
                .addHeader("Authorization", token)
                .build();

        Response response = client.newCall(request).execute();
        Assert.assertEquals(response.code(), 200);

        MessageDTO messageDTO = gson.fromJson(response.body().string(), MessageDTO.class);
//        System.out.println(messageDTO.getMessage());
        String[] message = messageDTO.getMessage().split(": ");
        id = message[1];
    }

    @Test
    public void deleteContactByIdSuccess() throws IOException {
        Request request = new Request.Builder()
                .url("https://contactapp-telran-backend.herokuapp.com/v1/contacts/" + id)
                .delete()
                .addHeader("Authorization", token)
                .build();
        Response response = client.newCall(request).execute();

        Assert.assertEquals(response.code(), 200);

        MessageDTO dto = gson.fromJson(response.body().string(), MessageDTO.class);
        System.out.println(dto.getMessage());
    }

    @Test
    public void deleteContactByIdWrongToken() throws IOException {
        Request request = new Request.Builder()
                .url("https://contactapp-telran-backend.herokuapp.com/v1/contacts/" + id)
                .delete()
                .addHeader("Authorization", "dwu549qd4")
                .build();
        Response response = client.newCall(request).execute();

        Assert.assertEquals(response.code(), 401);

        ErrorDTO errorDTO = gson.fromJson(response.body().string(), ErrorDTO.class);
        Assert.assertEquals(errorDTO.getError(), "Unauthorized");
    }

    @Test
    public void deleteContactByIdNotFound() throws IOException {
        Request request = new Request.Builder()
                .url("https://contactapp-telran-backend.herokuapp.com/v1/contacts/" + "wrong-ID")
                .delete()
                .addHeader("Authorization", token)
                .build();
        Response response = client.newCall(request).execute();

        Assert.assertEquals(response.code(), 400);

        ErrorDTO errorDTO = gson.fromJson(response.body().string(), ErrorDTO.class);
        Assert.assertEquals(errorDTO.getError(), "Bad Request");
//        System.out.println(errorDTO.getMessage());
        Assert.assertEquals(errorDTO.getMessage(), "Contact with id: wrong-ID not found in your contacts!");
    }
}
