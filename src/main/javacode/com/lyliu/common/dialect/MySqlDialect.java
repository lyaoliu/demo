package com.lyliu.common.dialect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ${name} class
 * MySQL数据库实现
 * @author lyliu
 * @date 2018/05/04 上午 9:13
 */
public class MySqlDialect implements Dialect {
    /**
     * 获取分页sql语句
     * @param sql
     *            原始查询SQL
     * @param offset
     *            开始记录索引（从零开始）
     * @param limit
     *            每页记录大小
     * @return
     */
    @Override
    public String getPageSql(String sql, int offset, int limit) {
        return DialectUtil.getPageSql(sql, offset, limit);
    }
    protected static final String SQL_END_DELIMITER = ";";

    public String getPageSql(String sql, boolean hasOffset) {
        return getPageSql(sql, -1, -1);
    }

    public boolean supportsLimit() {
        return true;
    }

    @Override
    public String getCountSql(String sql) {
        return DialectUtil.getMysqlCountString(sql);
    }

}
