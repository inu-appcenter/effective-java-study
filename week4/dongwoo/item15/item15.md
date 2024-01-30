## 아이템 15: 클래스와 멤버의 접근 권한을 최소화하라

잘 설계된 컴포넌트는 내부 구현을 완벽히 숨겨서 **정보 은닉**, **캡슐화**의 개념으로 구현과 api를 깔끔히 분리하고 api를 통해서만 다른 컴포넌트와 소통하며 내부 방식에는 전혀 개의치 않는다

정보 은닉의 장점은 개발, 테스트, 최적화, 적용, 분석, 수정을 개별적으로 할 수 있게 해준다

- 여러 컴포넌트를 병렬로 개발 가능 → 개발 속도 향상
- 각 컴포넌트를 빨리 파악하고 디버깅, 다른 컴포넌트로 교체하는 부담 적음
- 다른 컴포넌트에 영향 주지 않고 해당 컴포넌트만 최적화 가능
- 잘 분리된 컴포넌트는 어디서든 독립적으로 사용할 수 있음
- 시스템 전체가 완성되지 않더라도 개별 컴포넌트의 동작을 테스트 가능

이렇게 장점이 많은 정보은닉을 잘 하려면 어떻게 해야 할까? **모든 클래스와 멤버의 접근 수준을 가능한 낮은 수준을 부여해야 한다.**

패키지 외부에서 쓸 이유가 없으면 Private으로 선언하자 즉 클라이언트에 아무런 피해 없이 내부 구현을 언제든지 수정 할 수 있다 하지만 Public으로 선언하면 api가 되므로 하위 호환을 위해 영원히 관리해줘야만 한다

public일 필요가 없는 톱 레벨(가장 바깥)은 반드시 private로 범위를 좁혀라

```java
public class Human {
		private String name;
}
public class Job {
		private String category;
}

```

Job은 Human만이 가질 수 있다 동물은 직업이 없다 그러므로 Job은 Human에서만 쓰이는 클래스다

```java
public class Human{
    private String name;
    
    private static class Job
        private String category;
    }
}
```

한 클래스에서만 사용하는 package-private 톱레벨 클래스나 인터페이스는 이를 사용하는 클래스 안에 private static으로 중첩시켜보자 톱 레벨로 두면 같은 패키지의 모든 클래스가 접근할 수 있지만 private static으로 중첩시키면 바깥 클래스 하나에서만 접근할 수 있다

리스코프 치환 원칙에 따라 상위 클래스의 메서드를 재정의할 때 그 접근 수준을 상위 클래스보다 좁게 설정하면 컴파일 오류가 발생한다. 클래스가 인터페이스를 구현한 건 이 규칙의 특별한 예시이며 클래스는 모든 인터페이스가 정의한 모든 메서드를 public으로 선언해야 한다

내가 가장 고민했던 거다 → 단지 코드를 테스트하려는 목적으로 클래스, 인터페이스, 멤버의 접근 범위를 넓히려 할 때가 있다. 적당한 수준까진 넓혀도 괜찮다(?) 예를 들어 Public클래스의 private 멤버를 package-private으로 풀어주는건 허용할 수 있다 → 와 이거 대박, 하지만 그 이상은 안된다

public 클래스의 인스턴스 필드는 되도록 public이 아니어야 한다 필드가 가변 객체를 참조하거나 Final이 아닌 인스턴스 필드를 public으로 선언하면 그 필드에 담을 수 있는 값을 제한할 힘을 잃게 된다 그리고 그 필드에 관련된 모든 것은 불변식을 보장할 수 없게 된다→ 누가 바꿀지 모르니께 그러면 final public은 되겠네? ArrayList, Map 이런것들은 어칼건데

여기에 더해 필드가 수정될때 다른 작업을 할 수 없게 되므로 public가변 필드를 갖는 클래스는 일반적으로 스레드 안전하지 않다 이러한 개념은 정적 필드에서도 마찬가지다

그리고 아예 클래스에 public static final 배열 필드를 두거나 이 필드를 반환하는 접근자 메서드 자체를 제공해서는 안된다 클라이언트가 해당 public으로 private에 접근하여 수정이 가능해진다

```java
public class Human {
    private final List<Job> history;

    public List<Job> getHistory() {
        return history;
    }
}

```

이건 불변 객체일까? private final인데?? 놉 get해서 안에 내용을 변환할 수 있기 때문이다

필드가 private final이라고 무조건 불변은 아니다 원시 타입의 필드는 불변이지만 참조 타입의 필드는 가변일 수 있다

```java
public class Human {
    private final List<Job> history;

    public Human(List<Job> history) {
        this.history = List.copyOf(history);
    }

    public List<Job> getHistory() {
        return Collection.unmodifiedList(history);
    }
}
```

이렇게 하면 방어적 복사가 가능하다

자바9 부터 모듈 시스템이라는 개념이 도입되면서 두 가지 암묵적 접근 수준이 추가되었다 protected, public 멤버라도 해당 패키지를 Import하지 못하게 setting.gradle에서 막아놓으면 모듈 외부에서 접근할 수 없다

결론은 접근성은 가능한 최소로 하자 꼭 필요한 것만 골라 최소한의 public을 만들어주자, public클래스는 상수용 public static final 필드 외에는 어떠한 public필드도 가져선 안된다 public static final 필드가 참조하는 객체가 불변인지 확인하라