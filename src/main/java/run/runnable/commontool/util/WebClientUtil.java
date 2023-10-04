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
                            HttpClient.create().responseTimeout(Duration.ofSeconds(60))
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


    /**
     * 简单get请求
     * @param url
     * @return {@link Mono}<{@link String}>
     */
    public static Mono<String> simpleGetReq(String url, boolean useDefaultProxy){
        WebClient webClient = WebClient.builder()
                .clientConnector(
                        new ReactorClientHttpConnector(
                                useDefaultProxy ?  getHttpClientWithProxy() : getHttpClient()
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

    protected static HttpClient getHttpClientWithProxy(){
        return HttpClient.create()
                .proxyWithSystemProperties()
                .responseTimeout(Duration.ofSeconds(10));
    }

    protected static HttpClient getHttpClient(){
        return HttpClient.create()
                .responseTimeout(Duration.ofSeconds(10));
    }

}
