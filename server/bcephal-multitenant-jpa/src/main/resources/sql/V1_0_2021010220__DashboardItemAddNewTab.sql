ALTER TABLE BCP_DASHBOARD_ITEM ADD COLUMN IF NOT EXISTS newTab BOOLEAN DEFAULT FALSE;
ALTER TABLE BCP_TRANSFORMATION_ROUTINE ADD COLUMN IF NOT EXISTS confirmAction BOOLEAN DEFAULT FALSE;
ALTER TABLE BCP_SCHEDULER_PLANNER ADD COLUMN IF NOT EXISTS confirmAction BOOLEAN DEFAULT FALSE;







