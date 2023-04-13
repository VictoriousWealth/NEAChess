package All.DevelopmentTools;

import java.util.ArrayList;

public class Sort {
    public static ArrayList<Integer> quickSort(ArrayList<Integer> collection) {
        if (collection.size() < 2) { // a pivot might be the last element in the arraylist
            // hence the rightCollection will be empty, hence size less than 2
            return collection;
        } else {

            int pivot = (int) (collection.size() * Math.random());
            ArrayList<Integer> leftCollection = new ArrayList<>();
            ArrayList<Integer> rightCollection = new ArrayList<>();
            for (int index = 0; index < collection.size(); index++) {
                if (collection.get(index) <= collection.get(pivot) && index != pivot) {
                    leftCollection.add(collection.get(index));

                } else if (collection.get(index) > collection.get(pivot)) {
                    rightCollection.add(collection.get(index));
                }
            }

            // to show process
            System.out.println(leftCollection);
            System.out.println(collection.get(pivot));
            System.out.println(rightCollection);


            int temp = collection.get(pivot);
            collection.clear();
            collection.addAll(quickSort(leftCollection));
            collection.add(temp);
            collection.addAll(quickSort(rightCollection));
            return collection;
        }
    }
}