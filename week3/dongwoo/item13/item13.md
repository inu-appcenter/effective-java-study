## 아이템 13 clone재정의는 주의해서 진행하라

clone이 몰까 살면서 처음 들어봤다

clone메서드를 실행할 경우에 복사 대상이 되는 클래스는 java.lang.Cloneable인터페이스를 구현할 필요가 있다 Clonable인터페이스를 구현한 클래스의 인스턴스는 clone메서드를 호출하면 복사됩니다. 그리고 clone메서드의 반환값은 복사해서 만들어진 인스턴스가 된다 내부에서는 인스턴스가 같은 크기의 메모리를 확보한 뒤, 그 인스턴스의 필드 내용을 복사하는 것입니다 그니까 복제하면 두개의 객체 인스턴스는 다른데 내부 필드는 같다고 보면 된다

```java
public class AppCenterServer implements Cloneable{

    private List<String> member = new ArrayList<>();

    public AppCenterServer(List<String> member) {
        this.member = member;
    }

    @Override
    public AppCenterServer clone() {
        try {
            AppCenterServer clone = (AppCenterServer) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

AppCenterServer 15 = new AppCenterServer();
15.getList().add("이주원");
15.getList().add("이영재");
// 15 찍으면 [이주원, 이영재]
AppCenterServer 16 = 15.clone();
// 15, 16 해시값 [AppCenter@e121er, AppCenter@41er3]
15 == 16 -> false
15.getList() == 16.getList() -> true
15.getList().add("구준형");
//16찍으면 [이주원, 이영재, 구준형]
```

이렇다 벌써 주의해야될게 느껴진다

책으로 돌아오면 clone메서드가 선언된 곳이 Clonable이 아닌 Object이고 그마저도 protected이다 그래서 Clonable을 구현하는 것 만으로는 외부 객체에서 clone메서드를 호출할 수 없다

그러면 메서드 하나 없는 Clonable인터페이스는 무슨 일을 ㅎㄹ까 이거는 Object의 protected메서드인 clone의 동작 방식을 결정한다 Cloneable을 구현한 클래스의 인스턴스에서 clone을 호출하면 그 객체의 필드들을 하나하나 복사한 객체를 반환하며, 그렇지 않은 클래스의 인스턴스에서 호출하면 ClonenotSuppertedExecpetion을 던진다 실제로는 Clonable을 구현한 클래스는 clone메서드를 public으로 제공하며 사용자는 당연히 복제가 제대로 이루어지리라 기대한다 제대로된 clone메서드를 봐보면

```java
    @Override
    public AppCenterServer clone() {
        try {
            return (AppCenterServer) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
```

super.clone()을 호출하면 된다 클래스에 정의된 모든 필드가 원본 필드와 똑같은 값을 갖는다 모든 필드가 기본 타입이나 불변객체를 포함한다면이 객체는 이미 완벽한 상태라 손볼것도 없다 또한 clone메서드는

```java
@HotSpotIntrinsicCandidate
protected native Object clone() throws CloneNotSupportedException;
```

이렇게 Object를 반환하지만 공변 반환 타이핑을 지원하니 AppCenterServer를 반환하게 만들 수 있다

그리고 가변 객체를 참조하는 순간 재앙으로 돌아온다고 한다 이건 맨위에 있었던 예시로충분히 설명이 가능하다 재귀를 쓰면 해결할 수 있지만 재귀는 쓰지 말자 킼킼

CloneNotSupportedException 를 던지지 않는게 좋다 clone메서드에서 throws절을 없애야 한다 그래야 그 메서그를 사용하기 편하기 때문이다

Cloaneable을 구현한 thread safe클래스를 작성할 때는 clone메서드 역시 적절하게 동기화해줘야 한다 Object의 clone()은 동기화를 신경쓰지 않았기 때문이다  결국에는 Cloneable을 구현한 모든 클래스는 clone을 재정의해야 한다 이때 접근 재한자는 Pulbic으로, 반환타입은 클래스 자기 자신으로 변경한다 말 그대로 객체 내부 ‘깊은 구조’에 숨어있는 모든 가변 객체를 복사하고 복제본이 가진 객체 참조 모두가 복사된 객체드을 가리키게 함을 뜻한다 그리고 왠만하면 clone보다는 복사 생성자, 복사 팩터리를 쓰자 clone의 단점이 다 없고 인터페이스타입의 인스턴스를 인수로도 받을 수 있다