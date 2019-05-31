import java.util.Scanner;
import java.util.ArrayList;
import java.lang.Math;
import java.io.*;
import java.util.Random;

public class Test{
    public static void main(String[] args) throws FileNotFoundException{
        Chessboard board = new Chessboard();
        board.initBoard();
        board.printBoard();
        System.out.println("Enter training set directory or h for help: ");
        Scanner s = new Scanner(System.in);
        String path = s.nextLine();
        while (path.equals("h")){
            System.out.println("Welcome to COT! In order to communicate with the agent, one should use the usual algebraic chess notation, with the extra convention that, instead of omitting the piece in pawn moves, it is "
            + "preferred to use the notation 'Pe4' instead of simply 'e4'. Also, one may omit the 'x' character when it comes to capture-moves." + "\n" + "\n" + "As for the training dataset, one may use one of their own "
            + "or the one provided with COT.");
            System.out.println("Enter training set directory or h for help: ");
            s = new Scanner(System.in);
            path = s.nextLine();
        }
        Agent black = new Agent(15);
        black.initFirstLayer();
        File dir = new File(path);//Enter the appropriate directory of training data. e.g. C:\\Project\\Examples\\...
        black.train(dir);
        Move prevMove = new Move();
        Scanner scanner = new Scanner(System.in);
        for (int i=0; i<10; i++){
            System.out.println("Enter your move (as white): ");
            String move = scanner.nextLine();
            Move m = new Move();
            m = board.executeMove(move);
            board.printBoard();
            System.out.println("Black makes a move.");
            Move bMove = new Move();
            bMove = black.play(board,i,prevMove);
            m = board.executeMove(bMove.toString());
            board.printBoard();
            prevMove = bMove;
        }
    }
}

class Chessboard{
    private char board[][] = new char[8][8];
    private boolean wKingHasMoved = false;
    private boolean bKingHasMoved = false;
    private boolean wRRookHasMoved = false;
    private boolean wLRookHasMoved = false;
    private boolean bRRookHasMoved = false;
    private boolean bLRookHasMoved = false;
    
    public Chessboard(char[][] board){
        for (int i=0; i<8; i++){
            for (int j=0; j<8; j++){
                this.board[i][j] = board[i][j];
            }
        }
    }
    
    public Chessboard(){}
    
    public void printBoard(){
        System.out.println("------------------");
        for (int i=0; i<8; i++){
            System.out.print("|");
            for (int j=0; j<8; j++){
                System.out.print(board[i][j] + " ");
            }
            System.out.println("|");
        }
        System.out.println("------------------");
    }
    
    public void initBoard(){
        board[0][0] = 'r';
        board[0][7] = 'r';
        board[0][1] = 'n';
        board[0][6] = 'n';
        board[0][2] = 'b';
        board[0][5] = 'b';
        board[0][3] = 'q';
        board[0][4] = 'k';
        for (int i=0; i<8; i++){
            board[1][i] = 'p';
        }
        for (int i=0; i<8; i++){
            board[6][i] = 'P';
        }
        board[7][0] = 'R';
        board[7][7] = 'R';
        board[7][1] = 'N';
        board[7][6] = 'N';
        board[7][2] = 'B';
        board[7][5] = 'B';
        board[7][3] = 'Q';
        board[7][4] = 'K';
        for (int i=2; i<6; i++){
            for (int j=0; j<8; j++){
                board[i][j] = '.';
            }
        }
    }
    
    public char getSquare(int i, int j){
        return board[i][j];
    }
    
    public boolean isOpp(char x, char y){
        return (x>65 && y>65 && ((x<91 && y>97) || (x>97 && y<91)));
    }
    
    public boolean isFriend(char x, char y){
        return (x>65 && y>65 && ((x<91 && y<91) || (x>97 && y>97)));
    }
        
    public boolean inBoard(int i, int j){
        return (i>-1 && i<8 && j>-1 && j<8);
    }
    
    public boolean containsMove(ArrayList<Move> list, Move move){
        int length = list.size();
        boolean flag = true;
        int i = 0;
        while (flag && i<length){
            if (move.equals(list.get(i))){
                flag = false;
            }
            i++;
        }
        return !flag;
    }
    
