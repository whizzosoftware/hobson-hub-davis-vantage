/*
 *******************************************************************************
 * Copyright (c) 2013 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************
*/
package com.whizzosoftware.hobson.davisvantage;

import com.whizzosoftware.hobson.api.device.DeviceType;
import com.whizzosoftware.hobson.api.device.proxy.AbstractHobsonDeviceProxy;
import com.whizzosoftware.hobson.api.property.TypedProperty;
import com.whizzosoftware.hobson.api.variable.VariableConstants;
import com.whizzosoftware.hobson.api.variable.VariableMask;
import com.whizzosoftware.hobson.davisvantage.api.command.LoopResponse;
import com.whizzosoftware.hobson.davisvantage.api.command.VersionResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * A Hobson device representing a Davis Vantage controller.
 *
 * @author Dan Noguerol
 */
public class DavisVantageDevice extends AbstractHobsonDeviceProxy {

    DavisVantageDevice(DavisVantagePlugin plugin, String id) {
        super(plugin, id, "Davis Vantage", DeviceType.WEATHER_STATION);
    }

    @Override
    public void onStartup(String name, Map<String,Object> config) {
        setLastCheckin(System.currentTimeMillis());

        // publish the appropriate variable
        publishVariables(
            createDeviceVariable(VariableConstants.BAROMETRIC_PRESSURE_INHG, VariableMask.READ_ONLY, null, null),
            createDeviceVariable(VariableConstants.DEW_PT_F, VariableMask.READ_ONLY, null,null),
            createDeviceVariable(VariableConstants.FIRMWARE_VERSION, VariableMask.READ_ONLY, null, null),
            createDeviceVariable(VariableConstants.INDOOR_RELATIVE_HUMIDITY, VariableMask.READ_ONLY, null, null),
            createDeviceVariable(VariableConstants.INDOOR_TEMP_F, VariableMask.READ_ONLY, null, null),
            createDeviceVariable(VariableConstants.OUTDOOR_TEMP_F, VariableMask.READ_ONLY, null, null),
            createDeviceVariable(VariableConstants.OUTDOOR_RELATIVE_HUMIDITY, VariableMask.READ_ONLY, null, null),
            createDeviceVariable(VariableConstants.WIND_DIRECTION_DEGREES, VariableMask.READ_ONLY, null, null),
            createDeviceVariable(VariableConstants.WIND_SPEED_MPH, VariableMask.READ_ONLY, null, null)
        );
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public String getManufacturerName() {
        return null;
    }

    @Override
    public String getManufacturerVersion() {
        return null;
    }

    @Override
    public String getModelName() {
        return null;
    }

    @Override
    public String getPreferredVariableName() {
        return VariableConstants.OUTDOOR_TEMP_F;
    }

    @Override
    public void onDeviceConfigurationUpdate(Map<String,Object> config) {

    }

    void onTestResponse() {
        setLastCheckin(System.currentTimeMillis());
    }

    void onVersionResponse(VersionResponse response) {
        setVariableValue(VariableConstants.FIRMWARE_VERSION, response.getValue(), System.currentTimeMillis());
    }

    void onLoopResponse(LoopResponse loop) {
        Map<String,Object> values = new HashMap<>();

        values.put(VariableConstants.BAROMETRIC_PRESSURE_INHG, loop.hasBarometer() ? (loop.getBarometer() / 1000.0) : null);
        values.put(VariableConstants.DEW_PT_F, loop.getDewPoint());
        values.put(VariableConstants.INDOOR_TEMP_F, loop.hasInsideTemp() ? (loop.getInsideTemp() / 10.0) : null);
        values.put(VariableConstants.INDOOR_RELATIVE_HUMIDITY, loop.getInsideHumidity());
        values.put(VariableConstants.OUTDOOR_TEMP_F, loop.hasOutsideTemp() ? (loop.getOutsideTemp() / 10.0) : null);
        values.put(VariableConstants.OUTDOOR_RELATIVE_HUMIDITY, loop.getOutsideHumidity());
        values.put(VariableConstants.WIND_DIRECTION_DEGREES, loop.getWindDirection());
        values.put(VariableConstants.WIND_SPEED_MPH, loop.getWindSpeed());

        setVariableValues(values);
    }

    void onDisconnect() {
        setLastCheckin(null);
    }

    @Override
    protected TypedProperty[] getConfigurationPropertyTypes() {
        return null;
    }

    @Override
    public void onSetVariables(Map<String,Object> values) {
    }
}
