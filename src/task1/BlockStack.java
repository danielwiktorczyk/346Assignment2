package task1;

/**
 * Class BlockStack
 * Implements character block stack and operations upon it.
 *
 * $Revision: 1.4 $
 * $Last Revision Date: 2019/02/02 $
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca;
 * Inspired by an earlier code by Prof. D. Probst

 */
class BlockStack
{
	/**
	 * Task 1 requirements, stackAccessCounter
	 */
	public int stackAccessCounter = 0;
	
	/**
	 * # of letters in the English alphabet + 2
	 */
	public static final int MAX_SIZE = 28;

	/**
	 * Default stack size
	 */
	public static final int DEFAULT_SIZE = 6;

	/**
	 * Current size of the stack
	 */
	public int iSize = DEFAULT_SIZE;

	/**
	 * Current top of the stack
	 */
	public int iTop  = 3;

	/**
	 * stack[0:5] with four defined values
	 */
	public char acStack[] = new char[] {'a', 'b', 'c', 'd', '$', '$'};

	/**
	 * Default constructor
	 */
	public BlockStack()
	{
	}

	/**
	 * Supplied size
	 */
	public BlockStack(final int piSize)
	{


                if(piSize != DEFAULT_SIZE)
		{
			this.acStack = new char[piSize];

			// Fill in with letters of the alphabet and keep
			// 2 free blocks
			for(int i = 0; i < piSize - 2; i++)
				this.acStack[i] = (char)('a' + i);

			this.acStack[piSize - 2] = this.acStack[piSize - 1] = '$';

			this.iTop = piSize - 3;
                        this.iSize = piSize;
		}
	}

	/**
	 * Picks a value from the top without modifying the stack
	 * @return top element of the stack, char
	 */
	public char pick()
	{
		this.stackAccessCounter++; // task 1 requirement
		return this.acStack[this.iTop];
	}

	/**
	 * Returns arbitrary value from the stack array
	 * @return the element, char
	 */
	public char getAt(final int piPosition)
	{
		this.stackAccessCounter++; // task 1 requirement
		return this.acStack[piPosition];
	}

	/**
	 * Standard push operation
	 */
	public void push(final char pcBlock)
	{
		this.stackAccessCounter++; // task 1 requirement
		this.acStack[++this.iTop] = pcBlock;
		System.out.println("Pushed \'" + pcBlock + "\'!"); // task 1 requirement
	}

	/**
	 * Standard pop operation
	 * @return ex-top element of the stack, char
	 */
	public char pop()
	{
		this.stackAccessCounter++; // task 1 requirement
		char cBlock = this.acStack[this.iTop];
		this.acStack[this.iTop--] = '$'; // Leave prev. value undefined 
		System.out.println("Popped \'" + (cBlock=='$'? '*' : cBlock) + "\'!"); // task 1 requirement
		return cBlock;
	}
	
	/**
	 * The method to get iTop
	 */
	public int getITop() {
		//TODO
		return iTop;
	}
	
	/**
	 * The method to get iSize
	 */
	public int getISize() {
		//TODO
		return iSize;
	}
	
	/**
	 * The method to get the stack access counter
	 */
	public int getAccessCounter() {
		return this.stackAccessCounter;
	}
	
	/**
	 * A utility method, true if empty, false otherwise
	 */
	public boolean isEmpty() {
		//TODO
		return (this.iTop == -1);
	}
	
}

// EOF
