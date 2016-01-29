
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class NewlineGenerator {

    static JFrame frame = new JFrame("Newline Generator");
    static JTextField console = new JTextField();
    static JTextPane instructions = new JTextPane();
    static JTextField name = new JTextField("Current name: ");
    static boolean button = false;
    static long lastTyped = System.currentTimeMillis();

    public static void putIn(String output) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        try {
            FileWriter writer = new FileWriter("output.txt", true);
            if (output.substring(output.length() - 1, output.length()).contains(".") || output.substring(output.length() - 1, output.length()).contains("?") || output.substring(output.length() - 1, output.length()).contains("!")) {
                writer.write(output);
                java.util.Calendar cal = java.util.Calendar.getInstance();
                int seconds = cal.get(java.util.Calendar.SECOND);
                int minutes = cal.get(java.util.Calendar.MINUTE);
                int hours = cal.get(java.util.Calendar.HOUR);
                String timestamp = "" + hours + ":" + minutes + ":" + seconds;
                writer.write(" ");
                writer.write(timestamp);
                writer.write(" -");
                writer.write(name.getText().substring(14));
                writer.write(System.lineSeparator());
            } else {
                writer.write(output);
                writer.write(" ");
            }
            writer.close();
        } catch (IOException e) {
        }
    }

    public static void destroy(int wordLength) {
        try {
            while (console.getText().substring(0, 1).equals(" ") && !console.getText().isEmpty()) {
                console.setText(console.getText().substring(console.getText().indexOf(" ") + 1));
            }
            if (console.getText().length() >= wordLength) {
                console.setText(console.getText().substring(wordLength));
            }
            while (console.getText().substring(0, 1).equals(" ") && !console.getText().isEmpty()) {
                console.setText(console.getText().substring(console.getText().indexOf(" ") + 1));
            }
        } catch (StringIndexOutOfBoundsException e) {
        }
    }

    public static void writeOut() throws FileNotFoundException, UnsupportedEncodingException, IOException {
        String output = "";
        Scanner in = new Scanner(console.getText());
        boolean eos = false;
        while (in.hasNext()) {
            output = in.next();
            if (in.hasNext()) {
                putIn(output);
                destroy(output.length());
            }
            if (!in.hasNext() && (output.substring(output.length() - 1, output.length()).contains(".") || output.substring(output.length() - 1, output.length()).contains("?") || output.substring(output.length() - 1, output.length()).contains("!"))) {
                eos = true;
                putIn(output);
                destroy(output.length());
            }
        }
        if (eos) {
            console.setText("");
        }
        console.setCaretPosition(console.getText().length());
    }

    public static void start() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
        writer.print("");
        writer.close();
    }

    public static void window() {
        JButton changeName = new JButton("Change\nname");
        changeName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!button) {
                    button = true;
                    Thread.currentThread().interrupt();
                    try {
                        if (!console.getText().isEmpty()) {
                            String ending = console.getText() + ".";
                            putIn(ending);
                        }
                        console.setText("");
                        name.setText("Current name: ");
                    } catch (FileNotFoundException ex) {
                    } catch (UnsupportedEncodingException ex) {
                    } catch (IOException ex) {
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                    console.grabFocus();
                }
            }
        });
        instructions.setEditable(false);
        name.setEditable(false);
        instructions.setText("Instructions: Enter name and press enter. Enter text as desired.\n                       Close when finished. Output.txt will have the results.");
        instructions.setBounds(frame.getX(), frame.getY(), frame.getWidth(), 100);
        console.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Scanner check = new Scanner(console.getText());
                    if (console.hasFocus() && check.hasNext() && name.getText().equals("Current name: ")) {
                        name.setText(name.getText() + console.getText());
                        if (button) {
                            try {
                                String warning = "SYSTEM: User changed name to: " + console.getText();
                                putIn(warning);
                                destroy(0);
                                putIn(System.lineSeparator());
                                destroy(0);
                            } catch (FileNotFoundException ex) {
                            } catch (UnsupportedEncodingException ex) {
                            } catch (IOException ex) {
                            }
                            button = false;
                        }
                        console.setText("");
                        console.grabFocus();
                    }
                }
            }
        });
        console.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                lastTyped = System.currentTimeMillis();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                lastTyped = System.currentTimeMillis();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                lastTyped = System.currentTimeMillis();
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                Thread.currentThread().interrupt();
                try {
                    if (!console.getText().isEmpty()) {
                        putIn(console.getText());
                        destroy(console.getText().length());
                    }
                } catch (FileNotFoundException ex) {
                } catch (UnsupportedEncodingException ex) {
                } catch (IOException ex) {
                }
                System.exit(0);
            }
        });
        frame.add(instructions, BorderLayout.NORTH);
        frame.add(name, BorderLayout.CENTER);
        frame.add(console, BorderLayout.SOUTH);
        frame.add(changeName, BorderLayout.EAST);
        frame.setBounds(100, 50, 365, 110);
        frame.setResizable(false);
        frame.setVisible(true);
        console.grabFocus();
    }

    //Done
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, InterruptedException, IOException {
        window();
        start();
        while (true) {
            while (name.getText().equals("Current name: ")) {
                Thread.sleep(500);
            }
            Thread.sleep(100);
            while (!name.getText().equals("Current name: ")) {
                while (System.currentTimeMillis() - lastTyped < 250) {
                    Thread.sleep(50);
                }
                writeOut();

                Thread.sleep(1000);
            }
            Thread.sleep(100);
        }
    }
}