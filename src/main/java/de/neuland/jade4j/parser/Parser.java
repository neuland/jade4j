package de.neuland.jade4j.parser;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.neuland.jade4j.exceptions.JadeParserException;
import de.neuland.jade4j.lexer.Assignment;
import de.neuland.jade4j.lexer.Each;
import de.neuland.jade4j.lexer.Lexer;
import de.neuland.jade4j.lexer.token.Attribute;
import de.neuland.jade4j.lexer.token.Block;
import de.neuland.jade4j.lexer.token.CaseToken;
import de.neuland.jade4j.lexer.token.Colon;
import de.neuland.jade4j.lexer.token.Comment;
import de.neuland.jade4j.lexer.token.Conditional;
import de.neuland.jade4j.lexer.token.CssClass;
import de.neuland.jade4j.lexer.token.CssId;
import de.neuland.jade4j.lexer.token.Default;
import de.neuland.jade4j.lexer.token.Doctype;
import de.neuland.jade4j.lexer.token.Dot;
import de.neuland.jade4j.lexer.token.Eos;
import de.neuland.jade4j.lexer.token.Expression;
import de.neuland.jade4j.lexer.token.ExtendsToken;
import de.neuland.jade4j.lexer.token.Filter;
import de.neuland.jade4j.lexer.token.Include;
import de.neuland.jade4j.lexer.token.Indent;
import de.neuland.jade4j.lexer.token.Mixin;
import de.neuland.jade4j.lexer.token.Newline;
import de.neuland.jade4j.lexer.token.Outdent;
import de.neuland.jade4j.lexer.token.Tag;
import de.neuland.jade4j.lexer.token.Text;
import de.neuland.jade4j.lexer.token.Token;
import de.neuland.jade4j.lexer.token.When;
import de.neuland.jade4j.lexer.token.While;
import de.neuland.jade4j.lexer.token.Yield;
import de.neuland.jade4j.parser.node.AssigmentNode;
import de.neuland.jade4j.parser.node.BlockNode;
import de.neuland.jade4j.parser.node.CaseConditionNode;
import de.neuland.jade4j.parser.node.CaseNode;
import de.neuland.jade4j.parser.node.ConditionalNode;
import de.neuland.jade4j.parser.node.DoctypeNode;
import de.neuland.jade4j.parser.node.EachNode;
import de.neuland.jade4j.parser.node.ExpressionNode;
import de.neuland.jade4j.parser.node.FilterNode;
import de.neuland.jade4j.parser.node.LiteralNode;
import de.neuland.jade4j.parser.node.MixinNode;
import de.neuland.jade4j.parser.node.Node;
import de.neuland.jade4j.parser.node.TagNode;
import de.neuland.jade4j.parser.node.TextNode;
import de.neuland.jade4j.parser.node.WhileNode;
import de.neuland.jade4j.template.TemplateLoader;

public class Parser {

	private Lexer lexer;
	private Map<String, Node> blocks = new LinkedHashMap<String, Node>();
	private String[] textOnlyTags = { "script", "style" };
	private Integer _spaces = null;
	private final TemplateLoader templateLoader;
	private Parser extending;
	private final String filename;

	public Parser(String filename, TemplateLoader templateLoader) throws IOException {
		this.filename = filename;
		this.templateLoader = templateLoader;
		lexer = new Lexer(filename, templateLoader);
	}