    public ArrayList<Move> rookLegalMoves(char rook, int i, int j){
        ArrayList<Move> moves = new ArrayList<Move>();
        int k = 1;
        boolean flag = true;
        while (flag && i+k<8){
            char other = board[i+k][j];
            Move next = new Move(rook,i,j,i+k,j);
            if (isFriend(rook,other))
                flag = false;
            else if (isOpp(rook,other)){
                moves.add(next);
                flag = false;
            }
            else
                moves.add(next);
            k++;
        }
        k = 1;
        flag = true;
        while (flag && i-k>-1){
            char other = board[i-k][j];
            Move next = new Move(rook,i,j,i-k,j);
            if (isFriend(rook,other))
                flag = false;
            else if (isOpp(rook,other)){
                moves.add(next);
                flag = false;
            }
            else
                moves.add(next);
            k--;
        }
        k = 0;
        flag = true;
        while (flag && j+k<8){
            char other = board[i][j+k];
            Move next = new Move(rook,i,j,i,j+k);
            if (isFriend(rook,other))
                flag = false;
            else if (isOpp(rook,other)){
                moves.add(next);
                flag = false;
            }
            else
                moves.add(next);
            k++;
        }
        k = 1;
        flag = true;
        while (flag && j-k>-1){
            char other = board[i][j-k];
            Move next = new Move(rook,i,j,i,j-k);
            if (isFriend(rook,other))
                flag = false;
            else if (isOpp(rook,other)){
                moves.add(next);
                flag = false;
            }
            else
                moves.add(next);
            k--;
        }
        return moves;
    }
    
    public ArrayList<Move> knightLegalMoves(char knight, int i, int j){
        ArrayList<Move> moves = new ArrayList<Move>();
        for (int k=-2; k<3; k++){
            for (int l=-2; l<3; l++){
                if (inBoard(i+k,j+l)){
                    char other = board[i+k][j+l];
                    if (Math.abs(k)+Math.abs(l)==3 && !isFriend(knight,other)){
                        Move next = new Move(knight,i,j,i+k,j+l);
                        moves.add(next);
                    }
                }
            }
        }
        return moves;
    }
    
    public ArrayList<Move> bishopLegalMoves(char bishop, int i, int j){
        ArrayList<Move> moves = new ArrayList<Move>();
        int k = 1;
        boolean flag = true;
        while (flag && i+k<8 && j+k<8){
            char other = board[i+k][j+k];
            Move next = new Move(bishop,i,j,i+k,j+k);
            if (isFriend(bishop,other))
                flag = false;
            else if (isOpp(bishop,other)){
                moves.add(next);
                flag = false;
            }                
            else
                moves.add(next);
            k++;
        }
        k = 1;
        flag = true;
        while (flag && i+k<8 && j-k>-1){
            char other = board[i+k][j-k];
            Move next = new Move(bishop,i,j,i+k,j-k);
            if (isFriend(bishop,other))
                flag = false;
            else if (isOpp(bishop,other)){
                moves.add(next);
                flag = false;
            }                
            else
                moves.add(next);
            k++;
        }
        k = 1;
        flag = true;
        while (flag && i-k>-1 && j+k<8){
            char other = board[i-k][j+k];
            Move next = new Move(bishop,i,j,i-k,j+k);
            if (isFriend(bishop,other))
                flag = false;
            else if (isOpp(bishop,other)){
                moves.add(next);
                flag = false;
            }                
            else
                moves.add(next);
            k++;
        }
        k = 1;
        flag = true;
        while (flag && i-k>-1 && j-k>-1){
            char other = board[i-k][j-k];
            Move next = new Move(bishop,i,j,i-k,j-k);
            if (isFriend(bishop,other))
                flag = false;
            else if (isOpp(bishop,other)){
                moves.add(next);
                flag = false;
            }                
            else
                moves.add(next);
            k++;
        }
        return moves;
    }
    
    public ArrayList<Move> queenLegalMoves(char queen, int i, int j){
        ArrayList<Move> moves = new ArrayList<Move>();
        ArrayList<Move> bishop = new ArrayList<Move>();
        moves = rookLegalMoves(queen,i,j);
        bishop = bishopLegalMoves(queen,i,j);
        moves.addAll(bishop);
        return moves;
    }
    
    public ArrayList<Move> kingLegalMoves(char king, int i, int j){//You have ignored castling!!
        ArrayList<Move> moves = new ArrayList<Move>();
        for (int k=-1; i<2; i++){
            for (int l=-1; j<2; j++){
                char other = board[i+k][j+l];
                if (k!=0 && l!=0 && i+k>-1 && i+k <8 && j+l>-1 && j+l<8 && !isFriend(king,other)){
                    Move next = new Move(king,i,j,i+k,j+l);
                    moves.add(next);
                }
            }
        }
        return moves;
    }
    
