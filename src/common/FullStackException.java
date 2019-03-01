package common;

public class FullStackException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public FullStackException(String message) {
		super(message);
	}
	
	public FullStackException() {
		super("Full Stack !!!");
	}

}
