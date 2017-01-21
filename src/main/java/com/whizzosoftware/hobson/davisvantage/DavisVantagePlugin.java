/*******************************************************************************
 * Copyright (c) 2013 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.davisvantage;

import com.whizzosoftware.hobson.api.plugin.channel.AbstractChannelObjectPlugin;
import com.whizzosoftware.hobson.api.plugin.channel.ChannelIdleDetectionConfig;
import com.whizzosoftware.hobson.api.property.PropertyConstraintType;
import com.whizzosoftware.hobson.api.property.TypedProperty;
import com.whizzosoftware.hobson.davisvantage.api.codec.VantageSerialFrameDecoder;
import com.whizzosoftware.hobson.davisvantage.api.codec.VantageSerialFrameEncoder;
import com.whizzosoftware.hobson.davisvantage.api.command.*;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Hobson plugin that can read data from a Davis Vantage weather station.
 *
 * @author Dan Noguerol
 */
public class DavisVantagePlugin extends AbstractChannelObjectPlugin {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String DEVICE_ID = "default";

    private final ChannelIdleDetectionConfig idleDetectionConfig = new ChannelIdleDetectionConfig(10, "TEST\n");
    private DavisVantageDevice device;

    public DavisVantagePlugin(String pluginId, String version, String description) {
        super(pluginId, version, description);
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
            device = new DavisVantageDevice(this, DEVICE_ID);
            publishDeviceProxy(device);
        }
        send(new VersionRequest());
    }

    @Override
    protected void onChannelData(Object o) {
        if (device != null) {
            if (o instanceof LoopResponse) {
                logger.debug("Received a LOOP response: {}", o);
                // update variables
                LoopResponse loop = (LoopResponse) o;
                device.onLoopResponse(loop);
            } else if (o instanceof VersionResponse) {
                logger.debug("Received version response: {}", o);
                // process the version response
                VersionResponse ver = (VersionResponse) o;
                device.onVersionResponse(ver);
                // send a request for the newest data
                sendLOOPRequest();
            } else if (o instanceof Test) {
                logger.trace("Received a TEST response");
                // flag device as checked in
                device.onTestResponse();
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
        device.onDisconnect();
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
    protected TypedProperty[] getConfigurationPropertyTypes() {
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