	public Node parse() {
		BlockNode block = new BlockNode();
		block.setLineNumber(lexer.getLineno());
		block.setFileName(filename);
		while (!(peek() instanceof Eos)) {
			if (peek() instanceof Newline) {
				nextToken();
			} else {
				Node expr = parseExpr();
				if (expr != null) {
					block.push(expr);
				}
			}
		}
		if (extending != null) {
			// TODO check this:
			// this.context(parser) ???
			Node rootNode = extending.parse();
			// TODO check this too:
			// this.context()
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
		if (token instanceof ExtendsToken) {
			return parseExtends();
		}
		if (token instanceof Include) {
			return parseInclude();
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
		if (token instanceof While) {
			return parseWhile();
		}
		if (token instanceof CssClass || token instanceof CssId) {
			return parseCssClassOrId();
		}
		if (token instanceof Conditional) {
			return parseConditional();
		}
		if (token instanceof CaseToken) {
			return parseCase();
		}
		if (token instanceof Assignment) {
			return parseAssignment();
		}
		if (token instanceof Doctype) {
			return parseDoctype();
		}
		if (token instanceof Expression) {
			return parseCode();
		}
		if (token instanceof Yield) {
			return parseYield();
		}
		throw new JadeParserException(filename, lexer.getLineno(), templateLoader, token);
	}

	private Node parseComment() {
		Token token = expect(Comment.class);

		CommentNode node;
		if (peek() instanceof Indent) {
			node = new BlockCommentNode();
			node.setBlock(block());
		} else {
			node = new CommentNode();
		}
		node.setBuffered(token.isBuffer());
		node.setLineNumber(token.getLineNumber());
		node.setFileName(filename);
		node.setValue(token.getValue());

		return node;
	}

	private Node parseMixin() {
		Token token = expect(Mixin.class);
		Mixin mixinToken = (Mixin) token;
		MixinNode node = new MixinNode();
		node.setName(mixinToken.getValue());
		node.setLineNumber(mixinToken.getLineNumber());
		node.setFileName(filename);
		if (StringUtils.isNotBlank(mixinToken.getArguments())) {
			node.setArguments(mixinToken.getArguments());
		}
		if (peek() instanceof Indent) {
			node.setBlock(block());
		}
		return node;
	}

	private Node parseCssClassOrId() {
		Token tok = nextToken();
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

	private Node parseInclude() {
		Token token = expect(Include.class);
		Include includeToken = (Include) token;
		String templateName = includeToken.getValue().trim();

		Parser parser = createParser(templateName);
		Node ast = parser.parse();

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
		// TODO: contexts ??
		extending = parser;

		LiteralNode node = new LiteralNode();
		node.setValue("");
		return node;
	}

	private Parser createParser(String templateName) {
		URI currentUri = URI.create(filename);
		URI templateUri = currentUri.resolve(templateName);
		try {
			return new Parser(templateUri.toString(), templateLoader);
		} catch (IOException e) {
			throw new JadeParserException(filename, lexer.getLineno(), templateLoader, "the template [" + templateName
					+ "] could not be opened\n" + e.getMessage());
		}
	}

	private BlockNode parseYield() {
		nextToken();
		BlockNode block = (BlockNode) new BlockNode();
		block.setLineNumber(lexer.getLineno());
		block.setFileName(filename);
		block.setYield(true);
		return block;
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

	private Node block() {
		BlockNode block = new BlockNode();
		block.setLineNumber(lexer.getLineno());
		block.setFileName(filename);
		expect(Indent.class);
		while (!(peek() instanceof Outdent) && !(peek() instanceof Eos)) {
			if (peek() instanceof Newline) {
				nextToken();
			} else {
				Node parseExpr = this.parseExpr();
				if (parseExpr != null) {
					block.push(parseExpr);
				}
			}
		}
		if (peek() instanceof Outdent) {
			expect(Outdent.class);
		}
		return block;
	}

	private List<Node> whenBlock() {
		expect(Indent.class);
		List<Node> caseConditionalNodes = new LinkedList<Node>();
		while (!(peek() instanceof Outdent) && !(peek() instanceof Eos)) {
			if (peek() instanceof Newline) {
				nextToken();
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
		Token token = expect(Text.class);
		Node node = new TextNode();
		node.setValue(token.getValue());
		node.setLineNumber(token.getLineNumber());
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
		return node;
	}

	private Node parseWhile() {
		Token token = expect(While.class);
		While whileToken = (While) token;
		WhileNode node = new WhileNode();
		node.setValue(whileToken.getValue());
		node.setLineNumber(whileToken.getLineNumber());
		node.setFileName(filename);
		node.setBlock(block());
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
		// ast-filter look-ahead
		int i = 2;
		if (lookahead(i) instanceof Attribute) {
			i++;
		}
		if (lookahead(i) instanceof Colon) {
			i++;
			if (lookahead(i) instanceof Indent) {
				return this.parseASTFilter();
			}
		}
		Token token = nextToken();
		String name = token.getValue();
		TagNode tagNode = new TagNode();
		tagNode.setLineNumber(lexer.getLineno());
		tagNode.setFileName(filename);
		tagNode.setName(name);
		tagNode.setValue(name);

		while (true) {
			Token incomingToken = peek();
			if (incomingToken instanceof CssId) {
				Token tok = nextToken();
				tagNode.addAttribute("id", tok.getValue());
				continue;
			} else if (incomingToken instanceof CssClass) {
				Token tok = nextToken();
				tagNode.addAttribute("class", tok.getValue());
				continue;
			} else if (incomingToken instanceof Attribute) {
				Attribute tok = (Attribute) nextToken();
				tagNode.addAttributes(tok.getAttributes());
				continue;
			} else {
				break;
			}
		}

		// check immediate '.'
		boolean dot = false;
		if (peek() instanceof Dot) {
			dot = true;
			tagNode.setTextOnly(true);
			nextToken();
		}

		// (text | code | ':')?
		if (peek() instanceof Text) {
			tagNode.setTextNode(parseText());
		} else if (peek() instanceof Expression) {
			tagNode.setCodeNode(parseCode());
		} else if (peek() instanceof Colon) {
			Token next = nextToken();
			BlockNode block = new BlockNode();
			block.setLineNumber(next.getLineNumber());
			block.setFileName(filename);
			tagNode.setBlock(block);
			block.push(parseExpr());
		}

		// newline*
		while (peek() instanceof Newline) {
			nextToken();
		}

		if (!tagNode.isTextOnly()) {
			if (Arrays.asList(textOnlyTags).contains(tagNode.getName())) {
				tagNode.setTextOnly(true);
			}
		}

		// script special-case
		if ("script".equals(tagNode.getName())) {
			String type = tagNode.getAttribute("type");
			if (!dot && StringUtils.isNotBlank(type)) {
				String cleanType = type.replaceAll("^['\"]|['\"]$", "");
				if (!"text/javascript".equals(cleanType)) {
					tagNode.setTextOnly(false);
				}
			}
		}

		if (peek() instanceof Indent) {
			if (tagNode.isTextOnly()) {
				lexer.setPipeless(true);
				tagNode.setTextNode(parseTextBlock());
				lexer.setPipeless(false);
			} else {
				Node blockNode = block();
				if (tagNode.hasBlock()) {
					tagNode.getBlock().getNodes().addAll(blockNode.getNodes());
				} else {
					tagNode.setBlock(blockNode);
				}
			}
		}

		return tagNode;
	}

	private Node parseTextBlock() {
		TextNode textNode = new TextNode();
		textNode.setLineNumber(line());
		textNode.setFileName(filename);
		Token token = expect(Indent.class);
		Indent indentToken = (Indent) token;
		int spaces = indentToken.getIndents();
		if (null == this._spaces)
			this._spaces = spaces;
		String indentStr = StringUtils.repeat(" ", spaces - this._spaces);
		while (!(peek() instanceof Outdent)) {
			if (peek() instanceof Newline) {
				textNode.appendText("\n");
				this.nextToken();
			} else if (peek() instanceof Indent) {
				textNode.appendText("\n");
				textNode.appendText(this.parseTextBlock().getValue());
				textNode.appendText("\n");
			} else {
				textNode.appendText(indentStr + this.nextToken().getValue());
			}
		}

		if (spaces == this._spaces)
			this._spaces = null;

		token = expect(Outdent.class);
		return textNode;
	}

	private Node parseConditional() {
		Token token = expect(Conditional.class);
		Conditional conditionalToken = (Conditional) token;
		ConditionalNode conditionalNode = new ConditionalNode();
		conditionalNode.setValue(conditionalToken.getValue());
		conditionalNode.setConditionActive(conditionalToken.isConditionActive());
		conditionalNode.setInverseCondition(conditionalToken.isInverseCondition());
		conditionalNode.setLineNumber(conditionalToken.getLineNumber());
		conditionalNode.setFileName(filename);
		
		while (peek() instanceof Newline) {
			nextToken();
		}

		if (peek() instanceof Indent) {
			Node blockNode = block();
			conditionalNode.setBlock(blockNode);
		}

		if (peek() instanceof Conditional) {
			Conditional next = (Conditional) peek();
			if (next.isAlternativeCondition()) {
				Node elseNode = parseExpr();
				conditionalNode.setElseNode(elseNode);
			}
		}

		return conditionalNode;
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

	private Node parseCaseCondition() {
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
			codeNode.setBlock((BlockNode) parseBlock());
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
		Attribute attr = (Attribute) accept(Attribute.class);
		lexer.setPipeless(true);
		Node tNode = parseTextBlock();
		lexer.setPipeless(false);

		FilterNode node = new FilterNode();
		node.setValue(filterToken.getValue());
		node.setLineNumber(line());
		node.setFileName(filename);
		node.setTextBlock(tNode);
		if (attr != null) {
			node.setAttributes(attr.getAttributes());
		}
		return node;
	}

	private Node parseASTFilter() {
		Token token = expect(Filter.class);
		Filter filterToken = (Filter) token;
		Attribute attr = (Attribute) accept(Attribute.class);

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

	private Token nextToken() {
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
			return nextToken();
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
}
