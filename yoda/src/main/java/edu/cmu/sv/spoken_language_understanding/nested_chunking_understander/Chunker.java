package edu.cmu.sv.spoken_language_understanding.nested_chunking_understander;

/**
 * Created by David Cohen on 12/29/14.
 */
public interface Chunker {
    public void chunk(ChunkingProblem chunkingProblem);
}