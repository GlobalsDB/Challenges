package com.uosipa.globalsdb.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.StringTokenizer;

/**
 * @author Dmitry Levshunov (levshunov.d@gmail.com)
 */
public class StringUtil {
    private StringUtil() {
        throw new AssertionError();
    }

    public static Collection<String> splitToLines(String text) {
        if (text == null) {
            return Collections.emptyList();
        }

        Collection<String> result = new ArrayList<String>();

        StringTokenizer tokenizer = new StringTokenizer(text, "\r\n");
        while (tokenizer.hasMoreTokens()) {
            result.add(tokenizer.nextToken());
        }

        return result;
    }
}
