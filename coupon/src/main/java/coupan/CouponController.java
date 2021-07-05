package coupan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

 @RestController
 public class CouponController {
        @Autowired
        CouponRepository couponRepository;

@RequestMapping(value = "/chkAndModifyStock",
        method = RequestMethod.GET,
        produces = "application/json;charset=UTF-8")

public boolean modifyStock(HttpServletRequest request, HttpServletResponse response)
        {
                boolean status = false;
                Long couponId = Long.valueOf(request.getParameter("couponId"));
                int qty = Integer.parseInt(request.getParameter("qty"));

                Coupon coupon = couponRepository.findByCouponId(couponId);

                if(coupon != null){
                        if (coupon.getStock() >= qty) {

                                coupon.setStock(coupon.getStock() - qty);
                                couponRepository.save(coupon);
                                status = true;                               
                        }
                  }                         

                return status;
        }

        
 }
