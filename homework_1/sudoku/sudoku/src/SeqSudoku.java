import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class SeqSudoku {

	/** Inizializzazione dei campi:
	 *  @param sudoku matrice del sudoku inizializzata dal parse del file preso in input
	 *  @param nSol   numero di soluzioni totali del sudoku
	 */
	private int[][] sudoku = new int[9][9];
	private int nSol=0;

	/** Costruttore della classe,
	 *  prende in input:
	 *  @param fileName path del file di cui fare il parse;
	 *  Esegue il parse e salva sul campo sudoku
	 */
	@SuppressWarnings("resource")
	public SeqSudoku(String fileName)
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
		
		//Insert elements from String text into Sudoku grid
		int[][] temp = new int[9][9];
		int k = 0;
		for (int i=0; i<9; i++)
			for (int j=0; j<9; j++)
			{
				if (text.charAt(k)=='.'){
					temp[i][j] = 0;
					k++;
				}
				else{
					temp[i][j] = Character.getNumericValue(text.charAt(k));
					k++;
				}
			}
		sudoku = temp;
	}

		
	/** Metodo di risoluzione sequenziale del sudoku utilizzando backtracking.
	 * Prende in input:
	 * @param i numero riga;
	 * @param j numero colonna;
	 * @param cells matrice aggiornata;
	 * Aggiorna il numero di soluzioni ogni volta che si arriva al termine della matrice.
	 * Ogni volta che torna indietro pone uguale a zero la casella analizzata.
	 */
	public void solveBack(int i, int j, int[][] cells)
	{
        if (i == 9){
            i = 0;	
            j++;
            if (j == 9){		
            	nSol++;	
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

	/** Metodo per controllare se è possibile inserire un valore nella determinata casella.
	 * Controlla la riga, la colonna e la matrice 3x3 di appartenenza.
	 * Prende in input:
	 * @param i riga;
	 * @param j colonna;
	 * @param val valore da analizzare;
	 * @param cells matrice di sudoku aggiornata;
	 * @return true se ok, false altrimenti;
	 */
    public boolean isOK(int i, int j, int val, int[][] cells) 
    {
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
	            if (val == cells[boxRowOffset+k][boxColOffset+m])
	                return false;
	
	    return true;
    }
    
    /** ----- Metodi getter dei parametri ------**/
    public int[][] getSudoku() { return sudoku; }
    
    public int getNsol() { return nSol;}
	
}
