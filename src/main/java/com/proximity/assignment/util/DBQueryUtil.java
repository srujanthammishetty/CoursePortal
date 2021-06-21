package com.proximity.assignment.util;

import com.proximity.assignment.commons.Constants;
import com.proximity.assignment.commons.DBResult;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;

import java.util.Arrays;
import java.util.List;

import static com.proximity.assignment.util.Utils.wrapWithRoundBracket;

/**
 * @author sthammishetty on 19/06/21
 */
public class DBQueryUtil {

    public static DBResult createEntity(String tableName, List<String> columnsList, JSONArray params) {
        StringBuilder sb = new StringBuilder("INSERT INTO ").append(tableName).append(" %s ").append(" VALUES ");


        List<String> valuesList = Arrays.asList(StringUtils.repeat(Constants.QUESTION_MARK, columnsList.size()).split(""));
        String valueString = wrapWithRoundBracket(String.join(",", valuesList));
        sb.append(valueString);

        String insertQuery = sb.toString();
        String columnNames = wrapWithRoundBracket(String.join(",", columnsList));
        insertQuery = String.format(insertQuery, columnNames);
        return DBServiceUtil.executeUpdateWithParams(insertQuery, params);
    }

    public static DBResult getEntityById(String tableName, String columnId, Long idValue) {
        String query = new StringBuilder("SELECT * FROM ").append(tableName).append(" WHERE ")
                .append(columnId).append(" = ").append(idValue).toString();
        return DBServiceUtil.executeQuery(query);
    }
}
