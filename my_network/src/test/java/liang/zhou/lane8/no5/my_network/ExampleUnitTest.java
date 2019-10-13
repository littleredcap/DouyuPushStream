package liang.zhou.lane8.no5.my_network;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    public void myTest(String s){
        System.out.println("String");
    }
    public void myTest(Object o){
        System.out.println("Object");
    }

    @Test
    public void addition_isCorrect() {
        myTest(new Object());
    }
}