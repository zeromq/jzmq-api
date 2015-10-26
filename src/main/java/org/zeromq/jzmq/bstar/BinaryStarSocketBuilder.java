package org.zeromq.jzmq.bstar;

import org.zeromq.api.BinaryStar;
import org.zeromq.api.Socket;
import org.zeromq.jzmq.ManagedContext;
import org.zeromq.jzmq.sockets.ReqSocketBuilder;

public class BinaryStarSocketBuilder extends ReqSocketBuilder {
    public class Spec {
        public long heartbeatInterval = BinaryStar.BSTAR_HEARTBEAT;
    }

    private final Spec spec = new Spec();

    public BinaryStarSocketBuilder(ManagedContext context) {
        super(context);
    }

    public BinaryStarSocketBuilder withHeartbeatInterval(long heartbeatInterval) {
        spec.heartbeatInterval = heartbeatInterval;
        return this;
    }

    @Override
    public Socket connect(String url, String... additionalUrls) {
        Socket socket;
        if (additionalUrls.length == 0) {
            socket = super.connect(url, additionalUrls);
        } else {
            assert (additionalUrls.length == 1);
            socket = fork(url, additionalUrls[0]);
        }

        return socket;
    }

    @Override
    public Socket bind(String url, String... additionalUrls) {
        throw new UnsupportedOperationException("Cannot bind to Binary Star server");
    }

    private Socket fork(String url1, String url2) {
        BinaryStarClient client = new BinaryStarClient(this, url1, url2, spec.heartbeatInterval);
        return context.fork(client);
    }
}
