package com.gaojy.rice.dispatcher.scheduler.tasktype;

/**
 * @author gaojy
 * @ClassName RiceMapReduceTaskExecuter.java
 * @Description map reduce 任务执行器
 * @createTime 2022/02/14 13:55:00
 */
public class RiceMapReduceTaskExecuter  implements RiceExecuter{
    @Override
    public void execute(Long taskInstanceId) {
        // 先执行map
        // 后执行process
        // 再执行reduce

    }
}
