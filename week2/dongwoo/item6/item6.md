## 아이템6 불필요한 객체 생성을 피해라

객체를 매번 생성하기보다 객체 하나를 재사용하는 편이 나을때가 많다고 한다

```java
String str1 = new String("hello");
String str2 = "hello";
```

str1은 실행될때마다 String 힙 영역에 인스턴스를 새로 만든다 만약에 반복문으로 100개가 생성되면 인스턴스가 100개 생성된다

하지만 str2는 새로운 인스턴스가 아닌 하나의 String인스턴스를 사용한다 또한 가상 머신 안에서 이와 똑같은 문자열 리터럴을 사용하는 모든 코드가 같은 객체를 재사용함이 보장된다 → heap영역의 String Contant Pool에 저장되어서 인스턴스가 재사용됨

또한 정적 팩터리 메서드도 불필요한 객체 생성을 피할 수 있다

```java
public final class Boolean implements java.io.Serializable, Comparable<Boolean> {

    public static final Boolean TRUE = new Boolean(true);
    public static final Boolean FALSE = new Boolean(false);

    // ...
    public static Boolean valueOf(String s) {
        return parseBoolean(s) ? TRUE : FALSE;
    }
}
```

Boolean도 내부 까보면 정적 팩터리 메서드를 사용해서 미리 만든 True, False를 반환한다

또한 객체 생성이 비싼 경우에 캐싱을 통해 객체 생성을 방지할 수 있다

```java
static boolean isRomanNumeral(String s) {
    return s.matches("^(?=.)M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
}
```

교재에 나온 isRomanNumeral인데 s.matches가 정규표현식과 매치되는지 확인하는건데 성능이 안좋아서 중요한 상황에서 반복적으로 사용하기에 적절하지 않다 왜냐면 이 메서드가 내부에서 Pattern인스턴스를 만들어서 사용하고 그 이스턴스는 입력받는 정규표현식에 해당하는 유한 상태 머신(finite state machine)을 만들기 때문에 인스턴스 생성 비용이 높다고 한다

그러면 이거를 개선하려면 패턴 인스턴스 클래스를 초기화하는 과정에서 객체를 생성해서 캐싱해두고 나중에 isRomanNumeral메서드가 호출될 때마다 이 인스턴스를 재사용하면 된다

```java
public class RomanNumber {
    private static final Pattern ROMAN = Pattern.compile("^(?=.)M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");

    static boolean isRomanNumeral(String str) {
        return ROMAN.matcher(str).matches();
	}
}
```

이렇게하면 성능을 상당히 끌어올릴 수 있다

객체가 불변이라면 재사용해도 안전함이 명백하다 Map인터페이스에 KeySet메서드도 Boolean처럼 Set인스턴스는 계속 같은걸로 반환한다

불필요한 객체를 만들어내는 또 다른 예시는 오토박싱을 들 수 있다

오토박싱은 기본 타입과 박싱된 기본 타입을 자동으로 변환해주는 기술이다.

근데 이거 잘못쓰면 불필요한 메모리 할당과 재사용을 반복해서 성능이 느려질 수 있다

```
void add() {
    Long sum = 0L;
    for(long i = 0; i <= Integer.MAX_VALUE; i++) {
        sum += i;
    }
}
```

박싱 타입이 필요한 경우가 아니라면 기본 타입을 사용하고 오토박싱이 없게 주의하자

마지막으로 객체 생성은 비싸니 피해야 한다로 오해하면 안됨, JVM에서는 별 다른 일을 하지 안흔ㄴ 객체를 생성하고 회수하는 일이 크게 부담되지않는다 하지만 아주 무거운 객체가 아닌 다음에야 단순히 객체 생성을 피하고자 객체 풀을 만들지 말자 요즘 JVM의 가비지 컬렉터는 상당히 잘 최적화되어서 가벼운 객체용을 다룰 때는 직접 만든 객체 풀보다 훨씬 빠르다