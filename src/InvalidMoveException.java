/* 
 * Tic Tac Toe ÐÊA simple machine learning simulator.
 * 
 * Class: InvalidMoveException
 * 
 * InvalidMoveException is thrown if an agent attempts to execute an invalid move.
 * 
 * Author: 	Toby Waite
 * Contact: toby.waite@gmail.com 
 * Updated: November 16th, 2010.
 */

public class InvalidMoveException extends Exception {
	public InvalidMoveException(String message){
		super(message);
	}
}