    public ArrayList<Move> pawnLegalMoves(char pawn, int i, int j){//You have ignored the cases of i) en passent and 2) promotion to queen etc!!
        ArrayList<Move> moves = new ArrayList<Move>();
        int baseline = 6;
        int direction = -1;
        if (pawn>97){
            baseline = 1;
            direction = 1;
        }
        if (i!=baseline)
            for (int k=-1; k<2; k++){
                if (inBoard(i+direction,j+k)){
                    char other = board[i+direction][j+k];
                    if (k!=0 && isOpp(pawn,other)){
                        Move next = new Move(pawn,i,j,i+direction,j+k);
                        moves.add(next);
                    }
                    if (k==0 && !isFriend(pawn,other) && !isOpp(pawn,other)){
                        Move next = new Move(pawn,i,j,i+direction,j);
                        moves.add(next);
                    }
                }
            }
        else if (i==baseline){
            for (int k=-1; k<2; k++){
                if (inBoard(i+direction,j+k)){
                    char other = board[i+direction][j+k];
                    if (k!=0 && isOpp(pawn,other)){
                        Move next = new Move(pawn,i,j,i+direction,j+k);
                        moves.add(next);
                    }
                    if (k==0 && !isFriend(pawn,other) && !isOpp(pawn,other)){
                        Move next = new Move(pawn,i,j,i+direction,j);
                        moves.add(next);
                    }
                }
            }
            char other = board[i+direction][j];
            char another = board[i+2*direction][j];
            if (!isOpp(pawn,other) && !isFriend(pawn,other) && !isOpp(pawn,another) && !isFriend(pawn,another)){
                Move next = new Move(pawn,i,j,i+2*direction,j);
                moves.add(next);
            }
        }
        return moves;
    }
    
    public ArrayList<Move> castle(char castle){//"S" stands for white small, "T" for white big and "s,t" respectively.
        ArrayList<Move> moves = new ArrayList<Move>();
        if (castle=='S' && !wKingHasMoved && !wRRookHasMoved && board[7][6]=='.' && board[7][5]=='.'){
            Move next = new Move(castle,-1,-1,-1,-1);
            moves.add(next);
        }
        if (castle=='s' && !bKingHasMoved && !bRRookHasMoved && board[0][6]=='.' && board[0][5]=='.'){
            Move next = new Move(castle,-1,-1,-1,-1);
            moves.add(next);
        }
        if (castle=='T' && !wKingHasMoved && !wLRookHasMoved && board[7][4]=='.' && board[7][3]=='.' && board[7][2]=='.'){
            Move next = new Move(castle,-1,-1,-1,-1);
            moves.add(next);
        }
        if (castle=='t' && !bKingHasMoved && !bLRookHasMoved && board[0][4]=='.' && board[0][3]=='.' && board[0][2]=='.'){
            Move next = new Move(castle,-1,-1,-1,-1);
            moves.add(next);
        }
        return moves;
    }
    
    public void castleCheck(char piece, int i, int j){
        if (!wKingHasMoved && piece=='K'){
            wKingHasMoved = true;
        }
        else if (!bKingHasMoved && piece=='k'){
            bKingHasMoved = true;
        }
        else if (!wRRookHasMoved && piece=='R' && i==7 && j==7){
            wRRookHasMoved = true;
        }
        else if (!wLRookHasMoved && piece=='R' && i==7 && j==0){
            wLRookHasMoved = true;
        }
        else if (!bRRookHasMoved && piece=='r' && i==0 && j==7){
            bRRookHasMoved = true;
        }
        else if (!bLRookHasMoved && piece=='r' && i==0 && j==0){
            bLRookHasMoved = true;
        }
    }
    
    public ArrayList<Move> bLegalMoves(){
        ArrayList<Move> moves = new ArrayList<Move>();
        ArrayList<Move> current = new ArrayList<Move>();
        char piece;
        for (int i=0; i<8; i++){
            for (int j=0; j<8; j++){
                piece = board[i][j];
                if (piece=='r'){
                    current = rookLegalMoves('r',i,j);
                    moves.addAll(current);
                }
                else if (piece=='n'){
                    current = knightLegalMoves('n',i,j);
                    moves.addAll(current);
                }
                else if (piece=='b'){
                    current = bishopLegalMoves('b',i,j);
                    moves.addAll(current);
                }
                else if (piece=='q'){
                    current = queenLegalMoves('q',i,j);
                    moves.addAll(current);
                }
                else if (piece=='k'){
                    current = kingLegalMoves('k',i,j);
                    moves.addAll(current);
                }
                else if (piece=='p'){
                    current = pawnLegalMoves('p',i,j);
                    moves.addAll(current);
                }
                current = castle('s');
                moves.addAll(current);
                current = castle('t');
                moves.addAll(current);
            }
        }
        return moves;
    }
    
