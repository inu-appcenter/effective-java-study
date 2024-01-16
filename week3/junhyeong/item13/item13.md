# clone 재정의는 주의해서 진행하라 - item13

clone() : 자신을 복사해서 새로운 인스턴스를 생성

cloneable 인터페이스를 구현해야 사용할 수 있고, Object에 선언되어있다.

⇒ 인터페이스에 메서드가 없고 예외를 던지려고 만든 독특한 방식

```java
public class Stack {

		private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_CAPACITY = 16;
    public Stack() {
        elements = new Object[DEFAULT_CAPACITY];
    }
 
    public Object pop() {
        if (size == 0) throw new EmptyStackException();
				Object result = elements[--size];
        elements[size] = null;
				return result;
    }

}
```

이게 같은 배열을 참조하는거라서, 버그가 일어나기 쉬운 구조다.

한쪽에서 elements 변경이 일어나면 다른쪽에도 영향을 주기 때문

```java
@Override
public Stack clone() {
	try {
		Stack result = (Stack) super.clone();
		result.elements = elements.clone();
		return rsult;
	} catch(CloneNotSupportedExceptin e) {
		throw new AssertionError();
	}
}
```

해결방법은 배열의 clone()을 재귀적으로 호출하는거다.

배열의 clone은 원본 배열과 똑같은 배열을 반환해서 권장하는 방법

→ 스택 내부 정보를 복사

다만, 이 방법은 elements 필드가 final이면 새로운 값을 할당할 수 없는 한계가 있다.

```java
private Entry[] buckets = ...;

@Override
public HashTable clone() {
	try {
		HashTable result = (HashTable) super.clone();
		result.buckets = buckets.clone();
		return result;
	}
}
```

또한, 위 처럼 자신만의 배열을 갖지만 원본이랑 동일한 연결 리스트를 참조하게 되면, 연결 리스트 까지 복제해야한다.

### 결론

배열이 아닌 것의 복제는. 복사생성자 or 복사팩토리로 해결한다.