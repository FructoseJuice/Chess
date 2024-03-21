package Utils;

public class BitBoard {
    public static long merge(long board, long addition) {
         board |= addition;
         return board;
    }

    public static long add(long board, long token) {
        return merge(board, shiftToken(token));
    }

    public static boolean compare(long board, long toCompare) {
        return ((board & toCompare) > 0);
    }

    public static boolean compareToken(long token, long toCompare) {
        return ((shiftToken(token) & toCompare) > 0 );
    }

    public static long shiftToken(long token) {
        return (1L << token);
    }

    public static long prune(long board, long toPrune) {
        long bitMask;
        bitMask = board & toPrune; //Make mask to prune
        board ^= bitMask; //Prune away values

        return board;
    }

    public static void printBitboard(long bitboard) {
        for (int position = 0; position < 64; position++) {
            long bitMask = 1L << position;
            System.out.print(((bitboard & bitMask) != 0 ? "1" : "0") + " ");
            if ((position + 1) % 8 == 0) {
                System.out.println();
            }
        }
        System.out.println();
        /*
        for ( int pos = 0; pos < 64; pos++ ) {
            long bitMask = 1L << pos;
            System.out.print(((bitboard & bitMask) != 0 ? "1" : "0")+ " ");
        }
        System.out.println();

         */
    }
}
