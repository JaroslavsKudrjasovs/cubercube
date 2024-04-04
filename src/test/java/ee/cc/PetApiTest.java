package ee.cc;


import com.google.gson.Gson;
import ee.cc.dto.*;
import ee.cc.util.CCMatcherAssert;
import ee.cc.util.Requester;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.util.List;

public class PetApiTest {
    Requester requester = new Requester();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final long ID = System.currentTimeMillis();
    private static final String NAME = "Max";

    @DataProvider(name = "petStatusProvider")
    Object[][] objects() {
        return new Object[][]{
                {PetStatus.AVAILABLE.toString()},
                {PetStatus.PENDING.toString()},
                {PetStatus.SOLD.toString()}
        };
    }

    @Test(dataProvider = "petStatusProvider")
//, dependsOnMethods = "addPetPositiveTest")
    void requestPetsByStatusTest(String petStatus) {
        List<PetDTO> pets = List.of(new Gson().fromJson(requester.requestPetsByStatus(petStatus).body(), PetDTO[].class));
        CCMatcherAssert.assertThat(LOGGER, "Check the size of '" + petStatus + "' pets list: ", pets.size(), Matchers.is(Matchers.greaterThan(0)));
        pets.forEach(x -> {
            if (x.getName() != null && x.getName().contains("jjj"))
                LOGGER.info("Found pet: " + x.getId() + ": " + x.getName());
        });
    }

    //TODO: unable to get 400 error. Need consultation.
    @Test(dependsOnMethods = "addPetPositiveTest")
    void requestPetsByStatusNegativeTest() {
        String petStatus = "unknown";
        HttpResponse<String> httpResponse = requester.requestPetsByStatus(petStatus);
        CCMatcherAssert.assertThat(LOGGER, "Check HTTP response status: ", httpResponse.statusCode(), Matchers.equalTo(200));
        List<PetDTO> pets = List.of(new Gson().fromJson(httpResponse.body(), PetDTO[].class));
        CCMatcherAssert.assertThat(LOGGER, "Check that list of pets with '" + petStatus + "' status is empty: ", pets.size(), Matchers.equalTo(0));
    }

    @Test(dependsOnMethods = "addPetAsObjectPositiveTest")
    void requestPetByIdTest() {
        ApiResponse apiResponse;
        String responseBody = requester.requestPet("" + ID).body();
        apiResponse = new Gson().fromJson(responseBody, PetDTO.class);
        if (((PetDTO) apiResponse).getName() == null)
            apiResponse = new Gson().fromJson(responseBody, ApiResponse.class);

        CCMatcherAssert.assertThat(LOGGER, "Validate response body: ", apiResponse, Matchers.isA(PetDTO.class));
        CCMatcherAssert.assertThat(LOGGER, "Check pet ID: ", ((PetDTO) apiResponse).getId(), Matchers.equalTo(ID));
        CCMatcherAssert.assertThat(LOGGER, "Check pet name: ", ((PetDTO) apiResponse).getName(), Matchers.equalTo(NAME));
        CCMatcherAssert.assertThat(LOGGER, "Check pet status: ", ((PetDTO) apiResponse).getPetStatus(), Matchers.equalTo(PetStatus.PENDING));
        CCMatcherAssert.assertThat(LOGGER, "Check pet url list: ", ((PetDTO) apiResponse).getPhotoUrls(), Matchers.containsInAnyOrder("url-1", "url-2", "url-3"));
        CCMatcherAssert.assertThat(LOGGER, "Check pet url list: ", ((PetDTO) apiResponse).getTags(), Matchers.containsInAnyOrder(new Tag(2, "tag-2"), new Tag(1, "tag-1")));
    }

