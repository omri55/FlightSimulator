package interpreter;
import java.util.Arrays;

public class Parser {
	
	public int parse(String[] textLines) {
		boolean flag = false;
		for (int i = 0; i < textLines.length; i++) {
			String[] parsed = textLines[i].split(" ");

			if (parsed[0].equals("while") || parsed[0].equals("if")) {
				flag = true;
				Interpreter.cp.execute(parsed);
				continue;
			}
			if (flag) {
				if (parsed[0].equals("}")) {
					flag = false;
					Interpreter.cp.execute(parsed);
				}
				Interpreter.cp.execute(parsed);
				continue;
			}

			if (Interpreter.cmdsMap.containsKey(parsed[0]) == true) { // if the line starts with a known command
				Command c = Interpreter.cmdsMap.get(parsed[0]);
				if (c instanceof OpenServerCommand) {
					String[] subWords = Arrays.copyOfRange(parsed, 1, parsed.length);
					c.execute(subWords);
				}
//				else if (c instanceof ConditionParser) {
//					while(!parsed[0].equals("}"))
//					c.execute(parsed);
//				}
				else {
					String[] subWords = Arrays.copyOfRange(parsed, 1, parsed.length);
					c.execute(subWords);
				}

			} else if (Interpreter.symTable.containsKey(parsed[0]) == true) // if the line starts with a known variable
			{ // for example throttle = 1
				Command cc = new ClientWriterCommand();
				
				cc.execute(parsed);
			}
			else if (parsed[0].contains("=")) {
				Command ec = new EqualsCommand();
				ec.execute(parsed);
			}
		}
		return ReturnCommand.ret;
	}
}
