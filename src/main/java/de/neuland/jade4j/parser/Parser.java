package de.neuland.jade4j.parser;

import de.neuland.jade4j.exceptions.JadeParserException;
import de.neuland.jade4j.expression.ExpressionHandler;
import de.neuland.jade4j.lexer.Assignment;
import de.neuland.jade4j.lexer.Each;
import de.neuland.jade4j.lexer.Lexer;
import de.neuland.jade4j.lexer.token.*;
import de.neuland.jade4j.parser.node.*;
import de.neuland.jade4j.template.TemplateLoader;
import de.neuland.jade4j.util.CharacterParser;
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

            // hoist mixins
            Set<String> keySet = this.mixins.keySet();
            for (String name : keySet) {
                rootNode.getNodes().push(this.mixins.get(name));
            }
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

    private Node parseBlockCode() {
        Token tok = this.expect(BlockCode.class);
        ExpressionNode node;
        Token body = this.peek();
        String text;
        if (body instanceof PipelessText) {
            this.advance();
            text = StringUtils.join(body.getValues(), "\n");
        } else {
            text = "";
        }
        node = new ExpressionNode();
        node.setValue(text);
        node.setLineNumber(tok.getLineNumber());
        return node;
    }

    private Node parseComment() {
        Token token = expect(Comment.class);

        Node block = this.parseTextBlock();
        if (block != null) {
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
        List<String> args = node.getArguments();

        String rest;

        if (args.size() > 0) {
            Matcher matcher = Pattern.compile("^\\.\\.\\.").matcher(args.get(args.size() - 1).trim());
            if (matcher.find(0)) {
                rest = args.remove(args.size() - 1).trim().replaceAll("^\\.\\.\\.", "");
                node.setRest(rest);

            }
        }

        if (peek() instanceof Indent) {
            this.inMixin++;
            node.setBlock(block());
            node.setCall(false);
            this.mixins.put(mixinToken.getValue(), node);
            this.inMixin--;
            return node;
        } else {
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

        this.inBlock++;
        BlockNode blockNode;
        if (peek() instanceof Indent) {
            blockNode = block();
        } else {
            blockNode = new BlockNode();
            blockNode.setLineNumber(block.getLineNumber());
            blockNode.setFileName(filename);
            LiteralNode node = new LiteralNode();
            node.setValue("");
            blockNode.push(node);
        }
        this.inBlock--;
        blockNode.setName(name);
        blockNode.setLineNumber(line());

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
    }

    private Node parseMixinBlock() {
        Token tok = expect(MixinBlock.class);
        if (this.inMixin == 0) {
            throw new JadeParserException(filename, lexer.getLineno(), templateLoader, "Anonymous blocks are not allowed unless they are part of a mixin.");
        }
        return new MixinBlockNode();
    }

    private Node parseInclude() {
        Token token = expect(Include.class);
        Include includeToken = (Include) token;
        String templateName = includeToken.getValue().trim();
        String path = pathHelper.resolvePath(filename, templateName, templateLoader.getExtension());

        try {
            if (includeToken.getFilter() != null) {
                Reader reader = templateLoader.getReader(path);
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
                if (block != null)
                    node.setTextBlock(block);
                else {
                    node.setTextBlock(new BlockNode());
                }
                return node;
            }
        } catch (IOException e) {
            throw new JadeParserException(filename, lexer.getLineno(), templateLoader, "the included file [" + templateName + "] could not be opened\n" + e.getMessage());
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
                throw new JadeParserException(filename, lexer.getLineno(), templateLoader, "the included file [" + templateName + "] could not be opened\n" + e.getMessage());
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
            String resolvedPath = pathHelper.resolvePath(this.filename, templateName, templateLoader.getExtension());
            return new Parser(resolvedPath, templateLoader, expressionHandler);
        } catch (IOException e) {
            throw new JadeParserException(
                    this.filename,
                    lexer.getLineno(),
                    templateLoader,
                    "The template [" + templateName + "] could not be opened. Maybe it is located outside the base path?\n" + e.getMessage()
            );
        }
    }

    private String ensureJadeExtension(String templateName) {
        if (!templateLoader.getExtension().equals(FilenameUtils.getExtension(templateName))) {
            return templateName + "." + templateLoader.getExtension();
        }
        return templateName;
    }

    private BlockNode parseYield() {
        advance();
        BlockNode block = new BlockNode();
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
        if (peek() instanceof Else) {
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
        if (block != null)
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

    private Node tag(AttrsNode tagNode) {
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
            } else if (incomingToken instanceof AttributeList) {
                if (seenAttrs) {
                    throw new JadeParserException(filename, line(), templateLoader, this.filename + ", line " + this.peek().getLineNumber() + ":\nYou should not have jade tags with multiple attributes.");
                    //console.warn(this.filename + ', line ' + this.peek().line + ':\nYou should not have jade tags with multiple attributes.');
                }
                seenAttrs = true;
                AttributeList tok = (AttributeList) advance();
                List<Attribute> attrs = tok.getAttributes();
                tagNode.setSelfClosing(tok.isSelfClosing());

                for (Attribute attr : attrs) {
                    String name = attr.getName();
                    Object value = attr.getValue();
                    tagNode.setAttribute(name, value, attr.isEscaped());
                }
                continue;
            } else if (incomingToken instanceof AttributesBlock) {
                Token tok = this.advance();
                tagNode.addAttributes(tok.getValue());
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
            if (block == null)
                block = new BlockNode();
            tagNode.setBlock(block);
        } else if (peek() instanceof Indent) {
            BlockNode block = block();
//            if(!tagNode.hasBlock())
//                tagNode.setBlock(new BlockNode());
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
                    throw new JadeParserException(this.filename, line, templateLoader, " See " + matcher.group(0));
                }
                Parser inner = null;
                try {
                    inner = new Parser(range.getSrc(), this.filename, this.templateLoader, this.expressionHandler); //Need to be reviewed
                } catch (IOException e) {
                    throw new JadeParserException(this.filename, line, templateLoader, "Could not parse text");
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

    private Node parseTextBlock() {
        BlockNode blockNode = new BlockNode();
        blockNode.setLineNumber(line());
        blockNode.setFileName(filename);
        Token body = peek();
        if (!(body instanceof PipelessText)) {
            return null;
        }
        this.advance();
        ArrayList<String> values = body.getValues();
        Node[] textNodes = {};
        for (String value : values) {
            textNodes = ArrayUtils.addAll(textNodes, parseInlineTagsInText(value));
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

    //    var block = new nodes.Block;
//    block.line = this.line();
//    block.filename = this.filename;
//    this.expect('indent');
//    while ('outdent' != this.peek().type) {
//      switch (this.peek().type) {
//        case 'comment':
//        case 'newline':
//          this.advance();
//          break;
//        case 'when':
//          block.push(this.parseWhen());
//          break;
//        case 'default':
//          block.push(this.parseDefault());
//          break;
//        default:
//          throw new Error('Unexpected token "' + this.peek().type
//                          + '", expected "when", "default" or "newline"');
//      }
//    }
//    this.expect('outdent');
//
//    node.block = block;
//
//    return node;
    private Node parseBlockExpansion() {
        if (this.peek() instanceof Colon) {
            this.advance();
            BlockNode blockNode = new BlockNode();
            blockNode.push(this.parseExpr());
            return blockNode;
        } else {
            return this.block();
        }
    }

    private Node parseCase() {
        String val = expect(CaseToken.class).getValue();
        Node node = new CaseNode();
        node.setValue(val);
        node.setLineNumber(line());

        Node block = new BlockNode();
        block.setLineNumber(line());
        block.setFileName(filename);
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
                throw new JadeParserException(filename, lexer.getLineno(), templateLoader, "Unexpected token \"" + this.peek() + "\", expected \"when\", \"default\" or \"newline\"");
            }
        }
        expect(Outdent.class);
        node.setBlock(block);
        return node;
    }

    /**
     * when
     */

    private Node parseWhen() {
        String val = this.expect(When.class).getValue();
        CaseNode.When when = new CaseNode.When();
        when.setValue(val);
        if (!(this.peek() instanceof Newline)) {
            when.setBlock(this.parseBlockExpansion());
        }
        return when;

    }

    /**
     * default
     */

    private Node parseDefault() {
        expect(Default.class);
        Node when = new CaseNode.When();
        when.setValue("default");
        when.setBlock(this.parseBlockExpansion());
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
//        int i = 1;
//        while (lookahead(i) != null && lookahead(i) instanceof Newline)
//            ++i;
        block = peek() instanceof Indent;
        if (block) {
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
        if (tNode != null)
            node.setTextBlock(tNode);
        else {
            node.setTextBlock(new BlockNode());
        }
        if (attr != null) {
            node.setAttributes(convertToNodeAttributes(attr));
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
