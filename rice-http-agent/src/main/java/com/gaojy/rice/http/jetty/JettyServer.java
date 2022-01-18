package com.gaojy.rice.http.jetty;

import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.http.api.HttpServer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.StdErrLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author gaojy
 * @ClassName JettyServer.java
 * @Description TODO
 * @createTime 2022/01/18 00:21:00
 */
public class JettyServer implements HttpServer {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.HTTP_AGENT_LOGGER_NAME);
    private volatile Boolean closed = Boolean.FALSE;
    private final Server server;

    public JettyServer(Server server) {
        Log.setLog(new StdErrLog());
        Log.getLog().setDebugEnabled(false);
        this.server = server;
        try {
            this.server.start();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to start jetty server, cause: "
                    + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        if (server != null) {
            try {
                server.stop();
                closed = Boolean.TRUE;
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }


    @Override
    public boolean isClosed() {
        return closed;
    }

}
