# toString을 항상 재정의하라 - item12

```java
User user = new User();

System.out.print(user); // user@adbbd
```

객체 자체를 출력하면 

클래스_이름@16진수로_표시한_해시코드 가 반환된다. ( Objects.toString() )

그러나, 개발자들은 보통 의미있는 값을 보고싶어한다. 

e.g. PhoneNumber 클래스면 실제 전화번호 값

⇒ 책에서는 주요 정보 모두를 반환하는게 좋다고는 하는데, 물론 보안적인걸 생각해야겠죠

### 의도를 명확히 하자

- 주석으로 상세히 설명하자.
- 포맷을 명시하기로 했으면, 문자열과 객체를 상호전환 할 수 있는 정적 팩토리나 생성자를 함께 제공한다.

```java
// 전화번호 사이에 붙임표(-)를 붙여서 반환한다.
@override
public String toString() {
  return String.format("%s-%s-%s", areaCode, prefix, lineNum);
}

public static PhoneNumber of(String s) {
    String[] split = s.split("-");
    PhoneNumber phoneNumber = 
				new PhoneNumber(split[0], split[1], split[2]);
        return phoneNumber;
}
```

### toString이 반환한 값에 포함된 정보를 얻을 수 있는 API를 제공하자

어차피 toString에 노출한 정보는 외부에 노출해도 된다는 뜻이니 getter를 만들어서 클라이언트가 파싱하게 하지말자.