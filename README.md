# SmartShelf Backend (Terminal Java + MySQL)

Terminal-based inventory, purchasing, sales, supplier, notification, and analytics backend for SmartShelf.

## Prerequisites
- Java JDK (tested with JDK 26)
- MySQL Server 8.0 running on `localhost:3306` with database `smartshelf` initialized from `src/Database/Smartshelf.sql`
- MySQL Connector/J JAR in `lib/mysql-connector-j-26.10.0.jar`

## Database Configuration
Defaults in `src/Database/DBConnection.java`:
- URL: `jdbc:mysql://localhost:3306/smartshelf`
- User: `root`
- Password: `root`

Override optionally with environment variables:
- `SMARTSHELF_DB_URL`
- `SMARTSHELF_DB_USER`
- `SMARTSHELF_DB_PASSWORD`

## Build & Run
```powershell
.\build.ps1
.\run.ps1
```

On first launch with an empty `users` table, the terminal app guides you to create the initial `ADMIN` account before logging in.
