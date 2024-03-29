# 인터페이스는 타입을 정의하는 용도로만 사용하라 - item22

인터페이스는 자신을 구현한 클래스의 인스턴스를 참조할 수 있는 타입 역할을 한다.

달리 말해, 클래스가 어떤 인터페이스를 구현한다는 것은 자신의 인스턴스로 무엇을 할 수 있는지를 클라이언트에 얘기해주는 것이다. (오직 이 용도로만)

### 인터페이스를 잘못 사용한 예 - 상수 인터페이스

```java
public interface PhysicalConstants {
	static final double AVOGADROS_NUMBER = 6.022...;
}
```

내부 구현을 클래스의 API로 노출하는 행위다. 사용자에게 혼란을 주기도 하며,  클라이언트의 코드가 상수들에 종속되게 한다.

더 쓰지 않게 되더라도 호환성을 위해 여전히 상수 인터페이스를 구현하고 있어야한다

### 합당한 선택지

**강하게 연관된 클래스나 인터페이스 자체에 추가**

e.g. Integer.MAX_VALUE , MIN_VALUE

**열거 타입**

**인스턴스화 할 수 없는 유틸리티 클래스**