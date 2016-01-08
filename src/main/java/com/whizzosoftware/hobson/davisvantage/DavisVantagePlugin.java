/*******************************************************************************
 * Copyright (c) 2013 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.davisvantage;

import com.whizzosoftware.hobson.api.device.DeviceContext;
import com.whizzosoftware.hobson.api.plugin.channel.AbstractChannelObjectPlugin;
import com.whizzosoftware.hobson.api.plugin.channel.ChannelIdleDetectionConfig;
import com.whizzosoftware.hobson.api.property.PropertyConstraintType;
import com.whizzosoftware.hobson.api.property.TypedProperty;
import com.whizzosoftware.hobson.api.variable.VariableConstants;
import com.whizzosoftware.hobson.api.variable.VariableContext;
import com.whizzosoftware.hobson.api.variable.VariableUpdate;
import com.whizzosoftware.hobson.davisvantage.api.codec.VantageSerialFrameDecoder;
import com.whizzosoftware.hobson.davisvantage.api.codec.VantageSerialFrameEncoder;
import com.whizzosoftware.hobson.davisvantage.api.command.*;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A Hobson plugin that can read data from a Davis Vantage weather station.
 *
 * @author Dan Noguerol
 */
public class DavisVantagePlugin extends AbstractChannelObjectPlugin {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ChannelIdleDetectionConfig idleDetectionConfig = new ChannelIdleDetectionConfig(10, "TEST\n");
    private DavisVantageDevice device;

    public DavisVantagePlugin(String pluginId) {
        super(pluginId);
    }

    @Override
    public String getName() {
        return "Davis Vantage Plugin";
    }

    @Override
    protected ChannelInboundHandlerAdapter getDecoder() {
        return new VantageSerialFrameDecoder();
    }

    @Override
    protected ChannelOutboundHandlerAdapter getEncoder() {
        return new VantageSerialFrameEncoder();
    }

    @Override
    protected int getDefaultPort() {
        return 22222;
    }

    @Override
    protected ChannelIdleDetectionConfig getIdleDetectionConfig() {
        return idleDetectionConfig;
    }

    @Override
    protected void configureChannel(ChannelConfig cfg) {
    }

    @Override
    protected void onChannelConnected() {
        logger.debug("onChannelConnected()");
        if (device == null) {
            device = new DavisVantageDevice(this, "default");
            publishDevice(device);
        }
        send(new VersionRequest());
    }

    @Override
    protected void onChannelData(Object o) {
        if (device != null) {
            if (o instanceof LoopResponse) {
                logger.debug("Received a LOOP response: {}", o);

                DeviceContext dctx = device.getContext();

                // update variables
                LoopResponse loop = (LoopResponse) o;
                List<VariableUpdate> updates = new ArrayList<>();
                updates.add(new VariableUpdate(VariableContext.create(dctx, VariableConstants.BAROMETRIC_PRESSURE_INHG), loop.getBarometer() / 1000.0));
                updates.add(new VariableUpdate(VariableContext.create(dctx, VariableConstants.DEW_PT_F), loop.getDewPoint()));
                updates.add(new VariableUpdate(VariableContext.create(dctx, VariableConstants.INDOOR_TEMP_F), loop.getInsideTemp() / 10.0));
                updates.add(new VariableUpdate(VariableContext.create(dctx, VariableConstants.INDOOR_RELATIVE_HUMIDITY), loop.getInsideHumidity()));
                updates.add(new VariableUpdate(VariableContext.create(dctx, VariableConstants.OUTDOOR_TEMP_F), loop.getOutsideTemp() / 10.0));
                updates.add(new VariableUpdate(VariableContext.create(dctx, VariableConstants.OUTDOOR_RELATIVE_HUMIDITY), loop.getOutsideHumidity()));
                updates.add(new VariableUpdate(VariableContext.create(dctx, VariableConstants.WIND_DIRECTION_DEGREES), loop.getWindDirection()));
                updates.add(new VariableUpdate(VariableContext.create(dctx, VariableConstants.WIND_SPEED_MPH), loop.getWindSpeed()));
                fireVariableUpdateNotifications(updates);
            } else if (o instanceof VersionResponse) {
                logger.debug("Received version response: {}", o);
                // process the version response
                VersionResponse ver = (VersionResponse) o;
                fireVariableUpdateNotification(new VariableUpdate(VariableContext.create(device.getContext(), VariableConstants.FIRMWARE_VERSION), ver.getValue()));
                // send a request for the newest data
                sendLOOPRequest();
            } else if (o instanceof Test) {
                logger.trace("Received a TEST response");
                // flag device as checked in
                device.setDeviceAvailability(true, System.currentTimeMillis());
            } else if (!(o instanceof ACK) && !(o instanceof OK)) {
                logger.error("Received unknown response: {}", o);
            }
        } else {
            logger.error("Received data without a published device");
        }
    }

    @Override
    protected void onChannelDisconnected() {
        logger.debug("onChannelDisconnected()");

        setDeviceAvailability(device.getContext(), false, null);
    }

    @Override
    public long getRefreshInterval() {
        return 300;
    }

    @Override
    public void onRefresh() {
        // send a request for new data
        if (isConnected()) {
            sendLOOPRequest();
        }
    }

    @Override
    protected TypedProperty[] createSupportedProperties() {
        return new TypedProperty[] {
            new TypedProperty.Builder("serial.hostname", "Hostname", "The hostname or IP address of the Vantage base unit", TypedProperty.Type.STRING).
                constraint(PropertyConstraintType.required, true).
                build()
        };
    }

    protected void sendLOOPRequest() {
        logger.trace("Sending LOOP request");
        send(new LPSRequest(2, 1));
    }
}
