package kukka;

import kukka.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class OrderDetailViewHandler {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrdered_then_CREATE_1(@Payload Ordered ordered) {
        try {
            if (ordered.isMe()) {
                // view 객체 생성
                OrderDetail orderDetail = new OrderDetail();
                // view 객체에 이벤트의 Value 를 set 함
                orderDetail.setOrderId(ordered.getId());
                orderDetail.setFlowerType(ordered.getFlowerType());
                orderDetail.setPrice(ordered.getPrice());
                orderDetail.setPhoneNumber(ordered.getPhoneNumber());
                orderDetail.setAddress(ordered.getAddress());
                orderDetail.setCustomerName(ordered.getCustomerName());
                orderDetail.setStatus(ordered.getStatus());
                // view 레파지 토리에 save
                orderDetailRepository.save(orderDetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrderCancelled_then_UPDATE_1(@Payload OrderCancelled orderCancelled) {
        try {
            if (orderCancelled.isMe()) {
                // view 객체 조회
                List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(orderCancelled.getId());
                for (OrderDetail orderDetail : orderDetailList) {
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    orderDetail.setStatus(orderCancelled.getStatus());
                    // view 레파지 토리에 save
                    orderDetailRepository.save(orderDetail);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenDelivered_then_UPDATE_2(@Payload Delivered delivered) {
        try {
            if (delivered.isMe()) {
                // view 객체 조회
                List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(delivered.getOrderId());
                for (OrderDetail orderDetail : orderDetailList) {
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    orderDetail.setDeliveryStatus(delivered.getStatus());
                    // view 레파지 토리에 save
                    orderDetailRepository.save(orderDetail);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaymentConfirmed_then_UPDATE_3(@Payload PaymentConfirmed paymentConfirmed) {
        try {
            if (paymentConfirmed.isMe()) {
                // view 객체 조회
                List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(paymentConfirmed.getOrderId());
                for (OrderDetail orderDetail : orderDetailList) {
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    orderDetail.setStatus(paymentConfirmed.getStatus());
                    orderDetail.setPrice(paymentConfirmed.getPrice());
                    // view 레파지 토리에 save
                    orderDetailRepository.save(orderDetail);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaymentCancelled_then_UPDATE_4(@Payload PaymentCancelled paymentCancelled) {
        try {
            if (paymentCancelled.isMe()) {
                // view 객체 조회
                List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(paymentCancelled.getOrderId());
                for (OrderDetail orderDetail : orderDetailList) {
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    orderDetail.setStatus(paymentCancelled.getStatus());
                    // view 레파지 토리에 save
                    orderDetailRepository.save(orderDetail);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenDeliveryCancelled_then_UPDATE_5(@Payload DeliveryCancelled deliveryCancelled) {
        try {
            if (deliveryCancelled.isMe()) {
                // view 객체 조회
                List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(deliveryCancelled.getOrderId());
                for (OrderDetail orderDetail : orderDetailList) {
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    orderDetail.setDeliveryStatus(deliveryCancelled.getStatus());
                    // view 레파지 토리에 save
                    orderDetailRepository.save(orderDetail);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
