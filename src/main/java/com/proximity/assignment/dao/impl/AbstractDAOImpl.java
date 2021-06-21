package com.proximity.assignment.dao.impl;

import com.proximity.assignment.commons.DBResult;
import com.proximity.assignment.dao.AbstractDAO;
import com.proximity.assignment.util.DBServiceUtil;
import org.json.JSONArray;

/**
 * @author sthammishetty on 18/06/21
 */
public abstract class AbstractDAOImpl<T> implements AbstractDAO<T> {

    @Override
    public DBResult executeQuery(String query) {
        return DBServiceUtil.executeQuery(query);
    }

    @Override
    public DBResult update(String query) {
        return DBServiceUtil.executeUpdate(query);
    }

    @Override
    public DBResult updateWithParams(String query, JSONArray jsonArray) {
        return DBServiceUtil.executeUpdateWithParams(query, jsonArray);
    }


}
