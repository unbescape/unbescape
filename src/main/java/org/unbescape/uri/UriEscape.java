/*
 * =============================================================================
 * 
 *   Copyright (c) 2014, The UNBESCAPE team (http://www.unbescape.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.unbescape.uri;

import java.io.IOException;
import java.io.Writer;

/**
 * <p>
 *   Utility class for performing URI escape/unescape operations.
 * </p>
 *
 * <h4><u>Features</u></h4>
 *
 * <p>
 *   Specific features of the URI escape/unescape operations performed by means of this class:
 * </p>
 * <ul>
 *   <li>Support for percent-encoding-based escape operations (RFC3986) for diverse parts of an URI:
 *         <ul>
 *           <li><strong>Paths</strong>: Part of the URI path, might include several path levels/segments:
 *               <kbd>/admin/users/list?x=1</kbd> &rarr; <kbd>users/list</kbd></li>
 *           <li><strong>Path Segments</strong>: Part of the URI path, can include only one path level
 *               (<kbd>/</kbd> chars will be escaped): <kbd>/admin/users/list?x=1</kbd> &rarr; <kbd>users</kbd></li>
 *           <li><strong>Query Parameters</strong>: Names and values of the URI query parameters:
 *               <kbd>/admin/users/list?x=1</kbd> &rarr; <kbd>x</kbd> (name), <kbd>1</kbd> (value)</li>
 *           <li><strong>URI Fragment Identifiers</strong>: client-side part of URIs, specified after <kbd>#</kbd>:
 *               <kbd>/admin/users/list?x=1#something</kbd> &rarr; <kbd>#something</kbd></li>
 *         </ul>
 *   </li>
 *   <li>Support for both <em>percent-encoding</em> and <kbd>+</kbd> based unescaping of whitespace in query
 *       parameters.</li>
 * </ul>
 *
 * <h4><u>Input/Output</u></h4>
 *
 * <p>
 *   There are two different input/output modes that can be used in escape/unescape operations:
 * </p>
 * <ul>
 *   <li><em><kbd>String</kbd> input, <kbd>String</kbd> output</em>: Input is specified as a <kbd>String</kbd> object
 *       and output is returned as another. In order to improve memory performance, all escape and unescape
 *       operations <u>will return the exact same input object as output if no escape/unescape modifications
 *       are required</u>.</li>
 *   <li><em><kbd>char[]</kbd> input, <kbd>java.io.Writer</kbd> output</em>: Input will be read from a char array
 *       (<kbd>char[]</kbd>) and output will be written into the specified <kbd>java.io.Writer</kbd>.
 *       Two <kbd>int</kbd> arguments called <kbd>offset</kbd> and <kbd>len</kbd> will be
 *       used for specifying the part of the <kbd>char[]</kbd> that should be escaped/unescaped. These methods
 *       should be called with <kbd>offset = 0</kbd> and <kbd>len = text.length</kbd> in order to process
 *       the whole <kbd>char[]</kbd>.</li>
 * </ul>
 *
 * <h4><u>Glossary</u></h4>
 *
 * <dl>
 *   <dt>Percent encoding</dt>
 *     <dd>The percent-encoding technique for escaping consists of transforming the character that needs to be
 *         escaped into a sequence of bytes using a previously specified encoding (<kbd>UTF-8</kbd> by default), and
 *         then wrinting each byte as <kbd>%HH</kbd>, being <kbd>HH</kbd> its hexadecimal value (of the byte).
 *     </dd>
 * </dl>
 *
 * <h4><u>References</u></h4>
 *
 * <p>
 *   The following references apply:
 * </p>
 * <ul>
 *   <li><a href="http://www.ietf.org/rfc/rfc3986.txt" target="_blank">RFC3986: Uniform Resource Identifier
 *       (URI): Generic Syntax</a> [ietf.org]</li>
 *   <li><a href="http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4" target="_blank">HTML 4.01 Specification:
 *       Form Content Types</a> [w3.org]</li>
 * </ul>
 *
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 1.1.0
 *
 */
public final class UriEscape {


