import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrencyStressTest {

    // 假设你的帝王蟹菜品 ID 是 2，你需要先用登录接口拿到一个合法的 JSESSIONID
    private static final String TARGET_URL = "http://localhost:8080/order/create?itemId=2&count=1";
    // 替换为你浏览器里登录后按 F12 看到的真实 Session ID
    private static final String COOKIE = "JSESSIONID=A1B2C3D4E5F6...";

    // 模拟并发的线程数
    private static final int THREAD_COUNT = 100;

    public static void main(String[] args) {
        // CountDownLatch 就像田径赛场的发令枪
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(THREAD_COUNT);

        // 建立一个 100 人的线程池
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        System.out.println("====== 准备就绪，100 位大妈即将冲入餐厅抢购 1 只帝王蟹 ======");

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    // 所有线程在这里阻塞，等待发令枪响，实现“绝对同时”并发
                    startSignal.await();

                    // 发起 HTTP POST 请求
                    URL url = new URL(TARGET_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Cookie", COOKIE);

                    // 读取服务器返回结果（下单成功 还是 库存不足）
                    InputStream is = conn.getInputStream();
                    byte[] bytes = new byte[is.available()];
                    is.read(bytes);
                    String response = new String(bytes, "UTF-8");

                    System.out.println(Thread.currentThread().getName() + " 战果: " + response);

                } catch (Exception e) {
                    System.out.println(Thread.currentThread().getName() + " 请求失败");
                } finally {
                    doneSignal.countDown(); // 线程执行完毕，报告完工
                }
            });
        }

        // 3, 2, 1... 砰！发令枪响，100 个请求同一时刻砸向 Tomcat
        startSignal.countDown();

        try {
            // 等待所有人跑完
            doneSignal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown();
        System.out.println("====== 抢购结束，请去数据库检查库存是否变为负数！ ======");
    }
}