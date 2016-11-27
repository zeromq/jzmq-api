package org.zeromq.jzmq.clone;

import org.zeromq.api.BinaryStarReactor;
import org.zeromq.api.CloneServer;
import org.zeromq.jzmq.ManagedContext;

public class CloneServerBuilder {
    public class Spec {
        public BinaryStarReactor.Mode mode;
        public String primaryAddress;
        public String backupAddress;
        public int primaryPort;
        public int backupPort;
        public int primaryBstarPort;
        public int backupBstarPort;
        public long heartbeatInterval = 1000;
    }

    private ManagedContext context;
    private Spec spec = new Spec();

    public CloneServerBuilder(ManagedContext context) {
        this.context = context;
    }

    public CloneServerBuilder withMode(BinaryStarReactor.Mode mode) {
        spec.mode = mode;
        return this;
    }

    public CloneServerBuilder withPrimaryAddress(String primaryAddress) {
        spec.primaryAddress = primaryAddress;
        return this;
    }

    public CloneServerBuilder withBackupAddress(String backupAddress) {
        spec.backupAddress = backupAddress;
        return this;
    }

    public CloneServerBuilder withPrimaryPort(int primaryPort) {
        spec.primaryPort = primaryPort;
        return this;
    }

    public CloneServerBuilder withBackupPort(int backupPort) {
        spec.backupPort = backupPort;
        return this;
    }

    public CloneServerBuilder withPrimaryBinaryStarPort(int primaryBstarPort) {
        spec.primaryBstarPort = primaryBstarPort;
        return this;
    }

    public CloneServerBuilder withBackupBinaryStarPort(int backupBstarPort) {
        spec.backupBstarPort = backupBstarPort;
        return this;
    }

    public CloneServerBuilder withHeartbeatInterval(long heartbeatInterval) {
        spec.heartbeatInterval = heartbeatInterval;
        return this;
    }

    public CloneServer build() {
        String peerAddress;
        int localPort, peerPort, localBstarPort, peerBstarPort;
        if (spec.mode == BinaryStarReactor.Mode.PRIMARY) {
            localPort = spec.primaryPort;
            peerAddress = spec.backupAddress;
            peerPort = spec.backupPort;
            localBstarPort = spec.primaryBstarPort;
            peerBstarPort = spec.backupBstarPort;
        } else {
            localPort = spec.backupPort;
            peerAddress = spec.primaryAddress;
            peerPort = spec.primaryPort;
            localBstarPort = spec.backupBstarPort;
            peerBstarPort = spec.primaryBstarPort;
        }

        CloneServerImpl cloneServer = new CloneServerImpl(context, spec.mode, peerAddress, localPort, peerPort, localBstarPort, peerBstarPort);
        cloneServer.setHeartbeatInterval(spec.heartbeatInterval);

        return cloneServer;
    }
}
