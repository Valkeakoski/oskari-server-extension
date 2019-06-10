package flyway.valkeakoski;

import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.map.view.AppSetupServiceMybatisImpl;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.util.FlywayHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class V1_0_8__add_track_location_and_search_from_channels_to_user_saved_views implements JdbcMigration {
    private static final  String SEARCH_FROM_CHANNELS = "search-from-channels";
    private static final String TRACK_LOCATION = "track-location";
    private static final ViewService APP_SETUP_SERVICE = new AppSetupServiceMybatisImpl();
    private static final String BUNDLE_MAPFULL = "mapfull";
    private static final String MY_LOCATION_PLUGIN = "Oskari.mapframework.bundle.mapmodule.plugin.MyLocationPlugin";

    public void migrate(Connection connection) throws Exception {
        List<View> views = getUserViews(connection);

        // Add bundles to views
        for (View v : views) {
            // Adds search-from-channel to user views
            if (!FlywayHelper.viewContainsBundle(connection, SEARCH_FROM_CHANNELS, v.getId())) {
                FlywayHelper.addBundleWithDefaults(connection, v.getId(), SEARCH_FROM_CHANNELS);
                changeConfig(connection, v.getId());
            }
            // Adds track-location to user views
            if (!FlywayHelper.viewContainsBundle(connection, TRACK_LOCATION, v.getId())) {
                FlywayHelper.addBundleWithDefaults(connection, v.getId(), TRACK_LOCATION);
            }

            // Change mylocationplugin conf to user views
            View view = APP_SETUP_SERVICE.getViewWithConf(v.getId());
            final Bundle mapfull = v.getBundleByName(BUNDLE_MAPFULL);
            final JSONObject config = mapfull.getConfigJSON();
            final JSONArray plugins = config.optJSONArray("plugins");
            for (int i = 0; i < plugins.length(); ++i) {
                final JSONObject plugin = plugins.getJSONObject(i);
                if (MY_LOCATION_PLUGIN.equals(plugin.optString("id"))) {
                    final JSONObject pluginConfig = plugin.optJSONObject("config");
                    if (pluginConfig != null) {
                        pluginConfig.remove("zoom");
                        pluginConfig.put("zoom", 10);
                        break;
                    } else {
                        JSONObject configJSON = new JSONObject();
                        configJSON.put("zoom", 10);
                        plugin.put("config", configJSON);
                    }
                }
            }
            APP_SETUP_SERVICE.updateBundleSettingsForView(v.getId(), mapfull);


        }
    }

    private void changeConfig(Connection conn, long viewId) throws SQLException {
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


    private List<View> getUserViews(Connection connection) throws SQLException {
        final String sql = "SELECT SELECT id FROM portti_view WHERE type='USER'";
        List<View> views = new ArrayList<>();
        try (final PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while(rs.next()) {
                View view = new View();
                view.setId(rs.getLong("id"));
                views.add(view);
            }
        }
        return views;
    }
}
