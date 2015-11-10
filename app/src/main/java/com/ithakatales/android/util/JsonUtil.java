package com.ithakatales.android.util;

/**
 * @author Farhan Ali
 */
public class JsonUtil {

    // code copied from internet
    public static String formatJson(String text) {
        StringBuilder json = new StringBuilder();
        String indentString = "";

        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);
            switch (letter) {
                case '{':
                case '[':
                    json.append("\n" + indentString + letter + "\n");
                    indentString = indentString + "\t";
                    json.append(indentString);
                    break;
                case '}':
                case ']':
                    indentString = indentString.replaceFirst("\t", "");
                    json.append("\n" + indentString + letter);
                    break;
                case ',':
                    json.append(letter + "\n" + indentString);
                    break;
                default:
                    json.append(letter);
                    break;
            }
        }

        return json.toString();
    }

}
