package run.runnable.commontool.util;

import reactor.core.publisher.Flux;

import java.util.Scanner;

public interface ConsoleUtil {

    /**
     * 获取用户终端输入
     * @param tips 提示
     * @return {@link String }
     */
    public static String getUserInput(String tips){
        Scanner scanner = new Scanner(System.in);
        System.out.println( tips );
        return scanner.nextLine();
    }

    /**
     * 在指定秒数后离开系统
     * @param second 秒
     * @author asher
     * @date 2023/10/19
     */
    public static void existSystem(Integer second){
        existSystem(second, "");
    }

    /**
     * 在指定秒数后离开系统，带一个提示
     *
     * @param second 秒
     * @param tips   提示
     * @author asher
     * @date 2023/10/19
     */
    public static void existSystem(Integer second, String tips){
        System.out.println(tips);
        assert second > 0;
        Flux.range(0, second)
                .sort((number1, number2) -> -number1.compareTo(number2))
                .subscribe(it -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("窗口将在"+ (it+1) +"秒后关闭");
                });
    }

}
