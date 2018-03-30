
Unbescape: escape and unescape operations in Java
=================================================

------------------------------------------------------------------------------

_Unbescape_ is a Java library aimed at performing fully-featured and high-performance escape and unescape
operations for:

  * **HTML** (HTML5 and HTML 4)
  * **XML** (XML 1.0 and XML 1.1)
  * **JavaScript**
  * **JSON**
  * **URI**/**URL**
  * **CSS**
  * **CSV** (Comma-Separated Values)
  * **Java literals**
  * **Java `.properties` files**


Status
------

This project is stable and production-ready.

Current versions: 

  * Version **1.1.6.RELEASE** (30 Mar 2018)


License
-------

This software is licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).


Requirements
------------

  *   Java SE 6 or higher


Maven info
----------

  *   groupId: `org.unbescape`   
  *   artifactId: `unbescape`
  *   version: (see _Current Versions_ above)


Features
--------

  *   **High performance**
      *  No unneeded `String` or `char[]` objects are created, and specific optimizations are applied in order to provide maximum performance and reduce Garbage Collector latency (e.g. if a `String` has the same content after escaping/unescaping, exactly the same `String` object is returned, no copy is made).
      *  See (and execute) the [`benchmark.sh`](https://github.com/unbescape/unbescape-tests/blob/20140429/benchmark.sh) script in the
         [`unbescape-tests`](https://github.com/unbescape/unbescape-tests) repository for specific figures.
  *   **Highly configurable**
      *  Most escaped languages allow specifying the _type_ of escape to be performed: based on literals, on decimal numbers, hexadecimal, octal, etc.
      *  Most escaped languages allow specifying the _level_ of escape to be performed: only escape the _basic set_, escape _all non-ASCII characters_, escape _all non-alphanumeric_, etc.
      *  Provides sensible defaults and pre-configured, easy-to-use methods.
  *   **Documented API**
      *  Includes full JavaDoc API documentation for all public classes, explaining each escape and unescape operation in detail.
  *   **Unicode**
      *  All escape and unescape operations support the whole Unicode character set: `U+0000` to `U+10FFFF`, including characters not representable by only one char in Java (>`U+FFFF`).
  *   **HTML Escape/Unescape**
      *  Whole **HTML5** NCR (Named Character Reference) set supported, if required: `&rsqb;`,`&NewLine;`, etc. (HTML 4 set available too).
      *  Mixed named and numerical (decimal or hexa) character references supported.
      *  Ability to default to numerical (decimal or hexa) references when an applicable NCR does not exist (depending on the selected operation level).
      *  Support for unescape of double-char NCRs in HTML5: `&fjlig;` → `fj`.
      *  Support for a set of HTML5 unescape tweaks included in the HTML5 specification:
         *  Unescape of numerical character references not ending in semi-colon (e.g. `&#x23ac`).
         *  Unescape of specific NCRs not ending in semi-colon (e.g. `&aacute`).
         *  Unescape of specific numerical character references wrongly specified by their Windows-1252 codepage code instead of the Unicode one (e.g. `&#x80;` for `€` (`&euro;`) instead of `&#x20ac;`).
  *   **XML Escape/Unescape**
      *  Support for both XML 1.0 and XML 1.1 escape/unescape operations.
      *  No support for DTD-defined or user-defined entities. Only the five predefined XML character entities are supported: `&lt;`, `&gt;`, `&amp;`, `&quot;` and `&apos;`.
      *  Automatic escaping of allowed control characters.
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
  *   **JSON Escape/Unescape**
      *  Support for the JSON basic escape set: `\b`, `\t`, `\n`, `\f`, `\r`, `\"`, `\\`.
      *  Automatic escape of `/` (as `\/` if possible) when it appears after `<`, as in `</something>`.
      *  Support for escaping non-displayable, control characters: `U+0000` to `U+001F` and `U+007F` to `U+009F`.
      *  Support for U-based hexadecimal escapes (a.k.a. _unicode escapes_) both in escape
         and unescape operations: `\u00E1`.
  *   **URI/URL Escape/Unescape**
      *  Support for escape operations using percent-encoding (`%HH`).
      *  Escape URI paths, path fragments, query parameters and fragment identifiers.
  *   **CSS Escape/Unescape**
      *  Complete set of CSS _Backslash Escapes_ supported (e.g. `\+`, `\;`, `\(`, `\)`, etc.).
      *  Full set of escape syntax rules supported, both for **CSS identifiers** and **CSS Strings**
         (or _literals_).
      *  Non-standard tweaks supported: `\:` not used because of lacking support in Internet Explorer < 8,
         `\_` escaped at the beginning of identifiers for better Internet Explorer 6 support, etc.
      *  Hexadecimal escapes (a.k.a. _unicode escapes_) are supported both in escape and unescape operations,
         and both in _compact_ (`\E1 `) and six-digit forms (`\0000E1`).
      *  Support for unescaping unicode characters >`\uFFFF` both when represented in standard form (one char,
         `\20000`) and non-standard (surrogate pair, `\D840\DC00`, used by older WebKit browsers).
  *   **CSV (Comma-Separated Values) Escape/Unescape**
      *  Works according to the rules specified in RFC4180 (there is no _CSV standard_ as such).
      *  Encloses escaped values in double-quotes (`"value"`) if they contain any non-alphanumeric characters.
      *  Escapes double-quote characters (`"`) by writing them twice: `""`.
      *  Honors rules for maximum compatibility with Microsoft Excel.
  *   **Java Literal Escape/Unescape**
      *  Support for the Java basic escape set: `\b`, `\t`, `\n`, `\f`, `\r`, `\"`, `\'`, `\\`. Note `\'` will not be
         used in escaping levels < 3 (= _all but alphanumeric_) because escaping the apostrophe is not really required in Java String literals
         (only in Character literals).
      *  Support for escaping non-displayable, control characters: `U+0001` to `U+001F` and `U+007F` to `U+009F`.
      *  Support for U-based hexadecimal escapes (a.k.a. _unicode escapes_) both in escape
         and unescape operations: `\u00E1`.
      *  Support for Octal escapes, though only in unescape operations: `\071`. Not supported
         in escape operations (use of octal escapes is not recommended by the Java Language Specification).
  *   **Java `.properties` File Escape/Unescape**
      *  Support for the Java Properties basic escape set: `\t`, `\n`, `\f`, `\r`, `\\`. When escaping `.properties`
         keys (not values) `\ `, `\:` and `\=` will be applied too.
      *  Support for escaping non-displayable, control characters: `U+0001` to `U+001F` and `U+007F` to `U+009F`.
      *  Support for U-based hexadecimal escapes (a.k.a. _unicode escapes_) both in escape
         and unescape operations: `\u00E1`.



------------------------------------------------------------------------------

	
HTML Escape/Unescape
-------------------------

HTML escape and unescape operations are performed by means of the `org.unbescape.html.HtmlEscape` class. This class
defines a series of static methods that perform the desired operations (see the class _javadoc_ for more info).

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

XML escape and unescape operations are performed by means of the `org.unbescape.xml.XmlEscape` class. This class
defines a series of static methods that perform the desired operations (see the class _javadoc_ for more info).

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

JavaScript escape and unescape operations are performed by means of the `org.unbescape.javascript.JavaScriptEscape`
class. This class defines a series of static methods that perform the desired operations
(see the class _javadoc_ for more info).

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

JSON escape and unescape operations are performed by means of the `org.unbescape.json.JsonEscape` class. This class
defines a series of static methods that perform the desired operations (see the class _javadoc_ for more info).

There are simple, preconfigured methods:

```java
    final String escaped = JsonEscape.escapeJson(text);
    final String unescaped = JsonEscape.unescapeJson(escaped);
```

And also those that allow a more fine-grained configuration of the escape operation:

```java
    final String result =
        JsonEscape.escapeJson(
             text,
             JsonEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO__UHEXA,
             JsonEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
```



URI/URL Escape/Unescape
-----------------------

URI/URL escape and unescape operations are performed by means of the `org.unbescape.uri.UriEscape` class. This class
defines a series of static methods that perform the desired operations (see the class _javadoc_ for more info).

The methods for this type of escape/unescape operations are very simple:

```java
    final String escapedPath = UriEscape.escapeUriPath(text);
    final String escapedPathSegment = UriEscape.escapeUriPathSegment(text);
    final String escapedQueryParam = UriEscape.escapeUriQueryParam(text);
    final String escapedFragmentId = UriEscape.escapeUriFragmentId(text);
```

```java
    final String unescapedPath = UriEscape.unescapeUriPath(text);
    final String unescapedPathSegment = UriEscape.unescapeUriPathSegment(text);
    final String unescapedQueryParam = UriEscape.unescapeUriQueryParam(text);
    final String unescapedFragmentId = UriEscape.unescapeUriFragmentId(text);
```



CSS Escape/Unescape
--------------------

CSS escape and unescape operations are performed by means of the `org.unbescape.css.CssEscape` class. This class
defines a series of static methods that perform the desired operations (see the class _javadoc_ for more info).

Unbescape includes support for escaping both **CSS identifiers** and **CSS strings** (the former type apply
more strict syntax rules).

There are simple, preconfigured methods:

```java
    final String escapedIdentifier = CssEscape.escapeCssIdentifier(text);
    final String escapedString = CssEscape.escapeCssString(text);
    final String unescaped = CssEscape.unescapeCss(escapedIdentifierOrString);
```

And also those that allow a more fine-grained configuration of the escape operation:

```java
    final String identifierResult =
        CssEscape.escapeCssIdentifier(
             identifierText,
             CssIdentifierEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_SIX_DIGIT_HEXA,
             CssIdentifierEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    final String stringResult =
        CssEscape.escapeCssString(
             stringText,
             CssStringEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
             CssStringEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
```



CSV Escape/Unescape
-------------------

CSV (Comma-Separated Values) escape and unescape operations are performed by means of the `org.unbescape.csv.CsvEscape` class. This class
defines a series of static methods that perform the desired operations (see the class _javadoc_ for more info).

The methods for this type of escape/unescape operations are very simple:

```java
    final String escaped = CsvEscape.escapeCsv(text);
    final String unescaped = CsvEscape.unescapeCsv(escaped);
```



Java Literal Escape/Unescape
----------------------------

Java escape and unescape operations are performed by means of the `org.unbescape.java.JavaEscape` class. This class
defines a series of static methods that perform the desired operations (see the class _javadoc_ for more info).

There are simple, preconfigured methods:

```java
    final String escaped = JavaEscape.escapeJava(text);
    final String unescaped = JavaEscape.unescapeJava(escaped);
```

And also those that allow a more fine-grained configuration of the escape operation:

```java
    final String result =
        JavaEscape.escapeJava(
             text,
             JavaEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
```



Java `.properties` File Escape/Unescape
---------------------------------------

Java `.properties` escape and unescape operations are performed by means of the `org.unbescape.properties.PropertiesEscape` class. This class
defines a series of static methods that perform the desired operations (see the class _javadoc_ for more info).

Unbescape includes support for escaping both **properties keys** and **properties values** (keys require additional
escaping of ` `, `:` and `=`).

There are simple, preconfigured methods:

```java
    final String escapedKey = PropertiesEscape.escapePropertiesKey(text);
    final String escapedString = PropertiesEscape.escapePropertiesValue(text);
    final String unescaped = PropertiesEscape.unescapeProperties(escapedKeyOrValue);
```

And also those that allow a more fine-grained configuration of the escape operation:

```java
    final String identifierResult =
        PropertiesEscape.escapePropertiesKey(
             keyText, PropertiesKeyEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    final String stringResult =
        PropertiesEscape.escapePropertiesValue(
             valueText, PropertiesValueEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
```
