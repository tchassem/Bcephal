
ALTER TABLE BCP_BILLING_INVOICE_ITEM ADD COLUMN IF NOT EXISTS driverDecimalNumber INTEGER;
ALTER TABLE BCP_BILLING_INVOICE_ITEM ADD COLUMN IF NOT EXISTS unitCostDecimalNumber INTEGER;
ALTER TABLE BCP_BILLING_INVOICE_ITEM ADD COLUMN IF NOT EXISTS billingAmountDecimalNumber INTEGER;

ALTER TABLE BCP_CALCULATE_BILLING_ITEM ADD COLUMN IF NOT EXISTS driverDecimalNumber INTEGER;
ALTER TABLE BCP_CALCULATE_BILLING_ITEM ADD COLUMN IF NOT EXISTS unitCostDecimalNumber INTEGER;
ALTER TABLE BCP_CALCULATE_BILLING_ITEM ADD COLUMN IF NOT EXISTS billingAmountDecimalNumber INTEGER;