    @Test(dependsOnMethods = {"requestPetByIdTest", "updatePetTest"})
    void deletePetPositiveTest() {
        ApiResponse apiResponse = new Gson().fromJson(requester.deletePet("" + ID).body(), ApiResponse.class);
        CCMatcherAssert.assertThat(LOGGER, "Check response code: ", apiResponse.getCode(), Matchers.equalTo(200));
        CCMatcherAssert.assertThat(LOGGER, "Check response type: ", apiResponse.getType(), Matchers.equalTo("unknown"));
        CCMatcherAssert.assertThat(LOGGER, "Check response message: ", apiResponse.getMessage(), Matchers.equalTo("" + ID));

    }

    @Test(dependsOnMethods = {"updatePetTest", "deletePetPositiveTest"})
    void requestPetByIdNegativeTest() {
        ApiResponse apiResponse;
        HttpResponse<String> httpResponse = requester.requestPet("" + ID);

        CCMatcherAssert.assertThat(LOGGER, "Check HTTP response status code: ", httpResponse.statusCode(), Matchers.equalTo(404));

        apiResponse = new Gson().fromJson(httpResponse.body(), PetDTO.class);
        if (((PetDTO) apiResponse).getName() == null)
            apiResponse = new Gson().fromJson(httpResponse.body(), ApiResponse.class);

        CCMatcherAssert.assertThat(LOGGER, "Check response code: ", apiResponse.getCode(), Matchers.equalTo(1));
        CCMatcherAssert.assertThat(LOGGER, "Check response type: ", apiResponse.getType(), Matchers.equalTo("error"));
        CCMatcherAssert.assertThat(LOGGER, "Check response message: ", apiResponse.getMessage(), Matchers.equalTo("Pet not found"));
    }

    @DataProvider(name = "petsDP")
    Object[][] pets() {
        return new Object[][]{
                {"{\"id\":8888, \"category\":{\"id\":1, \"name\":\"category1\"}, \"name\":\"jaroMaxy\", \"photoUrls\":[\"url-3\",\"url-4\",\"url-5\"], \"tags\":[{\"id\":12, \"name\":\"tag-12\"}, {\"id\":22, \"name\":\"tag-22\"}], \"status\":\"available\"}"
                        , Matchers.equalTo(8888L), Matchers.equalTo("jaroMaxy"), Matchers.equalTo(PetStatus.AVAILABLE)},
                // ID = Long.MAX_VALUE = 9223372036854775807
                {"{\"id\":" + Long.MAX_VALUE + ", \"category\":{\"id\":1, \"name\":\"category1\"}, \"name\":\"jaroWolf\", \"photoUrls\":[\"url-1\",\"url-2\",\"url-3\"], \"tags\":[{\"id\":11, \"name\":\"tag-11\"}, {\"id\":21, \"name\":\"tag-21\"}], \"status\":\"pending\"}"
                        , Matchers.equalTo(Long.MAX_VALUE), Matchers.equalTo("jaroWolf"), Matchers.equalTo(PetStatus.PENDING)},
                // if ID is <=0, it should be assigned automatically
                {"{\"id\":0, \"category\":{\"id\":1, \"name\":\"category1\"}, \"name\":\"jaroHary\", \"photoUrls\":[\"url-6\",\"url-7\",\"url-8\"], \"tags\":[{\"id\":13, \"name\":\"tag-13\"}, {\"id\":23, \"name\":\"tag-23\"}], \"status\":\"available\"}"
                        , Matchers.is(Matchers.greaterThan(0L)), Matchers.not(Matchers.emptyOrNullString()), Matchers.not(Matchers.emptyOrNullString())},
                {"{\"id\":-1, \"category\":{\"id\":1, \"name\":\"category1\"}, \"name\":\"jaroHary\", \"photoUrls\":[\"url-6\",\"url-7\",\"url-8\"], \"tags\":[{\"id\":13, \"name\":\"tag-13\"}, {\"id\":23, \"name\":\"tag-23\"}], \"status\":\"available\"}"
                        , Matchers.is(Matchers.greaterThan(0L)), Matchers.not(Matchers.emptyOrNullString()), Matchers.not(Matchers.emptyOrNullString())},
                // if ID is missing, it should be assigned automatically
                {"{\"idd\":1, \"category\":{\"id\":1, \"name\":\"category1\"}, \"name\":\"jaroHary\", \"photoUrls\":[\"url-6\",\"url-7\",\"url-8\"], \"tags\":[{\"id\":13, \"name\":\"tag-13\"}, {\"id\":23, \"name\":\"tag-23\"}], \"status\":\"available\"}"
                        , Matchers.is(Matchers.greaterThan(0L)), Matchers.not(Matchers.emptyOrNullString()), Matchers.not(Matchers.emptyOrNullString())},
                // missing 'name' and 'status', properties are null after casting
                {"{\"id\":8888, \"category\":{\"id\":1, \"name\":\"category1\"}, \"name2\":\"jaroHary\", \"photoUrls\":[\"url-6\",\"url-7\",\"url-8\"], \"tags\":[{\"id\":13, \"name\":\"tag-13\"}, {\"id\":23, \"name\":\"tag-23\"}], \"status2\":\"available\"}"
                        , Matchers.equalTo(8888L), Matchers.is(Matchers.nullValue()), Matchers.is(Matchers.nullValue())}
        };
    }

