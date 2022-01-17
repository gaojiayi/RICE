
package com.gaojy.rice.http.api;

import com.gaojy.rice.common.extension.SPI;

/**
 * http invocation handler.
 */
@SPI
public interface HttpGetHandler extends HttpHandler {

    public HttpResponse handler(HttpRequest request);

}