# 1주차 정리

## 아이템 1 : 생성자 대신 정적 팩터리 메서드를 고려하라. (구준형)
책에서 언급하는 정적 팩터리 메서드의 장점 외에 추가로 장점을 언급하고 싶다.
- Stream과 같이 생성자를 사용한 반복이 활용되는 경우 매개변수를 줄이는 형태의 정적 팩터리 메서드가 이점이 있다.
```java
    public List<UserResponseDto> readAll(){
        return userRepository.findAll().stream()
                .map(user -> user.toDto(user, readReminderList(user.getUserNo()), readFriendList(user.getUserNo()), readFavorList(user.getUserNo()), readAnniversaryList(user.getUserNo()), getGiftInfo(user.getUserNo())))
                .collect(Collectors.toList());
    }
    
    // 정적 팩터리 메서드 from (매개변수 1개로 인스턴스화) 를 구현했다고 가정
public List<UserResponseDto> readAll(){
        return userRepository.findAll().stream()
        .map(User::from)
        .collect(Collectors.toList());
        }
```
이렇게 가독성 측면에서 도움이 되는 것 같다고 생각된다.

---

책에서 언급하는 정적 팩터리 메서드의 단점은 다음과 같았다.
- 정적 팩터리 메서드만 있다면 하위 클래스를 만들 수 없다
- 정적 팩터리 메서드는 프로그래머가 찾기 어렵다

이러한 단점에 대해서 우리의 사례에서는 단점으로 작용하지 않을 수도 있을 까라는 고민을 했다.

내가 백엔드 개발을 하면서 상속을 주로 사용했던 경우는 이때만 있었다.  
로그인 결과와 회원 가입 결과에 대한 응답을 구성할 때 공통적인 부분에 대해 상속을 통해 구현한 사례이다.
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignUpResultDto {

    private Boolean success;

    private int code;

    private String msg;

}

---

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignInResultDto extends SignUpResultDto {

    private String token;

    @Builder
    public SignInResultDto(Boolean success, int code, String msg, String token) {
        super(success, code, msg);
        this.token = token;
    }

}
```
(해당 예제는 책에서 제공하는 부분으로 어노테이션이 상당히 거슬리지만, 예시기 때문에 넘어간다...)

이런 사례에서는 private 생성자 / 정적 팩터리 메서드로 구현하는 경우 문제가 발생한다.
 
> Example Static Factory Method
```java
public static SignUpResultDto from(StatusEnum status) {
    return new SignUpResultDto(status.isSuccess, status.code, status.msg);
}
```

하지만, 이러한 사례가 거의 없었던 만큼 첫 번째 단점이 단점처럼 작용하지 않을 것 같다는 생각도 들었다.  
(응답 DTO 내에서 공통적인 부분에 대해 상속으로 처리하는 아이디어는 좋지만, 생각보다 엔티티별 응답 DTO를 여럿 구성하는 것이 아니라면 상속을 활용하기엔 애매한 부분이 있다.)

그리고 두 번째 단점으로 언급되는 프로그래머가 찾기 어렵다는 점은 책에서 언급한 네이밍 규칙을 최대한 따르는 방식을 적용한다면 개선할 수 있다고 생각된다.  
하지만, 첫 번째 단점처럼 완전히 단점처럼 작용하지 않는다라고 확답을 하기는 어려울 것 같다.  
(of, valueOf와 같은 네이밍 규칙을 무의식적으로 사용하는 경우가 생각보다 많기 때문이다.)

## 아이템 2 : 생성자에 매개변수가 많다면 빌더를 고려하라. (이영재)
책에서 언급되는 여러 가지 패턴(점층적 생성자 패턴, 자바빈즈 패턴)에 비해 빌더를 사용하는 것이 상당히 좋다고 생각한다.  
lombok의 Builder 어노테이션이 진입장벽을 낮추는 데 한 몫 했다고 생각한다.  
실제로 Spring Boot로 개발을 할 때 무의식적으로 어노테이션만 붙여서 빌더 패턴을 활용했는 데, 실제로 구현한 코드를 봤을 때 신기했다.

## 아이템 3 : private 생성자나 열거 타입으로 싱글톤임을 보증하라. (이주원)
Singleton이 무엇이고 어떻게 구현하는 지에 대해 알아둘 필요가 있다.  
하지만 우리가 Spring Boot로 개발을 할 때 직접적으로 Singleton을 구현할 사례가 있는 지는 모르겠다.

그래서 나는 책에서 언급한 Singleton을 구현하는 방식 (private, enum, static factory method)에 대해 숙지하고, 기존에 Singleton 생성 방식 6가지를 다시 한 번 공부해야겠다는 생각이 들었다.

## 아이템 4 : 인스턴스화를 막으려거든 private 생성자를 사용하라. (김동우)
private 생성자를 사용하여 인스턴스화를 막는 사례를 봤을 때 딱 2가지가 생각이 났다.
- 정적 팩터리 메서드를 구현하고 기존의 매개변수가 있는 생성자를 가리는 경우
- 유틸리티에 해당하는 메서드를 구현한 후 이를 클래스로 분리한 경우

해당 아이템에서 언급하는 부분은 후자에 해당하여 날짜 출력 형식 지정 등과 같은 경우 private 생성자로 인스턴스화를 막고, 정적 메서드로 해당 유틸리티를 사용하게 끔 구현하면 어떨까 생각한다.