    @Test(dataProvider = "petsDP")
    void addPetPositiveTest(String petJson, Matcher<Long> idMatcher, Matcher<String> nameMatcher, Matcher<PetStatus> statusMatcher) {
        LOGGER.info("adding " + petJson);
        PetDTO apiResponse = new Gson().fromJson(requester.addPet(petJson).body(), PetDTO.class);
        CCMatcherAssert.assertThat(LOGGER, "Validate response body: ", apiResponse, Matchers.isA(PetDTO.class));
        CCMatcherAssert.assertThat(LOGGER, "Check pet ID: ", apiResponse.getId(), idMatcher);
        CCMatcherAssert.assertThat(LOGGER, "Check pet name: ", apiResponse.getName(), nameMatcher);
        CCMatcherAssert.assertThat(LOGGER, "Check pet status: ", apiResponse.getPetStatus(), statusMatcher);
    }

    @Test
    void addPetAsObjectPositiveTest() {
        PetDTO petDTO = new PetDTO();
        petDTO.setId(ID);
        petDTO.setName(NAME);
        petDTO.setCategory(new Category(1, "category1"));
        petDTO.setPetStatus(PetStatus.PENDING);
        petDTO.setPhotoUrls(List.of("url-1", "url-2", "url-3"));
        petDTO.setTags(List.of(new Tag(1, "tag-1"), new Tag(2, "tag-2")));
        PetDTO apiResponse = new Gson().fromJson(requester.addPet(petDTO.toString()).body(), PetDTO.class);
        CCMatcherAssert.assertThat(LOGGER, "Validate response body: ", apiResponse, Matchers.isA(PetDTO.class));
        CCMatcherAssert.assertThat(LOGGER, "Check pet ID: ", apiResponse.getId(), Matchers.equalTo(ID));
        CCMatcherAssert.assertThat(LOGGER, "Check pet name: ", apiResponse.getName(), Matchers.equalTo(NAME));
        CCMatcherAssert.assertThat(LOGGER, "Check pet status: ", apiResponse.getPetStatus(), Matchers.equalTo(PetStatus.PENDING));
        CCMatcherAssert.assertThat(LOGGER, "Check pet url list: ", apiResponse.getPhotoUrls(), Matchers.containsInAnyOrder("url-1", "url-2", "url-3"));
        CCMatcherAssert.assertThat(LOGGER, "Check pet url list: ", apiResponse.getTags(), Matchers.containsInAnyOrder(new Tag(2, "tag-2"), new Tag(1, "tag-1")));
    }

