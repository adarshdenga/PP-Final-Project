package antlr;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

// import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * A test class to view the parse tree for a given file in a GUI window
 */

public class ParseTreeView {
    
    /** 
     * @param args args[0] should be the filename of the file you want to view
     * the parse tree of. This file should be in src.antlr
     */
    public static void main(String[] args) {
        // String BASE_DIR = System.getProperty("user.dir") + "/src/antlr/";

        // if (args.length < 1) {
        //     System.out.println("You need to define a filename as an argument!");
        //     return;
        // }

        // // prepare token stream
        // File file = new File(BASE_DIR + args[0] + ".aapfl");
        // if (!file.exists()) {
        //     System.out.println("Couldn't find file " + args[0] + ".aapfl. Make sure it's in the right place!");
        // }
        // CharStream stream;
        // try {
        //     stream = CharStreams.fromPath(file.toPath());
        //     langLexer lexer  = new langLexer(stream);   
        //     TokenStream tokenStream = new CommonTokenStream(lexer);
        //     langParser parser = new langParser(tokenStream);
        //     ParseTree tree = parser.prog(); 

        //     //show AST in console
        //     System.out.println(tree.toStringTree(parser));

        //     JFrame frame = new JFrame("Antlr AST");
        //     JPanel panel = new JPanel();
        //     JScrollPane scrollPane = new JScrollPane(panel);
        //     scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //     scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        //     scrollPane.setBounds(50, 30, 300, 50);
        //     TreeViewer viewr = new TreeViewer(Arrays.asList(
        //             parser.getRuleNames()),tree);
        //     viewr.setScale(1.5);//scale a little
        //     panel.add(viewr);
        //     frame.add(scrollPane);
        //     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //     frame.setSize(1920,1080);
        //     frame.setVisible(true);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        
    }
}
