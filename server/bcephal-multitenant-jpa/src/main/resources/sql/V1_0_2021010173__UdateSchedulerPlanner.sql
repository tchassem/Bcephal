UPDATE BCP_SCHEDULER_PLANNER_ITEM set gridType = 'REPORT_GRID' WHERE gridType = '0';
UPDATE BCP_SCHEDULER_PLANNER_ITEM set gridType = 'GRID' WHERE gridType = '1';
UPDATE BCP_SCHEDULER_PLANNER_ITEM set gridType = 'JOIN' WHERE gridType = '2';
UPDATE BCP_SCHEDULER_PLANNER_ITEM set gridType = 'MATERIALIZED_GRID' WHERE gridType = '2';