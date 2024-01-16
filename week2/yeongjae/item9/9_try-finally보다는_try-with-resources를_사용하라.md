# 9_try-finally보다는_try-with-resources를_사용하라

## try-finally 사용

```java
static String firstLineOfFile(String path) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(path));
    try {
        return br.readLine();
    } finally {
        br.close();
    }
}
```

```java
static void copy(String src, String dst) throws IOException {
    InputStream in = new FileInputStream(src);
    try {
        OutputStream out = new FileOutputStream(dst);
        try {
            byte[] buf = new byte[10];
            int n;
            while ((n = in.read(buf)) >= 0) {
                out.write(buf, 0, n);
            }
        } finally {
            out.close();
        }
    } finally {
        in.close();
    }
}
```

자원이 하나일때는 코드 가독성이 나쁘지 않다. 

하지만 두번째 예시처럼 자원이 두개가되면 인덴트가 2가 되면서 코드 가독성이 확 떨어지는 느낌을 받는다.

문제가 한가지 더 있다.

만약 firstLineOfFile 메서드에서 readLine과 close 호출 양쪽에서 예외가 발생하면 readLine에서 발생한 예외는 기록되지 않고 close 에서 발생한 예외만 기록된다.

스택 추적 내역에 첫 번째 예외에 관한 정보는 남지 않게 되어 디버깅을 어렵게한다.

## try-with-resources

이러한 문제는 자바 7에서 생긴 try-with-resources로 해결되었다.

```java
static String firstLineOfFile2(String path) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        return br.readLine();
    }
}

static void copy2(String src, String dst) throws IOException {
    try (InputStream in = new FileInputStream(src);
         OutputStream out = new FileOutputStream(dst)) {
        byte[] buf = new byte[10];
        int n;
        while ((n = in.read(buf)) >= 0) {
            out.write(buf, 0, n);
        }
    }
}
```

try-with-resources를 이용한 firstLineOfFile 메서드에서 readLine과 close 호출 양쪽에서 예외가 발생하면
readLine에서 발생한 예외가 출력되고 close에서 발생한 예외는 **숨겨진다.** 

try-finally와 다르게 예외가 그냥 버려지지않고 suppressed 꼬리표를 달고 출력된다.

또한 try-with-resources에서도 catch 절을 사용할 수 있기 때문에 앞으로는 try-finally 보다는 try-with-resources를 사용해보자.
