/*******************************************************************************
 * Copyright (c) 2015 PNF Software, Inc.
 *
 *     https://www.pnfsoftware.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.pnf.plugin.obb;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

import com.pnfsoftware.jeb.core.IUnitCreator;
import com.pnfsoftware.jeb.core.PluginInformation;
import com.pnfsoftware.jeb.core.Version;
import com.pnfsoftware.jeb.core.input.IInput;
import com.pnfsoftware.jeb.core.properties.IPropertyDefinitionManager;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.core.units.AbstractUnitIdentifier;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.IUnitProcessor;
import com.pnfsoftware.jeb.util.io.IO;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

/**
 * Android APK Expansion File plugin.
 * 
 * @author PNF Software
 *
 */
public class ObbPlugin extends AbstractUnitIdentifier {
    public static final ILogger LOG = GlobalLog.getLogger(ObbPlugin.class);

    private static final int[] OBB_SIG = {(byte)0x83, (byte)0x99, (byte)0x05, (byte)0x01};
    public static final String ID = "obb";
    public static final String FAT_IMAGE_NAME = "obb_image";

    public ObbPlugin() {
        super(ID, 1); // Give ObbPlugin higher priority than FatPlugin to
                      // make sure we enter this plugin first
    }

    @Override
    public PluginInformation getPluginInformation() {
        // requires JEB 2.1
        return new PluginInformation("Android OBB Plugin", "Opaque Binary Blob Parser", "PNF Software", Version.create(1, 0, 3));
    }

    public boolean canIdentify(IInput stream, IUnitCreator unit) {
        // Check for obb signature
        try(SeekableByteChannel ch = stream.getChannel()) {
            ch.position(ch.size() - OBB_SIG.length);
            ByteBuffer buff = ByteBuffer.allocate(OBB_SIG.length);
            ch.read(buff);
            return checkBytes(buff.array(), 0, OBB_SIG);
        }
        catch(IOException e) {
            return false;
        }
    }

    public void initialize(IPropertyDefinitionManager parent, IPropertyManager pm) {
        super.initialize(parent);
        /** Add any necessary property definitions here **/
    }

    @Override
    public IUnit prepare(String name, IInput data, IUnitProcessor processor, IUnitCreator parent) {
        // Parse obb data into object
        ObbData obbData = new ObbData();
        byte[] bytes = null;

        try(InputStream stream = data.getStream()) {
            bytes = IO.readInputStream(stream);
        }
        catch(IOException e) {
        }

        obbData.parseObbFile(bytes);

        // Create IUnit of type ObbUnit to delgate processing
        ObbUnit obbUnit = new ObbUnit(obbData, name, data, processor, parent, pdm);
        obbUnit.process();

        // Return the newly created ObbUnit
        return obbUnit;
    }
}
