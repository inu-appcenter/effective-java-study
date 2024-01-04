## 아이템 1 생성자 대신 정적 팩터리 메서드를 고려하라

```java
public static UnivInspectEntity of(Long userProfileId) {
        return UnivInspectEntity.builder()
                .univName("하얼빈대학교")
                .status(InspectStatus.BEFORE_INSPECT)
                .userGender(Gender.MALE)
                .userProfileId(userProfileId)
                .build();
    }
```

이런게 정적 팩터리 메서드이다 솔찍히 여기서부터 든 생각은 개소리 같다고 생각했다 static 이란 내가 알기론 클래스의 로딩이 끝나는 즉시(간단하게 프로그램 시작시) 바로 사용할 수 있고 Heap에 메머리 할당하지 않고 Static에 할당해서 가비지 컬렉터를 관리해주는 heap의 장점 없이 가비지 컬렉터도 없고 모든 객체가 메모리를 공유하는건데 이거 약간 그냥 클래스 내부의 멤버 변수나 메서드를 public으로 열어서 쓰는거랑 다른게 뭐지? 캡슐화에 안좋을거같은데 static은 매직넘버쓸때나 쓰는거 아니였나? 라는 생각이 들었다 이팩티브 자바를 스터디 하자고 한 영재님이 원망스러울 찰나 유명한데에는 이유가 있겠지 라는 생각으로 계속 읽어보았다

책에서 설명하는 이유

첫번째: 이름을 가질 수 있다 라는 곳이다 생성자로 생성하면 그냥 객체 클래스 이름이지만 정적 팩터리 메서드를 사용하면 BigInteger(int, int, Random)은 뭘하는지 애매한 반면 BigInteger.probablaPrime 으로 하면 뭘 하는지 확실히 알 수 있다

두번째: 호출될 때마다 인스턴스를 새로 생성하지는 않아도 된다. 이다

인스턴스를 캐싱하여 재활용하는 식으로 불필요한 객체 생성을 피할 수 있다 대표적인 예시로 Boolean.valueOf(boolean)은 객체를 아예 생성하지 않는다. 따라서 생성비용이 큰 객체가 자주 요청되는 상황이면 성능을 끌여올려 준다고 한다

또한 반복되는 요청에 같은 객체를 반환하는 식으로 정적 팩터리 방식의 클래스는 언제 어느 인스턴스를 살아 있게 할지를 철저히 통제할 수 있다. 이런 클래스를 인스턴스 통제 클래스라 한다

세번째: 반환타입의 하위 ㅇ타입 객체를 반화할 수 있는 능력이 있다

```java
interface Person{...}

public class Student implements Person{
    String name;

    public static Person birth(String name) {
        return new Student(name);
    }
    ....
}
```

api를 만들때 이 유연성을 이용하면 구현 클래스를 공개하지 않고도 그 객체를 반환할 수 있어서 api를 작게 유지할 수 있다

네번째: 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다

EnumSet클래스는 public생성자 없이 정적 팩터리만 제공한다 그리고 원소의 수에 따라 하위 클래스중에 하나의 인스턴스를 반환한다 원소가 64개 이하면 long 변수하나로 관리하는 RegularEnumSet의 인스턴스, 65개이상이면 long배열로 관리하는 JumboEnumSet인스턴스를 반환한다

```java
public static <E extends Enum<E>> EnumSet<E> noneOf(Class<E> elementType) {
        Enum<?>[] universe = getUniverse(elementType);
        if (universe == null)
            throw new ClassCastException(elementType + " not an enum");

        if (universe.length <= 64)
            return new RegularEnumSet<>(elementType, universe);
        else
            return new JumboEnumSet<>(elementType, universe);
    }
```

실제 EnumSet코드

저 코드를 사용하는 곳은 레귤러 셋과 점보셋의 존재를 모른다 클라이언트는 정적 팩터리 메서드가 반환하는 객체가 레귤러인지 점보인지 i dont give a shit 그냥 EnumSet의 하위클래스기만 하면 된다

다섯번쨰: 정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다

```java
public class Person{

    public static Person getPerson(int id) {
        Person person = new Person();
        // 여기서 하위 클래스를 뭐로할지 결정
        return person;
    }
    ....
}

public class Student implements Person{
    ....
}
```

하위 클래스로 학생이있든 교수가 있든 일단 상위 클래스에서 정적 팩토리 메서드 써서 사용할때는 원하는 구현체의 조건을 명시에서 작성 시점에 어떤 하위 클래스를 반환할지 결정한다 저기 주석에서 구현체의 FQCN을 읽어오고 해당하는 인스턴스를 생성하고 car가 그 인스턴스를 가리키도록 한다고 한다

우리는 JDBC에서 같은 mysql을 쓰든, oracle을 쓰든, postgresql을 쓰든 JDBC의 코드는 달라지지 않는다 그냥 getConnection() 을 쓰면 우리가 쓰는 드라이버 마다 다르게 반환해준다 getMySQLConnection을 하지 않는다 그래서 해당 설정 파일인 yml에서 읽어서 적용한다

connection이 서비스 인터페이스 역할, DriverManager.registerDriver가 제공자 등록 api역할, DriverManager.getConnection이 서비스 접근 api역할, Driver가 서비스 제공자 인터페이스 역할을 수행한다

이제부터 단점이다

첫번째: 상속을 하려면 pulbic이나 protected생성자가 필요하니 정적 팩터리 메서드에만 제공하면 하위 클래스를 만들 수 없다

컬렉션 프레임워크를 상속할 수 없다고 한다 Collection을 상속할 수 없는건 당연한거 아닌가 내가 너무 보수적인가…

두번째: 정적 팩터리 메서드는 프로그래머가 찾기 어렵다. 생성자처럼 api설명에 드러나지 않는다 javadoc이 자동으로 모아서 보여주지 않는다

이건 정적 팩터리 메서드에서 흔히 사용하는 명명 방식

- from : 하나의 매개 변수를 받아서 객체를 생성
- of : 여러개의 매개 변수를 받아서 인스턴스를 반환해주는 집계 메서드
- valueOf: from과 of의 더 자세한 버전
- instance | getInstance : 인스턴스를 생성. 매번 같은 인스턴스임은 보장하지 x 같은 이전에 반환한 인스턴스일 수 있음
- create | newInstance : instance | getInstance 와 같지만 매번 새로운 인스턴스를 생성, 반환을 보장
- getType : 다른 타입의 인스턴스를 생성. 이전에 반환했던 것과 같을 수 있음.
- newType : 다른 타입의 새로운 인스턴스를 생성.
- type: getType과 newType의 간결한 버전