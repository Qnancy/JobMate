package cn.edu.zju.cs.jobmate.utils.csv;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal RFC 4180-style CSV parser for Jinshuju exports (UTF-8, comma-separated).
 */
public final class CsvTextParser {

    private CsvTextParser() {
    }

    /**
     * Parse CSV text into rows; each row is an array of field values.
     */
    public static List<String[]> parse(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        String normalized = stripBom(text);
        List<String[]> rows = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        List<String> current = new ArrayList<>();
        boolean inQuotes = false;

        for (int i = 0; i < normalized.length(); i++) {
            char c = normalized.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < normalized.length() && normalized.charAt(i + 1) == '"') {
                        field.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    field.append(c);
                }
                continue;
            }

            if (c == '"') {
                inQuotes = true;
            } else if (c == ',') {
                current.add(field.toString());
                field.setLength(0);
            } else if (c == '\r') {
                // skip
            } else if (c == '\n') {
                current.add(field.toString());
                field.setLength(0);
                rows.add(current.toArray(new String[0]));
                current = new ArrayList<>();
            } else {
                field.append(c);
            }
        }

        if (inQuotes) {
            throw new IllegalArgumentException("CSV 引号未闭合");
        }

        if (!field.isEmpty() || !current.isEmpty()) {
            current.add(field.toString());
            rows.add(current.toArray(new String[0]));
        }

        return rows;
    }

    private static String stripBom(String text) {
        if (text.startsWith("\uFEFF")) {
            return text.substring(1);
        }
        return text;
    }
}
