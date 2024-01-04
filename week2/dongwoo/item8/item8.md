## 아이템 8 finalizer와 cleaner사용을 피해라

객체 소멸자가 두개 있다 finalizer는 예측할 수 없고 상황에 따라 위험할 수 있어 일반적으로 불필요하 자바 9세어는 depreaated되었고 cleaner를 그 대안으로 했다 하지만 cleaner또한 여전히 예측할 수 없고 느리고 일반적으로 불필요하다 속도또한 가비지 컬렉터 알고리즘에 달려있으며 가비지 컬렉터마다 다르다

또한 아예 실행되지 않을 수 있어서 수행 여부조차 보장하지 않기 때문에 상태를 영구적으로 수정하는 작업에는 절대 finalizer와 cleaner에 의존해서는 안된다.

finalizer 동작 중 발생한 **예외는 무시**되며, 처리할 작업이 남아있더라도 **그 순간 종료**된다. 잡지 못한 예외 때문에 해당 객체는 자칫 마루리가 덜 된 상태로 남을 수 있다. 보통의 경우 잡지 못한 예외가 스레드를 중단시키고 스택 추적 내역을 출력한다. 하지만 같은 일이 finalizer에서 발생한다면 경고조차 출력하지 않는다.

또한 공격에 노출되서 심각한 보안 문제를 일으킬 수 있다 생성이나 직렬화 과정에서  예외가 발생하면 생성되다 만 객체에서 악의적인 하위클래스의 finalizer가 수행될 수 있게 된다

참https://www.youtube.com/watch?v=6kNzL1bl1kI

```java
public class Account {

    private String name;

    public Account(String name) {
        if (this.name.equals("러시아")) {
            throw new RuntimeException("러시아는 회원가입 안됨");
        }
        this.name = name;
    }

    void transfer(int amount, String to) {
        System.out.println(name + "송금 완료");
    }
}
```

이런 코드가 있다 이름이 러시아면 계정 생성자조차 생성이 안되고 뭐 송금도 안된다 근데 Account코드를 건들지 않고 러시아로 생성하고 돈을 보낼 수 있다

```java
public class AccountAttack extends Account {

    public AccountAttack(final int amount) {
        super(amount);
    }

    @Override
    protected void finalize() throws Throwable {
        this.transfer(1000000000, "푸틴");
    }
}
```

```java
public class Main {
    public static void main(final String[] args) throws InterruptedException {
        Account account = null;
        try {
            account = new AccountAttack("러시아");
            account.transfer(1000);
        } catch (Exception e) {
            System.out.println("예외 터짐");
        }
        System.gc();
        sleep(3000);
    }
}
```

어캐될까?

```java
예외터짐
1000000000 송금 완료
```

그럼 아예 사용하면 안되나?

- 안전망 역할로 자원을 반납하려 할 때
- 네이티브 자원을 정리할때

이럴때 사용할 수 있다

cleaner, finalizer가 즉시 호출될것이란 보장은 없지만, 클라이언트가 하지 않은 자원 회수를 늦게라도 해주는 것이 아예 안하는 것보다 낫다. 하지만 이런 안전망 역할로 finalizer를 작성할 때 그만한 값어치가 있는지 신중히 고려해야 한다. 자바에서는 안전망 역할의 finalizer를 제공한다. FileInputStream, FileOutputStream, ThreadPoolExecutor가 대표적이다.

```java
public class Room implements AutoCloseable {
    private static final Cleaner cleaner = Cleaner.create();

    // Room을 참조하지 말것!!! 순환 참조
    private static class State implements Runnable { 
        int numJunkPiles;

        State(int numJunkPiles) {
            this.numJunkPiles = numJunkPiles;
        }

        @Override
        public void run() {  // **colse가 호출되거나, GC가 Room을 수거해갈 때 run() 호출**
            System.out.println("Room Clean");
            numJunkPiles = 0;
        }
    }

    private final State state;
    private final Cleaner.Cleanable cleanable;

    public Room(int numJunkPiles) {
        state = new State(numJunkPiles);
        cleanable = cleaner.register(this, state);
    }

    @Override
    public void close() {
        cleanable.clean();
    }
}
```

State 인스턴스가 Room 인스턴스를 참조할 경우 순환참조가 발생하고 가비지 컬렉터가 Room을 회수해갈 기회가 오지 않는다. State가 static인 이유도 바깥 객체를 참조하지 않기 위해서이다.

위 코드는 안전망을 만들었을 뿐이다. 클라이언트가 try-with-resources 블록으로 감쌌다면 방 청소를 정상적으로 출력한다.

```java
public static void main(final String[] args) {
    try (Room myRoom = new Room(8)) {
        System.out.println("ㅎㅇㅎㅇ");
    }
}
```

```java
public static void main(final String[] args) {
    new Room(8);
    System.out.println("ㅋㅋ");
}
```

첫번째 코드는 안녕을 출력한 후 방청소를 출력한다

하지만 두번째 코드는 ㅋㅋ에 이어서 방청소가 출력되지 않을 수 있다 그니까 예측할 수 없다