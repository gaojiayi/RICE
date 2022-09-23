import com.gaojy.rice.common.RiceThreadFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author gaojy
 * @ClassName test.java
 * @Description
 * @createTime 2022/08/18 22:30:00
 */
public class test {
    public static void main(String[] args) throws InterruptedException {
        final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3, new RiceThreadFactory("heartBeatToControllerThread"));
        executorService.execute(()->{
//            while (true){
                try {
                    System.out.println("====>>>");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//            }
        });

        Thread.sleep(3000);
        System.out.println("main stop");
    }
}
