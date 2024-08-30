ALTER TABLE bcp_email_filter ADD COLUMN IF NOT EXISTS filterVerb varchar(10);
ALTER TABLE bcp_email_filter ADD COLUMN IF NOT EXISTS openBrackets varchar(20);
ALTER TABLE bcp_email_filter ADD COLUMN IF NOT EXISTS closeBrackets varchar(20);
ALTER TABLE bcp_email_filter ADD COLUMN IF NOT EXISTS position INTEGER;
ALTER TABLE bcp_email_filter ADD COLUMN IF NOT EXISTS comparator varchar(20);
ALTER TABLE bcp_email_filter ADD COLUMN IF NOT EXISTS sign varchar(5);
ALTER TABLE bcp_email_filter ADD COLUMN IF NOT EXISTS number INTEGER;

ALTER TABLE bcp_email_filter DROP COLUMN IF EXISTS expeditor;
ALTER TABLE bcp_email_filter DROP COLUMN IF EXISTS subject;
ALTER TABLE bcp_email_filter DROP COLUMN IF EXISTS subjectOperator;
ALTER TABLE bcp_email_filter DROP COLUMN IF EXISTS attachmentOperator;
DO $$
BEGIN
  IF EXISTS(SELECT *
    FROM information_schema.columns
    WHERE table_name='bcp_email_filter' and column_name='attachment')
  THEN
      ALTER TABLE "public"."bcp_email_filter" RENAME COLUMN "attachment" TO "attributeValue";
  END IF;
  IF EXISTS(SELECT *
    FROM information_schema.columns
    WHERE table_name='bcp_email_filter' and column_name='expeditorOperator')
  THEN
      ALTER TABLE "public"."bcp_email_filter" RENAME COLUMN "expeditorOperator" TO "attributeOperator";
  END IF;
END $$;

ALTER TABLE BCP_FILE_LOADER ADD COLUMN IF NOT EXISTS confirmAction BOOLEAN DEFAULT FALSE;
ALTER TABLE BCP_JOIN ADD COLUMN IF NOT EXISTS confirmAction BOOLEAN DEFAULT FALSE;

    