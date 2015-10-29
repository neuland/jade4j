package de.neuland.jade4j.parser;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.neuland.jade4j.expression.ExpressionHandler;
import de.neuland.jade4j.lexer.token.*;
import de.neuland.jade4j.parser.node.*;
import de.neuland.jade4j.parser.node.BlockCommentNode;
import de.neuland.jade4j.util.CharacterParser;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import de.neuland.jade4j.exceptions.JadeParserException;
import de.neuland.jade4j.lexer.Assignment;
import de.neuland.jade4j.lexer.Each;
import de.neuland.jade4j.lexer.Lexer;
import de.neuland.jade4j.template.TemplateLoader;

public class Parser {

    public static final Pattern FILE_EXTENSION_PATTERN = Pattern.compile(".*\\.\\w+$");
    private Lexer lexer;
    private Map<String, Node> blocks = new LinkedHashMap<String, Node>();
    private String[] textOnlyTags = {"script", "style"};
    private Integer _spaces = null;
    private final TemplateLoader templateLoader;
    private ExpressionHandler expressionHandler;
    private Parser extending;
    private final String filename;
    private LinkedList<Parser> contexts = new LinkedList<Parser>();
    private CharacterParser characterParser;
    private int inMixin = 0;
    private HashMap mixins = new HashMap<String,MixinNode>();

    public Parser(String filename, TemplateLoader templateLoader,ExpressionHandler expressionHandler) throws IOException {
        this.filename = filename;
        this.templateLoader = templateLoader;
        this.expressionHandler = expressionHandler;
        lexer = new Lexer(filename, templateLoader,expressionHandler);
        characterParser = new CharacterParser();
        getContexts().push(this);
    }
    public Parser(String src, String filename, TemplateLoader templateLoader,ExpressionHandler expressionHandler) throws IOException {
        this.filename = filename;
        this.templateLoader = templateLoader;
        this.expressionHandler = expressionHandler;
        lexer = new Lexer(src,filename, templateLoader,expressionHandler);
        characterParser = new CharacterParser();
        getContexts().push(this);
    }

    public Node parse() {
        BlockNode block = new BlockNode();
        block.setLineNumber(lexer.getLineno());
        block.setFileName(filename);
        while (!(peek() instanceof Eos)) {
            if (peek() instanceof Newline) {
                advance();
            } else {
                Node expr = parseExpr();
                if (expr != null) {
                    block.push(expr);
                }
            }
        }
        if (extending != null) {
            getContexts().push(extending);
            Node rootNode = extending.parse();
            getContexts().pop();
            return rootNode;
        }

        return block;
    }

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
        if (token instanceof Text) {
            return parseText();
        }
        if (token instanceof Each) {
            return parseEach();
        }
//        if (token instanceof Code) {
//            return parseCode();
//        }
        if (token instanceof Expression) {
            return parseCode();
        }
        if (token instanceof BlockCode) {
            return parseBlockCode();
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
        if (token instanceof While) {
            return parseWhile();
        }
        if (token instanceof If) {
            return parseConditional();
        }
        if (token instanceof Assignment) {
            return parseAssignment();
        }

        throw new JadeParserException(filename, lexer.getLineno(), templateLoader, token);
    }
    /**
     * block code
     */

    private Node parseBlockCode(){
      Token tok = this.expect(BlockCode.class);
      CodeNode node;
      Token body = this.peek();
      String text;
      if (body instanceof PipelessText) {
        this.advance();
        text = String.join("\n",body.getValues());
      } else {
        text = "";
      }
        node = new CodeNode();
        node.setValue(text);
        node.setLineNumber(tok.getLineNumber());
        return node;
    }

    private Node parseComment() {
        Token token = expect(Comment.class);

        Node block = this.parseTextBlock();
        if (block != null ) {
            BlockCommentNode node = new BlockCommentNode();
            node.setBlock(block);
            node.setBuffered(token.isBuffer());
            node.setLineNumber(token.getLineNumber());
            node.setFileName(filename);
            node.setValue(token.getValue());
            return node;
        } else {
            CommentNode node = new CommentNode();
            node.setBuffered(token.isBuffer());
            node.setLineNumber(token.getLineNumber());
            node.setFileName(filename);
            node.setValue(token.getValue());
            return node;
        }
    }

