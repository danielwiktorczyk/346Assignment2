package task5;

import common.BaseThread;
import common.Semaphore;

// Import (aka include) some stuff.

/**
 * Class BlockManager
 * Implements character block "manager" and does twists with threads.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca;
 * Inspired by previous code by Prof. D. Probst
 *
 * $Revision: 1.5 $
 * $Last Revision Date: 2019/02/02 $

 */
public class BlockManager
{
	public static int Phase1Completion = 0;


	/**
	 * The stack itself
	 */
	private static BlockStack soStack = new BlockStack();

	/**
	 * Number of threads dumping stack
	 */
	private static final int NUM_PROBERS = 4;

	/**
	 * Number of steps they take
	 */
	private static int siThreadSteps = 5;

	/**
	 * For atomicity
	 */
	private static Semaphore mutex = new Semaphore(1);

	/*
	 * For synchronization
	 */

	/**
	 * s1 is to make sure phase I for all is done before any phase II begins
	 */
	private static Semaphore S1 = new Semaphore(0);

	/**
	 * s2 is for use in conjunction with Thread.turnTestAndSet() for phase II proceed
	 * in the thread creation order
	 */
	private static Semaphore S2 = new Semaphore(1);


	// The main()
	public static void main(String[] argv)
	{
		try
		{
			// Some initial stats...
			System.out.println("Main thread starts executing.");
			System.out.println("Initial value of top = " + soStack.getITop() + "."); // modified method name
			System.out.println("Initial value of stack top = " + soStack.pick() + ".");
			System.out.println("Main thread will now fork several threads.");

			/*
			 * The birth of threads
			 */
			AcquireBlock ab1 = new AcquireBlock();
			AcquireBlock ab2 = new AcquireBlock();
			AcquireBlock ab3 = new AcquireBlock();

			System.out.println("main(): Three AcquireBlock threads have been created.");

			ReleaseBlock rb1 = new ReleaseBlock();
			ReleaseBlock rb2 = new ReleaseBlock();
			ReleaseBlock rb3 = new ReleaseBlock();

			System.out.println("main(): Three ReleaseBlock threads have been created.");

			// Create an array object first
			CharStackProber	aStackProbers[] = new CharStackProber[NUM_PROBERS];

			// Then the CharStackProber objects
			for(int i = 0; i < NUM_PROBERS; i++)
				aStackProbers[i] = new CharStackProber();

			System.out.println("main(): CharStackProber threads have been created: " + NUM_PROBERS);
			/*
			 * Twist 'em all
			 */

			ab1.start();
			aStackProbers[0].start();
			rb1.start();
			aStackProbers[1].start();
			ab2.start();
			aStackProbers[2].start();
			rb2.start();
			ab3.start();
			aStackProbers[3].start();
			rb3.start();

			System.out.println("main(): All the threads are ready.");

			/*
			 * Wait by here for all forked threads to die
			 */
			ab1.join();
			ab2.join();
			ab3.join();

			rb1.join();
			rb2.join();
			rb3.join();

			for(int i = 0; i < NUM_PROBERS; i++)
				aStackProbers[i].join();

			// Some final stats after all the child threads terminated...
			System.out.println("System terminates normally.");
			System.out.println("Final value of top = " + soStack.getITop() + "."); // modified method name
			System.out.println("Final value of stack top = " + soStack.pick() + ".");
			System.out.println("Final value of stack top-1 = " + soStack.getAt(soStack.getITop() - 1) + "."); // modified method name
			System.out.println("Stack access count = " + soStack.getAccessCounter()); // printing out counter value as per task 1

			System.exit(0);
		}
		catch(InterruptedException e)
		{
			System.err.println("Caught InterruptedException (internal error): " + e.getMessage());
			e.printStackTrace(System.err);
		}
		catch(Exception e)
		{
			reportException(e);
		}
		finally
		{
			System.exit(1);
		}
	} // main()


	/**
	 * Inner AcquireBlock thread class.
	 */

	static class AcquireBlock extends BaseThread
	{
		/**
		 * A copy of a block returned by pop().
		 * @see BlocStack#pop()
		 */
		private char cCopy;

