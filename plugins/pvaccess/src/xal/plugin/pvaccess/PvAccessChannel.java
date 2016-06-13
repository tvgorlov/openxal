package xal.plugin.pvaccess;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.epics.pvaccess.client.Channel.ConnectionState;
import org.epics.pvaccess.client.ChannelGet;
import org.epics.pvaccess.client.ChannelGetRequester;
import org.epics.pvaccess.client.ChannelProvider;
import org.epics.pvaccess.client.ChannelProviderRegistryFactory;
import org.epics.pvaccess.client.ChannelPut;
import org.epics.pvaccess.client.ChannelPutRequester;
import org.epics.pvaccess.client.ChannelRequester;
import org.epics.pvaccess.util.logging.LoggingUtils;
import org.epics.pvdata.copy.CreateRequest;
import org.epics.pvdata.factory.PVDataFactory;
import org.epics.pvdata.misc.BitSet;
import org.epics.pvdata.monitor.MonitorRequester;
import org.epics.pvdata.pv.MessageType;
import org.epics.pvdata.pv.PVField;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.Status;
import org.epics.pvdata.pv.Structure;

import xal.ca.Channel;
import xal.ca.ChannelRecord;
import xal.ca.ChannelStatusRecord;
import xal.ca.ChannelTimeRecord;
import xal.ca.ConnectionException;
import xal.ca.GetException;
import xal.ca.IEventSinkValStatus;
import xal.ca.IEventSinkValTime;
import xal.ca.IEventSinkValue;
import xal.ca.Monitor;
import xal.ca.MonitorException;
import xal.ca.PutException;
import xal.ca.PutListener;

/**
 * Implementation of {@link Channel} class that uses pvAccess library.
 * pvAccess library can be used to connect with pvAccess or ChannelAccess protocol.
 * 
 * This implementation creates a connection to both protocols and only keeps the first 
 * one that is connected. This means that <b>if a pva channel and ca channel with same name
 * exist on the network the behavior of this class is non-deterministic</b>.
 * 
 * @author <a href="mailto:blaz.kranjc@cosylab.com">Blaz Kranjc</a>
 */
class PvAccessChannel extends Channel {
    
    // Default timeout parameters
    static final double DEFAULT_IO_TIMEOUT = 5.0;
    static final double DEFAULT_EVENT_TIMEOUT = 0.1;
    
    // Names of the standard fields
    static final String VALUE_FIELD_NAME = "value";
    static final String ALARM_FIELD_NAME = "alarm";
    static final String TIMESTAMP_FIELD_NAME = "timeStamp";
    static final String DISPLAY_FIELD_NAME = "display";
    static final String CONTROL_FIELD_NAME = "control";
    
    // Always get the whole pvStructure from the server
    private static final PVStructure pvRequest = CreateRequest.create().createRequest(null);

    // Channel implementation that is used by this class
    private org.epics.pvaccess.client.Channel channel;
    
    // Latch used to notify the connection to the channel
    private CountDownLatch connectionLatch;
    
    // Number of protocols used (pva and ca)
    private static final int PROTOCOL_N = 2;

    // Set that keeps track of created but unused channels
    private final Set<org.epics.pvaccess.client.Channel> createdChannels = new HashSet<>(PROTOCOL_N);

    // Lock for connection related stuff
    private final Object connectionLock = new Object();

    private static final Logger LOGGER = Logger.getLogger(PvAccessChannel.class.getName());

    private boolean isConnecting = false;
    
    /**
     * Constructor.
     * @param signalName Name of the PV
     */
    PvAccessChannel(String signalName) {
        super(signalName);
        m_dblTmIO = DEFAULT_IO_TIMEOUT;
        m_dblTmEvt = DEFAULT_EVENT_TIMEOUT;
        connectionFlag = false;
    }
    
