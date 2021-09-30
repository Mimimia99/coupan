# 꽃 구독 주문 사이트[kukka]

![image](https://user-images.githubusercontent.com/88864740/135373766-391c1869-bee0-40d9-9b31-8cbe9e0593ce.png)

본 프로젝트는 MSA/DDD/Event Storming/EDA 를 포괄하는 분석/설계/구현/운영 전단계를 커버하도록 구성한 프로젝트입니다.
이는 클라우드 네이티브 애플리케이션의 개발에 요구되는 체크포인트들을 통과하기 위한 내용을 포함합니다.

# 서비스 시나리오

기능적 요구사항
1. 고객이 꽃 구독타입을 선택하여 주문한다.
2. 주문이 되면 결제 시스템의 결제 기능이 호출된다.
3. 결제가 완료되면 주문 내역이 꽃가게 주인에게 전달된다.
4. 꽃가게 주인이 주문을 확인하여 주문한 꽃의 배송을 시작한다.
5. 고객이 주문을 취소할 수 있다.
6. 주문이 취소되면 결제가 취소된다.
7. 결제가 취소되면 배송이 취소된다.
8. 고객은 꽃 주문 및 결제 정보 등을 마이페이지를 통해 수시로 조회한다. ex) 꽃이름, 수량, 상태, 결제금액 등.

비기능적 요구사항
1. 트랜잭션
    1) 결제가 되지 않은 주문건은 아예 거래가 성립되지 않아야 한다 → Sync 호출
2. 장애격리
    1) 배송기능이 수행되지 않더라도 주문은 365일 24시간 받을 수 있어야 한다 → Async (event-driven), Eventual Consistency
    2) 결제시스템이 과중되면 사용자를 잠시동안 받지 않고 결제를 잠시후에 하도록 유도한다 → Circuit breaker, fallback
3. 성능
    1) 고객이 자주 상점관리에서 확인할 수 있는 배달상태를 주문시스템(프론트엔드)에서 확인할 수 있어야 한다 → CQRS


# 체크포인트

1. Saga
2. CQRS
3. Correlation
4. Req/Resp
5. Gateway
6. Deploy/ Pipeline
7. Circuit Breaker
8. Autoscale (HPA)
9. Zero-downtime deploy
10. Config Map / Persistence Volume
11. Polyglot
12. Self-healing (Liveness Probe)


# 분석/설계

