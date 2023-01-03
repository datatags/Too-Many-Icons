package me.datatags.toomanyicons;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.User;

import java.io.IOException;

import me.datatags.toomanyicons.actions.ActionManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class TooManyIcons extends JavaPlugin {

    private static TooManyIcons instance;
    private ActionManager actionManager;
    private String broadcasterId;
    private ITwitchClient client = null;
    private boolean failure = false;

    @Override
    public void onEnable() {
        instance = this;
        // Get the latest config after saving the default if missing
        saveDefaultConfig();

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> setupClient());
        getCommand("tmi").setExecutor(new RewardCommand(this));
        actionManager = new ActionManager(this);
    }

    @Override
    public void onDisable() {
        if (client != null) {
            client.close();
        }
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public ITwitchClient getTwitchClient() {
        return this.client;
    }

    public String getBroadcasterId() {
        return broadcasterId;
    }

    private void setupClient() {
        String clientId = getConfig().getString("client_id");
        String clientSecret = getConfig().getString("client_secret");
        String access = getConfig().getString("access_token");
        String refresh = getConfig().getString("refresh_token");
        long expiration = -1;
        boolean valid;
        OkHttpClient http = new OkHttpClient();
        Request validationRequest = new Request.Builder().url("https://id.twitch.tv/oauth2/validate")
                .addHeader("Authorization", "Bearer " + access).build();
        try (Response response = http.newCall(validationRequest).execute()) {
            valid = response.isSuccessful();
            if (valid) {
                JSONObject object = new JSONObject(response.body().string());
                expiration = object.getLong("expires_in");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // If we already failed to contact twitch...
            if (failure) {
                getLogger().severe("Failed again to contact twitch, giving up.");
                return;
            }
            getLogger().warning("Failed to request token status, retrying in 5 minutes...");
            failure = true;
            Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> setupClient(), 5 * 60 * 20);
            return;
        }
        // If it expires in less than 45 minutes, just grab a new one now
        if (!valid || expiration < 2700) {
            getLogger().warning("Access token is expired or expiring soon, requesting a new one...");
            RequestBody body = new FormBody.Builder().add("client_id", clientId).add("client_secret", clientSecret)
                    .add("grant_type", "refresh_token").add("refresh_token", refresh).build();
            Request request = new Request.Builder().url("https://id.twitch.tv/oauth2/token").post(body).build();
            try (Response response = http.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    getLogger().severe("Failed to refresh token: " + response.body().string());
                    // If we already failed to contact twitch...
                    if (failure) {
                        getLogger().severe("Failed again to contact twitch, giving up.");
                        return;
                    }
                    failure = true;
                    Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> setupClient(), 5 * 60 * 20);
                    return;
                }
                JSONObject object = new JSONObject(response.body().string());
                access = object.getString("access_token");
                refresh = object.getString("refresh_token");
                expiration = object.getLong("expires_in");
            } catch (IOException e) {
                e.printStackTrace();
                // If we already failed to contact twitch...
                if (failure) {
                    getLogger().severe("Failed again to contact twitch, giving up.");
                    return;
                }
                getLogger().warning("Failed to request token status, retrying in 5 minutes...");
                failure = true;
                Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> setupClient(), 5 * 60 * 20);
                return;
            }
            getConfig().set("access_token", access);
            getConfig().set("refresh_token", refresh);
            saveConfig();
            getLogger().info("Successfully retrieved new token, which expires in " + expiration + " seconds");
        } else {
            getLogger().info("Current token expires in " + expiration + " seconds");
            if (client != null) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> setupClient(), 30 * 60 * 20);
                return;
            }
        }
        // Build credential when possible
        OAuth2Credential credential = new OAuth2Credential("twitch", access);

        if (client != null) {
            client.close();
        }
        // Build TwitchClient
        client = TwitchClientBuilder.builder()
                .withClientId(getConfig().getString("client_id"))
                .withClientSecret(getConfig().getString("client_secret"))
                .withEnablePubSub(true)
                .withEnableHelix(true)
                .withDefaultAuthToken(credential)
                .build();

        User user = client.getHelix().getUsers(access, null, null).execute().getUsers().get(0);
        broadcasterId = user.getId();
        getLogger().info("Found broadcaster with ID " + broadcasterId);
        client.getPubSub().listenForChannelPointsRedemptionEvents(credential, broadcasterId);
        // Register event listeners
        client.getEventManager().getEventHandler(SimpleEventHandler.class).registerListener(new TwitchEventHandler(this));
        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> setupClient(), 30 * 60 * 20);
        actionManager.setup();
        failure = false;
    }

    public static TooManyIcons getInstance() {
        return instance;
    }
}