    @DataProvider(name = "petsDP_negative")
    Object[][] illegalPets() {
        return new Object[][]{
                //ID greater than max long
                {"{\"id\":" + (new BigDecimal(Long.MAX_VALUE).add(new BigDecimal(1))) + ", \"category\":{\"id\":1, \"name\":\"category1\"}, \"name\":\"jaroWolf\", \"photoUrls\":[\"url-1\",\"url-2\",\"url-3\"], \"tags\":[{\"id\":11, \"name\":\"tag-11\"}, {\"id\":21, \"name\":\"tag-21\"}], \"status\":\"available\"}"
                        , 500, "something bad happened"},
                //ID is string
                {"{\"id\":\"q\",\"category\":{\"id\":1, \"name\":\"category1\"}, \"name\":\"jaroMaxy\", \"photoUrls\":[\"url-3\",\"url-4\",\"url-5\"], \"tags\":[{\"id\":12, \"name\":\"tag-12\"}, {\"id\":22, \"name\":\"tag-22\"}], \"status\":\"available\"}"
                        , 500, "something bad happened"},
                //malformed JSON
                {"{\"id\":, \"category\":{\"id\":1, \"name\":\"category1\"}, \"name\":\"jaroHary\", \"photoUrls\":[\"url-6\",\"url-7\",\"url-8\"], \"tags\":[{\"id\":13, \"name\":\"tag-13\"}, {\"id\":23, \"name\":\"tag-23\"}], \"status\":\"available\"}"
                        , 400, "bad input"}
        };
    }

    //TODO: not able to get 405 error, need consultation
    @Test(dataProvider = "petsDP_negative")
    void addPetNegativeTest(String petJson, int code, String message) {
        LOGGER.info("adding " + petJson);
        ApiResponse apiResponse = new Gson().fromJson(requester.addPet(petJson).body(), ApiResponse.class);
        CCMatcherAssert.assertThat(LOGGER, "Check response status: ", apiResponse.getCode(), Matchers.equalTo(code));
        CCMatcherAssert.assertThat(LOGGER, "Check response message: ", apiResponse.getMessage(), Matchers.equalTo(message));
    }

    @Test(dependsOnMethods = {"requestPetByIdTest", "addPetAsObjectPositiveTest"})
    void updatePetTest() {
        PetDTO petDTO = new PetDTO();
        petDTO.setId(ID);
        petDTO.setName(NAME + "_added");
        petDTO.setCategory(new Category(2, "category2"));
        petDTO.setPetStatus(PetStatus.SOLD);
        petDTO.setPhotoUrls(List.of("url-1-new", "url-2-new", "url-3-new"));
        petDTO.setTags(List.of(new Tag(100, "tag-100"), new Tag(200, "tag-200")));
        PetDTO apiResponse = new Gson().fromJson(requester.updatePet(petDTO.toString()).body(), PetDTO.class);
        CCMatcherAssert.assertThat(LOGGER, "Validate response body: ", apiResponse, Matchers.isA(PetDTO.class));
        CCMatcherAssert.assertThat(LOGGER, "Check pet ID: ", apiResponse.getId(), Matchers.equalTo(ID));
        CCMatcherAssert.assertThat(LOGGER, "Check pet name: ", apiResponse.getName(), Matchers.equalTo(NAME + "_added"));
        CCMatcherAssert.assertThat(LOGGER, "Check pet status: ", apiResponse.getPetStatus(), Matchers.equalTo(PetStatus.SOLD));
        CCMatcherAssert.assertThat(LOGGER, "Check pet url list: ", apiResponse.getPhotoUrls(), Matchers.containsInAnyOrder("url-1-new", "url-2-new", "url-3-new"));
        CCMatcherAssert.assertThat(LOGGER, "Check pet url list: ", apiResponse.getTags(), Matchers.containsInAnyOrder(new Tag(200, "tag-200"), new Tag(100, "tag-100")));
        apiResponse = new Gson().fromJson(requester.requestPet("" + ID).body(), PetDTO.class);
        CCMatcherAssert.assertThat(LOGGER, "Validate response body: ", apiResponse, Matchers.isA(PetDTO.class));
        CCMatcherAssert.assertThat(LOGGER, "Check pet ID: ", apiResponse.getId(), Matchers.equalTo(ID));
        CCMatcherAssert.assertThat(LOGGER, "Check pet name: ", apiResponse.getName(), Matchers.equalTo(NAME + "_added"));
        CCMatcherAssert.assertThat(LOGGER, "Check pet status: ", apiResponse.getPetStatus(), Matchers.equalTo(PetStatus.SOLD));
        CCMatcherAssert.assertThat(LOGGER, "Check pet url list: ", apiResponse.getPhotoUrls(), Matchers.containsInAnyOrder("url-1-new", "url-2-new", "url-3-new"));
        CCMatcherAssert.assertThat(LOGGER, "Check pet url list: ", apiResponse.getTags(), Matchers.containsInAnyOrder(new Tag(200, "tag-200"), new Tag(100, "tag-100")));

    }

