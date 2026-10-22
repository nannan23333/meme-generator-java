package meme.registry;

import com.github.promeg.pinyinhelper.Pinyin;
import meme.core.Meme;
import meme.core.MemeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 表情注册中心：Spring 容器收集全部 Meme bean，提供查询 / 排序 / 模糊搜索。
 * 对齐 Rust meme_generator::memes / search。
 */
@Service
public class MemeRegistryService {

    private static final Logger log = LoggerFactory.getLogger(MemeRegistryService.class);

    private final Map<String, Meme> memes = new ConcurrentHashMap<>();

    public MemeRegistryService(List<Meme> memeBeans, org.springframework.core.env.Environment env) {
        Set<String> disabled = Set.of(env.getProperty("meme.disabled-list", "").split(","));
        for (Meme meme : memeBeans) {
            if (disabled.contains(meme.key())) {
                log.info("Meme disabled: {}", meme.key());
                continue;
            }
            memes.put(meme.key(), meme);
        }
        log.info("Loaded {} memes", memes.size());
    }

    public Meme getMeme(String key) {
        return memes.get(key);
    }

    public List<Meme> getMemesSorted(MemeSortBy sortBy, boolean reverse) {
        List<Meme> list = new ArrayList<>(memes.values());
        Comparator<Meme> comparator = switch (sortBy) {
            case KEY -> Comparator.comparing(Meme::key);
            case KEYWORDS -> Comparator.comparing(keywords());
            case KEYWORDS_PINYIN -> Comparator.comparing(m -> toPinyin(keywords().apply(m)));
            case DATE_CREATED -> Comparator.comparing(m -> m.info().dateCreated());
            case DATE_MODIFIED -> Comparator.comparing(m -> m.info().dateModified());
        };
        list.sort(comparator);
        if (reverse) {
            java.util.Collections.reverse(list);
        }
        return list;
    }

    public List<String> getMemeKeysSorted(MemeSortBy sortBy, boolean reverse) {
        return getMemesSorted(sortBy, reverse).stream().map(Meme::key).toList();
    }

    public List<MemeInfo> getMemeInfosSorted(MemeSortBy sortBy, boolean reverse) {
        return getMemesSorted(sortBy, reverse).stream().map(Meme::info).toList();
    }

    /** 模糊搜索，对齐 Rust search_memes：key / 关键词 / 快捷方式 /（可选）标签 取最高分排序。 */
    public List<String> searchMemes(String query, boolean includeTags) {
        record Entry(String key, double score) {
        }
        List<Entry> entries = new ArrayList<>();
        for (Meme meme : memes.values()) {
            MemeInfo info = meme.info();
            double score = FuzzySearch.score(meme.key(), query);
            for (String kw : info.keywords()) {
                score = Math.max(score, FuzzySearch.score(kw, query));
            }
            for (var sc : info.shortcuts()) {
                score = Math.max(score, FuzzySearch.score(sc.pattern(), query));
                if (sc.humanized() != null) {
                    score = Math.max(score, FuzzySearch.score(sc.humanized(), query));
                }
            }
            if (includeTags) {
                for (String tag : info.tags()) {
                    score = Math.max(score, FuzzySearch.score(tag, query));
                }
            }
            if (score > 0) {
                entries.add(new Entry(meme.key(), score));
            }
        }
        entries.sort((a, b) -> Double.compare(b.score(), a.score()));
        return entries.stream().map(Entry::key).toList();
    }

    private static Function<Meme, String> keywords() {
        return m -> String.join("/", m.info().keywords());
    }

    private static String toPinyin(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Pinyin.isChinese(c)) {
                sb.append(Pinyin.toPinyin(c).toLowerCase());
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
