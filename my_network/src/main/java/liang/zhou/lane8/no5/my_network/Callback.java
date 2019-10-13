package liang.zhou.lane8.no5.my_network;

public interface Callback {
    void onSuccess(byte result[]);

    /**
     * 连接服务器失败
     */
    void onFailure();
}
