/**
 * 
 */
package com.moriset.bcephal.service;

import com.moriset.bcephal.domain.universe.UniverseParameters;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Slf4j
public class UniverseWriter extends DbTableWriter {

	public UniverseWriter(EntityManager session) {
		super(session);
	}

	public void importFromCsvFile(String columns, String columnIndexes, String filePath) throws Exception {
		super.importFromCsvFile(columns, columnIndexes, filePath, UniverseParameters.SCHEMA_NAME,
				UniverseParameters.UNIVERSE_TABLE_NAME);
	}

	public void enableTriggers(boolean enable) {
		EntityTransaction tx = this.getSession().getTransaction();
		boolean commit = false;
		try {
			if (!tx.isActive()) {
				tx.begin();
				commit = enable;
			}
			String sql = enable ? "ALTER TABLE misp.misp_universe ENABLE TRIGGER report_publication"
					: "ALTER TABLE misp.misp_universe DISABLE TRIGGER report_publication";
			Query query = this.getSession().createNativeQuery(sql);
			query.executeUpdate();
			if (enable) {
				query = this.getSession().createNativeQuery("UPDATE misp.misp_universe set isready = isready");
				query.executeUpdate();
			}
			if (commit) {
				tx.commit();
			}
		} catch (Exception ex) {
			String error = "Unable to enable triggers";
			log.error(error, ex);
		} finally {
			if (commit && tx != null && tx.isActive()) {
				tx.rollback();
			}
		}
	}

}
