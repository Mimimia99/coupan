package kukka;

import kukka.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyHandler {
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString) {

    }

    @Autowired
    DeliveryRepository deliveryRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentConfirmed_OrderConfirm(@Payload PaymentConfirmed paymentConfirmed) {

        if (paymentConfirmed.isMe()) {
            System.out.println("##### listener OrderConfirm : " + paymentConfirmed.toJson());

            Delivery delivery = new Delivery();
            delivery.setOrderId(paymentConfirmed.getOrderId());
            delivery.setStatus("Delivered");
            deliveryRepository.save(delivery);
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentCancelled_OrderCancel(@Payload PaymentCancelled paymentCancelled) {

        if (paymentCancelled.isMe()) {
            System.out.println("##### listener OrderCancellation : " + paymentCancelled.toJson());

            List<Delivery> deliveryOptional = deliveryRepository
                    .findByOrderId(Long.valueOf(paymentCancelled.getOrderId()));

            for (Delivery delivery : deliveryOptional) {
                delivery.setStatus("DeliveryCancelled");
                deliveryRepository.save(delivery);
            }
        }
    }

}