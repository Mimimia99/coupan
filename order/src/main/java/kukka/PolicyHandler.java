package kukka;

import kukka.config.kafka.KafkaProcessor;
import feign.Request.Options;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import java.util.Optional;
import org.springframework.beans.BeanUtils;

@Service
public class PolicyHandler {
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString) {

    }

    @Autowired
    OrderRepository orderRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDelivered_OrderStatus(@Payload Delivered delivered) {

        // if(!delivered.validate()) return;

        if (delivered.isMe()) {
            System.out.println("##### listener OrderStatus : " + delivered.toJson());
            // Order order = new Order();
            // BeanUtils.copyProperties(this, order);
            // order.setId(delivered.getOrderId());
            Optional<Order> orderOptional = orderRepository.findById(delivered.getOrderId());
            Order order = orderOptional.get();// 위에서 find한 오더 객체를 찾아서 매핑
            order.setStatus("Delivered");
            orderRepository.save(order);

        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentConfirmed_OrderStatus(@Payload PaymentConfirmed paymentConfirmed) {

        if (paymentConfirmed.isMe()) {
            System.out.println("##### listener OrderStatus : " + paymentConfirmed.toJson());

            Optional<Order> orderOptional = orderRepository.findById(paymentConfirmed.getOrderId());
            // if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setStatus("PaymentConfirmed");

            orderRepository.save(order);
            // }
        }

    }

}