package org.zeromq.jzmq.beacon;

import org.zeromq.api.BeaconListener;
import org.zeromq.api.BeaconReactor;
import org.zeromq.api.UdpBeacon;
import org.zeromq.jzmq.ManagedContext;

import java.io.IOException;

public class BeaconReactorBuilder {
    public static class Spec {
        public UdpBeacon beacon;
        public int port;
        public BeaconListener listener;
        public long broadcastInterval;
        public boolean ignoreLocalAddress = true;
    }

    private final ManagedContext context;
    private final Spec spec = new Spec();

    public BeaconReactorBuilder(ManagedContext context) {
        this.context = context;
    }

    public BeaconReactorBuilder withBeacon(UdpBeacon beacon) {
        spec.beacon = beacon;
        return this;
    }

    public BeaconReactorBuilder withPort(int port) {
        spec.port = port;
        return this;
    }

    public BeaconReactorBuilder withListener(BeaconListener listener) {
        spec.listener = listener;
        return this;
    }

    public BeaconReactorBuilder withBroadcastInterval(long broadcastInterval) {
        spec.broadcastInterval = broadcastInterval;
        return this;
    }

    public BeaconReactorBuilder withIgnoreLocalAddress(boolean ignoreLocalAddress) {
        spec.ignoreLocalAddress = ignoreLocalAddress;
        return this;
    }

    public BeaconReactor build() throws IOException {
        assert spec.listener != null;

        BeaconReactorImpl reactor = new BeaconReactorImpl(context, spec.port, spec.beacon);
        reactor.setIgnoreLocalAddress(spec.ignoreLocalAddress);
        reactor.setListener(spec.listener);
        if (spec.broadcastInterval != 0) {
            reactor.setBroadcastInterval(spec.broadcastInterval);
        }

        return reactor;
    }

    public BeaconReactor start() throws IOException {
        BeaconReactor reactor = build();
        reactor.start();

        return reactor;
    }
}
