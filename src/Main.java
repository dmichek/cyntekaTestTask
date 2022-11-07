

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args)  throws IOException {

        //change

        //фывфыв

        List <Integer> asd = new ArrayList<>();

        List<String> list1 = new ArrayList();//DEVELOP1s
        List<String> list2 = new ArrayList();

        FileInputStream fileInputStream = new FileInputStream("input.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));

        addToList(br, list1);
        addToList(br, list2);

        if ((list1.size() == 0) || (list2.size() == 0)) {
            br.close();
            return;
        }

        String line;
        StringBuilder sb = new StringBuilder();
        boolean found = false;
        List<String> processed = new ArrayList();
        double accuracy = 0.55;//процент схожести строк, выше которого считаем, что строки похожи
        FileOutputStream fileOutputStream = new FileOutputStream("output.txt");

        for(String s1:list1){
            for(String s2:list2){
                if(findSimilarity(s1, s2) >= accuracy) {
                    //line = s1 + ";" + s2 + "\n";
                    line = sb.append(s1).append(";").append(s2).append("\n").toString();
                    fileOutputStream.write(line.getBytes());
                    sb.setLength(0);
                    processed.add(s2);
                    found = true;
                }
            }
            if (!found) {
                line = sb.append(s1).append(":?").append("\n").toString();
                fileOutputStream.write(line.getBytes());
                sb.setLength(0);
            }
            found = false;
        }

        for(String s:list2){
            if (!processed.contains(s)){
                line = s + ":?" + "\n";
                fileOutputStream.write(line.getBytes());
            }

        }

        br.close();
        fileOutputStream.close();

    }

    public static void addToList(BufferedReader br, List list) throws IOException {

        int size;
        String line = br.readLine();

        try {
            size = Integer.parseInt(line);
        } catch (NumberFormatException e){
            System.out.println("Is not a number");
            return;
        }
        size = Integer.parseInt(line);

        for (int i = 0; i < size; i++){
            line = br.readLine().toLowerCase();
            if (!line.isEmpty())
                list.add(line);
        }
    }

    public static double findSimilarity(String x, String y) {
        if (x == null && y == null) {
            return 1.0;
        }
        if (x == null || y == null) {
            return 0.0;
        }
        return getJaroWinklerDistance(x, y);
    }

    public static double getJaroWinklerDistance(CharSequence first, CharSequence second) {
        double DEFAULT_SCALING_FACTOR = 0.1;
        if (first != null && second != null) {
            int[] mtp = matches(first, second);
            double m = (double)mtp[0];
            if (m == 0.0) {
                return 0.0;
            } else {
                double j = (m / (double)first.length() + m / (double)second.length() + (m - (double)mtp[1]) / m) / 3.0;
                double jw = j < 0.7 ? j : j + Math.min(0.1, 1.0 / (double)mtp[3]) * (double)mtp[2] * (1.0 - j);
                return (double)Math.round(jw * 100.0) / 100.0;
            }
        } else {
            throw new IllegalArgumentException("Strings must not be null");
        }
    }

    private static int[] matches(CharSequence first, CharSequence second) {
        CharSequence max;
        CharSequence min;
        if (first.length() > second.length()) {
            max = first;
            min = second;
        } else {
            max = second;
            min = first;
        }

        int range = Math.max(max.length() / 2 - 1, 0);
        int[] matchIndexes = new int[min.length()];
        Arrays.fill(matchIndexes, -1);
        boolean[] matchFlags = new boolean[max.length()];
        int matches = 0;

        int transpositions;
        int prefix;
        for(int mi = 0; mi < min.length(); ++mi) {
            char c1 = min.charAt(mi);
            transpositions = Math.max(mi - range, 0);

            for(prefix = Math.min(mi + range + 1, max.length()); transpositions < prefix; ++transpositions) {
                if (!matchFlags[transpositions] && c1 == max.charAt(transpositions)) {
                    matchIndexes[mi] = transpositions;
                    matchFlags[transpositions] = true;
                    ++matches;
                    break;
                }
            }
        }

        char[] ms1 = new char[matches];
        char[] ms2 = new char[matches];
        transpositions = 0;

        for(prefix = 0; transpositions < min.length(); ++transpositions) {
            if (matchIndexes[transpositions] != -1) {
                ms1[prefix] = min.charAt(transpositions);
                ++prefix;
            }
        }

        transpositions = 0;

        for(prefix = 0; transpositions < max.length(); ++transpositions) {
            if (matchFlags[transpositions]) {
                ms2[prefix] = max.charAt(transpositions);
                ++prefix;
            }
        }

        transpositions = 0;

        for(prefix = 0; prefix < ms1.length; ++prefix) {
            if (ms1[prefix] != ms2[prefix]) {
                ++transpositions;
            }
        }

        prefix = 0;

        for(int mi = 0; mi < min.length() && first.charAt(mi) == second.charAt(mi); ++mi) {
            ++prefix;
        }

        return new int[]{matches, transpositions / 2, prefix, max.length()};
    }
}