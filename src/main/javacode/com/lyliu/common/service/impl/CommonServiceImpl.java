package com.lyliu.common.service.impl;

import com.lyliu.common.dao.Dao;
import com.lyliu.common.dao.IDao;
import com.lyliu.common.dao.PageInfo;
import com.lyliu.common.service.CommonService;

import java.util.List;
import java.util.Map;

/**
 * ${name} class
 *
 * @author lyliu
 * @date 2018/05/03 下午 8:57
 */
public class CommonServiceImpl extends Dao implements CommonService {
    protected IDao dao;
    public IDao getDao() {
        return dao;
    }

    public void setDao(IDao dao) {
        this.dao = dao;
    }

    @Override
    public List listUser() {

        return dao.queryForList("user.listUser","");
    }

    @Override
    public PageInfo listUser(Map M) {
        return dao.queryForPagewithCount("user.listUser",M);
    }
}
