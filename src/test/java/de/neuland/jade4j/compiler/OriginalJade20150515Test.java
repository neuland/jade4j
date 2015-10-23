package de.neuland.jade4j.compiler;

import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.TestFileHelper;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.filter.CDATAFilter;
import de.neuland.jade4j.filter.PlainFilter;
import de.neuland.jade4j.template.JadeTemplate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class OriginalJade20150515Test {

	@Test
	public void testBrowserIndex() throws IOException, JadeCompilerException {
		testJade("browser/index");
	}

	@Test
	public void testCasesAttrsInterpolation() throws IOException, JadeCompilerException {
		testJade("cases/attrs.interpolation");
	}

	@Test
	public void testCasesAttrs() throws IOException, JadeCompilerException {
		testJade("cases/attrs");
	}
	@Test
	public void testCasesAttrs2() throws IOException, JadeCompilerException {
		testJade("../lexer/attrs2");
	}

	@Test
	public void testCasesAttrsUnescaped() throws IOException, JadeCompilerException {
		testJade("cases/attrs.unescaped");
	}

	@Test
	public void testCasesAttrsData() throws IOException, JadeCompilerException {
		testJade("cases/attrs-data");
	}

	@Test
	public void testCasesBasic() throws IOException, JadeCompilerException {
		testJade("cases/basic");
	}

	@Test
	public void testCasesBlanks() throws IOException, JadeCompilerException {
		testJade("cases/blanks");
	}

	@Test
	public void testCasesBlockCode() throws IOException, JadeCompilerException {
		testJade("cases/block-code");
	}

	@Test
	public void testCasesBlockExpansion() throws IOException, JadeCompilerException {
		testJade("cases/block-expansion");
	}

	@Test
	public void testCasesBlockExpansionShorthands() throws IOException, JadeCompilerException {
		testJade("cases/block-expansion.shorthands");
	}

	@Test
	public void testCasesBlockquote() throws IOException, JadeCompilerException {
		testJade("cases/blockquote");
	}

	@Test
	public void testCasesBlocksInBlocks() throws IOException, JadeCompilerException {
		testJade("cases/blocks-in-blocks");
	}

	@Test
	public void testCasesBlocksInIf() throws IOException, JadeCompilerException {
		testJade("cases/blocks-in-if");
	}

	@Test
	public void testCasesCase() throws IOException, JadeCompilerException {
		testJade("cases/case");
	}

	@Test
	public void testCasesCaseBlocks() throws IOException, JadeCompilerException {
		testJade("cases/case-blocks");
	}

	@Test
	public void testCasesClasses() throws IOException, JadeCompilerException {
		testJade("cases/classes");
	}

	@Test
	public void testCasesClassesEmpty() throws IOException, JadeCompilerException {
		testJade("cases/classes-empty");
	}

	@Test
	public void testCasesCodeConditionals() throws IOException, JadeCompilerException {
		testJade("cases/code.conditionals");
	}

	@Test
	public void testCasesCodeEscape() throws IOException, JadeCompilerException {
		testJade("cases/code.escape");
	}

	@Test
	public void testCasesCodeIteration() throws IOException, JadeCompilerException {
		testJade("cases/code.iteration");
	}

	@Test
	public void testCasesComments() throws IOException, JadeCompilerException {
		testJade("cases/comments");
	}

	@Test
	public void testCasesCommentsSource() throws IOException, JadeCompilerException {
		testJade("cases/comments.source");
	}

	@Test
	public void testCasesCommentsInCase() throws IOException, JadeCompilerException {
		testJade("cases/comments-in-case");
	}

	@Test
	public void testCasesCustomFilter() throws IOException, JadeCompilerException {
		testJade("cases/custom-filter");
	}

	@Test
	public void testCasesDoctypeCustom() throws IOException, JadeCompilerException {
		testJade("cases/doctype.custom");
	}

	@Test
	public void testCasesDoctypeDefault() throws IOException, JadeCompilerException {
		testJade("cases/doctype.default");
	}

	@Test
	public void testCasesDoctypeKeyword() throws IOException, JadeCompilerException {
		testJade("cases/doctype.keyword");
	}

	@Test
	public void testCasesEachElse() throws IOException, JadeCompilerException {
		testJade("cases/each.else");
	}

	@Test
	public void testCasesEscapeChars() throws IOException, JadeCompilerException {
		testJade("cases/escape-chars");
	}

	@Test
	public void testCasesEscapingClassAttribute() throws IOException, JadeCompilerException {
		testJade("cases/escaping-class-attribute");
	}

	@Test
	public void testCasesFiltersCdata() throws IOException, JadeCompilerException {
		testJade("cases/filters.cdata");
	}

	@Test
	public void testCasesFiltersCoffeescript() throws IOException, JadeCompilerException {
		testJade("cases/filters.coffeescript");
	}

	@Test
	public void testCasesFiltersLess() throws IOException, JadeCompilerException {
		testJade("cases/filters.less");
	}

	@Test
	public void testCasesFiltersMarkdown() throws IOException, JadeCompilerException {
		testJade("cases/filters.markdown");
	}

	@Test
	public void testCasesFiltersStylus() throws IOException, JadeCompilerException {
		testJade("cases/filters.stylus");
	}

	@Test
	public void testCasesFiltersEmpty() throws IOException, JadeCompilerException {
		testJade("cases/filters-empty");
	}

	@Test
	public void testCasesHtml() throws IOException, JadeCompilerException {
		testJade("cases/html");
	}

	@Test
	public void testCasesHtml5() throws IOException, JadeCompilerException {
		testJade("cases/html5");
	}

	@Test
	public void testCasesIncludeScript() throws IOException, JadeCompilerException {
		testJade("cases/include.script");
	}

	@Test
	public void testCasesIncludeYieldNested() throws IOException, JadeCompilerException {
		testJade("cases/include.yield.nested");
	}

	@Test
	public void testCasesIncludeExtendsFromRoot() throws IOException, JadeCompilerException {
		testJade("cases/include-extends-from-root");
	}

	@Test
	public void testCasesIncludeExtendsOfCommonTemplate() throws IOException, JadeCompilerException {
		testJade("cases/include-extends-of-common-template");
	}

	@Test
	public void testCasesIncludeFilter() throws IOException, JadeCompilerException {
		testJade("cases/include-filter");
	}

	@Test
	public void testCasesIncludeFilterStylus() throws IOException, JadeCompilerException {
		testJade("cases/include-filter-stylus");
	}

	@Test
	public void testCasesIncludeOnlyText() throws IOException, JadeCompilerException {
		testJade("cases/include-only-text");
	}

	@Test
	public void testCasesIncludeOnlyTextBody() throws IOException, JadeCompilerException {
		testJade("cases/include-only-text-body");
	}

	@Test
	public void testCasesIncludeWithText() throws IOException, JadeCompilerException {
		testJade("cases/include-with-text");
	}

	@Test
	public void testCasesIncludeWithTextHead() throws IOException, JadeCompilerException {
		testJade("cases/include-with-text-head");
	}

	@Test
	public void testCasesIncludes() throws IOException, JadeCompilerException {
		testJade("cases/includes");
	}

	@Test
	public void testCasesIncludesWithExtJs() throws IOException, JadeCompilerException {
		testJade("cases/includes-with-ext-js");
	}

	@Test
	public void testCasesInheritanceAlertDialog() throws IOException, JadeCompilerException {
		testJade("cases/inheritance.alert-dialog");
	}


	@Test
	public void testCasesInheritanceDefaults() throws IOException, JadeCompilerException {
		testJade("cases/inheritance.defaults");
	}

	@Test
	public void testCasesInheritanceExtendInclude() throws IOException, JadeCompilerException {
		testJade("cases/inheritance.extend.include");
	}

	@Test
	public void testCasesInheritanceExtend() throws IOException, JadeCompilerException {
		testJade("cases/inheritance.extend");
	}

	@Test
	public void testCasesInheritanceExtendMixinsBlock() throws IOException, JadeCompilerException {
		testJade("cases/inheritance.extend.mixins.block");
	}

	@Test
	public void testCasesInheritanceExtendMixins() throws IOException, JadeCompilerException {
		testJade("cases/inheritance.extend.mixins");
	}

	@Test
	public void testCasesInheritanceExtendRecursive() throws IOException, JadeCompilerException {
		testJade("cases/inheritance.extend.recursive");
	}

	@Test
	public void testCasesInheritanceExtendWhitespace() throws IOException, JadeCompilerException {
		testJade("cases/inheritance.extend.whitespace");
	}

	@Test
	public void testCasesInheritance() throws IOException, JadeCompilerException {
		testJade("cases/inheritance");
	}

	@Test
	public void testCasesInlineTag() throws IOException, JadeCompilerException {
		testJade("cases/inline-tag");
	}

	@Test
	public void testCasesInterpolationEscape() throws IOException, JadeCompilerException {
		testJade("cases/interpolation.escape");
	}

	@Test
	public void testCasesLayoutAppend() throws IOException, JadeCompilerException {
		testJade("cases/layout.append");
	}

	@Test
	public void testCasesLayoutAppendWithoutBlock() throws IOException, JadeCompilerException {
		testJade("cases/layout.append.without-block");
	}

	@Test
	public void testCasesLayoutMultiAppendPrependBlock() throws IOException, JadeCompilerException {
		testJade("cases/layout.multi.append.prepend.block");
	}

	@Test
	public void testCasesLayoutPrepend() throws IOException, JadeCompilerException {
		testJade("cases/layout.prepend");
	}

	@Test
	public void testCasesLayoutPrependWithoutBlock() throws IOException, JadeCompilerException {
		testJade("cases/layout.prepend.without-block");
	}

	@Test
	public void testCasesMixinAttrs() throws IOException, JadeCompilerException {
		testJade("cases/mixin.attrs");
	}

	@Test
	public void testCasesMixinBlockTagBehaviour() throws IOException, JadeCompilerException {
		testJade("cases/mixin.block-tag-behaviour");
	}

	@Test
	public void testCasesMixinBlocks() throws IOException, JadeCompilerException {
		testJade("cases/mixin.blocks");
	}

	@Test
	public void testCasesMixinMerge() throws IOException, JadeCompilerException {
		testJade("cases/mixin.merge");
	}

	@Test
	public void testCasesMixinAtEndOfFile() throws IOException, JadeCompilerException {
		testJade("cases/mixin-at-end-of-file");
	}

	@Test
	public void testCasesMixinHoist() throws IOException, JadeCompilerException {
		testJade("cases/mixin-hoist");
	}

	@Test
	public void testCasesMixinViaInclude() throws IOException, JadeCompilerException {
		testJade("cases/mixin-via-include");
	}

	@Test
	public void testCasesMixins() throws IOException, JadeCompilerException {
		testJade("cases/mixins");
	}

	@Test
	public void testCasesMixinsRestArgs() throws IOException, JadeCompilerException {
		testJade("cases/mixins.rest-args");
	}

	@Test
	public void testCasesMixinsUnused() throws IOException, JadeCompilerException {
		testJade("cases/mixins-unused");
	}

	@Test
	public void testCasesNamespaces() throws IOException, JadeCompilerException {
		testJade("cases/namespaces");
	}

	@Test
	public void testCasesNesting() throws IOException, JadeCompilerException {
		testJade("cases/nesting");
	}

	@Test
	public void testCasesPre() throws IOException, JadeCompilerException {
		testJade("cases/pre");
	}

	@Test
	public void testCasesQuotes() throws IOException, JadeCompilerException {
		testJade("cases/quotes");
	}

	@Test
	public void testCasesRegression784() throws IOException, JadeCompilerException {
		testJade("cases/regression.784");
	}

	@Test
	public void testCasesRegression1794() throws IOException, JadeCompilerException {
		testJade("cases/regression.1794");
	}

	@Test
	public void testCasesScriptWhitespace() throws IOException, JadeCompilerException {
		testJade("cases/script.whitespace");
	}

	@Test
	public void testCasesScripts() throws IOException, JadeCompilerException {
		testJade("cases/scripts");
	}

	@Test
	public void testCasesScriptsNonJs() throws IOException, JadeCompilerException {
		testJade("cases/scripts.non-js");
	}

	@Test
	public void testCasesSinglePeriod() throws IOException, JadeCompilerException {
		testJade("cases/single-period");
	}

	@Test
	public void testCasesSource() throws IOException, JadeCompilerException {
		testJade("cases/source");
	}

	@Test
	public void testCasesStyles() throws IOException, JadeCompilerException {
		testJade("cases/styles");
	}

	@Test
	public void testCasesTagInterpolation() throws IOException, JadeCompilerException {
		testJade("cases/tag.interpolation");
	}

	@Test
	public void testCasesTagsSelfClosing() throws IOException, JadeCompilerException {
		testJade("cases/tags.self-closing");
	}

	@Test
	public void testCasesTemplate() throws IOException, JadeCompilerException {
		testJade("cases/template");
	}

	@Test
	public void testCasesText() throws IOException, JadeCompilerException {
		testJade("cases/text");
	}

	@Test
	public void testCasesUtf8Bom() throws IOException, JadeCompilerException {
		testJade("cases/utf8bom");
	}

	@Test
	public void testCasesVars() throws IOException, JadeCompilerException {
		testJade("cases/vars");
	}

	@Test
	public void testCasesWhile() throws IOException, JadeCompilerException {
		testJade("cases/while");
	}

	@Test
	public void testCasesXml() throws IOException, JadeCompilerException {
		testJade("cases/xml");
	}

	@Test
	public void testCasesYield() throws IOException, JadeCompilerException {
		testJade("cases/yield");
	}

	@Test
	public void testCasesYieldBeforeConditional() throws IOException, JadeCompilerException {
		testJade("cases/yield-before-conditional");
	}

	@Test
	public void testCasesYieldBeforeConditionalHead() throws IOException, JadeCompilerException {
		testJade("cases/yield-before-conditional-head");
	}

	@Test
	public void testCasesYieldHead() throws IOException, JadeCompilerException {
		testJade("cases/yield-head");
	}

	@Test
	public void testCasesYieldTitle() throws IOException, JadeCompilerException {
		testJade("cases/yield-title");
	}

	@Test
	public void testCasesYieldTitleHead() throws IOException, JadeCompilerException {
		testJade("cases/yield-title-head");
	}

	private void testJade(String path) throws IOException {
		File file = new File(TestFileHelper.getOriginal20150515ResourcePath(path+".jade"));

		JadeConfiguration jade = new JadeConfiguration();
		jade.setMode(Jade4J.Mode.XHTML); // original jade uses xhtml by default
		jade.setFilter("plain", new PlainFilter());
		jade.setFilter("cdata", new CDATAFilter());
		jade.setPrettyPrint(true);
		JadeTemplate template = jade.getTemplate(file.getPath());
		Writer writer = new StringWriter();
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("title","Jade");
		jade.renderTemplate(template, model, writer);
		String html = writer.toString();

		String expected = readFile(file.getPath().replace(".jade", ".html"));
		assertEquals(file.getName(), expected, html);
	}

	private String readFile(String fileName) {
		try {
			return FileUtils.readFileToString(new File(fileName));
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return "";
	}
}
