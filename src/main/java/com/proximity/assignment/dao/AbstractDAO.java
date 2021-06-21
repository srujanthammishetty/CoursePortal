package com.proximity.assignment.dao;

import com.proximity.assignment.commons.DBResult;
import org.json.JSONArray;

import java.util.List;

/**
 * @author sthammishetty on 18/06/21
 */
public interface AbstractDAO<T> {

    DBResult executeQuery(String query);

    DBResult update(String query);

    DBResult updateWithParams(String query, JSONArray jsonArray);

    T get(Long id);

    List<T> listAll();
}
