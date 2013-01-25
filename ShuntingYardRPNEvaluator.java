import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class ShuntingYardRPNEvaluator {

	// use 0 and 1 instead of true and false for int[] in operators map
	private static final int leftassociative = 0;
	private static final int rightassociative = 1;
	
	// precedence levels, based on wikipedia page
	private static final int level1 = 2;
	private static final int level2 = 3;
	private static final int level3 = 4;
	
	private static final Map<String, int[]> operators = new HashMap<String, int[]>();
	
	/*
	 * Initialize Map with operators. 
	 * key -> operator token
	 * value -> array <precedence, associativity flag>
	 */
	private static void initializeOperators(){
		// use a new array each time
		operators.put("+",new int[] {level1, leftassociative});
		operators.put("-",new int[] {level1, leftassociative});
		operators.put("*",new int[] {level2, leftassociative});
		operators.put("/",new int[] {level2, leftassociative});
		operators.put("^",new int[] {level3, rightassociative});
	}
	
	private static int compareTokens(String token1, String token2){
		return operators.get(token1)[0] - operators.get(token2)[0];
	}
	
	public static ArrayList<String> convertInfixToRPN(String[] tokens){
		ArrayList<String> rpn = new ArrayList<String>();
		Stack<String> operatorStack = new Stack<String>();
		
		// Shunting Yard algorithm as follows from wikipedia
		
		// process all input 
		for(String token : tokens){
			
			// CASE 1: token is operator - before pushing this token, pop off all operators which have higher precedence
			if(operators.containsKey(token)){
				// need to check if top of stack is operator or if its a paren
				while(!operatorStack.empty() && operators.containsKey(operatorStack.peek())){
//					System.out.println("hit");
//					System.out.println(operators.get(token)[1]+ " " + (operators.get(token)[1] == leftassociative));
//					System.out.println(compareTokens(token,operatorStack.peek()));
					if(((operators.get(token)[1] == leftassociative) && compareTokens(token,operatorStack.peek()) <= 0) 
							|| ((operators.get(token)[1] == rightassociative) && compareTokens(token,operatorStack.peek()) < 0)){
						rpn.add(operatorStack.pop()); // pop off operator and push into output
						continue; // restart loop 
					}
					break; // stop evaluation, no more comparisons possible
				}
				// after moving tokens in stack, push the current token!
				operatorStack.push(token);
			}
			// CASE 2: token is ( - push to stack and wait for )
			else if (token.equals("(")){
				operatorStack.push(token);
			}
			// CASE 3: token is ) - push all operators off stack until we find )
			else if (token.equals(")")){
				while(!operatorStack.empty() && !operatorStack.peek().equals("(")){
					rpn.add(operatorStack.pop());
				}
				operatorStack.pop(); // remove the matching parens
			}
			// CASE 4: token is a number - just push to output list
			else{
				rpn.add(token);
			}
//			System.out.println(token);
//			System.out.println(rpn.toString());
//			System.out.println(operatorStack.toString());
		}
		
		// finally push what remains in stack to output
		while(!operatorStack.empty()){
			rpn.add(operatorStack.pop());
		}
		
		return rpn;
	}
	
	public static Double evaluateRPN(ArrayList<String> rpn){
		Stack<Double> stack = new Stack<Double>();
		
		for(String token : rpn){
			// CASE 1: token is an operator.
			// determine which one, and evaluate appropriately then push result back onto stack
			if(operators.containsKey(token)){
				double value = 0;
				if(token.equals("+")){
					value = stack.pop() + stack.pop();
				}
				else if (token.equals("*")){
					value = stack.pop() * stack.pop();
				}
				else if (token.equals("-")){
					Double b = stack.pop();
					Double a = stack.pop();
					value = a - b;
				}
				else if (token.equals("/")){
					Double b = stack.pop();
					Double a = stack.pop();
					if(b == 0)
						throw new IllegalArgumentException("Divide by 0 error!");
					value = a / b;
				}
				else if (token.equals("^")){
					Double b = stack.pop();
					Double a = stack.pop();
					if(b == 0)
						throw new IllegalArgumentException("Divide by 0 error!");
					value = Math.pow(a, b);
				}	
				stack.push(value);
			}
			// CASE 2: token is a value, so convert to integer and push to stack
			else
				stack.push(Double.parseDouble(token));
		}
		
		// at this point, we should only have 1 value left!
		if(stack.size() > 1){
			throw new IllegalArgumentException("Expression has too many values!");
		}
		return stack.pop();
	}
	
	public static void main(String[] args) {
		initializeOperators();
		Scanner scan = new Scanner(System.in);
		String userInput = scan.nextLine();
		if(userInput.equals("help") || userInput.equals("-h")){
			System.out.println("Type in an expression to evaluate. Supported operators are +, -, *, /, and *. Use spaces between each character.");
			System.out.println("Type examples to see sample input and output.");
		}
		else if (userInput.equals("examples")){
			String equation = "( 1 + 2 ) * ( 3 / 4 ) ^ ( 5 + 6 )";
			String[] input = equation.split(" "); // use space as delimiter.

			System.out.println(equation);
			System.out.println(evaluateRPN(convertInfixToRPN(input)));
			
			System.out.println();
			equation = "1 + 2 * 3 / 4 ^ 5 + 6";
			input = equation.split(" ");
			
			System.out.println(equation);
			System.out.println(evaluateRPN(convertInfixToRPN(input)));
		}
		
		String equation = scan.nextLine();
		String[] input = equation.split(" ");
		System.out.println(evaluateRPN(convertInfixToRPN(input)));
	}
}
