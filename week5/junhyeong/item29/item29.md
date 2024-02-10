# 이왕이면 제네릭 타입으로 만들라 - item29

### **Object 기반 스택 - 제네릭이 절실한 강력 후보!**

```java
public class StackBasedObject {
    private **Object**[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public StackBasedObject() {
        elements = new **Object**[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(**Object** e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public **Object** pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        **Object** result = elements[--size];
        elements[size] = null; // 다 쓴 참조 해제
        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
```

지금 상태에서는 클라이언트가 스택에서 꺼낸 객체를 형변환 할 때 런타임 오류가 날 위험이 있다.

제네릭 클래스로 만들어보자.

### 클래스 선언에 타입 매개변수를 추가하기

```java
public class StackGeneric<E> {
    private **E**[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public StackGeneric() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY]; // 컴파일 에러 발생
    }

    public void push(**E** e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public **E** pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        **E** result = elements[--size];
        elements[size] = null; // 다 쓴 참조 해제
        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
```

item 28에서의 예시와 같이 제네릭으로 배열을 만들 수 없어서 오류가 나는거다.

(E[]) new Object[…] 이렇게 형변환 해도 타입 안전하지 않다는걸 이제는 알거다.

- 첫번째방법

그러나, 이 프로그램을 살펴보면 elements 배열이 private 필드에 저장되고, 클라이언트에게 반환되거나 다른 메서드에 전달되지 않으니깐, push 메서드를 통해 저장되는 배열의 원소 타입은 항상 E라는 것이 보증된다.

따라서 비검사 형변환은 확실히 안전하니 @SuppressWarnings 애너테이션으로 해당 경고를 숨긴다.(item27)

- 두번째방법

```java
public class StackGeneric<E> {
    private Object[] elements; // Obejct[] 타입이다!
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public StackGeneric() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public E pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }

        @SuppressWarnings("unchecked") // push에서 E 타입만 허용하므로 이 형변환은 안전하다.
        E result = (E) elements[--size];

        elements[size] = null; // 다 쓴 참조 해제
        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
```

배열을 그대로 두고 pop 메서드 전체에서 숨기는게 아니라 비검사 형변환을 수행하는 할당문에서만 숨길 수 있다.

두 번째 방식은 배열에서 원소를 읽을 때마다 형변환을 해줘야하지만

첫 번째 방식은 가독성이 좋고 코드가 짧아서 현업에서 선호된다.

그러나 힙 오염(item32)가 걱정되는 프로그래머는 두 번째 방식을 고수하기도 한다.