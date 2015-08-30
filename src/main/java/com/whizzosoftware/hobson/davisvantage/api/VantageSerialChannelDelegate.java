/*******************************************************************************
 * Copyright (c) 2013 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.davisvantage.api;

import com.whizzosoftware.hobson.davisvantage.api.command.LoopResponse;

/**
 * A delegate interface for receiving Davis Vantage serial data.
 *
 * @author Dan Noguerol
 */
public interface VantageSerialChannelDelegate {
    void onLoopPacket(LoopResponse packet);
}
