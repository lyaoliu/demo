package com.lyliu.common.dialect;

/**
 * ${name} class
 * oracle数据库方言
 * @author lyliu
 * @date 2018/05/04 上午 10:09
 */
public class OracleDialect implements Dialect {
    @Override
    public String getPageSql(String sql, int offset, int limit) {
        //去掉两边的空格
        sql=sql.trim();
        //去掉sql中间多余的空格
        sql=DialectUtil.getLineSql(sql);
        boolean isForUpdate = false;
        if (sql.toLowerCase().endsWith(" for update")) {
            sql = sql.substring(0, sql.length() - 11);
            isForUpdate = true;
        }
        StringBuffer pageSql = new StringBuffer(sql.length() + 100);
        pageSql.append("select * from ( select row_.*, rownum rownum_ from ( ");
        pageSql.append(sql);
        pageSql.append(" ) row_ ) where rownum_ > " + offset + " and rownum_ <= " + (offset + limit));
        if (isForUpdate) {
            pageSql.append(" for update");
        }
        return pageSql.toString();
    }

    @Override
    public String getCountSql(String sql) {
        return DialectUtil.getOracleCountString(sql);
    }
}
