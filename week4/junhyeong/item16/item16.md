# public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라 - item16

```java
class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
```

public 필드는 데이터 필드에 직접 접근할 수 있으니 캡슐화의 이점을 제공하지 못한다.

package-private 클래스 혹은 private 중첩 클래스라면 데이터 필드를 노출해도 하등(아무) 문제가 없다.

클래스가 표현하려는 추상 개념만 올바르게 표현해주자.

규칙을 어긴 사례 : java.awt.package : Point, Dimension 클래스

```java
public final class Time {
    public static final int HOURS_PER_DAY = 24;
    public static final int MINUTES_PER_HOUR = 60;
		public static final int SECONDS_PER_MINUTE = 60;

}
```

**public 필드가 불변이어도** 예외상황이 생기면(e.g. 윤초 60 → 61), 예외 로직을 포함한 접근자 메서드를 반환해야 한다. 그러면 직접 접근해서 사용하던 코드들을 전부 메서드를 사용하게 바꿔야 할 것이다.