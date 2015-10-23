package org.zeromq.jzmq.beacon;

import org.zeromq.api.BeaconListener;
import org.zeromq.api.BeaconReactor;
import org.zeromq.api.UdpBeacon;
import org.zeromq.jzmq.ManagedContext;

import java.io.IOException;

public class BeaconReactorBuilder {
    private final ManagedContext context;
    private UdpBeacon beacon;
    private int port;
    private BeaconListener listener;
    private boolean ignoreLocalAddress = true;

    public BeaconReactorBuilder(ManagedContext context) {
        this.context = context;
    }

    public BeaconReactorBuilder withBeacon(UdpBeacon beacon) {
        this.beacon = beacon;
        return this;
    }

    public BeaconReactorBuilder withPort(int port) {
        this.port = port;
        return this;
    }

    public BeaconReactorBuilder withListener(BeaconListener listener) {
        this.listener = listener;
        return this;
    }

    public BeaconReactorBuilder withIgnoreLocalAddress(boolean ignoreLocalAddress) {
        this.ignoreLocalAddress = ignoreLocalAddress;
        return this;
    }

    public BeaconReactor build() throws IOException {
        assert (listener != null);

        BeaconReactorImpl reactor = new BeaconReactorImpl(context, port, beacon);
        reactor.setIgnoreLocalAddress(ignoreLocalAddress);
        reactor.setListener(listener);

        return reactor;
    }

    public BeaconReactor start() throws IOException {
        BeaconReactor reactor = build();
        reactor.start();

        return reactor;
    }
}
