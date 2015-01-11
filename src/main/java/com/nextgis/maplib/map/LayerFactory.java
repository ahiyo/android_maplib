/*
 * Project:  NextGIS Mobile
 * Purpose:  Mobile GIS for Android.
 * Author:   Dmitry Baryshnikov (aka Bishop), bishop.dev@gmail.com
 * *****************************************************************************
 * Copyright (c) 2012-2015. NextGIS, info@nextgis.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nextgis.maplib.map;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;
import com.nextgis.maplib.api.ILayer;
import com.nextgis.maplib.datasource.ngw.Connection;
import com.nextgis.maplib.util.FileUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.nextgis.maplib.util.Constants.*;


public abstract class LayerFactory
{

    protected File mMapPath;


    public LayerFactory(File mapPath)
    {
        mMapPath = mapPath;
    }


    public ILayer createLayer(
            Context context,
            File path)
    {
        File config_file = new File(path, CONFIG);
        ILayer layer = null;

        try {
            String sData = FileUtil.readFromFile(config_file);
            JSONObject rootObject = new JSONObject(sData);
            int nType = rootObject.getInt(JSON_TYPE_KEY);

            switch (nType) {
                case LAYERTYPE_REMOTE_TMS:
                    layer = new RemoteTMSLayer(context, path);
                    break;
                case LAYERTYPE_NGW_RASTER:
                    layer = new NGWRasterLayer(context, path);
                    break;
            }
        } catch (IOException | JSONException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }

        return layer;
    }

    public Connection getConnectionFromAccount(Context context, String accountName){
        final AccountManager accountManager = AccountManager.get(context);
        for(Account account : accountManager.getAccountsByType(NGW_ACCOUNT_TYPE)){
            if(account.name.equals(accountName)){
                String url = accountManager.getUserData(account, "url");
                String password = accountManager.getPassword(account);
                String login = accountManager.getUserData(account, "login");
                return new Connection(accountName, login, password, url);
            }
        }
        return null;
    }

    public abstract void createNewRemoteTMSLayer(
            final Context context,
            final LayerGroup groupLayer);

    public abstract void createNewNGWLayer(
            final Context context,
            final LayerGroup groupLayer);
}