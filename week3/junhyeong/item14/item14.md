# Comparable을 구현할지 고려하라

```java
public interface Comparable<T> {
    public int compareTo(T o);
}
```

Comparable 인터페이스의 유일한 메서드 compareTo는 

Object의 eqauls의 동치성 비교를 넘어 순서를 비교할 수 있다.

### 규약

<aside>
💡 객체가 매개변수로 받은 객체보다 작으면 음의정수, 같으면 0, 크면 양의정수를 반환한다.

</aside>

- x.compareTo(y) == -y.compareTo(x)
- 추이성 , x.compareTo(y)  > 0 && y.compareTo(z) ⇒ x.compareTo(z)
- (x.compareTo(y)  == 0) == x.equals(y)
    
    → 이 권고는 필수는 아니지만 꼭 지키는게 좋다. 
    
    ⇒ e.g. 예외 ) String 클래스의 compareTo 메서드로 내용비교할 때, compareTo 메서드로 정렬 순서를 결정할 때
    

```java
		BigDecimal num1 = new BigDecimal("1.0");
    BigDecimal num2 = new BigDecimal("1.00");

    Set<BigDecimal> set1 = new HashSet<>(); // equals() 비교
		Set<BigDecimal> set2 = new TreeSet<>(); // compareTo() 비교

		set.add(...)

		set1 // 2
		set2 // 1
```

정렬된 컬렉션(e.g. TreeSet, TreeMap)은 동치성을 비교할 때 compareTo를 사용하기 때문에 마지막 규약을 지키는게 좋다.

### 메서드 작성 요령

- 제네릭 인터페이스라서 입력 인수 타입을 확인하거나 형변환 필요 x

→ 잘못된 인수는 컴파일 자체가 안돼서

- 정수 기본 타입 필드 비교시, 박싱 기본 타입 클래스의 정적메서드 compare을 사용하라

```java
// Integer.compare
public static int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }
```

→ compareTo 메서드에서 <, > 관계 연산자는 null 관련 오류를 일으킬 수 있음 

- 필드가 여러개면 핵심적인 필드부터 비교

```java
public int compareTo (PhoneNumber pn){
    int result = Short.compare(areaCode, pn.areaCode);
    if (result == 0){   
        result = Short.compare(prefix, pn.prefix);
        if(result == 0)
            result = Short.compare(lineNum, pn.lineNum);
    }
    return result;
}
```

→ 비교값이 0이 아니면, 순서가 결정되면 끝낼 수 있어서

### 비교자 생성 메서드

```java
private static final Comparator<PhoneNumber> COMPARATOR =
  comparingInt((PhoneNumber pn) -> pn.areaCode) // Comparator.comparingInt
  .thenComparingInt(pn -> pn.prefix)
  .thenComparingInt(pn -> pn.lineNum);
public int compareTo(PhoneNumber pn) {
	return COMPARATOR.compare(this, pn);
}
```

자바 8부터 Comparator 인터페이스가 일련의 비교자 생성 메서드를 지원

comparingInt 에서 areaCode를 비교하는 비교자 생성… 체이닝

간결하지만 약간의 성능저하

- 람다 표현식
1. 어떤 방법으로 작성해도 모든 원소를 전부 순회하는 경우는 람다식이 조금 느릴 수 밖에 없다. (어떤 방법으로 만들어도 최종 출력되는 bytecode 나 어셈블리 코드는 단순 while(혹은 for) 문 보다 몇 단계를 더 거치게 된다.)
2. 익명함수의 특성상 함수 외부의 캡처를 위해 캡처를 하는 시간제약 논리제약적인 요소도 고려해야 하며, 디버깅 시 함수 콜스택 추적이 극도로 어렵다.

### 주의사항

```java
static Comparator<Object> hashCodeOrder = new Comparator<>() {
  public int compare(Object o1, Object o2) {
    return o1.hashCode() - o2.hashCode();
  }
};
```

값의 차를 기준으로 하는 비교자는 사용하지 말자.

- 해시코드 차를 기준으로 하는 비교자는,  동일 객체를 보장하지 않기 때문에

→ 다른 객체와의 충돌 가능성을 낮추기 위해 사용되는 해시 함수)

- 정수 오버 플로를 일으킬 수 있다.

위 방식이 월등히 빠르지 않으니 아래 두 방식 중 하나를 사용하자.

```java
// 정적 compare 메서드 활용
static Comparator<Object> hashCodeOrder = new Comparator<>() {
    public int compare(Object o1, Object o2) {
        return Integer.compare(o1.hashCode(), o2.hashCode());
    }
};

// 비교자 생성 메서드 활용
static Comparator<Object> hashCodeOrder = Comparator.comparingInt(o -> o.hashCode());
```