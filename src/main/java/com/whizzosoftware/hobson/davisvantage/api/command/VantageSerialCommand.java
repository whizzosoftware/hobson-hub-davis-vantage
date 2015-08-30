/*******************************************************************************
 * Copyright (c) 2013 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.davisvantage.api.command;

/**
 * Base class for Vantage serial command frames.
 *
 * @author Dan Noguerol
 */
abstract public class VantageSerialCommand {
    private String type;

    public VantageSerialCommand(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public abstract byte[] getBytes();
}
