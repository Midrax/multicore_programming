import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.locks.ReentrantLock;


/** classe più generale nella quale viene richiamato l'algoritmo di soluzoine **/

public class ParSudoku {
	
	/** valori di
	 * @param emptyCells numero di caselle vuote
	 * @param nSol		 numero di soluzioni totali trovate, Integer per permettere il lock
	 * @param lock 		 variabile per permettere lock
	 * @param sudoku	 matrice generata dal parse
	 */
	int emptyCells = 0;															
	private Integer nSol = new Integer(0);										
	private ReentrantLock lock = new ReentrantLock();							
	private int[][] sudoku = new int[9][9];										

	/** Costruttore della classe, prende in input un path di file,
	 *  ne fa il parse, salva la matrice letta nel campo relativo
	 *  @param fileName path del file di cui fare il parse
	 */
	
	@SuppressWarnings("resource")
	public ParSudoku(String fileName)
	{
		String text = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			StringBuffer sb = new StringBuffer();
			String line = null;
			while((line = br.readLine())!=null)
			{
				sb.append(line);
			}
			text = sb.toString();
		}
		catch (FileNotFoundException ex){
		    ex.printStackTrace();
		} 
		catch (IOException e){
			e.printStackTrace();
		}
		
		int[][] temp = new int[9][9];
		int k = 0;
		for (int i=0; i<9; i++)
			for (int j=0; j<9; j++)
			{
				if (text.charAt(k)=='.'){
					temp[i][j] = 0;
					emptyCells++;
					k++;
				}
				else{
					temp[i][j] = Character.getNumericValue(text.charAt(k));
					k++;
				}
			}
		sudoku = temp;
	}
	
	/** Metodo in cui viene iniziata la risoluzione ricorsiva, 
	 * se il numero di caselle vuote è minore di 10 chiama quella sequenziale
	 * altrimenti inzia la generazione ad albero dei processi
	 * @param cells matrice di partenza;
	 * @return numero di soluzioni totali;
	 */
	
	public int solve(int[][] cells){
		if (emptyCells<10)
		{
			new BackTrack(0, 0, cells, 0).solveBack(0, 0, cells);
			return nSol;
		}
		else{
			BackTrack b = new BackTrack(0, 0, cells, 0);
			b.compute();
			return nSol;
		}
	}

	
	/** ------ Classe annidata con algoritmo risolutore parallelo e sequenziale------ **/
	
	@SuppressWarnings("serial")
	public class BackTrack extends RecursiveAction
	{
	
		int[][] cells;
		private int i, j, depth;	
		
		/**Costruttore del nuovo processo con valori di
		 * @param i 	numero riga
		 * @param j 	numero colonna
		 * @param cells matrice attuale
		 * @param depth numero di caselle già riempite
		 */
		
		public BackTrack(int i, int j, int[][] cells, int depth){
			this.cells = cells;
			this.i = i;
			this.j = j;		
			this.depth = depth;
		}
		
		
		/** Override del metodo compute, 
		 *  prevede la generazione di nuovi processi solo se:
		 *  il numero di caselle già riempite è minore di 10 (depth<=10);
		 *  la casella analizzata è diversa da 0, altrimenti richiama se stesso con valore i diverso;
		 *  altrimenti richiama il metodo sequenziale.
		 *  Creiamo nuove variabili ogni volta per evitare concorrenza tra i vari processi
		 */
		@Override
		public void compute()
		{
			if (depth>10)
			{
				solveBack(i, j, cells);
				return;
			}
			else if (cells[i][j]!=0){
				i++;
				depth++;
				if (i < 9){
					compute();
				}
			}
			else{
				ArrayList<BackTrack> threads = new ArrayList<BackTrack>();
				for (int val=1; val<10; val++)
					if (isOK(i, j, val, cells)) {
						int[][] newcells = duplicate(cells);
						newcells[i][j] = val;
						BackTrack thread = null;
						int newdepth = depth+1;
						thread = new BackTrack(i, j, newcells, newdepth);
						threads.add(thread);
					}
				for (int k=1; k<threads.size(); k++)
					threads.get(k).fork();
				if (!threads.isEmpty()) threads.get(0).compute();
				for (int k=1; k<threads.size(); k++)
					threads.get(k).join();
			}
		}
		
		/** Metodo per controllare se è possibile inserire un valore nella determinata casella.
		 * Controlla la riga, la colonna e la matrice 3x3 di appartenenza.
		 * Prende in input:
		 * @param i riga;
		 * @param j colonna;
 		 * @param val valore da analizzare;
		 * @param cells matrice di sudoku aggiornata;
		 * @return true se ok, false altrimenti;
		 */
		public boolean isOK(int i, int j, int val, int[][] cells){
	    		for (int k = 0; k < 9; ++k)
	                if (val == cells[k][j])
	                    return false;

	            for (int k = 0; k < 9; ++k)
	                if (val == cells[i][k])
	                    return false;

	            int boxRowOffset = (i / 3)*3;
	            int boxColOffset = (j / 3)*3;
	            for (int k = 0; k < 3; ++k)
	                for (int m = 0; m < 3; ++m)
	                    if (val == cells[boxRowOffset+m][boxColOffset+k])
	                        return false;

	            return true;
		}
		
		/** Metodo risolutore sequenziale. Utilizza un lock sul numero di soluzioni per evitare situazioni di data race.
		 * Prende in input:
		 * @param i riga;
		 * @param j colonna;
		 * @param cells matrice di sudoku aggiornata;
		 */
		public void solveBack(int i, int j, int[][] cells)
		{
	        if (i == 9){
	            i = 0;	
	            j++;
	            if (j == 9){		

					lock.lock();
					nSol++;
					lock.unlock();
					return;
	            }
	        }
	        if (cells[i][j] != 0){
		    	solveBack(i+1, j, cells);
		    	return;
		    }
		    for (int val = 1; val<10; ++val) 
		    	if (isOK(i,j,val,cells)) {
		    		cells[i][j] = val;
		    		solveBack(i, j, cells);

		    	}
		    cells[i][j] = 0;
		}
		
		/** Metodo per la generazione di un duplicato di una matrice di sudoku, 
		 * per evitare di salvare in una variabile la sola referenza alla locazione di memoria,
		 * così da evitare race conditions.
		 * @param cells matrice di sudoku originale
		 * @return ritorna il duplicato di cells in altra locazione di memoria
		 */
		public int[][] duplicate(int[][] cells) {
	        return Arrays.stream(cells).map(el -> el.clone()).toArray(val -> cells.clone());
	    }
	}
	
	/** ----- Metodi getter dei parametri ------**/
    public int[][] getSudoku() { return sudoku; }
    
    public int getNsol() { return nSol;}
	
}                        