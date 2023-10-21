package run.runnable.commontool.util;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.util.Iterator;
import java.util.function.Consumer;

public interface StreamUtil {

    public static <T> Iterable<T> getIterableFromIterator(Iterator<T> iterator)
    {
        return () -> iterator;
    }

    public static Flux<DataBuffer> readInputStream(InputStream inputStream){
        return DataBufferUtils.readInputStream(() -> inputStream, new DefaultDataBufferFactory(), 4096);
    }

    public static Consumer<DataBuffer> printDataBuffer() {
        return dataBuffer -> {
            // 在这里处理数据缓冲区
            byte[] bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            String content = new String(bytes);
            System.out.println(content);
        };
    }


}
