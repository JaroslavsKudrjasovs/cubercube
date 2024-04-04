package ee.cc;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeTest;

public abstract class BaseTest {
    @BeforeTest
    public void setUp() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
    }

}
