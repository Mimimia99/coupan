package kukka;

public class OrderCancelled extends AbstractEvent {

    private Long id;
    private String phoneNumber;
    private String address;
    private String customerName;
    private String status;
    private String FlowerType;
    private Long Price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getFlowerType() {
        return FlowerType;
    }

    public void setFlowerType(String FlowerType) {
        this.FlowerType = FlowerType;
    }
    public Long getPrice() {
        return Price;
    }

    public void setPrice(Long Price) {
        this.Price = Price;
    }
}