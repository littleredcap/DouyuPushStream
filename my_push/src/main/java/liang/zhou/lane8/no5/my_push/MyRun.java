package liang.zhou.lane8.no5.my_push;

import android.net.sip.SipSession;

import java.util.Arrays;
import java.util.Stack;
import java.util.logging.Handler;

public class MyRun {

    private int x;
    private int y;

    private static void sort(int samples[], int low, int upper) {
        int pivot = samples[low];
        int i = low;
        int j = upper;
        while (i < j) {
            if (samples[j] > pivot) {
                j--;
                continue;
            }
            if (samples[i] < pivot) {
                i++;
                continue;
            }
            int temp = samples[i];
            samples[i] = samples[j];
            samples[j] = temp;
        }
        int low_in_lower = 0;
        if (i - 1 > low) {
            sort(samples, low_in_lower, i - 1);
        }
        int upper_in_upper = samples.length - 1;
        if (j + 1 < upper) {
            sort(samples, j + 1, upper_in_upper);
        }
    }

    public static int[] qSort(int arr[], int start, int end) {
        int pivot = arr[start];
        int i = start;
        int j = end;
        while (i < j) {
            while ((i < j) && (arr[j] > pivot)) {
                j--;
            }
            while ((i < j) && (arr[i] < pivot)) {
                i++;
            }
            if ((arr[i] == arr[j]) && (i < j)) {
                i++;
            } else {
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        if (i - 1 > start) arr = qSort(arr, start, i - 1);
        if (j + 1 < end) arr = qSort(arr, j + 1, end);
        return (arr);
    }

    private static void bubble(int samples[]) {
        for (int i = 0; i < samples.length; i++) {
            for (int j = 0; j < samples.length - i - 1; j++) {
                if (samples[j] > samples[j + 1]) {
                    int temp = samples[j + 1];
                    samples[j + 1] = samples[j];
                    samples[j] = temp;
                }
            }
        }
    }

    private static void insertion(int samples[]) {
        for (int i = 0; i < samples.length - 1; i++) {
            int key = samples[i + 1];
            int index = i;
            while (index >= 0 && key < samples[index]) {
                samples[index + 1] = samples[index];
                index--;
            }
            samples[index + 1] = key;
        }
    }

    private static void selection(int samples[]) {

        for (int j = 0; j < samples.length; j++) {
            int min = samples[j];
            for (int i = j; i < samples.length-1; i++) {
                if (samples[i+1] < min) {
                    min = samples[i+1];
                    samples[i+1]=samples[j];
                    samples[j]=min;
                }
            }
        }

    }

    public static void quick(int samples[],int low,int upper){
        int pivot=samples[low];
        int i=low;
        int j=upper;
        while(i<j){
            if(samples[i]<pivot){
                i++;
                continue;
            }
            if(samples[j]>pivot){
                j--;
                continue;
            }

            int temp=samples[i];
            samples[i]=samples[j];
            samples[j]=temp;
        }

        if(i-1>low) {
            quick(samples, low, i-1);
        }
        if (j + 1 < upper) {
            quick(samples, j + 1, upper);
        }
    }

    public static void main(String[] args) {
        /*MyRun r1 = new MyRun();
        int sample[] = {22, 5, -6, 17, 112, 15, 102, 4, 2, 98,-88,16};
        long start = System.currentTimeMillis();
        quick(sample,0,sample.length-1);
        long end = System.currentTimeMillis();
        long duration = end - start;
        System.out.println(Arrays.toString(sample) + "duration:" + duration);*/
        int a[]=new int[10];
        System.out.println(a);

    }
}
