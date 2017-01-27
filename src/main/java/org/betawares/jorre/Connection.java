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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * The class <code>Connection</code> contains the specific parameters required 
 * to establish a connection to a remote listening peer.
 * 
 */
public class Connection {

    private String host;
    private int port;
    private boolean ssl;
    private ProxyType proxyType;
    private String proxyHost;
    private int proxyPort;
    private String proxyUsername;
    private String proxyPassword;

    public Connection(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    public ProxyType getProxyType() {
        return proxyType;
    }

    public void setProxyType(ProxyType proxyType) {
        this.proxyType = proxyType;
    }

    public enum ProxyType {

        SOCKS("Socks"), HTTP("HTTP"), NONE("None");

        private final String text;

        ProxyType(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

        public static ProxyType valueByText(String value) {
            for (ProxyType type : values()) {
                if (type.text.equals(value)) {
                    return type;
                }
            }
            return NONE;
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isSSL() {
        return this.ssl;
    }

    public void setSSL(boolean ssl) {
        this.ssl = ssl;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public static InetAddress getLocalAddress() throws SocketException {
        for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements();) {
            NetworkInterface iface = interfaces.nextElement();
            if (iface.isLoopback()) {
                continue;
            }
            for (InterfaceAddress addr : iface.getInterfaceAddresses()) {
                if (addr != null) {
                    InetAddress iaddr = addr.getAddress();
                    if (iaddr != null && iaddr instanceof Inet4Address) {
                        return iaddr;
                    }
                }
            }
        }
        return null;
    }

}
