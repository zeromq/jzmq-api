package org.zeromq.jzmq.bstar;

import org.zeromq.api.BinaryStarReactor;
import org.zeromq.api.BinaryStarReactor.Mode;
import org.zeromq.api.LoopHandler;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class BinaryStarReactorBuilder {
    public class Spec {
        public Mode mode;
        public String local;
        public String remote;
        public long heartbeatInterval = BinaryStarReactor.BSTAR_HEARTBEAT;
        public Socket voter;

        public LoopHandler activeHandler;
        public Object[] activeArgs;

        public LoopHandler voterHandler;
        public Object[] voterArgs;

        public LoopHandler passiveHandler;
        public Object[] passiveArgs;
    }

    private ManagedContext context;
    private Spec spec;

    public BinaryStarReactorBuilder(ManagedContext context) {
        this.context = context;
        this.spec = new Spec();
    }

    public BinaryStarReactorBuilder withMode(Mode mode) {
        spec.mode = mode;
        return this;
    }

    public BinaryStarReactorBuilder withLocalUrl(String local) {
        spec.local = local;
        return this;
    }

    public BinaryStarReactorBuilder withRemoteUrl(String remote) {
        spec.remote = remote;
        return this;
    }

    public BinaryStarReactorBuilder withHeartbeatInterval(long heartbeatInterval) {
        spec.heartbeatInterval = heartbeatInterval;
        return this;
    }

    public BinaryStarReactorBuilder withVoterSocket(Socket voter) {
        spec.voter = voter;
        return this;
    }

    public BinaryStarReactorBuilder withVoterSocket(String url) {
        Socket socket = context.buildSocket(SocketType.ROUTER)
            .bind(url);

        return withVoterSocket(socket);
    }

    public BinaryStarReactorBuilder withActiveHandler(LoopHandler activeHandler, Object... activeArgs) {
        spec.activeHandler = activeHandler;
        spec.activeArgs = activeArgs;
        return this;
    }

    public BinaryStarReactorBuilder withVoterHandler(LoopHandler voterHandler, Object... voterArgs) {
        spec.voterHandler = voterHandler;
        spec.voterArgs = voterArgs;
        return this;
    }

    public BinaryStarReactorBuilder withPassiveHandler(LoopHandler passiveHandler, Object... passiveArgs) {
        spec.passiveHandler = passiveHandler;
        spec.passiveArgs = passiveArgs;
        return this;
    }

    public BinaryStarReactor build() {
        assert spec.voter != null;
        assert spec.voterHandler != null;

        BinaryStarReactor reactor = new BinaryStarReactorImpl(context, spec.mode, spec.local, spec.remote);
        reactor.registerVoterSocket(spec.voter);
        reactor.setVoterHandler(spec.voterHandler, spec.voterArgs);
        reactor.setActiveHandler(spec.activeHandler, spec.activeArgs);
        reactor.setPassiveHandler(spec.passiveHandler, spec.passiveArgs);
        reactor.setHeartbeatInterval(spec.heartbeatInterval);

        return reactor;
    }

    public BinaryStarReactor start() {
        BinaryStarReactor reactor = build();
        reactor.start();

        return reactor;
    }
}
