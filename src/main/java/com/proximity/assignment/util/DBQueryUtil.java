package com.proximity.assignment.util;

import com.proximity.assignment.commons.Constants;
import com.proximity.assignment.commons.DBResult;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.proximity.assignment.util.Utils.wrapWithRoundBracket;

/**
 * @author sthammishetty on 19/06/21
 */
public class DBQueryUtil {

    public static DBResult createEntity(String tableName, List<String> columnsList, JSONArray params, String returnColumn) {
        StringBuilder sb = new StringBuilder("INSERT INTO ").append(tableName).append(" %s ").append(" VALUES ");
        List<String> valuesList = Arrays.asList(StringUtils.repeat(Constants.QUESTION_MARK, columnsList.size()).split(""));
        String valueString = wrapWithRoundBracket(String.join(",", valuesList));
        sb.append(valueString);
        if (StringUtils.isNotBlank(returnColumn)) {
            sb.append(" RETURNING ").append(returnColumn);
        }
        String insertQuery = sb.toString();
        String columnNames = wrapWithRoundBracket(String.join(",", columnsList));
        insertQuery = String.format(insertQuery, columnNames);
        return DBServiceUtil.executeQueryWithParams(insertQuery, params);
    }

    public static DBResult getEntityByEntityId(String tableName, String columnId, Long idValue) {
        String query = new StringBuilder("SELECT * FROM ").append(tableName).append(" WHERE ")
                .append(columnId).append(" = ").append(idValue).toString();
        return DBServiceUtil.executeQuery(query);
    }

    public static Long getIdFromResult(DBResult result, String idKey) {
        Long idValue = null;
        if (Objects.nonNull(result.getResult())) {
            JSONArray jsonArray = (JSONArray) result.getResult();
            if (jsonArray.length() != 0) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                idValue = Long.valueOf(String.valueOf(jsonObject.get(idKey)));
            }
        }
        return idValue;
    }
}
