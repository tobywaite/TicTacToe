/* 
 * Tic Tac Toe ÐÊA simple machine learning simulator.
 * 
 * Class: ParameterException
 * 
 * ParameterException is thrown if an user specifies an invalid parameter when calling the TicTacToe program.
 * 
 * Author: 	Toby Waite
 * Contact: toby.waite@gmail.com 
 * Updated: November 16th, 2010.
 */

class ParameterException extends Exception {
	public ParameterException(String message) {
		super(message);
	}
}