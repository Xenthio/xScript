package xScript;
import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.text.*;


public class Test extends JFrame {
	public String logic = "(\\\\W)*(>|<|>=|<=|!=|eq|==|=)";
	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	File f = new File("/bin");
	List<File> files = Arrays.asList(f.listFiles());
	
	String result = files.stream()
		      .map(n -> String.valueOf(n))
		      .collect(Collectors.joining("|"));
	private String[] fonts = ge.getAvailableFontFamilyNames();
	private String templol1 = result.replace("/bin/", "");
	private String templol = templol1.replace("|[", "");
	public String commands = "(\\W)*(echo|bash|ls|let|if|fi|" + templol + ")";
	
    public Test () {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);

        StyleContext cobj = StyleContext.getDefaultStyleContext();
        StyleContext cont = StyleContext.getDefaultStyleContext();
        AttributeSet a = cobj.addAttribute(cobj.getEmptySet(), StyleConstants.Bold, true);
        AttributeSet b = cont.addAttribute(cont.getEmptySet(), StyleConstants.Bold, false);
        final AttributeSet attrCommands = cobj.addAttribute(a, StyleConstants.Foreground, Color.decode("#ff0080"));
        final AttributeSet attrStrings = cobj.addAttribute(a, StyleConstants.Foreground, Color.decode("#ff8000"));
        final AttributeSet attrLogic = cobj.addAttribute(a, StyleConstants.Foreground, Color.decode("#00ffff"));
        final AttributeSet attrBlack = cont.addAttribute(b, StyleConstants.Foreground, Color.WHITE);
        DefaultStyledDocument doc = new DefaultStyledDocument() {
            public void insertString (int offset, String str, AttributeSet a) throws BadLocationException {
                super.insertString(offset, str, a);
                System.out.println(templol);
                System.out.println(files);
                String text = getText(0, getLength());

                int before = findLastNonWordChar(text, offset);
                if (before < 0) before = 0;
                int after = findFirstNonWordChar(text, offset + str.length());
                int wordL = before;
                int wordR = before;

                int before2 = findLastQuote(text, offset);
                if (before2 < 0) before2 = 0;
                int after2 = findFirstQuote(text, offset + str.length());
                int wordL2 = before;
                int wordR2 = before;


            	if (text.contains("\"")) {
                    while (wordR2 <= after2 - 1) {

                        if (wordR2 == after2 || String.valueOf(text.charAt(wordR2)).matches("\"")) {

                        	System.out.println(text.charAt(wordL2) + String.valueOf(wordL2));
                        	System.out.println(text.charAt(wordR2) + String.valueOf(wordR2));
                        	System.out.println(after2);

                            if (text.charAt(wordL2) == '"' && text.charAt(wordR2) == '"') {
                            	System.out.println("Found a match! at: " + text.charAt(wordL2) + wordL2 + ", " + text.charAt(wordR2) + wordR2);
                                setCharacterAttributes(wordL2, wordR2, attrStrings, false);
                                break;
                            } else {
                                setCharacterAttributes(wordL2, wordR2, attrBlack, false);
                            }
                            wordL2 = wordR2;

                        }
                        System.out.println("wordR2 = " + wordR2);
                        System.out.println("TextLength = " + text.length());

                        if (!((wordR2 + 1) == text.length())) {
                        	wordR2 += 1;
                        } else {
                        	break;
                        }
                        System.out.println("wordR2 = " + wordR2);
                        System.out.println("pass");
                    }

            	}



                while (wordR <= after) {

                    if (wordR == after || String.valueOf(text.charAt(wordR)).matches("\\W")) {

                        if (text.substring(wordL, wordR).matches(commands)) {
                            setCharacterAttributes(wordL, wordR - wordL, attrCommands, false);
                        	System.out.println(wordL);
                        	System.out.println(wordR - wordL);
                        } else if (text.substring(wordL, wordR).matches(logic)) {
                        	setCharacterAttributes(wordL, wordR - wordL, attrLogic, false);
                        } else if (text.charAt(wordL2) == '"' && text.charAt(wordR2) == '"') {

                        } else{
                            setCharacterAttributes(wordL, wordR - wordL, attrBlack, false);
                        }
                        wordL = wordR;

                    }
                    wordR++;
                }
            }

            public void remove (int offs, int len) throws BadLocationException {
                super.remove(offs, len);

                String text = getText(0, getLength());
                int before = findLastNonWordChar(text, offs);
                if (before < 0) before = 0;
                int after = findFirstNonWordChar(text, offs);

                int before2 = findLastQuote(text, offs);
                if (before2 < 0) before = 0;
                int after2 = findFirstQuote(text, offs);

            	if (text.contains("\"")) {
//                    if (after2 > text.length()) {
//                    	System.out.println("");
//                    	while (after2 > text.length()) {
//                    		after2 -= 1;
//                    	}
//                    }
            		if (!(after2 >= text.length())) {
            			if (text.charAt(before2) == '"' && text.charAt(after2) == '"') {
                			//System.out.println("Found a match! at: " + text.charAt() + String.valueOf(wordL2) + ", " + text.charAt(wordR2) + String.valueOf(wordR2));
                			setCharacterAttributes(before2, after2, attrStrings, false);
                		} else {
                        	setCharacterAttributes(before2, after2, attrBlack, false);
                		}
            		}
                }


                if (text.substring(before, after).matches(commands)) {
                    setCharacterAttributes(before, after - before, attrCommands, false);
                } else {
                    setCharacterAttributes(before, after - before, attrBlack, false);
                }
            }
        };
        JTextPane txt = new JTextPane(doc);

        List<String> list = Arrays.asList(fonts);
        Font font = new Font("what the poo brain", Font.PLAIN, 13);
        if (list.contains("Courier New")) {
        	font = new Font("Courier New", Font.PLAIN, 13);
        } else if (list.contains("Consolas")) {
        	font = new Font("Consolas", Font.PLAIN, 13);
        } else if (list.contains("Freemono")) {
        	font = new Font("Freemono", Font.PLAIN, 13);
        } else if (list.contains("Hack")) {
        	font = new Font("Hack", Font.PLAIN, 13);
        }
        txt.setFont(font);
        txt.setText("echo \"Hello, world!\"");
        txt.setBackground(Color.decode("#303030"));
        txt.setBorder(BorderFactory.createLineBorder(Color.decode("#303030")));
        JScrollPane scroll = new JScrollPane(txt);
        scroll.setBackground(Color.decode("#303030"));
        scroll.setBorder(BorderFactory.createLineBorder(Color.decode("#303030")));
        getContentPane().add(scroll);
        setVisible(true);
    }
    
    public static void main (String[] args) {

        new Test();
    }
    
    private int findLastNonWordChar (String text, int index) {
        while (--index >= 0) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
        }
        return index;
    }

    private int findLastQuote (String text, int index) {
        while (--index >= 0) {
            if (String.valueOf(text.charAt(index)).matches("\"")) {
                break;
            }
        }
        return index;
    }

    private int findFirstQuote (String text, int index) {
        while (index < text.length()) {
            if (String.valueOf(text.charAt(index)).matches("\"")) {
                break;
            }
            index++;
        }
        return index;
    }

    private int findFirstNonWordChar (String text, int index) {
        while (index < text.length()) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
            index++;
        }
        return index;
    }
}