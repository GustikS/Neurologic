package discoverer;

import java.util.*;

public class IntArray {
    /** Value representing joker -- don't care when searching for a part */
    private static final int joker = -1;

    /** Main array */
    private int[] array;

    /** Hash value */
    private int hash;

    /** Pointer to next empty index */
    private int next;

    /** Constructor if whole array is provided
     *
     * @param newArray array of integers
     */
    public IntArray(int[] newArray, boolean shallow) {
        array = shallow ? newArray : newArray.clone();
        next = array.length;
        refreshHash();
    }

    /** Constructor if array is provided and index from which the array should
     * be used
     *
     * @param newArray array of integers
     * @param indexFrom index from which the array should be used
     */
    public IntArray(int[] newArray, int indexFrom) {
        int size = newArray.length - indexFrom;
        array = new int[size];
        System.arraycopy(newArray, indexFrom, array, 0, size);
        next = array.length;
        refreshHash();
    }

    @Override
    public int hashCode() {
        return hash;
    }

    /** Equality of two IntArray objects. Equal when arrays are identic.
     *
     * @param o IntArray object
     * @return true if two arrays are equal
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof IntArray)) return false;

        IntArray otherArray = (IntArray) o;

        for (int i = 0; i < array.length; i++)
            if (array[i] != otherArray.get(i)) return false;

        return true;
    }

    /** Display IntArray as a string.
     *
     * @return elements from array separated by "|"
     */
    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < next; i++)
            str += array[i] + "|";

        return str;
    }

    /** Replaces given indices in array with JOKER
     *
     * @param indices array of indices for replacement
     */
    public void joke(int[] indices) {
        for (int i = 0; i < indices.length; i++)
            array[indices[i] + 2] = joker; // 2 = headerSize
        refreshHash();
    }

    /**
     * Refresh hash value. Called when joked.
     */
    private void refreshHash() {
        hash = Arrays.hashCode(array);
    }

    /** Return element at given index
     *
     * @param index index of value to return
     * @return element at given index
     */
    public int get(int index) {
        return array[index];
    }

    public int size() {
        return next;
    }

    public int[] toArrayOfInts() {
        return array.clone();
    }
}
