package xal.plugin.jcaserver;

import gov.aps.jca.CAException;
import xal.ca.ChannelFactory;
import xal.ca.ChannelSystem;

/**
 * Concrete implementation of ChannelFactory that uses JCA.
 * 
 * @version 0.1 13 Jul 2015
 * @author Blaž Kranjc <blaz.kranjc@cosylab.com>
 */
public class JcaServerChannelFactory extends ChannelFactory {
    /** JCA channel system */
    final private JcaServerChannelSystem JCA_SERVER_SYSTEM;
    
    /**
     * Channel server for creating and holding PVs. Made static to all channels.
     */
    static private JcaServer CHANNEL_SERVER;
     
    /** Constructor */
    public JcaServerChannelFactory() {
        JCA_SERVER_SYSTEM = new JcaServerChannelSystem();
        try {
            CHANNEL_SERVER = new JcaServer();
        } catch (CAException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a JCA server channel for the specified PV
     * 
     * @param signalName
     *            The name of the PV signal
     */
    protected xal.ca.Channel newChannel(final String signalName) {
        return (xal.ca.Channel) new JcaServerChannel(signalName, CHANNEL_SERVER);
    }

    /**
     * JcaSystem handles static behavior of Jca channels
     * 
     * @return the JCA channel system
     */
    protected ChannelSystem channelSystem() {
        return JCA_SERVER_SYSTEM;
    }

    /** print information about this channel factory */
    public void printInfo() {
        JCA_SERVER_SYSTEM.printInfo();
    }

    @Override
    public boolean init() {
        // nothing to initialize
        return true;
    }
 
}