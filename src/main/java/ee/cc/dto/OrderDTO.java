package ee.cc.dto;

public class OrderDTO {
    /*{
id	integer($int64)
petId	integer($int64)
quantity	integer($int32)
shipDate	string($date-time)
status	string
Order Status

Enum:
[ placed, approved, delivered ]
complete	boolean
}*/
    private long id;
    private long petId;
    private int quantity;
    private String shipDate;
    private OrderStatus status;
    private boolean complete;

    public void setId(long id) {
        this.id = id;
    }

    public void setPetId(long petId) {
        this.petId = petId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setShipDate(String shipDate) {
        this.shipDate = shipDate;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public long getId() {
        return id;
    }

    public long getPetId() {
        return petId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getShipDate() {
        return shipDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public boolean isComplete() {
        return complete;
    }

    public enum OrderStatus {
        PLACED, APPROVED, DELIVERED
    }
}
