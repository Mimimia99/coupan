package kukka;

import kukka.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PolicyHandler {
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString) {

    }

    @Autowired
    PaymentRepository paymentRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCancelled_PaymentCancellation(@Payload OrderCancelled orderCancelled) {

        if (orderCancelled.isMe()) {
            System.out.println("##### listener PaymentCancellation : " + orderCancelled.toJson());

            //Payment payment = new Payment();
            //payment.setOrderId(orderCancelled.getId());
            Optional<Payment> paymentOptional = paymentRepository.findByOrderId(Long.valueOf(orderCancelled.getId()));
            Payment payment = paymentOptional.get();//위에서 find한 오더 객체를 찾아서 매핑
            payment.setStatus("Payment Cancelled");
            paymentRepository.save(payment);
            payment.setStatus("PaymentCancelled");

            paymentRepository.save(payment);
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {
    }

}