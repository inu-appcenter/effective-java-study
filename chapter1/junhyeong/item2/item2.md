# 생성자에 매개변수가 많다면 빌더를 고려하자 - item2

생성자와 정적 팩토리 메서드는 매개변수가 많다면, 작성하기 어렵거나 읽기 어렵다.

그렇다고 세터 메서드를 쓰면 일관성이 깨지고 불변으로 만들 수 없다.

⇒ 빌더 패턴

```java
public class User {
    private String name;
    private String gender;
    private String role;

    private User(String name, String gender, String role) {
        this.name = name;
        this.gender = gender;
        this.role = role;
    }

    public static class UserBuilder {
        // 필수
        private final String name;
        private final String gender;
        private String role = "USER";

        public UserBuilder(String name, String gender) {
            this.name = name;
            this.gender = gender;
        }
        public UserBuilder role(String val) {
            role = val;
            return this;
        }
        public User build() {
            return new User(this);
        }
    }
    private User(UserBuilder userBuilder) {
        name = userBuilder.name;
        gender = userBuilder.gender;
        role = userBuilder.role;
    }
    public static void main(String[] args) {
        User user = new UserBuilder("user", "man")
                .role("USER")
                .build();
    }
```

동일한 멤버변수를 가지는, 정적 멤버 클래스(빌더)를 선언해서

필수값은 생성자로, 나머지는 필드값 주입하고 빌더를 반환하는 메서드로 원하는값을 조립하고

build() 메서드로 본 클래스를 반환해주는 방식이다.

필수값을 빌더 생성자로 둔 부분이 좀 예쁜 패턴인 것 같다.