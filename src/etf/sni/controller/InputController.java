package etf.sni.controller;

import java.util.ArrayList;
import java.util.List;

//Using Singletone pattern
public class InputController {

	private static final List<String> sqlInjectionExpressinos = new ArrayList<>();
	private static final List<String> xssAttackExpressions = new ArrayList<>();
	
	private static InputController inputController;
	
	private InputController() {
		addSqlInjectionExpressions();
		addxssAttackExpressions();
	}

	public static InputController getInputController() {
		if(inputController == null)
			inputController = new InputController();
		return inputController;
	}
	
	private static void addSqlInjectionExpressions() {
		//(?i) - starts case-insensitive mode
		//(?-i) - stops case-insensitive mode
		sqlInjectionExpressinos.add("(?i)(.*drop.*((table)|(schema)|(column)|(constraint)).*)(?-i)");
		sqlInjectionExpressinos.add("(?i)(.*insert.*into.*)(?-i)");
		sqlInjectionExpressinos.add("(?i)(.*select.*from.*)(?-i)");
		sqlInjectionExpressinos.add("(?i)(.*update.*set.*)(?-i)");
		sqlInjectionExpressinos.add("(?i)(.*delete.*from.*)(?-i)");
		sqlInjectionExpressinos.add("(?i)(.*grant.*on.*to.*)(?-i)");
		sqlInjectionExpressinos.add("(?i)(.*call.*().*)(?-i)");
		sqlInjectionExpressinos.add("(?i)(.*or.*=.*)(?-i)");
	}
	
	private static void addxssAttackExpressions() {
		xssAttackExpressions.add("<.*>.*<\\/.*>");
		xssAttackExpressions.add("<.*/>");
		xssAttackExpressions.add("<.*>");
		xssAttackExpressions.add("(?i)(redirect\\..*)(?-i)");
		xssAttackExpressions.add("(?i)(document\\..*)(?-i)");
		xssAttackExpressions.add("(?i)(javascript.*)(?-i)");
		xssAttackExpressions.add("(?i)(eval\\(.*)(?-i)");
		xssAttackExpressions.add("(?i)(alert\\(.*)(?-i)");
	}
	
	// 0 - secure input
	// 1 - SqlInjection detected
	// 2 - XSS detected
	public int checkInput(List<String> messages) {
		if (!isSqlInjectionSafe(messages)) {
			return 1;
		}
		else if (!isXssSafe(messages)) {
			return 2;
		}
		return 0;
	}
	
	public boolean isSafeInput(List<String> messages, List<String> expressions) {
		boolean safe = true;
		boolean flag = false;
		
		for(String message : messages) {
			for(String regex : expressions) {
				if(message.matches(regex)) {
					safe = false;
					flag = true;
					break;
				}
			}
			if(flag) {
				break;
			}
		}
		
		return safe;
	}
	
	public boolean isSqlInjectionSafe(List<String> messages) {
		return isSafeInput(messages, sqlInjectionExpressinos);
	}
	
	public boolean isXssSafe(List<String> messages) {
		return isSafeInput(messages, xssAttackExpressions);
	}
		
}
