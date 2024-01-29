# 클래스와 멤버의 접근 권한을 최소화하라 - item15

<aside>
💡 **잘 설계된 컴포넌트**
내부 데이터, 내부 구현 정보를 외부 컴포넌트로 부터 얼마나 잘 숨겼는가 (정보 은닉, 캡슐화)

</aside>

### 장점

- 개발 속도 : 여러 컴포넌트의 병렬 개발이 가능함
- 관리 비용 : 각 컴포넌트를 빨리 디버깅, 교체하는 비용이 낮음
- 성능 최적화 : 최적화할 컴포넌트를 정해 해당 컴포넌트만 최적화 가능
- 재사용성 : 외부에 거의 의존하지 않으면 다른 환경에서도 유용하게 쓰일 가능성이 큼
- 큰 시스템 제작 난이도를 낮춰줌 : 전체가 완성되지 않더라도 개별 컴포넌트의 테스트가 가능

캡슐화의 핵심 : 접근 제한자. 모든 클래스와 멤버의 접근성을 최대한 좁히자.

### 접근 수준

- private : 멤버를 선언한 톱 레벨 클래스에서만 접근할 수 있다.
- package-private : 멤버가 소속된 패키지 안의 모든 클래스에서 접근할 수 있다.
- protected : package-private의 접근 범위를 포함하며 이 멤버를 선언한 클래스의 하위 클래스에서도 접근할 수 있다.
- public : 모든 곳에서 접근할 수 있다.

클래스의 공개 API를 세심히 설계한 후, 그 외의 모든 멤버는 private으로 만들자.그런 다음 오직 같은 패키지의 다른 클래스가 접근해야 하는 멤버에 한하여 (private 제한자를 제거해) package-private으로 풀어주자.

단, 멤버 접근성을 좁히지 못하게 방해하는 제약이 있다.

상위 클래스의 메서드를 재정의할 때는 그 접근 수준을 상위 클래스에서보다 좁게 설정할 수 없다는 것. (리스코프 치환원칙)

### public 클래스의 인스턴스 필드는 되도록 public이 아니어야 한다. (상수용 public static final 필드 제외)

```java
public class Speaker {
    private int volume;

    void volumeUp() {
        if (volume >= 100) {
            System.out.println("음량을 증가할 수 없습니다. 최대 음량입니다.");
        } else {
            volume += 10;
            System.out.println("음량을 10 증가합니다.");
        }
    }
}
```

a 개발자는 volumn을 100까지 제한하는걸로 기대했지만, volumn이 public 필드라면, 다른 개발자가 이를 보고 자유롭게 수정해도 되는줄 알게될 수 있다.

또한, public 가변 필드는 스레드 안전하지 않다.

“필드가 final이면서 불변 객체를 참조하더라도 문제는 여전히 남는다. 내부 구현을 바꾸고 싶어도 public 필드를 없애는 방식으로는 리팩터링할 수 없게 된다.”

⇒ ???

### public static final 배열 필드를 두거나 필드를 반환하는 접근자 메서드를 제공하면 안 된다.

```java
public class Main {
	public static final char[] VALUES = {'a','b','c'};
	
}

Main.VALUES[0] = 'z';
```

배열 내부의 값은 변경할 수 있다.

VALUES에는 실제 값이 아니라 값의 주소를 가리키는 참조값이 있는것이기 때문에, 참조값이 가리키는 배열값 수정은 상관이 없다.

**해결방법 1**

```java
private static final Character[] VALUES = {'a', 'b', 'c'};
public static final List<Character> list = Collections.unmodifiableList(Arrays.asList(VALUES));
```

public 배열을 private으로 만들고 public 불변 리스트를 추가한다.

**해결방법 2**

```java
private static final Character[] VALUES = {'a', 'b', 'c'};
public static final Character[] values() {
	return VALUES.clone(); // 방어적복사
}
```

public 배열을 private으로 만들고 복사본을 반환해주는 public 메서드를 추가

### 자바 9의 모듈

```java
module com.example.a{
    exports user.domain; // 외부에 노출하고 싶은 패키지
}
```

```java
module com.example.b{
    requires com.example.a;    // 사용할 패키지
}
```

module-info.java를 통해

서로 다른 프로젝트 a, b에 대해 a에서 선언한 클래스를 b에서 불러와 사용할수 있다.