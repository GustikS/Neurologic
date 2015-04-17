package discoverer;

public class Combinations {
    private static int maxCombinations = 200;

    /** Empty private constructor to avoid constructing */
    private Combinations() {}

    /** Indices for n = 3, k = 0 */
    private static final int[][] c30 = {{}};

    /** Indices for n = 3, k = 1 */
    private static final int[][] c31 = {{0},{1},{2}};

    /** Indices for n = 3, k = 2 */
    private static final int[][] c32 = {{0,1},{0,2},{1,2}};

    /** Indices for n = 3, k = 3 */
    private static final int[][] c33 = {{0,1,2}};

    /** Method for returning all combinations of indices. First, it tries to
     * return saved results, if fails it will compute new values.
     *
     * @param n length of array
     * @param k how many indices
     * @return all combinations of indices
     */
    public static final int[][] combinations(int n, int k) {
        if (n == 3) {
            if (k == 0) return c30;
            if (k == 1) return c31;
            if (k == 2) return c32;
            if (k == 3) return c33;
        }

        return compute(n, k);
    }

    /** Method for manual computation of combinations of indices.
     *
     * @param n length of array
     * @param k how many indices
     * @return all combinations of indices
     */
    private static final int[][] compute(int n, int k) {
        /* Algorithm for computation all combinations is taken from D. E. Knuth book */
        int combinations = maxCombinations; /* maximal possible combinations */
        int[][] result = new int[combinations][k];    /* initialization of array with ^ */
        int cnt = 0;                                  /* combinations counter */
        int[] c = new int[k + 1 + 2];

        for (int j = 1; j < k + 1; j++)
            c[j] = j - 1;

        c[k + 1] = n;
        c[k + 2] = 0;

        int j = 0;
        for (; j <= k; c[j]++) {
            System.arraycopy(c, 1, result[cnt++], 0, k);

            for (j = 1; c[j] + 1 == c[j + 1]; j++)
                c[j] = j - 1;
        }

        /* shrink result into array which fits exactly */
        int[][] fitResult = new int[cnt][k];
        System.arraycopy(result, 0, fitResult, 0, cnt);

        return fitResult;
    }
}
