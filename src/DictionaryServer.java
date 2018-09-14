import java.util.*;
import java.net.*;
import java.io.*;

//Kai Liu, UNIMELB Master of IT, ID: 882063
public class DictionaryServer {
//DictionaryServer.jar <port> <dictionary-file>
	private static HashMap<String, String> dictionary = null;
	private static PrintStream ps = null;
	private static String filenameT = "";
	private static File filename;
	//Dictionary.txt
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//StartServer(args[0]);
		createDic(args[1]);
		StoreDic();
		waitForConnection(args[0]);
	}
	private static void StoreDic() {
		// TODO Auto-generated method stub
		FileReader read;
		try {
			read = new FileReader(filename.getName());
			BufferedReader br = new BufferedReader(read);
			String s;
			dictionary = new HashMap<String, String>();
			try {
				while((s = br.readLine())!=null) {
					if(s!=null) {
						String[] temp = s.split("@");
						dictionary.put(temp[0], temp[1]);
					}
					//System.out.println("I have got words: ");
					//System.out.println(s);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Dictionary is empty");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Dictionary is empty2222");
		}
	}
	private static void createDic(String name) {
		filenameT = name+".txt";
		filename = new File(filenameT);
		if(!filename.exists()) {
			try {
				filename.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("File "+filename+" is already exist.");
				e.printStackTrace();
			}
		}
	}
	private static void waitForConnection(String port) {
		int portN = 0;
		try {
			portN = Integer.parseInt(port);
		}catch(Exception e) {
			System.out.println("Port number must be integer");
		}
		
		try {
			ServerSocket server = new ServerSocket(portN);
			while(true) {
				System.out.println("Server is open, waiting for client.....");
				Socket client = server.accept();
				new Thread(new ClientRecevie(client)).start();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Connection failed, please try again later");
		}
		
	}

	static class ClientRecevie implements Runnable{
		private Socket socket;
		public ClientRecevie(Socket socket) {
			this.socket = socket;
			System.out.println("Client ["+socket.getPort()+"] connected");
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			handle();
		}
		private synchronized void remove(String word) {
			dictionary.remove(word);
			Scanner input;
			try {
				input = new Scanner(filename);
		        StringBuffer temp=new StringBuffer();
		        while(input.hasNext())
		        {
		            String tem=input.nextLine();
		            String[] t = tem.split("@");
		            //System.out.println(tem);
		            if(t[0].equals(word)) {
		                //System.out.println("I removed: "+tem);
		            }else {
		            	temp.append(tem);
		            }
		        }
		        PrintWriter output=new PrintWriter(filename);
		        output.print(temp);
		        input.close();
		        output.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		private synchronized void add(String word, String meaning) {
			dictionary.put(word, meaning);
			Scanner input;
			try {
				input = new Scanner(filename);
		        StringBuffer temp=new StringBuffer();
		        boolean det = false;
		        while(input.hasNext())
		        {	
		        	String tem = input.nextLine();
		        	String[] t = tem.split("@");
		        	if(t[0].equals(word)) {
		        		temp.append(word+"@"+meaning+"\t\n");
		        		det = true;
		        	}else {
		        		temp.append(tem+"\t\n");
		        	}
		        }
		        if(det == false) {
		        	temp.append(word+"@"+meaning);
		        }
		        PrintWriter output=new PrintWriter(filename);
		        output.print(temp);
		        input.close();
		        output.close();
			}catch(FileNotFoundException e){
				e.printStackTrace();
			}
		}
		private synchronized static String search(String word) {
			String meaning = dictionary.get(word);
			return meaning;
		}
		private void handle() {
			
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				Writer writer = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
				String temp;
				//System.out.println("Im here"+br.readLine());
				int index;
				while((temp = br.readLine())!= null) {
					if((index = temp.indexOf("$$")) != -1) {
						//System.out.println("Client search for "+temp);
						//Output is: Client search for remove apple$$
						String[] s = temp.substring(0, index).split("#");
						String command = s[0];
						String word = s[1];
						String meaning = search(word);
						//System.out.println(meaning);
						//System.out.println(dictionary.get(word));
						//System.out.println("word = "+word+" , command = "+command);
						//Output for remove: word = apple , command = remove
						//Output for search: word = apple , command = search
						switch(command) {
							case "add": 
								if(s.length==3) {
									add(word, s[2]);
									writer.write("Add word succuss");
									writer.flush();
									System.out.println("Client["+socket.getPort()+"]"+" is adding a word");
									break;
								}else {
									writer.write("You didnt have any explanation");
									writer.flush();
									break;
								}
							case "remove": System.out.println("hello");
								if(meaning!=null) {
									remove(word);
									writer.write("Remove word succuss");
									writer.flush();
									System.out.println("Client["+socket.getPort()+"]"+" is removing a word");
									break;
								}else {
									writer.write("Failed: Word does not exits");
									writer.flush();
									break;
								}
							case "search": 
								//System.out.println(meaning);
								//currently null
								if(meaning!=null) {
									System.out.println("Client["+socket.getPort()+"]"+" is searching for word");
									writer.write(meaning);
									writer.flush();
									break;
								}else {
									writer.write("Failed: Word does not exits");
									writer.flush();
									break;
								}
							default: System.out.println("false");
						}
						writer.close();
						break;
					}
					//System.out.println("Client search for "+temp);
				}
				//System.out.println("end connection");
				br.close();
				socket.close();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
