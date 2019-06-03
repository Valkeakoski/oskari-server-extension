package flyway.valkeakoski;

import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.map.view.AppSetupServiceMybatisImpl;
import fi.nls.oskari.map.view.ViewService;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class V1_0_5__change_search_bundle_config implements JdbcMigration {
    private static final ViewService VIEW_SERVICE = new AppSetupServiceMybatisImpl();


    public void migrate(Connection connection) throws Exception {
        List<View> views = VIEW_SERVICE.getViewsForUser(-1);
        for (View v : views) {
            if (v.isDefault()) {
                changeConfig(connection, v.getId());
            }
        }
    }

    public void changeConfig(Connection conn, long viewId) throws SQLException {
        final String sql = "UPDATE portti_view_bundle_seq " +
                "SET " +
                "    config='{\"autocomplete\": false, \"disableDefault\": true}' " +
                "WHERE bundle_id IN (SELECT id FROM portti_bundle WHERE name='search') AND view_id=?";

        try (PreparedStatement statement =
                     conn.prepareStatement(sql)){
            statement.setLong(1, viewId);
            statement.execute();
        }
    }
}

