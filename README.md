[![Build Status](https://secure.travis-ci.org/neuland/jade4j.png?branch=master)](http://travis-ci.org/neuland/jade4j)
# Attention: jade4j is now pug4j
In alignment with the javascript template engine we renamed jade4j to pug4j. You will find it under https://github.com/neuland/pug4j
This is also a new release which supports almost the entire pug 2 syntax.

Please report pug4j issues in the new repository.
 
# jade4j - a jade implementation written in Java
jade4j's intention is to be able to process jade templates in Java without the need of a JavaScript environment, while being **fully compatible** with the original jade syntax.

## Contents

- [Example](#example)
- [Syntax](#syntax)
- [Usage](#usage)
- [Simple static API](#simple-api)
- [Full API](#api)
    - [Caching](#api-caching)
    - [Output Formatting](#api-output)
    - [Filters](#api-filters)
    - [Helpers](#api-helpers)
    - [Model Defaults](#api-model-defaults)
    - [Template Loader](#api-template-loader)
- [Expressions](#expressions)
- [Reserved Words](#reserved-words)
- [Framework Integrations](#framework-integrations)
- [Breaking Changes](#breaking-changes)
- [Authors](#authors)
- [License](#license)


## Example

index.jade

```
doctype html
html
  head
    title= pageName
  body
    ol#books
      for book in books
        if book.available
          li #{book.name} for #{book.price} €
```

Java model

```java
List<Book> books = new ArrayList<Book>();
books.add(new Book("The Hitchhiker's Guide to the Galaxy", 5.70, true));
books.add(new Book("Life, the Universe and Everything", 5.60, false));
books.add(new Book("The Restaurant at the End of the Universe", 5.40, true));

Map<String, Object> model = new HashMap<String, Object>();
model.put("books", books);
model.put("pageName", "My Bookshelf");
```

Running the above code through `String html = Jade4J.render("./index.jade", model)` will result in the following output:

```html
<!DOCTYPE html>
<html>
  <head>
    <title>My Bookshelf</title>
  </head>
  <body>
    <ol id="books">
      <li>The Hitchhiker's Guide to the Galaxy for 5,70 €</li>
      <li>The Restaurant at the End of the Universe for 5,40 €</li>
    </ol>
  </body>
</html>
```

## Syntax

We have put up an [interactive jade documentation](http://naltatis.github.com/jade-syntax-docs/).

See also the original [visionmedia/jade documentation](https://github.com/visionmedia/jade#a6).

## Usage

### via Maven

As of release 0.4.1, we have changed maven hosting to sonatype. Using Github Maven Repository is no longer
required.

Please be aware that we had to change the group id from 'de.neuland' to 'de.neuland-bfi' in order to
meet sonatype conventions for group naming.

Just add following dependency definitions to your `pom.xml`.

```xml
<dependency>
  <groupId>de.neuland-bfi</groupId>
  <artifactId>jade4j</artifactId>
  <version>1.3.2</version>
</dependency>
```

### Build it yourself

Clone this repository ...

```bash
git clone https://github.com/neuland/jade4j.git
```

... build it using `maven` ...

```bash
cd jade4j
mvn install
```

... and use the `jade4j-1.x.x.jar` located in your target directory.

<a name="simple-api"></a>
## Simple static API

Parsing template and generating template in one step.

```java
String html = Jade4J.render("./index.jade", model);
```

If you use this in production you would probably do the template parsing only once per template and call the render method with different models.

```java
JadeTemplate template = Jade4J.getTemplate("./index.jade");
String html = Jade4J.render(template, model);
```

Streaming output using a `java.io.Writer`

```java
Jade4J.render(template, model, writer);
```

<a name="api"></a>
## Full API

If you need more control you can instantiate a `JadeConfiguration` object.

```java
JadeConfiguration config = new JadeConfiguration();

JadeTemplate template = config.getTemplate("index");

Map<String, Object> model = new HashMap<String, Object>();
model.put("company", "neuland");

config.renderTemplate(template, model);
```

<a name="api-caching"></a>
### Caching

The `JadeConfiguration` handles template caching for you. If you request the same unmodified template twice you'll get the same instance and avoid unnecessary parsing.

```java
JadeTemplate t1 = config.getTemplate("index.jade");
JadeTemplate t2 = config.getTemplate("index.jade");
t1.equals(t2) // true
```

You can clear the template and expression cache by calling the following:

```java
config.clearCache();
```

For development mode, you can also disable caching completely:

```java
config.setCaching(false);
```

<a name="api-output"></a>
### Output Formatting

By default, Jade4J produces compressed HTML without unneeded whitespace. You can change this behaviour by enabling PrettyPrint:

```java
config.setPrettyPrint(true);
```

Jade detects if it has to generate (X)HTML or XML code by your specified [doctype](https://github.com/visionmedia/jade#a6-11).

If you are rendering partial templates that don't include a doctype jade4j generates HTML code. You can also set the `mode` manually:

```
config.setMode(Jade4J.Mode.HTML);   // <input checked>
config.setMode(Jade4J.Mode.XHTML);  // <input checked="true" />
config.setMode(Jade4J.Mode.XML);    // <input checked="true"></input>
```

<a name="api-filters"></a>
### Filters

Filters allow embedding content like `markdown` or `coffeescript` into your jade template:

    script
      :coffeescript
        sayHello -> alert "hello world"

will generate

    <script>
      sayHello(function() {
        return alert("hello world");
      });
    </script>

jade4j comes with a `plain` and `cdata` filter. `plain` takes your input to pass it directly through, `cdata` wraps your content in `<![CDATA[...]]>`. You can add your custom filters to your configuration.

    config.setFilter("coffeescript", new CoffeeScriptFilter());

To implement your own filter, you have to implement the `Filter` Interface. If your filter doesn't use any data from the model you can inherit from the abstract `CachingFilter` and also get caching for free. See the [neuland/jade4j-coffeescript-filter](https://github.com/neuland/jade4j-coffeescript-filter) project as an example.

<a name="api-helpers"></a>
### Helpers

If you need to call custom java functions the easiest way is to create helper classes and put an instance into the model.

```java
public class MathHelper {
    public long round(double number) {
        return Math.round(number);
    }
}
```

```java
model.put("math", new MathHelper());
```

Note: Helpers don't have their own namespace, so you have to be careful not to overwrite them with other variables.

```
p= math.round(1.44)
```

<a name="api-model-defaults"></a>
### Model Defaults

If you are using multiple templates you might have the need for a set of default objects that are available in all templates.

```java
Map<String, Object> defaults = new HashMap<String, Object>();
defaults.put("city", "Bremen");
defaults.put("country", "Germany");
defaults.put("url", new MyUrlHelper());
config.setSharedVariables(defaults);
```

<a name="api-template-loader"></a>
### Template Loader

By default, jade4j searches for template files in your work directory. By specifying your own `FileTemplateLoader`, you can alter that behavior. You can also implement the `TemplateLoader` interface to create your own.

```java
TemplateLoader loader = new FileTemplateLoader("/templates/", "UTF-8");
config.setTemplateLoader(loader);
```

<a name="expressions"></a>
## Expressions

The original jade implementation uses JavaScript for expression handling in `if`, `unless`, `for`, `case` commands, like this

    - var book = {"price": 4.99, "title": "The Book"}
    if book.price < 5.50 && !book.soldOut
      p.sale special offer: #{book.title}

    each author in ["artur", "stefan", "michael"]
      h2= author

As of version 0.3.0, jade4j uses [JEXL](http://commons.apache.org/jexl/) instead of [OGNL](http://en.wikipedia.org/wiki/OGNL) for parsing and executing these expressions.

We decided to switch to JEXL because its syntax and behavior is more similar to ECMAScript/JavaScript and so closer to the original jade.js implementation. JEXL runs also much faster than OGNL. In our benchmark, it showed a **performance increase by factor 3 to 4**.

We are using a slightly modified JEXL version which to have better control of the exception handling. JEXL now runs in a semi-strict mode, where non existing values and properties silently evaluate to `null`/`false` where as invalid method calls lead to a `JadeCompilerException`.

<a name="reserved-words"></a>
## Reserved Words

JEXL comes with the three builtin functions `new`, `size` and `empty`. For properties with this name the `.` notation does not work, but you can access them with `[]`.

```
- var book = {size: 540}
book.size // does not work
book["size"] // works
```

You can read more about this in the [JEXL documentation](http://commons.apache.org/proper/commons-jexl/reference/syntax.html#Language_Elements).

<a name="framework-integrations"></a>
## Framework Integrations
- [neuland/spring-jade4j](https://github.com/neuland/spring-jade4j) jade4j for Spring.
- [jooby-jade](https://github.com/jooby-project/jooby/tree/master/jooby-jade) jade4j for [Jooby](http://jooby.org).
- [vertx-web](http://vertx.io/docs/vertx-web/js/#_jade_template_engine) jade4j for [Vert.X](http://vertx.io/)

<a name="breaking-changes"></a>
## Breaking Changes

### 1.3.1
- Fixed a mayor scoping bug in loops. Use this version and not 1.3.0

### 1.3.0
- setBasePath has been removed from JadeConfiguration. Set folderPath on FileTemplateLoader instead.
- Scoping of variables in loops changed, so its more in line with jade. This could break your template.

### 1.2.0
- Breaking change in filter interface: if you use filters outside of the project, they need to be adapted to new interface

### 1.0.0
In Version 1.0.0 we added a lot of features of JadeJs 1.11. There are also some Breaking Changes:
- Instead of 'id = 5' you must use '- var id = 5'
- Instead of 'h1(attributes, class = "test")' you must use 'h1(class= "test")&attributes(attributes)'
- Instead of '!!! 5' you must use 'doctype html'
- Jade Syntax for Conditional Comments is not supported anymore
- Thanks to rzara for contributing to issue-108

<a name="authors"></a>
## Authors

- Artur Tomas / [atomiccoder](https://github.com/atomiccoder)
- Stefan Kuper / [planetk](https://github.com/planetk)
- Michael Geers / [naltatis](https://github.com/naltatis)
- Christoph Blömer / [chbloemer](https://github.com/chbloemer)

Special thanks to [TJ Holowaychuk](https://github.com/visionmedia) the creator of jade!

<a name="license"></a>
## License

The MIT License

Copyright (C) 2011-2019 [neuland Büro für Informatik](http://www.neuland-bfi.de/), Bremen, Germany

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
