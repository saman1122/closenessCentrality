package fr.insta.saman;

import javax.naming.Context;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main(String[] args)
    {
        File file = new File(App.class.getClassLoader().getResource("rollernet.dyn").getFile());
        List<String> result = null;
        try (Stream<String> stream = Files.lines(file.toPath())) {
            result= stream
                    .filter(line -> {
                        Integer secondes = Integer.parseInt((line.split(" "))[2]);
                        return (secondes >= 1200) && (secondes <= 1800);
                    })
                    .collect(Collectors.toList());


        } catch (IOException e) {
            e.printStackTrace();
        }

        result.forEach(System.out::println);
    }
}
