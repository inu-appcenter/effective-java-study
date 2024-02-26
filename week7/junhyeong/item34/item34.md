# int 상수 대신 열거 타입을 사용하라 - item34

### 정수 열거 패턴

```java
public static final int APPLE_FUJI = 0;
public static final int ORANGE_BLOOD = 2;
```

열거 타입 지원 전에는 정수 상수를 한 묶음 선언해 사용하곤 했다.

그러나 이는 타입 안전을 보장할 방법이 없다.

오렌지를 건네야 할 메서드에 사과를 보내 == 비교하더라도 컴파일러는 아무런 경고 메시지를 출력하지 않는다.

출력한 값을 보면 의미없는 숫자로 보여서 도움이 되지 않고

문자열 상수를 사용해도 하드코딩한 문자열에 오타가 있어도 컴파일러는 확인할 길이 없으니 자연스럽게 런타임 버그가 생긴다.

### 열거 타입

```java
public enum Apple {FUJI, PIPPIN}
```

자바의 열거 타입 자체는 클래스다.

상수 하나당 자신의 인스턴스를 하나씩 만들어 public static final 필드로 공개한다.

열거 타입은 밖에서 접근할 수 있는 생성자를 제공하지 않으므로 사실상 final이다. → 클라이언트가 인스턴스를 직접 생성하거나 확장할 수 없으니 열거 타입 선언으로 만들어진 인스턴스들은 딱 하나씩만 존재함이 보장된다.

싱글톤은 원소가 하나뿐인 열거 타입이라 할 수 있고, 거꾸로 열거 타입은 싱글톤을 일반화한 형태라고 볼 수 있다.

### 정수 열거 패턴의 단점 해결

타입 안전성 제공 : Apple 열거 타입을 매개변수로 받는 메서드를 선언했다면, 다른 타입을 넘기려면 컴파일오류

열거 타입에 새로운 상수 추가하거나 순서를 바꿔도 다시 컴파일되지 않음.

→ 공개되는 것이 오직 필드의 이름뿐이기 때문

마지막으로 열거 타입의 toString 메서드는 출력하기에 적합한 문자열을 내어준다.

```java
public abstract class Enum<E extends Enum<E>>
        implements Constable, Comparable<E>, Serializable
```

Enum 자체는 Object 메소드들과 Comparable, Serializable을 구현했다.

### 열거 타입에 메서드나 필드 추가

각 상수와 연관된 데이터를 해당 상수 자체에 내제시키고 싶을 때.

```java
public enum Planet {
	MARS(6.2443e+23, 3.3644e8)

	private final double mass; // 질량
	private final double radius; // 반지름
	private final double surfaceGravity; // 표면중력

	private static final double G = 6.67; // 중력 상수

	~~private~~ Planet(double mass, double radius) {
		this.mass = mass;
		this.radius = radius;
		surfaceGravity = G * mass / (radius * radius);
	}

	public double surfaceWeight(double mass) {
		return mass * surfaceGravity;
	}
}
```

열거타입은 private 생성자만 가지므로 private을 안붙여줘도된다.

### 상수별로 다르게 동작하는 코드

예쁘지 않은 예

```java
public enum Operation {
    PLUS,MINUS,TIMES,DIVDE;

    public double apply(double x, double y) {
        switch (this) {
            case PLUS:
                return x + y;
            case MINUS:
                return x - y;
            case TIMES:
                return x * y;
            case DIVDE:
                return x / y;
        }
        throw new AssertionError("알 수 없는 연산:" + this);
    }
```

동작은 하지만 예쁘지 않다. throw문도 실제로 도달할 일이 없지만 switch 특성상 case에 없는경우 리턴할 값이 필요해서 작성해야한다.

더 나쁜 점은 깨지기 쉬운 코드라는 사실.

새로운 상수를 추가하면 해당 case문도 추가해야한다.

### 상수별 메서드 구현

열거 타입에 추상 메서드를 선언하고  각 상수에서 자신에 맞게 재정의하는 방법을 제공한다.

```java
public enum Operation {
    PLUS {
        public double apply(double x, double y) { return x + y; }
    },
    MINUS {
        public double apply(double x, double y) { return x - y; }
    },
    TIMES {
        public double apply(double x, double y) { return x * y; }
    },
    DIVIDE {
        public double apply(double x, double y) { return x / y; }
    };

    public abstract double apply(double x, double y);
}
```

추상 메서드 때문에 재정의하지 않았다면 컴파일 오류로 알려줄 것이다.

