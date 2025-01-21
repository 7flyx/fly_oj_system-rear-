












public class Main {
    public static void main(String[] args) {
        int i = 0; // 作为分母的累加和
        double s = 0; // 累加和
        int cnt = 0; // 统计需要的项数
        while (s <= 3) { // s <=3时，循环继续
            cnt = cnt + 1; // 项数+1
            i = i + cnt; // 分母部分 累加上项数
            s = s + (1.0 / i); // 计算累加和
        }
    }
}
