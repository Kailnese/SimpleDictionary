import java.net.*;
import java.io.*;
import java.util.*;
public class Client {
	//Kai Liu, UNIMELB Master of IT, ID: 882063
	//DictionaryClient.jar <server-address> <server-port>
	private static Socket client;
	private static Writer writer;
	private static String mean;
	//command = command + word + (meaning)if have
	public static void run(String command, String address, String port) {
		connect(address,port);
		send(command);
		receive();
	}
	public static void connect(String address, String port) {
		int portN = 0;
		try {
			portN = Integer.parseInt(port);
		}catch(Exception e) {
			System.out.println("Port number must be integer");
		}
		try {
			client = new Socket(address, portN);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void send(String msg) {
		try {
			writer = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
			writer.write(msg);
			writer.write("$$\n");
			writer.flush();
			System.out.println("Cliect[port:" + client.getLocalPort() + "] send message success");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void receive() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
			//client.setSoTimeout(15000);
			String temp;
			//System.out.println(br.readLine());
			while((temp = br.readLine())!=null) {
				//System.out.println(temp);
				setMeaning(temp);
			}
			System.out.println("Cliect[port:" + client.getLocalPort() + "] message recived");
			br.close();
			writer.close();
			client.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getMeaning() {
		return this.mean;
	}
	public static void setMeaning(String meaning) {
		mean = meaning;
	}
}
