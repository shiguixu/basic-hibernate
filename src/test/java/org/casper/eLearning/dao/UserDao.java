package org.casper.eLearning.dao;

import java.util.List;
import java.util.Map;

import org.casper.eLearning.model.Pager;
import org.casper.eLearning.model.User;
import org.springframework.stereotype.Repository;

//@Repository是spring基于持久层（dao）的注入注解；而相对应的业务层为@Service;控制器层为@Controller
@Repository("userDao")
public class UserDao extends BaseDao<User> implements IUserDao {
	
	@Override
	public List<User> listUserByHql(String hql,Object[] args,Map<String,Object> alias){
		return super.list(hql, args, alias);
	}

	@Override
	public Pager<User> findUserByHql(String hql, Object[] args,
			Map<String, Object> alias) {
		return super.find(hql, args, alias);
	}

}