    /**
     * @return Logger object of PvAccessChannel.
     */
    static Logger getLogger() {
        return LOGGER;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        if (channel != null && channel.getConnectionState() == ConnectionState.CONNECTED) {
            return true;
        }
        return false;
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestConnection() {
        if ( m_strId == null || isConnected()) { 
            return;
        }

        reset();

        synchronized (connectionLock) {
            isConnecting = true;
            ChannelProvidersProvider.countUp();

            connectionLatch = new CountDownLatch(1);
        
            org.epics.pvaccess.client.Channel caChannel = ChannelProvidersProvider.getCaProvider().createChannel(m_strId,
                            new PvAccessChannel.ChannelRequesterImpl(), ChannelProvider.PRIORITY_DEFAULT);
            createdChannels.add(caChannel);

            org.epics.pvaccess.client.Channel pvaChannel = ChannelProvidersProvider.getPvaProvider().createChannel(m_strId,
                            new PvAccessChannel.ChannelRequesterImpl(), ChannelProvider.PRIORITY_DEFAULT);
            createdChannels.add(pvaChannel);
        }
    }


    /**
     * Handle the channel connected event.
     */
    private void handleConnection(org.epics.pvaccess.client.Channel connectedChannel) {
        synchronized (connectionLock) {
            if (connectionFlag) {
                // If a channel was already connected from any other protocol do nothing.
                return;
            }

            this.channel = connectedChannel;
            cleanCreatedChannels(connectedChannel);

            connectionLatch.countDown();

            isConnecting = false;
            connectionFlag = true;

            if (connectionProxy != null) {
                connectionProxy.connectionMade(PvAccessChannel.this);
            }
        }
    }
    
    /**
     * Clean all created channels.
     */
    private void cleanCreatedChannels() {
        cleanCreatedChannels(null);
    }

    /**
     * Clean all created channels except for the one passed as parameter.
     * @param connectedChannel Channel that was connected and should not be cleaned
     */
    private void cleanCreatedChannels(org.epics.pvaccess.client.Channel connectedChannel) {
        if (connectedChannel != null) {
            // We do not want to clean the connected channel
            createdChannels.remove(connectedChannel);
        }
        for (org.epics.pvaccess.client.Channel c : createdChannels) {
            c.destroy();
        }
        createdChannels.remove(createdChannels);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkConnection() throws ConnectionException {
        checkConnection(true);
    }

    /**
     * Check if the channel is connected.
     * The connection attempt procedure may be started if the channel is not yet connected.
     * @param attemptConnection If true the function will try to attempt connection to the channel if not connected
     * @throws ConnectionException Thrown if connection failed
     */
    private void checkConnection(boolean attemptConnection) throws ConnectionException {
        if ( !isConnected() ) {
            if ( attemptConnection ) {
                connectAndWait();
                checkConnection(false);
            }
            else {
                throw new ConnectionException( this, "The channel " + m_strId + " must be connected for this operation." );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean connectAndWait(double timeout) {
        requestConnection();
        try {
            if (connectionLatch.await((long) timeout, TimeUnit.SECONDS)) {
                return true;
            }
        } catch (InterruptedException e) {
            // This should not happen, but if it does the connection was not established
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        reset();
    }

    /**
     * Resets the channel to the unconnected state.
     */
    private void reset() {
        synchronized (connectionLock) {
            if (connectionFlag || isConnecting) {
                isConnecting = false;
                connectionFlag = false;
                ChannelProvidersProvider.countDown();
            }

            cleanCreatedChannels();

            if (channel != null) {
                channel.destroy();
                channel = null;
            }
        }
    }

    /* As there is no method that is always called when the channel is no longer needed
     * {@link #finalize} is used to keep track of channel instances. */
    @Override
    protected void finalize() throws Throwable {
        if (connectionFlag) {
            ChannelProvidersProvider.countDown();
        }
        super.finalize();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> elementType() throws ConnectionException {
        try {
            return getChannelRecord().getElementType();
        } catch (GetException e) {
            throw new ConnectionException(this, e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int elementCount() throws ConnectionException {
        try {
            return getChannelRecord().getElementCount();
        } catch (GetException e) {
            throw new ConnectionException(this, e.getMessage());
        }
    }

    /**
     * PvAccess does not support access restrictions yet.
     */
    @Deprecated
    @Override
    public boolean readAccess() throws ConnectionException {
        return true;
    }

    /**
     * PvAccess does not support access restrictions yet.
     */
    @Deprecated
    @Override
    public boolean writeAccess() throws ConnectionException {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUnits() throws ConnectionException, GetException {
        return getChannelRecord().getUnits();
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    @Override
    public String[] getOperationLimitPVs() {
        return constructLimitPVs( "LOPR", "HOPR" ); // TODO Check the pva protocol
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    @Override
    public String[] getWarningLimitPVs() {
        return constructLimitPVs( "LOW", "HIGH" ); // TODO Check the pva protocol
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    @Override
    public String[] getAlarmLimitPVs() {
        return constructLimitPVs( "LOLO", "HIHI" ); // TODO Check the pva protocol
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    @Override
    public String[] getDriveLimitPVs() {
        return constructLimitPVs( "DRVL", "DRVH" ); // TODO Check the pva protocol
    }

    /** 
     * Construct the lower and upper limit PVs from the lower and upper suffixes
     * @return two element array of PVs with the lower and upper limit PVs 
     */
    private String[] constructLimitPVs( final String lowerSuffix, final String upperSuffix ) {
        final String[] rangePVs = new String[2];
        rangePVs[0] = channelName() + "." + lowerSuffix;
        rangePVs[1] = channelName() + "." + upperSuffix;
        return rangePVs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Number rawUpperDisplayLimit() throws ConnectionException, GetException {
        return getChannelRecord().getUpperDisplayLimit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Number rawLowerDisplayLimit() throws ConnectionException, GetException {
        return getChannelRecord().getLowerDisplayLimit();
    }

    /**
     * PvAccess default structure does not provide info on alarm limits.
     */
    @Deprecated
    @Override
    public Number rawUpperAlarmLimit() throws ConnectionException, GetException {
        return 0;
    }

    /**
     * PvAccess default structure does not provide info on alarm limits.
     */
    @Deprecated
    @Override
    public Number rawLowerAlarmLimit() throws ConnectionException, GetException {
        return 0;
    }

    /**
     * PvAccess default structure does not provide info on warning limits.
     */
    @Deprecated
    @Override
    public Number rawUpperWarningLimit() throws ConnectionException, GetException {
        return 0;
    }

    /**
     * PvAccess default structure does not provide info on warning limits.
     */
    @Deprecated
    @Override
    public Number rawLowerWarningLimit() throws ConnectionException, GetException {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Number rawUpperControlLimit() throws ConnectionException, GetException {
        return getChannelRecord().getUpperControlLimit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Number rawLowerControlLimit() throws ConnectionException, GetException {
        return getChannelRecord().getLowerControlLimit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChannelRecord getRawValueRecord() throws ConnectionException, GetException {
        return getRawTimeRecord();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ChannelRecord getRawStringValueRecord() throws ConnectionException, GetException {
        return getRawTimeRecord();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ChannelStatusRecord getRawStringStatusRecord() throws ConnectionException, GetException {
        return getRawTimeRecord();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ChannelTimeRecord getRawStringTimeRecord() throws ConnectionException, GetException {
        return getRawTimeRecord();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChannelStatusRecord getRawStatusRecord() throws ConnectionException, GetException {
        return getRawTimeRecord();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChannelTimeRecord getRawTimeRecord() throws ConnectionException, GetException {
        return getChannelRecord();
    }

    /**
     * Synchronous get from the channel.
     * A timeout is used for limiting the connecting time.
     * @return Channel record that contains information on the channel state.
     * @throws ConnectionException Thrown when connection to the channel failed.
     * @throws GetException Thrown when get operation failed.
     */
    private PvAccessChannelRecord getChannelRecord() throws ConnectionException, GetException {
        CountDownLatch latch = new CountDownLatch(1);
        EventSinkValTimeWithLatch listener = new EventSinkValTimeWithLatch(latch);

        getRawValueTimeCallback(listener, true);
        try {
            if (latch.await((long) m_dblTmIO, TimeUnit.SECONDS)) {
                return listener.getRecord();
            }
        } catch (InterruptedException e) {
            throw new GetException("Concurrency error" + e.getMessage());
        }
        throw new GetException("Timeout on get operation. No data recieved in " + m_dblTmIO + " seconds.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void getRawValueCallback(IEventSinkValue listener) throws ConnectionException, GetException {
        getRawValueCallback(listener, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void getRawValueCallback(IEventSinkValue listener, boolean attemptConnection)
            throws ConnectionException, GetException {
        checkConnection(attemptConnection);

        ChannelGetRequester requester = new PvAccessChannel.ChannelGetRequesterImpl(EventSinkAdapter.getAdapter(listener));
        channel.createChannelGet(requester, pvRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getRawValueTimeCallback(IEventSinkValTime listener, boolean attemptConnection)
            throws ConnectionException, GetException {
        checkConnection(attemptConnection);

        ChannelGetRequester requester = new PvAccessChannel.ChannelGetRequesterImpl(EventSinkAdapter.getAdapter(listener));
        channel.createChannelGet(requester, pvRequest);
    }

    /**
     * {@inheritDoc}
     */
    private void putRawValCallback(final Object newVal, final String type, final PutListener listener) 
            throws ConnectionException, PutException {
        checkConnection();
        
        ChannelPutRequester requester = new PvAccessChannel.ChannelPutRequesterImpl(newVal, type, listener);
        channel.createChannelPut(requester, pvRequest);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void putRawValCallback(String newVal, PutListener listener) throws ConnectionException, PutException {
        putRawValCallback(newVal, String.class.getName(), listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putRawValCallback(byte newVal, PutListener listener) throws ConnectionException, PutException {
        putRawValCallback(newVal, byte.class.getName(), listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putRawValCallback(short newVal, PutListener listener) throws ConnectionException, PutException {
        putRawValCallback(newVal, short.class.getName(), listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putRawValCallback(int newVal, PutListener listener) throws ConnectionException, PutException {
        putRawValCallback(newVal, int.class.getName(), listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putRawValCallback(float newVal, PutListener listener) throws ConnectionException, PutException {
        putRawValCallback(newVal, float.class.getName(), listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putRawValCallback(double newVal, PutListener listener) throws ConnectionException, PutException {
        putRawValCallback(newVal, double.class.getName(), listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putRawValCallback(byte[] newVal, PutListener listener) throws ConnectionException, PutException {
        putRawValCallback(newVal, byte[].class.getName(), listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putRawValCallback(short[] newVal, PutListener listener) throws ConnectionException, PutException {
        putRawValCallback(newVal, short[].class.getName(), listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putRawValCallback(int[] newVal, PutListener listener) throws ConnectionException, PutException {
        putRawValCallback(newVal, int[].class.getName(), listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putRawValCallback(float[] newVal, PutListener listener) throws ConnectionException, PutException {
        putRawValCallback(newVal, float[].class.getName(), listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putRawValCallback(double[] newVal, PutListener listener) throws ConnectionException, PutException {
        putRawValCallback(newVal, double[].class.getName(), listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Monitor addMonitorValTime(IEventSinkValTime listener, int intMaskFire)
            throws ConnectionException, MonitorException {
        return addMonitor(EventSinkAdapter.getAdapter(listener), intMaskFire);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Monitor addMonitorValStatus(IEventSinkValStatus listener, int intMaskFire)
            throws ConnectionException, MonitorException {
        return addMonitor(EventSinkAdapter.getAdapter(listener), intMaskFire);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Monitor addMonitorValue(IEventSinkValue listener, int intMaskFire)
            throws ConnectionException, MonitorException {
        return addMonitor(EventSinkAdapter.getAdapter(listener), intMaskFire);
    }
    
    private Monitor addMonitor(EventSinkAdapter listener, int intMaskFire) throws ConnectionException {
        MonitorRequester monitorRequester = new MonitorRequesterImpl(listener, this, intMaskFire);

        channel.createMonitor(monitorRequester, pvRequest);
        return (Monitor) monitorRequester;
    }
        
    /**
     * ChannelRequesterImplementation that sends the connection established events to the 
     * PvAccessChannel class on connection.
     */
    private class ChannelRequesterImpl implements ChannelRequester {
        
        /** Constructor */
        public ChannelRequesterImpl() {}

        /**
         * {@inheritDoc}
         */
        @Override
        public String getRequesterName() {
            return getClass().getName();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void message(String message, MessageType messageType) {
            PvAccessChannel.LOGGER.log(LoggingUtils.toLevel(messageType), message);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void channelCreated(Status status, org.epics.pvaccess.client.Channel connectedChannel) {
            PvAccessChannel.LOGGER.info("Channel '" + connectedChannel.getChannelName() + "["+
                    connectedChannel.getProvider().getProviderName() +"]' created with status: " + status + ".");
        }

        /**
         * {@inheritDoc}
         * 
         * This implementation raises an event on the outer class, when the channel is connected.
         */
        @Override
        public void channelStateChange(org.epics.pvaccess.client.Channel connectedChannel, ConnectionState newState) {
            PvAccessChannel.LOGGER.info("State of channel " + connectedChannel.getChannelName() + "[" +
                    connectedChannel.getProvider().getProviderName() + "] changed to " + newState.toString() + ".");

            if (newState == ConnectionState.CONNECTED) {
                PvAccessChannel.this.handleConnection(connectedChannel);
            }
        }
    }
    
    /**
     * ChannelGetRequester implementation.
     */
    private class ChannelGetRequesterImpl implements ChannelGetRequester {

        EventSinkAdapter listener;
        
        /**
         * Constructor
         * @param listener An object on which the events are raised
         */
        ChannelGetRequesterImpl(EventSinkAdapter listener) {
            this.listener = listener;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getRequesterName() {
            return getClass().getName();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void message(String message, MessageType type) {
            PvAccessChannel.LOGGER.log(LoggingUtils.toLevel(type), message);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void channelGetConnect(Status status, ChannelGet channelGet, Structure structure) {
            if (status.isSuccess())
            {
                channelGet.lastRequest();
                channelGet.get();
            }
            else
                PvAccessChannel.LOGGER.warning("Error while connecting channel " + PvAccessChannel.this.channelName() + " for get operation.");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void getDone(Status status, ChannelGet channelGet, PVStructure pvStructure, BitSet changedBitSet) {
            if (status.isSuccess())
            {
                listener.eventValue(pvStructure, PvAccessChannel.this);
            } 
            else 
                PvAccessChannel.LOGGER.warning("Error while getting the value from " + PvAccessChannel.this.channelName() + ".");
        }
        
    }
    
    /**
     * ChannelPutRequester implementation.
     */
    private class ChannelPutRequesterImpl implements ChannelPutRequester {
        
        private final PutListener listener;
        private final Object value;
        private final String pvType;

        /**
         * Constructor for the ChannelPutRequester.
         * @param value Object to be put on the channel
         * @param type Name of the type of the object to put
         * @param listener Listener object on which events are raised when operation is completed
         */
        ChannelPutRequesterImpl(Object value, String type, PutListener listener) {
            this.value = value;
            this.pvType = type;
            this.listener = listener;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String getRequesterName() {
            return getClass().getName();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void message(String message, MessageType type) {
            PvAccessChannel.LOGGER.log(LoggingUtils.toLevel(type), message);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void channelPutConnect(Status status, ChannelPut channelPut, Structure structure) {
            channelPut.lastRequest();

            if (status.isSuccess())
            {
                PVStructure pvStructure = PVDataFactory.getPVDataCreate().createPVStructure(structure);
                BitSet bitSet = new BitSet(pvStructure.getNumberFields());

                PVField val = pvStructure.getSubField(VALUE_FIELD_NAME);
                PvAccessPutUtils.put(val, value, pvType);
                bitSet.set(val.getFieldOffset());
                channelPut.put(pvStructure, bitSet);
            } else {
                PvAccessChannel.LOGGER.warning("Error while connecting channel " + PvAccessChannel.this.channelName() +
                        " for put operation.");
            }
            
        }

        /**
         * This method is not used in this implementation.
         */
        @Override
        public void getDone(Status status, ChannelPut channelPut, PVStructure pvStructure, BitSet bitSet) {
            // Not used.
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void putDone(Status status, ChannelPut channelPut) {
            if (status.isSuccess()) {
                listener.putCompleted(PvAccessChannel.this);
            } else {
                PvAccessChannel.LOGGER.warning("Error while puting a value to channel " + PvAccessChannel.this.channelName() + ".");
            }
        }
        
    }

    /**
     * Class used for instance counting and PvAccess ChannelProvider initialization.
     */
    private final static class ChannelProvidersProvider {
    
        private static AtomicInteger count = new AtomicInteger(0);
        
        /* Should not be instanced */
        private ChannelProvidersProvider() {}

        /**
         * Decrease the number of instances and stop the channel providers if no instances are left.
         */
        static void countDown() {
            int currentCount = count.decrementAndGet();
            assert currentCount < 0;
            if (currentCount == 0) {
                stopProviders();
            }
        }

        /**
         * Increase the number of instances and start the channel providers if needed.
         */
        static void countUp() {
            int pastCount = count.getAndIncrement();
            if (pastCount == 0) {
                startProviders();
            }
        }

        /**
         * Start the channel providers.
         */
        private static synchronized void startProviders() {
            org.epics.pvaccess.ClientFactory.start();
            org.epics.ca.ClientFactory.start();
        }

        /**
         * Stop the channel providers.
         */
        private static synchronized void stopProviders() {
            org.epics.pvaccess.ClientFactory.stop();
            org.epics.ca.ClientFactory.stop();
        }

        /**
         * Get CA channel provider.
         * @return CA channel provider
         */
        static ChannelProvider getCaProvider() {
            return ChannelProviderRegistryFactory.getChannelProviderRegistry()
                    .getProvider(org.epics.ca.ClientFactory.PROVIDER_NAME);
        }

        /**
         * Get pva channel provider.
         * @return pva channel provider
         */
        static ChannelProvider getPvaProvider() {
            return ChannelProviderRegistryFactory.getChannelProviderRegistry()
                    .getProvider(org.epics.pvaccess.ClientFactory.PROVIDER_NAME);
        }

    }

}
