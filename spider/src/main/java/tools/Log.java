package tools;

public class Log {
	public static void println(String string) {
		System.out.println(string);
	}
	public static void info(String string) {
		//println("info\t"+string);
	}
	public static void errer(String string) {
		println("error\t"+string);
	}
}
