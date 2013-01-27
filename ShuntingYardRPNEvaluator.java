import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class ShuntingYardRPNEvaluator {

	// use 0 and 1 instead of true and false for int[] in operators map
	private static final int leftassociative = 0;
	private static final int rightassociative = 1;
	
	// precedence levels, based on Wikipedia page
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
		
		// Shunting Yard algorithm as follows from Wikipedia
		
		// process all input 
		for(String token : tokens){
			
			// CASE 1: token is operator - before pushing this token, pop off all operators which have higher precedence
			if(operators.containsKey(token)){
				// need to check if top of stack is operator or if its a paren
				while(!operatorStack.empty() && operators.containsKey(operatorStack.peek())){
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
				// addition
				if(token.equals("+")){
					value = stack.pop() + stack.pop();
				}
				// multiplication
				else if (token.equals("*")){
					value = stack.pop() * stack.pop();
				}
				// subtraction
				else if (token.equals("-")){
					Double b = stack.pop();
					Double a = stack.pop();
					value = a - b;
				}
				// division
				else if (token.equals("/")){
					Double b = stack.pop();
					Double a = stack.pop();
					if(b == 0)
						throw new IllegalArgumentException("Divide by 0 error!");
					value = a / b;
				}
				// power
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
	
	private static void examples(){
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
	
	private static class Equation{
		String equation;
		Double solution;
		
		public Equation(String e, Double s){
			this.equation = e;
			this.solution = s;
		}
		
		public String getEquation(){return equation;}
		public Double getSolution(){return solution;}
		
	}
	
	/*
	 * Simple testing function to run test cases.
	 */
	private static void tests(){
		System.out.println("Running test equations!");
		
		ArrayList<Equation> equations = new ArrayList<Equation>();
		
		equations.add(new Equation("1 + 2",3.0));
		equations.add(new Equation("1 - 2",-1.0));
		equations.add(new Equation("2 - 1",1.0));
		equations.add(new Equation("4 * 3",12.0));
		equations.add(new Equation("3 * 4",12.0));
		equations.add(new Equation("10 / 5",2.0));
		equations.add(new Equation("5 / 10",0.5));
		equations.add(new Equation("2 ^ 5",32.0));
		equations.add(new Equation("2 ^ ( 5 - 3 )",4.0));
		equations.add(new Equation("2 ^ 5 - 3",29.0));
		equations.add(new Equation("4 - 2 ^ 3",-4.0));
		equations.add(new Equation("( 4 - 2 ) ^ 3",8.0));
		equations.add(new Equation("( 6 + 2 ) ^ ( 2 - 3 )",0.125));
		equations.add(new Equation("6 + 2 ^ 2 - 3 * 5 / 5",7.0));
		equations.add(new Equation("( 6 + 2 ) ^ ( ( 2 - 3 ) * 5 / 5 )",0.125));
		
		int numTests = 0; 
		int passedTests = 0;
		
		// run each equation through evaluator!
		for(Equation equation: equations){
			numTests++;
			
			String[] input = equation.getEquation().split(" "); 

			System.out.print(equation.getEquation() + " == " + equation.getSolution());
			
			Double shuntingValue = evaluateRPN(convertInfixToRPN(input));
			if(shuntingValue.compareTo(equation.getSolution())== 0){
				System.out.println(" \tPASSED! (Calculated: "+shuntingValue+")");
				passedTests++;
			}
			else{
				System.out.println(" \tFAILED! (Calculated: "+shuntingValue+")");
			}
	
		}
		System.out.printf("Passed %d/%d tests.\n",passedTests,numTests);
	}
	
	public static void main(String[] args) {
		initializeOperators();

		Scanner scan = new Scanner(System.in);
		String userInput = scan.nextLine();
		
		if(userInput.equals("help") || userInput.equals("-h")){
			System.out.println("Type in an expression to evaluate. Supported operators are +, -, *, /, and *. Use spaces between each character.");
			System.out.println("Type examples to see sample input and output.");
			System.out.println("Type tests to see results of various inputs.");
		}
		else if (userInput.equals("examples")){
			examples();
		}
		else if (userInput.equals("tests")){
			tests();
		}
		else{
			//String equation = scan.nextLine();
			String equation = userInput;
			String[] input = equation.split(" ");
			System.out.println(evaluateRPN(convertInfixToRPN(input)));
		}
	}
}
