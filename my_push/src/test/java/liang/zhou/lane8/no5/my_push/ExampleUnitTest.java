package liang.zhou.lane8.no5.my_push;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        int sample[]={2,5,6,10,12,15};
        int key=7;
        int low=0;
        int upper=sample.length-1;
        int index=-1;
        while(low<upper){
            int middle=(low+upper)/2;
            if(sample[middle]==key){
                index=middle;
            }else if(sample[middle]>key){
                upper=middle;
            }else{
                low=middle;
            }
        }
        System.out.println(index);
    }
}