    public ArrayList<Move> wLegalMoves(){
        ArrayList<Move> moves = new ArrayList<Move>();
        ArrayList<Move> current = new ArrayList<Move>();
        char piece;
        for (int i=0; i<8; i++){
            for (int j=0; j<8; j++){
                piece = board[i][j];
                if (piece=='R'){
                    current = rookLegalMoves('R',i,j);
                    moves.addAll(current);
                }
                else if (piece=='N'){
                    current = knightLegalMoves('N',i,j);
                    moves.addAll(current);
                }
                else if (piece=='B'){
                    current = bishopLegalMoves('B',i,j);
                    moves.addAll(current);
                }
                else if (piece=='Q'){
                    current = queenLegalMoves('Q',i,j);
                    moves.addAll(current);
                }
                else if (piece=='K'){
                    current = kingLegalMoves('K',i,j);
                    moves.addAll(current);
                }
                else if (piece=='P'){
                    current = pawnLegalMoves('P',i,j);
                    moves.addAll(current);
                }
                current = castle('S');
                moves.addAll(current);
                current = castle('T');
                moves.addAll(current);
            }
        }
        return moves;
    }
    
    public ArrayList<Move> legalMoves(){
        ArrayList<Move> moves = new ArrayList<Move>();
        ArrayList<Move> current = new ArrayList<Move>();
        moves = wLegalMoves();
        current = bLegalMoves();
        moves.addAll(current);
        return moves;
    }
    
    public void undoMove(Move move){
        char piece = move.getPiece();
        int iRow = move.getiRow();
        int iCol = move.getiColumn();
        int fRow = move.getfRow();
        int fCol = move.getfColumn();
        if (piece=='S'){
            board[7][4] = 'K';
            board[7][6] = '.';
            board[7][7] = 'R';
            board[7][5] = '.';
        }
        else if (piece=='T'){
            board[7][4] = 'K';
            board[7][2] = '.';
            board[7][0] = 'R';
            board[7][3] = '.';
        }
        else if (piece=='s'){
            board[0][4] = 'k';
            board[0][6] = '.';
            board[0][7] = 'r';
            board[0][5] = '.';
        }
        else if (piece=='T'){
            board[0][4] = 'k';
            board[0][2] = '.';
            board[0][0] = 'r';
            board[0][3] = '.';
        }
        else{
            Move m = new Move(piece,fRow,fCol,iRow,iCol);
            Move temp = new Move();
            temp = executeMove(m.toString());
        }
    }
    
