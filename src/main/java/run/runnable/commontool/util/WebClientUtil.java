package run.runnable.commontool.util;

import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * @author Asher
 * on 2023/10/2
 */
public class WebClientUtil {


    /**
     * 简单get请求
     * @param url
     * @return {@link Mono}<{@link String}>
     */
    public static Mono<String> simpleGetReq(String url){
        WebClient webClient = WebClient.builder()
                .clientConnector(
                    new ReactorClientHttpConnector(
                            HttpClient.create().proxyWithSystemProperties().responseTimeout(Duration.ofSeconds(10))
                    )
                )
                .build();
        return webClient
                .get()
                .uri(url)
                .accept(MediaType.ALL)
                .retrieve()
                .bodyToMono(String.class);
    }

}
