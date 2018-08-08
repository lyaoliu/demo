package com.lyliu.common.interceptor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

import com.lyliu.common.dialect.Dialect;
import com.lyliu.common.dialect.MyRowBounds;
import com.lyliu.common.dialect.MySqlDialect;
import com.lyliu.common.dialect.OracleDialect;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.alibaba.druid.util.JdbcConstants.MYSQL;
import static com.alibaba.druid.util.JdbcConstants.ORACLE;


/**
 *
 * 分页拦截器，用于拦截需要进行分页查询的操作，然后对其进行分页处理。
 * 利用拦截器实现Mybatis分页的原理：
 * 要利用JDBC对数据库进行操作就必须要有一个对应的Statement对象，Mybatis在执行Sql语句前就会产生一个包含Sql语句的Statement对象，而且对应的Sql语句
 * 是在Statement之前产生的，所以我们就可以在它生成Statement之前对用来生成Statement的Sql语句下手。在Mybatis中Statement语句是通过RoutingStatementHandler对象的
 * prepare方法生成的。所以利用拦截器实现Mybatis分页的一个思路就是拦截StatementHandler接口的prepare方法，然后在拦截器方法中把Sql语句改成对应的分页查询Sql语句，之后再调用
 * StatementHandler对象的prepare方法，即调用invocation.proceed()。
 * 对于分页而言，在拦截器里面我们还需要做的一个操作就是统计满足当前条件的记录一共有多少，这是通过获取到了原始的Sql语句后，把它改为对应的统计语句再利用Mybatis封装好的参数和设
 * 置参数的功能把Sql语句中的参数进行替换，之后再执行查询记录数的Sql语句进行总记录数的统计。
 * @author lyliu
 *  @date 2018/05/03 下午 4:19
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})})
public class PageInterceptor implements Interceptor {
    private static final Logger LOGGER=LogManager.getLogger("PageInterceptor");
    //默认数据库方言
    private static String defaultDialect="oracle";
    private String dialectType;
    public void setDialectType(String dialectType) {
        this.dialectType = dialectType;
    }

    public String getDialectType() {
        return dialectType;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (invocation.getTarget() instanceof StatementHandler) {
            return handleStatementHandler(invocation);
        }
        return invocation.proceed();
    }

    /**
     *  获取代理对象
     * @param o
     * @return
     */
    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o,this);
    }

    /**
     * 设置代理对象的参数
     * @param properties
     */
    @Override
    public void setProperties(Properties properties) {

    }

    /**
     * @param invocation
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private Object handleStatementHandler(Invocation invocation)
            throws InvocationTargetException, IllegalAccessException {
        StatementHandler statementHandler = (StatementHandler) invocation
                .getTarget();
        MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
        RowBounds rowBounds = (RowBounds) metaStatementHandler
                .getValue("delegate.rowBounds");
        //没有进行分页查询
        if (rowBounds == null || (rowBounds.getOffset() == RowBounds.NO_ROW_OFFSET && rowBounds
                .getLimit() == RowBounds.NO_ROW_LIMIT)) {
            return invocation.proceed();
        }

        // 分离代理对象链(由于目标类可能被多个拦截器拦截，从而形成多次代理，通过下面的两次循环可以分离出最原始的的目标类)
        while (metaStatementHandler.hasGetter("h")) {
            Object object = metaStatementHandler.getValue("h");
            metaStatementHandler = SystemMetaObject.forObject(object);
        }
        // 分离最后一个代理对象的目标类
        while (metaStatementHandler.hasGetter("target")) {
            Object object = metaStatementHandler.getValue("target");
            metaStatementHandler = SystemMetaObject.forObject(object);
        }
         Configuration configuration = (Configuration) metaStatementHandler.
                getValue("delegate.configuration");
        dialectType = configuration.getVariables().getProperty("dialect");
        if (null == dialectType || "".equals(dialectType)) {
            LOGGER.warn("Property dialect is not setted,use default 'mysql' ");
            dialectType = defaultDialect;
        }
        Dialect dialect = null;
        switch (dialectType) {
            case MYSQL:
                dialect = new MySqlDialect();
                break;
            case ORACLE:
                dialect = new OracleDialect();
                break;
            default:
                dialect = new MySqlDialect();
        }
        Boolean isCount=((MyRowBounds)rowBounds).isCount();
        String originalSql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");
        if(isCount){
            metaStatementHandler.setValue("delegate.boundSql.sql", dialect.getCountSql(originalSql));
            metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
            metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
            BoundSql boundSql = statementHandler.getBoundSql();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("生成总数SQL : " + boundSql.getSql());
            }
        }else {
            metaStatementHandler.setValue("delegate.boundSql.sql", dialect.getPageSql(originalSql, rowBounds.getOffset(), rowBounds.getLimit()));
            metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
            metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
            BoundSql boundSql = statementHandler.getBoundSql();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("生成分页SQL : " + boundSql.getSql());
            }
        }
        // 将执行权交给下一个拦截器
        return invocation.proceed();
    }



}
