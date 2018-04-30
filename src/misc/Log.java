package misc;

public class Log {
	private static String succ = "[SUCC] ";
	private static String fail = "[FAIL] ";
	private static String info = "[INFO] ";
	
	public static void success(String s){
		System.out.println(succ + s);
	}
	public static void failure(String s){
		System.out.println(fail + s);
	}
	public static void info(String s){
		System.out.println(info + s);
	}
}
