import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

import org.omg.CORBA.PRIVATE_MEMBER;

/**
 * This class manages the lifetime of commands. You need to modify this class such that it can:
 *     1. validate the command
 *     2. create subcommands
 *     3. execute subcommands
 *     4. suspend/stop/resume the command if you are doing Part 4.
 */
public class CommandManager {
	/**arrOfSubCmd is an array of SubCmd objects parsed by cmdParse*/
	private ArrayList<SubCmd> arrOfSubCmd;
	/** arrOfThreads is an array that stores all started subcommand threads*/
	private ArrayList<Thread> arrOfThreads = new ArrayList<Thread>();
	/** arrOfABQ is an arraylist of ArrayBlockingQueue created based on the no. of cmds. i.e. pipes*/
	private ArrayList<ArrayBlockingQueue<Object>> arrOfABQ = new ArrayList<ArrayBlockingQueue<Object>>();
	/** history stores all the input cmds*/
	protected static ArrayList<String> history = new ArrayList<String>();
	/**oriWorkspace stores the original workspace of the process*/
	protected static String oriWorkspace = System.getProperty("user.dir");	
	/**reDir is used to decide whether there is a ">"(redirection) in the command*/
	private boolean reDir = false;
	/**reDirFile stores the file address to redirect to*/
	private String reDirFile = null;
	/** exit console if set true*/
	public boolean exit = false;
	/** boolean cmdErr, set true if there's any error in the input. false by default  */
	private boolean cmdErr = false;
	
		
	public CommandManager() {
	}
	//end of CommandManager constructor
	
	private ArrayList<SubCmd> cmdParse(String cmd){
		
		ArrayList<SubCmd> s = new ArrayList<SubCmd>();
		String[] cmdSplit = cmd.split("\\|"); //split the input command into subcommands with "|"
		for (int i = 0; i < cmdSplit.length; i++) {
			if (cmdSplit[i].indexOf(">") == -1) {
				s.add(new SubCmd(cmdSplit[i].trim()));
			}else {
				String[] reDirSplit = cmdSplit[i].split(">");
				if (reDirSplit.length == 1) {
					this.cmdErr = true;
					System.out.println("Error: redirection missing argument.");
				}else {
					this.reDir = true;
					cmdSplit[i] = reDirSplit[0].trim();
					this.reDirFile = reDirSplit[1].trim();
					s.add(new SubCmd(cmdSplit[i].trim()));
				}				
			}
		}
		return s;
	}
	
	/*
	 * validates whether the input is valid
	 */
	private String cmdValidation(ArrayList<SubCmd> cmdArr){
		ArrayList<String> validCmd = new ArrayList(Arrays.asList("cat", "cd", "grep", "history", "lc", "ls", "pwd", "sleep", "exit"));
		for (int i = 0; i < cmdArr.size(); i++) {
			if (validCmd.indexOf(cmdArr.get(i).getName()) == -1) {
				cmdErr = true;
				return "Error: invalid command.";
			}
			if (cmdArr.get(i).getName().equals("pwd") && i != 0) {
				cmdErr = true;
				return "Error: invalid pipe order. pwd shouldn't have pipe input.";
			}
			if (cmdArr.get(i).getName().equals("cd") && cmdArr.size() > 1) {
				cmdErr = true;
				return "Error: invalid pipe order. cd shouldn't have pipe input/output.";
			}
			if (cmdArr.get(i).getName().equals("cat")) {
				if (i != 0) {
					cmdErr = true;
					return "Error: invalid pipe order. cat shouldn't have pipe input";
				}else if (cmdArr.get(i).getArgus().size() == 0) {
					cmdErr = true;
					return "Error: cat missing argument.";
				}
			}
			if (cmdArr.get(i).getName().equals("ls") && i != 0) {
				cmdErr = true;
				return "Error: invalid pipe order. ls shouldn't have pipe input";
			}
			if (cmdArr.get(i).getName().equals("lc") && i == 0) {
				cmdErr = true;
				return "Error: invalid pipe order.lc must have pipe input.";
			}
			if (cmdArr.get(i).getName().equals("grep")) {
				if (i == 0) {
					cmdErr = true;
					return "Error: invalid pipe order.grep must have pipe input.";	
				}else if (cmdArr.get(i).getArgus().size() == 0) {
					cmdErr = true;
					return "Error: grep missing argument.";
				}else if (cmdArr.get(i).getArgus().size() != 1) {
					cmdErr = true;
					return "Error: grep invalid argument.";
				}
			}
			if (cmdArr.get(i).getName().equals("history") && i != 0) {
				cmdErr = true;
				return "Error: invalid pipe order. history shouldn't have pipe input";
			}
			if (cmdArr.get(i).getName().equals("sleep") && i != 0) {
				cmdErr = true;
				return "Error: invalid pipe order. sleep shouldn't have pipe input";
			}
			if (cmdArr.get(i).getName().equals("exit")) {
				if (cmdArr.size() > 1) {
					cmdErr = true;
					return "Error: invalid pipe order.exit shouldn't have pipe input/output.";
				}else if (cmdArr.get(i).getArgus().size() != 0) {
					cmdErr = true;
					return "Error: invalid input. exit shouldn't have argument(s).";
				}
			}
		}
		return null;
	}

