# 태그 달린 클래스보다는 클래스 계층구조를 활용하라 - item23

**태그** : 해당 클래스가 어떠한 타입인지에 대한 정보를 담고있는 멤버 변수

```java
public class Figure {
    enum Shape {RECTANGLE, CIRCLE}

    //태그 필드 - 현재 모양을 나타낸다.
    private Shape shape;

    // 다음 필드들은 모양이 사각형일 때만 사용.
    private double length;
    private double width;

    // 다음 필드들은 모양이 원일 때만 싸용.
    private double radius;

    //원용 생성자
    public Figure(double radius) {
        shape = Shape.CIRCLE;
        this.radius = radius;
    }

    //사각형용 생성자
    public Figure(double length, double width) {
        shape = Shape.RECTANGLE;
        this.length = length;
        this.width = width;
    }

    private double area() {
        switch (shape) {
            case RECTANGLE:
                return length + width;
            case CIRCLE:
                return Math.PI * (radius * radius);
            default:
                throw new AssertionError(shape);
        }
    }
}
```

### 단점

- 열거 타입, 태그 필드, switch 문 등 쓸데없는 코드
- 단일 책임 원칙 위배 (원, 사각형)
- 개방 폐쇄 원칙 위배 (삼각형을 추가한다면, swich문을 건들여야한다.)

### 클래스 계층 구조로 바꾸기

```java
abstract class Figure {
  abstract dobule area(); 
}

class Circle extends Figure {
  final double radius;
  
  Circle(double radius) {this.radius = radius;}
  
  @Override double area() {return Math.PI * (radius * radius);}
}

class Rectangle extends Figure {
  final double length;
  final double width;
  
  Rectangle(double length, double width) {
    this.length = length;
    this.width = width;
  }
  
  @Override double area() {return length * width;}
}
```

추상 클래스에 태그 값에 따라 동작이 달라지는 메서드들을 추상 메서드로 선언

일정한 메서드는 일반 메서드로 추가

```java
class Square extends Rectangle { 
	Square(double side) {
    super(side,side);
  }
}
```

계층 구조로 바꿨으면, 정사각형을 추가할 때, 이 코드만 반영하면 된다.