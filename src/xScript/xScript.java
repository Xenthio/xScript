package xScript;
import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.text.*;


public class xScript extends JFrame implements ActionListener {
	public String logic = "(\\\\W)*(>|<|>=|<=|!=|eq|==)";
	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	File f = new File("/bin");
	List<File> files = Arrays.asList(f.listFiles());
	int lastQuoteL = 0;
	int lastQuoteR = 0;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu editMenu;
	private JMenuItem openMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem saveAsMenuItem;
	private JMenuItem cutMenuItem;
	private JMenuItem copyMenuItem;
	private JMenuItem pasteMenuItem;
	public JTextPane txt;
	public String indoc = "nul";
	public File filedoc;
	
    StyleContext cobj = StyleContext.getDefaultStyleContext();
    StyleContext cont = StyleContext.getDefaultStyleContext();
    AttributeSet a = cobj.addAttribute(cobj.getEmptySet(), StyleConstants.Bold, true);
    AttributeSet b = cont.addAttribute(cont.getEmptySet(), StyleConstants.Bold, false);
    final AttributeSet attrKeyWord = cobj.addAttribute(a, StyleConstants.Foreground, Color.decode("#ff0080"));
    final AttributeSet attrString = cobj.addAttribute(a, StyleConstants.Foreground, Color.decode("#ff8000"));
    final AttributeSet attrLogic = cobj.addAttribute(a, StyleConstants.Foreground, Color.decode("#cc00ff"));
    final AttributeSet attrVar = cobj.addAttribute(a, StyleConstants.Foreground, Color.decode("#00ffff"));
    final AttributeSet attrComment = cont.addAttribute(b, StyleConstants.Foreground, Color.gray);
	final AttributeSet attrBlack = cont.addAttribute(b, StyleConstants.Foreground, Color.WHITE);

	String result = files.stream()
		      .map(n -> String.valueOf(n))
		      .collect(Collectors.joining("|"));
	private String[] fonts = ge.getAvailableFontFamilyNames();
	private String templol1 = result.replace("/bin/", "");
	private String templol = templol1.replace("|[", "");
	public String commands = "(\\W)*(echo|bash|ls|let|if|fi|" + templol + ")";
	
	public void updateTitle (String i) {
		setTitle("xScript - " + i);
	}
	
