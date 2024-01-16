## 아이템 14 Comparable을 구현할지 고려하라

Comparable의 유일무이한 메서드인 compareTo를 알아보자 compareTo는 동치성 비교에 더해서 순서까지 비교할 수 있으며 제네릭한 점을 제외하곤 equals랑 똑같다

Comparable을 구현한 객체들은 정렬, 검색, 극단값 계산, 자동 정렬되는 컬렉션 관리도 쉽게 할 수 있다

자바 라이브러리에 값 클래스, 열거 객체는 Comparable을 구현했다

```java
public final class String
    implements java.io.Serializable, Comparable<String>, CharSequence,
               Constable, ConstantDesc {

public interface Comparable<T> {
    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure {@link Integer#signum
     * signum}{@code (x.compareTo(y)) == -signum(y.compareTo(x))} for
     * all {@code x} and {@code y}.  (This implies that {@code
     * x.compareTo(y)} must throw an exception if and only if {@code
     * y.compareTo(x)} throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies
     * {@code x.compareTo(z) > 0}.
     *
     * <p>Finally, the implementor must ensure that {@code
     * x.compareTo(y)==0} implies that {@code signum(x.compareTo(z))
     * == signum(y.compareTo(z))}, for all {@code z}.
     *
     * @apiNote
     * It is strongly recommended, but <i>not</i> strictly required that
     * {@code (x.compareTo(y)==0) == (x.equals(y))}.  Generally speaking, any
     * class that implements the {@code Comparable} interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * @param   o the object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *          is less than, equal to, or greater than the specified object.
     *
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException if the specified object's type prevents it
     *         from being compared to this object.
     */
    public int compareTo(T o);
```

comparable은 제네릭 자리에 비교할게 들어가고 구현체는 반드시 compareTo메서드를 오버라이딩 해서 사용해야됨 구현은 호출하는 객체와 비교하는 객체의 순서를 비교함 주어진 객체보다 작으면 음의 정수 주어진 객체와 같으면 0, 주어진 객체보다 크면 양의 정수 반환

compareTo의 일반 규약

- 두객체 참조의 순서를 바꿔 비교해도 예상한 결과가 나와야함
    - a.compareTo(b)가 양수면 b.compareTo(a)는 음수여야하고 둘이 같다면 어떻게 하든 0이 나와야 할거다
- 첫번째가 두번째보다 크고 두번째가 세번째보다 크면 첫번째는 세번째 보다 커야함
    - a > b, b > c, a > c
- 크기가 같은 객체들끼리는 어떤 객체와 비교하더라도 항상 같아야함
    - a.compare(b)가 0 이면 a, b에 대해서 다른 어떤 비교 결과도 같아야 한다
- 권고지만 compareTo로 수행한 동치성 테스트 결과가 equals와 같아야 한다
    - (x.compareTo(y) == 0) == (x.equals(y)) 여야 한다
    - 예시 new BigDecimal(1.0) 이랑 new BigDecimal(1.00)은 compareTo로 비교하면 같지만 equals로 비교하면 다르다

다 버리고 compareable 구현체 사용법

- 객체 참조 필드가 하나뿐인 비교자

```java
public int compareTo(Member target) {
		return Long.compare(this.id, target.id)
}
```

- 기본 타입 필드가 여럿일 때의 비교자(정렬 기준 필드가 여러개)

```java
public int compareTo(Member target) {
		int result = Long.compare(this.id, target.id) //우선순위가 높음
		if (result == 0) {
				result = Long.compare(this.prefix, target.prefix);
				if (result == 0) {
						result = Short.compare(this.lineNum, target.lineNum); //우선순위가 낮음
				}
		}
		return result;
}
```

- 비교자 생성 메서드를 활용한 비교자

```java
private static final Comparator<PhoneNumber> COMPARATOR = 
				comparingInt((PhoneNumber pn) -> pn.areaCode)
						.thenComparingInt(pn -> pn.prefix)
						.thenComparingInt(pn -> pn.lineNum);

public int compareTo(PhoneNumber pn){
		return COMPARATOR.compare(this, pn)
}
```

이방법은 자바 8 에서 Comparator 인터페이스가 일련의 비교자 생성 메서드와 팀을 꾸려 메서드 연쇄 방식으로 비교자를 생성할 수 있게되서 나온 방법이다 하지만 이 방법을 쓰면 간결해지지만 성능 저하가 뒤따른다

- 해시코드 값의 차를 기준으로 하는 비교자

```java
static Comparator<Object> hashCodeOrder = new Comparator<>() {
		public int compare(Object o1, Object o2) {
				return Integer.compare(o1.hashCode(), o2.hashCode());
		}
};
```

순서를 고려하는 값 클래스를 작성한다면 꼭 Comparable인터페이스를 구현하여 그 인스턴스를 쉽게 정렬하고, 검ㅎ색하고, 비교기능을 제공하는 컬렉션과 아우러지도록 써야한다