package ee.cc.dto;

import com.google.gson.annotations.SerializedName;

public enum PetStatus {
    @SerializedName("available")
    AVAILABLE,
    @SerializedName("pending")
    PENDING,
    @SerializedName("sold")
    SOLD;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
