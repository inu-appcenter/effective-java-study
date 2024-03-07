# 확장할 수 있는 열거 타입이 필요하면 인터페이스를 사용하라 - item38

열거 타입은 확장할 수 없다. 그런데, 확장할 수 있는 열거 타입이 연산 코드와 같은 곳에 어울린다.

이 때, 열거 타입이 인터페이스를 구현할 수 있다는 사실을 이용하면 확장처럼 사용할 수 있다.

```java
public enum BasicOperation implements Operation {
    PLUS("+") {
        @Override
        public double apply(double x, double y) {
            return x + y;
        }
    },
    MINUS("-") {
        @Override
        public double apply(double x, double y) {
            return x - y;
        }
    },
    TIMES("*") {
        @Override
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE("/") {
        @Override
        public double apply(double x, double y) {
            return x / y;
        }
    };

    private final String symbol;

    BasicOperation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
```

이렇게 하면 Operation을 구현한 또 다른 열거 타입을 정의해 BasicOpertaion을 대체할 수 있다.

```java
public enum ExtendedOperation implements Operation {
    EXP("^") {
        @Override
        public double apply(double x, double y) {
            return Math.pow(x, y);
        }
    },
    REMAINDER("%") {
        @Override
        public double apply(double x, double y) {
            return x % y;
        }
    };

    private final String symbol;

    ExtendedOperation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
```

apply가 인터페이스에 선언되어 있어서 열거 타입에 따로 추상 메서드로 선언하지 않아도 된다. (상수별 메서드 구현과 다른 점)

### 타입 수준에서 확장된 열거 타입

```java
public enum ExtendedOperation implements Operation {

    public static void main(String[] args) {
        double x = Double.parseDouble(args[0]);
        double y = Double.parseDouble(args[1]);
        test(ExtendedOperation.class, x, y);
    }
    private static <T extends Enum<T> & Operation> void test(Class<T> opEnumType, double x, double y) {
        for (Operation operation : opEnumType.getEnumConstants()) {
            System.out.printf("%f %s %f = %f%n", x, operation, y, operation.apply(x, y));
	        }
	    }
	}
}
```

<T extends Enum<T> & Operation> : Class 객체가 열거 타입인 동시에 Opertaion 하위 타입이어야 한다는 뜻

### Collection<? extends Opertaion>

```java
 public static void main(String[] args) {
    double x = Double.parseDouble(args[0]);
    double y = Double.parseDouble(args[1]);
    test(Arrays.asList(ExtendedOperation.values()), x, y);
}

 private static void test(Collection<? extends Operation> operations, double x, double y) {
     for (Operation operation : operations) {
        System.out.printf("%f %s %f = %f%n", x, operation, y, operation.apply(x, y));
     }
  }
```

두 번째 방법은 Class 객체 대신 한정적 와일드카드 타입인 Collection<? extends Opertaion>을 넘기는 것
그나마 덜 복잡하고 test 메서드가 살짝 유연해졌다. 다시 말해서 여러 구현 타입의 연산을 조합해 호출할 수 있게 되었다.

### **인터페이스를 이용한 확장된 열거 타입의 문제점**

열거 타입끼리 구현을 상속할 수 없다

→ 디폴트 메서드로 인터페이스에 추가하는 방법을 사용할 수 있다.

⇒ 다만, 열거타입 모두에 정의된 메서드가 들어가야해서 규모가 커지면 별도의 도우미 클래스나 정적 도우미 메서드로 분리하자.