```java
public enum Operation {
    PLUS("+") {
        public double apply(double x, double y) { return x + y; }
    },
    MINUS("-") {
        public double apply(double x, double y) { return x - y; }
    },
    TIMES("*") {
        public double apply(double x, double y) { return x * y; }
    },
    DIVIDE("/") {
        public double apply(double x, double y) { return x / y; }
    };

    private final String symbol;

    Operation(String symbol) { this.symbol = symbol; }

    @Override public String toString() { return symbol; }

    public abstract double apply(double x, double y);

    // 열거 타입용 fromString 메서드 구현하기 
    private static final Map<String, Operation> stringToEnum =
            Stream.of(values()).collect(
                    toMap(Object::toString, e -> e));
		// ("+", Operation.PLUS)
    // 지정한 문자열에 해당하는 Operation을 (존재한다면) 반환한다.
    public static Optional<Operation> fromString(String symbol) {
        return Optional.ofNullable(stringToEnum.get(symbol));
    }

    public static void main(String[] args) {
        double x = Double.parseDouble(args[0]);
        double y = Double.parseDouble(args[1]);
        for (Operation op : Operation.values())
            System.out.printf("%f **%s** %f = %f%n",
                    x, op, y, op.apply(x, y));
    }
}
```

toString을 연산기호필드를 리턴하게 해서 계산식 출력을 편하게 해준다.

toString메서드를 재정의하려면 toString이 반환하는 문자열을 해당 열거 타입 상수로 다시 변환해주는 fromString 메서드를 함께 제공하는 걸 고려하자.

존재하지 않을 수 있으니 Optional로 감싼걸 유의.

### 값에 따라 분기하여 코드를 공유하는 열거 타입

```java
enum PayrolLDay {
    MONDAY, TUESDAY, WEDSENDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY:

    private static final int MINS_PER_SHIFT = 8 * 60;
    
    int pay(int minutesWorked, int payRate) {
        int basePay = minuitesWorked * payRate;
        
        int overtimePay;
        switch(this) {
            case SATURDAY: case SUNDAY:
                overtimePay = basePay /2;
                break;
            **default**:
                overtimePay = minuutesWorked <= MINS_PER_SHIFT ? 0 : (minutesWorked - MINS_PER_SHIFT) * payRate /  2;
        }
        return basePay + overtimePay;
    }
}
```

급여명세서에 쓸 요일을 표현하는 열거타입.

간결하지만 관리 관점에서는 위험한 코드. 휴가와 같은 새로운 값을 열거 타입에 추가하고 싶은데, 만약 case문을 넣어주지 않았으면 휴가 날 열심히 일해도 급여을 못 받게 된다.

### 전략 열거 타입

```java
enum PayrollDay {
    MONDAY(WEEKDAY), TUESDAY(WEEKDAY), WEDNESDAY(WEEKDAY),
    THURSDAY(WEEKDAY), FRIDAY(WEEKDAY),
    SATURDAY(WEEKEND), SUNDAY(WEEKEND);

    private final PayType payType;

    PayrollDay(PayType payType) { this.payType = payType; }
    // PayrollDay() { this(PayType.WEEKDAY); } // (역자 노트) 원서 4쇄부터 삭제
    
    int pay(int minutesWorked, int payRate) {
        return payType.pay(minutesWorked, payRate);
    }

    // 전략 열거 타입
    enum PayType {
        WEEKDAY {
            int overtimePay(int minsWorked, int payRate) {
                return minsWorked <= MINS_PER_SHIFT ? 0 :
                        (minsWorked - MINS_PER_SHIFT) * payRate / 2;
            }
        },
        WEEKEND {
            int overtimePay(int minsWorked, int payRate) {
                return minsWorked * payRate / 2;
            }
        };

        abstract int overtimePay(int mins, int payRate);
        private static final int MINS_PER_SHIFT = 8 * 60;

        int pay(int minsWorked, int payRate) {
            int basePay = minsWorked * payRate;
            return basePay + overtimePay(minsWorked, payRate);
        }
    }

    public static void main(String[] args) {
        for (PayrollDay day : values())
            System.out.printf("%-10s%d%n", day, day.pay(8 * 60, 1));
    }
}
```

잔업 수당 계산을 중첩 열거 타입으로 옮기고 

톱레벨 클래스에서 생성자에서 적절한걸 선택하도록 한다.

이러면 휴가를 추가하고싶으면 전략 열거 타입에 추가하고 분기처리하면 될 것 같다.

대부분의 경우 열거 타입의 성능이 정수 상수와 별반 다르지 않기 때문에, 필요한 원소를 컴파일타임에 다 알 수 있는 상수 집합이라면 항상 열거 타입을 사용하자. (태양계 행성, 한 주의 요일, 체스 말)