    private Node parseMixin() {
        Mixin mixinToken = (Mixin) expect(Mixin.class);
        MixinNode node = new MixinNode();
        node.setName(mixinToken.getValue());
        node.setLineNumber(mixinToken.getLineNumber());
        node.setFileName(filename);

        if (StringUtils.isNotBlank(mixinToken.getArguments())) {
            node.setArguments(mixinToken.getArguments());
        }

        if (peek() instanceof Indent) {
            this.inMixin++;
            node.setBlock(block());
            node.setCall(false);
            this.mixins.put(mixinToken.getValue(),node);
            this.inMixin--;
            return node;
        }else{
            node.setCall(true);
            return node;
        }
    }

    private Node parseCall() {
        Token token = expect(Call.class);
        Call callToken = (Call) token;
        MixinNode mixin = new MixinNode();
        mixin.setBlock(new BlockNode());
        mixin.setName(callToken.getValue());
        mixin.setLineNumber(callToken.getLineNumber());
        mixin.setFileName(filename);

        if (StringUtils.isNotBlank(callToken.getArguments())) {
            mixin.setArguments(callToken.getArguments());
        }
//        this.tag(mixin);
//        if(mixin.)
        if(mixin.hasBlock()&&!mixin.getBlock().hasNodes())
            mixin.setBlock(null);
        return mixin;
    }

    private Node parseCssClassOrId() {
        Token tok = advance();
        Tag div = new Tag("div", line());
        lexer.defer(div);
        lexer.defer(tok);
        return parseExpr();
    }

    private Node parseBlock() {
        Token token = expect(Block.class);
        Block block = (Block) token;
        String mode = block.getMode();
        String name = block.getValue().trim();

        Node blockNode;
        if (peek() instanceof Indent) {
            blockNode = block();
        } else {
            blockNode = new BlockNode();
            blockNode.setLineNumber(block.getLineNumber());
            blockNode.setFileName(filename);
        }

        ((BlockNode) blockNode).setMode(mode);

        if (blocks.containsKey(name)) {
            BlockNode prev = (BlockNode) blocks.get(name);
            if ("append".equals(prev.getMode())) {
                blockNode.getNodes().addAll(prev.getNodes());
            }
            if ("prepend".equals(prev.getMode())) {
                blockNode.getNodes().addAll(0, prev.getNodes());
            }
            if ("replace".equals(prev.getMode())) {
                blockNode = prev;
            }
        }

        blocks.put(name, blockNode);
        return blockNode;
    }

    private Node parseMixinBlock(){
        Token tok = expect(MixinBlock.class);
        if(this.inMixin != 0){
            throw new JadeParserException(filename, lexer.getLineno(), templateLoader, "Anonymous blocks are not allowed unless they are part of a mixin.");
        }
        return new MixinBlockNode();
    }

    private Node parseInclude() {
        Token token = expect(Include.class);
        Include includeToken = (Include) token;
        String templateName = includeToken.getValue().trim();

        String extension = FilenameUtils.getExtension(templateName);
        if (!"".equals(extension) && !"jade".equals(extension)) {
            try {
                if(includeToken.getFilter()!=null) {
                    Reader reader = templateLoader.getReader(resolvePath(templateName));
                    FilterNode node = new FilterNode();
                    node.setValue(includeToken.getFilter());
                    node.setLineNumber(line());
                    node.setFileName(filename);
                    TextNode text = new TextNode();
                    text.setValue(IOUtils.toString(reader));
                    BlockNode block = new BlockNode();
                    LinkedList<Node> nodes = new LinkedList<Node>();
                    nodes.add(text);
                    block.setNodes(nodes);
                    if(block!=null)
                        node.setTextBlock(block);
                    else{
                        node.setTextBlock(new BlockNode());
                    }
                    return node;
                }else{
                    Reader reader = templateLoader.getReader(resolvePath(templateName));
                    LiteralNode node = new LiteralNode();
                    node.setLineNumber(lexer.getLineno());
                    node.setFileName(filename);
                    node.setValue(IOUtils.toString(reader));
                    return node;
                }
            } catch (IOException e) {
                throw new JadeParserException(filename, lexer.getLineno(), templateLoader, "the included file [" + templateName + "] could not be opened\n" + e.getMessage());
            }
        }

        Parser parser = createParser(templateName);
        parser.setBlocks(blocks);
        contexts.push(parser);
        Node ast = parser.parse();
        contexts.pop();

        if (peek() instanceof Indent && ast instanceof BlockNode) {
            ((BlockNode) ast).getIncludeBlock().push(block());
        }

        return ast;
    }

