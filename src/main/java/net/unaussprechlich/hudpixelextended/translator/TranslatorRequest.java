/*
 * HudPixelReloaded - License
 * <p>
 * The repository contains parts of Minecraft Forge and its dependencies. These parts have their licenses
 * under forge-docs/. These parts can be downloaded at files.minecraftforge.net.This project contains a
 * unofficial copy of pictures from the official Hypixel website. All copyright is held by the creator!
 * Parts of the code are based upon the Hypixel Public API. These parts are all in src/main/java/net/hypixel/api and
 * subdirectories and have a special copyright header. Unfortunately they are missing a license but they are obviously
 * intended for usage in this kind of application. By default, all rights are reserved.
 * The original version of the HudPixel Mod is made by palechip and published under the MIT license.
 * The majority of code left from palechip's creations is the component implementation.The ported version to
 * Minecraft 1.8.9 and up HudPixel Reloaded is made by PixelModders/Eladkay and also published under the MIT license
 * (to be changed to the new license as detailed below in the next minor update).
 * <p>
 * For the rest of the code and for the build the following license applies:
 * <p>
 * # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
 * #  HudPixel by PixelModders, Eladkay & unaussprechlich is licensed under a Creative Commons         #
 * #  Attribution-NonCommercial-ShareAlike 4.0 International License with the following restrictions.  #
 * #  Based on a work at HudPixelExtended & HudPixel.                                                  #
 * # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
 * <p>
 * Restrictions:
 * <p>
 * The authors are allowed to change the license at their desire. This license is void for members of PixelModders and
 * to unaussprechlich, except for clause 3. The licensor cannot revoke these freedoms in most cases, as long as you follow
 * the following license terms and the license terms given by the listed above Creative Commons License, however in extreme
 * cases the authors reserve the right to revoke all rights for usage of the codebase.
 * <p>
 * 1. PixelModders, Eladkay & unaussprechlich are the authors of this licensed material. GitHub contributors are NOT
 * considered authors, neither are members of the HudHelper program. GitHub contributers still hold the rights for their
 * code, but only when it is used separately from HudPixel and any license header must indicate that.
 * 2. You shall not claim ownership over this project and repost it in any case, without written permission from at least
 * two of the authors.
 * 3. You shall not make money with the provided material. This project is 100% non commercial and will always stay that
 * way. This clause is the only one remaining, should the rest of the license be revoked. The only exception to this
 * clause is completely cosmetic features. Only the authors may sell cosmetic features for the mod.
 * 4. Every single contibutor owns copyright over his contributed code when separated from HudPixel. When it's part of
 * HudPixel, it is only governed by this license, and any copyright header must indicate that. After the contributed
 * code is merged to the release branch you cannot revoke the given freedoms by this license.
 * 5. If your own project contains a part of the licensed material you have to give the authors full access to all project
 * related files.
 * 6. You shall not act against the will of the authors regarding anything related to the mod or its codebase. The authors
 * reserve the right to take down any infringing project.
 */

package net.unaussprechlich.hudpixelextended.translator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eladkay.hudpixel.HudPixelMod;
import net.unaussprechlich.hudpixelextended.HudPixelExtended;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class TranslatorRequest {


    private final String message;
    private final ITranslatorRequestCallback callback;
    private final String username;
    private JsonObject response;
    private boolean failed;

    public TranslatorRequest(String message, ITranslatorRequestCallback callback, String username) {
        this.message = message;
        this.callback = callback;
        this.username = username;
        request();
    }


    public JsonObject getResponse() {
        return response;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public boolean isFailed() {
        return failed;
    }

    private void request() {

        new Thread(() -> {
            try {
                String translate = message.replace(" ", "%20").replace("ä", "ae").replace("ö", "oe").replace("ü", "ue");
                URL u = new URL("https://translate.unaussprechlich.net/?text=" + translate
                        + "&lang=" + Translator.getLanguage() + "&uuid=" + HudPixelExtended.UUID);
                HttpsURLConnection con = (HttpsURLConnection) u.openConnection();
                con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                StringBuilder sBuilder = new StringBuilder();
                String buff = "";
                while ((buff = br.readLine()) != null) {
                    sBuilder.append(buff);
                }
                String data = sBuilder.toString();
                response = jsonParser(data);
                failed = false;
                callback.translatorCallback(this);
            } catch (IOException e) {
                e.printStackTrace();
                failed = true;
                HudPixelMod.Companion.instance().getLogger().info("Couldn't translate message->"
                        + message + " username->" + username + " response->" + response);
                callback.translatorCallback(this);
            }
        }).start();
    }


    private JsonObject jsonParser(String data) {
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(data).getAsJsonObject();
    }


}