    /**
     * The default encoding for URI escaping/unescaping: <kbd>UTF-8</kbd>.
     */
    public static final String DEFAULT_ENCODING = "UTF-8";



    /**
     * <p>
     *   Perform am URI path <strong>escape</strong> operation
     *   on a <kbd>String</kbd> input using <kbd>UTF-8</kbd> as encoding.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI path (will not be escaped):
     * </p>
     * <ul>
     *   <li><kbd>A-Z a-z 0-9</kbd></li>
     *   <li><kbd>- . _ ~</kbd></li>
     *   <li><kbd>! $ & ' ( ) * + , ; =</kbd></li>
     *   <li><kbd>: @</kbd></li>
     *   <li><kbd>/</kbd></li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the <kbd>UTF-8</kbd> and then representing each byte
     *   in <kbd>%HH</kbd> syntax, being <kbd>HH</kbd> the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be escaped.
     * @return The escaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no escaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String escapeUriPath(final String text) {
        return escapeUriPath(text, DEFAULT_ENCODING);
    }


    /**
     * <p>
     *   Perform am URI path <strong>escape</strong> operation
     *   on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI path (will not be escaped):
     * </p>
     * <ul>
     *   <li><kbd>A-Z a-z 0-9</kbd></li>
     *   <li><kbd>- . _ ~</kbd></li>
     *   <li><kbd>! $ & ' ( ) * + , ; =</kbd></li>
     *   <li><kbd>: @</kbd></li>
     *   <li><kbd>/</kbd></li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the specified <em>encoding</em> and then representing each byte
     *   in <kbd>%HH</kbd> syntax, being <kbd>HH</kbd> the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be escaped.
     * @param encoding the encoding to be used for unescaping.
     * @return The escaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no escaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String escapeUriPath(final String text, final String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.escape(text, UriEscapeUtil.UriEscapeType.PATH, encoding);
    }



    /**
     * <p>
     *   Perform am URI path segment <strong>escape</strong> operation
     *   on a <kbd>String</kbd> input using <kbd>UTF-8</kbd> as encoding.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI path segment (will not be escaped):
     * </p>
     * <ul>
     *   <li><kbd>A-Z a-z 0-9</kbd></li>
     *   <li><kbd>- . _ ~</kbd></li>
     *   <li><kbd>! $ & ' ( ) * + , ; =</kbd></li>
     *   <li><kbd>: @</kbd></li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the <kbd>UTF-8</kbd> and then representing each byte
     *   in <kbd>%HH</kbd> syntax, being <kbd>HH</kbd> the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be escaped.
     * @return The escaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no escaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String escapeUriPathSegment(final String text) {
        return escapeUriPathSegment(text, DEFAULT_ENCODING);
    }


    /**
     * <p>
     *   Perform am URI path segment <strong>escape</strong> operation
     *   on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI path segment (will not be escaped):
     * </p>
     * <ul>
     *   <li><kbd>A-Z a-z 0-9</kbd></li>
     *   <li><kbd>- . _ ~</kbd></li>
     *   <li><kbd>! $ & ' ( ) * + , ; =</kbd></li>
     *   <li><kbd>: @</kbd></li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the specified <em>encoding</em> and then representing each byte
     *   in <kbd>%HH</kbd> syntax, being <kbd>HH</kbd> the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be escaped.
     * @param encoding the encoding to be used for escaping.
     * @return The escaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no escaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String escapeUriPathSegment(final String text, final String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.escape(text, UriEscapeUtil.UriEscapeType.PATH_SEGMENT, encoding);
    }



    /**
     * <p>
     *   Perform am URI query parameter (name or value) <strong>escape</strong> operation
     *   on a <kbd>String</kbd> input using <kbd>UTF-8</kbd> as encoding.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI query parameter (will not be escaped):
     * </p>
     * <ul>
     *   <li><kbd>A-Z a-z 0-9</kbd></li>
     *   <li><kbd>- . _ ~</kbd></li>
     *   <li><kbd>! $ ' ( ) * , ;</kbd></li>
     *   <li><kbd>: @</kbd></li>
     *   <li><kbd>/ ?</kbd></li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the <kbd>UTF-8</kbd> and then representing each byte
     *   in <kbd>%HH</kbd> syntax, being <kbd>HH</kbd> the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be escaped.
     * @return The escaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no escaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String escapeUriQueryParam(final String text) {
        return escapeUriQueryParam(text, DEFAULT_ENCODING);
    }


    /**
     * <p>
     *   Perform am URI query parameter (name or value) <strong>escape</strong> operation
     *   on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI query parameter (will not be escaped):
     * </p>
     * <ul>
     *   <li><kbd>A-Z a-z 0-9</kbd></li>
     *   <li><kbd>- . _ ~</kbd></li>
     *   <li><kbd>! $ ' ( ) * , ;</kbd></li>
     *   <li><kbd>: @</kbd></li>
     *   <li><kbd>/ ?</kbd></li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the specified <em>encoding</em> and then representing each byte
     *   in <kbd>%HH</kbd> syntax, being <kbd>HH</kbd> the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be escaped.
     * @param encoding the encoding to be used for escaping.
     * @return The escaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no escaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String escapeUriQueryParam(final String text, final String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.escape(text, UriEscapeUtil.UriEscapeType.QUERY_PARAM, encoding);
    }



    /**
     * <p>
     *   Perform am URI fragment identifier <strong>escape</strong> operation
     *   on a <kbd>String</kbd> input using <kbd>UTF-8</kbd> as encoding.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI fragment identifier (will not be escaped):
     * </p>
     * <ul>
     *   <li><kbd>A-Z a-z 0-9</kbd></li>
     *   <li><kbd>- . _ ~</kbd></li>
     *   <li><kbd>! $ & ' ( ) * + , ; =</kbd></li>
     *   <li><kbd>: @</kbd></li>
     *   <li><kbd>/ ?</kbd></li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the <kbd>UTF-8</kbd> and then representing each byte
     *   in <kbd>%HH</kbd> syntax, being <kbd>HH</kbd> the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be escaped.
     * @return The escaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no escaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String escapeUriFragmentId(final String text) {
        return escapeUriFragmentId(text, DEFAULT_ENCODING);
    }


    /**
     * <p>
     *   Perform am URI fragment identifier <strong>escape</strong> operation
     *   on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI fragment identifier (will not be escaped):
     * </p>
     * <ul>
     *   <li><kbd>A-Z a-z 0-9</kbd></li>
     *   <li><kbd>- . _ ~</kbd></li>
     *   <li><kbd>! $ & ' ( ) * + , ; =</kbd></li>
     *   <li><kbd>: @</kbd></li>
     *   <li><kbd>/ ?</kbd></li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the specified <em>encoding</em> and then representing each byte
     *   in <kbd>%HH</kbd> syntax, being <kbd>HH</kbd> the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be escaped.
     * @param encoding the encoding to be used for escaping.
     * @return The escaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no escaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String escapeUriFragmentId(final String text, final String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.escape(text, UriEscapeUtil.UriEscapeType.FRAGMENT_ID, encoding);
    }







    /**
     * <p>
     *   Perform am URI path <strong>escape</strong> operation
     *   on a <kbd>char[]</kbd> input using <kbd>UTF-8</kbd> as encoding.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI path (will not be escaped):
     * </p>
     * <ul>
     *   <li><kbd>A-Z a-z 0-9</kbd></li>
     *   <li><kbd>- . _ ~</kbd></li>
     *   <li><kbd>! $ & ' ( ) * + , ; =</kbd></li>
     *   <li><kbd>: @</kbd></li>
     *   <li><kbd>/</kbd></li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the <kbd>UTF-8</kbd> and then representing each byte
     *   in <kbd>%HH</kbd> syntax, being <kbd>HH</kbd> the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>char[]</kbd> to be escaped.
     * @param offset the position in <kbd>text</kbd> at which the escape operation should start.
     * @param len the number of characters in <kbd>text</kbd> that should be escaped.
     * @param writer the <kbd>java.io.Writer</kbd> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static void escapeUriPath(final char[] text, final int offset, final int len, final Writer writer)
                                       throws IOException {
        escapeUriPath(text, offset, len, writer, DEFAULT_ENCODING);
    }


    /**
     * <p>
     *   Perform am URI path <strong>escape</strong> operation
     *   on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI path (will not be escaped):
     * </p>
     * <ul>
     *   <li><kbd>A-Z a-z 0-9</kbd></li>
     *   <li><kbd>- . _ ~</kbd></li>
     *   <li><kbd>! $ & ' ( ) * + , ; =</kbd></li>
     *   <li><kbd>: @</kbd></li>
     *   <li><kbd>/</kbd></li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the specified <em>encoding</em> and then representing each byte
     *   in <kbd>%HH</kbd> syntax, being <kbd>HH</kbd> the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>char[]</kbd> to be escaped.
     * @param offset the position in <kbd>text</kbd> at which the escape operation should start.
     * @param len the number of characters in <kbd>text</kbd> that should be escaped.
     * @param writer the <kbd>java.io.Writer</kbd> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if <kbd>text</kbd> is <kbd>null</kbd>.
     * @param encoding the encoding to be used for escaping.
     */
    public static void escapeUriPath(final char[] text, final int offset, final int len, final Writer writer,
                                       final String encoding)
                                       throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }

        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }

        final int textLen = (text == null? 0 : text.length);

        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }

        if (len < 0 || (offset + len) > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }

        UriEscapeUtil.escape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.PATH, encoding);
    }



    /**
     * <p>
     *   Perform am URI path segment <strong>escape</strong> operation
     *   on a <kbd>char[]</kbd> input using <kbd>UTF-8</kbd> as encoding.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI path segment (will not be escaped):
     * </p>
     * <ul>
     *   <li><kbd>A-Z a-z 0-9</kbd></li>
     *   <li><kbd>- . _ ~</kbd></li>
     *   <li><kbd>! $ & ' ( ) * + , ; =</kbd></li>
     *   <li><kbd>: @</kbd></li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the <kbd>UTF-8</kbd> and then representing each byte
     *   in <kbd>%HH</kbd> syntax, being <kbd>HH</kbd> the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>char[]</kbd> to be escaped.
     * @param offset the position in <kbd>text</kbd> at which the escape operation should start.
     * @param len the number of characters in <kbd>text</kbd> that should be escaped.
     * @param writer the <kbd>java.io.Writer</kbd> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static void escapeUriPathSegment(final char[] text, final int offset, final int len, final Writer writer)
                                              throws IOException {
        escapeUriPathSegment(text, offset, len, writer, DEFAULT_ENCODING);
    }


    /**
     * <p>
     *   Perform am URI path segment <strong>escape</strong> operation
     *   on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI path segment (will not be escaped):
     * </p>
     * <ul>
     *   <li><kbd>A-Z a-z 0-9</kbd></li>
     *   <li><kbd>- . _ ~</kbd></li>
     *   <li><kbd>! $ & ' ( ) * + , ; =</kbd></li>
     *   <li><kbd>: @</kbd></li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the specified <em>encoding</em> and then representing each byte
     *   in <kbd>%HH</kbd> syntax, being <kbd>HH</kbd> the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>char[]</kbd> to be escaped.
     * @param offset the position in <kbd>text</kbd> at which the escape operation should start.
     * @param len the number of characters in <kbd>text</kbd> that should be escaped.
     * @param writer the <kbd>java.io.Writer</kbd> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if <kbd>text</kbd> is <kbd>null</kbd>.
     * @param encoding the encoding to be used for escaping.
     */
    public static void escapeUriPathSegment(final char[] text, final int offset, final int len, final Writer writer,
                                              final String encoding)
                                              throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }

        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }

        final int textLen = (text == null? 0 : text.length);

        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }

        if (len < 0 || (offset + len) > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        UriEscapeUtil.escape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.PATH_SEGMENT, encoding);
    }



    /**
     * <p>
     *   Perform am URI query parameter (name or value) <strong>escape</strong> operation
     *   on a <kbd>char[]</kbd> input using <kbd>UTF-8</kbd> as encoding.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI query parameter (will not be escaped):
     * </p>
     * <ul>
     *   <li><kbd>A-Z a-z 0-9</kbd></li>
     *   <li><kbd>- . _ ~</kbd></li>
     *   <li><kbd>! $ ' ( ) * , ;</kbd></li>
     *   <li><kbd>: @</kbd></li>
     *   <li><kbd>/ ?</kbd></li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the <kbd>UTF-8</kbd> and then representing each byte
     *   in <kbd>%HH</kbd> syntax, being <kbd>HH</kbd> the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>char[]</kbd> to be escaped.
     * @param offset the position in <kbd>text</kbd> at which the escape operation should start.
     * @param len the number of characters in <kbd>text</kbd> that should be escaped.
     * @param writer the <kbd>java.io.Writer</kbd> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static void escapeUriQueryParam(final char[] text, final int offset, final int len, final Writer writer)
                                             throws IOException {
        escapeUriQueryParam(text, offset, len, writer, DEFAULT_ENCODING);
    }


    /**
     * <p>
     *   Perform am URI query parameter (name or value) <strong>escape</strong> operation
     *   on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI query parameter (will not be escaped):
     * </p>
     * <ul>
     *   <li><kbd>A-Z a-z 0-9</kbd></li>
     *   <li><kbd>- . _ ~</kbd></li>
     *   <li><kbd>! $ ' ( ) * , ;</kbd></li>
     *   <li><kbd>: @</kbd></li>
     *   <li><kbd>/ ?</kbd></li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the specified <em>encoding</em> and then representing each byte
     *   in <kbd>%HH</kbd> syntax, being <kbd>HH</kbd> the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>char[]</kbd> to be escaped.
     * @param offset the position in <kbd>text</kbd> at which the escape operation should start.
     * @param len the number of characters in <kbd>text</kbd> that should be escaped.
     * @param writer the <kbd>java.io.Writer</kbd> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if <kbd>text</kbd> is <kbd>null</kbd>.
     * @param encoding the encoding to be used for escaping.
     */
    public static void escapeUriQueryParam(final char[] text, final int offset, final int len, final Writer writer,
                                             final String encoding)
                                             throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }

        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }

        final int textLen = (text == null? 0 : text.length);

        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }

        if (len < 0 || (offset + len) > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        UriEscapeUtil.escape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.QUERY_PARAM, encoding);
    }



    /**
     * <p>
     *   Perform am URI fragment identifier <strong>escape</strong> operation
     *   on a <kbd>char[]</kbd> input using <kbd>UTF-8</kbd> as encoding.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI fragment identifier (will not be escaped):
     * </p>
     * <ul>
     *   <li><kbd>A-Z a-z 0-9</kbd></li>
     *   <li><kbd>- . _ ~</kbd></li>
     *   <li><kbd>! $ & ' ( ) * + , ; =</kbd></li>
     *   <li><kbd>: @</kbd></li>
     *   <li><kbd>/ ?</kbd></li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the <kbd>UTF-8</kbd> and then representing each byte
     *   in <kbd>%HH</kbd> syntax, being <kbd>HH</kbd> the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>char[]</kbd> to be escaped.
     * @param offset the position in <kbd>text</kbd> at which the escape operation should start.
     * @param len the number of characters in <kbd>text</kbd> that should be escaped.
     * @param writer the <kbd>java.io.Writer</kbd> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static void escapeUriFragmentId(final char[] text, final int offset, final int len, final Writer writer)
                                             throws IOException {
        escapeUriFragmentId(text, offset, len, writer, DEFAULT_ENCODING);
    }


    /**
     * <p>
     *   Perform am URI fragment identifier <strong>escape</strong> operation
     *   on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   The following are the only allowed chars in an URI fragment identifier (will not be escaped):
     * </p>
     * <ul>
     *   <li><kbd>A-Z a-z 0-9</kbd></li>
     *   <li><kbd>- . _ ~</kbd></li>
     *   <li><kbd>! $ & ' ( ) * + , ; =</kbd></li>
     *   <li><kbd>: @</kbd></li>
     *   <li><kbd>/ ?</kbd></li>
     * </ul>
     * <p>
     *   All other chars will be escaped by converting them to the sequence of bytes that
     *   represents them in the specified <em>encoding</em> and then representing each byte
     *   in <kbd>%HH</kbd> syntax, being <kbd>HH</kbd> the hexadecimal representation of the byte.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>char[]</kbd> to be escaped.
     * @param offset the position in <kbd>text</kbd> at which the escape operation should start.
     * @param len the number of characters in <kbd>text</kbd> that should be escaped.
     * @param writer the <kbd>java.io.Writer</kbd> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if <kbd>text</kbd> is <kbd>null</kbd>.
     * @param encoding the encoding to be used for escaping.
     */
    public static void escapeUriFragmentId(final char[] text, final int offset, final int len, final Writer writer,
                                             final String encoding)
                                             throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }

        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }

        final int textLen = (text == null? 0 : text.length);

        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }

        if (len < 0 || (offset + len) > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        UriEscapeUtil.escape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.FRAGMENT_ID, encoding);
    }
















    /**
     * <p>
     *   Perform am URI path <strong>unescape</strong> operation
     *   on a <kbd>String</kbd> input using <kbd>UTF-8</kbd> as encoding.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded (<kbd>%HH</kbd>) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use <kbd>UTF-8</kbd> in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be unescaped.
     * @return The unescaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no unescaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String unescapeUriPath(final String text) {
        return unescapeUriPath(text, DEFAULT_ENCODING);
    }


    /**
     * <p>
     *   Perform am URI path <strong>unescape</strong> operation
     *   on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded (<kbd>%HH</kbd>) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use the specified <kbd>encoding</kbd> in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be unescaped.
     * @param encoding the encoding to be used for unescaping.
     * @return The unescaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no unescaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String unescapeUriPath(final String text, final String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.unescape(text, UriEscapeUtil.UriEscapeType.PATH, encoding);
    }



    /**
     * <p>
     *   Perform am URI path segment <strong>unescape</strong> operation
     *   on a <kbd>String</kbd> input using <kbd>UTF-8</kbd> as encoding.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded (<kbd>%HH</kbd>) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use <kbd>UTF-8</kbd> in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be unescaped.
     * @return The unescaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no unescaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String unescapeUriPathSegment(final String text) {
        return unescapeUriPathSegment(text, DEFAULT_ENCODING);
    }


    /**
     * <p>
     *   Perform am URI path segment <strong>unescape</strong> operation
     *   on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded (<kbd>%HH</kbd>) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use specified <kbd>encoding</kbd> in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be unescaped.
     * @param encoding the encoding to be used for unescaping.
     * @return The unescaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no unescaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String unescapeUriPathSegment(final String text, final String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.unescape(text, UriEscapeUtil.UriEscapeType.PATH_SEGMENT, encoding);
    }



    /**
     * <p>
     *   Perform am URI query parameter (name or value) <strong>unescape</strong> operation
     *   on a <kbd>String</kbd> input using <kbd>UTF-8</kbd> as encoding.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded (<kbd>%HH</kbd>) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use <kbd>UTF-8</kbd> in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be unescaped.
     * @return The unescaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no unescaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String unescapeUriQueryParam(final String text) {
        return unescapeUriQueryParam(text, DEFAULT_ENCODING);
    }


    /**
     * <p>
     *   Perform am URI query parameter (name or value) <strong>unescape</strong> operation
     *   on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded (<kbd>%HH</kbd>) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use specified <kbd>encoding</kbd> in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be unescaped.
     * @param encoding the encoding to be used for unescaping.
     * @return The unescaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no unescaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String unescapeUriQueryParam(final String text, final String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.unescape(text, UriEscapeUtil.UriEscapeType.QUERY_PARAM, encoding);
    }



    /**
     * <p>
     *   Perform am URI fragment identifier <strong>unescape</strong> operation
     *   on a <kbd>String</kbd> input using <kbd>UTF-8</kbd> as encoding.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded (<kbd>%HH</kbd>) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use <kbd>UTF-8</kbd> in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be unescaped.
     * @return The unescaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no unescaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String unescapeUriFragmentId(final String text) {
        return unescapeUriFragmentId(text, DEFAULT_ENCODING);
    }


    /**
     * <p>
     *   Perform am URI fragment identifier <strong>unescape</strong> operation
     *   on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded (<kbd>%HH</kbd>) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use specified <kbd>encoding</kbd> in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be unescaped.
     * @param encoding the encoding to be used for unescaping.
     * @return The unescaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no unescaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String unescapeUriFragmentId(final String text, final String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.unescape(text, UriEscapeUtil.UriEscapeType.FRAGMENT_ID, encoding);
    }







    /**
     * <p>
     *   Perform am URI path <strong>unescape</strong> operation
     *   on a <kbd>char[]</kbd> input using <kbd>UTF-8</kbd> as encoding.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded (<kbd>%HH</kbd>) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use <kbd>UTF-8</kbd> in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>char[]</kbd> to be unescaped.
     * @param offset the position in <kbd>text</kbd> at which the escape operation should start.
     * @param len the number of characters in <kbd>text</kbd> that should be escaped.
     * @param writer the <kbd>java.io.Writer</kbd> to which the unescaped result will be written. Nothing will
     *               be written at all to this writer if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static void unescapeUriPath(final char[] text, final int offset, final int len, final Writer writer)
            throws IOException {
        unescapeUriPath(text, offset, len, writer, DEFAULT_ENCODING);
    }


    /**
     * <p>
     *   Perform am URI path <strong>unescape</strong> operation
     *   on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded (<kbd>%HH</kbd>) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use specified <kbd>encoding</kbd> in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>char[]</kbd> to be unescaped.
     * @param offset the position in <kbd>text</kbd> at which the escape operation should start.
     * @param len the number of characters in <kbd>text</kbd> that should be escaped.
     * @param writer the <kbd>java.io.Writer</kbd> to which the unescaped result will be written. Nothing will
     *               be written at all to this writer if <kbd>text</kbd> is <kbd>null</kbd>.
     * @param encoding the encoding to be used for unescaping.
     */
    public static void unescapeUriPath(final char[] text, final int offset, final int len, final Writer writer,
                                     final String encoding)
            throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }

        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }

        final int textLen = (text == null? 0 : text.length);

        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }

        if (len < 0 || (offset + len) > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }

        UriEscapeUtil.unescape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.PATH, encoding);
    }



    /**
     * <p>
     *   Perform am URI path segment <strong>unescape</strong> operation
     *   on a <kbd>char[]</kbd> input using <kbd>UTF-8</kbd> as encoding.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded (<kbd>%HH</kbd>) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use <kbd>UTF-8</kbd> in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>char[]</kbd> to be unescaped.
     * @param offset the position in <kbd>text</kbd> at which the escape operation should start.
     * @param len the number of characters in <kbd>text</kbd> that should be escaped.
     * @param writer the <kbd>java.io.Writer</kbd> to which the unescaped result will be written. Nothing will
     *               be written at all to this writer if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static void unescapeUriPathSegment(final char[] text, final int offset, final int len, final Writer writer)
            throws IOException {
        unescapeUriPathSegment(text, offset, len, writer, DEFAULT_ENCODING);
    }


    /**
     * <p>
     *   Perform am URI path segment <strong>unescape</strong> operation
     *   on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded (<kbd>%HH</kbd>) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use specified <kbd>encoding</kbd> in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>char[]</kbd> to be unescaped.
     * @param offset the position in <kbd>text</kbd> at which the escape operation should start.
     * @param len the number of characters in <kbd>text</kbd> that should be escaped.
     * @param writer the <kbd>java.io.Writer</kbd> to which the unescaped result will be written. Nothing will
     *               be written at all to this writer if <kbd>text</kbd> is <kbd>null</kbd>.
     * @param encoding the encoding to be used for unescaping.
     */
    public static void unescapeUriPathSegment(final char[] text, final int offset, final int len, final Writer writer,
                                            final String encoding)
            throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }

        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }

        final int textLen = (text == null? 0 : text.length);

        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }

        if (len < 0 || (offset + len) > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        UriEscapeUtil.unescape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.PATH_SEGMENT, encoding);
    }



    /**
     * <p>
     *   Perform am URI query parameter (name or value) <strong>unescape</strong> operation
     *   on a <kbd>char[]</kbd> input using <kbd>UTF-8</kbd> as encoding.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded (<kbd>%HH</kbd>) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use <kbd>UTF-8</kbd> in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>char[]</kbd> to be unescaped.
     * @param offset the position in <kbd>text</kbd> at which the escape operation should start.
     * @param len the number of characters in <kbd>text</kbd> that should be escaped.
     * @param writer the <kbd>java.io.Writer</kbd> to which the unescaped result will be written. Nothing will
     *               be written at all to this writer if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static void unescapeUriQueryParam(final char[] text, final int offset, final int len, final Writer writer)
            throws IOException {
        unescapeUriQueryParam(text, offset, len, writer, DEFAULT_ENCODING);
    }


    /**
     * <p>
     *   Perform am URI query parameter (name or value) <strong>unescape</strong> operation
     *   on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded (<kbd>%HH</kbd>) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use specified <kbd>encoding</kbd> in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>char[]</kbd> to be unescaped.
     * @param offset the position in <kbd>text</kbd> at which the escape operation should start.
     * @param len the number of characters in <kbd>text</kbd> that should be escaped.
     * @param writer the <kbd>java.io.Writer</kbd> to which the unescaped result will be written. Nothing will
     *               be written at all to this writer if <kbd>text</kbd> is <kbd>null</kbd>.
     * @param encoding the encoding to be used for unescaping.
     */
    public static void unescapeUriQueryParam(final char[] text, final int offset, final int len, final Writer writer,
                                           final String encoding)
            throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }

        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }

        final int textLen = (text == null? 0 : text.length);

        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }

        if (len < 0 || (offset + len) > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        UriEscapeUtil.unescape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.QUERY_PARAM, encoding);
    }



    /**
     * <p>
     *   Perform am URI fragment identifier <strong>unescape</strong> operation
     *   on a <kbd>char[]</kbd> input using <kbd>UTF-8</kbd> as encoding.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded (<kbd>%HH</kbd>) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use <kbd>UTF-8</kbd> in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>char[]</kbd> to be unescaped.
     * @param offset the position in <kbd>text</kbd> at which the escape operation should start.
     * @param len the number of characters in <kbd>text</kbd> that should be escaped.
     * @param writer the <kbd>java.io.Writer</kbd> to which the unescaped result will be written. Nothing will
     *               be written at all to this writer if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static void unescapeUriFragmentId(final char[] text, final int offset, final int len, final Writer writer)
            throws IOException {
        unescapeUriFragmentId(text, offset, len, writer, DEFAULT_ENCODING);
    }


    /**
     * <p>
     *   Perform am URI fragment identifier <strong>unescape</strong> operation
     *   on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   This method will unescape every percent-encoded (<kbd>%HH</kbd>) sequences present in input,
     *   even for those characters that do not need to be percent-encoded in this context (unreserved characters
     *   can be percent-encoded even if/when this is not required, though it is not generally considered a
     *   good practice).
     * </p>
     * <p>
     *   This method will use specified <kbd>encoding</kbd> in order to determine the characters specified in the
     *   percent-encoded byte sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>char[]</kbd> to be unescaped.
     * @param offset the position in <kbd>text</kbd> at which the escape operation should start.
     * @param len the number of characters in <kbd>text</kbd> that should be escaped.
     * @param writer the <kbd>java.io.Writer</kbd> to which the unescaped result will be written. Nothing will
     *               be written at all to this writer if <kbd>text</kbd> is <kbd>null</kbd>.
     * @param encoding the encoding to be used for unescaping.
     */
    public static void unescapeUriFragmentId(final char[] text, final int offset, final int len, final Writer writer,
                                           final String encoding)
            throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }

        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }

        final int textLen = (text == null? 0 : text.length);

        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }

        if (len < 0 || (offset + len) > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        UriEscapeUtil.unescape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.FRAGMENT_ID, encoding);
    }










    private UriEscape() {
        super();
    }


}

