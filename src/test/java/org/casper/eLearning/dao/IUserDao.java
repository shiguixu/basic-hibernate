package org.casper.eLearning.dao;

import java.util.List;
import java.util.Map;

import org.casper.eLearning.model.Pager;
import org.casper.eLearning.model.User;

public interface IUserDao extends IBaseDao<User> {
	List<User> listUserByHql(String hql,Object[] args,Map<String,Object> alias);
	Pager<User> findUserByHql(String hql,Object[] args,Map<String,Object> alias);
	public Pager<User> find(String hql,Object[] args,Map<String,Object> alias);
}
