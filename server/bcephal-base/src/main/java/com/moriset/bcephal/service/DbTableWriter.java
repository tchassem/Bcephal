/**
 * 
 */
package com.moriset.bcephal.service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;

import jakarta.persistence.EntityManager;
import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class DbTableWriter {

	private EntityManager session;

	public DbTableWriter(EntityManager session) {
		this.session = session;
	}

	protected void importFromCsvFile(String columns, String columnIndexes, String filePath, String scheme, String table)
			throws Exception {
		synchronized (this) {
			try {
				Session ses = (Session) this.session.getDelegate();
				ses.doReturningWork(new ReturningWork<Void>() {
					@Override
					public Void execute(Connection connection) throws SQLException {
						CopyManager cp = connection.unwrap(PGConnection.class).getCopyAPI();
						try {
							String input = loadCsvFromFile(filePath);
							StringReader reader = new StringReader(input);
							cp.copyIn("COPY " + scheme.toLowerCase() + table.toLowerCase() + "(" + columns + ")"
									+ " FROM STDIN csv DELIMITER ';' ENCODING 'UTF-8'", reader);
							connection.commit();
						} catch (IOException e) {
							e.printStackTrace();
						}
						return null;
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
	}

	private String loadCsvFromFile(final String fileName) throws IOException {
		try (InputStream is = new FileInputStream(fileName)) {
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) != -1) {
				result.write(buffer, 0, length);
			}
			is.close();
			return result.toString("UTF-8");
		}
	}

}
