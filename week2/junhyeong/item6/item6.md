# 불필요한 객체 생성을 피하라 - item6

### 객체 생성의 반복

```java
String s = new String("a");
String s = "a" // String pool 에서 관리되어 같은 문자열을 가리킨다.

Boolean b = Boolean(s) // deprecated Java 9
Boolean b = Boolean.valueOf(s)
```

자원 낭비를 줄이기 위해 새로운 인스턴스를 매번 만드는 대신 하나의 인스턴스를 사용하자.

### 생성 비용이 비싼 객체

```java
static boolean isRomanNumeral(String s) {
	return s.matches(regex...)
// 내부적으로 사용하는 Pattern 인스턴스는 생성 비용이 높다.
// https://github.com/java-squid/effective-java/issues/6#issuecomment-696519565
// 그냥 matches 메서드가 유한상태머신으로 구현되는데 이게 사이즈가 엄청 크다.
}

public class RomanNumerals {
	private static final Pattern ROMAN = Pattern.compile(regex...);

	static boolean isRomanNumeral(String s) {
		return ROMAN.matcher(s).matches();

	}
}
```

생성 비용이 비싼 인스턴스는재사용을 통해 성능을 개선한다.

### 오토 박싱

```java
private static long sum() {
	Long sum = 0L;
	for(long i=0; i<=Integer.MAX_VALUE; i++) {
		sum += i;
	}
	return sum;
}
```

Long 인스턴스가 약 2의 31승개가 만들어진다.

의도치 않은 오토박싱을 주의하자.

→ “이 예제는 좀 눈여겨 볼 만 한 것 같다. wrapper class 를 별 생각없이 쓰고 있었는데 연산시에 실수 하지 않도록 해야겠다.”

### 오해금지

<aside>
💡 객체 생성은 비싸니 피해야 한다 x
불필요한 객체 생성을 피하자. o

단순히 객체 생성을 피하려고 객체 풀을 만들지 말자.
데이터베이스 연결 같은 경우에는 생성 비용이 워낙 비싸서 재사용하는편이 낫지만, 일반적으로는 코드를 헷갈리게 하고 메모리 사용량을 늘려서 성능을 떨어뜨린다.

</aside>