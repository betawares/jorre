/*
 * ISC License
 *
 * Copyright (c) 2016, Betawares
 *
 * Permission to use, copy, modify, and/or distribute this software for any 
 * purpose with or without fee is hereby granted, provided that the above 
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
 * REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY 
 * AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, 
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM 
 * LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR 
 * OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR 
 * PERFORMANCE OF THIS SOFTWARE.
 */

package org.betawares.jorre;

/**
 * The class {@code Connection} contains the specific settings required 
 * to establish a connection to a (@link Server).
 * 
 */
public class Connection {

    public static final long DEFAULT_MAX_RESPONSE_AGE = 5000;
    public static final long DEFAULT_IDLE_PING_TIME = 30000;
    public static final long DEFAULT_IDLE_TIMEOUT = 60000;
    public static final int DEFAULT_CONNECTION_TIMEOUT = 60000;
    
    private final String host;
    private final int port;
    private boolean ssl = false;
        
    private long maxResponseAge = DEFAULT_MAX_RESPONSE_AGE; 
    private long idlePingTime = DEFAULT_IDLE_PING_TIME;
    private long idleTimeout = DEFAULT_IDLE_TIMEOUT;
    private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
        
    /**
     * Creates a Connection object with default values for ssl, maxResponseAge, 
     * ildePingTime and idleTimeout.
     * 
     * @param host    the address of the server
     * @param port    the port that the server is listening on
     */
    public Connection(String host, int port) {
        this(host, port, false, DEFAULT_MAX_RESPONSE_AGE, DEFAULT_IDLE_PING_TIME, DEFAULT_IDLE_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
    }

    /**
     * 
     * @param host    the address of the server
     * @param port    the port that the server is listening on
     * @param ssl     if true ssl will be enabled
     * @param maxResponseAge
     *            number of milliseconds to wait for a {@link ClientResponse};
     *            after this time elapses a {@link CommunicationException} is generated
     * @param idlePingTime    
     *            number of milliseconds without communication from the {@link Server} 
     *            before sending a {@link Ping} message; 
     *            this value should be less than {@code idleTimeout}
     * @param idleTimeout
     *            number of milliseconds without communication from the {@link Server} 
     *            before it is disconnected
     * @param connectionTimeout
     *            number of milliseconds to attempt a connection to the {@link Server} 
     *            before it is abandoned
     */
    public Connection(String host, int port, boolean ssl, long maxResponseAge, long idlePingTime, long idleTimeout, int connectionTimeout) {
        this.host = host;
        this.port = port;
        this.ssl = ssl;
        this.maxResponseAge = maxResponseAge;
        this.idlePingTime = idlePingTime;
        this.idleTimeout = idleTimeout;
        this.connectionTimeout = connectionTimeout;
    }
    
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isSSL() {
        return this.ssl;
    }

    public void setSSL(boolean ssl) {
        this.ssl = ssl;
    }
    
    public long getMaxResponseAge() {
        return maxResponseAge;
    }

    public void setMaxResponseAge(long maxResponseAge) {
        this.maxResponseAge = maxResponseAge;
    }

    public long getIdlePingTime() {
        return idlePingTime;
    }

    public void setIdlePingTime(long idlePingTime) {
        this.idlePingTime = idlePingTime;
    }

    public long getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

}