    private xScript () {
    	

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(564, 407);
        setLocationRelativeTo(null);
        
        
        
        DefaultStyledDocument doc = new DefaultStyledDocument() {
        	
        	public void insertString (int offset, String str, AttributeSet a) throws BadLocationException{
        		super.insertString(offset, str, a);
        		update();
        	}
        	
        	public void remove (int offs, int len) throws BadLocationException {
        		super.remove(offs, len);
        		update();
        	}
        	public void update() throws BadLocationException {
        		
        		
        		String text = getText(0, getLength());
        		String[] lines = text.split("\n");
        		for (String line : lines) {
        			String[] words = line.split(" ");
        			Boolean instring = false;
        			Boolean incommnt = false;
        			for (String word : words) {
        				
        				int posL = text.indexOf(word);
        				int posR = posL + word.length();
        				
        				if (incommnt) {
        					setCharacterAttributes(posL, posR, attrComment, false);
        				} else if (word.startsWith("#") || word.matches("#")) {
        					setCharacterAttributes(posL, posR, attrComment, false);
        					incommnt = true;
        				} else if (word.startsWith("\"")) {
        					setCharacterAttributes(posL, posR, attrString, false);
        					instring = true;
        				} else if (word.endsWith("\"")) {
        					setCharacterAttributes(posL, posR, attrString, false);
        					instring = false;
        				} else if (instring) {
        					setCharacterAttributes(posL, posR, attrString, false);
        				} else if (word.matches(commands)) {
        					setCharacterAttributes(posL, posR, attrKeyWord, false);
        				} else if (word.matches(logic)) { //|| word.matches("[") || word.matches("]") || word.matches("[[") || word.matches("]]")) {
        					setCharacterAttributes(posL, posR, attrLogic, false);
        				} else if (word.contains("=") || word.startsWith("$")) {
        					setCharacterAttributes(posL, posR, attrVar, false);
        				
        				} else {
        					setCharacterAttributes(posL, posR, attrBlack, false);
        				}
        				int tick = 0;
        				char[] poop = word.toCharArray();
        				for (char i : poop) {
        					
        					char[] chars = text.toCharArray();
        					chars[posL + tick] = 'z';
        					text = String.valueOf(chars);
        					tick += 1;
        				}
        				System.out.println(text);
        			}
        		}
        	}
        };
        menuBar = new JMenuBar();
        
        // build the File menu
        fileMenu = new JMenu("File");
        openMenuItem = new JMenuItem("Open");
        openMenuItem.addActionListener(this);
        fileMenu.add(openMenuItem);
        saveMenuItem = new JMenuItem("Save");
        saveMenuItem.addActionListener(this);
        fileMenu.add(saveMenuItem);
//        saveAsMenuItem = new JMenuItem("Save As...");
//        saveAsMenuItem.addActionListener(this);
//        fileMenu.add(saveAsMenuItem);

        // build the Edit menu
        editMenu = new JMenu("Edit");
        cutMenuItem = new JMenuItem("Cut");
        copyMenuItem = new JMenuItem("Copy");
        pasteMenuItem = new JMenuItem("Paste");
        editMenu.add(cutMenuItem);
        editMenu.add(copyMenuItem);
        editMenu.add(pasteMenuItem);

        // add menus to menubar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        // put the menubar on the frame
        
        setJMenuBar(menuBar);
        txt = new JTextPane(doc);
        txt.setCaretColor(Color.white);
        List<String> list = Arrays.asList(fonts);
        Font font = new Font("what the poo brain", Font.PLAIN, 12);
        if (list.contains("Courier New")) {
        	font = new Font("Courier New", Font.PLAIN, 12);
        } else if (list.contains("Consolas")) {
        	font = new Font("Consolas", Font.PLAIN, 12);
        } else if (list.contains("Freemono")) {
        	font = new Font("Freemono", Font.PLAIN, 12);
        } else if (list.contains("Hack")) {
        	font = new Font("Hack", Font.PLAIN, 12);
        }
        txt.setFont(font);
        txt.setText("echo \"Hello, world!\"");
        txt.setBackground(Color.decode("#1E1E1E"));
        txt.setBorder(BorderFactory.createLineBorder(Color.decode("#1E1E1E")));
        JScrollPane scroll = new JScrollPane(txt);
        scroll.setBackground(Color.decode("#1E1E1E"));
        scroll.setBorder(BorderFactory.createLineBorder(Color.decode("#1E1E1E")));
        getContentPane().add(scroll);
        setTitle("xScript - New Script");
        setVisible(true);
    }
    
    public static void main (String[] args) {
    	System.setProperty("apple.laf.useScreenMenuBar", "true");
        xScript panel = new xScript();
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		ProcessBuilder processBuilder = new ProcessBuilder();
		String i = e.getActionCommand();
		if (i == "Open") {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			int result = fileChooser.showOpenDialog(getParent());
			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				try {
					indoc = selectedFile.getAbsolutePath();
					filedoc = selectedFile;
					System.out.println(indoc);
					String content = Files.readString(selectedFile.toPath());
					txt.setText(content);
					updateTitle(selectedFile.getName());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		if (i == "Save") {
			if (indoc == "nul") {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				int result = fileChooser.showSaveDialog(getParent());
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					
					try {
						indoc = selectedFile.getAbsolutePath();
						filedoc = selectedFile;
						selectedFile.createNewFile();
						PrintWriter writer = new PrintWriter(selectedFile.getAbsolutePath(), "UTF-8");
						System.out.println(txt.getText());
						writer.print(txt.getText());
						writer.close();
						processBuilder.command("chmod", "+x", selectedFile.getAbsolutePath());
						processBuilder.start();
						updateTitle(selectedFile.getName());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				
				}
			} else {
				try {
					PrintWriter writer = new PrintWriter(indoc, "UTF-8");
					System.out.println(txt.getText());
					writer.print(txt.getText());
					writer.close();
					processBuilder.command("chmod", "+x", indoc);
					processBuilder.start();
					updateTitle(filedoc.getName());
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
				
		
		
	}
	
   
}

