# 제네릭과 가변인수를 함께 쓸 때는 신중하라 - item32

가변인수 메서드를 호출하면 가변인수를 담기 위한 배열이 자동으로 하나 만들어진다.

클라이언트에 이 배열이 노출되어 varargs 매개변수(int…numbers)에 제네릭이나 매개변수화 타입이 포함되면 알기 어려운 컴파일 경고가 발생한다.

```java
warning: [unchecked] Possible heap pollution from 
    parameterized vararg type List<String>
```

매개변수화 타입의 변수가 타입이 다른 객체를 참조하면 힙 오염이 발생한다. 예시를 보자.

힙 오염 : 제네릭 타입 시스템의 타입 안전성을 해칠 수 있는 상황

```java
static void dangerous(List<String>... stringLists) {
    List<Integer> intList = List.of(42);
    Object[] objects = stringLists;
    objects[0] = intList; // 힙 오염 발생
    String s = stringLists[0].get(0); // ClassCastException
}
```

보이지 않는곳에서 형변환이 이루어저 예외를 던지기 때문이다.

프로그래머가 제네릭 varargs 매개변수를 받는 메서드를 선언할 수 있게 한 이유(제네릭 배열을 허용 안하면서)??

위 코드가 경고로 끝나는이유??

→ 실무에서 매우 유용하기 때문 e.g. Arrays.asList(T…a) , Collections.addAll(Collection<? super T>)

사용자는 경고를 호출하는곳마다 @SuppressWarnings(”unchecked”)로 경고를 숨기거나 @SafeVarargs로 경고를 숨길 수 있다.

### 메서드가 안전한지 어떻게 확신?

메서드가 이 배열에 아무것도 저장하지 않고(그 매개변수들을 덮어쓰지 않고) 그 배열의 참조가 밖으로 노출되지 않는다면(신뢰할 수 없는 코드가 배열에 접근할 수 없다면)타입 안전하다.

**안전하지 않은 예 - 1**

```java
static <T> T[] toArray(T... args) {
    return args;
}
```

이 메서드가 반환하는 배열의 타입은 메서드에 인수를 넘기는 컴파일타임에 결정되므로 컴파일러에게 충분한 정보를 주어주지 않아서 위험하다.

- dangerous 메서드처럼 Object[] 배열을 반환해서 T [] 배열로 형변환하는게 안전하지 않을 수 있음
- 메서드 내에서 가변인자가 어떻게 될지 모름

**안전하지 않은 예 - 2**

```java
static <T> T[] pickTwo(T a, T b, T c) {
    switch(ThreadLocalRandom.current().nextInt(3)) {
      case 0: return toArray(a, b);
      case 1: return toArray(a, b);
      case 2: return toArray(a, b);
    }
    throw new AssertionError(); // 도달할 수 없다. 
}

public static void main(String[] args) {
    String[] attributes = pickTwo("좋은", "빠른", "저렴한");
}
```

다음은 T 타입 인수 3개를 받아 무작위로 담은 배열을 반환하는 메서드다.

문제없어 보이지만 ClassCastException이 발생한다.

toArray에서는 Object[] 배열이 반환되는데 String[]에 담으려고 하기 때문이다.

**올바른 예**

```java
@SafeVarargs
static <T> List<T> flatten(List<? extends T>... lists) {
    List<T> result = new ArrayList<>();
    for (List<? extends T> list : lists)
    result.addAll(list);
    return result;
}
```

위 메서드는 임의 개수의 리스트를 인수로 받아, 받은 순서대로 그 안의 모든 원소를 하나의 리스트로 옮겨 담아 반환한다.