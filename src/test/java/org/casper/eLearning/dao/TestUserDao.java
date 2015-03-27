package org.casper.eLearning.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.casper.eLearning.model.Pager;
import org.casper.eLearning.model.SystemContext;
import org.casper.eLearning.model.User;
import org.casper.eLearning.util.AbstractDbUnitTestCase;
import org.casper.eLearning.util.EntitiesHelper;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
//指定使用的单元测试执行类，这里我们使用SpringJunit4ClassRunner.class类
@RunWith(SpringJUnit4ClassRunner.class)
//指定Spring的配置文件路径，可以指定多个，用逗号隔开；
@ContextConfiguration("/beans.xml")
//指定测试类之前，要执行的操作：DependencyInjectionTestExecutionListener.class可以实现测试类之前的依赖注入
@TestExecutionListeners({DbUnitTestExecutionListener.class, 
		DependencyInjectionTestExecutionListener.class})
public class TestUserDao extends AbstractDbUnitTestCase{
	
	@Inject
	private SessionFactory sessionFactory;
	@Inject
	private IUserDao userDao;
	
	@Before
	public void setUp() throws DataSetException, SQLException, IOException {
		Session s = sessionFactory.openSession();
		//把当前线程与事务绑定
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(s));
		bakcupOneTable("tb_user");
	}
	
	@Test
	public void testSelect() throws DatabaseUnitException, SQLException, IOException {
		IDataSet ds = createDateSet("tb_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,ds);
		User u = userDao.select(1);
		EntitiesHelper.assertUser(u);
	}
	
/*	@Test(expected=ObjectNotFoundException.class)
	public void testDelete() throws DatabaseUnitException, SQLException, IOException {
		IDataSet ds = createDateSet("tb_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,ds);
		userDao.delete(1);
		User tu = userDao.select(1);
	}*/
	
	@Test
	public void testListUserByHql() throws DatabaseUnitException, SQLException, IOException{
		IDataSet ds=createDateSet("tb_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		String hql="from User u where u.id=? and u.username=:username";
		Object[] args=new Object[]{1};
		Map<String,Object> alias=new HashMap<String,Object>();
		alias.put("username", "admin1");
		List<User> lists=userDao.listUserByHql(hql, args, alias);
		Assert.assertEquals(lists.get(0).getUsername(), "admin1");
	}
	
	@Test
	public void testFind() throws IOException, DatabaseUnitException, SQLException{
		IDataSet ds=createDateSet("tb_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		Object[] args= new Object[]{1};
		Map<String,Object> alias=new HashMap<String, Object>();
		alias.put("username", "admin%");
		SystemContext.setPageOffset(0);
		SystemContext.setPageSize(5);
		String hql ="from User u where u.id>? and u.username like :username";
		Pager<User> lists=userDao.find(hql, args, alias);
		Assert.assertEquals(5, lists.getDatas().size());
		}
	@After
	public void tearDown() throws DatabaseUnitException, SQLException, IOException {
		SessionHolder holder = (SessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
		Session s = holder.getSession(); 
		s.flush();
		TransactionSynchronizationManager.unbindResource(sessionFactory);
		resumesTable();
	}
}
