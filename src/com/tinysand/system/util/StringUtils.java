package com.tinysand.system.util;

import java.util.Objects;
import java.util.function.BiFunction;

public class StringUtils {
    public static boolean isEmpty(final Object text) {
        return !Objects.nonNull(text) || (String.valueOf(text))
                .trim().isEmpty();
    }

    private static String wordProcessor
            (final String text, final BiFunction
                    <Character, Character, String> converter) {
        if (!Objects.nonNull(text) || text.trim().length() == 0
                || !Objects.nonNull(converter))
            return NULL_TEXT;
        final StringBuilder resultBuilder = new StringBuilder();
        for (int index = 0; index < text.length() - 1; index++) {
            if (index == 0)
                resultBuilder.append(Character.toLowerCase
                        (text.charAt(index)));

            Character firstChar = text.charAt(index);
            Character secondChar = text.charAt(index + 1);
            resultBuilder.append(converter.apply(firstChar, secondChar));
        }
        return resultBuilder.toString();
    }

    public static String toUnderline(String text) {
        return wordProcessor(text, (firstChar, secondChar) -> {
            if (Character.isUpperCase(firstChar) &&
                    Character.isLowerCase(secondChar))
                return NULL_TEXT + secondChar;
            else if (Character.isLowerCase(firstChar) &&
                    Character.isUpperCase(secondChar))
                return UNDERLINE + Character.toLowerCase
                        (secondChar) + NULL_TEXT;
            else
                return NULL_TEXT + secondChar;
        });
    }

    public static String toCamel(String text) {
        return wordProcessor(text, (firstChar, secondChar) -> {
            if (firstChar == UNDERLINE_CHAR &&
                    Character.isLowerCase(secondChar))
                return Character.toUpperCase(secondChar) + NULL_TEXT;
            else if (secondChar == UNDERLINE_CHAR)
                return NULL_TEXT;
            else
                return secondChar + NULL_TEXT;
        });
    }

    private static final char UNDERLINE_CHAR = '_';
    private static final String NULL_TEXT = "";
    private static final String UNDERLINE = "_";
}
