package common;

public class EmptyStackException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public EmptyStackException(String message) {
		super(message);
	}
	
	public EmptyStackException() {
		super("Empty Stack !!!");
	}

}
