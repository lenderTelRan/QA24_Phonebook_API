package restassured;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import dto.ContactDTO;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Random;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class DeleteContactByIDRestAssured {
    String token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwic3ViIjoibWFyYUBnbWFpbC5jb20iLCJpc3MiOiJSZWd1bGFpdCIsImV4cCI6MTcxNDk5NDA0OCwiaWF0IjoxNzE0Mzk0MDQ4fQ.2OSjvVJLit8Xcfodm4CtME8A38JGm0EdlpIPhpkzneQ";
    String id;
    @BeforeMethod
    public void preCondition() {
        RestAssured.baseURI = "https://contactapp-telran-backend.herokuapp.com";
        RestAssured.basePath = "v1";

        int i = new Random().nextInt(100) + 100;

        ContactDTO contact = ContactDTO.builder()
                .name("Tony" + i)
                .lastName("Stark")
                .email("stark" + i + "@gmail.com")
                .phone("04458877" + i)
                .address("Miami")
                .build();

        String message = given()
                .body(contact)
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .when()
                .post("contacts")
                .then()
                .assertThat().statusCode(200)
                .extract()
                .path("messege");       // method path --> for response header

        String[] contactId = message.split(": ");
        id = contactId[1];

    }

    @Test
    public void deleteContactById() {
        given()
                .header("Authorization", token)
                .when()
                .delete("contacts/" + id)
                .then()
                .assertThat().statusCode(200)
                .assertThat().body("message", equalTo("Contact was deleted!"));
    }

    @Test
    public void deleteContactByIdWrongToken() {
        given()
                .header("Authorization", "fw5ef42")
                .when()
                .delete("contacts/" + id)
                .then()
                .assertThat().statusCode(401);
    }
}
