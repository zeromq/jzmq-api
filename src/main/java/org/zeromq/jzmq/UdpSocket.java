package org.zeromq.jzmq;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Socket for sending UDP messages.
 * <p>
 * This class was adapted from <code>ZreUdp</code> in the
 *   <a href="https://github.com/zeromq/jyre/blob/master/src/main/java/org/zyre/ZreUdp.java">Jyre</a>
 * project.
 */
public class UdpSocket {
    private static final byte[] BIND_ADDRESS = new byte[] {0, 0, 0, 0};
    private static final String BROADCAST_HOST = "255.255.255.255";

    private DatagramChannel handle;     // Channel for send/recv
    private DatagramSocket socket;      // Socket for send/recv
    private int port;                   // UDP port number we work on
    private InetAddress address;        // Own address
    private SocketAddress broadcast;    // Broadcast address
    private SocketAddress sender;       // Where last recv came from
    private String host;                // Our own address as string
    private String from;                // Sender address of last message

    /**
     * Constructor.
     *
     * @param port The local port to bind to
     */
    public UdpSocket(int port) throws IOException {
        this.port = port;

        initialize();
    }

    private void initialize() throws IOException {
        // Create UDP socket
        this.handle = DatagramChannel.open();
        this.socket = handle.socket();
        this.broadcast = new InetSocketAddress(InetAddress.getByName(BROADCAST_HOST), port);

        // Configure as non-blocking
        handle.configureBlocking(false);

        // Ask operating system to let us do broadcasts from socket
        socket.setBroadcast(true);

        // Allow multiple processes to bind to socket; incoming
        // messages will come to each process
        socket.setReuseAddress(true);

        // Find the applicable local address
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface networkInterface : Collections.list(interfaces)) {
            if (networkInterface.isLoopback()) {
                continue;
            }

            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            for (InetAddress address : Collections.list(addresses)) {
                if (address instanceof Inet4Address) {
                    this.address = address;
                    this.host = address.getHostAddress();
                    break;
                }
            }
        }

        socket.bind(new InetSocketAddress(InetAddress.getByAddress(BIND_ADDRESS), port));
    }

    /**
     * Close the socket.
     */
    public void close() {
        try {
            handle.close();
        } catch (IOException ignored) {
        }
    }

    /**
     * Return the UDP channel.
     *
     * @return The UDP channel
     */
    public DatagramChannel getChannel() {
        return handle;
    }

    /**
     * Return the UDP socket.
     * 
     * @return The UDP socket
     */
    public DatagramSocket getSocket() {
        return socket;
    }

    /**
     * Return our own SocketAddress.
     * 
     * @return Our own address
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Return our own IP address as printable string.
     *
     * @return Our own IP address
     */
    public String getHost() {
        return host;
    }

    /**
     * Return the UDP socket port.
     *
     * @return The UDP socket port
     */
    public int getPort() {
        return port;
    }

    /**
     * Return the SocketAddress of the peer that sent last message.
     * 
     * @return The address of last peer
     */
    public SocketAddress getSender() {
        return sender;
    }

    /**
     * Return the IP address of peer that sent last message.
     *
     * @return The IP address of last peer
     */
    public String getFrom() {
        return from;
    }

    /**
     * Send a message using UDP broadcast.
     *
     * @param buffer The message as a byte buffer
     * @throws IOException If an error occurs sending the message
     */
    public void send(ByteBuffer buffer) throws IOException {
        handle.send(buffer, broadcast);
    }

    /**
     * Send a message using UDP.
     * 
     * @param buffer The message as a byte buffer
     * @param address The host name or address of the server
     * @throws IOException If an error occurs sending the message
     */
    public void send(ByteBuffer buffer, String address) throws IOException {
        SocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName(address), port);
        handle.send(buffer, socketAddress);
    }

    /**
     * Receive a message from UDP broadcast.
     *
     * @param buffer The buffer to place received data into
     * @return The size of received message, or -1
     * @throws IOException If an error occurs receiving the message
     */
    public int receive(ByteBuffer buffer) throws IOException {
        int read = -1;
        int remaining = buffer.remaining();

        sender = handle.receive(buffer);
        if (sender != null) {
            from = ((InetSocketAddress) sender).getAddress().getHostAddress();
            read = remaining - buffer.remaining();
        }

        return read;
    }
}
