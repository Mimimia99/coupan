package kukka;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.Optional;

//@RepositoryRestResource(collectionResourceRel="payments", path="payments")
public interface PaymentRepository extends PagingAndSortingRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);
}
