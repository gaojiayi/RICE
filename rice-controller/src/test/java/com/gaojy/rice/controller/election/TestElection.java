package com.gaojy.rice.controller.election;

import com.alipay.sofa.jraft.entity.PeerId;
import com.gaojy.rice.controller.config.ControllerConfig;
import java.io.File;
import java.io.FileNotFoundException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author gaojy
 * @ClassName TestElection.java
 * @Description 
 * @createTime 2022/01/13 23:23:00
 */
@RunWith(JUnit4.class)
public class TestElection {

    @Test
    public void electionTest() throws InterruptedException {

        ControllerConfig config = new ControllerConfig();
        config.setController_election_port(8081);
        config.setAllControllerAddressStr("127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083");
        config.setElectionDataPath("/tmp/server" + config.getController_election_port());
        RiceElectionManager manager1 = new RiceElectionManager(config, new LeaderStateListener() {
            @Override
            public void onLeaderStart(long leaderTerm) {
            }

            @Override
            public void onLeaderStop(long leaderTerm) {

            }
        });
        manager1.start();

        config.setController_election_port(8082);
        config.setElectionDataPath("/tmp/server" + config.getController_election_port());
        RiceElectionManager manager2 = new RiceElectionManager(config, new LeaderStateListener() {
            @Override
            public void onLeaderStart(long leaderTerm) {
            }

            @Override
            public void onLeaderStop(long leaderTerm) {

            }
        });
        manager2.start();

        config.setController_election_port(8083);
        config.setElectionDataPath("/tmp/server" + config.getController_election_port());
        RiceElectionManager manager3 = new RiceElectionManager(config, new LeaderStateListener() {
            @Override
            public void onLeaderStart(long leaderTerm) {
            }

            @Override
            public void onLeaderStop(long leaderTerm) {

            }
        });
        manager3.start();

        Thread.sleep(1000 * 3);
        Assert.assertTrue(manager1.isLeader() | manager2.isLeader() | manager3.isLeader());
        manager1.stopElection();
        manager2.stopElection();
        manager3.stopElection();

    }

    @AfterClass
    public static void clearFile() throws Exception {
        deletefile("/tmp/server8081");
        deletefile("/tmp/server8082");
        deletefile("/tmp/server8083");
    }

    private static boolean deletefile(String delpath) throws Exception {
        try {

            File file = new File(delpath);
            // 当且仅当此抽象路径名表示的文件存在且 是一个目录时，返回 true
            if (!file.isDirectory()) {
                file.delete();
            } else if (file.isDirectory()) {
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File delfile = new File(delpath + File.separator + filelist[i]);
                    if (!delfile.isDirectory()) {
                        delfile.delete();
                        System.out.println(delfile.getAbsolutePath() + "删除文件成功");
                    } else if (delfile.isDirectory()) {
                        deletefile(delpath + File.separator + filelist[i]);
                    }
                }
                System.out.println(file.getAbsolutePath() + "删除成功");
                file.delete();
            }

        } catch (FileNotFoundException e) {
            System.out.println("deletefile() Exception:" + e.getMessage());
        }
        return true;
    }
}
