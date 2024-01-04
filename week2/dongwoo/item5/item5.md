## 아이템 5 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라

많은 클래스는 하나 이상의 자원에 의존한다 UserService만 봐도 많은 Repository를 의존한다

유저를 저장할때 비밀번호를 인코딩하는 클래스를 의존하게 되는데 이럴때 정적 유틸리티 클래스나 싱클턴으로 구현할 수 있다 하지만 사용하는 자원에 따라 동작이 달라지는 클래스에는 정적 유틸리티 클래스나 싱글톤 방식이 적합하지 않다

```java
public class UserService {

    private final PasswordEncoder passwordEncoder;

		public UserService(PasswordEncoder passwordEncoder) {
				this.passwordEncoder = passwordEncoder
		}

    public void registerUser(SignUpInfo signUpInfo) {
				...
				password = passwordEncoder.encode(signUpInfo.getPassword());
				...
    }
}
```

이런식으로 의존성을 주입할 수 있다 자원이 추가되도 여러 자원을 더 추가할 수 있다 또한 이렇게하면 passwordEncoder는 불변을 보장하며 여러 클라이언트가 의존 객체들을 안심하고 공유할 수 있다

장점으로는 객체 주입이 유연해지고 테스트를 용이하게 해준다

```java
class Drink {
    private Coffee coffee;

    public Drink() {
        this.coffee = new Coffee();
    }
}
```

이렇게 내부에 new 로 커피를 새로 만들면 의존성 주입이 일어나지 않은 것이다 크랑이언트는 서비스에 의존하고 서비스는 클라이언트에 강결합되어있기 때문에 다른 커피를 마시고 싶을 경우 Drink내부의 코드를 변경해야 한다

```java
class Drink {
    private Coffee coffee;

    public Drink(Coffee coffee) {
        this.coffee = coffee;
    }
}

class Coffee {}

class Espresso extends Coffee{}

class Latte extends Coffee {}
```

이렇게 생성자를 통해 의존성 주입을 한 경우 Drink는 Coffee를 외부에서 넙겨받는다 그럼 상속받은 에스프레소랑 라때 클래스도 Drink에 쉽게 전달될 수 있다

이렇게 재사용성, 유연성, 테스트 용이 등 결국엔 유지보수 측면에서 강한 장점을 가진다

하지만 단점으로는

- 책임이 분리되어있어 클래스 수를 늘리면 복잡성이 증가한다 → 스파게티 코드
- 주입된 객체들에 코드 추적이 어려움
- 프레임워크 빌드시간이 늘어나며 프레임워크에대한 의존도가 높아진다