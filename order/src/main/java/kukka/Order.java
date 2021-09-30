package kukka;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name = "Order_table")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String phoneNumber;
    private String address;
    private String customerName;
    private String status;
    private String flowerType;
    private Long price;

    @PostPersist
    public void onPostPersist() {
        Ordered ordered = new Ordered();
        BeanUtils.copyProperties(this, ordered);

        this.setStatus("Ordered");
        ordered.publishAfterCommit();

        // Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.
        // 소스확인

        kukka.external.Payment payment = new kukka.external.Payment();
        // mappings goes here

        payment.setOrderId(this.getId());
        payment.setPrice(this.getPrice());
        payment.setStatus("PaymentConfirmed");

        OrderApplication.applicationContext.getBean(kukka.external.PaymentService.class).payment(payment);

    }

    @PostUpdate
    public void onPostUpdate() {
        if (this.getStatus().equals("OrderCancelled")) {
            OrderCancelled orderCancelled = new OrderCancelled();
            BeanUtils.copyProperties(this, orderCancelled);
            orderCancelled.setStatus("OrderCancelled");
            orderCancelled.publishAfterCommit();
        }

    }

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
        return flowerType;
    }

    public void setFlowerType(String flowerType) {
        this.flowerType = flowerType;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

}