package cn.edu.zju.cs.jobmate.utils.csv;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JinshujuCsvHeadersTest {

    @Test
    void rowToMapUsesHeaderNames() {
        String[] header = { JinshujuCsvHeaders.COMPANY_FULL_NAME, JinshujuCsvHeaders.INFO_TYPE };
        String[] values = { "华为技术有限公司", JinshujuCsvHeaders.INFO_TYPE_COMPANY };
        Map<String, String> row = JinshujuCsvHeaders.rowToMap(header, values);
        assertEquals("华为技术有限公司", JinshujuCsvHeaders.get(row, JinshujuCsvHeaders.COMPANY_FULL_NAME));
        assertEquals(JinshujuCsvHeaders.INFO_TYPE_COMPANY, JinshujuCsvHeaders.get(row, JinshujuCsvHeaders.INFO_TYPE));
    }
}
