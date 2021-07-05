package coupan;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name="Payment_table")
public class Payment {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long couponId;
    private Long orderId;
    private String status;
    private Integer payAmt;
    private String payDate;
    private String payCancelDate;
    
    @PostPersist
    public void onPostPersist(){
        Payed payed = new Payed();
        this.setStatus("PayCompleted");

        SimpleDateFormat format1 = new SimpleDateFormat ( "yyyyMMddHHmmss");
        String payDate = format1.format (System.currentTimeMillis());
        this.setPayDate(payDate);

        BeanUtils.copyProperties(this, payed);
        payed.publishAfterCommit();

   }

    @PreUpdate
    public void onPreUpdate(){
        Payed payed = new Payed();
        this.setStatus("PayCancelled");

        SimpleDateFormat format1 = new SimpleDateFormat ( "yyyyMMddHHmmss");
        String payCancelDate = format1.format (System.currentTimeMillis());
        this.setPayCancelDate(payCancelDate);

        BeanUtils.copyProperties(this, payed);
        payed.publishAfterCommit();
        
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
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
    public Integer getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(Integer payAmt) {
        this.payAmt = payAmt;
    }
    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }
    public String getPayCancelDate() {
        return payCancelDate;
    }

    public void setPayCancelDate(String payCancelDate) {
        this.payCancelDate = payCancelDate;
    }




}
