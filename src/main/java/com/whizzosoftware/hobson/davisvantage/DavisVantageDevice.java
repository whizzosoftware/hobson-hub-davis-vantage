/*******************************************************************************
 * Copyright (c) 2013 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.davisvantage;

import com.whizzosoftware.hobson.api.device.AbstractHobsonDevice;
import com.whizzosoftware.hobson.api.device.DeviceType;
import com.whizzosoftware.hobson.api.property.PropertyContainer;
import com.whizzosoftware.hobson.api.property.TypedProperty;
import com.whizzosoftware.hobson.api.variable.HobsonVariable;
import com.whizzosoftware.hobson.api.variable.VariableConstants;

/**
 * A Hobson device representing a Davis Vantage controller.
 *
 * @author Dan Noguerol
 */
public class DavisVantageDevice extends AbstractHobsonDevice {

    public DavisVantageDevice(DavisVantagePlugin plugin, String id) {
        super(plugin, id);
        setDefaultName("Davis Vantage");
    }

    @Override
    public void onStartup(PropertyContainer config) {
        super.onStartup(config);

        // publish the appropriate variable
        publishVariable(VariableConstants.BAROMETRIC_PRESSURE_INHG, null, HobsonVariable.Mask.READ_ONLY);
        publishVariable(VariableConstants.DEW_PT_F, null, HobsonVariable.Mask.READ_ONLY);
        publishVariable(VariableConstants.FIRMWARE_VERSION, null, HobsonVariable.Mask.READ_ONLY);
        publishVariable(VariableConstants.INDOOR_RELATIVE_HUMIDITY, null, HobsonVariable.Mask.READ_ONLY);
        publishVariable(VariableConstants.INDOOR_TEMP_F, null, HobsonVariable.Mask.READ_ONLY);
        publishVariable(VariableConstants.OUTDOOR_TEMP_F, null, HobsonVariable.Mask.READ_ONLY);
        publishVariable(VariableConstants.OUTDOOR_RELATIVE_HUMIDITY, null, HobsonVariable.Mask.READ_ONLY);
        publishVariable(VariableConstants.WIND_DIRECTION_DEGREES, null, HobsonVariable.Mask.READ_ONLY);
        publishVariable(VariableConstants.WIND_SPEED_MPH, null, HobsonVariable.Mask.READ_ONLY);
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public DeviceType getType() {
        return DeviceType.WEATHER_STATION;
    }

    @Override
    public String getPreferredVariableName() {
        return VariableConstants.OUTDOOR_TEMP_F;
    }

    @Override
    protected TypedProperty[] createSupportedProperties() {
        return null;
    }

    @Override
    public void onSetVariable(String name, Object value) {
    }
}
