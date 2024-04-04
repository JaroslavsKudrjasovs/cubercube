package ee.cc;

import ee.cc.dto.Category;
import ee.cc.dto.PetDTO;
import ee.cc.dto.PetStatus;
import ee.cc.dto.Tag;

import java.util.List;

public class TempTest {
    public static void main(String[] args) {
        PetDTO petDTO = new PetDTO();
        petDTO.setId(888);
        petDTO.setName("jaroWolfy");
        petDTO.setCategory(new Category(1, "category1"));
        petDTO.setPetStatus(PetStatus.AVAILABLE);
        petDTO.setPhotoUrls(List.of("url-1", "url-2", "url-3"));
        petDTO.setTags(List.of(new Tag(1, "tag-1"), new Tag(2, "tag-2")));
        System.out.println("myPet: " + petDTO);
    }
}
