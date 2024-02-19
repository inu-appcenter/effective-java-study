# 한정적 와일드카드를 사용해 API 유연성을 높이라 - item31

item28 복습 : 매개변수화 타입은 불공변이다. 즉, List<String>은 List<Object>의 하위 타입도 상위타입도 아니다.

List<Object>에는 어떤 객체든 넣을 수 있지만 List<String>에는 문자열만 넣을 수 있다.

즉, List<String>은 List<Object>가 하는 일을 제대로 수행하지 못하니 하위 타입이 될 수 없다. (리스코프 치환 원칙 위반)

하지만 때론 불공변 방식보다 유연한 무언가가 필요하다.

```java
public class Stack<E> {
    public Stack();
    public void push(E e);
    public E pop();
    public boolean isEmpty();
    public void pushAll(Iterable<E> src) {
    for (E e : src)
        push(e);
    }
}
```

pushAll 메서드는 깨끗이 컴파일 되지만, 완벽하진 않다

```java
Strack<Number> numberStack = new Stack<>();
Iterable<Integer> intergers = ...;
numberStack.pushAll(integers); // error
```

Integer는 Number의 하위 타입이니 잘 동작해야 할 것 같은데, 매개변수화 타입이 불공변이기 때문에 오류가 발생한다.

자바는 이런 상황에 대처할 수 있는 **한정적 와일드카드 타입**이라는 특별한 매개변수화 타입을 지원한다. pushAll의 입력 매개변수 타입은 ‘E의 Iterable이 아니라 'E의 하위 타입의 Iterable'이어야 하며, 

와일드카드 타입 **Iterable<? extends E>**가 정확히 이런 뜻이다.

그럼 언제 어떤 와일드카드 타입을 쓰는게 좋을까?

<aside>
💡 PECS : producer-extends, consumer-super

</aside>

즉, 매개변수화 타입 T가 생산자라면 <? extends T>를 사용하고, 소비자라면 

<? super T>를 사용하라.

Stack에서는, 

pushAll의 src 매개변수는 Stack이 사용할 E 인스턴스를 생산하므로

<? extends T>

popAll의 매개변수는 Statck으로부터 E 인스턴스를 소비하므로 <? super T>

### 어떤 선언이 나을까?

```java
public static <E> void swap(List<E> list, int i, int j);
public static void swap(List<?> list, int i, int j);
```

타입 매개변수와 와일드카드에는 공통되는 부분이 있어서, 메서드를 정의할 때 어느 것을 사용해도 괜찮을 때가 있다.

public API 라면 간단한 두 번째가 낫다.

### 메서드 선언에 타입 매개변수가 한 번만 나오면 와일드카드로 대체하라

이때 비한정적 타입 매개변수라면 비한정적 와일드카드로 바꾸고, 한정적 타입 매개변수라면 한정적 와일드 카드로 바꾸면 된다.

<T> → <?>

<E extends Number> → <? extends Number>

하지만 아래 코드를 컴파일하면 도움되지 않는 오류 메시지가 발생한다.

```java
public static void swap(List<?> list, int i, int j) {
	list.set(i, list.set(j, list.get(i)));
}
```

꺼낸값을 다시 넣을때, 실제 타입을 알 수 없으니깐. 형변환이나 로 타입을 사용하지 않고 해결할 수 있다.

```java
public static void swap(List<?> list, int i, int j) {
    swapHelper(i, list.set(j, list.get(i));
}
public static <E> void swapHelper(List<E> list, int i, int j) {
    list.set(i, list.set(j, list.get(i));
}
```

와일드카드 타입의 실제 타입을 알려주는 도우미 메서드를 따로 작성하는 방법이다.