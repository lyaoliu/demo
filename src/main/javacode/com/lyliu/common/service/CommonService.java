package com.lyliu.common.service;

import com.lyliu.common.dao.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * ${name} class
 *
 * @author lyliu
 * @date 2018/05/03 下午 8:56
 */
public interface CommonService {
    public List listUser();
    public PageInfo listUser(Map M);
}
