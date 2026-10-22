package meme.registry;

/**
 * 模糊搜索打分：query 的字符按顺序出现在候选串中（子序列匹配）即得分，
 * 分数 = query 长度 / 最小匹配窗口长度（越紧凑分越高），不匹配为 0。
 * 行为近似 Rust fuzzy-matcher (skim)。
 */
final class FuzzySearch {

    private FuzzySearch() {
    }

    /** 返回 (0,1] 的得分；不匹配返回 0。 */
    static double score(String candidate, String query) {
        if (candidate == null || query == null || query.isEmpty()) {
            return 0;
        }
        String c = candidate.toLowerCase(java.util.Locale.ROOT);
        String q = query.toLowerCase(java.util.Locale.ROOT);
        // 找包含 q 的最短窗口（滑动两次取最优：贪心前向 + 贪心后向）
        int best = shortestWindow(c, q);
        if (best < 0) {
            return 0;
        }
        return (double) q.length() / best;
    }

    private static int shortestWindow(String s, String q) {
        int forward = windowForward(s, q);
        int backward = windowBackward(s, q);
        if (forward < 0 && backward < 0) {
            return -1;
        }
        if (forward < 0) {
            return backward;
        }
        if (backward < 0) {
            return forward;
        }
        return Math.min(forward, backward);
    }

    private static int windowForward(String s, String q) {
        int qi = 0, start = -1, end = -1;
        for (int i = 0; i < s.length() && qi < q.length(); i++) {
            if (s.charAt(i) == q.charAt(qi)) {
                if (start < 0) {
                    start = i;
                }
                end = i;
                qi++;
            }
        }
        if (qi < q.length()) {
            return -1;
        }
        return end - start + 1;
    }

    private static int windowBackward(String s, String q) {
        int qi = q.length() - 1, start = -1, end = -1;
        for (int i = s.length() - 1; i >= 0 && qi >= 0; i--) {
            if (s.charAt(i) == q.charAt(qi)) {
                if (end < 0) {
                    end = i;
                }
                start = i;
                qi--;
            }
        }
        if (qi >= 0) {
            return -1;
        }
        return end - start + 1;
    }
}
