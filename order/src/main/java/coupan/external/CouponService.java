
package coupan.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

//@FeignClient(name="coupon", url="http://localhost:8081")
//@FeignClient(name="coupon", url="http://coupon:8080")
@FeignClient(name="coupon", url="${api.url.coupon}")

public interface CouponService {
    @RequestMapping(method= RequestMethod.GET, path="/chkAndModifyStock")
    public boolean modifyStock(@RequestParam("couponId") Long couponId,
                            @RequestParam("qty") Integer qty);

}