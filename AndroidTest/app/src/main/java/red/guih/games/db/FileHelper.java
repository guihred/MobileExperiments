package red.guih.games.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by guilherme.hmedeiros on 16/03/2018.
 */
final class FileHelper {
    private FileHelper() {
    }

    /**
     * Parses a file containing sql statements into a String array that contains
     * only the sql statements. Comments and white spaces in the file are not
     * parsed into the String array. Note the file must not contained malformed
     * comments and all sql statements must end with a semi-colon ";" in order
     * for the file to be parsed correctly. The sql statements in the String
     * array will not end with a semi-colon ";".
     *
     * @param sqlFile - BufferedReader for the file that contains sql statements.
     * @return String array containing the sql statements.
     */
    private static String[] parseSqlFile(BufferedReader sqlFile) throws IOException {
        String line;
        StringBuilder sql = new StringBuilder();
        String multiLineComment = null;

        while ((line = sqlFile.readLine()) != null) {
            line = line.trim();

            // Check for start of multi-line comment
            if (multiLineComment == null) {
                // Check for first multi-line comment type
                if (line.startsWith("/*")) {
                    if (!line.endsWith("}")) {
                        multiLineComment = "/*";
                    }
                    // Check for second multi-line comment type
                } else if (line.startsWith("{")) {
                    if (!line.endsWith("}")) {
                        multiLineComment = "{";
                    }
                    // Append line if line is not empty or a single line comment
                } else if (!line.startsWith("--") && !line.isEmpty()) {
                    sql.append(line);
                } // Check for matching end comment
            } else if ("/*".equals(multiLineComment)) {
                if (line.endsWith("*/")) {
                    multiLineComment = null;
                }
                // Check for matching end comment
            } else if (line.endsWith("}")) {
                multiLineComment = null;

            }

        }

        sqlFile.close();

        return sql.toString().split(";");
    }

    /**
     * Parses a file containing sql statements into a String array that contains
     * only the sql statements. Comments and white spaces in the file are not
     * parsed into the String array. Note the file must not contained malformed
     * comments and all sql statements must end with a semi-colon ";" in order
     * for the file to be parsed correctly. The sql statements in the String
     * array will not end with a semi-colon ";".
     *
     * @param sqlFile - InputStream for the file that contains sql statements.
     * @return String array containing the sql statements.
     */
    static String[] parseSqlFile(InputStream sqlFile) throws IOException {
        return parseSqlFile(new BufferedReader(new InputStreamReader(sqlFile)));
    }


}
