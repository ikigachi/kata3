package software.ulpgc.katas;

import software.ulpgc.katas.io.UrlVideogameReader;
import software.ulpgc.katas.io.VideogameReader;
import software.ulpgc.katas.model.Videogame;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class Main {
    static void main() throws IOException {
        URL url = new URL("https://gist.githubusercontent.com/VamsiSukamanchi/e75c15ef386c24f26b02f9c7672f3ccd/raw/c96b551e942c13383d0e3d35761c2251b6b3b335/videogames.csv");
        VideogameReader reader = new UrlVideogameReader(url);
        List<Videogame> videogames = reader.readAll();
        for (Videogame videogame : videogames) {
            System.out.println(videogame);
        }


    }
}
