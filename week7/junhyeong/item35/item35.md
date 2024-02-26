# ordinal 메서드 대신 인스턴스 필드를 사용하라 - item35

ordinal : 해당 상수가 열거 타입에서 몇 번째 위치인지 반환하는 메서드.
상수 선언 순서를 바꾸는 순간 어지러워진다.

```java
public enum Ensemble {
    SOLO, DUET, TRIO, QUARTET, QUINTET, SEXTET, SEPTET, OCTET, NONET, DECTET;

    public int numberOfMusicians() {
        return ordinal() + 1;
    }
}
```

추가로 정수값이 같은 상수를 추가할 방법이 없다.

스프링에서는 jpa 엔티티와 매핑된 컬럼 안에 enum 값을 어떻게 넣을지 정의하는 애노테이션이 있다. 기본이 EnumType.ORDINAL이라서 텍스트 자체를 저장하는 STRING으로 변경하자.

**@Enumerated(value = EnumType.STRING)**