# Existing database inspection

The Java application connects to `apna_academy` on local MySQL port 3306. A structure-only export is saved as `apna_academy_schema.sql`; it has no records and excludes DROP statements. The verified schema has 15 tables with the columns and relationships in that file. No schema or record changes were made for this migration.

Certificate verification reads from the separate `certificate_system.certificates` table. The app performs a read-only lookup by `certificate_id`; the MySQL account needs `SELECT` permission on that schema. Certificate data is not copied into `apna_academy`.

## Run the read-only inspection

Make sure MySQL Workbench is connected to `apna_academy`. Open `inspect_database.sql`, select `apna_academy` as the default schema, and run the script. It reads table, column, key, index, trigger, and routine metadata; it does not change data or schema.

Alternatively, from a terminal in the project directory, run:

```powershell
mysql --host=localhost --port=3306 --user=root --database=apna_academy --table < database/inspect_database.sql
```

If MySQL prompts for a password, use the password configured for that local server. Do not put a real password into this repository.

## Export schema for migration

To export table definitions without application records, use MySQL Workbench's Server > Data Export, select `apna_academy`, choose **Dump Structure Only**, and export the schema. Alternatively, from a terminal:

```powershell
mysqldump --host=localhost --port=3306 --user=root --no-data --no-create-db --skip-add-drop-table --routines --triggers apna_academy > database/apna_academy_schema.sql
```

The PHP source/assets are still needed to copy the original pages and behavior. The structure export is for reference only; do not apply it as a migration.

Latest read-only counts for public content: 5 courses, 4 faculty, 4 events, 1 placement, 1 job, 7 notes, and 0 certificates. No records were added by the app. The schema has no separate `admissions` table; admissions are represented in `students`, and test questions are in `test_questions`.
