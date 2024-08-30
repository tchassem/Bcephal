
DELETE FROM BCP_SEC_CLIENT_FUNCTIONALITY WHERE code = 'sourcing.link';
DELETE FROM BCP_SEC_RIGHT WHERE functionality = 'sourcing.link';

DELETE FROM BCP_SEC_CLIENT_FUNCTIONALITY WHERE code = 'transformation.tree';
DELETE FROM BCP_SEC_RIGHT WHERE functionality = 'transformation.tree';

DELETE FROM BCP_SEC_CLIENT_FUNCTIONALITY WHERE code = 'transformation.tree.scheduler';
DELETE FROM BCP_SEC_RIGHT WHERE functionality = 'transformation.tree.scheduler';

DELETE FROM BCP_SEC_CLIENT_FUNCTIONALITY WHERE code = 'transformation.tree.scheduler.log';
DELETE FROM BCP_SEC_RIGHT WHERE functionality = 'transformation.tree.scheduler.log';

DELETE FROM BCP_SEC_CLIENT_FUNCTIONALITY WHERE code = 'reporting.report.spreadsheet';
DELETE FROM BCP_SEC_RIGHT WHERE functionality = 'reporting.report.spreadsheet';

DELETE FROM BCP_SEC_CLIENT_FUNCTIONALITY WHERE code = 'reconciliation.auto.reco.scheduler.log';
DELETE FROM BCP_SEC_RIGHT WHERE functionality = 'reconciliation.auto.reco.scheduler.log';

DELETE FROM BCP_SEC_CLIENT_FUNCTIONALITY WHERE code = 'dynamic.form';
DELETE FROM BCP_SEC_RIGHT WHERE functionality = 'dynamic.form';

DELETE FROM BCP_SEC_CLIENT_FUNCTIONALITY WHERE code = 'reporting.report.join.grid.scheduler';
DELETE FROM BCP_SEC_RIGHT WHERE functionality = 'reporting.report.join.grid.scheduler';

DELETE FROM BCP_SEC_CLIENT_FUNCTIONALITY WHERE code = 'reporting.report.join.grid.scheduler.log';
DELETE FROM BCP_SEC_RIGHT WHERE functionality = 'reporting.report.join.grid.scheduler.log';
