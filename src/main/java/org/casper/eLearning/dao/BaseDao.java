package org.casper.eLearning.dao;

import java.lang.reflect.ParameterizedType;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class BaseDao<T> implements IBaseDao<T> {

	//通过依赖注入获得SessionFactory
	@Inject
	private SessionFactory sessionFactory;
	
	//使用了inject注解过后可以省去getter 和setter方法
/*	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	*/
	/**
	 * 获取当前的Session
	 * @return
	 */
	protected Session getSession(){
		return sessionFactory.getCurrentSession();
	}
	
	/**
	 * 定义了一个class，获取泛型的class；
	 */
	private Class<?> clz;
	public Class<?> getClz() {
		if(clz==null) {
			//获取泛型的Class对象
			clz = ((Class<?>)
					(((ParameterizedType)(this.getClass().getGenericSuperclass())).getActualTypeArguments()[0]));
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
	 *更新一个对象 
	 */
	@Override
	public void update(T t) {
		getSession().update(t);
	}
	
	/**
	 *根据id删除一个对象
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
		return (T) getSession().load(getClz(), id) ;
	}
	

}
