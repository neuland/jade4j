package de.neuland.pug4j.parser;

import de.neuland.pug4j.exceptions.PugParserException;
import de.neuland.pug4j.expression.ExpressionHandler;
import de.neuland.pug4j.lexer.token.Assignment;
import de.neuland.pug4j.lexer.token.Each;
import de.neuland.pug4j.lexer.Lexer;
import de.neuland.pug4j.lexer.token.*;
import de.neuland.pug4j.parser.node.*;
import de.neuland.pug4j.template.TemplateLoader;
import de.neuland.pug4j.util.CharacterParser;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    public static final Pattern FILE_EXTENSION_PATTERN = Pattern.compile(".*\\.\\w+$");
    private Lexer lexer;
    private LinkedHashMap<String, BlockNode> blocks = new LinkedHashMap<String, BlockNode>();
    private String[] textOnlyTags = {"script", "style"};
    private Integer _spaces = null;
    private final TemplateLoader templateLoader;
    private ExpressionHandler expressionHandler;
    private Parser extending;
    private final String filename;
    private LinkedList<Parser> contexts = new LinkedList<Parser>();
    private CharacterParser characterParser;
    private int inMixin = 0;
    private HashMap<String, MixinNode> mixins = new HashMap<String, MixinNode>();
    private int inBlock = 0;
    private PathHelper pathHelper = new PathHelper();

    public Parser(String filename, TemplateLoader templateLoader, ExpressionHandler expressionHandler) throws IOException {
        this.filename = filename;
        this.templateLoader = templateLoader;
        this.expressionHandler = expressionHandler;
        lexer = new Lexer(filename, templateLoader, expressionHandler);
        characterParser = new CharacterParser();
        getContexts().push(this);
    }

    public Parser(String src, String filename, TemplateLoader templateLoader, ExpressionHandler expressionHandler) throws IOException {
        this.filename = filename;
        this.templateLoader = templateLoader;
        this.expressionHandler = expressionHandler;
        lexer = new Lexer(src, filename, templateLoader, expressionHandler);
        characterParser = new CharacterParser();
        getContexts().push(this);
    }
    //done
    private PugParserException error(String code, String message, Token token){
        return new PugParserException(this.filename,token.getStartLineNumber(),templateLoader,message,code);
    }
    //done
    private BlockNode emptyBlock(){
        return this.emptyBlock(0);
    }
    //done
    private BlockNode emptyBlock(int line){
        return this.initBlock(line,new LinkedList<>());
    }
    //done
    public Node parse() {
        BlockNode block = emptyBlock(0);
        while (!(peek() instanceof Eos)) {
            if (peek() instanceof Newline) {
                advance();
            } else if(peek() instanceof TextHtml){
                block.getNodes().addAll(parseTextHtml());
            } else {
                Node expr = parseExpr();
                if (expr != null) {
                    if(expr instanceof BlockNode && !((BlockNode) expr).isYield()){
                        block.getNodes().addAll(expr.getNodes());
                    }else {
                        block.push(expr);
                    }
                }
            }
        }
        //TODO: check if still needed
        if (extending != null) {
            getContexts().push(extending);
            Node rootNode = extending.parse();
            getContexts().pop();

            // hoist mixins
            Set<String> keySet = this.mixins.keySet();
            for (String name : keySet) {
                rootNode.getNodes().push(this.mixins.get(name));
            }
            return rootNode;
        }

        return block;
    }
    //done
    private Node parseExpr() {
        Token token = peek();
        if (token instanceof Tag) {
            return parseTag();
        }
        if (token instanceof Mixin) {
            return parseMixin();
        }
        if (token instanceof Block) {
            return parseBlock();
        }
        if (token instanceof MixinBlock) {
            return parseMixinBlock();
        }
        if (token instanceof CaseToken) {
            return parseCase();
        }
        if (token instanceof ExtendsToken) {
            return parseExtends();
        }
        if (token instanceof Include) {
            return parseInclude();
        }
        if (token instanceof Doctype) {
            return parseDoctype();
        }
        if (token instanceof Filter) {
            return parseFilter();
        }
        if (token instanceof Comment) {
            return parseComment();
        }
        if (token instanceof Text || token instanceof InterpolatedCode || token instanceof StartPugInterpolation) {
            return parseText(true);
        }
        if (token instanceof TextHtml) {
            return initBlock(this.peek().getStartLineNumber(),parseTextHtml());
        }
        if (token instanceof Dot) {
            return parseDot();
        }
        if (token instanceof Each) {
            return parseEach();
        }
        if (token instanceof Code) {
            return parseCode();
        }
        if (token instanceof Expression) {
            return parseCode();
        }
        if (token instanceof BlockCode) {
            return parseBlockCode();
        }
        if (token instanceof If) {
            return parseConditional();
        }
        if (token instanceof While) {
            return parseWhile();
        }
        if (token instanceof Call) {
            return parseCall();
        }
        if (token instanceof Interpolation) {
            return parseInterpolation();
        }
        if (token instanceof Yield) {
            return parseYield();
        }
        if (token instanceof CssClass || token instanceof CssId) {
            return parseCssClassOrId();
        }
        if (token instanceof Assignment) {
            return parseAssignment();
        }

        throw error("INVALID_TOKEN","unexpected token \"" + peek().getType() + "\"",peek());
    }
    //done
    private BlockNode initBlock(int startLineNumber, LinkedList<Node> nodes) {
        if(nodes==null){
            throw new PugParserException(this.filename,this.line(),templateLoader,"`nodes` is not an array");
        }
        BlockNode blockNode = new BlockNode();
        blockNode.setNodes(nodes);
        blockNode.setLineNumber(startLineNumber);
        blockNode.setFileName(this.filename);
        return blockNode;
    }

    /**
     * block code
     */
    //done
    private Node parseBlockCode() {
        Token tok = this.expect(BlockCode.class);
        int line = tok.getStartLineNumber();
        int column = tok.getStartColumn();
        Token body = this.peek();
        String text = "";

        if(body instanceof StartPipelessText){
            advance();
            while (!(peek() instanceof EndPipelessText)){
                tok = advance();
                if(tok instanceof Text){
                    text += tok.getValue();
                }else if(tok instanceof Newline){
                    text += "\n";
                }else{
                    throw error("INVALID_TOKEN","Unexpected token type: "+tok.getType(),tok);
                }
            }
            advance();
        }

        ExpressionNode node = new ExpressionNode();
        node.setValue(text);
        node.setBuffer(false);
        node.setEscape(false);
        node.setInline(false);
        node.setLineNumber(line);
        node.setColumn(column);
        return node;
    }
    //done
    private Node parseComment() {
        Token token = expect(Comment.class);

        Node block = this.parseTextBlock();
        if (block != null) {
            BlockCommentNode node = new BlockCommentNode();
            node.setValue(token.getValue());
            node.setBlock(block);
            node.setBuffered(token.isBuffer());
            node.setLineNumber(token.getStartLineNumber());
            node.setColumn(token.getStartColumn());
            node.setFileName(filename);
            return node;
        } else {
            CommentNode node = new CommentNode();
            node.setValue(token.getValue());
            node.setBuffered(token.isBuffer());
            node.setLineNumber(token.getStartLineNumber());
            node.setColumn(token.getStartColumn());
            node.setFileName(filename);
            return node;
        }
    }

    //done
    private Node parseMixin() {
        Mixin mixinToken = (Mixin) expect(Mixin.class);


        if (peek() instanceof Indent) {
            this.inMixin++;
            MixinNode node = new MixinNode();
            node.setName(mixinToken.getValue());
            node.setLineNumber(mixinToken.getStartLineNumber());
            node.setColumn(mixinToken.getStartColumn());
            node.setFileName(filename);

            if (StringUtils.isNotBlank(mixinToken.getArguments())) {
                node.setArguments(mixinToken.getArguments());
            }
            List<String> args = node.getArguments();

            String rest;

            if (args.size() > 0) {
                Matcher matcher = Pattern.compile("^\\.\\.\\.").matcher(args.get(args.size() - 1).trim());
                if (matcher.find(0)) {
                    rest = args.remove(args.size() - 1).trim().replaceAll("^\\.\\.\\.", "");
                    node.setRest(rest);

                }
            }

            node.setBlock(block());
            node.setCall(false);
            this.mixins.put(mixinToken.getValue(), node);
            this.inMixin--;
            return node;
        } else {
            throw error("MIXIN_WITHOUT_BODY","Mixin " + mixinToken.getValue() + " declared without body",mixinToken);
        }
    }
    //done
    private Node parseCall() {
        Call callToken = (Call) expect(Call.class);
        MixinNode mixin = new MixinNode();
        mixin.setBlock(emptyBlock(callToken.getStartLineNumber()));
        mixin.setName(callToken.getValue());
        mixin.setLineNumber(callToken.getStartLineNumber());
        mixin.setColumn(callToken.getStartColumn());
        mixin.setFileName(filename);
        mixin.setCall(true);

        if (StringUtils.isNotBlank(callToken.getArguments())) {
            mixin.setArguments(callToken.getArguments());
        }


        this.tag(mixin);
        if (mixin.hasCodeNode()) {
            mixin.getBlock().push(mixin.getCodeNode());
            mixin.setCodeNode(null);
        }
        if (mixin.hasBlock() && mixin.getBlock().getNodes().isEmpty())
            mixin.setBlock(null);
        return mixin;
    }

    private Node parseCssClassOrId() {
        Tag div = new Tag("div");
        div.setStartColumn(peek().getStartColumn());
        div.setStartLineNumber(peek().getStartLineNumber());
        div.setEndColumn(peek().getEndColumn());
        div.setEndLineNumber(peek().getEndLineNumber());
        div.setFileName(this.filename);
        lexer.defer(div);
        return parseExpr();
    }

    //done
    private BlockNode parseBlock() {
        Block blockToken = (Block)expect(Block.class);
        String mode = blockToken.getMode();
        String name = blockToken.getValue().trim();

        this.inBlock++;
        BlockNode blockNode;
        if (peek() instanceof Indent) {
            blockNode = block();
        } else {
            blockNode = emptyBlock(blockToken.getStartLineNumber());
        }
        blockNode.setNamedBlock(true);
        blockNode.setName(name);
        blockNode.setLineNumber(blockToken.getStartLineNumber());
        blockNode.setColumn(blockToken.getStartColumn());
        blockNode.setFileName(this.filename);
        this.inBlock--;

        BlockNode prev;
        if (this.blocks.get(name) == null)
            prev = new BlockNode();
        else
            prev = this.blocks.get(name);


        if ("replace".equals(prev.getMode())) {
            this.blocks.put(name, prev);
            return prev;
        }
        LinkedList<Node> allNodes = new LinkedList<Node>();
        allNodes.addAll(prev.getPrepended());
        allNodes.addAll(blockNode.getNodes());
        allNodes.addAll(prev.getAppended());
        //ok


        if ("append".equals(mode)) {
            LinkedList<Node> appendedNodes = new LinkedList<Node>();
            if (prev.getParser() == this) {
                appendedNodes.addAll(prev.getAppended());
                appendedNodes.addAll(blockNode.getNodes());
            } else {
                appendedNodes.addAll(blockNode.getNodes());
                appendedNodes.addAll(prev.getAppended());
            }
            prev.setAppended(appendedNodes);
        } else if ("prepend".equals(mode)) {
            LinkedList<Node> prependedNodes = new LinkedList<Node>();
            if (prev.getParser() == this) {
                prependedNodes.addAll(blockNode.getNodes());
                prependedNodes.addAll(prev.getPrepended());
            } else {
                prependedNodes.addAll(prev.getPrepended());
                prependedNodes.addAll(blockNode.getNodes());
            }
            prev.setPrepended(prependedNodes);

        }

        blockNode.setNodes(allNodes);
        blockNode.setAppended(prev.getAppended());
        blockNode.setPrepended(prev.getPrepended());
        blockNode.setMode(mode);
        blockNode.setParser(this);
        blockNode.setSubBlock(this.inBlock > 0);

        blocks.put(name, blockNode);
        return blockNode;

        //TODO: check were this logik is in the future
//        Block block = (Block) token;
//        String mode = block.getMode();
//        String name = block.getValue().trim();
//
//        this.inBlock++;
//        BlockNode blockNode;
//        if (peek() instanceof Indent) {
//            blockNode = block();
//        } else {
//            blockNode = new BlockNode();
//            blockNode.setLineNumber(block.getStartLineNumber());
//            blockNode.setFileName(filename);
//            LiteralNode node = new LiteralNode();
//            node.setValue("");
//            blockNode.push(node);
//        }
//        this.inBlock--;
//        blockNode.setName(name);
//        blockNode.setLineNumber(line());
//
//        BlockNode prev;
//        if (this.blocks.get(name) == null)
//            prev = new BlockNode();
//        else
//            prev = this.blocks.get(name);
//
//
//        if ("replace".equals(prev.getMode())) {
//            this.blocks.put(name, prev);
//            return prev;
//        }
//        LinkedList<Node> allNodes = new LinkedList<Node>();
//        allNodes.addAll(prev.getPrepended());
//        allNodes.addAll(blockNode.getNodes());
//        allNodes.addAll(prev.getAppended());
//        //ok
//
//
//        if ("append".equals(mode)) {
//            LinkedList<Node> appendedNodes = new LinkedList<Node>();
//            if (prev.getParser() == this) {
//                appendedNodes.addAll(prev.getAppended());
//                appendedNodes.addAll(blockNode.getNodes());
//            } else {
//                appendedNodes.addAll(blockNode.getNodes());
//                appendedNodes.addAll(prev.getAppended());
//            }
//            prev.setAppended(appendedNodes);
//        } else if ("prepend".equals(mode)) {
//            LinkedList<Node> prependedNodes = new LinkedList<Node>();
//            if (prev.getParser() == this) {
//                prependedNodes.addAll(blockNode.getNodes());
//                prependedNodes.addAll(prev.getPrepended());
//            } else {
//                prependedNodes.addAll(prev.getPrepended());
//                prependedNodes.addAll(blockNode.getNodes());
//            }
//            prev.setPrepended(prependedNodes);
//
//        }
//
//        blockNode.setNodes(allNodes);
//        blockNode.setAppended(prev.getAppended());
//        blockNode.setPrepended(prev.getPrepended());
//        blockNode.setMode(mode);
//        blockNode.setParser(this);
//        blockNode.setSubBlock(this.inBlock > 0);
//
//        blocks.put(name, blockNode);
//        return blockNode;
    }
    //done
    private Node parseMixinBlock() {
        Token tok = expect(MixinBlock.class);
        if (this.inMixin == 0) {
            throw error("BLOCK_OUTISDE_MIXIN","Anonymous blocks are not allowed unless they are part of a mixin.",tok);
        }

        MixinBlockNode mixinBlockNode = new MixinBlockNode();
        mixinBlockNode.setLineNumber(tok.getStartLineNumber());
        mixinBlockNode.setColumn(tok.getStartColumn());
        mixinBlockNode.setFileName(this.filename);
        return mixinBlockNode;
    }
    //done
    private Node parseInclude() {
        Include includeToken = (Include)expect(Include.class);

//        IncludeNode node = new IncludeNode();


        LinkedList<Node> filters = new LinkedList<Node>();
        while(peek() instanceof Filter){
            filters.add(parseIncludeFilter());
        }

        Path pathToken = (Path) expect(Path.class);

//        FileReference file = new FileReference();
//        file.setFilename(this.filename);
//        String filePath = pathToken.getValue().trim();
//        file.setPath(filePath);
//        file.setLine(pathToken.getStartLineNumber());
//        file.setColumn(pathToken.getStartColumn());
//        node.setFile(file);

//        String extension = FilenameUtils.getExtension(path);
//        if(templateLoader.getExtension().equals(extension) && filters.size()==0){
//            Parser parser = createParser(path);
//            parser.setBlocks(new LinkedHashMap<String, BlockNode>(blocks));
//            parser.setMixins(mixins);
//            contexts.push(parser);
//            Node ast = parser.parse();
//            contexts.pop();
//            ast.setFileName(path);
//            if (peek() instanceof Indent && ast != null) {
//                ((BlockNode) ast).getIncludeBlock().push(block());
//            }else{
//                ast.setBlock(emptyBlock(includeToken.getStartLineNumber()));
//            }
//            return ast;
//        }else{
//            node.setRaw(true);
//            node.setFilters(filters);
//            if(peek() instanceof Indent){
//                throw error("RAW_INCLUDE_BLOCK","Raw inclusion cannot contain a block",peek());
//            }
//        }
//        return node;

        //TODO: check where logik will be
        String templateName = pathToken.getValue().trim();
        String path = templateLoader.resolvePath(filename, templateName, templateLoader.getExtension());

        try {
            if (filters.size()>0) {
                Reader reader = templateLoader.getReader(path);
                FilterNode node = new FilterNode();
                node.setFilter(filters);
                node.setLineNumber(line());
                node.setFileName(filename);

                TextNode text = new TextNode();
                text.setValue(IOUtils.toString(reader));

                BlockNode block = new BlockNode();
                LinkedList<Node> nodes = new LinkedList<Node>();
                nodes.add(text);
                block.setNodes(nodes);
                if (block != null)
                    node.setBlock(block);
                else {
                    node.setBlock(emptyBlock(includeToken.getStartLineNumber()));
                }
                return node;
            }
        } catch (IOException e) {
            throw new PugParserException(filename, lexer.getLineno(), templateLoader, "the included file [" + templateName + "] could not be opened\n" + e.getMessage());
        }

        // non-jade
        String extension = FilenameUtils.getExtension(path);
        if (!templateLoader.getExtension().equals(extension)) {
            try {
                Reader reader = templateLoader.getReader(path);
                LiteralNode node = new LiteralNode();
                node.setLineNumber(lexer.getLineno());
                node.setFileName(filename);
                node.setValue(IOUtils.toString(reader));
                return node;
            } catch (IOException e) {
                throw new PugParserException(filename, lexer.getLineno(), templateLoader, "the included file [" + templateName + "] could not be opened\n" + e.getMessage());
            }
        }

        Parser parser = createParser(templateName);
        parser.setBlocks(new LinkedHashMap<String, BlockNode>(blocks));
        parser.setMixins(mixins);
        contexts.push(parser);
        Node ast = parser.parse();
        contexts.pop();
        ast.setFileName(path);
        if (peek() instanceof Indent && ast != null) {
            //Fill YieldBlock with Nodes and make it a normal Block
            BlockNode includeBlock = ((BlockNode) ast).getYieldBlock();
            includeBlock.push(block());
            includeBlock.setYield(false);
        }

        return ast;
    }
    //done
    private Node parseExtends() {
        ExtendsToken extendsToken = (ExtendsToken) expect(ExtendsToken.class);
        Path path = (Path) expect(Path.class);

        String templateName = path.getValue().trim();

        Parser parser = createParser(templateName);

        parser.setBlocks(blocks);
        parser.setContexts(contexts);
        extending = parser;

//        ExtendsNode node = new ExtendsNode();
//
//        FileReference file = new FileReference();
//        file.setPath(path.getValue().trim());
//        file.setLine(path.getStartLineNumber());
//        file.setColumn(path.getStartColumn());
//        file.setFilename(this.filename);
//
//        node.setFile(file);
//        node.setLineNumber(extendsToken.getStartLineNumber());
//        node.setColumn(extendsToken.getStartColumn());
//        node.setFileName(this.filename);
        LiteralNode node = new LiteralNode();
        node.setValue("");
        return node;
    }

    private Parser createParser(String templateName) {
        templateName = ensurePugExtension(templateName);
        try {
            String resolvedPath = templateLoader.resolvePath(this.filename, templateName, templateLoader.getExtension());
            return new Parser(resolvedPath, templateLoader, expressionHandler);
        } catch (IOException e) {
            throw new PugParserException(
                    this.filename,
                    lexer.getLineno(),
                    templateLoader,
                    "The template [" + templateName + "] could not be opened. Maybe it is located outside the base path?\n" + e.getMessage()
            );
        }
    }

    private String ensurePugExtension(String templateName) {
        if (!templateLoader.getExtension().equals(FilenameUtils.getExtension(templateName))) {
            return templateName + "." + templateLoader.getExtension();
        }
        return templateName;
    }

    //done
    private BlockNode parseYield() {
        Token token = expect(Yield.class);
        BlockNode block = new BlockNode();
        block.setLineNumber(token.getStartLineNumber());
        block.setColumn(token.getStartColumn());
        block.setFileName(filename);
        block.setYield(true);
        return block;
    }
    //TODO: check later
    private Node parseInterpolation() {
        Token token = advance();
        String name = token.getValue();
        TagNode tagNode = new TagNode();
        tagNode.setBlock(emptyBlock(token.getStartLineNumber()));
        tagNode.setLineNumber(token.getStartLineNumber());
        tagNode.setColumn(token.getStartColumn());
        tagNode.setFileName(filename);
        tagNode.setName(name);
        tagNode.setBuffer(true);
        return this.tag(tagNode,true);
    }

    private Node blockExpansion() {
        if (peek() instanceof Colon) {
            Token token = expect(Colon.class);
            Colon colon = (Colon) token;
            BlockNode block = new BlockNode();
            block.setLineNumber(colon.getStartLineNumber());
            block.setFileName(filename);
            block.getNodes().add(parseExpr());
            return block;
        }
        return block();
    }
    //done
    private BlockNode block() {
        Token token = expect(Indent.class);

        BlockNode block = emptyBlock(token.getStartLineNumber());
        while (!(peek() instanceof Outdent)) {
            if (peek() instanceof Newline) {
                advance();
            } else if(peek() instanceof TextHtml){
                block.getNodes().addAll(parseTextHtml());
            } else {
                Node expr = parseExpr();
                if (expr != null) {
                    if(expr instanceof BlockNode && !((BlockNode) expr).isYield()){
                        block.getNodes().addAll(expr.getNodes());
                    }else {
                        block.push(expr);
                    }
                }
            }
        }
        expect(Outdent.class);
        return block;
    }

    private List<CaseConditionNode> whenBlock() {
        expect(Indent.class);
        List<CaseConditionNode> caseConditionalNodes = new LinkedList<CaseConditionNode>();
        while (!(peek() instanceof Outdent) && !(peek() instanceof Eos)) {
            if (peek() instanceof Newline) {
                advance();
            } else {
                caseConditionalNodes.add(this.parseCaseCondition());
            }
        }
        if (peek() instanceof Outdent) {
            expect(Outdent.class);
        }
        return caseConditionalNodes;
    }
    //done
    private Node parseText() {
        return parseText(false);
    }
    //done
    private Node parseText(boolean block) {
        LinkedList<Node>  tags = new LinkedList<Node>();
        int lineno = peek().getStartLineNumber();
        Token nextToken = peek();

        while(true){
            if(nextToken instanceof Text){
                Token token = advance();
                TextNode textNode = new TextNode();
                textNode.setValue(token.getValue());
                textNode.setLineNumber(token.getStartLineNumber());
                textNode.setColumn(token.getStartColumn());
                textNode.setFileName(this.filename);
                tags.add(textNode);
            }else if(nextToken instanceof InterpolatedCode){
                InterpolatedCode token = (InterpolatedCode) advance();
                ExpressionNode expressionNode = new ExpressionNode();
                expressionNode.setValue(token.getValue());
                expressionNode.setBuffer(token.isBuffer());
                expressionNode.setEscape(token.isMustEscape());
                expressionNode.setInline(true);
                expressionNode.setLineNumber(token.getStartLineNumber());
                expressionNode.setColumn(token.getStartColumn());
                expressionNode.setFileName(this.filename);
                tags.add(expressionNode);
            }else if(nextToken instanceof Newline){
                if(!block)
                    break;
                Token token = advance();
                Token nextType = peek();
                if(nextType instanceof Text || nextType instanceof InterpolatedCode){
                    TextNode textNode = new TextNode();
                    textNode.setValue("\n");
                    textNode.setLineNumber(token.getStartLineNumber());
                    textNode.setColumn(token.getStartColumn());
                    textNode.setFileName(this.filename);
                    tags.add(textNode);
                }

            }else if(nextToken instanceof StartPugInterpolation){
                advance();
                tags.add(parseExpr());
                expect(EndPugInterpolation.class);
            }else{
                break;
            }
            nextToken = peek();
        }
        if(tags.size()==1)
            return tags.get(0);
        else
            return initBlock(lineno,tags);
    }
    //done
    private LinkedList<Node> parseTextHtml(){
        LinkedList<Node>  nodes= new LinkedList<Node>();
        Node currentNode = null;
        while(true){
            if(peek() instanceof TextHtml){
                Token text = advance();
                if(currentNode==null){
                    TextNode textNode = new TextNode();
                    textNode.setValue(text.getValue());
                    textNode.setFileName(this.filename);
                    textNode.setLineNumber(text.getStartLineNumber());
                    textNode.setColumn(text.getStartColumn());
                    textNode.setHtml(true);
                    currentNode=textNode;
                    nodes.add(currentNode);
                }else{
                    currentNode.setValue(currentNode.getValue() + "\n" + text.getValue());
                }
            }else if(peek() instanceof Indent){
                BlockNode block = block();
                LinkedList<Node> blockNodes = block.getNodes();
                for (Node node : blockNodes) {
                    if(node instanceof TextNode && ((TextNode) node).isHtml()){
                        if(currentNode==null){
                            currentNode=node;
                            nodes.add(currentNode);
                        }else{
                            currentNode.setValue(currentNode.getValue() + "\n" + node.getValue());
                        }
                    }else{
                        currentNode = null;
                        nodes.add(node);
                    }
                }
            }else if(peek() instanceof Expression){
                currentNode = null;
                nodes.add(parseCode(true));
            }else if(peek() instanceof Newline){
                advance();
            }else{
                break;
            }
        }
        return nodes;
    }
    //done
    private Node parseDot() {
        this.advance();
        return parseTextBlock();
    }
    //done
    private Node parseEach() {
        Each eachToken = (Each) expect(Each.class);
        EachNode node = new EachNode();
        node.setValue(eachToken.getValue());
        node.setKey(eachToken.getKey());
        node.setCode(eachToken.getCode());
        node.setLineNumber(eachToken.getStartLineNumber());
        node.setColumn(eachToken.getStartColumn());
        node.setFileName(filename);
        node.setBlock(block());
        if (peek() instanceof Else) {
            advance();
            node.setElseNode(block());
        }
        return node;
    }

    //done
    private Node parseWhile() {
        While whileToken = (While) expect(While.class);
        WhileNode node = new WhileNode();
        node.setValue(whileToken.getValue());
        node.setLineNumber(whileToken.getStartLineNumber());
        node.setColumn(whileToken.getStartColumn());
        node.setFileName(filename);

        //handle block
        if(peek()instanceof Indent){
            node.setBlock(block());
        }else{
            node.setBlock(emptyBlock());
        }

        return node;
    }

    private Node parseAssignment() {
        Token token = expect(Assignment.class);
        Token assignmentToken = (Assignment) token;
        Node node = new AssigmentNode();
        node.setName(assignmentToken.getName());
        node.setValue(assignmentToken.getValue());
        node.setLineNumber(assignmentToken.getStartLineNumber());
        node.setFileName(filename);
        return node;
    }
    //done
    private Node parseTag() {
        Token token = advance();
        String name = token.getValue();
        TagNode tagNode = new TagNode();
        tagNode.setName(name);
        tagNode.setBlock(emptyBlock());
        tagNode.setLineNumber(token.getStartLineNumber());
        tagNode.setColumn(token.getStartColumn());
        tagNode.setFileName(filename);
        tagNode.setValue(name);
        return this.tag(tagNode,true);
    }
    private Node tag(AttrsNode tagNode) {
        return tag(tagNode,false);
    }
    //done
    private Node tag(AttrsNode tagNode,boolean selfClosingAllowed) {
        // ast-filter look-ahead
        boolean seenAttrs = false;
        while (true) {
            Token incomingToken = peek();
            if (incomingToken instanceof CssId) {
                Token tok = advance();
                tagNode.setAttribute("id", tok.getValue(), false);
                continue;
            } else if (incomingToken instanceof CssClass) {
                Token tok = advance();
                tagNode.setAttribute("class", tok.getValue(), false);
                continue;
            } else if (incomingToken instanceof StartAttributes) {
                if (seenAttrs) {
                    throw new PugParserException(filename, line(), templateLoader, this.filename + ", line " + this.peek().getStartLineNumber() + ":\nYou should not have jade tags with multiple attributes.");
                    //console.warn(this.filename + ', line ' + this.peek().line + ':\nYou should not have jade tags with multiple attributes.');
                }
                seenAttrs = true;
                attrs(tagNode);

                continue;
            } else if (incomingToken instanceof AttributesBlock) {
                Token tok = this.advance();
                tagNode.addAttributes(tok.getValue());
            } else {
                break;
            }
        }

        // check immediate '.'
        if (peek() instanceof Dot) {
            tagNode.setTextOnly(true);
            advance();
        }

        // (text | code | ':')?
        if (peek() instanceof Text || peek() instanceof InterpolatedCode) {
            Node node = parseText();
            if(node instanceof BlockNode){
                BlockNode block = (BlockNode) node;
                tagNode.getBlock().getNodes().addAll(block.getNodes());
            }else {
                tagNode.getBlock().push(node);
            }
        } else if (peek() instanceof Expression) {
            tagNode.getBlock().push(parseCode(true));
        } else if (peek() instanceof Colon) {
            Token next = advance();
            Node node = parseExpr();
            if(node instanceof BlockNode) {
                tagNode.setBlock(node);
            }else{
                LinkedList<Node> nodes = new LinkedList<>();
                nodes.add(node);
                tagNode.setBlock(initBlock(tagNode.getLineNumber(), nodes));
            }
        }else if(peek() instanceof Slash){
            if(selfClosingAllowed){
                advance();
                tagNode.setSelfClosing(true);
            }
        }

        // newline*
        while (peek() instanceof Newline) {
            advance();
        }
        if (tagNode.isTextOnly()) {
            Node block = this.parseTextBlock();
            if (block == null)
                block = emptyBlock(tagNode.getLineNumber());
            tagNode.setBlock(block);
        } else if (peek() instanceof Indent) {
            BlockNode block = block();
            for (int i = 0, len = block.getNodes().size(); i < len; ++i) {
                tagNode.getBlock().push(block.getNodes().get(i));
            }
        }

        return tagNode;

    }
    //done
    private void attrs(AttrsNode attrsNode){
        expect(StartAttributes.class);

        Token token = advance();
        while(token instanceof Attribute){
            Attribute attr = (Attribute) token;
            String name = attr.getName();
            Object value = attr.getAttributeValue();
            attrsNode.setAttribute(name, value, attr.isEscaped());

            token = advance();
        }
        defer(token);
        expect(EndAttributes.class);
    }

    private Node[] parseInlineTagsInText(String str) {
        int line = this.line();
        Matcher matcher = Pattern.compile("(\\\\)?#\\[((?:.|\\n)*)$").matcher(str);
        TextNode text = new TextNode();
        text.setLineNumber(line);
        text.setFileName(filename);
        if (matcher.find(0) && matcher.groupCount() > 1) {
            if (matcher.group(1) != null) { // escape
                text.setValue(str.substring(0, matcher.start()) + "#[");//Not sure if Matcher.end() is correct
                Node[] rest = this.parseInlineTagsInText(matcher.group(2));
                if (rest[0] instanceof TextNode) {
                    text.setValue(text.getValue() + rest[0].getValue());
                    rest = ArrayUtils.remove(rest, 0);
                }
                Node[] textNodes = {text};
                return ArrayUtils.addAll(textNodes, rest);
            } else {
                text.setValue(str.substring(0, matcher.start()));//Not sure if Matcher.end() is correct
                Node[] textNodes = {text};
                Node[] buffer = textNodes;
                String rest = matcher.group(2);
                CharacterParser.Match range = null;
                try {
                    range = characterParser.parseMax(rest);
                } catch (CharacterParser.SyntaxError syntaxError) {
                    throw new PugParserException(this.filename, line, templateLoader, " See " + matcher.group(0));
                }
                Parser inner = null;
                try {
                    inner = new Parser(range.getSrc(), this.filename, this.templateLoader, this.expressionHandler); //Need to be reviewed
                } catch (IOException e) {
                    throw new PugParserException(this.filename, line, templateLoader, "Could not parse text");
                }
                buffer = ArrayUtils.add(buffer, inner.parse());
                return ArrayUtils.addAll(buffer, this.parseInlineTagsInText(rest.substring(range.getEnd() + 1)));
            }
        } else {
            text.setValue(str);
            Node[] textNodes = {text};
            return textNodes;
        }
    }
    //done
    private BlockNode parseTextBlock() {
        Token token = accept(StartPipelessText.class);
        if(token == null)
            return null;

        BlockNode blockNode = emptyBlock(token.getStartLineNumber());
        while(!(peek() instanceof EndPipelessText)){
            token = advance();
            if(token instanceof Text){
                TextNode textNode = new TextNode();
                textNode.setValue(token.getValue());
                textNode.setLineNumber(token.getStartLineNumber());
                textNode.setColumn(token.getStartColumn());
                textNode.setFileName(this.filename);
                blockNode.getNodes().add(textNode);
            }else if(token instanceof Newline){
                TextNode textNode = new TextNode();
                textNode.setValue("\n");
                textNode.setLineNumber(token.getStartLineNumber());
                textNode.setColumn(token.getStartColumn());
                textNode.setFileName(this.filename);
                blockNode.getNodes().add(textNode);
            }else if(token instanceof StartPugInterpolation){
                blockNode.getNodes().add(parseExpr());
                expect(EndPugInterpolation.class);
            }else if(token instanceof InterpolatedCode){
                ExpressionNode expressionNode = new ExpressionNode();
                expressionNode.setValue(token.getValue());
                expressionNode.setBuffer(token.isBuffer());
                expressionNode.setEscape(((InterpolatedCode) token).isMustEscape());
                expressionNode.setInline(true);
                expressionNode.setLineNumber(token.getStartLineNumber());
                expressionNode.setColumn(token.getStartColumn());
                expressionNode.setFileName(this.filename);
                blockNode.getNodes().add(expressionNode);
            }else{
                throw error("INVALID_TOKEN","Unexpected token type: " + token.getType(),token);
            }
        }
        advance();
        return blockNode;
    }

    //could be done
    private Node parseConditional() {
        If conditionalToken = (If) expect(If.class);
        ConditionalNode conditional = new ConditionalNode();
        conditional.setLineNumber(conditionalToken.getStartLineNumber());
        conditional.setColumn(conditionalToken.getStartColumn());
        conditional.setFileName(filename);

        List<IfConditionNode> conditions = conditional.getConditions();

        IfConditionNode main = new IfConditionNode(conditionalToken.getValue(), conditionalToken.getStartLineNumber());
        main.setInverse(conditionalToken.isInverseCondition());
        if(peek() instanceof Indent){
            main.setBlock(block());
        }else{
            main.setBlock(emptyBlock());
        }
        conditions.add(main);


        while (true) {
            if(peek() instanceof Newline){
                expect(Newline.class);
            }else if (peek() instanceof ElseIf) {
                ElseIf token = (ElseIf) expect(ElseIf.class);
                IfConditionNode elseIf = new IfConditionNode(token.getValue(), token.getStartLineNumber());
                if(peek() instanceof Indent){
                    elseIf.setBlock(block());
                }else{
                    elseIf.setBlock(emptyBlock());
                }
                conditions.add(elseIf);
            }else if(peek() instanceof Else){
                Else token = (Else) expect(Else.class);
                IfConditionNode elseNode = new IfConditionNode(null, token.getStartLineNumber());
                elseNode.setDefault(true);
                if(peek() instanceof Indent){
                    elseNode.setBlock(block());
                }else{
                    elseNode.setBlock(emptyBlock());
                }
                conditions.add(elseNode);

                break;
            }else{
                break;
            }
        }

        return conditional;
    }

    //done
    private BlockNode parseBlockExpansion() {
        Token token = accept(Colon.class);
        if (token!=null) {
            Node node = this.parseExpr();
            if(node instanceof BlockNode){
                return (BlockNode) node;
            }else{
                LinkedList<Node> nodes = new LinkedList<>();
                nodes.add(node);
                return initBlock(node.getLineNumber(), nodes);
            }
        } else {
            return this.block();
        }
    }

    //done
    private CaseNode parseCase() {
        Token token = expect(CaseToken.class);
        String val = token.getValue();
        CaseNode node = new CaseNode();
        node.setValue(val);
        node.setLineNumber(token.getStartLineNumber());
        node.setColumn(token.getStartColumn());
        node.setFileName(this.filename);

        Node block = emptyBlock(token.getStartLineNumber()+1);
        expect(Indent.class);
        while (!(peek() instanceof Outdent)) {
            if (peek() instanceof Comment) {
                advance();
            } else if (peek() instanceof Newline) {
                advance();
            } else if (peek() instanceof When) {
                block.push(parseWhen());
            } else if (peek() instanceof Default) {
                block.push(parseDefault());
            } else {
                throw error("INVALID_TOKEN", "Unexpected token \"" + this.peek() + "\", expected \"when\", \"default\" or \"newline\"",peek());
            }
        }
        expect(Outdent.class);
        node.setBlock(block);
        return node;
    }

    /**
     * when
     */
    //done
    private Node parseWhen() {
        Token token = this.expect(When.class);
        String val = token.getValue();
        CaseNode.When when = new CaseNode.When();
        when.setValue(val);
        when.setLineNumber(token.getStartLineNumber());
        when.setColumn(token.getStartColumn());
        when.setFileName(this.filename);
        if (!(this.peek() instanceof Newline)) {
            when.setBlock(this.parseBlockExpansion());
        }
        return when;

    }

    /**
     * default
     */
    //done
    private Node parseDefault() {
        Token token = expect(Default.class);
        Node when = new CaseNode.When();
        when.setValue("default");
        when.setBlock(this.parseBlockExpansion());
        when.setLineNumber(token.getStartLineNumber());
        when.setColumn(token.getStartColumn());
        when.setFileName(this.filename);
        return when;
    }

    private CaseConditionNode parseCaseCondition() {
        CaseConditionNode node = new CaseConditionNode();
        Token token = null;
        if (peek() instanceof When) {
            token = expect(When.class);
        } else {
            token = expect(Default.class);
            node.setDefault(true);
        }
        node.setLineNumber(token.getStartLineNumber());
        node.setFileName(filename);
        node.setValue(token.getValue());
        node.setBlock(blockExpansion());
        return node;
    }

    //done
    private Node parseCode() {
        return parseCode(false);
    }

    //done
    private Node parseCode(boolean noBlock) {
        Token token = expect(Expression.class);
        Expression expressionToken = (Expression) token;
        ExpressionNode codeNode = new ExpressionNode();
        codeNode.setValue(expressionToken.getValue());
        codeNode.setBuffer(expressionToken.isBuffer());
        codeNode.setEscape(expressionToken.isEscape());
        codeNode.setInline(noBlock);
        codeNode.setLineNumber(expressionToken.getStartLineNumber());
        codeNode.setColumn(expressionToken.getStartColumn());
        codeNode.setFileName(filename);
        if(noBlock)
            return codeNode;
        boolean block;
        block = peek() instanceof Indent;
        if (block) {
            if(token.isBuffer()){
                throw error("BLOCK_IN_BUFFERED_CODE", "Buffered code cannot have a block attached to it",peek());
            }
            codeNode.setBlock(block());
        }
        return codeNode;
    }
    //done
    private Node parseDoctype() {
        Doctype doctype = (Doctype) expect(Doctype.class);
        DoctypeNode doctypeNode = new DoctypeNode();
        doctypeNode.setValue(doctype.getValue());
        doctypeNode.setLineNumber(doctype.getStartLineNumber());
        doctypeNode.setColumn(doctype.getStartColumn());
        doctypeNode.setFileName(this.filename);
        return doctypeNode;
    }
    //done
    private Node parseIncludeFilter(){
        Filter token = (Filter) expect(Filter.class);
        IncludeFilterNode includeFilter = new IncludeFilterNode();
        includeFilter.setValue(token.getValue());
        includeFilter.setLineNumber(token.getStartLineNumber());
        includeFilter.setColumn(token.getStartColumn());
        includeFilter.setFileName(this.filename);
        if(peek() instanceof StartAttributes){
            attrs(includeFilter);
        }

        return includeFilter;
    }
    //done
    private Node parseFilter() {
        Filter filterToken = (Filter) expect(Filter.class);

        FilterNode node = new FilterNode();
        node.setValue(filterToken.getValue());
        node.setLineNumber(line());
        node.setFileName(filename);
        node.setColumn(filterToken.getStartColumn());

        if(peek() instanceof StartAttributes){
            attrs(node);
        }
        BlockNode blockNode;
        if(peek() instanceof Text){
            Token textToken = advance();
            LinkedList<Node> nodes = new LinkedList<>();
            TextNode textNode = new TextNode();
            textNode.setValue(filterToken.getValue());
            textNode.setLineNumber(textToken.getStartLineNumber());
            textNode.setColumn(textToken.getStartColumn());
            textNode.setFileName(this.filename);
            nodes.add(textNode);
            blockNode = initBlock(textToken.getStartLineNumber(), nodes);
        }else if(peek() instanceof Filter){
            LinkedList<Node> nodes = new LinkedList<>();
            nodes.add(parseFilter());
            blockNode = initBlock(filterToken.getStartLineNumber(),nodes);
        }else{
            BlockNode textBlock = parseTextBlock();
            if(textBlock!=null) {
                blockNode = textBlock;
            }
            else {
                blockNode = emptyBlock(filterToken.getStartLineNumber());
            }
        }

        node.setBlock(blockNode);
        return node;
    }

    private Node parseASTFilter() {
        Token token = expect(Filter.class);
        Filter filterToken = (Filter) token;
        AttributeList attr = (AttributeList) accept(AttributeList.class);

        token = expect(Colon.class);

        FilterNode node = new FilterNode();
        node.setValue(filterToken.getValue());
        node.setBlock(block());
        node.setLineNumber(line());
        node.setFileName(filename);
        node.setAttributes(convertToNodeAttributes(attr));
        return node;
    }

    private List<Attr> convertToNodeAttributes(AttributeList attr) {
        List<Attribute> attributes = attr.getAttributes();
        List<Attr> attributeNodes = new LinkedList<Attr>();
        for (Attribute attribute : attributes) {
            attributeNodes.add(new Attr(attribute.getName(), attribute.getValue(), attribute.isEscaped()));
        }
        return attributeNodes;
    }

    private Token lookahead(int i) {
        return lexer.lookahead(i);
    }

    private Token peek() {
        return lookahead(0);
    }

    private void skip(int n) {
        while (n > 0) {
            lexer.advance();
            n = n - 1;
        }
    }

    private Token advance() {
        return lexer.advance();
    }
    private void defer(Token token) {
        lexer.defer(token);
    }

    @SuppressWarnings("rawtypes")
    private Token accept(Class clazz) {
        if (this.peek().getClass().equals(clazz)) {
            return lexer.advance();
        }
        return null;
    }

    private int line() {
        return lexer.getLineno();
    }

    @SuppressWarnings("rawtypes")
    private Token expect(Class expectedTokenClass) {
        Token t = this.peek();
        if (t.getClass().equals(expectedTokenClass)) {
            return advance();
        } else {
            throw error("INVALID_TOKEN","expected \"" + expectedTokenClass.toString() + "\", but got "+peek().getType()+"\"",peek());
        }
    }
    public Map<String, BlockNode> getBlocks() {
        return blocks;
    }

    public void setBlocks(LinkedHashMap<String, BlockNode> blocks) {
        this.blocks = blocks;
    }

    public LinkedList<Parser> getContexts() {
        return contexts;
    }

    public void setContexts(LinkedList<Parser> contexts) {
        this.contexts = contexts;
    }

    public void setMixins(HashMap mixins) {
        this.mixins = mixins;
    }
}
