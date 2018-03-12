package fr.insta.saman;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App
{
    private final static double MAX_NUMBER = Double.POSITIVE_INFINITY;
    private final static int NBR_PARTICIPANT = 62;

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

        //result.forEach(System.out::println);

        List<String> reformat = new ArrayList<>();

        result.forEach(x-> {
            String[] tab = x.split(" ");
            if (Integer.parseInt(tab[1]) < Integer.parseInt(tab[0])) {
                reformat.add(tab[1] + " " + tab[0]);
            } else {
                reformat.add(tab[0] + " " + tab[1]);
            }
        });

        List<String> liensUniques = reformat.stream().sorted().distinct().collect(Collectors.toList());
        List<String> lienDoubles = new ArrayList<>();

        liensUniques.forEach(line -> {
            String[] s = line.split(" ");
            lienDoubles.add(line);
            lienDoubles.add(s[1] + " " + s[0]);
        });

        HashMap<Integer,Double> rank = new HashMap<>();

        for (int i=0; i<NBR_PARTICIPANT;i++) {
            int finalI = i;
            double nbr;
            nbr = lienDoubles.stream().filter(s -> s.startsWith(finalI + " ")).collect(Collectors.toList()).size()/2;
            rank.put(i,nbr/liensUniques.size());
        }

        FloydWarshall floydWarshall = new FloydWarshall(getMatriceFromListDistance(lienDoubles));

        double[][] floyd = floydWarshall.floydWarshall();

        HashMap<Integer,Double> sommeDistance = new HashMap<>();

        for (int i=0; i<floyd.length;i++) {
            double resultat = 0d;

            for (int j=0;j<floyd.length;j++) {
                if (i!=j) resultat+=floyd[i][j];
            }

            sommeDistance.put(i,resultat);
        }

        HashMap<Integer,Double> resultatIndiceCloseness = new HashMap<>();

        sommeDistance.forEach((x,y) -> resultatIndiceCloseness.put(x,calculCloseness(y)));

        System.out.println("Rendu du jour");
        System.out.println("ID | Indice closeness | rank");
        System.out.println("---------------------------------------");

        for (int i=0; i<NBR_PARTICIPANT;i++){
            System.out.println(i + " | " + resultatIndiceCloseness.get(i) + " | " + rank.get(i));
        }
    }

    private static double calculCloseness(double somme) {
        return ((NBR_PARTICIPANT - 1)/somme);
    }

    private static double [][] getMatriceFromListDistance(List<String> in) {
        double [][] retour = new double[NBR_PARTICIPANT][NBR_PARTICIPANT];

        for (int i = 0; i<retour.length; i++) {
            for (int j = 0; j < retour.length; j++) {
                if (i == j) {
                    retour[i][j] = 0d;
                }else {
                    retour[i][j] = MAX_NUMBER;
                }
            }
        }

        for (String x : in) {
            String[] s = x.split(" ");
            int indice1 = Integer.parseInt(s[0]);
            int indice2 = Integer.parseInt(s[1]);

            retour[indice1][indice2] = 1d;
        }

        return retour;
    }
}
