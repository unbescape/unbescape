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
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 1.1
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
     *   <li><kbd>'A'-'Z'</kbd>,<kbd>'a'-'z'</kbd>,<kbd>'0'-'9'</kbd></li>
     *   <li><kbd>'-'</kbd>,<kbd>'.'</kbd>,<kbd>'_'</kbd>,<kbd>'~'</kbd></li>
     *   <li><kbd>'!'</kbd>,<kbd>'$'</kbd>,<kbd>'&'</kbd>,<kbd>'\''</kbd>,<kbd>'('</kbd>,<kbd>')'</kbd>,<kbd>'*'</kbd>,<kbd>'+'</kbd>,<kbd>','</kbd>,<kbd>';'</kbd>,<kbd>'='</kbd></li>
     *   <li><kbd>':'</kbd>,<kbd>'@'</kbd></li>
     *   <li><kbd>'/'</kbd></li>
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
     *   <li><kbd>'A'-'Z'</kbd>,<kbd>'a'-'z'</kbd>,<kbd>'0'-'9'</kbd></li>
     *   <li><kbd>'-'</kbd>,<kbd>'.'</kbd>,<kbd>'_'</kbd>,<kbd>'~'</kbd></li>
     *   <li><kbd>'!'</kbd>,<kbd>'$'</kbd>,<kbd>'&'</kbd>,<kbd>'\''</kbd>,<kbd>'('</kbd>,<kbd>')'</kbd>,<kbd>'*'</kbd>,<kbd>'+'</kbd>,<kbd>','</kbd>,<kbd>';'</kbd>,<kbd>'='</kbd></li>
     *   <li><kbd>':'</kbd>,<kbd>'@'</kbd></li>
     *   <li><kbd>'/'</kbd></li>
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
     *   <li><kbd>'A'-'Z'</kbd>,<kbd>'a'-'z'</kbd>,<kbd>'0'-'9'</kbd></li>
     *   <li><kbd>'-'</kbd>,<kbd>'.'</kbd>,<kbd>'_'</kbd>,<kbd>'~'</kbd></li>
     *   <li><kbd>'!'</kbd>,<kbd>'$'</kbd>,<kbd>'&'</kbd>,<kbd>'\''</kbd>,<kbd>'('</kbd>,<kbd>')'</kbd>,<kbd>'*'</kbd>,<kbd>'+'</kbd>,<kbd>','</kbd>,<kbd>';'</kbd>,<kbd>'='</kbd></li>
     *   <li><kbd>':'</kbd>,<kbd>'@'</kbd></li>
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
     *   <li><kbd>'A'-'Z'</kbd>,<kbd>'a'-'z'</kbd>,<kbd>'0'-'9'</kbd></li>
     *   <li><kbd>'-'</kbd>,<kbd>'.'</kbd>,<kbd>'_'</kbd>,<kbd>'~'</kbd></li>
     *   <li><kbd>'!'</kbd>,<kbd>'$'</kbd>,<kbd>'&'</kbd>,<kbd>'\''</kbd>,<kbd>'('</kbd>,<kbd>')'</kbd>,<kbd>'*'</kbd>,<kbd>'+'</kbd>,<kbd>','</kbd>,<kbd>';'</kbd>,<kbd>'='</kbd></li>
     *   <li><kbd>':'</kbd>,<kbd>'@'</kbd></li>
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
     *   <li><kbd>'A'-'Z'</kbd>,<kbd>'a'-'z'</kbd>,<kbd>'0'-'9'</kbd></li>
     *   <li><kbd>'-'</kbd>,<kbd>'.'</kbd>,<kbd>'_'</kbd>,<kbd>'~'</kbd></li>
     *   <li><kbd>'!'</kbd>,<kbd>'$'</kbd>,<kbd>'\''</kbd>,<kbd>'('</kbd>,<kbd>')'</kbd>,<kbd>'*'</kbd>,<kbd>','</kbd>,<kbd>';'</kbd></li>
     *   <li><kbd>':'</kbd>,<kbd>'@'</kbd></li>
     *   <li><kbd>'/'</kbd>,<kbd>'?'</kbd></li>
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
     *   <li><kbd>'A'-'Z'</kbd>,<kbd>'a'-'z'</kbd>,<kbd>'0'-'9'</kbd></li>
     *   <li><kbd>'-'</kbd>,<kbd>'.'</kbd>,<kbd>'_'</kbd>,<kbd>'~'</kbd></li>
     *   <li><kbd>'!'</kbd>,<kbd>'$'</kbd>,<kbd>'\''</kbd>,<kbd>'('</kbd>,<kbd>')'</kbd>,<kbd>'*'</kbd>,<kbd>','</kbd>,<kbd>';'</kbd></li>
     *   <li><kbd>':'</kbd>,<kbd>'@'</kbd></li>
     *   <li><kbd>'/'</kbd>,<kbd>'?'</kbd></li>
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
     *   <li><kbd>'A'-'Z'</kbd>,<kbd>'a'-'z'</kbd>,<kbd>'0'-'9'</kbd></li>
     *   <li><kbd>'-'</kbd>,<kbd>'.'</kbd>,<kbd>'_'</kbd>,<kbd>'~'</kbd></li>
     *   <li><kbd>'!'</kbd>,<kbd>'$'</kbd>,<kbd>'&'</kbd>,<kbd>'\''</kbd>,<kbd>'('</kbd>,<kbd>')'</kbd>,<kbd>'*'</kbd>,<kbd>'+'</kbd>,<kbd>','</kbd>,<kbd>';'</kbd>,<kbd>'='</kbd></li>
     *   <li><kbd>':'</kbd>,<kbd>'@'</kbd></li>
     *   <li><kbd>'/'</kbd>,<kbd>'?'</kbd></li>
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
     *   <li><kbd>'A'-'Z'</kbd>,<kbd>'a'-'z'</kbd>,<kbd>'0'-'9'</kbd></li>
     *   <li><kbd>'-'</kbd>,<kbd>'.'</kbd>,<kbd>'_'</kbd>,<kbd>'~'</kbd></li>
     *   <li><kbd>'!'</kbd>,<kbd>'$'</kbd>,<kbd>'&'</kbd>,<kbd>'\''</kbd>,<kbd>'('</kbd>,<kbd>')'</kbd>,<kbd>'*'</kbd>,<kbd>'+'</kbd>,<kbd>','</kbd>,<kbd>';'</kbd>,<kbd>'='</kbd></li>
     *   <li><kbd>':'</kbd>,<kbd>'@'</kbd></li>
     *   <li><kbd>'/'</kbd>,<kbd>'?'</kbd></li>
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
     *   <li><kbd>'A'-'Z'</kbd>,<kbd>'a'-'z'</kbd>,<kbd>'0'-'9'</kbd></li>
     *   <li><kbd>'-'</kbd>,<kbd>'.'</kbd>,<kbd>'_'</kbd>,<kbd>'~'</kbd></li>
     *   <li><kbd>'!'</kbd>,<kbd>'$'</kbd>,<kbd>'&'</kbd>,<kbd>'\''</kbd>,<kbd>'('</kbd>,<kbd>')'</kbd>,<kbd>'*'</kbd>,<kbd>'+'</kbd>,<kbd>','</kbd>,<kbd>';'</kbd>,<kbd>'='</kbd></li>
     *   <li><kbd>':'</kbd>,<kbd>'@'</kbd></li>
     *   <li><kbd>'/'</kbd></li>
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
     *   <li><kbd>'A'-'Z'</kbd>,<kbd>'a'-'z'</kbd>,<kbd>'0'-'9'</kbd></li>
     *   <li><kbd>'-'</kbd>,<kbd>'.'</kbd>,<kbd>'_'</kbd>,<kbd>'~'</kbd></li>
     *   <li><kbd>'!'</kbd>,<kbd>'$'</kbd>,<kbd>'&'</kbd>,<kbd>'\''</kbd>,<kbd>'('</kbd>,<kbd>')'</kbd>,<kbd>'*'</kbd>,<kbd>'+'</kbd>,<kbd>','</kbd>,<kbd>';'</kbd>,<kbd>'='</kbd></li>
     *   <li><kbd>':'</kbd>,<kbd>'@'</kbd></li>
     *   <li><kbd>'/'</kbd></li>
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
     *   <li><kbd>'A'-'Z'</kbd>,<kbd>'a'-'z'</kbd>,<kbd>'0'-'9'</kbd></li>
     *   <li><kbd>'-'</kbd>,<kbd>'.'</kbd>,<kbd>'_'</kbd>,<kbd>'~'</kbd></li>
     *   <li><kbd>'!'</kbd>,<kbd>'$'</kbd>,<kbd>'&'</kbd>,<kbd>'\''</kbd>,<kbd>'('</kbd>,<kbd>')'</kbd>,<kbd>'*'</kbd>,<kbd>'+'</kbd>,<kbd>','</kbd>,<kbd>';'</kbd>,<kbd>'='</kbd></li>
     *   <li><kbd>':'</kbd>,<kbd>'@'</kbd></li>
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
     *   <li><kbd>'A'-'Z'</kbd>,<kbd>'a'-'z'</kbd>,<kbd>'0'-'9'</kbd></li>
     *   <li><kbd>'-'</kbd>,<kbd>'.'</kbd>,<kbd>'_'</kbd>,<kbd>'~'</kbd></li>
     *   <li><kbd>'!'</kbd>,<kbd>'$'</kbd>,<kbd>'&'</kbd>,<kbd>'\''</kbd>,<kbd>'('</kbd>,<kbd>')'</kbd>,<kbd>'*'</kbd>,<kbd>'+'</kbd>,<kbd>','</kbd>,<kbd>';'</kbd>,<kbd>'='</kbd></li>
     *   <li><kbd>':'</kbd>,<kbd>'@'</kbd></li>
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
     *   <li><kbd>'A'-'Z'</kbd>,<kbd>'a'-'z'</kbd>,<kbd>'0'-'9'</kbd></li>
     *   <li><kbd>'-'</kbd>,<kbd>'.'</kbd>,<kbd>'_'</kbd>,<kbd>'~'</kbd></li>
     *   <li><kbd>'!'</kbd>,<kbd>'$'</kbd>,<kbd>'\''</kbd>,<kbd>'('</kbd>,<kbd>')'</kbd>,<kbd>'*'</kbd>,<kbd>','</kbd>,<kbd>';'</kbd></li>
     *   <li><kbd>':'</kbd>,<kbd>'@'</kbd></li>
     *   <li><kbd>'/'</kbd>,<kbd>'?'</kbd></li>
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
     *   <li><kbd>'A'-'Z'</kbd>,<kbd>'a'-'z'</kbd>,<kbd>'0'-'9'</kbd></li>
     *   <li><kbd>'-'</kbd>,<kbd>'.'</kbd>,<kbd>'_'</kbd>,<kbd>'~'</kbd></li>
     *   <li><kbd>'!'</kbd>,<kbd>'$'</kbd>,<kbd>'\''</kbd>,<kbd>'('</kbd>,<kbd>')'</kbd>,<kbd>'*'</kbd>,<kbd>','</kbd>,<kbd>';'</kbd></li>
     *   <li><kbd>':'</kbd>,<kbd>'@'</kbd></li>
     *   <li><kbd>'/'</kbd>,<kbd>'?'</kbd></li>
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
     *   <li><kbd>'A'-'Z'</kbd>,<kbd>'a'-'z'</kbd>,<kbd>'0'-'9'</kbd></li>
     *   <li><kbd>'-'</kbd>,<kbd>'.'</kbd>,<kbd>'_'</kbd>,<kbd>'~'</kbd></li>
     *   <li><kbd>'!'</kbd>,<kbd>'$'</kbd>,<kbd>'&'</kbd>,<kbd>'\''</kbd>,<kbd>'('</kbd>,<kbd>')'</kbd>,<kbd>'*'</kbd>,<kbd>'+'</kbd>,<kbd>','</kbd>,<kbd>';'</kbd>,<kbd>'='</kbd></li>
     *   <li><kbd>':'</kbd>,<kbd>'@'</kbd></li>
     *   <li><kbd>'/'</kbd>,<kbd>'?'</kbd></li>
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
     *   <li><kbd>'A'-'Z'</kbd>,<kbd>'a'-'z'</kbd>,<kbd>'0'-'9'</kbd></li>
     *   <li><kbd>'-'</kbd>,<kbd>'.'</kbd>,<kbd>'_'</kbd>,<kbd>'~'</kbd></li>
     *   <li><kbd>'!'</kbd>,<kbd>'$'</kbd>,<kbd>'&'</kbd>,<kbd>'\''</kbd>,<kbd>'('</kbd>,<kbd>')'</kbd>,<kbd>'*'</kbd>,<kbd>'+'</kbd>,<kbd>','</kbd>,<kbd>';'</kbd>,<kbd>'='</kbd></li>
     *   <li><kbd>':'</kbd>,<kbd>'@'</kbd></li>
     *   <li><kbd>'/'</kbd>,<kbd>'?'</kbd></li>
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











    private UriEscape() {
        super();
    }


}

