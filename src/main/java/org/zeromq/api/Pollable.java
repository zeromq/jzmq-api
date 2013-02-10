package org.zeromq.api;

import java.util.EnumSet;

public interface Pollable {

    Socket getSocket();

    EnumSet<PollerType> getOptions();
}
