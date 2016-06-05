package pt.ua.sd.ropegame.genrepository;

/**
 * Class to create the game's logging file.
 */
class Logger {

    /**
     * Returns a formatted String according to the specified parameters
     * @param row A String array containing the elements to be written.
     * @param size The minimum size (in spaces) that the correspondent element must occupy.
     * @return A formatted String.
     * @throws IllegalArgumentException If arrays have different sizes.
     */
    public static String log(String[] row, int[] size) {
        if(row.length != size.length)
            throw new IllegalArgumentException("Both arrays must have the same size!");

        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < row.length; i++) {
            String t = row[i];

            int lent_t = t.length();
            int s = size[i];

            int nblankSpaces = s-lent_t;

            for(int j = 0; j <= nblankSpaces; j++)
                sb.append(" ");

            sb.append(t);
        }

        return sb.toString();
    }
}
