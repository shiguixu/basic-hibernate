package org.casper.eLearning.model;

import java.util.List;

/**
 * 分页对象：用于传递分页的参数
 * @author mxp
 */

public class Pager<T> {
	/**
	 * 分页的大小
	 */
	private Integer pageSize;
	/**
	 * 分页的起始位置
	 */
	private Integer pageOffset;
	/**
	 * 总记录数
	 */
	private Long total;
	/**
	 * 分页数据
	 */
	private List<T> datas;
	
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getPageOffset() {
		return pageOffset;
	}
	public void setPageOffset(Integer pageOffset) {
		this.pageOffset = pageOffset;
	}
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
	public List<T> getDatas() {
		return datas;
	}
	public void setDatas(List<T> datas) {
		this.datas = datas;
	}
	
}