		public void run()
		{
			System.out.println("AcquireBlock thread [TID=" + this.iTID + "] starts executing.");


			phase1();
			mutex.P();
			Phase1Completion++;
			mutex.V();
			try
			{
				System.out.println("AcquireBlock thread [TID=" + this.iTID + "] requests Ms block.");

				this.cCopy = soStack.pop();

				System.out.println
						(
								"AcquireBlock thread [TID=" + this.iTID + "] has obtained Ms block " + this.cCopy +
										" from position " + (soStack.getITop() + 1) + "." // modified method name
						);


				System.out.println
						(
								"Acq[TID=" + this.iTID + "]: Current value of top = " +
										soStack.getITop() + "." // modified method name
						);

				System.out.println
						(
								"Acq[TID=" + this.iTID + "]: Current value of stack top = " +
										soStack.pick() + "."
						);
			}
			catch(Exception e)
			{
				reportException(e);
				System.exit(1);
			}
			S1.P();
			while(!(this.turnTestAndSet())){
				S2.P();
				S2.V();
			}
			System.out.println("THREAD ID : "+ this.getTID());
			S2.P();
			System.out.println("PASSED ID : "+ this.getTID());
			phase2();
			S2.V();


			System.out.println("AcquireBlock thread [TID=" + this.iTID + "] terminates.");
		}
	} // class AcquireBlock


	/**
	 * Inner class ReleaseBlock.
	 */
	static class ReleaseBlock extends BaseThread
	{
		/**
		 * Block to be returned. Default is 'a' if the stack is empty.
		 */
		private char cBlock = 'a';

		public void run()
		{

			System.out.println("ReleaseBlock thread [TID=" + this.iTID + "] starts executing.");


			phase1();
			mutex.P();
			Phase1Completion++;
			mutex.V();
			try
			{
				if(soStack.isEmpty() == false)
					this.cBlock = (char)(soStack.pick() + 1);


				System.out.println
						(
								"ReleaseBlock thread [TID=" + this.iTID + "] returns Ms block " + this.cBlock +
										" to position " + (soStack.getITop() + 1) + "." // modified method name
						);

				soStack.push(this.cBlock);

				System.out.println
						(
								"Rel[TID=" + this.iTID + "]: Current value of top = " +
										soStack.getITop() + "." // modified method name
						);

				System.out.println
						(
								"Rel[TID=" + this.iTID + "]: Current value of stack top = " +
										soStack.pick() + "."
						);
			}
			catch(Exception e)
			{
				reportException(e);
				System.exit(1);
			}
			S1.P();
			while(!(this.turnTestAndSet())){
				S2.P();
				S2.V();
			}
			System.out.println("THREAD ID : "+ this.getTID());
			S2.P();
			System.out.println("PASSED ID : "+ this.getTID());
			phase2();
			S2.V();


			System.out.println("ReleaseBlock thread [TID=" + this.iTID + "] terminates.");
		}
	} // class ReleaseBlock


	/**
	 * Inner class CharStackProber to dump stack contents.
	 */
	static class CharStackProber extends BaseThread
	{
		public void run()
		{
			phase1();
			mutex.P();
			Phase1Completion++;
			mutex.V();
			try
			{
				for(int i = 0; i < siThreadSteps; i++)
				{
					System.out.print("Stack Prober [TID=" + this.iTID + "]: Stack state: ");

					// [s] - means ordinary slot of a stack
					// (s) - current top of the stack
					char gottenAt;
					for(int s = 0; s < soStack.getISize(); s++) // modified method name
						System.out.print
								(
										(s == BlockManager.soStack.getITop() ? "(" : "[") + // modified method name
												( (gottenAt = BlockManager.soStack.getAt(s)) == '$' ? "*" : gottenAt) + // modified printing of $ to *
												(s == BlockManager.soStack.getITop() ? ")" : "]") // modified method name
								);

					System.out.println(".");

				}
			}
			catch(Exception e)
			{
				reportException(e);
				System.exit(1);
			}
			while(Phase1Completion < 10) {
				mutex.P();
				mutex.V();
			}
			mutex.P();
			if (Phase1Completion == 10) {
				Phase1Completion++;
				System.out.println("-----------------");
				System.out.println("-----------------");
				System.out.println("PHASE 1 COMPLETE");
				System.out.println("-----------------");
				System.out.println("-----------------");
			}
			mutex.V();

			S1.V();
			S1.V();
			while(!(this.turnTestAndSet())){
				S2.P();
				S2.V();
			}
			System.out.println("THREAD ID : "+ this.getTID());
			S2.P();
			System.out.println("PASSED ID : "+ this.getTID());
			phase2();
			S2.V();
		}
	} // class CharStackProber


	/**
	 * Outputs exception information to STDERR
	 * @param poException Exception object to dump to STDERR
	 */
	private static void reportException(Exception poException)
	{
		System.err.println("Caught exception : " + poException.getClass().getName());
		System.err.println("Message          : " + poException.getMessage());
		System.err.println("Stack Trace      : ");
		poException.printStackTrace(System.err);
	}
} // class BlockManager

// EOF