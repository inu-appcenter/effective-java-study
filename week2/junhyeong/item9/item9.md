# try-finally보다는 try-with-resources를 사용하라 - item9

close() 메서드를 호출해서 직접 닫아줘야 할 때 (InputStream, java.sql.Connection)

전통적으로 try-finally가 쓰였다.

```java
static String firstLineOfFile(String path) throws IOException {
	BufferedReader br = new BufferedReader(new FileReader(path));
	try {
		return br.readLine();
	} finally {
		br.close();
	}
}

static void copy(String src, String dst) throws IOException {
	InputStream in = new FileInputStream(src);
	try {
		OutputStream out = new FileOutputStream(dst);
			try {
				byte[] buf = new byte[BUFFER_SIZE];
				int n;
				while ((n = in.read(buf)) >= 0)
					out.write(buf, 0, n);
			} finally {
				// out 이 null이라면..? NPE
				if(out != null){
					out.close();
				}
			}
	} finally {
		in.close();
	}
}
```

그러나 자원이 두개 이상이면 너무 지저분하다.

또한,  close() 에서 예외가 발생하면, write() 예외를 집어 삼키므로 디버깅이 힘들어진다. (먼저 발생한 예외를 표현할 수는 있지만 마찬가지로 지저분하다.)

```java
static String firstLineOfFile(String path) throws IOException {
	try (BufferedReader br = new BufferedReader(
			new FileReader(path))) {
		return br.readLine();
	}
}

static void copy(String src, String dst) throws IOException {
	try (InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst))
 {
		byte[] buf = new byte[BUFFER_SIZE];
		int n;
		while ((n = in.read(buf)) >= 0)
		out.write(buf, 0, n);
	}
}
```

AutoCloseable 인터페이스를 구현하면 try-with-resources 를 사용해서 close() 메서드를 실행시킨다.