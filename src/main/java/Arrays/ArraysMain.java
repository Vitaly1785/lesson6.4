package Arrays;

import java.util.Arrays;

public class ArraysMain {

    public static int[] arraysRemake(int[] b){
        if(Arrays.toString(b).contains(String.valueOf(4))){
            for (int i = b.length -1; i >= 0 ; i--) {
                if(b[i] == 4){
                    int[] a = Arrays.copyOfRange (b, i+1, b.length);
                    return a;
                }
            }
        }else
            throw new RuntimeException("4 not found");
        return null;
    }

    public static boolean arrayTrueFalse(int[] b){
        if(Arrays.toString(b).contains(String.valueOf(4)) && Arrays.toString(b).contains(String.valueOf(1))){
            return true;
        }
        return false;
    }

}
