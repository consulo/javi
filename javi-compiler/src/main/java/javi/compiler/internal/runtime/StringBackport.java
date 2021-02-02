package javi.compiler.internal.runtime;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author VISTALL
 * @since 02/02/2021
 *
 * String Extensions which moved from JDK+
 */
public class StringBackport {
    /**
     * Returns a string whose value is this string, with incidental
     * {@linkplain Character#isWhitespace(int) white space} removed from
     * the beginning and end of every line.
     * <p>
     * Incidental {@linkplain Character#isWhitespace(int) white space}
     * is often present in a text block to align the content with the opening
     * delimiter. For example, in the following code, dots represent incidental
     * {@linkplain Character#isWhitespace(int) white space}:
     * <blockquote><pre>
     * String html = """
     * ..............&lt;html&gt;
     * ..............    &lt;body&gt;
     * ..............        &lt;p&gt;Hello, world&lt;/p&gt;
     * ..............    &lt;/body&gt;
     * ..............&lt;/html&gt;
     * ..............""";
     * </pre></blockquote>
     * This method treats the incidental
     * {@linkplain Character#isWhitespace(int) white space} as indentation to be
     * stripped, producing a string that preserves the relative indentation of
     * the content. Using | to visualize the start of each line of the string:
     * <blockquote><pre>
     * |&lt;html&gt;
     * |    &lt;body&gt;
     * |        &lt;p&gt;Hello, world&lt;/p&gt;
     * |    &lt;/body&gt;
     * |&lt;/html&gt;
     * </pre></blockquote>
     * First, the individual lines of this string are extracted. A <i>line</i>
     * is a sequence of zero or more characters followed by either a line
     * terminator or the end of the string.
     * If the string has at least one line terminator, the last line consists
     * of the characters between the last terminator and the end of the string.
     * Otherwise, if the string has no terminators, the last line is the start
     * of the string to the end of the string, in other words, the entire
     * string.
     * A line does not include the line terminator.
     * <p>
     * Then, the <i>minimum indentation</i> (min) is determined as follows:
     * <ul>
     * <li><p>For each non-blank line (as defined by {@link String#isBlank()}),
     * the leading {@linkplain Character#isWhitespace(int) white space}
     * characters are counted.</p>
     * </li>
     * <li><p>The leading {@linkplain Character#isWhitespace(int) white space}
     * characters on the last line are also counted even if
     * {@linkplain String#isBlank() blank}.</p>
     * </li>
     * </ul>
     * <p>The <i>min</i> value is the smallest of these counts.
     * <p>
     * For each {@linkplain String#isBlank() non-blank} line, <i>min</i> leading
     * {@linkplain Character#isWhitespace(int) white space} characters are
     * removed, and any trailing {@linkplain Character#isWhitespace(int) white
     * space} characters are removed. {@linkplain String#isBlank() Blank} lines
     * are replaced with the empty string.
     *
     * <p>
     * Finally, the lines are joined into a new string, using the LF character
     * {@code "\n"} (U+000A) to separate lines.
     *
     * @return string with incidental indentation removed and line
     * terminators normalized
     * @apiNote This method's primary purpose is to shift a block of lines as far as
     * possible to the left, while preserving relative indentation. Lines
     * that were indented the least will thus have no leading
     * {@linkplain Character#isWhitespace(int) white space}.
     * The result will have the same number of line terminators as this string.
     * If this string ends with a line terminator then the result will end
     * with a line terminator.
     * @implSpec This method treats all {@linkplain Character#isWhitespace(int) white space}
     * characters as having equal width. As long as the indentation on every
     * line is consistently composed of the same character sequences, then the
     * result will be as described above.
     * @see String#lines()
     * @see String#isBlank()
     * @see String#indent(int)
     * @see Character#isWhitespace(int)
     * @since 15
     */
    public static String stripIndent(String str) {
        int length = str.length();
        if (length == 0) {
            return "";
        }
        char lastChar = str.charAt(length - 1);
        boolean optOut = lastChar == '\n' || lastChar == '\r';
        List<String> lines = str.lines().collect(Collectors.toList());
        final int outdent = optOut ? 0 : outdent(lines);
        return lines.stream()
                .map(line -> {
                    int firstNonWhitespace = indexOfNonWhitespace(line);
                    int lastNonWhitespace = lastIndexOfNonWhitespace(line);
                    int incidentalWhitespace = Math.min(outdent, firstNonWhitespace);
                    return firstNonWhitespace > lastNonWhitespace
                            ? "" : line.substring(incidentalWhitespace, lastNonWhitespace);
                })
                .collect(Collectors.joining("\n", "", optOut ? "\n" : ""));
    }

    public static int lastIndexOfNonWhitespace(String value) {
        int length = value.length();
        int right = length;
        while (0 < right) {
            char ch = value.charAt(right - 1);
            if (ch != ' ' && ch != '\t' && !Character.isWhitespace(ch)) {
                break;
            }
            right--;
        }
        return right;
    }

