import java.util.ArrayList;

/**
 * This class if for parsing single command and the arguments that follows it.
 * consists of name(string) and arguments(arraylist)
 */

public class SubCmd {
	private String subCmdName;
	private ArrayList<String> subCmdArgList = new ArrayList<String>();
	
	// constructor splits the name of the cmd from the arguments.
	public SubCmd(String subCmd){
		String[] subCmdSplit = subCmd.split("\\s+"); 
		this.subCmdName = subCmdSplit[0];
		for (int i = 1; i < subCmdSplit.length; i++) {
			subCmdArgList.add(subCmdSplit[i]);
		}
	}
	
	public String getName(){
		return this.subCmdName;
	}
	
	public ArrayList<String> getArgus(){
		return this.subCmdArgList;
	}
	
}
