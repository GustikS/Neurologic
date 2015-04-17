package discoverer;

import java.util.*;

public class Chunks {
    private Set<Object> chunkStore;
    private static int headerSize = 2;
    private static int partSize = 3;

    public Chunks() {
        chunkStore = new HashSet<Object>();
    }

/*
 *    @Override
 *    public String toString() {
 *        String string = "";
 *        for(IntArray chunk: chunkStore)
 *            string += chunk + "\n";
 *
 *        return string;
 *    }
 */

    private static final int[][] splitArrayToChunks(int partSize, int[] array) {
        /* [1|1,2,3,4,5,6,7,8,9] -> [1,1|1,2,3],[1,2|4,5,6],[1,3|7,8,9] */
        int id = array[0];                                                /* first int is id of literal */
        int bodyLen = array.length - 1;                                   /* array minus id */
        int totalPartsCount = (int) Math.ceil((double) bodyLen/partSize); /* number of created parts */
        int fullFilledPartsCount = bodyLen/partSize;                      /* number of fullfilled parts */
        int lastPartSize = bodyLen - (fullFilledPartsCount) * partSize;   /* size of last part */
        int[][] parts = new int[totalPartsCount][partSize + headerSize];

        /* split long array into fullfilled parts */
        for (int i = 0; i < fullFilledPartsCount; i++) {
            parts[i][0] = id;
            parts[i][1] = i;
            /* split the long array, the +1 means, that we are not copying the id, thus everything is shifted */
            System.arraycopy(array, (1 + i * partSize), parts[i], headerSize, partSize);
        }

        /* check if there are some trailing ints and create last chunk if necessary */
        if (totalPartsCount != fullFilledPartsCount) {
            parts[fullFilledPartsCount][0] = id;
            parts[fullFilledPartsCount][1] = fullFilledPartsCount;
            /* split the long array, the +1 means, that we are not copying the id, thus everything is shifted */
            System.arraycopy(array, (1 + fullFilledPartsCount * partSize), parts[fullFilledPartsCount], headerSize, lastPartSize);
        }

        return parts;
    }

    private static final IntArray[] jokeChunk(int[] array, int jokers) {
        /* [1,1|1,2,3] -> [1,1|*,2,3], [1,1|1,*,3], [1,1|1,2,*] */
        int[][] indicesToJoke = Combinations.combinations(array.length - headerSize, jokers);
        IntArray[] completedChunks = new IntArray[indicesToJoke.length];

        for (int i = 0; i < indicesToJoke.length; i++) {
            IntArray completedChunk = new IntArray(array, false);
            completedChunk.joke(indicesToJoke[i]);
            completedChunks[i] = completedChunk;
        }

        return completedChunks;
    }

    public void insert(int[] literal) {
        if (Global.debugEnabled) System.out.println("Inserting literal:\t" + literal);
        /* [1|1,2,3,4,5,6,7,8,9...] -> iserted into db */
        int[][] chunksWithoutJokers = splitArrayToChunks(partSize, literal);

        /* for each chunk still without jokers */
        for (int i = 0; i < chunksWithoutJokers.length; i++) {
            /* for each possible count of jokers of given chunk */
            for (int j = 0; j <= chunksWithoutJokers[i].length - headerSize; j++) {
                IntArray[] chunksWithJokers = jokeChunk(chunksWithoutJokers[i], j);
                /* for each possible chunk now with all jokers */
                for (int k = 0; k < chunksWithJokers.length; k++) {
                    //System.out.println(chunksWithJokers[k]);
                    chunkStore.add(chunksWithJokers[k]);
                }
            }
        }
    }

    public boolean containsNG(List<Integer> query) {
        List<QueryArray> ch = splitArrayToChunksNG(partSize, query);

        for (QueryArray qa: ch)
            if (!chunkStore.contains(qa))
                return false;

        return true;
    }

    private static final List<QueryArray> splitArrayToChunksNG(int partSize, List<Integer> array) {
        /* [1|1,2,3,4,5,6,7,8,9] -> [1,1|1,2,3],[1,2|4,5,6],[1,3|7,8,9] */
        int id = array.get(0);
        int bodyLen = array.size() - 1;                                   /* array minus id */
        int totalPartsCount = (int) Math.ceil((double) bodyLen/partSize); /* number of created parts */
        int fullFilledPartsCount = bodyLen/partSize;                      /* number of fullfilled parts */
        int lastPartSize = bodyLen - (fullFilledPartsCount) * partSize;   /* size of last part */
        //int[][] parts = new int[totalPartsCount][partSize + headerSize];
        List<QueryArray> parts = new ArrayList<QueryArray>();

        /* split long array into fullfilled parts */
        for (int i = 0; i < fullFilledPartsCount; i++) {
            QueryArray q = new QueryArray();
            q.add(id);
            q.add(i);
            q.addAll(array.subList(1+i*partSize,1+i*partSize+partSize));
            parts.add(q);

            /* split the long array, the +1 means, that we are not copying the id, thus everything is shifted */
            //System.arraycopy(array, (1 + i * partSize), parts[i], headerSize, partSize);
        }

        /* check if there are some trailing ints and create last chunk if necessary */
        if (totalPartsCount != fullFilledPartsCount) {
            QueryArray q = new QueryArray();
            q.add(id);
            q.add(fullFilledPartsCount);
            q.addAll(array.subList(1+fullFilledPartsCount*partSize,1+fullFilledPartsCount*partSize+lastPartSize));

            while (q.size() != partSize + headerSize)
                q.add(0);

            parts.add(q);

            /* split the long array, the +1 means, that we are not copying the id, thus everything is shifted */
            //System.arraycopy(array, (1 + fullFilledPartsCount * partSize), parts[fullFilledPartsCount], headerSize, lastPartSize);
        }

        return parts;
    }

    public boolean contains(int[] query) {
        int[][] queryChunks = splitArrayToChunks(partSize, query);

        /* each chunk is checked against database */
        for (int i = 0; i < queryChunks.length; i++) {
            IntArray chunk = new IntArray(queryChunks[i], true);
            if (!chunkStore.contains(chunk))
                return false;
        }

        return true;
    }
}
