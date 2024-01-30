## 아이템 23 태그 달린 클래스보다는 클래스 계층 구조를 활용해라

하나의 멤버변수가 두가지 이상의 의미를 표현할 수 있으며 그중 현재 표현하는 의미를 태그값으로 알려주는 클래스를 본 적이 있을 것이다

```java
public class Figure {

    enum Shape {RECTANGLE, CIRCLE}
    
    final Shape shape;
		// 사각형인 것들
    double length;
    double width;
		//원인 것들
    double radius;
    
    // 원용 생성자
    Figure(double radius) { 
        shape = Shape.CIRCLE;
        this.radius = radius;
    }
    
    // 사각형용 생성자
    Figure(double width, double length, ) {
        shape = Shape.RECTANGLE;
        this.width = width;
        this.length = length;
    }
    
    double area() {
        switch (shape) {
            case RECTANGLE:
                return width * length;
            case CIRCLE:
                return Math.PI * (radius * radius);
            default:
                throw new AssertionError(shape);
		}
}
```

이러면 단점이 너무 많다 열거 타입, 태그 필드, switch문 등 쓸대없는 코드가 많다 여러 구현이 한 클래스에 혼합돼 있어서 가독성도 나쁘다 만약에 다른 도형이 추가되면 변경사항이 너무 많아지고 클래스가 너무 커진다

이런것들을 계층 구조로 바꿔보자

```java
public abstract class Figure {
    abstract double area();
}

public final class Circle extends Figure {

    final double radius;
    
    Circle(double radius) {
        this.radius = radius;
    }
    
    @Override
    double area() {
        return Math.PI * (radius * radius);
    }
}

public class Rectangle extends Figure {

    final double width;
    final double length;
    
    Rectangle(double width, double length) {
        this.width = width;
        this.length = length;
    }
    
    @Override
    double area() {
        return width * length;
    }
}
```

계층구조를 사용하여 태그 달린 클래스의 단점을 모두 날려버렸다 또한 클래스 계층구조에서라면 정사각형이 사각형의 특별한 형태임을 아주 간단하게 반영할 수 있다

```java
class Square extends Rectangle {
		Square(double side) {
				super(side, side);
		}
}
```

결론 → 태그 달린 클래스를 사용할 일이 거의 없다 계층구조로 대체하자