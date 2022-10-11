package rocks.zipcode;

import java.util.ArrayList;
import java.util.Iterator;

public class Compiler {

    enum TokenType {
        paren,
        thesis,
        name,
        number,
        string
    }
    class Token {
        public TokenType type;
        public String value;
        Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    enum AstType {
        program { // visitor funcs
            public String enter(Ast node, Ast parent) {
                return ";; Begin program code\n\t\tSTART";
            }
            public String exit(Ast node, Ast parent) {
                return ";; Print top of stack\n\t\tPOP\n\t\tPRINT\n\t\tHALT";
            }
        },
        callexpression {
            public String enter(Ast node, Ast parent) {
                return ";; call enter";
            }
            public String exit(Ast node, Ast parent) {
                return "\t\tDO " + node.value.toUpperCase();
            }
        },
        numberliteral {
            public String enter(Ast node, Ast parent) {
                return ";; num enter";
            }
            public String exit(Ast node, Ast parent) {
                return "\t\tPUSH #"+ node.value;
            }
        },
        stringliteral {
            public String enter(Ast node, Ast parent) {
                return ";; string enter";
            }
            public String exit(Ast node, Ast parent) {
                return ";; string exit";
            }
        };

        abstract String enter(Ast node, Ast parent);
        abstract String exit(Ast node, Ast parent);
    }
    class Ast {
        public AstType type;
        public String value;
        public ArrayList<Ast> params;
        Ast(AstType type,
            String value) {
                this.type = type;
                this.value = value;
                this.params = new ArrayList<>();
            }
    }

    public String compile(String input) {
        ArrayList<Token> tokens;
        try {
            tokens = tokenizer(input);
            printTokens(tokens);
            Ast ast = parser(tokens);
            //NewAst newAst = transformer(ast);
            String output = codeGenerator(ast);
            return output;
            } catch (Exception e) {
                System.err.println(";; Error in input: "+e);
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<Token> tokenizer(String input) throws Exception {
        int current = 0;
        ArrayList<Token> tokens = new ArrayList<>();
        // maybe this should be a weird Iterable??? with a push()??

        while (current < input.length()) {

            String ch = String.valueOf(input.charAt(current));

            if (ch.equals("(")) {
                tokens.add(new Token(TokenType.paren, "("));
                current++;
                continue;
            }
            if (ch.equals(")")) {
                tokens.add(new Token(TokenType.thesis, ")"));
                current++;
                continue;
            }
            if (ch.isBlank()) {
                current++;
                continue;
            }
            if (ch.matches("[0-9]")) {
                String value = "";
                while (ch.matches("[0-9]")) {
                    value = value + ch;
                    current++;
                    ch = String.valueOf(input.charAt(current));
                }
                tokens.add(new Token(TokenType.number, value));
                continue;
            }
            if (ch.equals("\"")) {
                String value = "";
                while (!ch.equals("\"")) { // NB !
                    value = value + ch;
                    current++;
                    ch = String.valueOf(input.charAt(current));
                }
                ch = String.valueOf(input.charAt(current));
                tokens.add(new Token(TokenType.string, value));
                continue;
            }
            if (ch.matches("[a-zA-Z]")) {
                String value = "";
                while (ch.matches("[a-zA-Z]")) {
                    value = value + ch;
                    current++;
                    ch = String.valueOf(input.charAt(current));
                }
                tokens.add(new Token(TokenType.name, value));
                continue;
            }
            throw new Exception(";; Illegal character in input.");
        }
        return tokens;
    }
    
    private void printTokens(ArrayList<Token> tts) {
        for (Token t : tts) {
            System.err.println(";; "+t.type+":"+t.value);
        }
    }
    private Ast parser(ArrayList<Token> tokens) {
        //int current = 0;
        Ast root = new Ast(AstType.program, null);
        Iterator<Token> tokenIterator = tokens.iterator();
        Token token = null;
        while (tokenIterator.hasNext()) {
            root.params.add(this.walk(token, tokenIterator));
        }
        return root;
    }

    private Ast walk(Token token, Iterator<Token> tokens) {
        if (tokens.hasNext()) {
            token = tokens.next();
            System.err.println(";; walk: 0 "+token.value);
        } else System.err.println(";; EOF 0");

        //= tokens.get(idx);

        if (token.type == TokenType.number) {
            return new Ast(AstType.numberliteral, token.value);
        }
        if (token.type == TokenType.string) {
            return new Ast(AstType.stringliteral, token.value);
        }
        if (token.type == TokenType.paren) {
            if (tokens.hasNext()) {
                token = tokens.next();
                System.err.println(";; walk: 1 "+token.value);
            } else System.err.println(";; EOF 1");
            Ast node = new Ast(AstType.callexpression, token.value);

            while (token.type != TokenType.thesis) {
                Ast t = walk(token, tokens);
                if (t != null) node.params.add(t);
                else break;
            }
            return node;
        }
        if (token.type == TokenType.thesis) {
            return null;
        }

        System.err.println(";; UNKNOWN TOKEN..."+token.value);
        return null;
    }

    private void traverseAndEmit(Ast ast) {
        traverseNodeAndEmit(ast, null);
    }

    private void traverseList(ArrayList<Ast> list, Ast parent) {
        for (Ast child : list) {
            traverseNodeAndEmit(child, parent);
        }
    }

    private void traverseNodeAndEmit(Ast node, Ast parent) {
        emitCode(node.type.enter(node, parent));

        if (node.type == AstType.program) {
            traverseList(node.params, node);
        }
        if (node.type == AstType.callexpression) {
            traverseList(node.params, node);
        }
        if (node.type == AstType.numberliteral) ;
        if (node.type == AstType.stringliteral) ;

        emitCode(node.type.exit(node, parent));
    }

    private void emitCode(String code) {
        //System.err.println(code);
        outputCode.append(code+"\n");
    }

    private StringBuilder outputCode; 

    private String codeGenerator(Ast ast) {
        // For debugging, print out the AST received.
        System.err.println(";; BEGIN Ast Dump");
        printNode(ast);
        System.err.println(";; END Ast Dump");

        // bad: using an instance varable to capture emitted code.
        outputCode = new StringBuilder();
        traverseAndEmit(ast);
        return outputCode.toString();
    }

    private void printNode(Ast ast) {
        System.err.println(";; "+ast.type.toString()+":"+ast.value);
        for (Ast e : ast.params) {
            printNode(e);
        }
    }
}
