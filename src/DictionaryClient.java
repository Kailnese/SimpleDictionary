import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

//Kai Liu, UNIMELB Master of IT, ID: 882063
public class DictionaryClient {
	private JFrame frame;

	/**
	 * Launch the application.
	 */
	private static String address;
	private static String port;
	private static Client dc;
	public static void main(String[] args) {
		address = args[0];
		port = args[1];
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DictionaryClient window = new DictionaryClient();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * Create the application.
	 */
	public DictionaryClient() {
		initialize();
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private int width = 500;
	private int height = 300;
	private int X = 700;
	private int Y = 300;
	private JTextField text;
	private JLabel Defination;
	private JTextField textField;
	
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(X, Y, width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
				
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 484, 261);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
				
		this.Defination = new JLabel();
		Defination.setBounds(29, 136, 75, 22);
		panel.add(Defination);
				
		JButton Add = new JButton("Add");
		Add.setBounds(29, 27, 93, 35);
		panel.add(Add);
				
		JButton Search = new JButton("Search");
		Search.setBounds(196, 27, 93, 35);
		panel.add(Search);
				
		JButton Remove = new JButton("Remove");
		Remove.setBounds(364, 27, 93, 35);
		panel.add(Remove);
				
		String info = "Enter a word";
		this.text = new JTextField();
		this.text.setText(info);
		this.text.addFocusListener(new Focus(info,this.text));
		this.text.setBounds(185, 72, 119, 35);
		panel.add(text);
		this.text.setColumns(10);
		
		textField = new JTextField();
		textField.setBounds(29, 136, 428, 115);
		panel.add(textField);
		textField.setColumns(10);
				
		Add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//Defination.setText(text.getText());
				int count = 0;
				Client dic = new Client();
				String temp = text.getText();
				//Limit entering word without space
				for(int a = 0; a<text.getText().length(); a++) {
					if(temp.charAt(a)!=' ') {
						count++;
					}
				}
				if(count==temp.length()) {
					dic.run("search#"+text.getText(), address, port);
					if(dic.getMeaning().equals("Failed: Word does not exits")) {
						dic.run("add#"+text.getText()+"#"+textField.getText(), address, port);
						if(dic.getMeaning().equals("You didnt have any explanation")) {
							JOptionPane.showMessageDialog(panel, "Enter a word with explanation!", "Notice",JOptionPane.WARNING_MESSAGE);
						}else {
							JOptionPane.showMessageDialog(panel, "Add success!", "Notice",JOptionPane.WARNING_MESSAGE);
						}
					}else {
						int n = JOptionPane.showConfirmDialog(panel, "The word you want add is already exist, do you want override?", "Warning",JOptionPane.YES_NO_OPTION);
						if(n==0) {
							//System.out.println("I want remove");
							dic.run("add#"+text.getText()+"#"+textField.getText(), address, port);
							if(dic.getMeaning().equals("You didnt have any explanation")) {
								JOptionPane.showMessageDialog(panel, "Enter a word with explanation!", "Notice",JOptionPane.WARNING_MESSAGE);
							}else {
								JOptionPane.showMessageDialog(panel, "Add success!", "Notice",JOptionPane.WARNING_MESSAGE);
							}
						}else {
							//System.out.println("I dont want remove");
							JOptionPane.showMessageDialog(panel, "You canceled", "Notice",JOptionPane.WARNING_MESSAGE);
						}
					}
				}else {
					JOptionPane.showMessageDialog(panel, "Enter a word without any space", "Notice",JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		Search.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int count = 0;
				String temp = text.getText();
				Client dic = new Client();
				//Limit entering word without space
				for(int a = 0; a<text.getText().length(); a++) {
					if(temp.charAt(a)!=' ') {
						count++;
					}
				}
				if(count==temp.length()) {
					dic.run("search#"+text.getText(), address, port);
					if(dic.getMeaning().equals("Failed: Word does not exits")) {
						JOptionPane.showMessageDialog(panel, "Word does not exist", "Notice",JOptionPane.WARNING_MESSAGE);
					}else {
						textField.setText(dic.getMeaning());
					}
					//}
					//DictionaryClient
				}else {
					JOptionPane.showMessageDialog(panel, "Enter a word without any space", "Notice",JOptionPane.WARNING_MESSAGE);
				}
			}
					
		});
		Remove.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int count = 0;
				String temp = text.getText();
				Client dic = new Client();
				//Limit entering word without space
				for(int a = 0; a<text.getText().length(); a++) {
					if(temp.charAt(a)!=' ') {
						count++;
					}
				}
				//Warning user if he really want delete or not
				if(count==temp.length()) {
					int n = JOptionPane.showConfirmDialog(panel, "Are you sure u want remove?", "Warning",JOptionPane.YES_NO_OPTION);
					if(n==0) {
						dic.run("remove#"+text.getText(), address, port);
						if(dic.getMeaning().equals("Failed: Word does not exits")) {
							JOptionPane.showMessageDialog(panel, "Failed: Word does not exits","Notice",JOptionPane.WARNING_MESSAGE); 
						}else {
							JOptionPane.showMessageDialog(panel, "Remove Success", "Notice",JOptionPane.WARNING_MESSAGE); 
						}
						
					}
				}else {
					JOptionPane.showMessageDialog(panel, "Enter a word without any space", "Notice",JOptionPane.WARNING_MESSAGE);
				}
			}		
		});
	}
}
class Focus implements FocusListener {
    String info;
    JTextField jtf;
    public Focus(String info, JTextField jtf) {
        this.info = info;
        this.jtf = jtf;
    }
    @Override
    public void focusGained(FocusEvent e) {
        String temp = jtf.getText();
        if(temp.equals(info)){
            jtf.setText("");
        }
    }
    @Override
    public void focusLost(FocusEvent e) {
        String temp = jtf.getText();
        if(temp.equals("")){
            jtf.setText(info);
        }
    }
}
