# equals를 재정의하려거든 hashCode도 재정의하라 - item11

hashCode : 객체의 주소값을 변환하여 생성한 고유한 정수값

```java
PhoneNumber phoneNum= new User ("01012345678");
PhoneNumber phoneNum2= new User ("01012345678");

phoneNum.equals(phoneNum2); // true
 
Map<User, String> users = new HashMap<>();

users.put(user1, "ssar");
users.get(user2); // null ???

 
```

equals를 재정의해서 PhoneNumber 의 num 필드값이 같으면 같은 객체로 재정의 했다고 하자.

필드명이 “01012345678”로 같은 phoneNum2로 꺼내면 “01012345678”이 나와야 할 것 같지만, null이 반환된다.

이것은 hash 값을 사용하는 다른 Collection HashSet, HashTable 에서도 공통적으로 발생한다.

위 컬렉션에서는 equals()로 비교하기전에 , hasCode() 값으로 비교를 먼저하기 때문이다.

### Hash code 구현방법

- 최악의 방법

```java
@Override
public int hashCode() {
    return 1;
}
```

이러면 동치인 객체에 똑같은 해시코드를 반환해서 적합은 하나, hash 자료구조는 O(1) 의 시간을 기대하고 쓰는건데 O(n)이 되버린다.

→ 항상 같은 해시값을 반환하면 해시테이블에서 충돌 발생. 값 저장을 위해서 연결리스트를 n번 탐색해야한다.

해시 테이블 : 해시를 배열의 인덱스로 환산해서 데이터에 접근하는 자료구조

- Objects.hash

```java
@Override
public int hashCode() {
    return Objects.hash(name, price, quantity);
}
```

IDE 에서 자동으로 만들어주는 Objects.hash 메서드를 타고 들어가
Arrays.hashCode()를 사용하고있다.

```java
public static int hashCode(**Object a[]**) {
     if (a == null)
         return 0;

     int result = 1;

     for (Object element : a)
         result = 31 * result + (element == null ? 0 : element.hashCode());

     return result;
}
```

해시코드의 분포를 좋게 해주는 코드를 적용한걸 쓸 수 있다.

단, 배열이 만들어지고 기본 타입이 있으면 박싱과 언박싱이 들어가서, 성능에 민감하지 않은 상황에서 사용하라 한다.

- 전형적인 방법

```java
@Override
public int hashCode() {
    int result = areaCode;
    result = 31 * result + prefix;
    result = 31 * result + lineNum;
    return result;
}
```

고유한 값(지역코드, 접두사, 회선 번호)를 사용해서 해시코드의 충돌 가능성을 낮춘다.

31을 곱하는건 메르센 소수라 해시 코드의 분포를 균일하게 만드는데 도움이 되기 때문이라고 한다.

- hash 캐싱

```java
private int hashCode;

@Override
public int hashCode() {
  	int result = hashCode;
  	if (result == 0) {
      result = areaCode;
      result = 31 * result + prefix;
      result = 31 * result + lineNum;
      hashCode = result;
    }
  return result;
}
```

클래스가 불변이고 해시코드를 계산하는 비용이 크면

즉, 초기화 비용이 크면 지연초기화를 사용하는 방법을 사용한다.

단, 스레드 안정성을 고려하자. (final, synchronized…)