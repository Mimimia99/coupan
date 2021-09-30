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
public class MyPageViewHandler {

    @Autowired
    private MyPageRepository myPageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrdered_then_CREATE_1(@Payload Ordered ordered) {
        try {
            if (ordered.isMe()) {
                // view 객체 생성
                MyPage myPage = new MyPage();
                // view 객체에 이벤트의 Value 를 set 함
                myPage.setOrderId(ordered.getId());
                myPage.setFlowerType(ordered.getFlowerType());
                myPage.setPrice(ordered.getPrice());
                myPage.setPhoneNumber(ordered.getPhoneNumber());
                myPage.setAddress(ordered.getAddress());
                myPage.setCustomerName(ordered.getCustomerName());
                myPage.setStatus(ordered.getStatus());

                // view 레파지 토리에 save
                myPageRepository.save(myPage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenDelivered_then_UPDATE_1(@Payload Delivered delivered) {
        try {
            if (delivered.isMe()) {
                // view 객체 조회
                List<MyPage> myPageList = myPageRepository.findByOrderId(delivered.getOrderId());
                for (MyPage myPage : myPageList) {
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setDeliveryStatus(delivered.getStatus());
                    // view 레파지 토리에 save
                    myPageRepository.save(myPage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrderCancelled_then_UPDATE_2(@Payload OrderCancelled orderCancelled) {
        try {
            if (orderCancelled.isMe()) {
                // view 객체 조회
                List<MyPage> myPageList = myPageRepository.findByOrderId(orderCancelled.getId());
                for (MyPage myPage : myPageList) {
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setStatus(orderCancelled.getStatus());
                    // view 레파지 토리에 save
                    myPageRepository.save(myPage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaymentConfirmed_then_UPDATE_4(@Payload PaymentConfirmed paymentConfirmed) {
        try {
            if (paymentConfirmed.isMe()) {
                // view 객체 조회
                List<MyPage> myPageList = myPageRepository.findByOrderId(paymentConfirmed.getOrderId());
                for (MyPage myPage : myPageList) {
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setStatus(paymentConfirmed.getStatus());
                    myPage.setPrice(paymentConfirmed.getPrice());
                    // view 레파지 토리에 save
                    myPageRepository.save(myPage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaymentCancelled_then_UPDATE_5(@Payload PaymentCancelled paymentCancelled) {
        try {
            if (paymentCancelled.isMe()) {
                // view 객체 조회
                List<MyPage> myPageList = myPageRepository.findByOrderId(paymentCancelled.getOrderId());
                for (MyPage myPage : myPageList) {
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setStatus(paymentCancelled.getStatus());
                    // view 레파지 토리에 save
                    myPageRepository.save(myPage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenDeliveryCancelled_then_UPDATE_6(@Payload DeliveryCancelled deliveryCancelled) {
        try {
            if (deliveryCancelled.isMe()) {
                // view 객체 조회
                List<MyPage> myPageList = myPageRepository.findByOrderId(deliveryCancelled.getOrderId());
                for (MyPage myPage : myPageList) {
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setDeliveryStatus(deliveryCancelled.getStatus());
                    // view 레파지 토리에 save
                    myPageRepository.save(myPage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
