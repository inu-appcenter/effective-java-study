## 아이템7 다 쓴 객체 참조를 해제하라

자바처럼 가비지 컬렉터가 언어를  쓴다고 메모리 관리에 신경을 쓰지 않으면 안된다

```java
public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        return elements[--size];
    }

    /**
     * 원소를 위한 공간을 적어도 하나 이상 확보한다.
     * 배열 크기를 늘려야 할 때마다 대략 두 배씩 늘린다.
     */
    private void ensureCapacity() {
        if (elements.length == size)
            elements = Arrays.copyOf(elements, 2 * size + 1);
    }
}
```

교재의 코드에서 메모리 누수는 스택이 커졌다가 줄어들때 겍체들을 가비지 컬렉터가 회수하지 않는다 만약에 이 프로그램을 오래 실행하다 보면 가비지 컬렉션 활동과 메모리 사용량이 늘어나 결국 성능이 저하되고 극한으로 억까해보면 OutOfMemoryError를 일으켜 프로그램이 예기치 않게 종료되기도 한다. 그러면 스택에서 다 객체들을 가비지 컬렉터가 회수하지 않으면 이 스택의 그 객체들이 다 쓴 참조를 여전히 가지고 있기 때문에 다 해법은 간단하다 해당 참조를 다 썻을때 null처리(참조 해제)하면 된다. 예시에서 해결해보면

```java
public Object pop() {
		if (size == 0) throw new exmptyStackExeption();
		Object result = element[--size];
				element[size] = null;  //다 쓴 참조 해제
		return result;
}
```

이렇게 하면 된다 null처리해서 GC에게 해당객체를 더는 쓰지 않을 것임을 알려야한다

또한 캐시 역시 메모리 누수를 일으키는 주범이다 객체 참조를 캐시에 넣고 나서 이 사실을 잊은 채 그 객체를 다 쓴 뒤로도 한참을 그냥 놔두는 일을 자주 접할 수 있다 WeekhaskMap을 사용해서 캐시를 만들면 다 쓴 엔트리는 자동으로 제거된다

```java
Object key = new Object();
Object value = new Object();

cache.put(key, value);
```

이렇게 했는데 key가 사라지면 의미없는 캐시라서 그렇다