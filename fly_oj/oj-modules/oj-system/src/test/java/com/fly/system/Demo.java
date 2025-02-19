package com.fly.system;

import java.util.HashSet;
import java.util.LinkedList;

public class Demo {
    static class Solution {
        public String minWindow(String s, String t) {
            if (s == null || t == null || s.length() < t.length()) return "";

            //int[] cntS = new int[128];
            int[] cnt = new int[128];
            char[] chT = t.toCharArray();
            HashSet<Character> set = new HashSet<>();
            int n = chT.length;
            for (int i = 0; i < n; i++) {
                cnt[chT[i]]++;
                set.add(chT[i]);
            }
            String ans = "";
            char[] chS = s.toCharArray();
            LinkedList<Integer> q = new LinkedList<>();
            for (int i = 0; i < chS.length; ) {
                if (cnt[chS[i]] > 0) {
                    cnt[chS[i]] --;
                    n--;
                }

                if (n == 0) { // t字符全部用完了，收集答案
                    if (ans.length() == 0 || ans.length() > q.size()) {
                        ans = s.substring(q.peekFirst(), q.peekLast() + 1);
                    }
                    int j = q.pollFirst();
                    if (set.contains(chS[j])) {
                        cnt[chS[j]]++;
                        n++;
                    }
                }
                // 当前收集到的答案的长度，已经短于 窗口内的长度，直接收紧窗口
                if(s.length() != 0 && q.size() > 0 &&  s.length() > i - q.peekFirst()) {
                    int j = q.pollFirst();
                    if (set.contains(chS[j])) {
                        cnt[chS[j]]++;
                        n++;
                    }
                } else {
                    q.addLast(i);
                    i++;
                }
            }
            return ans;
        }
    }

    public static void main(String[] args) {
        // 测试用例
        Solution s = new Solution();
        String s1 = s.minWindow("ADOBECODEBANC", "ABC");
        System.out.println(s1);

    }
}
