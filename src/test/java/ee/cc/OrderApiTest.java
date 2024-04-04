package ee.cc;

import ee.cc.dto.OrderDTO;
import ee.cc.dto.PetStatus;
import ee.cc.util.CCMatcherAssert;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

public class OrderApiTest extends BaseTest {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(Calendar.getInstance().getTime());
    private final long id = System.currentTimeMillis();

    @Test
    void getStoreInventoryTest() {
        var inventory = RestAssured.get("/store/inventory")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().body().as(Map.class);
        CCMatcherAssert.assertThat(LOGGER, "Response should contain number of available pets: ",
                inventory.containsKey(PetStatus.AVAILABLE.toString()), Matchers.is(true));
        CCMatcherAssert.assertThat(LOGGER, "Check number of available pets: ",
                Double.parseDouble(inventory.get(PetStatus.AVAILABLE.toString()).toString()), Matchers.is(Matchers.greaterThan(0.0)));
    }

    @Test
    void addStoreOrderTest() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(id);
        orderDTO.setPetId(999L);
        orderDTO.setQuantity(1);
        orderDTO.setShipDate(timeStamp);
        orderDTO.setStatus(OrderDTO.OrderStatus.PLACED);
        orderDTO.setComplete(false);
        JsonPath jsonPath = RestAssured.given()
                .when()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .with()
                .body(orderDTO)
                .post("/store/order")
                .then()
                .assertThat()
                .statusCode(200)
                .body("id", equalTo(id))
                .extract().jsonPath();
        CCMatcherAssert.assertThat(LOGGER, "Log ID. ", jsonPath.get("id"), equalTo(id));
    }

    //todo: mess-up in swagger
    @Test(dependsOnMethods = "addStoreOrderTest")
    void getStoreOrderByIdTest() {
        OrderDTO orderDTO = RestAssured
                .get("/store/order/" + id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract().as(OrderDTO.class);
        CCMatcherAssert.assertThat(LOGGER, "Check order fields. ID: ", orderDTO.getId(), equalTo(id));
        CCMatcherAssert.assertThat(LOGGER, "Check order fields. PetId: ", orderDTO.getPetId(), equalTo(999L));
        CCMatcherAssert.assertThat(LOGGER, "Check order fields. Quantity: ", orderDTO.getQuantity(), equalTo(1));
        CCMatcherAssert.assertThat(LOGGER, "Check order fields. Status: ", orderDTO.getStatus(), equalTo(OrderDTO.OrderStatus.PLACED));
        CCMatcherAssert.assertThat(LOGGER, "Check order fields. Complete: ", orderDTO.isComplete(), equalTo(false));
    }

    @Test(dependsOnMethods = {"addStoreOrderTest", "getStoreOrderByIdTest"})
    void deleteStoreOrderByIdTest() {
        RestAssured
                .delete("/store/order/" + id)
                .then()
                .assertThat()
                .statusCode(200);
        //negative test case
        RestAssured
                .delete("/store/order/" + id)
                .then()
                .assertThat()
                .statusCode(404);
    }
}
