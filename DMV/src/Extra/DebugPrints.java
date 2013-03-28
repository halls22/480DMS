package Extra;

/**
 * if you are writing a print statement to help you debug,
 * call this debug(). You can turn them all off at the same 
 * time by setting print_debug_statements=false.
 */
public class DebugPrints {

	public static boolean print_debug_statements = false;
	
	public static void debug(String s){
		if(print_debug_statements)
			System.out.println(s);// hello?cgd
	}
	
	public static void debug(int[] pixels) {
		if(print_debug_statements)
			return;
		for(int i=0; i<pixels.length; i++){
			debug(Integer.toString(pixels[i]));
		}
	}
	
	public static void printAndExit(String s, int i){
		System.out.println(s);
		System.exit(i);
	}
	
	// having trouble finding a file? to find your current path:
	// debug(System.getProperty("user.dir"));
	
	// TODO: determine the OS and make a call to the terminal
}

