package me.datatags.badb7sbadbuttons;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.TwitchClientBuilder;

import java.io.IOException;

import me.datatags.badb7sbadbuttons.actions.ActionManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class BadB7sBadButtons extends JavaPlugin {

    private static BadB7sBadButtons instance;
    private ActionManager actionManager;
    private String broadcasterId;
    private ITwitchClient client = null;

    @Override
    public void onEnable() {
        instance = this;
        // Get the latest config after saving the default if missing
        this.saveDefaultConfig();

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> setupClient());
        getCommand("bbbb").setExecutor(new RewardCommand(this));
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
            // ???
            e.printStackTrace();
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
                    return;
                }
                JSONObject object = new JSONObject(response.body().string());
                access = object.getString("access_token");
                refresh = object.getString("refresh_token");
                expiration = object.getLong("expires_in");
            } catch (IOException e) {
                // ???
                e.printStackTrace();
                return;
            }
            getConfig().set("access_token", access);
            getConfig().set("refresh_token", refresh);
            saveConfig();
            getLogger().info("Successfully retrieved new token!");
        }

        if (valid) {
            getLogger().info("Current token expires in " + expiration + " seconds");
            if (client != null) {
                getLogger().info("Will re-check in 30 minutes");
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

        broadcasterId = client.getHelix().getUsers(access, null, null).execute().getUsers().get(0).getId();
        getLogger().info("Found broadcaster with ID " + broadcasterId);
        client.getPubSub().listenForChannelPointsRedemptionEvents(credential, broadcasterId);
        // Register event listeners
        client.getEventManager().getEventHandler(SimpleEventHandler.class).registerListener(new TwitchEventHandler(this));
        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> setupClient(), 30 * 60 * 20);
        getLogger().info("Client ready, will re-check token in 30 minutes");
        actionManager.setup();
    }

    public static BadB7sBadButtons getInstance() {
        return instance;
    }
}
