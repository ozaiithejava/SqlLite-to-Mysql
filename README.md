# SqlLite-to-Mysql
basic convanter


## Usage:
```java
public static void main(String[] args) {
        // Kullanım örneği
        SqlManager sqlManager = new SqlManager();
        sqlManager.setSqliteDbPath("sqlite-database.db");
        sqlManager.setMysqlDbUrl("jdbc:mysql://localhost:3306/mysql-database");
        sqlManager.setMysqlUsername("your_mysql_username");
        sqlManager.setMysqlPassword("your_mysql_password");
        sqlManager.setTableName("your_table_name");

        // Veri transferini başlat
        sqlManager.convert();
    }
```
