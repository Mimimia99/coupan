package kukka;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name = "Delivery_table")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long orderId;
    private String status;

    @PostPersist
    public void onPostPersist() {
        Delivered delivered = new Delivered();
        BeanUtils.copyProperties(this, delivered);
        // delivered.setStatus("Delivered");
        delivered.publishAfterCommit();

        // Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        System.out.println("1------------------Delivered----------------");

    }

    @PostUpdate
    public void onPostUpdate() {

        if (this.getStatus().equals("PaymentCancelled")) {

            DeliveryCancelled deliveryCancelled = new DeliveryCancelled();
            BeanUtils.copyProperties(this, deliveryCancelled);
            deliveryCancelled.setStatus("DeliveryCancelled");
            deliveryCancelled.publishAfterCommit();

            System.out.println("1------------Delivery Cancelled-------------");

        }  
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}