package run.runnable.commontool.util;

import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


/**
 * 快速创建一个WebClient
 *
 * @author asher
 * @date 2023/10/19
 */
public interface WebClientUtil {

    /**
     * 简单get请求
     *
     * @param url 网址
     * @return {@link Mono }<{@link String }>
     * @author asher
     * @date 2023/10/19
     */
    static Mono<String> simpleGetReq(String url){
        return WebClient.builder()
                .baseUrl(url).build()
                .get()
                .retrieve()
                .bodyToMono(String.class);
    }

    /**
     * 简单post请求
     *
     * @param url     网址
     * @param headers headers
     * @param body    body
     * @return {@link Mono }<{@link String }>
     * @author asher
     * @date 2023/10/19
     */
    static Mono<String> simplePostReq(String url, MultiValueMap<String, String> headers, String body){
        return WebClient.builder()
                .baseUrl(url)
                .defaultHeaders(it -> {
                    it.addAll(headers);
                })
                .build()
                .post()
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(String.class);
    }

    /**
     * 带header的简单get请求
     *
     * @param url     网址
     * @param headers headers
     * @return {@link Mono }<{@link String }>
     * @author asher
     * @date 2023/10/19
     */
    static Mono<String> simpleGetReqWithHeader(String url, MultiValueMap<String, String> headers){
        return WebClient.builder()
                .defaultHeaders(it -> {
                    it.addAll(headers);
                })
                .baseUrl(url).build()
                .get()
                .retrieve()
                .bodyToMono(String.class);
    }

    /**
     * 带header的简单get请求
     *
     * @param url    网址
     * @param header header
     * @param values header values
     * @return {@link Mono }<{@link String }>
     * @author asher
     * @date 2023/10/19
     */
    static Mono<String> simpleGetReqWithHeader(String url, String header, String... values){
        return WebClient.builder()
                .defaultHeader(header, values)
                .baseUrl(url).build()
                .get()
                .retrieve()
                .bodyToMono(String.class);
    }

}