    public Move executeMove(String move){//Also returns the move it executes (this is used in Example, as well).
        int length = move.length();
        Move m = new Move();
        if (length==3 && !(move.charAt(0)=='O') && !(move.charAt(0)=='o')){
            char piece = move.charAt(0);
            int row = 7-(move.charAt(2) - '1');
            int col = move.charAt(1) - 'a';
            for (int i=0; i<8; i++){
                for (int j=0; j<8; j++){
                    if (board[i][j]==piece){
                        ArrayList<Move> legalMoves = new ArrayList<Move>();
                        m = new Move(piece,i,j,row,col);
                        legalMoves = legalMoves();
                        boolean legal = containsMove(legalMoves,m);
                        if (legal){
                            board[i][j] = '.';
                            board[m.getfRow()][m.getfColumn()] = piece;
                            castleCheck(piece,i,j);
                            return m;
                        }
                    }
                }
            }
        }
        else if (length==4 && move.charAt(1)=='x'){
            char piece = move.charAt(0);
            int row = 7-(move.charAt(3) - '1');
            int col = move.charAt(2) - 'a';
            for (int i=0; i<8; i++){
                for (int j=0; j<8; j++){
                    if (board[i][j]==piece){
                        ArrayList<Move> legalMoves = new ArrayList<Move>();
                        m = new Move(piece,i,j,row,col);
                        legalMoves = legalMoves();
                        boolean legal = containsMove(legalMoves,m);
                        if (legal){
                            board[i][j] = '.';
                            board[m.getfRow()][m.getfColumn()] = piece;
                            castleCheck(piece,i,j);
                            return m;
                        }
                    }
                }
            }
        }
        else if (length==4 && move.charAt(1)>96){
            char piece = move.charAt(0);
            int iCol = move.charAt(1) - 'a';
            int row = 7-(move.charAt(3) - '1');
            int col = move.charAt(2) - 'a';
            for (int i=0; i<8; i++){
                if (board[i][iCol]==piece){
                    ArrayList<Move> legalMoves = new ArrayList<Move>();
                    m = new Move(piece,i,iCol,row,col);
                    legalMoves = legalMoves();
                    boolean legal = containsMove(legalMoves,m);
                    if (legal){
                        board[i][iCol] = '.';
                        board[m.getfRow()][m.getfColumn()] = piece;
                        castleCheck(piece,i,iCol);
                        return m;
                    }
                }
            }
        }
        else if (length==4 && move.charAt(1)<57){
            char piece = move.charAt(0);
            int iRow = 7-(move.charAt(1) - '1');
            int row = 7-(move.charAt(3) - '1');
            int col = move.charAt(2) - 'a';
            for (int i=0; i<8; i++){
                if (board[iRow][i]==piece){
                    ArrayList<Move> legalMoves = new ArrayList<Move>();
                    m = new Move(piece,iRow,i,row,col);
                    legalMoves = legalMoves();
                    boolean legal = containsMove(legalMoves,m);
                    if (legal){
                        board[iRow][i] = '.';
                        board[m.getfRow()][m.getfColumn()] = piece;
                        castleCheck(piece,iRow,i);
                        return m;
                    }
                }
            }
        }
        else if (length==5 && !(move.charAt(0)=='O') && !(move.charAt(0)=='o')){
            char piece = move.charAt(0);
            int iCol = move.charAt(1) - 'a';
            int row = 7-(move.charAt(4) - '1');
            int col = move.charAt(3) - 'a';
            for (int i=0; i<8; i++){
                if (board[i][iCol]==piece){
                    ArrayList<Move> legalMoves = new ArrayList<Move>();
                    m = new Move(piece,i,iCol,row,col);
                    legalMoves = legalMoves();
                    boolean legal = containsMove(legalMoves,m);
                    if (legal){
                        board[i][iCol] = '.';
                        board[m.getfRow()][m.getfColumn()] = piece;
                        castleCheck(piece,i,iCol);
                        return m;
                    }
                }
            }
            return m;
        }
        else if (length==5 && (move.charAt(0)=='O' || move.charAt(0)=='o')){
            char colour = move.charAt(0);
            char castle = 'T';
            if (colour>96){
                castle = 't';
            }
            ArrayList<Move> legalMoves = new ArrayList<Move>();
            m = new Move(castle,-1,-1,-1,-1);
            legalMoves = legalMoves();
            boolean legal = containsMove(legalMoves,m);
            if (colour=='O' && legal){
                board[7][2] = 'K';
                board[7][4] = '.';
                board[7][3] = 'R';
                board[7][0] = '.';
                wKingHasMoved = true;
                return m;
            }
            else if (colour=='o' && legal){
                board[0][2] = 'k';
                board[0][4] = '.';
                board[0][3] = 'r';
                board[0][0] = '.';
                bKingHasMoved = true;
                return m;
            }
        }
        else if (length==3 && (move.charAt(0)=='O' || move.charAt(0)=='o')){
            char colour = move.charAt(0);
            char castle = 'S';
            if (colour>96){
                castle = 's';
            }
            ArrayList<Move> legalMoves = new ArrayList<Move>();
            m = new Move(castle,-1,-1,-1,-1);
            legalMoves = legalMoves();
            boolean legal = containsMove(legalMoves,m);
            if (colour=='O' && legal){
                board[7][6] = 'K';
                board[7][4] = '.';
                board[7][5] = 'R';
                board[7][7] = '.';
                wKingHasMoved = true;
                return m;
            }
            else if (colour=='o' && legal){
                board[0][6] = 'k';
                board[0][4] = '.';
                board[0][5] = 'r';
                board[0][7] = '.';
                bKingHasMoved = true;
                return m;
            }
        }
        return m;
    }
}

class Move{
    private char piece;
    private int iRow;
    private int iColumn;
    private int fRow;
    private int fColumn;
    
    public Move(char p, int i, int j, int fi, int fj){
        piece = p;
        iRow = i;
        iColumn = j;
        fRow = fi;
        fColumn = fj;
    }
    
    public Move(Move move){
        this.piece = move.piece;
        this.iRow = move.iRow;
        this.iColumn = move.iColumn;
        this.fRow = move.fRow;
        this.fColumn = move.fColumn;
    }
    
    public Move(){}
    
    public char getPiece(){
        return piece;
    }
    
    public int getiRow(){
        return iRow;
    }
    
    public int getiColumn(){
        return iColumn;
    }
    public int getfRow(){
        return fRow;
    }
    
    public int getfColumn(){
        return fColumn;
    }
    
    public void printMove(){
        System.out.print(piece);
        System.out.print(iRow);
        System.out.print(iColumn);
        System.out.print(fRow);
        System.out.print(fColumn);
    }
    
    public String toString(){
        String move;
        if (piece=='S'){
            move = "O-O";
        }
        else if (piece=='s'){
            move = "o-o";
        }
        else if (piece=='T'){
            move = "O-O-O";
        }
        else if (piece=='t'){
            move = "o-o-o";
        }
        else{
            char ic = (char)(iColumn + 'a');
            char fc = (char)(fColumn + 'a');
            int revRow = 8-fRow;
            move = "" + piece + ic + fc + revRow;
        }
        return move;
    }
    
    public boolean equals(Move other){
        return (this.piece==other.getPiece() && this.iRow==other.getiRow() && this.iColumn==other.getiColumn() && this.fRow==other.getfRow() && this.fColumn==other.getfColumn());
    }
}

