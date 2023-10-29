package run.runnable.commontool.util;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * cmd 命令实用程序
 *
 * @author asher
 * @date 2023/10/21
 */
public interface CmdCommandUtil {


    public static String CMD_GIT_BRANCH_SHOW = "git branch --show-current;";


    public static String executeGitBash(String cmdCommand){
        return execute("\"C:\\Program Files\\Git\\bin\\bash.exe\" -c " + "\"" + cmdCommand + "\"");
    }

    public static InputStream executeGitBashNeedStream(String cmdCommand){
        return executeNeedStream("\"C:\\Program Files\\Git\\bin\\bash.exe\" -c " + "\"" + cmdCommand + "\"");
    }


    @SneakyThrows
    public static InputStream executeNeedStream(String command) {
        Process process = Runtime.getRuntime().exec(command);
        return process.getInputStream();
    }

    @SneakyThrows
    public static String execute(String command) {
        StringBuilder stringBuilder = new StringBuilder();
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), "GBK"));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        return stringBuilder.toString();
    }

    @SneakyThrows
    public static String execute(String... command) {
        final Stream<String> commandStream = Arrays.stream(command);

        ProcessBuilder processBuilder = new ProcessBuilder(
                Stream.concat(Stream.of("sh", "-c"), commandStream).toList()
        );
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }
        return result.toString().trim();
    }

}
