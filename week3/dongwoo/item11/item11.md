## 아이템11 equals를 재정의하려거든 hashCode도 재정의하라

인텔리제이에서도 인정한다

![img.png](img.png)

hashCode는 무엇인가 객체의 hashCode는 일반적으로 각 객체의 주소값을 변환하여 생성한 객체의 고유한 정수값이다 따라서 두 객체가 동일한 객체인지 비교할 때 사용할 수 있다

equals를 재정의할때 hashCode도 재정의하지않으면 hashCode의 일반 규약을 어기게 되어 해당 클래스의 인스턴스를 HashMap이나 HashSet같은 컬렉션의 원소로 사용할 때 문제를 일으킬 것이다.

- equals(Object)가 두 객체를 같다고 판단했다면 두 객체의 hashCode는 똑같은 값을 반환해야 한다

Object명세서의 이부분때문이다

equals는 물리적으로 다른 두 객체를 논리적으로 같다고 할 수 있다 하지만 Object의 기본 hashCode는 이 둘이 전혀 다르다고 판단하여 서로 다른 값을 반환한다

```java
Map<PhoneNumber, String> m = new HashMap<>();
m.put(new PhoneNumber(01063333099), "동우");
m.get(new PhoneNumber(01063333099)
```

이러면 동우가 나와야 하지만 실제로는 null을 반환한다 왜와이? 여기서 두개의 PhoneNumber인스턴스가 사용되었기 때문에 이다 PhoneNumber클래스는 hashCode를 재정의하지 않았기 때문에 논리적으로 동치인 두 객체가 서로 다른 해시코드를 반환하여 실패하였다

그러면 모든 객체에 같은 해시코드를 적용해버리자!

```java
@Override
public int hashCode() {return 132;}
```

이러면 모든 PhoneNumber객체에 같은 해시코드를 반환하니까 가능은하겠지 하지만 모든 PhoneNumber 타입의 해시테이블의 버킷 하나에 담겨서 LinkedList처럼 동작한다 그래서 O(1)인 해시테이블이 O(n)이 된다

```
    @Override
    public int hashCode() {
        int result = String.hashCode(areaCode);
        result = 31 * result + String.hashCode(prefix);
        result = 31 * result + String.hashCode(lineNum);
        return result;
    }
```

이렇게 하면 된다 왜 31인가? 홀수이면서 소수다 짝수이고 오버플로우가 발생하면 정보를 잃는다 31을 사용하면 시프트 연산과 뺄셈으로 대체하여 최적화할 수 있다 (-> 31 * i == (i << 5) - i)

자바 7 이후로는

```java
@Override
public int hashCode() {
    return Objects.hash(areaCode, prefix, lineNum);
```

이렇게 바꿀 수 있다

우리 스프링 프로젝트에선 어떨까?

```java
@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity user = (UserEntity) o;
        return Objects.equals(id, user.id) && Objects.equals(userProfileId, user.userProfileId) && Objects.equals(abilityLevel, user.abilityLevel) && Objects.equals(appearanceLevel, user.appearanceLevel) && Objects.equals(academicLevel, user.academicLevel) && Objects.equals(incomeLevel, user.incomeLevel) && Objects.equals(kakaoAppUserId, user.kakaoAppUserId) && gender == user.gender;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userProfileId, abilityLevel, appearanceLevel, academicLevel, incomeLevel, kakaoAppUserId, gender);
    }
```

모든 칼럼에 대해서 사용하면 equals에서 비교할 요소 범위 만큼 hashCode도 그 요소 그대로 작성해 준다

결론은 equals를 사용하면 내부 값만 비교하기 때문에 사실 해시코드가 같은지는 신경 안써줘도 된다고 생각했지만 hash를 사용하는 컬렉션의 원소를 사용할때 문제를 일으키기 때문에 equals(Object)가 두 객체를 같다고 판단했다면 두 객체의 hashCode는 똑같은 값을 반환해야 한다