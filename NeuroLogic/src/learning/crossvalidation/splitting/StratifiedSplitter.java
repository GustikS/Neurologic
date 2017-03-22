package learning.crossvalidation.splitting;

import learning.LearningSample;

import java.util.Collection;

/**
 * Created by gusta on 14.3.17.
 */
public class StratifiedSplitter implements SplittingStrategy {
    @Override
    public Collection<Collection<LearningSample>> splitIntoFolds(Iterable<LearningSample> samples) {
        return null;
    }
}
