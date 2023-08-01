package cn.az.code.stream;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Liz
 */
@Slf4j
public class StreamDemo {

    public static void main(String[] args) {
        List<Integer> nums = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> nums2 = Arrays.asList(1, 2, 4, 5);
        List<String> strings = Arrays.asList("1", "2", "3");

        nums.stream().map(n -> n * n).sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        nums.stream().flatMap(i -> nums2.stream().map(j -> new int[] { i, j })).collect(Collectors.toList());

        nums.stream().flatMap(i -> nums.stream().filter(j -> (i + j) % 3 == 0).map(j -> new int[] { i, j }))
                .collect(Collectors.toList());

        nums.stream().filter(i -> i > 4).findAny().ifPresent(i -> log.info(String.valueOf(i)));

        // log.info(String.valueOf(nums.stream().reduce(0, Integer::sum)));
        // nums.stream().reduce(Integer::sum).ifPresent(System.out::print);
        // nums.stream().reduce(Integer::max);

        strings.stream().map(String::toUpperCase).collect(Collectors.joining());

        Stream.iterate(new int[] { 0, 1 }, t -> new int[] { t[1], t[0] + t[1] }).limit(20).map(t -> t[0])
                .forEach(System.out::print);
        Stream.iterate(0, s -> s + 1).limit(10).mapToInt(Integer::shortValue).sum();
        Stream.generate(Random::new).limit(20).forEach(System.out::print);

        try (Stream<String> lines = Files.lines(new File("").toPath()).filter(s -> s.contains("haha"))) {
            lines.findFirst();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 基本类型数组 => 转换成 集合
        int[] ints = new int[] { 1, 2, 3, 4, 5 };
        Arrays.stream(ints).boxed().distinct().limit(5).collect(Collectors.toList()).forEach(System.out::print);
    }
}
