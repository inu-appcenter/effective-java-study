# 이왕이면 제네릭 메서드로 만들라 - item30

메서드도 제네릭으로 만들 수 있다. 매개변수화 타입을 받는 정적 유틸리티 메서드가 보통 제네릭이다.

e.g. Collections ( binaraySearch, sort )

```java
public static <T>
    int binarySearch(List<? extends Comparable<? super T>> list, T key) {
        if (list instanceof RandomAccess || list.size()<BINARYSEARCH_THRESHOLD)
            return Collections.indexedBinarySearch(list, key);
        else
            return Collections.iteratorBinarySearch(list, key);
    }
```

### 작성방법

- 문제있는 메서드

```java
public static Set union(Set s1, Set s2){
    Set result = new HashSet(s1);
    result.addAll(s2);
    return result;
}
```

위 메서드는 컴파일은 되지만 타입 안전하지 않아 경고가 발생한다.

Set이 사용할 원소 타입을 타입 매개변수로 명시하고, 메서드 안에서도 이 타입 매개변수만 사용하게 수정하면된다.

⇒ 타입 매개변수 목록은 메서드의 제한자와 반환 타입 사이에 넣는다.

타입 매개변수 목록 : <E>

반환 타입 : Set<E>

```java
public static <E> Set<E> union(Set<E> s1, Set<E> s2){
    Set<E> result = new HashSet<>(s1);
    result.addAll(s2);
    return result;
}
```

위 메서드는 집합 3개의 타입이 모두 같아야한다. 이는 한정적 와일드카드 타입(item31) 사용하면 유연하게 개선 가능하다.

### 제네릭 싱글톤 패턴

불변 객체를 여러 타입으로 활용할 수 있게 만들어야 할 때가 있다.

제네릭은 런타임에 타입 정보가 소거되므로 하나의 객체를 어떤 타입으로든 매개변수화할 수 있다.

하지만, 이렇게 하려면 요청한 타입 매개변수에 맞게 매번 그 객체의 타입을 바꿔주는 정적 팩터리를 만들어야 한다.

이 패턴을 제네릭 싱글톤 팩토리라고 한다.

아래와 같은 함수 객체나 Collections.emptySet 같은 컬렉션용으로 사용한다.

```java
public static <T> Comparator<T> reverseOrder() {
   return (Comparator<T>) ReverseComparator.REVERSE_ORDER;
}
```

한마디로 제네릭으로 만들어진 팩토리 메서드. 불변 객체를 타입에 맞게 여러번 재사용 가능하다.

### 항등함수

항등함수는 입력값을 받으면 동일한 출력을 주는 함수다. 그러므로 함수가 내부적으로 어떤 상태나 메모리를 유지하지 않는다.

그러므로 요청할 때마다 새로 생성하는 것은 낭비다.

제네릭이 소거 방식을 사용한 덕분에 타입별로 만들 필요가 없다.

```java
private static UnaryOperator<Object> IDENTITY_FN = (t) -> t;
// Object 타입을 입력으로 받아들이고 그대로 반환하는 UnaryOperator 정의
    
@SuppressWarnings("unchecked")
public static <T> UnaryOperator<T> identityFunction(){
    return (UnaryOperator<T>) IDENTITY_FN;
}
```

- 제네릭 싱글톤 팩터리로 항등함수를 작성한 예시

IDENTITY_FN 를 UnaryOperator<T>로 형변환하면 비검사 형변환 경고가 발생한다.

T가 어떤 타입이든 UnaryOperator<Object> != UnaryOperator<T> 이기 때문이다.

그러나, 항등 함수가 입력값을 그대로 반환하는 함수라서 타입이 안전하다는걸 알고있다.

따라서 애너테이션으로 경고를 가린 모습이다.

```java
// 30-5 제네릭 싱글턴을 사용하는 예
public static void main(String[] args) {
    String[] strings = { "삼베", "대마", "나일론" };
    UnaryOperator<String> sameString = identityFunction();
    for (String s : strings)
        System.out.println(sameString.apply(s));

    Number[] numbers = { 1, 2.0, 3L };
    UnaryOperator<Number> sameNumber = identityFunction();
    for (Number n : numbers)
        System.out.println(sameNumber.apply(n));
}
```

### 재귀적 타입 한정

자기 자신이 들어간 표현식을 사용하여 타입 매개변수의 허용범위를 한정할 수 있다.

e.g. Comparable 인터페이스

```java
public interface Comparable<T> {
  public int compareTo(T o);
}
```

Comparable을 구현한 원소의 컬렉션을 입력받는 메서드들은 주로 그 원소들을 정렬, 검색, 최대최소를 구하는 식으로 사용된다.

이 기능을 수행하려면 컬렉션에 담긴 모든 원소가 상호 비교될 수 있어야한다.

다음은 이 제약을 코드로 표현한 모습이다.

```java
public static <E extends Comparable<E>> E max(Comparable<E> c);
```

타입 한정 타입인  <E extends Comparable<E>>는

"모든 타입 E는 자신과 비교할 수 있다" 라고 읽을 수 있다.