package com.gaojy.rice.processor.api.common;

import com.alipay.sofa.jraft.RouteTable;
import com.alipay.sofa.jraft.conf.Configuration;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.option.CliOptions;
import com.alipay.sofa.jraft.rpc.impl.cli.CliClientServiceImpl;
import com.gaojy.rice.processor.api.config.ProcessorConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * @author gaojy
 * @ClassName ElectionClient.java
 * @Description
 * @createTime 2022/02/09 15:13:00
 */
public class ElectionClient {
    final String groupId;
    final String confStr;
    final Configuration conf = new Configuration();
    final CliClientServiceImpl cliClientService = new CliClientServiceImpl();

    public ElectionClient(ProcessorConfig processorConfig) {
        groupId = processorConfig.ELECTION_GROUP_ID;
        final List<String> confServerList = new ArrayList<>();
        /* raft 端口默认是业务端口  -2 */
        processorConfig.getControllerServerList().stream().forEach(server -> {
            confServerList.add(server.split(":")[0] + ":" + (Integer.parseInt(server.split(":")[1]) - 2));
        });
        confStr = confServerList.stream().collect(Collectors.joining(","));

        if (!conf.parse(confStr)) {
            throw new IllegalArgumentException("Fail to parse conf:" + confStr);
        }
        RouteTable.getInstance().updateConfiguration(groupId, conf);
        cliClientService.init(new CliOptions());
    }

    public String getMasterController() throws InterruptedException, TimeoutException {
        if (!RouteTable.getInstance().refreshLeader(cliClientService, groupId, 1000).isOk()) {
            throw new IllegalStateException("Refresh leader failed");
        }
        final PeerId leader = RouteTable.getInstance().selectLeader(groupId);
        return leader.getEndpoint().getIp() + ":" + (leader.getEndpoint().getPort() + 2);
    }

    public void close() {
        cliClientService.shutdown();
    }
}
