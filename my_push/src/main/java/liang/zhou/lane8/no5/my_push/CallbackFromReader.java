package liang.zhou.lane8.no5.my_push;

public interface CallbackFromReader {

    /**
     *
     * @param data each frame captured from camera
     */
    void handleDataFromImageReader(byte data[]);
}
