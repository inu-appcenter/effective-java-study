# 다 쓴 객체 참조를 해제하라 - item7

가비지 컬렉터가 알아서 해주니 메모리 관리를 신경 쓰지 않아도 된다고 오해할 수 있다.

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
        return elements[--size];
    }

}
```

pop메서드에서 메모리 누수가 일어난다.

pop을 통해 스택에서 꺼냈지만, 배열의 인덱스만 감소시킨거니깐 기존 인덱스에 객체 참조값들을 가지고 있다. 이렇게 스택이 다 쓴 참조를 가지고 있을 경우에는 가비지 컬렉터가 처리하지 못한다.

단순히 하나로 보이지만, 이 객체가 참조하는 모든 객체들을 회수하지 못해서 성능에 악영향을 준다.

⇒ 다 썼을 때 null 처리 (참조 해제)

모든 객체를 쓰자마자 null 처리하면 지저분할 뿐이다. 다 쓴 참조를 해제하는 가장 좋은 방법은 참조를 담은 변수를 스코프 밖으로 밀어내는 것

⇒ 변수의 생존범위를 파악해서 스코프를 좁혀서 선언하면 가비지컬렉터가 자동으로 처리해줄거니깐

### Null 처리는 언제?

<aside>
💡 위 Stack 클래스에서 메모리 누수에 취약한 이유는 자신이 자기 메모리를 직접 관리하기 때문.
가비지 컬렉터는 외부에서 뭐가 비활성 됐는지 모른다.
자기 메모리를 직접 관리하는 클래스를 주시하자.

캐시, 리스너(콜백) : WeakHashMap

</aside>