package org.casper.eLearning.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import junit.framework.Assert;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class AbstractDbUnitTestCase {
	//DBUnit的数据库连接对象
	public static IDatabaseConnection dbunitCon;
	//临时文件用于保存数据库的内容
	private File tempFile;

	/**
	 * 在AbstractDbunitTestCase加载之前初始化Dbunit的数据库的连接对象
	 * 
	 * @throws DatabaseUnitException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	@BeforeClass
	public static void init() throws DatabaseUnitException, SQLException,
			ClassNotFoundException {
		dbunitCon = new DatabaseConnection(DbUtil.getConnection());
	}

	/**
	 * 根据配置文件的名称创建DateSet
	 * 
	 * @param tname配置文件的名称
	 * @return
	 * @throws DataSetException
	 * @throws IOException
	 */
	protected IDataSet createDateSet(String tableName) throws DataSetException,
			IOException {
		InputStream is = AbstractDbUnitTestCase.class.getClassLoader()
				.getResourceAsStream(tableName + ".xml");
		Assert.assertNotNull("dbunit的基本数据文件不存在", is);
		return new FlatXmlDataSet(is);
	}
	
	/**
	 * 备份文件
	 * @param ds
	 * @throws IOException
	 * @throws DataSetException
	 */
	private void writeBackupFile(IDataSet ds) throws IOException,
		DataSetException {
			tempFile = File.createTempFile("back", "xml");
			FlatXmlDataSet.write(ds, new FileWriter(tempFile));
	}
	
	/**
	 * 备份所有的数据库文件
	 * @throws SQLException
	 * @throws IOException
	 * @throws DataSetException
	 */
	protected void backupAllTable() throws SQLException, IOException,
			DataSetException {
		IDataSet ds = dbunitCon.createDataSet();
		writeBackupFile(ds);
	}
	
	/**
	 * 自定义备份文件
	 * @param tname
	 * @throws DataSetException
	 * @throws IOException
	 * @throws SQLException
	 */
	protected void backupCustomTable(String[] tname) throws DataSetException,
			IOException, SQLException {
		QueryDataSet ds = new QueryDataSet(dbunitCon);
		for (String str : tname) {
			ds.addTable(str);
		}
		writeBackupFile(ds);
	}
	
	/**
	 * 备份一张表
	 * @param tname
	 * @throws DataSetException
	 * @throws IOException
	 * @throws SQLException
	 */
	protected void bakcupOneTable(String tname) throws DataSetException,
			IOException, SQLException {
		backupCustomTable(new String[] { tname });
	}
	
	/**
	 * 恢复表的数据
	 * @throws DatabaseUnitException
	 * @throws SQLException
	 * @throws IOException
	 */
	protected void resumesTable() throws DatabaseUnitException, SQLException,
			IOException {
		IDataSet ds = new FlatXmlDataSet(tempFile);
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
	}
	
	/**
	 * 关闭DBunit的数据库的连接
	 */
	@AfterClass
	public static void destory() {
		try {
			if (dbunitCon != null)
				dbunitCon.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
