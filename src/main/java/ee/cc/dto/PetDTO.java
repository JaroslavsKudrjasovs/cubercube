package ee.cc.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class PetDTO extends ApiResponse{
    private long id;
    private Category category;
    private String name;
    private List<String> photoUrls;
    private List<Tag> tags;
    @SerializedName("status")
    private PetStatus petStatus;

    public void setId(long id) {
        this.id = id;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void setPetStatus(PetStatus petStatus) {
        this.petStatus = petStatus;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"category\":" + category +
                ", \"name\":\"" + name + '\"' +
                ", \"photoUrls\":" + photoUrls.stream().map(x -> "\"" + x + "\"").collect(Collectors.joining(",", "[", "]")) +
                ", \"tags\":" + tags +
                ", \"status\":\"" + petStatus + '\"' +
                '}';
    }

    public long getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public PetStatus getPetStatus() {
        return petStatus;
    }
}