	public void commandInput(String inputCmd){
		if (!inputCmd.contains("history")) {
			history.add(inputCmd);
		}
		this.arrOfSubCmd = cmdParse(inputCmd);
		if (cmdValidation(arrOfSubCmd) != null) {
			System.out.println(cmdValidation(arrOfSubCmd));
		}
		
		if (!cmdErr) {
			/*
			 * adding queues to the arrOfABQ
			 * each cmd has at least one queue
			 */
			for (int i = 0; i < arrOfSubCmd.size(); i++) {
				arrOfABQ.add(new ArrayBlockingQueue<Object>(10));
			}

			/**
			 * main loop
			 * cmd manipulation
			 */
			for(int i = 0; i < arrOfSubCmd.size(); i++){
				
				SubCmd subCmd = arrOfSubCmd.get(i);
				String subCmdName = arrOfSubCmd.get(i).getName();
				ArrayBlockingQueue<Object> inABQ = null;			
				if (i > 0) {
					inABQ = arrOfABQ.get(i-1);
				} //only creates an input queue when there is more than one cmd
				ArrayBlockingQueue<Object> outABQ = arrOfABQ.get(i);
				

				if (subCmdName.equals("pwd")) {
					if (i != 0) {
						System.out.println("Error: invalid pipe order. pwd shouldn't have pipe input.");
						cmdErr = true;
						break;
					}else {
						SubCmd_Pwd pwd = new SubCmd_Pwd(subCmd, outABQ);
						pwd.start();
						arrOfThreads.add(pwd);
					}				
				}

				else if (subCmdName.equals("cd")) {
					SubCmd_Cd cd = new SubCmd_Cd(subCmd, outABQ);
						cd.start();
						arrOfThreads.add(cd);
				}

				else if (subCmdName.equals("cat")) {
						SubCmd_Cat cat = new SubCmd_Cat(subCmd, outABQ);
						cat.start();
						arrOfThreads.add(cat);
				}

				else if (subCmdName.equals("ls")) {
						SubCmd_Ls ls = new SubCmd_Ls(subCmd, outABQ);
						ls.start();
						arrOfThreads.add(ls);
				}

				else if (subCmdName.equals("lc")) {
						SubCmd_Lc lc = new SubCmd_Lc(subCmd, inABQ, outABQ);
						lc.start();
						arrOfThreads.add(lc);
				}

				else if (subCmdName.equals("grep")) {
						SubCmd_Grep grep = new SubCmd_Grep(subCmd, inABQ, outABQ);
						grep.start();
						arrOfThreads.add(grep);
				}
 
				else if (subCmdName.equals("history")) {
						SubCmd_History history = new SubCmd_History(subCmd, outABQ);
						history.start();
						arrOfThreads.add(history);
				}

				else if (subCmdName.equals("sleep")) {
						SubCmd_Sleep sleep = new SubCmd_Sleep(subCmd, outABQ);
						sleep.start();
						arrOfThreads.add(sleep);
				}

				else if (subCmdName.equals("exit")) {
						exit = true;
						this.history.clear(); // clear all content stored in history, if I don't do this, history will record all the commands input in test mode
						try {
							arrOfABQ.get(arrOfABQ.size()-1).put(Filter.stopSign);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println("REPL exits. Bye.");
						break;
				}
			} //end of main loop 
			
			//output to console or file only if there's no command error
			if (!cmdErr) {
				//output to console if reDir is false
				if (!reDir) {
					while(true) {
						try {
							Object queueElement = arrOfABQ.get(arrOfABQ.size()-1).take();
							if (queueElement != Filter.stopSign) {
								System.out.println(queueElement.toString());
							}
							else {
								// push the sign out of queue
								break;
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					arrOfABQ.get(arrOfABQ.size()-1).clear();
				}
				
				//output to file if reDir is true
				else {
					reDir = false; //reset reDir
					File file = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + reDirFile);
					
						//create file
							try{
							file.createNewFile();
							//write contents of the last BLQ to file
							FileWriter fw = new FileWriter(System.getProperty("user.dir") + System.getProperty("file.separator") + reDirFile);
							BufferedWriter bw = new BufferedWriter(fw);
							while(true) {
								try {
									Object queueElement = arrOfABQ.get(arrOfABQ.size()-1).take();
									if (queueElement != Filter.stopSign) {
										String s = queueElement.toString()+"\n";
										bw.write(s);
									}
									else {
										break;
									}
								} catch (Exception e) {
								}
							}
							bw.close();
							} catch(IOException e){
							}
					arrOfABQ.get(arrOfABQ.size()-1).clear();
				}
			}			
			//join all the started threads
			for (Thread t : arrOfThreads) {
				try {
					t.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			arrOfABQ.clear(); //reset arrOfABQ for next cmd
			arrOfThreads.clear(); //reset arrOfThread for next cmd
		}//end of cmd manipulation
		cmdErr = false; //reset cmdErr for next cmd
	}//end of commandInput	
	
	/*
	 * This is for Part 4
	 */
	public void kill() {
		Thread.currentThread().interrupt();;
	}
}
