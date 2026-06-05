package cn.edu.zju.cs.jobmate.utils.csv;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CsvTextParserTest {

    @Test
    void parseSimpleRows() {
        List<String[]> rows = CsvTextParser.parse("a,b\n1,2");
        assertEquals(2, rows.size());
        assertEquals("a", rows.get(0)[0]);
        assertEquals("b", rows.get(0)[1]);
        assertEquals("1", rows.get(1)[0]);
        assertEquals("2", rows.get(1)[1]);
    }

    @Test
    void parseQuotedComma() {
        List<String[]> rows = CsvTextParser.parse("name,desc\n华为,\"北京、上海\"");
        assertEquals("北京、上海", rows.get(1)[1]);
    }

    @Test
    void stripUtf8Bom() {
        List<String[]> rows = CsvTextParser.parse("\uFEFF序号,名称\n1,测试");
        assertEquals("序号", rows.get(0)[0]);
    }

    @Test
    void unclosedQuoteThrows() {
        assertThrows(IllegalArgumentException.class, () -> CsvTextParser.parse("\"abc\n1,2"));
    }
}
