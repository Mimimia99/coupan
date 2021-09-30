package kukka;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;
import java.util.Optional;

@Entity
@Table(name = "Payment_table")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long orderId;
    private Long price;
    private String status;

    @PostPersist
    public void onPostPersist() {

        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$" + this.getStatus());
        if (this.getStatus().equals("Ordered")) {
            PaymentConfirmed paymentConfirmed = new PaymentConfirmed();
            BeanUtils.copyProperties(this, paymentConfirmed);
            System.out.println("payment $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$" + paymentConfirmed.getStatus());
            paymentConfirmed.setStatus("PaymentConfirmed");
            paymentConfirmed.publishAfterCommit();

        } else if (this.getStatus().equals("PaymentCancelled")) {
            System.out.println("$$$$$$$$$$$$$$$$$$$ PaymentCancelled $$$$$$$$$$$$$$$$$$$$$$$$$");
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$" + this.getOrderId());
            PaymentCancelled paymentCancelled = new PaymentCancelled();
            BeanUtils.copyProperties(this, paymentCancelled);
            System.out.println("payment$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$" + paymentCancelled.getStatus());
            paymentCancelled.publishAfterCommit();
        }

    }

    @PostUpdate
    public void onPostPersistPaymentCanceled() {
        System.out.println("$$$$$$$$$$$$$$$$$$$ Payment PostUpdate $$$$$$$$$$$$$$$$$$$$$$$$$");
      
        PaymentCancelled paymentCancelled = new PaymentCancelled();
        BeanUtils.copyProperties(this, paymentCancelled);
        paymentCancelled.publishAfterCommit();
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

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}