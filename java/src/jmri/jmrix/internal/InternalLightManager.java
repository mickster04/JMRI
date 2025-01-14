package jmri.jmrix.internal;

import jmri.Light;
import jmri.implementation.AbstractVariableLight;

/**
 * Implement a LightManager for "Internal" (virtual) lights.
 *
 * @author Bob Jacobsen Copyright (C) 2009
 */
public class InternalLightManager extends jmri.managers.AbstractLightManager {

    public InternalLightManager(InternalSystemConnectionMemo memo) {
        super(memo);
    }

    /**
     * Create and return an internal (no layout connection) Light
     */
    @Override
    protected Light createNewLight(String systemName, String userName) {
        return new AbstractVariableLight(systemName, userName) {

            //protected void forwardCommandChangeToLayout(int s) {}
            @Override
            protected void sendIntensity(double intensity) {
            }

            @Override
            protected void sendOnOffCommand(int newState) {
            }

            @Override
            protected int getNumberOfSteps() {
                return 100;
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternalSystemConnectionMemo getMemo() {
        return (InternalSystemConnectionMemo) memo;
    }

    @Override
    public boolean validSystemNameConfig(String systemName) {
        return true;
    }

    @Override
    public boolean supportsVariableLights(String systemName) {
        return true;
    }

    @Override
    public boolean allowMultipleAdditions(String systemName) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntryToolTip() {
        return Bundle.getMessage("AddOutputEntryToolTip");
    }

}
