package org.zeromq.jzmq.clone;

import org.zeromq.api.CloneClient;
import org.zeromq.jzmq.ManagedContext;

public class CloneClientBuilder {
    public class Spec {
        public String primaryAddress;
        public String backupAddress;
        public int primaryPort;
        public int backupPort;
        public String subtree;
        public long heartbeatInterval = 1000L;
    }

    private ManagedContext context;
    private Spec spec = new Spec();

    public CloneClientBuilder(ManagedContext context) {
        this.context = context;
    }

    public CloneClientBuilder withPrimaryAddress(String primaryAddress) {
        spec.primaryAddress = primaryAddress;
        return this;
    }

    public CloneClientBuilder withBackupAddress(String backupAddress) {
        spec.backupAddress = backupAddress;
        return this;
    }

    public CloneClientBuilder withPrimaryPort(int primaryPort) {
        spec.primaryPort = primaryPort;
        return this;
    }

    public CloneClientBuilder withBackupPort(int backupPort) {
        spec.backupPort = backupPort;
        return this;
    }

    public CloneClientBuilder withSubtree(String subtree) {
        spec.subtree = subtree;
        return this;
    }

    public CloneClientBuilder withHeartbeatInterval(long heartbeatInterval) {
        spec.heartbeatInterval = heartbeatInterval;
        return this;
    }

    public CloneClient build() {
        CloneClient cloneClient = new CloneClientImpl(context, spec.heartbeatInterval);
        if (spec.subtree != null) {
            cloneClient.subscribe(spec.subtree);
        }
        if (spec.primaryAddress != null) {
            cloneClient.connect(spec.primaryAddress, spec.primaryPort);
        }
        if (spec.backupAddress != null) {
            cloneClient.connect(spec.backupAddress, spec.backupPort);
        }
        
        return cloneClient;
    }
}
