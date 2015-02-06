import java.util.*;

public class MyShell{
	public static void main(String[] args){
		CommandManager cM = new CommandManager();
		Scanner sc = new Scanner(System.in);
		while (!cM.exit) {
			System.out.print("> ");	
			if(sc.hasNextLine()) {
				String userInput = sc.nextLine();
				cM.commandInput(userInput);
			}
		}
		sc.close();
	}
}