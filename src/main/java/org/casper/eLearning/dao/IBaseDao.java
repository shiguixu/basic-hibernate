package org.casper.eLearning.dao;

public interface IBaseDao<T> {

	/**
	 * 增加一个对象
	 * @param t
	 */
	public T add(T t);
	
	/**
	 * 更新一个对象
	 * @param t
	 * @return
	 */
	public void update(T t);
	
	/**
	 * 根据id删除一个对象
	 * @param id
	 */
	public void delete(Integer id);
	
	/**
	 * 根据id查询对象
	 * @param id
	 * @return
	 */
	public T select(Integer id);
}
