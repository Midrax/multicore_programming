
import java.math.BigInteger;

/** Classe main, utile per far girare il programma.
 *  Qui vengono inizializzate le variabili, ParSudoku e SeqSudoku con la stessa matrice di sudoku presa in input da file.
 *  Durante l'esecuzione viene calcolato il tempo di risoluzione di entrambi gli algoritmi (parallelo e sequenziale).
 *  Stampa il fattore di riempimento della matrice ricevuta in input.
 *  Viene stampato il valore dello spazio delle soluzioni del sudoku.
 *  Viene infine stampato il calcolo dello speedUp.
 *  @authors S.Sakib, Tiziano
 */

public class Main {
	
	public static void main(String[] sudokuPath) 
	{
		//ISTRUZIONI DA TERMINALE
		//javac Main.java; java Main PATH/TO/TEXTFILE.txt
		
		/**
		//Shortcuts to access files from folder "test1"
		String test1_a = "/Users/Ridam/Documents/workspace/Sudoku/test1/test1_a.txt";
		String test1_b = "/Users/Ridam/Documents/workspace/Sudoku/test1/test1_b.txt";
		String test1_c = "/Users/Ridam/Documents/workspace/Sudoku/test1/test1_c.txt";
		String test1_d = "/Users/Ridam/Documents/workspace/Sudoku/test1/test1_d.txt";
		String test1_e = "/Users/Ridam/Documents/workspace/Sudoku/test1/test1_e.txt";
		
		//Shortcuts to access files from folder "test2"
		String test2_a = "/Users/Ridam/Documents/workspace/Sudoku/test2/test2_a.txt";
		String test2_b = "/Users/Ridam/Documents/workspace/Sudoku/test2/test2_b.txt";
		String test2_c = "/Users/Ridam/Documents/workspace/Sudoku/test2/test2_c.txt";
		String test2_d = "/Users/Ridam/Documents/workspace/Sudoku/test2/test2_d.txt";
		String test2_e = "/Users/Ridam/Documents/workspace/Sudoku/test2/test2_e.txt";
		
		//Shortcuts to access files from folder "debugInstances"
		String game0 = "/Users/Ridam/Documents/workspace/Sudoku/debugInstances/game0.txt";
		String game1 = "/Users/Ridam/Documents/workspace/Sudoku/debugInstances/game1.txt";
		String game2 = "/Users/Ridam/Documents/workspace/Sudoku/debugInstances/game2.txt";
		String game3 = "/Users/Ridam/Documents/workspace/Sudoku/debugInstances/game3.txt";
		**/
		
		String sudoku = sudokuPath[0];
		
		//Inizializzazione della variabile SeqSudoku e calcolo dello spazio delle soluzioni
		SeqSudoku s = new SeqSudoku(sudoku);
		int emptyCells = 0;
		int[][] cellCandidates = new int[9][9];
		int[][] cells = s.getSudoku();
		for (int i=0; i<9; i++)
			for (int j=0; j<9; j++)
				if (cells[i][j] == 0){
					emptyCells++;
					for (int val=1; val<=9; val++)
					{
						if (s.isOK(i, j, val, cells))
							cellCandidates[i][j]++;
					}
				}
		
		//Stampa del numero di caselle vuote e del fattore di riempimento
		System.out.println("empty cells: "+emptyCells);
		double fillFactor = 100.0-emptyCells*100.0/81.0;
		System.out.println("fill factor: "+(int) fillFactor+"%");
		BigInteger searchSpace = BigInteger.valueOf(1);
		for (int i = 0; i<9; i++)
			for (int j = 0; j<9; j++)
				if (cellCandidates[i][j]!=0)
				searchSpace = searchSpace.multiply(BigInteger.valueOf(cellCandidates[i][j]));
		System.out.println("search space before elimination: "+searchSpace+" branches");
		System.out.println();
		
		// Inizializzazione della variabile ParSudoku 
		// Risoluzione mediante algoritmo parallelo
		// Stampa del tempo necessario e delle soluzioni trovate
		System.out.println("solving in parallel...");
    	long start = System.currentTimeMillis();
    	ParSudoku ps = new ParSudoku(sudoku);
    	ps.solve(ps.getSudoku());
        long end = System.currentTimeMillis();
        double partime = end-start;
        System.out.println("done in: "+partime+ " ms");
        System.out.println("solutions: "+ps.getNsol());
        System.out.println();
        
		// Inizializzazione della variabile SeqSudoku (nuovamente per non falsare i tempi di esecuzione)
		// Risoluzione mediante algoritmo sequenziale
		// Stampa del tempo necessario e delle soluzioni trovate  
        System.out.println("solving sequentially...");
        start = System.currentTimeMillis();
        SeqSudoku ss = new SeqSudoku(sudoku);
    	ss.solveBack(0,0,ss.getSudoku());
        end = System.currentTimeMillis();
        double seqtime = end-start;
        System.out.println("done in: "+seqtime+ " ms");
        System.out.println("solutions: "+ss.getNsol());
        System.out.println();
        
        //Stampa dello speedUp
        System.out.println("speedup: "+(seqtime/partime));
        
	}
}