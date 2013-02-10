package org.springframework.batch.item.database;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.hsqldb.Types;
import org.junit.Test;
import org.junit.runners.JUnit4;
import org.junit.runner.RunWith;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@RunWith(JUnit4.class)
public class StoredprocedureItemReaderConfigTests {

	/*
	 * Should fail if trying to call getConnection() twice
	 */
	@Test
	public void testUsesCurrentTransaction() throws Exception {
		DataSource ds = mock(DataSource.class);
		DatabaseMetaData dmd = mock(DatabaseMetaData.class);
		when(dmd.getDatabaseProductName()).thenReturn("Oracle");
		Connection con = mock(Connection.class);
		when(con.getMetaData()).thenReturn(dmd);
		when(con.getMetaData()).thenReturn(dmd);
		when(con.getAutoCommit()).thenReturn(false);
		CallableStatement cs = mock(CallableStatement.class);
		when(con.prepareCall("{call foo_bar()}", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
				ResultSet.HOLD_CURSORS_OVER_COMMIT)).thenReturn(cs);
		when(ds.getConnection()).thenReturn(con);
		when(ds.getConnection()).thenReturn(con);
		con.commit();
		PlatformTransactionManager tm = new DataSourceTransactionManager(ds);
		TransactionTemplate tt = new TransactionTemplate(tm);
		final StoredProcedureItemReader<String> reader = new StoredProcedureItemReader<String>();
		reader.setDataSource(new ExtendedConnectionDataSourceProxy(ds));
		reader.setUseSharedExtendedConnection(true);
		reader.setProcedureName("foo_bar");
		final ExecutionContext ec = new ExecutionContext();
		tt.execute(
				new TransactionCallback() {
                    @Override
					public Object doInTransaction(TransactionStatus status) {
						reader.open(ec);
						reader.close();
						return null;
					}
				});
	}
	
	/*
	 * Should fail if trying to call getConnection() twice
	 */
	@Test
	public void testUsesItsOwnTransaction() throws Exception {
		
		DataSource ds = mock(DataSource.class);
		DatabaseMetaData dmd = mock(DatabaseMetaData.class);
		when(dmd.getDatabaseProductName()).thenReturn("Oracle");
		Connection con = mock(Connection.class);
		when(con.getMetaData()).thenReturn(dmd);
		when(con.getMetaData()).thenReturn(dmd);
		when(con.getAutoCommit()).thenReturn(false);
		CallableStatement cs = mock(CallableStatement.class);
		when(con.prepareCall("{call foo_bar()}", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)).thenReturn(cs);
		when(ds.getConnection()).thenReturn(con);
		when(ds.getConnection()).thenReturn(con);
		con.commit();
		PlatformTransactionManager tm = new DataSourceTransactionManager(ds);
		TransactionTemplate tt = new TransactionTemplate(tm);
		final StoredProcedureItemReader<String> reader = new StoredProcedureItemReader<String>();
		reader.setDataSource(ds);
		reader.setProcedureName("foo_bar");
		final ExecutionContext ec = new ExecutionContext();
		tt.execute(
				new TransactionCallback() {
                    @Override
					public Object doInTransaction(TransactionStatus status) {
						reader.open(ec);
						reader.close();
						return null;
					}
				});
	}

	/*
	 * Should fail if trying to call getConnection() twice
	 */
	@Test
	public void testHandlesRefCursorPosition() throws Exception {
		
		DataSource ds = mock(DataSource.class);
		DatabaseMetaData dmd = mock(DatabaseMetaData.class);
		when(dmd.getDatabaseProductName()).thenReturn("Oracle");
		Connection con = mock(Connection.class);
		when(con.getMetaData()).thenReturn(dmd);
		when(con.getMetaData()).thenReturn(dmd);
		when(con.getAutoCommit()).thenReturn(false);
		CallableStatement cs = mock(CallableStatement.class);
		when(con.prepareCall("{call foo_bar(?, ?)}", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)).thenReturn(cs);
		when(ds.getConnection()).thenReturn(con);
		when(ds.getConnection()).thenReturn(con);
		con.commit();
		PlatformTransactionManager tm = new DataSourceTransactionManager(ds);
		TransactionTemplate tt = new TransactionTemplate(tm);
		final StoredProcedureItemReader<String> reader = new StoredProcedureItemReader<String>();
		reader.setDataSource(ds);
		reader.setProcedureName("foo_bar");
		reader.setParameters(new SqlParameter[] {
				new SqlParameter("foo", Types.VARCHAR),
				new SqlParameter("bar", Types.OTHER)});
		reader.setPreparedStatementSetter(
				new PreparedStatementSetter() {
                    @Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
					}
				});
		reader.setRefCursorPosition(3);
		final ExecutionContext ec = new ExecutionContext();
		tt.execute(
				new TransactionCallback() {
                    @Override
					public Object doInTransaction(TransactionStatus status) {
						reader.open(ec);
						reader.close();
						return null;
					}
				});
	}
}
