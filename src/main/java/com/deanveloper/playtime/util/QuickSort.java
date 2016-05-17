package com.deanveloper.playtime.util;

import java.util.List;
import java.util.UUID;

/**
 * Quick sort algorithm
 *
 * @author Lars Vogel
 */
public class QuickSort  {
    private List<Integer> times;
    private List<String> names;
    private List<UUID> ids;

    public void sort(List<String> names, List<UUID> ids, List<Integer> times) {
        // check for empty or null array
        if (times == null || times.size() == 0){
            return;
        }
        this.times = times;
        quicksort(0, times.size() - 1);
    }

    private void quicksort(int low, int high) {
        int i = low, j = high;
        // Get the pivot element from the middle of the list
        int pivot = times.get(low + (high-low)/2);

        // Divide into two lists
        while (i <= j) {
            // If the current value from the left list is smaller then the pivot
            // element then get the next element from the left list
            while (times.get(i) < pivot) {
                i++;
            }
            // If the current value from the right list is larger then the pivot
            // element then get the next element from the right list
            while (times.get(j) > pivot) {
                j--;
            }

            // If we have found a values in the left list which is larger then
            // the pivot element and if we have found a value in the right list
            // which is smaller then the pivot element then we exchange the
            // values.
            // As we are done we can increase i and j
            if (i <= j) {
                exchange(i, j);
                i++;
                j--;
            }
        }
        // Recursion
        if (low < j)
            quicksort(low, j);
        if (i < high)
            quicksort(i, high);
    }

    private void exchange(int i, int j) {
        int temp = times.get(i);
        times.set(i, times.get(j));
        times.set(j, temp);

        UUID tempId = ids.get(i);
        ids.set(i, ids.get(j));
        ids.set(j, tempId);

        String tempName = names.get(i);
        names.set(i, names.get(j));
        names.set(j, tempName);
    }
}
