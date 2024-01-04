## 아이템 2 생성자에 매개변수가 많다면 빌더를 고려하라

이건 익히 알고있었다 책 보기 전에 예상해보면 일단 매개변수가 많은 생성자는 엄청나게 길어질 수 있고 가독성이 떨어진다 그러다보면 휴먼 에러가 발생할 수 있다 그래서 사용하는거로 알고있다

정적 팩터리와 생성자에는 똑같은 제약이 있따 매개변수가 많으면 적절하게 대응하기 어렵다 식품의 클래스에는 영양정보가 20개는 들어있을것이다 하지만 이것중에 대부분이 0이면 기존 개발자들은 어떻게 했을까? 매개변수 한개 받는 생성자, 두개받는 생성자, 세개받는 생성자등등 엄청난 오버로딩을 했다 그럼 이중에서 내가 원하는 매개변수를 포함한 놈들을 골라 호출하면 되니까 → 점층적 생성자 패턴도 쓸 수 있지만 매개변수가 너무 많아지면 클라이언트 코드를 작성하거나 읽기 어렵다(진짜 저 위에 있는 글 책 보기전에 썻음 ㄹㅇ로)

자바 빈즈 패턴이란 매개변수 없는 생성자 만들고 setter쓰는거다 (ㄷㄷ…) 단점은 객체를 하나 만들려면 메서드를 많이 호출해야되고 객체가 완전히 생성되기전까지 일관성이 무너진 상태에 놓이게 된다 스레드 세이프를 하려면 프로그래머가 추가 작업을 해줘야한다(왜지?)

위에는 너무 말도안되는 대안을 내놨다 이제부터 빌더패턴이다

클라이언트는 필요한 객체를 직접 만드는 대신 필수 매개변수만으로 생성자를 호출해서 빌더 객체를 얻는다 그다음에 빌더 객체가 제공하는 일종의 세터 메서드들로 원하는 선택 매개변수들을 설정한다 마지막으로 .build()를 호출해서 우리에게 필요한 불변 객체를 얻는다 사용을 봐보자

```java
public class NutritionFacts {
    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int sodium;

    public static class Builder {
				// 필수 매개변수
        private final int servingSize;
        private final int servings;
				// 선택 매개변수 - 기본값으로 초기화
        private int calories = 0;
        private int fat = 0;
        private int sodium = 0;

        public Builder(int servingSize, int servings) {
            this.servingSize = servingSize;
            this.servings = servings;
        }

        public Builder calories(int val) {
						// validation 가능
            this.calories = val;
            return this;
        }

        public Builder fat(int val) {
            this.fat = val;
            return this;
        }

        public Builder sodium(int val) {
            this.sodium = val;
            return this;
        }

        public NutritionFacts build() {
            return new NutritionFacts(this);
        }
    }
    public NutritionFacts(Builder builder) {
        this.servingSize = builder.servingSize;
        this.servings = builder.servings;
        this.calories = builder.calories;
        this.fat = builder.fat;
        this.sodium = builder.sodium;
    }
}

User user = new User.Builder(240, 80)
                .calories(100)
                .sodium(12)
                .build();
```

저렇게 필수인 240, 80넣고 제로 칼로리라서 fat가 없으면 그냥 fat는 안써주면 빌더 정적 팩토리 메서드에 기본값으로 0 해놨으니까 상관없다

빌더의 세터 메서드들은 빌더 자신을 반환하기 떄문에 연쇄적으로 호출할 수 있다 이러한 방식을 메서드 호출이 흐르듯 연결된다는 뜻으로 fluent api | method chaining이라 한다

빌더 패턴은 계층적으로 설계된 클래스와 함께 쓰기에 좋다 각 계층의 클래스에 관련 빌더를 멤버로 정의하고 추상 클래스는 추상 빌더를 구체 클래스는 구체 빌더를 갖게 한다.

```java
class Pizza{

    public enum Topping {HAM, MUSHROOM, ONION, PEPPER}

    final Set<Topping> toppings;

    abstract static class Builder<T extends Builder<T>> {

        EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);

        public T addTopping(Topping topping) {
            toppings.add(Objects.requireNonNull(topping));
            return self();
        }

        abstract Pizza build();

        protected abstract T self();
    }
    Pizza(Builder<?> builder) {
        toppings = builder.toppings;
    }
}

class Nypizza extends Pizza {
    public enum Size {SMALL, MEDIUM, LARGE}

    private final Size size;

    public static class Builder extends Pizza.Builder<Builder> {
        private Size size;

        public Builder(Size size) {
            this.size = size;
        }

        @Override
        public NyPizza build() {
            return new NyPizza(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
    private Nypizza(Builder builder) {
        super(builder);
        size = builder.size;
    }
}

NyPizza pizza = new Nypizza.Builder(SMALL)
                .addTopping(HAM)
                .addTopping(MUSHROOM)
                .build();
```

NyPizza.builder는 Nypizza를 반환한다 하위 클래스의 메서드가 상위 클래스의 메서드가 정의한 반환 타입이 아닌 그 하위 타입을 반환하는 기능을 공변 반환 타이핑이라 한다 위 코드에서

```java
        @Override
        protected Builder self() {
            return this;
        }
```

이게 공변 반환 타이핑이다 이럼 클라이언트가 형변환에 신경쓰지 않고도 빌더를 사용할 수 있다

빌더 패턴의 단점은 빌더 생성 비용이 크지는 않지만 성능에 민감한 상황에서는 문제가될 수 있다 그리고 코드가 장황해서 매개변수가 4개 이상이여야지 값어치를 한다 그리고 api는 시간이 지날수록 매개변수가 많아진다