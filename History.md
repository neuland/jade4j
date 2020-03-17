# History
## 1.3.2 / 2020-03-17  
* Fixed issue #193: Mixin's block argument can't execute multiple times in a loop

## 1.3.1 / 2020-02-28
* Fixed issue #191: Scoping Issue with nested loops
* Fixed issue #187: maven pom flexmark-all is too much
* Fixed issue #188: Unit tests failures on default Windows console

## 1.3.0 / 2019-10-10
* removed obsolete basePath handling. Basepath Should be configured in the FileTemplateLoader
* made file extension configurable. removed last static jade extension check.
* Fixed issue #172: json als mixin argument (quoted keys)
* Fixed issue #153: Variable assignments in for/each loops
* Improvements to issue #150: Caused by: java.lang.RuntimeException this reader only responds to

## 1.2.7 / 2017-11-02
* Improving cache syncronisation

## 1.2.6 / 2017-11-01
* Fixing issue #154: using .pug extension
* Fixing issue #157: array constructing in mixin parameters don't work
* Testcase #155: case with default not working (at least using JsExpressionHandler)
* Fixing multiline Code Blocks
* Syncronize template creation when cache is enabled

## 1.2.5 / 2016-10-24
* Fixing issue #147: Fix for issue #52 broke everything

## 1.2.4 / 2016-10-11
* Fixed issue #141: Jade4J does not support unbuffered code blocks
* Fixing issue #52: Includes in templates in paths with spaces

## 1.2.3 / 2016-06-17
* Performance improvements

## 1.2.2 / 2016-06-17
* Fixing issue #106: Filters cannot be set using xml configuration
* Testcase issue 65
* Fixing issue #78: Incorrect rendering of backslashes in attribute values
* Fixing issue #68: Multi-Line Strings include the trailing slash

## 1.2.1 / 2016-04-18
* Fixing issue #132: class attribute not supported

## 1.2.0 / 2016-04-18
* Fixing issue #135: Resource loaded using the ClasspathTemplateLoader require *.jade extension before they are copied in (Thanks to nishtahir and crowmagnumb)
* Fixing issue #129: multiple class attributes per HTML tag are not supported (breaking change in Filter interface, you need to adapt thirdparty filters)

## 1.1.4 / 2016-01-14
* set fileName property to TextNode (thx to code4craft)

## 1.1.3 / 2015-12-08
* Fixed Testcase: include-extends-of-common-template
* Added Lexer Testcases
* Updated Dependencies to newest version.

## 1.1.2 / 2015-12-02
* Fixing issue #126: Concatinate Null with String => NullPointerException
* Improved error messages

## 1.1.1 / 2015-12-02
* Fixing issue #125: NumberFormatException when comparing loop-iterator with a String

## 1.1.0 / 2015-11-29
* Feature: Add Rest Attributes to Mixins

## 1.0.10 / 2015-11-27
* Fixing issue #124: Mixin Merge not working correct
* Some Mixin refinements
* Fixing include-with-text.jade

## 1.0.9 / 2015-11-27
* Fixing jade-Output without doctype
* Fixing issue #122: Mixin Block after Mixin Call on same Line
* Fixing issue #123: Block append not working correct.

## 1.0.8 / 2015-11-26
* Fixing issue #120: Terse Mode not working as expected
* Fixed IndexOutOfBoundsException in substring.

## 1.0.7 / 2015-11-16
* Fixing issue #101: "}" symbol breaks the template inside of a string (Thanks to moio)

## 1.0.6 / 2015-11-12
* Fixing issue #118: Problems with nested/inherited attributes
* Fixing issue #117: &attributes() -> String index out of range when mixin called with ()()

## 1.0.5 / 2015-11-12
* Fixing Issue #116: &attributes() -> Classcast Exception with integers as keys in maps, inside of loops

## 1.0.4 / 2015-11-12
* Fixing Issue #115: &attributes() -> Classcast Exception with integers as keys in maps
* Fixing Issue #104: mixin definitions are ignored in extension template (Thanks to rzara)

## 1.0.3 / 2015-11-11
* Fixing Issue #114: Blank strings in brackets being casted to 0

## 1.0.2 / 2015-11-10
* Fixing Issue #113: &attributes on Mixin not working

## 1.0.1 / 2015-11-10
* Fixing Issue #112: Fixed ++ and -- recognition
* Fixing Issue #111: Maven Upgrade auf 3.2.5
* Added Testcases for closed Issues

## 1.0.0 / 2015-11-06
* In this version we updated to a lot of features of JadeJs 1.11 (2015-06-12).
* Breaking Change: Instead of 'id = 5' you must use '- var id = 5'
* Breaking Change: Instead of 'h1(attributes, class = "test")' you must use 'h1(class= "test")&attributes(attributes)'
* Breaking Change: Instead of '!!! 5' you must use 'doctype html'
* Breaking Change: Instead of '!!! 5' you must use 'doctype html'
* Jade Syntax for Conditional Comments is not supported anymore
* Thanks to rzara for contributing to issue-108

## 0.4.3 / 2015-05-27
* Accepted pull request from dusanmsk (#91) regarding mixin argument splitting and added further tests.

## 0.4.2 / 2015-03-18
* added issue89: Test files renamed (was #89 instead of #90).

## 0.4.1 / 2014-11-29
* fixed tab support #79
* .jade file extension appending is now done before the template loader #71
* added support for mixin default blocks #80

## 0.4.0 / 2013-11-20
* we are now on maven central #25
* adapted pom to meet sonatype requirements
* changed artifact group id
* fixed double output of objects implementing Map and Iterable interfaces #63

## 0.3.17 / 2013-10-09
* added sources to maven repository
* added support for multiple block statements in one mixin
* fixed issues when using if/case statements inside a mixin

## 0.3.16 / 2013-10-07
* allowed including files without having to register a specific filter
* enabled self closing tags with trailing "/" #57

## 0.3.15 / 2013-09-12
* added support for including non jade files (js, css, ...) inside a template

## 0.3.14 / 2013-08-24
* added ability to clear expression and template caches
* added new convenience method to Jade4J thats lets you use Reader #49

## 0.3.13 / 2013-08-21
* the indentation exception shows the expected indent sequence #50
* code nodes can have sub blocks #44
* better error message for invalid attribute definition #37
* blockquotes are now parsed correctly and don't interfere with "layout blocks" #45
* ExpressionStrings are now evaluated multiple times to support expressions that point to expressions #47

## 0.3.12 / 2013-06-20
* reduced jexl log level for 'unknown variable' messages
