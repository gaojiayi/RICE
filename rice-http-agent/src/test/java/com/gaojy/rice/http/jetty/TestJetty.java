package com.gaojy.rice.http.jetty;

import com.alibaba.fastjson.JSON;
import com.gaojy.rice.http.api.HttpResponse;
import com.gaojy.rice.http.api.HttpServer;
import org.apache.http.Consts;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.io.IOException;
import java.util.Map;

@RunWith(JUnit4.class)
public class TestJetty {
    static HttpServer server;

    @BeforeClass
    public static void startServer() {
        JettyHttpBinder jettyHttpBinder = new JettyHttpBinder();
        jettyHttpBinder.addHttpHandler("/A", (request) -> {
            Map<String, Object> paramMap = request.getParamMap();
            paramMap.forEach((k, v) -> {
                v = v + "/A";
                paramMap.put(k, v);
            });
            HttpResponse httpResponse = new HttpResponse(paramMap);
            return httpResponse;
        });

        jettyHttpBinder.addHttpHandler("/A/B", (request) -> {
            Map<String, Object> paramMap = request.getParamMap();
            paramMap.forEach((k, v) -> {
                v = v + "/A/B";
                paramMap.put(k, v);
            });
            HttpResponse httpResponse = new HttpResponse(paramMap);
            return httpResponse;
        });
        jettyHttpBinder.addHttpHandler("/C", (request) -> {
            Map<String, Object> paramMap = request.getParamMap();
            paramMap.forEach((k, v) -> {
                v = v + "/C";
                paramMap.put(k, v);
            });
            HttpResponse httpResponse = new HttpResponse(paramMap);
            return httpResponse;
        });
        HttpServer server = jettyHttpBinder.bind(8091);
    }

    @Test
    public void jettyMutilHandlerTest() throws IOException {
        String response = Request.Post("http://localhost:8091/rice/A")
                .bodyForm(Form.form().add("data", "data").build())
                .execute().returnContent().asString(Consts.UTF_8);
        Assert.assertEquals(response, JSON.parseObject(response).get("data").toString(), "{\"data\":\"data/A\"}");

        response = Request.Get("http://localhost:8091/rice/A/B?data=data")
                .execute().returnContent().asString(Consts.UTF_8);
        Assert.assertEquals(response, JSON.parseObject(response).get("data").toString(), "{\"data\":\"data/A/B\"}");

        response = Request.Post("http://localhost:8091/rice/C")
                .bodyString("{\"data\":\"data\"}", ContentType.APPLICATION_JSON)
                .execute().returnContent().asString(Consts.UTF_8);
        Assert.assertEquals(response, JSON.parseObject(response).get("data").toString(), "{\"data\":\"data/C\"}");

    }

    @Test
    public void jettyJsonPostTest() throws IOException {
        String response = Request.Post("http://localhost:8091/rice/A")
                .bodyString("{\"data\":\"data\"}", ContentType.APPLICATION_JSON)
                .execute().returnContent().asString(Consts.UTF_8);

        Assert.assertEquals(response, JSON.parseObject(response).get("data").toString(), "{\"data\":\"data/A\"}");

    }

    @Test
    public void jettyGetTest() throws IOException {
        String response = Request.Get("http://localhost:8091/rice/A?data=data")
                .execute().returnContent().asString(Consts.UTF_8);
        Assert.assertEquals(response, JSON.parseObject(response).get("data").toString(), "{\"data\":\"data/A\"}");

    }

    public static void closeServer() {
        server.close();
    }

}
