package org.casper.eLearning.dao;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.casper.eLearning.model.Pager;
import org.casper.eLearning.model.SystemContext;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class BaseDao<T> implements IBaseDao<T> {

	// 通过依赖注入获得SessionFactory
	@Inject
	private SessionFactory sessionFactory;

	// 使用了inject注解过后可以省去getter 和setter方法
	/*
	 * public SessionFactory getSessionFactory() { return sessionFactory; }
	 * public void setSessionFactory(SessionFactory sessionFactory) {
	 * this.sessionFactory = sessionFactory; }
	 */
	/**
	 * 获取当前的Session
	 * 
	 * @return
	 */
	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	/**
	 * 定义了一个class，获取泛型的class；
	 */
	private Class<?> clz;

	public Class<?> getClz() {
		if (clz == null) {
			// 获取泛型的Class对象
			clz = ((Class<?>) (((ParameterizedType) (this.getClass()
					.getGenericSuperclass())).getActualTypeArguments()[0]));
		}
		return clz;
	}

	/**
	 * 增加一个对象
	 */
	@Override
	public T add(T t) {
		getSession().save(t);
		return t;
	}

	/**
	 * 更新一个对象
	 */
	@Override
	public void update(T t) {
		getSession().update(t);
	}

	/**
	 * 根据id删除一个对象
	 */
	@Override
	public void delete(Integer id) {
		getSession().delete(this.select(id));
	}

	/**
	 * 根据id查询一个对象
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T select(Integer id) {
		return (T) getSession().load(getClz(), id);
	}

	/**
	 * 初始化排序的字段和排序的方式
	 * @param hql 输入的hql语句
	 * @return 返回增加了排序规则的hql语句
	 */
	private String initSort(String hql) {
		String order = SystemContext.getOrder();
		String sort = SystemContext.getSort();
		if (sort != null && !"".equals(sort.trim())) {
			hql += " order by" + sort;
			if (!"desc".equals(order)) {
				hql += " asc";
			} else {
				hql += " desc";
			}
		}
		return hql;
	}

	/**
	 * 设置别名参数
	 * from User u u.username= :username
	 * @param query ：Hibernate的查询对象
	 * @param alias	：使用到的别名参数
	 */
	private void setAliasParameter(Query query, Map<String, Object> alias) {
		if (alias != null) {
			Set<String> keys = alias.keySet();
			for (String key : keys) {
				Object val = alias.get(key);
				if (val instanceof Collection) {
					// 查选条件是列表
					query.setParameterList(key, (Collection) val);
				} else {
					query.setParameter(key, val);
				}
			}
		}
	}

	
	/**
	 * 设置带参数的查询
	 * 
	 * @param query：Hibernate的查询的对象
	 * @param args	：设置的参数
	 */
	private void setParameter(Query query, Object[] args) {
		if (args != null && args.length > 0) {
			int index = 0;
			for (Object arg : args) {
				query.setParameter(index++, arg);
			}
		}
	}

	/**
	 * 根据hql的查询，使用参数对象，别名查询
	 * 
	 * @param hql
	 * @param args
	 * @param alias
	 * @return
	 */
	public List<T> list(String hql, Object[] args, Map<String, Object> alias) {
		hql = initSort(hql);
		Query query = getSession().createQuery(hql);
		setAliasParameter(query, alias);
		setParameter(query, args);
		return query.list();
	}
	
	/**
	 * 使用参数查询
	 * @param hql
	 * @param args
	 * @return
	 */
	public List<T> listByParameter(String hql, Object[] args) {
		return this.list(hql, args, null);
	}
	/**
	 * 基于别名的查询
	 * @param hql
	 * @param alias
	 * @return
	 */
	public List<T> listByAlias(String hql, Map<String, Object> alias) {
		return this.list(hql, null, alias);
	}
	/**
	 * 基于单个参数对象的查询
	 * @param hql
	 * @param obj
	 * @return
	 */
	public List<T> list(String hql, Object obj) {
		return this.list(hql, new Object[] { obj });
	}



	/**
	 * 设置分页参数
	 * 
	 * @param query：Hibernate的查询对象
	 * @param pager	：分页参数
	 */
	private void setPagers(Query query, Pager pager) {
		Integer pageSize = SystemContext.getPageSize();
		Integer pageOffset = SystemContext.getPageOffset();
		if (pageOffset == null || pageOffset < 0) {
			pageOffset = 0;
		}
		if (pageSize == null || pageSize < 0) {
			pageSize = 10;
		}
		pager.setPageOffset(pageOffset);
		pager.setPageSize(pageSize);
		query.setFirstResult(pageOffset).setMaxResults(pageSize);

	}
	/**
	 * 注意这个fetch的功能，我们这里只是查询每条hql的总数，fetch的作用就是在不影响Hibernate的性能，但是又能加载关联类的实体类；我们在针对Hibernate缓存是最好默认关闭 lazy=false，影响性能；
	 * @param hql：查询的对象
	 * @param isHql	：是不是hql语句，针对后面的分类有基于hql和sql的查询
	 * @return 返回统计查询的数量
	 */
	private String getCountHql(String hql,boolean isHql){
		String e =hql.substring(hql.indexOf("from")); //String hql ="select .....from User u inner join fetch u.address "
		String c ="select count(*) "+e;
		if(isHql){
			c=c.replaceAll("fetch", "");
		}
		return c;
	}
	/**
	 * 实现分页查找的方法 
	 * @param hql
	 * @param args
	 * @param alias
	 * @return
	 */
	public Pager<T> find(String hql,Object[] args,Map<String,Object> alias){
		hql=initSort(hql);
		//获取查选总记录数的hql语句
		String cq=getCountHql(hql, true);
		//创建查选总记录数的query对象
		Query cquery =getSession().createQuery(cq);
		Query query=getSession().createQuery(hql);
		//设置别名参数
		setAliasParameter(query, alias);
		setAliasParameter(cquery, alias);
		//设置参数
		setParameter(query, args);
		setParameter(cquery, args);
		Pager<T> pages=new Pager<T>();
		setPagers(query, pages);
		@SuppressWarnings("unchecked")
		List<T> datas=query.list();
		pages.setDatas(datas);
		Long total=(Long)cquery.uniqueResult();
		pages.setTotal(total);
		return pages;
	}
	
	public Pager<T> findByAlias(String hql,Map<String,Object> alias){
		return this.find(hql, null, alias);
	}
	public Pager<T> findByParameter(String hql,Object[] args){
		return this.find(hql, args, null);
	}
	public Pager<T> find(String hql,Object arg){
		return this.findByParameter(hql, new Object[]{arg});
	}
	public Pager<T> find(String hql){
		return this.findByAlias(hql, null);
	}
	
	
	/**
	 * 查询一个对象
	 */
	public Object queryObject(String hql,Object[] args,Map<String,Object> alias){
		Query query=getSession().createQuery(hql);
		setAliasParameter(query, alias);
		setParameter(query, args);
		return query.uniqueResult();
	}
	
	public Object queryObjectByAlias(String hql,Map<String,Object> alias){
		return this.queryObject(hql, null, alias);
	}
	public Object queryObjectByParameter(String hql,Object[] args){
		return this.queryObject(hql, args, null);
	}
	public Object queryObject(String hql,Object arg){
		return this.queryObject(hql, new Object[]{arg});
	}
	public Object queryObject(String hql){
		return this.queryObject(hql,null);
	}
	
	/**
	 * 更新语句：在真实的项目开发中不会涉及到真正的删除数据，都是通过在表中一些特殊字段，标识数据删除，
	 * 所以只需更新的方法就行
	 */
	public void updateByHql(String hql,Object[] args){
		Query query =getSession().createQuery(hql);
		setParameter(query, args);
		query.executeUpdate();
	}
	public void updateByHql(String hql,Object obj){
		this.updateByHql(hql, new Object[] {obj});
	}
	public void updateByHql(String hql){
		this.updateByHql(hql, null);
	}
	
	
	
	
	
	
	

}