    private Node parseExtends() {
        Token token = expect(ExtendsToken.class);
        ExtendsToken extendsToken = (ExtendsToken) token;
        String templateName = extendsToken.getValue().trim();

        Parser parser = createParser(templateName);

        parser.setBlocks(blocks);
        parser.setContexts(contexts);
        extending = parser;

        LiteralNode node = new LiteralNode();
        node.setValue("");
        return node;
    }

    private Parser createParser(String templateName) {
        templateName = ensureJadeExtension(templateName);
        try {
            return new Parser(resolvePath(templateName), templateLoader,expressionHandler);
        } catch (IOException e) {
            throw new JadeParserException(filename, lexer.getLineno(), templateLoader, "the template [" + templateName
                    + "] could not be opened\n" + e.getMessage());
        }
    }

    private String ensureJadeExtension(String templateName) {
        if (!"jade".equals(FilenameUtils.getExtension(templateName))) {
            return templateName + ".jade";
        }
        return templateName;
    }

    private String resolvePath(String templateName) {
        URI currentUri = URI.create(filename);
        URI templateUri = currentUri.resolve(templateName);
        return templateUri.toString();
    }

    private BlockNode parseYield() {
        advance();
        BlockNode block = (BlockNode) new BlockNode();
        block.setLineNumber(lexer.getLineno());
        block.setFileName(filename);
        block.setYield(true);
        return block;
    }
    private Node parseInterpolation() {
        Token token = advance();
        String name = token.getValue();
        TagNode tagNode = new TagNode();
        tagNode.setLineNumber(lexer.getLineno());
        tagNode.setFileName(filename);
        tagNode.setName(name);
        tagNode.setValue(name);
        tagNode.setBuffer(true);
        return this.tag(tagNode);
    }

    private Node blockExpansion() {
        if (peek() instanceof Colon) {
            Token token = expect(Colon.class);
            Colon colon = (Colon) token;
            BlockNode block = new BlockNode();
            block.setLineNumber(colon.getLineNumber());
            block.setFileName(filename);
            block.getNodes().add(parseExpr());
            return block;
        }
        return block();
    }