    public static int indexOfNonWhitespace(String value) {
        int length = value.length();
        int left = 0;
        while (left < length) {
            char ch = value.charAt(left);
            if (ch != ' ' && ch != '\t' && !Character.isWhitespace(ch)) {
                break;
            }
            left++;
        }
        return left;
    }

    private static int outdent(List<String> lines) {
        // Note: outdent is guaranteed to be zero or positive number.
        // If there isn't a non-blank line then the last must be blank
        int outdent = Integer.MAX_VALUE;
        for (String line : lines) {
            int leadingWhitespace = indexOfNonWhitespace(line);
            if (leadingWhitespace != line.length()) {
                outdent = Integer.min(outdent, leadingWhitespace);
            }
        }
        String lastLine = lines.get(lines.size() - 1);
        if (lastLine.isBlank()) {
            outdent = Integer.min(outdent, lastLine.length());
        }
        return outdent;
    }

    /**
     * Returns a string whose value is this string, with escape sequences
     * translated as if in a string literal.
     * <p>
     * Escape sequences are translated as follows;
     * <table class="striped">
     * <caption style="display:none">Translation</caption>
     * <thead>
     * <tr>
     * <th scope="col">Escape</th>
     * <th scope="col">Name</th>
     * <th scope="col">Translation</th>
     * </tr>
     * </thead>
     * <tbody>
     * <tr>
     * <th scope="row">{@code \u005Cb}</th>
     * <td>backspace</td>
     * <td>{@code U+0008}</td>
     * </tr>
     * <tr>
     * <th scope="row">{@code \u005Ct}</th>
     * <td>horizontal tab</td>
     * <td>{@code U+0009}</td>
     * </tr>
     * <tr>
     * <th scope="row">{@code \u005Cn}</th>
     * <td>line feed</td>
     * <td>{@code U+000A}</td>
     * </tr>
     * <tr>
     * <th scope="row">{@code \u005Cf}</th>
     * <td>form feed</td>
     * <td>{@code U+000C}</td>
     * </tr>
     * <tr>
     * <th scope="row">{@code \u005Cr}</th>
     * <td>carriage return</td>
     * <td>{@code U+000D}</td>
     * </tr>
     * <tr>
     * <th scope="row">{@code \u005Cs}</th>
     * <td>space</td>
     * <td>{@code U+0020}</td>
     * </tr>
     * <tr>
     * <th scope="row">{@code \u005C"}</th>
     * <td>double quote</td>
     * <td>{@code U+0022}</td>
     * </tr>
     * <tr>
     * <th scope="row">{@code \u005C'}</th>
     * <td>single quote</td>
     * <td>{@code U+0027}</td>
     * </tr>
     * <tr>
     * <th scope="row">{@code \u005C\u005C}</th>
     * <td>backslash</td>
     * <td>{@code U+005C}</td>
     * </tr>
     * <tr>
     * <th scope="row">{@code \u005C0 - \u005C377}</th>
     * <td>octal escape</td>
     * <td>code point equivalents</td>
     * </tr>
     * <tr>
     * <th scope="row">{@code \u005C<line-terminator>}</th>
     * <td>continuation</td>
     * <td>discard</td>
     * </tr>
     * </tbody>
     * </table>
     *
     * @return String with escape sequences translated.
     * @throws IllegalArgumentException when an escape sequence is malformed.
     * @implNote This method does <em>not</em> translate Unicode escapes such as "{@code \u005cu2022}".
     * Unicode escapes are translated by the Java compiler when reading input characters and
     * are not part of the string literal specification.
     * @jls 3.10.7 Escape Sequences
     * @since 15
     */
    public static String translateEscapes(String str) {
        if (str.isEmpty()) {
            return "";
        }
        char[] chars = str.toCharArray();
        int length = chars.length;
        int from = 0;
        int to = 0;
        while (from < length) {
            char ch = chars[from++];
            if (ch == '\\') {
                ch = from < length ? chars[from++] : '\0';
                switch (ch) {
                    case 'b':
                        ch = '\b';
                        break;
                    case 'f':
                        ch = '\f';
                        break;
                    case 'n':
                        ch = '\n';
                        break;
                    case 'r':
                        ch = '\r';
                        break;
                    case 's':
                        ch = ' ';
                        break;
                    case 't':
                        ch = '\t';
                        break;
                    case '\'':
                    case '\"':
                    case '\\':
                        // as is
                        break;
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                        int limit = Integer.min(from + (ch <= '3' ? 2 : 1), length);
                        int code = ch - '0';
                        while (from < limit) {
                            ch = chars[from];
                            if (ch < '0' || '7' < ch) {
                                break;
                            }
                            from++;
                            code = (code << 3) | (ch - '0');
                        }
                        ch = (char) code;
                        break;
                    case '\n':
                        continue;
                    case '\r':
                        if (from < length && chars[from] == '\n') {
                            from++;
                        }
                        continue;
                    default: {
                        String msg = String.format(
                                "Invalid escape sequence: \\%c \\\\u%04X",
                                ch, (int) ch);
                        throw new IllegalArgumentException(msg);
                    }
                }
            }

            chars[to++] = ch;
        }

        return new String(chars, 0, to);
    }
}
