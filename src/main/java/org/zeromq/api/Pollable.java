package org.zeromq.api;

import java.nio.channels.SelectableChannel;
import java.util.EnumSet;

public interface Pollable {

    Socket getSocket();

    SelectableChannel getChannel();

    EnumSet<PollerType> getOptions();
}
