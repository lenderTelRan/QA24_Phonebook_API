package okhttp;

import com.google.gson.Gson;
import dto.ContactDTO;
import dto.ErrorDTO;
import dto.GetAllContactsDTO;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

public class GetAllContactsTestsOkhttp {
    String token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwic3ViIjoibWFyYUBnbWFpbC5jb20iLCJpc3MiOiJSZWd1bGFpdCIsImV4cCI6MTcxNDk5NDA0OCwiaWF0IjoxNzE0Mzk0MDQ4fQ.2OSjvVJLit8Xcfodm4CtME8A38JGm0EdlpIPhpkzneQ";
    Gson gson = new Gson();
    OkHttpClient client = new OkHttpClient();

    @Test
    public void getAllContactsSuccess() throws IOException {
        Request request = new Request.Builder()
                .url("https://contactapp-telran-backend.herokuapp.com/v1/contacts")
                .get()
                .addHeader("Authorization", token)
                .build();

        Response response = client.newCall(request).execute();

        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(response.code(), 200);

        GetAllContactsDTO contactsDTO = gson.fromJson(response.body().string(), GetAllContactsDTO.class);

        List<ContactDTO> contacts = contactsDTO.getContacts();
        for(ContactDTO contact : contacts) {
            System.out.println(contact.getId());
        }
    }

    @Test
    public void getAllContactsWrongToken() throws IOException {
        Request request = new Request.Builder()
                .url("https://contactapp-telran-backend.herokuapp.com/v1/contacts")
                .get()
                .addHeader("Authorization", "fa45fe5fEfewf")
                .build();

        Response response = client.newCall(request).execute();

        Assert.assertFalse(response.isSuccessful());
        Assert.assertEquals(response.code(), 401);

        ErrorDTO error = gson.fromJson(response.body().string(), ErrorDTO.class);
        Assert.assertEquals(error.getError(), "Unauthorized");
    }

}
