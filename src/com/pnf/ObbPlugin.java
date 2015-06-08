package com.pnf;

import com.pnfsoftware.jeb.core.PluginInformation;
import com.pnfsoftware.jeb.core.properties.IPropertyDefinitionManager;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.core.units.AbstractUnitIdentifier;
import com.pnfsoftware.jeb.core.units.IBinaryFrames;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.IUnitProcessor;

public class ObbPlugin extends AbstractUnitIdentifier{
	private static String ID = "obb_plugin";
	
	public ObbPlugin() {
		super(ID, 0);
	}

	public boolean identify(byte[] stream, IUnit unit) {
		ObbData data = new ObbData();
		return data.parseObbFile(stream);
	}
	
	public void initialize(IPropertyDefinitionManager parent, IPropertyManager pm) {
        super.initialize(parent, pm);
        /** Add any necessary property definitions here **/
    }

	@Override
	public IUnit prepare(String name, byte[] data, IUnitProcessor processor, IUnit unit) {
		return null;
	}

	@Override
	public PluginInformation getPluginInformation() {
		return new PluginInformation("Android OBB Plugin", "", "1.0", "PNF Software");
	}

	@Override
	public IUnit reload(IBinaryFrames data, IUnitProcessor processor, IUnit unit) {
		return null;
	}
}