## AS-IS 조직 (Horizontally-Aligned)
![image](https://user-images.githubusercontent.com/88864740/135375102-36f22043-72f1-485f-8fba-983d567679c7.png)

## TO-BE 조직 (Vertically-Aligned)
![image](https://user-images.githubusercontent.com/88864740/135375149-af395bc9-0b70-4aca-8598-d00c1b458853.png)


## Event Storming 결과
* MSAEz 로 모델링한 이벤트스토밍 결과:  https://www.msaez.io/#/storming/XUPd3rsARfQbeKeRRQgZyiBCKkf2/f4cdc076d7df427ec32a60cb30cb3f76


### 이벤트 도출
![ddd1](https://user-images.githubusercontent.com/88864740/135374980-7b004286-5ce0-4b5e-9184-7958be606ae8.png)

### 부적격 이벤트 탈락
![ddd2](https://user-images.githubusercontent.com/88864740/135375184-412a2bc2-1931-4c57-9785-2957643dbeb7.png)

    - 과정중 도출된 잘못된 도메인 이벤트들을 걸러내는 작업을 수행함
    - 주문내역을 조회함 :  UI 의 이벤트이지, 업무적인 의미의 이벤트가 아니라서 제외
    - 주문수정됨/결제변경됨/배송변경됨 : 구현범위를 벗어난 이벤트로 제외

### 액터, 커맨드 부착하여 읽기 좋게
![ddd3](https://user-images.githubusercontent.com/88864740/135375283-41815d1b-f387-4efb-acf2-b4504c6f0db0.png)

### 어그리게잇으로 묶기
![ddd4](https://user-images.githubusercontent.com/88864740/135375596-beaf91f7-2265-46a3-9757-a6eece02f926.png)

    - 주문, 결제, 배송와 같이 그와 연결된 command 와 event 들에 의하여 트랜잭션이 유지되어야 하는 단위로 그들 끼리 묶어줌

### 바운디드 컨텍스트로 묶기
![ddd5](https://user-images.githubusercontent.com/88864740/135375629-71cd78a7-7049-4ea7-ad57-44989f222187.png)

    - 도메인 서열 분리 
    - Core Domain: 주문, 결제, 배송: 없어서는 안될 핵심 서비스이며, 연견 Up-time SLA 수준을 99.999% 목표, 배포주기는 주문, 결제, 배송의 경우 1주일 1회 미만
    - Supporting Domain:  -- : 경쟁력을 내기위한 서비스이며, SLA 수준은 연간 60% 이상 uptime 목표, 배포주기는 각 팀의 자율이나 표준 스프린트 주기가 1주일 이므로 1주일 1회 이상을 기                                준으로 함.

### 폴리시 부착및 컨텍스트 매핑
![ddd6](https://user-images.githubusercontent.com/88864740/135375659-340778e5-86f2-43ab-b1ce-ac66c3a6092f.png)

↓ View Model 추가
![ddd7](https://user-images.githubusercontent.com/88864740/135375697-153ee5a1-6a17-42c2-b916-8ee0f16bbdc0.png)

### 완성된 1차 모형(영문으로 변경)

![ddd8](https://user-images.githubusercontent.com/88864740/135375785-7aa3efac-d80e-4775-a9f5-5560df71b074.png)
  

### 1차 완성본에 대한 기능적/비기능적 요구사항을 커버하는지 검증

![ddd9](https://user-images.githubusercontent.com/88864740/135375901-e3c9d104-91b1-4774-ac44-77eb7067e973.png)

    - 고객이 꽃 구독 상품을 구매한다 (ok)
    - 구매가 완료되면 결제가 진행된다 (ok)
    - 결제는 구매내역을 통해 결제가 진행된다. (ok)
    - 결제가 완료되면, 결제완료상태(PaymentConfirmed)로 변경된다. (ok)
    - 꽃구독 주문이 되면 주문내역을 KUKKA 상점으로 전달한다(ok)
    - Kukka 상점에서 꽃 구독 배송 출발한다(ok)

![ddd10](https://user-images.githubusercontent.com/88864740/135375970-6b0e55eb-cc63-4320-90ed-8424d4fa8dc8.png)

    - 고객이 꽃 구독 구매를 취소할 수 있다 (ok)
    - 고객이 꽃 구독 구매를 취소하면 결제가 취소된다. (ok)
    - 결제가 취소되면 꽃 배송을 취소한다. (ok)
    - 고객은 구매 상태와 결제 상태 등을 중간중간 조회한다. (View-green sticker 의 추가로 ok) 


### 비기능 요구사항에 대한 검증

![ddd11](https://user-images.githubusercontent.com/88864740/135376139-7eb6841f-a39a-4d63-b535-7e3f40291164.png)

    - ① : 주문이 완료되면 결제 시스템에서 결제가 진행되어야 한다. (Req/Res)
    - ② : 결제 시스템이 정상 수행되지 않더라도 배송은 365일 24시간 받을 수 있어야 한다. (Pub/sub)
    - ③ : 꽃 구독 주문 시스템이 과중되면 잠시동안 구매를 받지 않고 잠시후에 진행하도록 유도한다 (Circuit breaker)
    - ④ : 고객이 꽃 구독 주문정보를 별도의 고객페이지에서 확인할 수 있어야 한다 (CQRS)
          꽃가게 주인이 고객의 주문정보를 별도의 관리자 페이지에서 확인할 수 있어야 한다 (CQRS)     


## 헥사고날 아키텍처 다이어그램 도출
    
![image](https://user-images.githubusercontent.com/88864740/135376399-c8afcd19-63fb-4463-a161-25667a7317b2.png)

    - Chris Richardson, MSA Patterns 참고하여 Inbound adaptor와 Outbound adaptor를 구분함
    - 호출관계에서 PubSub 과 Req/Resp 를 구분함
    - 서브 도메인과 바운디드 컨텍스트의 분리:  각 팀의 KPI 별로 아래와 같이 관심 구현 스토리를 나눠가짐


# 구현:

구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 8083이다)

```
cd order
mvn spring-boot:run

cd payment
mvn spring-boot:run 

cd delivery
mvn spring-boot:run
```

## DDD 의 적용

- 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다. 아래 Order가 그 예시이다.
```
package kukka;
import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;
@Entity
@Table(name = "Order_table")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String phoneNumber;
    private String address;
    private String customerName;
    private String status;
    private String flowerType;
    private Long price;

.....

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getFlowerType() {
        return flowerType;
    }
    public void setFlowerType(String flowerType) {
        this.flowerType = flowerType;
    }
    public Long getPrice() {
        return price;
    }
    public void setPrice(Long price) {
        this.price = price;
    }
}

```

   - Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 데이터 접근 어댑터를 자동 생성하기 위하여   
     Spring Data REST 의 RestRepository 를 적용하였다
```
package kukka;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;
@RepositoryRestResource(collectionResourceRel="orders", path="orders")
public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {
}

```

    - 적용 후 REST API 의 테스트: 꽃 구독 주문(order) 시, 결제(payment) 시스템 내  결제내역 생성되므로 order 서비스에 대한 정상 처리 여부를 확인한다.
```
< local에서 구동 시>

# order 서비스의 꽃 구독 주문 처리
http POST http://localhost:8081/orders phoneNumber="01012341234" address="SEOUL" customerName="KIM" flowerType="MixBox" price=10000 

# 주문 상태 확인
http GET http://localhost:8081/orders
http GET http://localhost:8081/myPages

# 결제 상태 확인
http GET http://localhost:8082/payments

# 주문상세 확인
http GET http://localhost:8083/orderDetails

# 배송 처리 후 확인

http POST http://localhost:8083/deliveries orderId=1 status="Delivered"
http GET http://localhost:8083/deliveries

# MyPage 확인
http GET http://localhost:8081/myPages
```

```
< aws에서 구동 시>

# order 서비스의 꽃 구독 주문 처리
http POST http://acc10696f75b642ab8a5c2b0f763227e-1739077027.ap-northeast-2.elb.amazonaws.com:8080/orders phoneNumber="01012341234" address="Seoul" customerName="KWON" flowerType="RoseBox" price=10000 status="Ordered" 

# 주문 상태 확인
http GET http://acc10696f75b642ab8a5c2b0f763227e-1739077027.ap-northeast-2.elb.amazonaws.com:8080/orders
http GET http://acc10696f75b642ab8a5c2b0f763227e-1739077027.ap-northeast-2.elb.amazonaws.com:8080/myPages

# 결제 상태 확인
http GET http://acc10696f75b642ab8a5c2b0f763227e-1739077027.ap-northeast-2.elb.amazonaws.com:8080/payments

# 주문상세 확인
http GET http://acc10696f75b642ab8a5c2b0f763227e-1739077027.ap-northeast-2.elb.amazonaws.com:8080/orderDetails

# 배송 처리 후 확인
http POST  http://acc10696f75b642ab8a5c2b0f763227e-1739077027.ap-northeast-2.elb.amazonaws.com:8080/deliveries orderId=1 status="Delivered"
http GET http://acc10696f75b642ab8a5c2b0f763227e-1739077027.ap-northeast-2.elb.amazonaws.com:8080/deliveries

# MyPage 확인
http GET http://acc10696f75b642ab8a5c2b0f763227e-1739077027.ap-northeast-2.elb.amazonaws.com:8080/myPages
```

## 폴리글랏 퍼시스턴스

order 서비스와 payment 서비스는 h2 DB로 구현하고, 그와 달리 delivery 서비스의 경우 mySQL DB로 구현하여, MSA간 서로 다른 종류의 DB간에도 문제 없이 동작하여 다형성을 만족하는지 확인하였다.(Spring Cloud JPA를 사용하여 개발하였기 때문에 소스의 변경 부분은 전혀 없으며, 단지 데이터베이스 제품의 설정 (pom.xml, application.yml) 만으로 mysql 에 부착시켰다.)


- Order, payment 서비스의 pom.xml 설정
```
# pom.yml (Order, Payment)
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>runtime</scope>
		</dependency>
```

- Delivery 서비스의 pom.xml 설정
```
# pom.yml (Delivery)

		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<version>8.0.25</version>
		</dependency>
		<dependency>
```

- Delivery 의 application.yml
```
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://mysql-1631696398.default.svc.cluster.local:3306/class?useSSL=false&characterEncoding=UTF-8&serverTimezone=UTC
    username: root
    password: RmDqZpf2rq
  jpa:
    database: mysql
    database-platform: org.hibernate.dialect.MySQL5InnoDBDialect
    generate-ddl: true
    show-sql: true 
```


## CQRS

Viewer를 별도로 구현하여 아래와 같이 view가 출력된다.

- myPage 구현

↓ 꽃 구독 주문 완료 시, 데이터 생성
```
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
```

↓ 주문 결제 완료 시 또는 주문 결제취소 시에 상태값 등이 변경된다.
```
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
.....
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

```

- 꽃 구독 주문 후의 MyPage
![image](https://user-images.githubusercontent.com/88864740/135501174-5d33dc70-99ee-498b-b0cf-97359ace4ee7.png)

- 꽃 구독 주문 취소 후의 myPage (변경된 상태 노출값 확인 가능)
![image](https://user-images.githubusercontent.com/88864740/135501962-c939538f-7a14-431b-9981-d0ea7c9d1c63.png)


## 동기식 호출(Req/Resp)

분석단계에서의 조건 중 하나로 주문(order)→결제(payment)간의 호출은 동기식 일관성을 유지하는 트랜잭션으로 처리하기로 하였다. 호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어 있는 REST 서비스를 FeignClient 를 이용하여 호출하도록 한다.

- 쿠폰서비스를 호출하기 위하여 FeignClient를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현
```
# (order) PaymentService.java

package kukka.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@FeignClient(name = "payment", url = "${api.url.payment}")
public interface PaymentService {

    @RequestMapping(method = RequestMethod.GET, path = "/payments")
    public void payment(@RequestBody Payment payment);

}
```

- 주문된 직후(@PostPersist) 결제정보가 생성 되어 결제처리(PaymentConfirmed) 되도록 처리 (payment 생성)
```
# Order.java

    @PostPersist
    public void onPostPersist() {
        Ordered ordered = new Ordered();
        BeanUtils.copyProperties(this, ordered);

        this.setStatus("Ordered");
        ordered.publishAfterCommit();

        kukka.external.Payment payment = new kukka.external.Payment();
        // mappings goes here

        payment.setOrderId(this.getId());
        payment.setPrice(this.getPrice());
        payment.setStatus("PaymentConfirmed");

        OrderApplication.applicationContext.getBean(kukka.external.PaymentService.class).payment(payment);

    }    
```

- 동기식 호출에서는 호출 시간에 따른 타임 커플링이 발생하며, 결제 시스템(payment)이 장애가 나면 예약도 못하는 것을 확인

결제 서비스(payment)를 잠시 내려놓음
![image](https://user-images.githubusercontent.com/88864740/135502643-6542006f-8295-4de3-8c72-f77def449a29.png)


↓ 꽃구독 주문하기(order)
```
http POST http://acc10696f75b642ab8a5c2b0f763227e-1739077027.ap-northeast-2.elb.amazonaws.com:8080/orders phoneNumber="01033334444" address="Bundang" customerName="LEE" flowerType="RandomBox" price=20000  status="Ordered"
```
< Fail >
↓ 꽃 구독 주문 시 500 error 발생
![image](https://user-images.githubusercontent.com/88864740/135502812-924919b4-a8e7-41ca-94ec-e2d2ea926451.png)

- 결제(payment) 서비스 재기동
![image](https://user-images.githubusercontent.com/88864740/135502993-fb76c473-f5a7-4265-81f3-3caadcffc900.png)

- 꽃 구독 주문하기(order)
```
http POST http://acc10696f75b642ab8a5c2b0f763227e-1739077027.ap-northeast-2.elb.amazonaws.com:8080/orders phoneNumber="01033334444" address="Bundang" customerName="LEE" flowerType="RandomBox" price=20000  status="Ordered"
```
< Success >
![image](https://user-images.githubusercontent.com/88864740/135503087-ef071d00-7d33-460d-9f4f-d849856e2127.png)


- 결제완료가 생성되었는지 확인을 통해 정상 req/res 처리 여부 확인

↓ payment 에 결제완료 정보 생성
![image](https://user-images.githubusercontent.com/88864740/135503580-1bdf9b66-13b1-4b4a-857a-ff1c6a9b5191.png)


## Gateway 적용

- gateway > applitcation.yml 설정

```
server:
  port: 8088

---

spring:
  profiles: default
  cloud:
    gateway:
      routes:
        - id: order
          uri: http://localhost:8081
          predicates:
            - Path=/orders/**, /myPages/**
        - id: payment
          uri: http://localhost:8082
          predicates:
            - Path=/payments/** 
        - id: delivery
          uri: http://localhost:8083
          predicates:
            - Path=/deliveries/**, /orderDetails/**
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true


---

spring:
  profiles: docker
  cloud:
    gateway:
      routes:
        - id: order
          uri: http://order:8080
          predicates:
            - Path=/orders/**, /myPages/**
        - id: payment
          uri: http://payment:8080
          predicates:
            - Path=/payments/** 
        - id: delivery
          uri: http://delivery:8080
          predicates:
            - Path=/deliveries/**, /orderDetails/**
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true

server:
  port: 8080
```

- gateway 테스트(로컬)

```
http POST http://localhost:8088/orders phoneNumber="01055556666" address="Jeju" customerName="Han" flowerType="DaisyBox" price=30000  status="Ordered"
```

↓ gateway 8088포트로 쿠폰 등록 처리 시,  8081 포트로 링크되어 정상 처리
![image](https://user-images.githubusercontent.com/88864740/135384854-c2cdee92-00d2-4112-be68-7cb35b723833.png)


- gateway 테스트(aws)

```
http POST http://acc10696f75b642ab8a5c2b0f763227e-1739077027.ap-northeast-2.elb.amazonaws.com:8080/orders phoneNumber="01033334444" address="Bundang" customerName="LEE" flowerType="RandomBox" price=20000  status="Ordered"
```

↓ gateway 8088포트로 쿠폰 등록 처리 시,  http://order:8080/ 으로 링크되어 정상 처리
![image](https://user-images.githubusercontent.com/88864740/135503933-6be603f0-51bb-4011-858a-e96852767b43.png)



## 비동기식 호출(Pub/Sub)

- 결제 시스템(payment)에서  처리 후에 배송 시스템(delivery)에서 배송 처리되는 행위는 동기식이 아니라 비 동기식으로 처리하여 배송 시스템(delivery)의 배송 처리로 인해 주문시스템이 블로킹 되지 않도록 처리한다. 이를 위하여 결제완료 되었음을 도메인 이벤트를 카프카로 송출한다(Publish)
 
```
# Payment.java

package kukka;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;
import java.util.Optional;

@Entity
@Table(name = "Payment_table")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long orderId;
    private Long price;
    private String status;

    @PostPersist
    public void onPostPersist() {

        if (this.getStatus().equals("Ordered")) {
            PaymentConfirmed paymentConfirmed = new PaymentConfirmed();
            BeanUtils.copyProperties(this, paymentConfirmed);
            paymentConfirmed.setStatus("PaymentConfirmed");
            paymentConfirmed.publishAfterCommit();

        } 
    }
```

- 배송 시스템(delivery)에서는 결제가 발생하면 이벤트를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다. (subscribe)
```
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
```

- 결제 시스템(payment)는 배송 시스템(delivery)와 완전히 분리되어있으며(sync transaction 없음) 이벤트 수신에 따라 처리되기 때문에, 배송 서비스(delivery)가 유지보수로 인해 잠시 내려간 상태라도 꽃 구독 주문 진행에 문제 없다.(시간적 디커플링)
  
↓ 배송(delivery) 서비스를 잠시 내려놓음(ctrl+c)  :8082 포트 확인되지 않음
![image](https://user-images.githubusercontent.com/88864740/135386702-13c75dd4-3100-42a3-a175-d9f0527165c7.png)

↓ 꽃 구독 주문하기(order)
```
http POST http://localhost:8081/orders phoneNumber="01012345678" address="Busan" customerName="PARK" flowerType="LilyBox" price=15000 status="Ordered"
```

< Success >
![image](https://user-images.githubusercontent.com/88864740/135386799-66c14667-cf9c-4997-82b2-234f9dec67db.png)

↓ delivery 와 연동되지 않아 myPage에서 확인 시, delivery 시스템의 처리 영역은 확인되지 않는다. ex) deliveryStatus
![image](https://user-images.githubusercontent.com/88864740/135387132-0a9042f7-e4c4-46c8-87cc-134a99bf44fc.png)


## Deploy / Pipeline

### codebuild 사용

* codebuild를 사용하여 pipeline 생성 및 배포

```
# (Order) buildspec.yml

version: 0.2

env:
  variables:
    _PROJECT_NAME: "user03-order"

phases:
  install:
    commands:
      - echo install kubectl
      - curl -LO https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl
      - chmod +x ./kubectl
      - mv ./kubectl /usr/local/bin/kubectl
  pre_build:
    commands:
      - echo Logging in to Amazon ECR...
      - echo $_PROJECT_NAME
      - echo $AWS_ACCOUNT_ID
      - echo $AWS_DEFAULT_REGION
      - echo $CODEBUILD_RESOLVED_SOURCE_VERSION
      - echo start command
      - docker login --username AWS -p $(aws ecr get-login-password --region $AWS_DEFAULT_REGION) $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com
  build:
    commands:
      - echo Build started on `date`
      - echo Building the Docker image...
      - mvn package -Dmaven.test.skip=true
      - docker build -t $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com/$_PROJECT_NAME:$CODEBUILD_RESOLVED_SOURCE_VERSION  .
  post_build:
    commands:
      - echo Pushing the Docker image...
      - docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com/$_PROJECT_NAME:$CODEBUILD_RESOLVED_SOURCE_VERSION
      - echo connect kubectl
      - kubectl config set-cluster k8s --server="$KUBE_URL" --insecure-skip-tls-verify=true
      - kubectl config set-credentials admin --token="$KUBE_TOKEN"
      - kubectl config set-context default --cluster=k8s --user=admin
      - kubectl config use-context default
      - |
          cat <<EOF | kubectl apply -f -
          apiVersion: v1
          kind: Service
          metadata:
            name: $_PROJECT_NAME
            labels:
              app: $_PROJECT_NAME
          spec:
            ports:
              - port: 8080
                targetPort: 8080
            selector:
              app: $_PROJECT_NAME
          EOF
      - |
          cat  <<EOF | kubectl apply -f -
          apiVersion: apps/v1
          kind: Deployment
          metadata:
            name: $_PROJECT_NAME
            labels:
              app: $_PROJECT_NAME
          spec:
            replicas: 1
            selector:
              matchLabels:
                app: $_PROJECT_NAME
            template:
              metadata:
                labels:
                  app: $_PROJECT_NAME
              spec:
                containers:
                  - name: $_PROJECT_NAME
                    image: $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com/$_PROJECT_NAME:$CODEBUILD_RESOLVED_SOURCE_VERSION
                    ports:
                      - containerPort: 8080
                    readinessProbe:
                      httpGet:
                        path: /actuator/health
                        port: 8080
                      initialDelaySeconds: 10
                      timeoutSeconds: 2
                      periodSeconds: 5
                      failureThreshold: 10
                    livenessProbe:
                      httpGet:
                        path: /actuator/health
                        port: 8080
                      initialDelaySeconds: 120
                      timeoutSeconds: 2
                      periodSeconds: 5
                      failureThreshold: 5
          EOF
cache:
  paths:
    - '/root/.m2/**/*'
``` 

- order 서비스 배포 
![image](https://user-images.githubusercontent.com/88864740/135436874-a7c27984-fed1-4098-881f-3b1be3ffbd31.png)

- order 서비스 배포 진행 단계 
![image](https://user-images.githubusercontent.com/88864740/135437079-00847230-4155-4aa1-88b7-46a11747cd5d.png)

- 서비스 배포 상태
![image](https://user-images.githubusercontent.com/88864740/135499446-51cac160-aec2-4fe8-9011-a67a13c5f54a.png)

![image](https://user-images.githubusercontent.com/88864740/135498339-545691db-5295-42a8-b85f-61326fedb02c.png)


## 서킷 브레이킹(Circuit Breaking)

* 서킷 브레이킹 프레임워크의 선택 :  Spring FeignClient + Hystrix 사용하여 Circuit breaking 구현함
- 시나리오 : 쿠폰 구매 시, order → payment 에서 req/res 로 구현되어 있어, 주문이 과도한 요청으로 지연이 발생하는 경우 CirCuit Breaker 통해 장애격리한다.

- Hystrix 설정: : 요청처리 시간이 300 밀리세컨이 초과할 경우 Circuit breaker 가 동작하여 처리하도록 적용
- 피호출되는 payment 서비스에 sleep을 설정해 타임아웃 시간에 걸리도록 설정한다.

- order 서비스 > application.yml
```
feign:
  hystrix:
    enabled: true

hystrix:
  command:
    default:
      execution.isolation.thread.timeoutInMilliseconds: 300

```

- payment 서비스 로직에 sleep 추가 
```
    @PostPersist
    public void onPostPersist() {

        if (this.getStatus().equals("Ordered")) {
            PaymentConfirmed paymentConfirmed = new PaymentConfirmed();
            BeanUtils.copyProperties(this, paymentConfirmed);
            paymentConfirmed.setStatus("PaymentConfirmed");
            paymentConfirmed.publishAfterCommit();
            
            try {
                Thread.currentThread().sleep((long) (200 + Math.random() * 110));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
```

* 부하테스터 siege 툴을 통한 서킷 브레이커 동작 확인:
- 동시사용자 100명
- 30초 동안 실시

```
siege -c50 -t30S -v --content-type "application/json" 'http://order:8080/orders POST {"flowerType":"AAAA","price":10000}'
```
↓변경필요!!
![image](https://user-images.githubusercontent.com/84000890/124469554-783d7c00-ddd5-11eb-9c85-72e1ef9a3730.png)

앞서 설정한 부하가 발생하여 Circuit Breaker가 발동, 초반에는 요청 실패처리되었으며
밀린 부하가 처리되면서 다시 요청을 받기 시작함

- Availability 가 높아진 것을 확인 (siege) 
↓변경필요!!
![image](https://user-images.githubusercontent.com/84000890/124469595-84c1d480-ddd5-11eb-9789-0f8890d09c53.png)

↓ 실패/성공 건을 보통 대략 0.9초 이상 수행 시 풀렸다가 다시 CB 동작이  것으로 확인됨
↓변경필요!!
![image](https://user-images.githubusercontent.com/84000890/124469629-90150000-ddd5-11eb-9d95-998554bc943e.png)



### Autoscale (HPA)
앞서 CB 는 시스템을 안정되게 운영할 수 있게 해줬지만 사용자의 요청을 100% 받아들여주지 못했기 때문에 이에 대한 보완책으로 자동화된 확장 기능을 적용하고자 한다. 

- 진행 전 기본 배포 상태

![image](https://user-images.githubusercontent.com/84000890/124469846-d79b8c00-ddd5-11eb-8f6d-80d1d7e3cfd3.png)


- 쿠폰 구매(order) 서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 설정은 CPU 사용량이 15프로를 넘어서면 replica 를 10개까지 늘려준다:
```
kubectl autoscale deploy order --min=1 --max=10 --cpu-percent=15 -n auto

kubectl get hpa -n auto
```
![image](https://user-images.githubusercontent.com/84000890/124470021-04e83a00-ddd6-11eb-8efa-37bc3fae8451.png)


- CB 에서 했던 방식대로 워크로드를 30초 동안 걸어준다.
```
siege -c50 -t30S -v --content-type "application/json" 'http://order:8080/orders POST {"flowerType":"AAAA","price":10000}'
```
↓변경필요!!
![image](https://user-images.githubusercontent.com/84000890/124470087-1f221800-ddd6-11eb-8c64-b5bf81681a01.png)

- 오토스케일이 어떻게 되고 있는지 모니터링을 걸어둔다:
```
watch -n 1 kubectl get pod
```



- 어느정도 시간이 흐른 후 (약 30초) 스케일 아웃이 벌어지는 것을 확인할 수 있다

![image](https://user-images.githubusercontent.com/84000890/124470193-45e04e80-ddd6-11eb-85e9-aab2d4516142.png)



- siege 의 로그를 보아도 전체적인 성공률이 높아진 것을 확인 할 수 있다.
  (동일 워크로드 CB 대비 3.83% -> 50.6% 성공률 향상)

![image](https://user-images.githubusercontent.com/84000890/124470165-3d881380-ddd6-11eb-9e76-f1107492f7cc.png)



## 무정지 재배포

* 먼저 무정지 재배포가 100% 되는 것인지 확인하기 위해서 Autoscaler 이나 CB 설정을 제거함

- seige 로 배포작업 직전에 워크로드를 모니터링 함.
```
$ siege -c10 -t60S -v --content-type "application/json" -r10 -v --content-type "application/json" 'http://order:8080/orders POST {"couponId":"1", "customerId":"2", "qty":"1", "amt":"30000", "status":"ordered"}'
```
- Readiness가 설정되지 않은 yml 파일로 배포 중 버전3에서 버전4로 업그레이드 시 서비스 요청 처리 실패
```
kubectl set image deploy order order=user18skccacr.azurecr.io/order:v4 -n read
```

↓ 버전 업그레이드 시작과 동시에 에러 발생

![image](https://user-images.githubusercontent.com/84000890/124468725-71fad000-ddd4-11eb-984d-7e7ca77c3dc7.png)

- deployment.yml에 readiness 옵션을 추가
```
 readinessProbe:
   tcpSocket:
     port: 8080
   initialDelaySeconds: 10
   timeoutSeconds: 2
   periodSeconds: 5
   failureThreshold: 5          
```

↓ 버전은 다시 한번 버전 3 상태로 맞춤
![image](https://user-images.githubusercontent.com/84000890/124468939-b25a4e00-ddd4-11eb-94c8-f50f3c1a6212.png)


- 동일하게 seige 로 배포작업 직전에 워크로드를 모니터링 함.
```
$ siege -c10 -t60S -v --content-type "application/json" -r10 -v --content-type "application/json" 'http://order:8080/orders POST {"couponId":"1", "customerId":"2", "qty":"1", "amt":"30000", "status":"ordered"}'
```

- readiness 옵션을 배포 옵션을 설정 한 경우 Availability가 배포기간 동안 변화가 없기 때문에 무정지 재배포가 성공한 것으로 확인됨.

![image](https://user-images.githubusercontent.com/84000890/124469148-f51c2600-ddd4-11eb-82cd-680953c23dd3.png)



## ConfigMap
- req/res 호출 시 피호출되는 경로에 대해서 환경변수로 받아 처리하도록 ConfigMap적용
- order 서비스의 deployment.yml 파일에 아래 항목 추가
```
          env:
            - name: configurl			
              valueFrom:
                configMapKeyRef:
                  name: apiurl
                  key: url 
```
- order 서비스의 application.yml 파일 설정 : local 과 cloud 다른 값으로 처리하도록 설정
```
spring:
  profiles: default
  
  ....
  
api:
  url:
    coupon: http://localhost:8081   
---
spring:
  profiles: docker
  
  ....
  
api:
  url:
    coupon: ${configurl}  
```

- order > CouponService.java 에서 feignClient 호출 시 configMap 설정 데이터 가져오도록 아래 항목 추가

```
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
```

- ConfigMap 생성 및 조회

```
kubectl create configmap apiurl --from-literal=url=http://coupon:8080
kubectl get configmap apiurl -o yaml
```

![image](https://user-images.githubusercontent.com/84000890/124415681-51a92200-dd90-11eb-91bd-cd1c57565b7f.png)


- ConfigMap 설정 정상 여부 확인 : req/res 처리 정상 여부 확인

↓ 쿠폰 구매 정상 처리

![image](https://user-images.githubusercontent.com/84000890/124415826-992fae00-dd90-11eb-8700-5a66c7cd8663.png)

↓ 수량도 정상 변경 처리

![image](https://user-images.githubusercontent.com/84000890/124415831-9f258f00-dd90-11eb-8a7f-b4d09715396e.png)



## Self-Healing (Liveness Probe)

- 구매(order) 서비스의 deployment.yaml에 liveness probe 옵션 추가 
- 5초 간격으로 /tmp/healthy 에 접근해도록 강제 설정
```
            args:
            - /bin/sh
            - -c
            - touch /tmp/healthy; sleep 30; rm -rf /tmp/healthy; sleep 600
            livenessProbe:
              exec:
                command:
                - cat
                - /tmp/healthy
              initialDelaySeconds: 5
              periodSeconds: 5 
```

![image](https://user-images.githubusercontent.com/84000890/124466540-b46edd80-ddd1-11eb-9a60-2628073de341.png)
 
- order에 liveness 적용 확인 

![image](https://user-images.githubusercontent.com/84000890/124466634-c5b7ea00-ddd1-11eb-8f49-19e924dccd61.png)

- order 서비스에 liveness가 발동되었고, 포트에 응답이 없기에 Restart가 발생함

![image](https://user-images.githubusercontent.com/84000890/124466572-ba64be80-ddd1-11eb-88b8-40678c9ea1ac.png)
