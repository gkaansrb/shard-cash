# KakaoPay 과제(카카오페이 뿌리기)

### Spec
* [프로그램 언어 : Kotlin])
* [빌드 : gradle])
* [플랫폼 : Spring Boot])
* [DB : H2])
* [Test Toolkit : Mockito, Mock-kotlin])
* [port : 8080])
* [Database 접근 주소 : http://localhost:8080/h2-console/login.jsp?jsessionid=cb5c3579f69c28e5ddde4d4ebc28c11c]
* [JDBC URL : jdbc:h2:mem:kakao;DB_CLOSE_ON_EXIT=FALSE;AUTOCOMMIT=OFF)]
* [JPA 연동 : QueryDsl, Spring-boot-starter-data-jpa]


### build


### Logic

* 뿌리기 기본은 요청 금액/뿌리는인원 으로 1/n 으로 나뉩니다
* 1/n 을 하고 난 잔액은 남은만큼만 각 Cashshared 에 1원씩 추가 분배 됩니다
* Token 은 3자리 문자로 숫자 및 알파벳 대소문자로 랜덤하게 구성 됩니다
* 동시성 문제는 @Version annotation 으로 방어하였습니다.
* Token 의 생성 경우 기존에 유효하게 생성된 것이 있다면 계속 재생성하여 유일값으로 return 합니다.
* Token 의 유효성은 생성 후 7일간이며, 이후 조회가 불가능하여 유효하지 않다고 판단합니다.
* 7일이 지나 뿌리기 획득 및 조회가 만료된 Token 값의 경우 다시 재생성 해서 사용할 수 있습니다
* 캐시 뿌리기 주문이 생성된 지 10분이 지나면 캐시를 획득하지 못합니다

### Test
* 서버 기동 후 CashShare.http 을 이용하여 기본적인 뿌리기, 받기, 보기 API 를 테스트 할 수 있습니다(intelliJ IDE 한정) 
* CashShareServiceTest.kt 파일에 기본 요건에 대한 테스트 케이스가 작성되어 있습니다. 

