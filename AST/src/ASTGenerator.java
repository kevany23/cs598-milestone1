import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

public class ASTGenerator {

    static ArrayList<String> LineNum = new ArrayList<String>();
    static ArrayList<String> Type = new ArrayList<String>();
    static ArrayList<String> Content = new ArrayList<String>();

    private static String readFile(String directory) throws IOException {
        Path path = Paths.get(directory);
        byte[] encoded = Files.readAllBytes(path);
        return new String(encoded, Charset.forName("UTF-8"));
    }

    public static void main(String args[]) throws IOException{
        String inputString = readFile(args[0]);
        ANTLRInputStream input = new ANTLRInputStream(inputString);
        Java8Lexer lexer = new Java8Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Java8Parser parser = new Java8Parser(tokens);
        ParserRuleContext ctx = parser.compilationUnit();

        generateAST(ctx, false, 0);

        // System.out.println("digraph G {");
        // printDOT();
        // System.out.println("}");
        writeDOT();
    }

    private static void generateAST(RuleContext ctx, boolean verbose, int indentation) {
        boolean toBeIgnored = !verbose && ctx.getChildCount() == 1 && ctx.getChild(0) instanceof ParserRuleContext;

        if (!toBeIgnored) {
            String ruleName = Java8Parser.ruleNames[ctx.getRuleIndex()];
            LineNum.add(Integer.toString(indentation));
            Type.add(ruleName);
            Content.add(ctx.getText());
        }
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree element = ctx.getChild(i);
            if (element instanceof RuleContext) {
                generateAST((RuleContext) element, verbose, indentation + (toBeIgnored ? 0 : 1));
            }
        }
    }

    private static void printDOT(){
        printLabel();
        int pos = 0;
        for(int i=1; i<LineNum.size();i++){
            pos=getPos(Integer.parseInt(LineNum.get(i))-1, i);
            System.out.println((Integer.parseInt(LineNum.get(i))-1)+Integer.toString(pos)+"->"+LineNum.get(i)+i);
        }
    }

    private static void printLabel(){
        for(int i =0; i<LineNum.size(); i++){
            System.out.println(LineNum.get(i)+i+"[label=\""+Type.get(i)+"\\n "+Content.get(i)+" \"]");
        }
    }

    private static void writeDOT() throws IOException {
        FileWriter out = new FileWriter("AST.dot");
        out.write("digraph G {\n");
        writeLabel(out);
        int pos = 0;
        for(int i=1; i<LineNum.size();i++){
            pos=getPos(Integer.parseInt(LineNum.get(i))-1, i);
            out.write((Integer.parseInt(LineNum.get(i))-1)+Integer.toString(pos)+"->"+LineNum.get(i)+i);
            out.write("\n");
        }
        out.write("}");
        out.close();
    }

    private static void writeLabel(FileWriter out) throws IOException {
        for(int i =0; i<LineNum.size(); i++){
            // remove [] and ""
            String content = Type.get(i)+"\\n "+Content.get(i);
            content = content.replace("[", "");
            content = content.replace("]", "");
            content = content.replace("\"", "");
            out.write(LineNum.get(i)+i+"[label=\""+content+" \"]");
            out.write("\n");
        }
    }

    private static int getPos(int n, int limit){
        int pos = 0;
        for(int i=0; i<limit;i++){
            if(Integer.parseInt(LineNum.get(i))==n){
                pos = i;
            }
        }
        return pos;
    }
}