class Example{
    private String[][] table = new String[10][2];
    
    public Example(File file)throws FileNotFoundException{
        readExample(file);
    }
    
    public void readExample(File file)throws FileNotFoundException{
        Scanner scanner = new Scanner(file);
        boolean colour = true;//True stands for white while false stands for black!!
        int i = 0;
        int j = 0;
        while (scanner.hasNext()){
            String s = scanner.next();
            String move;
            if (s.charAt(s.length()-1)=='.'){
                colour = true;
            }
            if (colour && s.charAt(0)>64){
                if (s.charAt(0)>96){
                    move = "P" + s;
                }
                else{
                    move = s;
                }
                table[i][0] = move;
                colour = false;
                i++;
            }
            else if (!colour && s.charAt(0)>64){
                if (s.charAt(0)>96){
                    move = "p" + s;
                }
                else if (s.charAt(0)=='O' && s.length()==3){
                    move = "o-o";
                }
                else if (s.charAt(0)=='O' && s.length()==5){
                    move = "o-o-o";
                }
                else{
                    char first = s.charAt(0);
                    first = (char)(first + ('a' - 'A'));
                    move = Character.toString(first) + s.substring(1);
                }
                table[j][1] = move;
                j++;
            }
        }
    }
    
    public void printExample(){
        for (int i=0; i<10; i++){
            for (int j=0; j<2; j++){
                System.out.print(table[i][j]);
            }
            System.out.println("");
        }
    }
    
    public String getMove(int i, int j){
        return table[i][j];
    }
}

class Agent{//You have to define all the rules used into the Agent class!!
    private int n;
    private double[][] firstLayer = new double[10][15];//The second dimension is to be changed whenever a rule is added (unknown error when initialized as n or 'unidentified')!!
    private Chessboard board = new Chessboard();
    
    public Agent(int n){
        this.n = n;
    }
    
    public void initFirstLayer(){
        for (int i=0; i<10; i++){
            for (int j=0; j<n; j++){
                firstLayer[i][j] = 0;
            }
        }
    }
    
    public void standardizeFirstLayer(){
        double sum = 0;
        for (int i=0; i<10; i++){
            sum = 0;
            for (int j=0; j<n; j++){
                sum += firstLayer[i][j];
            }
            for (int j=0; j<n; j++){
                firstLayer[i][j] /= sum;
            }
        }
    }
    
    public boolean rule0(Move move){//Occupy centre!
        return ((move.getfRow()==3 || move.getfRow()==4) && (move.getfColumn()==3 || move.getfColumn()==4));
    }
    
    public boolean rule1(Move prevMove, Move move){//Do not move the same piece again!
        char piece = move.getPiece();
        int ii = move.getiRow();
        int ij = move.getiColumn();
        char prevPiece = prevMove.getPiece();
        int fi = prevMove.getfRow();
        int fj = prevMove.getfColumn();
        return !(piece==prevPiece && ii==fi && ij==fj);        
    }
    
    public boolean rule2(Chessboard board){//Equilibrium
        int black = 0;
        int white = 0;
        for (int i=0; i<8; i++){
            for (int j=0; j<8; j++){
                char piece = board.getSquare(i,j);
                if (piece=='r'){
                    black += 5;
                }
                else if (piece=='n' || piece=='b'){
                    black += 3;
                }
                else if (piece=='q'){
                    black += 9;
                }
                else if (piece=='p'){
                    black += 1;
                }
                else if (piece=='R'){
                    white += 5;
                }
                else if (piece=='N' || piece=='B'){
                    white += 3;
                }
                else if (piece=='Q'){
                    white += 9;
                }
                else if (piece=='P'){
                    white += 1;
                }
            }
        }
        if (black<white){
            return false;
        }
        else{
            return true;
        }
    }
    
    public boolean rule3(Chessboard board){//This counts the influnce score of all the pieces on the board (1 point per square in piece's legal moves --- supporting pieces have been ignored for reasons of simplicity)
        int white = 0;
        int black = 0;
        ArrayList<Move> wMoves = new ArrayList<Move>();
        ArrayList<Move> bMoves = new ArrayList<Move>();
        wMoves = board.wLegalMoves();
        bMoves = board.bLegalMoves();
        white = wMoves.size();
        black = bMoves.size();
        if (black<white){
            return false;
        }
        else{
            return true;
        }
    }
    
    public boolean inCenter(Move move){//Assistant method that is used in rules having to do with central moves.
        int row = move.getfRow();
        int col = move.getfColumn();
        return ((row==3 || row==4) && (col==3 || col==4));
    }
    
