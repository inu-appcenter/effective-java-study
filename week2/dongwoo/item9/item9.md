## 아이템 9 try-finally보다는 try-with-resouces를 사용하라

자바에서는 close메서드로 직접 닫아야하는 자원이 많지만 실제로 클라이언트에서 놓치기 쉽다 하지만 그러면 예측할 수 없는 성능 문제가 된다 이런 자원 중 상당수가 finalizer를 활용하고 있지만 믿을만하지 못하다

```java
public static String firstLineOfFile() throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    try {
        return br.readLine();
    } finally {
        br.close();
    }
}
```

보통은 자원이 제대로 닫힘을 보장하는 수단으로 try-finally가 쓰였다 근데 자원이 하나 더 있다면?

```java
public static void read() throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    try {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        try {
             String line = br.readLine();
             bw.write(line);
        } finally {
             bw.close();
        }
    } finally {
        br.close();
    }
}
```

바로 아도겐 코드 나와버린다 그리고 첫번째 에외가 두번쨰 예외을 아예 삼켜버려서  스택 추적 내역에서 디버깅을 어렵게한다 어떻게 해야 할까?

자바 7에서 나온 try-with-resouces를 사용하면 된다 이걸 사용하려면 사용하는 자원이 AutoClosable인터페이스를 구현해야 한다

책 예제의 첫번째 코드를 그대로 구현해보면

```java
public static String inputString() throws IOException {
    try (BufferedReader br = new BufferedReader(new InputStream(System.in))) {
        return br.readLine();
    }
}
```

이렇게 하면 된다 이렇게 하면 읽기 수월할 뿐 아니라 문제를 진단하기도 훨씬 좋다 그래서 꼭 회수해야 하는 자원을 다룰 때에는 try-finally말고 try-with-resouces를 사용하자