## 아이템 18 상속보다는 컴포지션을 사용하라

상속은 코드를 재사용하기 좋지만 잘못 사용하면 오류를 내기 쉽다 부모, 자식 클래스가 하나이ㅡ 패키지 안에서 상속하고 문서화도 잘 되었으면 안전하지만 패키치 경계를 넘어서면 위험하다 또한 상속은 캡슐화를 깨트린다(상위 클래스의 구현이 하위 클래스에 노출되기 떄문에) 또한 상위 클래스의 릴리즈마다 내부 구현이 달라지고 하위 클래스는 가만히 있는데 오작동할 수 있다

```java
public class Car {
    private Integer cc;

    public Car(Integer cc) {
        this.cc = cc;
    }
}
public class Suv extends Car {
    private String name;

    public Suv(String cc, String name) {
        super(cc);
        this.name = name;
    }
    
}
```

이렇게 Car에는 배기량이 있고 상속할때 이름을 추가한다 하면 지금은 잘 돌아가지만 만약에 자동차들의 배기량 스펙이 단순히 숫자를 넘어서서 문자열로 해야 한다면

```java
public class Car {
    private String cc;

    public Car(String cc) {
        this.cc = cc;
    }
}
public class Suv extends Car {
    private String name;

    public Suv(String cc, String name) {
        super(cc);
        this.name = name;
    }
    
}
```

이렇게 cc를 String으로 바꿨는데 Suv클래스는 아무것도 안해도 컴파일 에러가 난다 이렇게 하위 클래스가 상위 클래스에 강하게 결합하는 구조를 띄기 때문에 유연하게 대처하기 어렵다

해결방안→ 상속보다는 컴포지션을 사용하자

```java
public class Suv {
    private String name;
    private Car car;

    public Suv(String name, Car car) {
        this.name = name;
        this.car = car;
    }
}
```

그냥 Suv는 멤버변수로 Car클래스를 갖는다 이게 컴포지션이다 Suv에서 Car클래스를 사용하고 싶으면 Car클래스의 메서드를 호출하는 방식으로 사용하면 된다 그럼 Car클래스가 변하더라도 Suv는 영향을 받지 않는다

상속은 하위 클래스가 상위 클래스의 하위 타입인 상황에서만 쓰여야한다 B가 A의 is-a관계여야만 A를 상속해야 한다 Suv is a Car 이러면 상속해야 한다 예시를 잘못들었다 그냥 개념만 말하고자 만약에 is-a관계가 아니다 라면 A를 private 인스턴스로 두고 A와는 다른 api를 제공해야 한다 필수 구성 요소가 아니라 구현하는 방법중 하나기 떄문에 상속은 코드 재사용이 아니라 확장이다