
Unbescape: escaping in Java done right
======================================

------------------------------------------------------------------------------

_Unbescape_ is a Java library aimed at performing fully-featured and high-performance escape and unescape operations for **HTML** (HTML5 and HTML 4), **XML**, **JavaScript**, **JSON**, **CSS** and **Java Literals**.


Status
------

This project is still under active development. Some features are already available and fully functional (check below).

Current versions: 

  * **Version 0.2**


License
-------

This software is licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).


Requirements
------------

  *   Java SE 5.0 or higher


Maven info
----------

  *   groupId: `org.unbescape`   
  *   artifactId: `unbescape`
  *   version: (see _Current Versions_ above)


Features
--------

  *   **High performance** and **low memory footprint**
      *   No unneeded `String` or `char[]` objects are created, and specific optimizations are applied in order to provide maximum performance (e.g. if a `String` has the same content after escaping/unescaping, exactly the same `String` object is returned, no copy is made).
      *  See (and execute) the [`HtmlBenchmark`](https://github.com/unbescape/unbescape-tests/blob/20140410/src/test/java/org/unbescape/html/HtmlBenchmark.java) and [`XmlBenchmark`](https://github.com/unbescape/unbescape-tests/blob/20140410/src/test/java/org/unbescape/xml/XmlBenchmark.java) classes in the [`unbescape-tests`](https://github.com/unbescape/unbescape-tests) repository for specific figures.     
  *   **HTML Escape/Unescape** _[already available]_
      *  Whole **HTML5** NCR (Named Character Reference) set supported, if required:    `&rsqb;`,`&NewLine;`, etc. (HTML 4 set available too).
      *  Mixed named and numerical (decimal or hexa) character references supported.
      *  Ability to default to numerical (decimal or hexa) references when an applicable NCR does not exist (depending on the selected operation level).
      *  Support for the whole Unicode character set: `\u0000` to `\u10FFFF`, including characters not representable by only one char in Java (>`\uFFFF`).
      *  Support for unescape of double-char NCRs in HTML5: `&fjlig;` → `fj`.
      *  Support for a set of HTML5 unescape tweaks included in the HTML5 specification:
         *  Unescape of numerical character references not ending in semi-colon (e.g. `&#x23ac`).
         *  Unescape of specific NCRs not ending in semi-colon (e.g. `&aacute`).
         *  Unescape of specific numerical character references wrongly specified by their Windows-1252 codepage code instead of the Unicode one (e.g. `&#x80;` for `€` (`&euro;`) instead of `&#x20ac;`).
  *   **XML Escape/Unescape** _[already available]_
      *  Support for both XML 1.0 and XML 1.1 escape/unescape operations.
      *  No support for DTD-defined or user-defined entities. Only the five predefined XML character entities are supported: `&lt;`, `&gt;`, `&amp;`, `&quot;` and `&apos;`.
      *  Automatic escaping of allowed control characters.
      *  Support for the whole Unicode character set: `\u0000` to `\u10FFFF`, including characters not representable by only one char in Java (>`\uFFFF`).
  *   **JavaScript Escape/Unescape**
      *  Support for the JavaScript basic escape set: `\0`, `\b`, `\t`, `\n`, `\v`, `\f`, `\r`, `\"`, `\'`, `\\`.
         Note that `\v` (`U+000B`) will not be used in escape operations (only unescape) because it is
         not supported by Microsoft Internet Explorer versions < 9.
      *  Automatic escape of `/` (as `\/` if possible) when it appears after `<`, as in `</something>`.
      *  Support for escaping non-displayable, control characters: `U+0001` to `U+001F` and `U+007F` to `U+009F`.
      *  Support for X-based hexadecimal escapes (a.k.a. _hexadecimal escapes_) both in escape
         and unescape operations: `\xE1`.
      *  Support for U-based hexadecimal escapes (a.k.a. _unicode escapes_) both in escape
         and unescape operations: `\u00E1`.
      *  Support for Octal escapes, though only in unescape operations: `\071`. Not supported
         in escape operations (octal escapes were deprecated in version 5 of the ECMAScript
         specification).
      *  Support for the whole Unicode character set: `\u0000` to `\u10FFFF`, including characters not representable by only one char in Java (>`\uFFFF`).
  *   **JSON Escape/Unescape**
      *  Support for the JSON basic escape set: `\b`, `\t`, `\n`, `\f`, `\r`, `\"`, `\\`.
      *  Automatic escape of `/` (as `\/` if possible) when it appears after `<`, as in `</something>`.
      *  Support for escaping non-displayable, control characters: `U+0000` to `U+001F` and `U+007F` to `U+009F`.
      *  Support for U-based hexadecimal escapes (a.k.a. _unicode escapes_) both in escape
         and unescape operations: `\u00E1`.
      *  Support for the whole Unicode character set: `\u0000` to `\u10FFFF`, including characters not representable by only one char in Java (>`\uFFFF`).
  *   **CSS Escape/Unescape** _[not yet available]_
  *   **Java Literal Escape/Unescape** _[not yet available]_


------------------------------------------------------------------------------

	
HTML Escape/Unescape
-------------------------

HTML escape and unescape operations are performed by means of the `org.unbescape.html.HtmlEscape` class. This class defines a series of static methods that perform the desired operations (see the class _javadoc_ for more info).

There are simple, preconfigured methods:

```java
    final String escaped = HtmlEscape.escapeHtml5(text);
    final String unescaped = HtmlEscape.unescapeHtml(escaped);
```

And also those that allow a more fine-grained configuration of the escape operation:

```java
    final String result = 
        HtmlEscape.escapeHtml(
             text, 
             HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_HEXA,
             HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
```


	
XML Escape/Unescape
-------------------------

XML escape and unescape operations are performed by means of the `org.unbescape.xml.XmlEscape` class. This class defines a series of static methods that perform the desired operations (see the class _javadoc_ for more info).

There are simple, preconfigured methods:

```java
    final String escaped = XmlEscape.escapeXml11(text);
    final String unescaped = XmlEscape.unescapeXml(escaped);
```

And also those that allow a more fine-grained configuration of the escape operation:

```java
    final String result = 
        XmlEscape.escapeXml11(
             text, 
             XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_DECIMAL,
             XmlEscapeLevel.LEVEL_3_ALL_NON_ALPHANUMERIC);
```



JavaScript Escape/Unescape
--------------------------

JavaScript escape and unescape operations are performed by means of the `org.unbescape.javascript.JavaScriptEscape` class. This class defines a series of static methods that perform the desired operations (see the class _javadoc_ for more info).

There are simple, preconfigured methods:

```java
    final String escaped = JavaScriptEscape.escapeJavaScript(text);
    final String unescaped = JavaScriptEscape.unescapeJavaScript(escaped);
```

And also those that allow a more fine-grained configuration of the escape operation:

```java
    final String result =
        JavaScriptEscape.escapeJavaScript(
             text,
             JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA,
             JavaScriptEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
```



JSON Escape/Unescape
--------------------

JSON escape and unescape operations are performed by means of the `org.unbescape.json.JSONEscape` class. This class defines a series of static methods that perform the desired operations (see the class _javadoc_ for more info).

There are simple, preconfigured methods:

```java
    final String escaped = JSONEscape.escapeJSON(text);
    final String unescaped = JSONEscape.unescapeJSON(escaped);
```

And also those that allow a more fine-grained configuration of the escape operation:

```java
    final String result =
        JSONEscape.escapeJSON(
             text,
             JSONEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO__UHEXA,
             JSONEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
```
