## 아이템 16: public 클래스에서는 public필드가 아닌 접근자 메서드를 사용하라

```java
class Point {
		public double x;
		public double y;
}
```

이러런 클래스는 캡슐화의 이점을 제공하지 못한다 그래서 private로 바꾸고 getter, setter를 추가한다 그렇게 하면 유연성을 얻고 내부 표현을 바꿀 수 있다

1. API를 수정하지 않고는 내부 표현을 바꿀 수 없음
2. 불변식을 보장할 수 없음
3. 외부에서 필드에 접근할때 부수 작업을 수행할 수 없음

해결책

1. Getter Setter사용
2. private 중첩 클래스 사용
3. package-private 클래스 사용

기본적이라고 생각하지만 자바 플랫폼 라이브러리에도 public클래스의 필드를 직접 노출하지 말라는 규칙을 어긴 사례가 있다 Point와 Dimension 클래스이다 Dimension 클래스의 심각한 성능 문제는 오늘날까지도 해결되지 못했다

public 클래스의 필드가 불변이라면 불변식을 보장못하는 단점은 없어지지만 그래도 나머지 1, 3 번 단점은 해결할 수 없다