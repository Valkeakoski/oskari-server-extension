package flyway.valkeakoski;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import fi.nls.oskari.log.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;

public class V1_0_3__change_view_servlet_prefix_to_full_map implements JdbcMigration {
    private static final Logger LOG = LogFactory.getLogger(V1_0_3__change_view_servlet_prefix_to_full_map.class);



    public void migrate(Connection connection) throws Exception {

        try {
            fixViewPrefix(connection);
        }
        finally {
            LOG.info("Updated views");

        }
    }

    public void fixViewPrefix(Connection conn) throws SQLException {
        final String sql = "UPDATE portti_view " +
                "SET " +
                "    application='full-map' " +
                "WHERE application = 'servlet'";

        try (PreparedStatement statement =
                     conn.prepareStatement(sql)){
            statement.execute();
        }
    }
}