package org.zeromq.jzmq.device;

import org.zeromq.api.DeviceType;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class DeviceBuilder {
    public class Spec {
        public DeviceType deviceType;
        public String frontend;
        public String backend;
    }
    
    private ManagedContext context;
    private Spec spec = new Spec();

    public DeviceBuilder(ManagedContext context, DeviceType deviceType) {
        this.context = context;
        spec.deviceType = deviceType;
    }

    public DeviceBuilder withFrontendUrl(String frontend) {
        spec.frontend = frontend;
        return this;
    }

    public DeviceBuilder withBackendUrl(String backend) {
        spec.backend = backend;
        return this;
    }

    public void start() {
        switch (spec.deviceType) {
            case STREAMER:
                startStreamer();
                break;
            case FORWARDER:
                startForwarder();
                break;
            case QUEUE:
                startQueue();
                break;
        }
    }

    private void startStreamer() {
        Socket frontend = context.buildSocket(SocketType.PULL)
            .bind(spec.frontend);

        Socket backend = context.buildSocket(SocketType.PUSH)
            .bind(spec.backend);

        context.forward(frontend, backend);
    }

    private void startForwarder() {
        Socket frontend = context.buildSocket(SocketType.XSUB)
            .bind(spec.frontend);

        Socket backend = context.buildSocket(SocketType.XPUB)
            .bind(spec.backend);

        context.forward(frontend, backend);
    }

    private void startQueue() {
        Socket frontend = context.buildSocket(SocketType.ROUTER)
            .bind(spec.frontend);

        Socket backend = context.buildSocket(SocketType.DEALER)
            .bind(spec.backend);

        context.forward(frontend, backend);
    }
}