    public boolean rule4(Chessboard board, Move move){//Measures the center influence (the four central squares).
        char piece = move.getPiece();
        int row = move.getfRow();
        int col = move.getfColumn();
        ArrayList<Move> moves = new ArrayList<Move>();
        moves = board.bLegalMoves();
        int length = moves.size();
        for (int i=0; i<length; i++){
            Move m = new Move();
            m = moves.get(i);
            char p = m.getPiece();
            int r = m.getiRow();
            int c = m.getiColumn();
            if (piece==p && row==r && col==c && inCenter(m)){
                return true;
            }
        }
        return false;
    }
    
    public boolean rule5(Chessboard board, Move move){//Play so as to avoid moving in squares controled by white.
        int row = move.getfRow();
        int col = move.getfColumn();
        ArrayList<Move> wMoves = new ArrayList<Move>();
        wMoves = board.wLegalMoves();
        int length = wMoves.size();
        for (int i=0; i<length; i++){
            Move m = new Move();
            m = wMoves.get(i);
            int r = m.getfRow();
            int c = m.getfColumn();
            if (row==r && col==c){
                return false;
            }
        }
        return true;
    }
    
    public boolean rule6(Chessboard board, Move move){//React to threats to pieces other than pawns.
        ArrayList<Move> wMoves = new ArrayList<Move>();
        wMoves = board.wLegalMoves();
        char piece = move.getPiece();
        if (piece=='p'){
            return false;
        }
        int row = move.getiRow();
        int col = move.getiColumn();
        int length = wMoves.size();
        for (int i=0; i<length; i++){
            Move m = new Move();
            m = wMoves.get(i);
            int r = m.getfRow();
            int c = m.getfColumn();
            if (row==r && col==c){
                return true;
            }
        }
        return false;
    }
    
    public boolean rule7(Chessboard board, Move move){//React to threats to pawns (it is used to model the case of a pawn sacrifice).
        ArrayList<Move> wMoves = new ArrayList<Move>();
        wMoves = board.wLegalMoves();
        char piece = move.getPiece();
        if (!(piece=='p')){
            return false;
        }
        int row = move.getiRow();
        int col = move.getiColumn();
        int length = wMoves.size();
        for (int i=0; i<length; i++){
            Move m = new Move();
            m = wMoves.get(i);
            int r = m.getfRow();
            int c = m.getfColumn();
            if (row==r && col==c){
                return true;
            }
        }
        return false;
    }
    
    public boolean rule8(Move move){//Move pieces from their initial positions.
        char piece = move.getPiece();
        if (piece=='p'){
            return false;
        }
        int row = move.getiRow();
        return (row==0);
    }
    
    public boolean rule9(Move move){//Prepare small castling.
        char piece = move.getPiece();
        int row = move.getiRow();
        int col = move.getiColumn();
        if (!(piece=='k') && !(piece=='r') && col>4 && row==0){
            return true;
        }
        return false;
    }
    
    public boolean rule10(Move move){//Prepare big castling.
        char piece = move.getPiece();
        int row = move.getiRow();
        int col = move.getiColumn();
        if (!(piece=='k') && !(piece=='r') && col<4 && row==0){
            return true;
        }
        return false;
    }
    
    public boolean rule11(Move move){//Occupy space behind pawns.
        char piece = move.getPiece();
        int ic = move.getiColumn();
        int fc = move.getfColumn();
        if (piece=='p' && ic==fc){
            return true;
        }
        return false;
    }
    
    public boolean rule12(Move move){//Avoid returning to line 0.
        int row = move.getfRow();
        return (!(row==0));
    }
    
    public boolean rule13(Chessboard board, Move move){//Opens lines for pieces to move.
        ArrayList<Move> newMoves = new ArrayList<Move>();
        newMoves = board.bLegalMoves();
        int newLength = newMoves.size();
        char[][] b = new char[8][8];
        for (int i=0; i<8; i++){
            for (int j=0; j<8; j++){
                b[i][j] = board.getSquare(i,j);
            }
        }
        Chessboard prevBoard = new Chessboard(b);
        prevBoard.undoMove(move);
        ArrayList<Move> oldMoves = new ArrayList<Move>();
        oldMoves = prevBoard.bLegalMoves();
        int oldLength = oldMoves.size();
        return (newLength>oldLength);
    }
    
    public boolean rule14(Move move){//Protect king's side.
        char piece = move.getPiece();
        int col = move.getiColumn();
        return (!(piece=='p') || !(col>4));
    }
    
