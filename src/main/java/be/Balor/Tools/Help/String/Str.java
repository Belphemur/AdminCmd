/**
 * Programmer: Jacob Scott
 * Program Name: Str
 * Description:
 * Date: Mar 31, 2011
 */
package be.Balor.Tools.Help.String;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * @author jacob
 */
public class Str extends OutputStream {

    public String text = "";

    public static String argStr(String[] s) {
        return argStr(s, " ", 0);
    }
    
    public static String argStr(String[] s, int start) {
        return argStr(s, " ", start);
    }

    public static String argStr(String[] s, String sep) {
        return argStr(s, sep, 0);
    }

    public static String argStr(String[] s, String sep, int start) {
        String ret = "";
        if (s != null) {
            for (int i = start; i < s.length; ++i) {
                ret += s[i];
                if (i + 1 < s.length) {
                    ret += sep;
                }
            }
        }
        return ret;
    }

    public static String argStr(String[] s, String sep, int start, int length) {
        String ret = "";
        if (s != null) {
            for (int i = start, j = 0; i < s.length && j < length; ++i, ++j) {
                ret += s[i];
                if (i + 1 < s.length) {
                    ret += sep;
                }
            }
        }
        return ret;
    }

    public static boolean isIn(String input, String[] check) {
        input = input.trim();
        for (String c : check) {
            if (input.equalsIgnoreCase(c.trim())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isIn(String input, String check) {
        String comms[] = check.split(",");
        input = input.trim();
        for (String c : comms) {
            if (input.equalsIgnoreCase(c.trim())) {
                return true;
            }
        }
        return false;
    }

    public static boolean startIsIn(String input, String check) {
        String comms[] = check.split(",");
        for (String c : comms) {
            if (input.length() >= c.length()) {
                if (input.substring(0, c.length()).equalsIgnoreCase(c)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean startIsIn(String input, String[] check) {
        for (String c : check) {
            if (input.length() >= c.length()) {
                if (input.substring(0, c.length()).equalsIgnoreCase(c)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int count(String str, String find) {
        int c = 0;
        for (int i = 0; i < str.length() - find.length(); ++i) {
            if (str.substring(i, i + find.length()).equals(find)) {
                ++c;
            }
        }
        return c;
    }

    public static int count(String str, char find) {
        int c = 0;
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) == find) {
                ++c;
            }
        }
        return c;
    }

    public static int countIgnoreCase(String str, String find) {
        int c = 0;
        for (int i = 0; i < str.length() - find.length(); ++i) {
            if (str.substring(i, i + find.length()).equalsIgnoreCase(find)) {
                ++c;
            }
        }
        return c;
    }

    public static int indexOf(String array[], String search) {
        if (array != null && array.length > 0) {
            for (int i = array.length - 1; i >= 0; --i) {
                if (array[i].equals(search)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int indexOfIgnoreCase(String array[], String search) {
        for (int i = array.length - 1; i >= 0; --i) {
            if (array[i].equalsIgnoreCase(search)) {
                return i;
            }
        }
        return -1;
    }

    public static String getStackStr(Exception err) {
        if (err == null) {// || err.getCause() == null) {
            return "";
        }
        Str stackoutstream = new Str();
        PrintWriter stackstream = new PrintWriter(stackoutstream);
        err.printStackTrace(stackstream);
        stackstream.flush();
        stackstream.close();
        return stackoutstream.text;

    }

    /**
     * pads str on the right (space-padded) (left-align)
     * @param str
     * @param len
     * @return
     */
    public static String padRight(String str, int len) {
        for (int i = str.length(); i < len; ++i) {
            str += ' ';
        }
        return str;
    }

    /**
     * pads str on the right with pad (left-align)
     * @param str
     * @param len
     * @param pad
     * @return
     */
    public static String padRight(String str, int len, char pad) {
        for (int i = str.length(); i < len; ++i) {
            str += pad;
        }
        return str;
    }

    /**
     * pads str on the left (space-padded) (right-align)
     * @param str
     * @param len
     * @return
     */
    public static String padLeft(String str, int len) {
        return repeat(' ', len - str.length()) + str;
    }

    /**
     * pads str on the left with pad (right-align)
     * @param str
     * @param len
     * @param pad
     * @return
     */
    public static String padLeft(String str, int len, char pad) {
        return repeat(pad, len - str.length()) + str;
    }

    /**
     * pads str on the left & right (space-padded) (center-align)
     * @param str
     * @param len
     * @return
     */
    public static String padCenter(String str, int len) {
        len -= str.length();
        int prepad = len / 2;
        return repeat(' ', prepad) + str + repeat(' ', len - prepad);
    }

    /**
     * pads str on the left & right with pad (center-align)
     * @param str
     * @param len
     * @param pad
     * @return
     */
    public static String padCenter(String str, int len, char pad) {
        len -= str.length();
        int prepad = len / 2;
        return repeat(pad, prepad) + str + repeat(pad, len - prepad);
    }

    public static String repeat(char ch, int len) {
        String str = "";
        for (int i = 0; i < len; ++i) {
            str += ch;
        }
        return str;
    }

    /**
     * Returns a sequence str of the provided str count # of times
     * @param str
     * @param count
     * @return
     */
    public static String repeat(String str, int count) {
        String retstr = "";
        for (int i = 0; i < count; ++i) {
            retstr += str;
        }
        return retstr;
    }

    @Override
    public void write(int b) throws IOException {
        text += (char) b;
    }
} // end class Str
