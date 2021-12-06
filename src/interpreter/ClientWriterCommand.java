package interpreter;

import java.io.PrintWriter;
import java.util.Map.Entry;

public class ClientWriterCommand implements Command {
	
	PrintWriter out;
	
	public ClientWriterCommand() {
		out = ConnectCommand.out;
	}
	
	@Override
	public void execute(String[] parsed) {
		boolean bindExists = false;
		for (String s : parsed) {
			if (s.equals("bind"))
				bindExists = true;
		}
		if (bindExists) {
			Interpreter.varToPath.put(parsed[0], parsed[3]);
		} else {
			Interpreter.symTable.put(parsed[0], Double.parseDouble(parsed[2]));    //    "x = " 560
				for (Entry<String, String> e : Interpreter.varToPath.entrySet()) {
					if (e.getValue().equals("simX")) {
						Interpreter.symTable.put(e.getKey(), Double.parseDouble(parsed[2]));
					} 
				}
			
			String data = "set " + Interpreter.varToPath.get(parsed[0]) + " " + parsed[2]; // x = bind simX
			if(out != null) {
				out.println(data);
				out.flush();	
			}
		}
	}
}