    public void study(Example e){
        board.initBoard();
        Move prevMove = new Move();
        for (int i=0; i<10; i++){
            String white;
            white = e.getMove(i,0);
            Move needless = new Move();
            needless = board.executeMove(white);
            for (int j=0; j<n; j++){
                String s;
                s = e.getMove(i,1);
                Move move = new Move();
                move = board.executeMove(s);
                if (j==0 && rule0(move)){
                    firstLayer[i][0]++;
                }
                else if (j==1 && i>0 && rule1(prevMove,move)){
                    firstLayer[i][1]++;
                }
                else if (j==2 && rule2(board)){
                    firstLayer[i][2]++;
                }
                else if (j==3 && rule3(board)){
                    firstLayer[i][3]++;
                }
                else if (j==4 && rule4(board,move)){
                    firstLayer[i][4]++;
                }
                else if (j==5 && rule5(board,move)){
                    firstLayer[i][5]++;
                }
                else if (j==6 && rule6(board,move)){
                    firstLayer[i][6]++;
                }
                else if (j==7 && rule7(board,move)){
                    firstLayer[i][7]++;
                }
                else if (j==8 && rule8(move)){
                    firstLayer[i][8]++;
                }
                else if (j==9 && rule9(move)){
                    firstLayer[i][9]++;
                }
                else if (j==10 && rule10(move)){
                    firstLayer[i][10]++;
                }
                else if (j==11 && rule11(move)){
                    firstLayer[i][11]++;
                }
                else if (j==12 && rule12(move)){
                    firstLayer[i][12]++;
                }
                else if (j==13 && rule13(board,move)){
                    firstLayer[i][13]++;
                }
                else if (j==14 && rule14(move)){
                    firstLayer[i][14]++;
                }
                prevMove = move;
            }
        }
    }
    
    public Move play(Chessboard board, int moveCount, Move prevMove){
        double[] row = new double[n];
        for (int i=0; i<n; i++){
            row[i] = firstLayer[moveCount][i];
        }
        ArrayList<Move> moves = new ArrayList<Move>();
        moves = board.bLegalMoves();
        int len = moves.size();
        boolean flag = true;
        int i = 0;
        while (flag && i<n){
            int next = maxMove(row);
            row[next] = -1;
            ArrayList<Move> temp = new ArrayList<Move>(len);
            temp.addAll(moves);
            for (int j=0; j<len; j++){
                char[][] nextBoard = new char[8][8];
                for (int s=0; s<8; s++){
                    for (int t=0; t<8; t++){
                        nextBoard[s][t] = board.getSquare(s,t);
                    }
                }
                Chessboard nextChessboard = new Chessboard(nextBoard);
                Move move = new Move();
                move = moves.get(j);
                String sMove = move.toString();
                Move m1 = new Move();
                m1 = nextChessboard.executeMove(sMove);
                if (next==0 && !rule0(move)){
                    moves.remove(j);
                    j--;
                }
                else if (next==1 && moveCount>0 && !rule1(prevMove,move)){
                    moves.remove(j);
                    j--;
                }
                else if (next==2 && !rule2(nextChessboard)){
                    moves.remove(j);
                    j--;
                }
                else if (next==3 && !rule3(nextChessboard)){
                    moves.remove(j);
                    j--;
                }
                else if (next==4 && !rule4(nextChessboard,move)){
                    moves.remove(j);
                    j--;
                }
                else if (next==5 && !rule5(nextChessboard,move)){
                    moves.remove(j);
                    j--;
                }
                else if (next==6 && !rule6(nextChessboard,move)){
                    moves.remove(j);
                    j--;
                }
                else if (next==7 && !rule7(nextChessboard,move)){
                    moves.remove(j);
                    j--;
                }
                else if (next==8 && !rule8(move)){
                    moves.remove(j);
                    j--;
                }
                else if (next==9 && !rule9(move)){
                    moves.remove(j);
                    j--;
                }
                else if (next==10 && !rule10(move)){
                    moves.remove(j);
                    j--;
                }
                else if (next==11 && !rule11(move)){
                    moves.remove(j);
                    j--;
                }
                else if (next==12 && !rule12(move)){
                    moves.remove(j);
                    j--;
                }
                else if (next==13 && !rule13(board,move)){
                    moves.remove(j);
                    j--;
                }
                else if (next==14 && !rule14(move)){
                    moves.remove(j);
                    j--;
                }
                len = moves.size();
            }
            if (moves.isEmpty()){
                flag = false;
                moves.addAll(temp);
            }
            i++;
        }
        int length = moves.size();
        Random rand = new Random();
        int moveNo = rand.nextInt(length);
        Move last = new Move();
        last = moves.get(moveNo);
        return last;
    }
    
    public int maxMove(double[] array){
        int length = array.length;
        double max = array[0];
        int pos = 0;
        for (int i=0; i<length; i++){
            if (array[i]>max){
                max = array[i];
                pos = i;
            }
        }
        return pos;
    }
    
    public void train(File dir)throws FileNotFoundException{//Trains the agent on multiple examples.
        initFirstLayer();
        int i = 1;//Debugging
        for (File file : dir.listFiles()){
            Example e = new Example(file);
            study(e);
        }
        standardizeFirstLayer();
    }
}