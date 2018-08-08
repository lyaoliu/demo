package com.lyliu.common.dialect;

/**
 * ${name} class
 * 数据库方言接口
 * @author lyliu
 * @date 2018/05/04 上午 9:11
 */
public interface Dialect {
    /**
     * @descrption 获取分页SQL
     * @author xdwang
     * @create 2012-12-19下午7:48:44
     * @param sql
     *            原始查询SQL
     * @param offset
     *            开始记录索引（从零开始）
     * @param limit
     *            每页记录大小
     * @return 返回数据库相关的分页SQL语句
     */
    public abstract String getPageSql(String sql, int offset, int limit);

    /**
     * 获取总数count sql
     * @param sql
     * @return
     */
    public abstract String getCountSql(String sql);

}
