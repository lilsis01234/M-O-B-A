package Engine.Tile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MapParser {

    public record MapData(int[][] tileNumbers, int columns, int rows) {}

    public MapData parse(String filePath) {
        int[][] mapTiles = new int[100][100];
        int maxCols = 0;
        int maxRows = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean inMapSection = false;
            int currentRow = 0;

            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;

                if (trimmed.equals("MAP")) {
                    inMapSection = true;
                    continue;
                }
                if (trimmed.equals("TILES")) {
                    break;
                }

                if (inMapSection) {
                    String[] numbers = trimmed.split("\\s+");
                    if (maxCols == 0) {
                        maxCols = numbers.length;
                    }
                    for (int col = 0; col < maxCols && col < numbers.length; col++) {
                        mapTiles[currentRow][col] = Integer.parseInt(numbers[col]);
                    }
                    currentRow++;
                    maxRows = currentRow;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new MapData(mapTiles, maxCols, maxRows);
    }
}

