package flyway.valkeakoski;

import fi.nls.oskari.db.BundleHelper;
import fi.nls.oskari.domain.map.view.Bundle;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;
import java.sql.SQLException;

public class V1_0_6__register_track_location_bundle implements JdbcMigration {
    private static final String BUNDLE_ID = "track-location";

    public void migrate(Connection connection)  throws SQLException {
        // BundleHelper checks if these bundles are already registered
        Bundle bundle = new Bundle();
        bundle.setName(BUNDLE_ID);
        BundleHelper.registerBundle(bundle, connection);
    }
}