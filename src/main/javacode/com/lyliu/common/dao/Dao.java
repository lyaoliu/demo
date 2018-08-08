package com.lyliu.common.dao;

import com.lyliu.common.dialect.MyRowBounds;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ${name} class
 *
 * @author lyliu
 * @date 2018/05/03 上午 10:01
 */
public class Dao implements IDao {
    protected SqlSessionTemplate sqlSession;
    public SqlSessionTemplate getSqlSession() {
        return sqlSession;
    }

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }


    @Override
    public List queryForList(String sql, Object obj) {
        return sqlSession.selectList(sql,obj);
    }

    @Override
    public int update(String sql, Object obj) {
        return sqlSession.update(sql,obj);
    }

    @Override
    public int delete(String sql, Object obj) {
        return sqlSession.delete(sql,obj);
    }

    @Override
    public int insert(String sql, Object obj) {
        return sqlSession.insert(sql,obj);
    }

    @Override
    public Object queryForObject(String sql, Object obj) {
        return sqlSession.selectOne(sql,obj);
    }

    @Override
    public List queryForPage(String sql, Object obj, RowBounds rowBounds) {
       // sqlSession.selectList(sql,obj,rowBounds);
        return  sqlSession.selectList(sql,obj,rowBounds);
    }

    @Override
    public List queryForPage(String sql, Object obj) {
        return sqlSession.selectList(sql,obj,new MyRowBounds());
    }

    @Override
    public PageInfo queryForPagewithCount(String sql, Object obj) {
        List list=sqlSession.selectList(sql,obj,new MyRowBounds());
        List<Object> map= sqlSession.selectList(sql,obj,new MyRowBounds(true));
        int total=((BigDecimal)((Map)map.get(0)).get("COUNT")).intValue();
        PageInfo pageInfo=new PageInfo(total,list);
        return pageInfo;
    }

    public void select(String statement, Object parameter , ResultHandler handler) {
        sqlSession.select(statement,parameter,handler);
    }
}