    private BlockNode block() {
        BlockNode block = new BlockNode();
        block.setLineNumber(lexer.getLineno());
        block.setFileName(filename);
        expect(Indent.class);
        while (!(peek() instanceof Outdent)) {
            if (peek() instanceof Newline) {
                advance();
            } else {
                Node parseExpr = this.parseExpr();
                parseExpr.setFileName(filename);
                if (parseExpr != null) {
                    block.push(parseExpr);
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

    private Node parseText() {
        Token tok = expect(Text.class);
        Node[] tokens = this.parseInlineTagsInText(tok.getValue());
        if (tokens.length == 1) return tokens[0];
        BlockNode node = new BlockNode();
        for (int i = 0; i < tokens.length; i++) {
          node.push(tokens[i]);
        }
        node.setValue(tok.getValue());
        node.setLineNumber(tok.getLineNumber());
        node.setFileName(filename);
        return node;
    }

    private Node parseEach() {
        Token token = expect(Each.class);
        Each eachToken = (Each) token;
        EachNode node = new EachNode();
        node.setValue(eachToken.getValue());
        node.setKey(eachToken.getKey());
        node.setCode(eachToken.getCode());
        node.setLineNumber(eachToken.getLineNumber());
        node.setFileName(filename);
        node.setBlock(block());
        if (peek() instanceof Else || peek() instanceof Expression) {
            advance();
            node.setElseNode(block());
        }
        return node;
    }

    private Node parseWhile() {
        Token token = expect(While.class);
        While whileToken = (While) token;
        WhileNode node = new WhileNode();
        node.setValue(whileToken.getValue());
        node.setLineNumber(whileToken.getLineNumber());
        node.setFileName(filename);
        BlockNode block = block();
        if(block!=null)
            node.setBlock(block);
        else
            node.setBlock(new BlockNode());
        return node;
    }

    private Node parseAssignment() {
        Token token = expect(Assignment.class);
        Token assignmentToken = (Assignment) token;
        Node node = new AssigmentNode();
        node.setName(assignmentToken.getName());
        node.setValue(assignmentToken.getValue());
        node.setLineNumber(assignmentToken.getLineNumber());
        node.setFileName(filename);
        return node;
    }

    private Node parseTag() {
        Token token = advance();
        String name = token.getValue();
        TagNode tagNode = new TagNode();
        tagNode.setLineNumber(lexer.getLineno());
        tagNode.setFileName(filename);
        tagNode.setName(name);
        tagNode.setValue(name);
        tagNode.setSelfClosing(token.isSelfClosing());
        return this.tag(tagNode);
    }

    private Node tag(TagNode tagNode){
        // ast-filter look-ahead
        boolean seenAttrs = false;
        while (true) {
            Token incomingToken = peek();
            if (incomingToken instanceof CssId) {
                Token tok = advance();
                tagNode.setAttribute("id", tok.getValue(),false);
                continue;
            } else if (incomingToken instanceof CssClass) {
                Token tok = advance();
                tagNode.setAttribute("class", tok.getValue(),false);
                continue;
            } else if (incomingToken instanceof AttributeList) {
                if (seenAttrs) {
                    //console.warn(this.filename + ', line ' + this.peek().line + ':\nYou should not have jade tags with multiple attributes.');
                }
                seenAttrs = true;
                AttributeList tok = (AttributeList) advance();
                Map<String, Object> attrs = tok.getAttributes();
                tagNode.setSelfClosing(tok.isSelfClosing());
                for (String name : attrs.keySet()) {
                    Object value = attrs.get(name);
                    if(value instanceof ValueString) {
                        ValueString valueString = (ValueString) value;
                        tagNode.setAttribute(name, valueString.getValue(),valueString.isEscape());
                    }else if(value instanceof ExpressionString) {
                        ExpressionString expressionString = (ExpressionString) value;
                        tagNode.setAttribute(name, value, expressionString.isEscape());
                    }else if(value instanceof Boolean){
                        tagNode.setAttribute(name, value, false);
                    }else if(value instanceof String){
                        tagNode.setAttribute(name, value, false);
                    }

                }
                continue;
            } else if (incomingToken instanceof AttributesBlock) {
                Token tok = this.advance();
                tagNode.addAttributes(tok.getValue());
                break;
            } else {
                break;
            }
        }

        // check immediate '.'
        boolean dot = false;
        if (peek() instanceof Dot) {
            dot = true;
            tagNode.setTextOnly(true);
            advance();
        }

        // (text | code | ':')?
        if (peek() instanceof Text) {
            tagNode.getBlock().push(parseText());
        } else if (peek() instanceof Expression) {
            tagNode.setCodeNode(parseCode());
        } else if (peek() instanceof Colon) {
            Token next = advance();
            BlockNode block = new BlockNode();
            block.setLineNumber(next.getLineNumber());
            block.setFileName(filename);
            block.push(parseExpr());
            tagNode.setBlock(block);
        }

        // newline*
        while (peek() instanceof Newline) {
            advance();
        }
        if (tagNode.isTextOnly()) {
            Node block = this.parseTextBlock();
            if(block == null)
                block = new BlockNode();
            tagNode.setBlock(block);
        }else if(peek() instanceof Indent){
            BlockNode block = block();
            if(!tagNode.hasBlock())
                tagNode.setBlock(new BlockNode());
            for (int i = 0, len = block.getNodes().size(); i < len; ++i) {
                tagNode.getBlock().push(block.getNodes().get(i));
            }
        }
//        else{
//            if (Arrays.asList(textOnlyTags).contains(tagNode.getName())) {
//                tagNode.setTextOnly(true);
//            }
//        }
//
//        // script special-case
//        if ("script".equals(tagNode.getName())) {
//            String type = tagNode.getAttribute("type");
//            if (!dot && StringUtils.isNotBlank(type)) {
//                String cleanType = type.replaceAll("^['\"]|['\"]$", "");
//                if (!"text/javascript".equals(cleanType)) {
//                    tagNode.setTextOnly(false);
//                }
//            }
//        }
//
//        if (peek() instanceof Indent) {
//            if (tagNode.isTextOnly()) {
//                lexer.setPipeless(true);
//                tagNode.setTextNode(parseTextBlock());
//                lexer.setPipeless(false);
//            } else {
//                Node blockNode = block();
//                if (tagNode.hasBlock()) {
//                    tagNode.getBlock().getNodes().addAll(blockNode.getNodes());
//                } else {
//                    tagNode.setBlock(blockNode);
//                }
//            }
//        }

        return tagNode;

    }
    private Node[] parseInlineTagsInText(String str) {
        int line = this.line();
        Matcher matcher = Pattern.compile("(\\\\)?#\\[((?:.|\\n)*)$").matcher(str);
        if (matcher.find(0) && matcher.groupCount()>1) {
            if (matcher.group(1) != null) { // escape
                TextNode text = new TextNode();
                text.setValue(str.substring(0, matcher.start()) + "#[");//Not sure if Matcher.end() is correct
                text.setLineNumber(line);
                Node[] rest = this.parseInlineTagsInText(matcher.group(2));
                if (rest[0] instanceof TextNode) {
                    text.setValue(text.getValue() + rest[0].getValue());
                    rest = ArrayUtils.remove(rest,0);
                }
                Node[] textNodes = {text};
                return ArrayUtils.addAll(textNodes, rest);
            } else {
                TextNode text = new TextNode();
                text.setValue(str.substring(0, matcher.start()));//Not sure if Matcher.end() is correct
                text.setLineNumber(line);
                Node[] textNodes = {text};
                Node[] buffer = textNodes;
                String rest = matcher.group(2);
                CharacterParser.Match range = characterParser.parseMax(rest);
                Parser inner = null;
                try {
                    inner = new Parser(range.getSrc(), this.filename, this.templateLoader,this.expressionHandler); //Need to be reviewed
                } catch (IOException e) {
                    throw new JadeParserException(this.filename,line,templateLoader,"Could not parse text");
                }
                buffer = ArrayUtils.add(buffer,inner.parse());
                return ArrayUtils.addAll(buffer, this.parseInlineTagsInText(rest.substring(range.getEnd() + 1)));
            }
        } else {
            TextNode text = new TextNode();
            text.setValue(str);
            text.setLineNumber(line);
            Node[] textNodes = {text};
            return textNodes;
        }
    }

    private Node parseTextBlock() {
        BlockNode blockNode = new BlockNode();
        blockNode.setLineNumber(line());
        blockNode.setFileName(filename);
        Token body  = peek();
        if(!(body instanceof PipelessText)){
            return null;
        }
        this.advance();
        ArrayList<String> values = body.getValues();
        Node[] textNodes = {};
        for (String value : values) {
            textNodes = ArrayUtils.addAll(textNodes,parseInlineTagsInText(value));
        }
        blockNode.setNodes(new LinkedList<Node>(Arrays.asList(textNodes)));
        return blockNode;
    }

    private Node parseConditional() {
        If conditionalToken = (If) expect(If.class);
        ConditionalNode conditional = new ConditionalNode();
        conditional.setLineNumber(conditionalToken.getLineNumber());
        conditional.setFileName(filename);

        List<IfConditionNode> conditions = conditional.getConditions();

        IfConditionNode main = new IfConditionNode(conditionalToken.getValue(), conditionalToken.getLineNumber());
        main.setInverse(conditionalToken.isInverseCondition());
        main.setBlock(block());
        conditions.add(main);

        while (peek() instanceof ElseIf) {
            ElseIf token = (ElseIf) expect(ElseIf.class);
            IfConditionNode elseIf = new IfConditionNode(token.getValue(), token.getLineNumber());
            elseIf.setBlock(block());
            conditions.add(elseIf);
        }

        if (peek() instanceof Else) {
            Else token = (Else) expect(Else.class);
            IfConditionNode elseNode = new IfConditionNode(null, token.getLineNumber());
            elseNode.setDefault(true);
            elseNode.setBlock(block());
            conditions.add(elseNode);
        }

        return conditional;
    }

    private Node parseCase() {
        Token token = expect(CaseToken.class);
        CaseToken caseToken = (CaseToken) token;
        CaseNode node = new CaseNode();
        node.setLineNumber(caseToken.getLineNumber());
        node.setFileName(filename);
        node.setValue(caseToken.getValue());
        node.setConditions(whenBlock());
        return node;
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
        node.setLineNumber(token.getLineNumber());
        node.setFileName(filename);
        node.setValue(token.getValue());
        node.setBlock(blockExpansion());
        return node;
    }

    private Node parseCode() {
        Token token = expect(Expression.class);
        Expression expressionToken = (Expression) token;
        ExpressionNode codeNode = new ExpressionNode();
        codeNode.setValue(expressionToken.getValue());
        codeNode.setBuffer(expressionToken.isBuffer());
        codeNode.setEscape(expressionToken.isEscape());
        codeNode.setLineNumber(expressionToken.getLineNumber());
        codeNode.setFileName(filename);
        boolean block = false;
        int i = 1;
        while (lookahead(i) != null && lookahead(i) instanceof Newline)
            ++i;
        block = lookahead(i) instanceof Indent;
        if (block) {
            skip(i - 1);
            codeNode.setBlock((BlockNode) block());
        }
        return codeNode;
    }

    private Node parseDoctype() {
        Token token = expect(Doctype.class);
        Doctype doctype = (Doctype) token;
        DoctypeNode doctypeNode = new DoctypeNode();
        doctypeNode.setValue(doctype.getValue());
        return doctypeNode;
    }

    // var tok = this.expect('code')
    // , node = new nodes.Code(tok.val, tok.buffer, tok.escape)
    // , block
    // , i = 1;
    // node.line = this.line();
    // while (this.lookahead(i) && 'newline' == this.lookahead(i).type) ++i;
    // block = 'indent' == this.lookahead(i).type;
    // if (block) {
    // this.skip(i-1);
    // node.block = this.block();
    // }
    // return node;

    private Node parseFilter() {
        Token token = expect(Filter.class);
        Filter filterToken = (Filter) token;
        AttributeList attr = (AttributeList) accept(AttributeList.class);
        lexer.setPipeless(true);
        Node tNode = parseTextBlock();
        lexer.setPipeless(false);

        FilterNode node = new FilterNode();
        node.setValue(filterToken.getValue());
        node.setLineNumber(line());
        node.setFileName(filename);
        if(tNode!=null)
            node.setTextBlock(tNode);
        else{
            node.setTextBlock(new BlockNode());
        }
        if (attr != null) {
            node.setAttributes(attr.getAttributes());
        }
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
        node.setAttributes(attr.getAttributes());
        return node;
    }

    private Token lookahead(int i) {
        return lexer.lookahead(i);
    }

    private Token peek() {
        return lookahead(1);
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
            throw new JadeParserException(filename, lexer.getLineno(), templateLoader, expectedTokenClass, t.getClass());
        }
    }

    public Map<String, Node> getBlocks() {
        return blocks;
    }

    public void setBlocks(Map<String, Node> blocks) {
        this.blocks = blocks;
    }

    public LinkedList<Parser> getContexts() {
        return contexts;
    }

    public void setContexts(LinkedList<Parser> contexts) {
        this.contexts = contexts;
    }
}
