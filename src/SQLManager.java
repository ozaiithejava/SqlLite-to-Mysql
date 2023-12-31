import java.sql.*;

public class SqlManager {

    private String sqliteDbPath;
    private String mysqlDbUrl;
    private String mysqlUsername;
    private String mysqlPassword;
    private String tableName;

    public void setSqliteDbPath(String sqliteDbPath) {
        this.sqliteDbPath = sqliteDbPath;
    }

    public void setMysqlDbUrl(String mysqlDbUrl) {
        this.mysqlDbUrl = mysqlDbUrl;
    }

    public void setMysqlUsername(String mysqlUsername) {
        this.mysqlUsername = mysqlUsername;
    }

    public void setMysqlPassword(String mysqlPassword) {
        this.mysqlPassword = mysqlPassword;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void convert() {
        try {
            // SQLite veritabanına bağlan
            Connection sqliteConnection = DriverManager.getConnection("jdbc:sqlite:" + sqliteDbPath);

            // MySQL veritabanına bağlan
            Connection mysqlConnection = DriverManager.getConnection(mysqlDbUrl, mysqlUsername, mysqlPassword);

            // SQLite tablosundaki sütun bilgilerini al
            DatabaseMetaData metaData = sqliteConnection.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, tableName, null);
            
            // MySQL tablosunu oluştur
            createMySQLTable(mysqlConnection, resultSet);

            // SQLite tablosundaki verileri seç
            String selectQuery = "SELECT * FROM " + tableName;
            try (PreparedStatement selectStatement = sqliteConnection.prepareStatement(selectQuery);
                 ResultSet dataResultSet = selectStatement.executeQuery()) {

                // MySQL tablosuna verileri ekleyen sorgu
                String insertQuery = "INSERT INTO " + tableName + " VALUES (";
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    insertQuery += "?,";
                }
                insertQuery = insertQuery.substring(0, insertQuery.length() - 1); // Son virgülü kaldır
                insertQuery += ")";
                
                try (PreparedStatement insertStatement = mysqlConnection.prepareStatement(insertQuery)) {

                    // SQLite'den veri çek ve MySQL'e ekle
                    while (dataResultSet.next()) {
                        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                            insertStatement.setObject(i, dataResultSet.getObject(i));
                        }
                        // MySQL'e ekle
                        insertStatement.executeUpdate();
                    }

                    System.out.println("Veri transferi tamamlandı.");
                }
            }

            // Bağlantıları kapat
            sqliteConnection.close();
            mysqlConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createMySQLTable(Connection mysqlConnection, ResultSet resultSet) throws SQLException {
        StringBuilder createTableQuery = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (");
        
        while (resultSet.next()) {
            String columnName = resultSet.getString("COLUMN_NAME");
            String typeName = resultSet.getString("TYPE_NAME");
            createTableQuery.append(columnName).append(" ").append(typeName).append(",");
        }
        
        createTableQuery.setLength(createTableQuery.length() - 1); // Son virgülü kaldır
        createTableQuery.append(")");

        try (Statement createTableStatement = mysqlConnection.createStatement()) {
            createTableStatement.executeUpdate(createTableQuery.toString());
        }
    }
}
