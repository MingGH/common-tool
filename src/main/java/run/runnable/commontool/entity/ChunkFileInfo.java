package run.runnable.commontool.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Asher
 * on 2023/11/14
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChunkFileInfo {

    private long startOffset;
    private long endOffset;
    private int chunkSize;

    private byte[] bytes;

}
