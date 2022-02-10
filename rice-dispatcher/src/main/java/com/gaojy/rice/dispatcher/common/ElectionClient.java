package com.gaojy.rice.dispatcher.common;

import com.alipay.sofa.jraft.RouteTable;
import com.alipay.sofa.jraft.conf.Configuration;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.option.CliOptions;
import com.alipay.sofa.jraft.rpc.impl.cli.CliClientServiceImpl;
import com.gaojy.rice.dispatcher.RiceDispatchScheduler;
import java.util.concurrent.TimeoutException;

/**
 * @author gaojy
 * @ClassName ElectionClient.java
 * @Description TODO
 * @createTime 2022/02/09 15:13:00
 */
public class ElectionClient {
    private final RiceDispatchScheduler riceDispatchScheduler;
    final String groupId;
    final String confStr;
    final Configuration conf = new Configuration();
    final CliClientServiceImpl cliClientService = new CliClientServiceImpl();

    public ElectionClient(RiceDispatchScheduler riceDispatchScheduler) {
        this.riceDispatchScheduler = riceDispatchScheduler;
        groupId = riceDispatchScheduler.getDispatcherConfig().ELECTION_GROUP_ID;
        confStr = riceDispatchScheduler.getDispatcherConfig().getAllControllerAddressStr();
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
        return leader.getEndpoint().getIp() + ":" + leader.getEndpoint().getPort();
    }

    public void close(){
        cliClientService.shutdown();
    }
}
