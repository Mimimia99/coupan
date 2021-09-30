package kukka;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderDetailRepository extends CrudRepository<OrderDetail, Long> {

    List<OrderDetail> findByOrderId(Long orderId);
}