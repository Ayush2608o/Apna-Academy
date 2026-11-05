# Existing database inspection

The Java application connects to the configured MySQL database using `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`. A structure-only export is saved as `apna_academy_schema.sql`; it has no records and excludes DROP statements. The verified schema has 15 tables with the columns and relationships in that file.

Existing certificates are stored in the separate `certificate_system.certificates` table. Its identifier column is `certificate_id`, and student/course display names are stored directly in that table. Set `CERTIFICATE_SCHEMA` to use a different schema. Certificates issued by the academy admin are stored in `apna_academy.certificates` and use `certificate_number`; verification checks both sources. Certificate QR codes link to the public verification page.

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

The checked-in export is schema-only and contains no application records. The schema has no separate `admissions` table; admissions are represented in `students`, and test questions are in `test_questions`.
