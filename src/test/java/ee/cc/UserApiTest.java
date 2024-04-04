package ee.cc;

import com.google.gson.Gson;
import ee.cc.dto.UserDTO;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.entity.ContentType;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class UserApiTest extends BaseTest{

    @DataProvider(name = "users")
    Object[][] usersDataProvider() {
        return new Object[][]{
                {8888, "Arnold", "Schwarzenegger"}
        };
    }

    private UserDTO generateUserDTO(long id, String firstName, String lastName) {
        String password = RandomStringUtils.randomAlphanumeric(10);
        String phone = RandomStringUtils.randomNumeric(8);
        String userJson = "{\"id\": " + id +
                ",\"username\": \"" + (firstName + "." + lastName).toLowerCase() +
                "\",\"firstName\": \"" + firstName +
                "\",\"lastName\": \"" + lastName +
                "\",\"email\": \"" + (firstName + "." + lastName + "@gmail.com").toLowerCase() +
                "\",\"password\": \"" + password +
                "\",\"phone\": \"" + phone +
                "\",\"userStatus\": 0}";
        return new Gson().fromJson(userJson, UserDTO.class);
    }

    @Test(dataProvider = "users")
    void addUserTest(long id, String firstName, String lastName) {
        UserDTO userDTO = generateUserDTO(id, firstName, lastName);
        JsonPath jsonPath = RestAssured.given()
                .when()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .with()
                .body(userDTO)
                .post("/user")
                .then()
                .assertThat()
                .statusCode(200)
                .log()
                .body()
                .assertThat()
                .extract().body().jsonPath();
        assertEquals(jsonPath.get("message"), "" + id);
    }

    @Test(dependsOnMethods = "addUserTest")
    public void requestUserByUsernameTest() {
        String firstName = (String) usersDataProvider()[0][1];
        String lastName = (String) usersDataProvider()[0][2];
        JsonPath jsonPath = RestAssured.given()
                .when()
                .get("/user/" + (firstName + "." + lastName).toLowerCase())
                .then()
                .assertThat()
                .statusCode(200)
                .assertThat()
                .extract().body().jsonPath();

        assertEquals(jsonPath.get("firstName"), firstName);
        assertEquals(jsonPath.get("lastName"), lastName);
        assertEquals(jsonPath.get("userStatus"), Integer.valueOf(0));

        //negative test
        jsonPath = RestAssured.given()
                .when()
                .get("/user/invalid.username")
                .then()
                .assertThat()
                .statusCode(404)
                .assertThat()
                .extract().body().jsonPath();
        assertEquals(jsonPath.get("code"), Integer.valueOf(1));
        assertEquals(jsonPath.get("type"), "error");
        assertEquals(jsonPath.get("message"), "User not found");
    }

    @Test
    void addUsersFromListTest() {
        List<UserDTO> usersList = List.of(
                generateUserDTO(10001, "Bob", "Hunt"),
                generateUserDTO(10002, "Tom", "Black"),
                generateUserDTO(10003, "Mat", "Cook")
        );
        RestAssured.given()
                .when()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .with()
                .body(usersList)
                .post("/user/createWithList")
                .then()
                .assertThat()
                .statusCode(200)
                .log()
                .body()
                .assertThat()
                .extract().body().jsonPath();
        //check if users were added
        usersList.forEach(dto -> {
            JsonPath jsonPath = RestAssured.given()
                    .when()
                    .get("/user/" + dto.getUsername())
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .assertThat()
                    .extract().body().jsonPath();
            assertEquals(jsonPath.get("firstName"), dto.getFirstName());
            assertEquals(jsonPath.get("lastName"), dto.getLastName());
        });
    }

    @Test
    void addUsersFromArrayTest() {
        UserDTO[] usersArray = new UserDTO[]{
                generateUserDTO(10001, "Bobby", "Hunt"),
                generateUserDTO(10002, "Tommy", "Black"),
                generateUserDTO(10003, "Matty", "Cook")
        };
        RestAssured.given()
                .when()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .with()
                .body(usersArray)
                .post("/user/createWithArray")
                .then()
                .assertThat()
                .statusCode(200)
                .log()
                .body()
                .assertThat()
                .extract().body().jsonPath();
        //check if users were added
        List.of(usersArray).forEach(dto -> {
            JsonPath jsonPath = RestAssured.given()
                    .when()
                    .get("/user/" + dto.getUsername())
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .assertThat()
                    .extract().body().jsonPath();
            assertEquals(jsonPath.get("firstName"), dto.getFirstName());
            assertEquals(jsonPath.get("lastName"), dto.getLastName());
        });
    }

    @Test(priority = 100)
    void deleteUserTest() {
        String firstName = (String) usersDataProvider()[0][1];
        String lastName = (String) usersDataProvider()[0][2];
        RestAssured.given()
                .when()
                .delete("/user/" + (firstName + "." + lastName).toLowerCase())
                .then()
                .assertThat()
                .statusCode(200)
                .assertThat()
                .extract().body().jsonPath();

        JsonPath jsonPath = RestAssured.given()
                .when()
                .get("/user/" + (firstName + "." + lastName).toLowerCase())
                .then()
                .assertThat()
                .statusCode(404)
                .assertThat()
                .extract().body().jsonPath();
        assertEquals(jsonPath.get("code"), Integer.valueOf(1));
        assertEquals(jsonPath.get("type"), "error");
        assertEquals(jsonPath.get("message"), "User not found");

    }

    @Test(dependsOnMethods = {"addUserTest", "requestUserByUsernameTest"})
    void updateUserTest() {
        String firstName = (String) usersDataProvider()[0][1];
        String lastName = (String) usersDataProvider()[0][2];
        UserDTO userDTO = RestAssured.given()
                .when()
                .get("/user/" + (firstName + "." + lastName).toLowerCase())
                .then()
                .assertThat()
                .statusCode(200)
                .extract().as(UserDTO.class);
        userDTO.setPassword("tempPassword");
        userDTO.setUserStatus(999);
        userDTO.setEmail(userDTO.getEmail().replace("gmail.com", "inbox.lv"));
        RestAssured.given()
                .when()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .with()
                .body(userDTO)
                .put("/user/" + userDTO.getUsername())
                .then()
                .assertThat()
                .statusCode(200);
        JsonPath jsonPath = RestAssured.given()
                .when()
                .get("/user/" + (firstName + "." + lastName).toLowerCase())
                .then()
                .assertThat()
                .statusCode(200)
                .extract().body().jsonPath();
        assertEquals(jsonPath.get("userStatus"), Integer.valueOf(999));
        assertEquals(jsonPath.get("password"), "tempPassword");
        assertEquals(jsonPath.get("email"), userDTO.getUsername() + "@inbox.lv");

    }

    @Test(dependsOnMethods = "addUserTest")
    void loginTest() {
        String firstName = (String) usersDataProvider()[0][1];
        String lastName = (String) usersDataProvider()[0][2];
        UserDTO userDTO = RestAssured.given()
                .when()
                .get("/user/" + (firstName + "." + lastName).toLowerCase())
                .then()
                .assertThat()
                .statusCode(200)
                .extract().as(UserDTO.class);
        JsonPath jsonPath = RestAssured.given()
                .param("username", userDTO.getUsername())
                .param("password", userDTO.getPassword())
                .when()
                .get("/user/login")
                .then()
                .assertThat()
                .statusCode(200)
                .extract().body().jsonPath();
        assertEquals(jsonPath.get("code"), Integer.valueOf(200));
        assertEquals(jsonPath.get("type"), "unknown");
        assertTrue(jsonPath.getString("message").startsWith("logged in user session:"));
    }

    @Test(dependsOnMethods = "loginTest")
    void logoutTest() {
        RestAssured.given()
                .when()
                .get("/user/logout")
                .then()
                .assertThat()
                .statusCode(200)
                .extract().body().jsonPath();
    }
}
