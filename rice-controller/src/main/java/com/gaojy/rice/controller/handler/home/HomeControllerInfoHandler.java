package com.gaojy.rice.controller.handler.home;

import com.gaojy.rice.controller.RiceControllerBootStrap;
import com.gaojy.rice.controller.handler.AbstractHttpHandler;
import com.gaojy.rice.http.api.HttpRequest;
import com.gaojy.rice.http.api.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gaojy
 * @ClassName HomeControllerInfoHandler.java
 * @Description
 * @createTime 2022/11/19 21:18:00
 */
public class HomeControllerInfoHandler extends AbstractHttpHandler {

    /**
     * {
     * collectors: [
     * {
     * address: "150.23.10.1:8080",
     * isMaster: true,
     * isCurrent: false,
     * status: 0
     * },
     * {
     * address: "150.23.10.2:8080",
     * isMaster: false,
     * isCurrent: true,
     * status: 0
     * },
     * {
     * address: "150.23.10.3:8080",
     * isMaster: false,
     * isCurrent: false,
     * status: 0
     * },
     * {
     * address: "150.23.10.4:8080",
     * isMaster: false,
     * isCurrent: false,
     * status: 0
     * }
     * ],
     * start_time: "2022-01-23 12:45:16"
     * }
     */

    public HomeControllerInfoHandler(String path) {
        super(path);
    }

    @Override
    public HttpResponse handler(HttpRequest request) throws Exception {
        Map<String, Object> responseMap = new HashMap<>();
        final List<ControllerInfo> controllerInfoList = new ArrayList<>();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String leader = RiceControllerBootStrap.getController().getReplicatorManager().getLeader();

        Long bootTime = RiceControllerBootStrap.getController().getBootTime();
        String time = formatDate.format(bootTime);
        responseMap.put("start_time", time);

        final String localHost = RiceControllerBootStrap.getController().getControllerConfig().getLocalHost();
        final int localPort = RiceControllerBootStrap.getController().getControllerConfig().getControllerPort();

        String allControllerAddressStr = RiceControllerBootStrap.getController().getControllerConfig().getAllControllerAddressStr();
        Arrays.asList(allControllerAddressStr.split(",")).stream().forEach(endpoint -> {
            ControllerInfo controllerInfo = new ControllerInfo();
            controllerInfo.setAddress(endpoint);
            controllerInfo.setCurrent((localHost + ":" + localPort).equals(endpoint));
            controllerInfo.setMaster(leader.equals(endpoint));
            controllerInfo.setStatus(0);
            controllerInfoList.add(controllerInfo);
        });
        responseMap.put("collectors", controllerInfoList);
        return new HttpResponse(responseMap);
    }

    class ControllerInfo {

        String address;
        Boolean isMaster;
        Boolean isCurrent;
        Integer status;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Boolean getMaster() {
            return isMaster;
        }

        public void setMaster(Boolean master) {
            isMaster = master;
        }

        public Boolean getCurrent() {
            return isCurrent;
        }

        public void setCurrent(Boolean current) {
            isCurrent = current;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }
    }
}
