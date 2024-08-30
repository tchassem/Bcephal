ALTER TABLE BCP_JOIN_COLUMN ADD COLUMN IF NOT EXISTS variableName TEXT;

ALTER TABLE BCP_ARCHIVE_CONFIGURATION_ENRICHMENT_ITEM ADD COLUMN IF NOT EXISTS variableName TEXT;
ALTER TABLE BCP_BILLING_MODEL ADD COLUMN IF NOT EXISTS dueDateVariableName TEXT;
ALTER TABLE BCP_BILLING_MODEL ADD COLUMN IF NOT EXISTS invoiceVariableName TEXT;
ALTER TABLE BCP_BILLING_MODEL ADD COLUMN IF NOT EXISTS fromVariableName TEXT;
ALTER TABLE BCP_BILLING_MODEL ADD COLUMN IF NOT EXISTS toVariableName TEXT;
ALTER TABLE BCP_BILLING_MODEL_ENRICHMENT_ITEM ADD COLUMN IF NOT EXISTS variableName TEXT;
ALTER TABLE BCP_DASHBOARD_ITEM_VARIABLE_REFERENCE_CONDITION ADD COLUMN IF NOT EXISTS variableName TEXT;
ALTER TABLE BCP_DASHBOARD_REPORT_FIELD_PROPERTIES ADD COLUMN IF NOT EXISTS fromDateVariableName TEXT;
ALTER TABLE BCP_DASHBOARD_REPORT_FIELD_PROPERTIES ADD COLUMN IF NOT EXISTS toDateVariableName TEXT;
ALTER TABLE BCP_UNIVERSE_DYNAMIC_FILTER_ITEM ADD COLUMN IF NOT EXISTS startDateVariableName TEXT;
ALTER TABLE BCP_UNIVERSE_DYNAMIC_FILTER_ITEM ADD COLUMN IF NOT EXISTS endDateVariableName TEXT;
ALTER TABLE BCP_JOIN_COLUMN_FIELD ADD COLUMN IF NOT EXISTS variableName TEXT;
ALTER TABLE BCP_JOIN_CONDITION_ITEM ADD COLUMN IF NOT EXISTS dateValue_variableName TEXT;
ALTER TABLE BCP_UNION_GRID_CONDITION_ITEM ADD COLUMN IF NOT EXISTS dateValue_variableName TEXT;
ALTER TABLE BCP_VARIABLE_INTERVAL ADD COLUMN IF NOT EXISTS variableName TEXT;
ALTER TABLE BCP_VARIABLE_REFERENCE_CONDITION ADD COLUMN IF NOT EXISTS periodValueVariableName TEXT;
ALTER TABLE BCP_FORM_MODEL_BUTTON_ACTION ADD COLUMN IF NOT EXISTS variableName TEXT;
ALTER TABLE BCP_FORM_MODEL_FIELD ADD COLUMN IF NOT EXISTS variableName TEXT;
ALTER TABLE BCP_FORM_MODEL_FIELD_REFERENCE_CONDITION ADD COLUMN IF NOT EXISTS variableName TEXT;
ALTER TABLE BCP_FORM_MODEL_FIELD_VALIDATION_ITEM ADD COLUMN IF NOT EXISTS variableName TEXT;
ALTER TABLE BCP_FORM_MODEL_SPOT_ITEM ADD COLUMN IF NOT EXISTS variableName TEXT;
ALTER TABLE BCP_TRANSFORMATION_ROUTINE_FIELD ADD COLUMN IF NOT EXISTS variableName TEXT;
ALTER TABLE BCP_TRANSFORMATION_ROUTINE_MAPPING_CONDITION ADD COLUMN IF NOT EXISTS variableName TEXT;
ALTER TABLE BCP_TRANSFORMATION_ROUTINE_SPOT_CONDITION ADD COLUMN IF NOT EXISTS variableName TEXT;
ALTER TABLE BCP_AUTO_RECO_COMMON_DIMENSION ADD COLUMN IF NOT EXISTS variableName TEXT;
ALTER TABLE BCP_RECONCILIATION_JOIN_MODEL_ENRICHMENT ADD COLUMN IF NOT EXISTS variableName TEXT;
ALTER TABLE BCP_RECONCILIATION_MODEL_ENRICHMENT ADD COLUMN IF NOT EXISTS variableName TEXT;
ALTER TABLE BCP_WRITE_OFF_FIELD ADD COLUMN IF NOT EXISTS variableName TEXT;
ALTER TABLE BCP_WRITE_OFF_FIELD_VALUE ADD COLUMN IF NOT EXISTS variableName TEXT;
ALTER TABLE BCP_WRITE_OFF_JOIN_FIELD ADD COLUMN IF NOT EXISTS variableName TEXT;
ALTER TABLE BCP_WRITE_OFF_JOIN_FIELD_VALUE ADD COLUMN IF NOT EXISTS variableName TEXT;
ALTER TABLE BCP_TASK ADD COLUMN IF NOT EXISTS variableName TEXT;