    @Test
        //todo: needs to be fixed
    void uploadImageTest() {
        String metaData = "scorpion image meta data";
        String fileName = "scorpion.jpg";
        String resultOfImageUpload = requester.uploadImage("8888", metaData, "./src/test/resources/" + fileName);
        CCMatcherAssert.assertThat(LOGGER, "Check the result of image upload: ", resultOfImageUpload, CoreMatchers.containsString("\"code\":200"));
        CCMatcherAssert.assertThat(LOGGER, "Check the existence of metadata in the result: ", resultOfImageUpload, CoreMatchers.containsString(metaData));
        CCMatcherAssert.assertThat(LOGGER, "Check the existence of fileName in the result: ", resultOfImageUpload, CoreMatchers.containsString(fileName));
    }

    @Test
    void updatePetNameStatusTest() {
        String petJson = (String) pets()[0][0];
        PetDTO petDTO = new Gson().fromJson(petJson, PetDTO.class);
        long id = petDTO.getId();
        String petName = petDTO.getName();
        PetDTO apiResponse = new Gson().fromJson(requester.addPet(petDTO.toString()).body(), PetDTO.class);
        LOGGER.info("Before update:");
        CCMatcherAssert.assertThat(LOGGER, "Check pet name: ", apiResponse.getName(), Matchers.equalTo(petName));
        CCMatcherAssert.assertThat(LOGGER, "Check pet status: ", apiResponse.getPetStatus(), Matchers.equalTo(PetStatus.AVAILABLE));
        CCMatcherAssert.assertThat(LOGGER, "Check pet url list: ", apiResponse.getPhotoUrls(), Matchers.containsInAnyOrder("url-3", "url-4", "url-5"));
        CCMatcherAssert.assertThat(LOGGER, "Check pet url list: ", apiResponse.getTags(), Matchers.containsInAnyOrder(new Tag(22, "tag-22"), new Tag(12, "tag-12")));

        String content = MessageFormat.format("name={0}&status={1}", petName + "_updated", PetStatus.SOLD);
        HttpResponse<String> response =
                requester.updatePetNameStatus("" + id, content);
        CCMatcherAssert.assertThat(LOGGER, "Check update statusCode: ", response.statusCode(), Matchers.equalTo(200));
        response = requester.requestPet("" + id);
        apiResponse = new Gson().fromJson(response.body(), PetDTO.class);
        LOGGER.info("After update:");
        CCMatcherAssert.assertThat(LOGGER, "Check pet name: ", apiResponse.getName(), Matchers.equalTo(petName + "_updated"));
        CCMatcherAssert.assertThat(LOGGER, "Check pet status: ", apiResponse.getPetStatus(), Matchers.equalTo(PetStatus.SOLD));
        // other properties should remain untouched
        CCMatcherAssert.assertThat(LOGGER, "Check pet url list: ", apiResponse.getPhotoUrls(), Matchers.containsInAnyOrder("url-3", "url-4", "url-5"));
        CCMatcherAssert.assertThat(LOGGER, "Check pet url list: ", apiResponse.getTags(), Matchers.containsInAnyOrder(new Tag(22, "tag-22"), new Tag(12, "tag-12")));
    }
}

