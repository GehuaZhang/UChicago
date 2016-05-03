//Note: You need to change to file direction in 59 lines to load data.

package Preparing;
import java.io.*;  //Package for basic operations
import java.util.*;  //Package for data split

public class CovarianceMatrix {
		
	//CalculateSingleResult, Multiplication and Transpose, those three functions are used for Matrix Operations, but not used in this program.
		
		//Matrix multiplication
		public float CalculateSingleResult(float[][] matrixa, float[][] matrixb, int row, int col) {  
			float result = 0;  
			for(int k=0; k<matrixa[0].length; k++) {  
				result += matrixa[row][k] * matrixb[k][col];  
			}  
			return result;  
		}  

		public float[][] Multiplication(float[][] matrixa, float[][] matrixb) {    
			float[][] result = new float[matrixa.length][matrixb[0].length];  
	        for(int i=0; i<matrixa.length; i++) {  
	            for(int j=0; j<matrixb[0].length; j++) {
	                result[i][j] = CalculateSingleResult(matrixa, matrixb, i, j);   
	            }  
	        }  
	        return result;  
		} 
		
		//Matrix Transpose
		public float[][] Transpose(float[][] matrixa){
			float[][] result = new float[matrixa[0].length][matrixa.length];
	        for(int i=0; i<matrixa.length; i++) {  
	            for(int j=0; j<matrixa[0].length; j++) {
	                result[j][i] = matrixa[i][j];
	            }
	        }
	        return result;
		}
		
		
	// Find the Expectation of an array
		
		public float Expectation(float[] vectora){
			float result = 0.0f;
			float sums = 0.0f;
			for(int i=0;i<vectora.length;i++){
				sums = sums + vectora[i];
			}
			result = sums / vectora.length;
			return result;
		}
	
	// Main function. 
		//Firstly try to load data, if fails, catch exception and end program
	    
		public static void main(String[] args) { 
	        try { 
	            File csv = new File("/Users/zee/Documents/JAVAPrograms/Finance/src/Preparing/StockPrice.csv");  //Load data from csv
	            BufferedReader a = new BufferedReader(new FileReader(csv));
	            String line = "";
	            StringBuffer PriceStringBuffer = new StringBuffer();
	            
	            //Count data size and split data
	            int StockNum = 0;
	            int TotalLen = 0;
	            int DataLen = 0;
	            while ((line = a.readLine()) != null) {
	            	StockNum ++;
	                StringTokenizer st = new StringTokenizer(line, ",");
	                while (st.hasMoreTokens()) { 
	                	PriceStringBuffer.append(st.nextToken() + " ");	
	                }
	            }
	            String PriceString = PriceStringBuffer.toString();
	            String PriceList[] = PriceString.split(" ");
	            TotalLen = PriceList.length;
	            DataLen = TotalLen/StockNum;
	            a.close(); //close data
	            
	            //Convert data to float from string
	            float PriceMatrix[][] = new float[StockNum][DataLen];
	            for(int i=0;i<StockNum;i++){
	            	for(int j=0;j<DataLen;j++){
	            		PriceMatrix[i][j] = Float.parseFloat(PriceList[DataLen*i+j]);
	            	}
	            }
	            
	            //Declare an object from class and an instantiation
	            CovarianceMatrix MatrixOperation = new CovarianceMatrix();
	            
	            //Calculate the rate of rate of the each stock
	            float EarnRateMatrix[][] = new float[StockNum][DataLen];
	            for(int i=0;i<StockNum;i++){
	            	float Mean = MatrixOperation.Expectation(PriceMatrix[i]);
	            	for(int j=0;j<DataLen;j++){
	            		EarnRateMatrix[i][j] = PriceMatrix[i][j]/Mean - 1;
	            	}
	            }

	            //Calculate Covariance Matrix.
	            //Math: Cov(a,b) = E(a*b)-E(a)*E(b)
	            float CovMatrix[][] = new float[StockNum][StockNum];
	            for(int i=0;i<StockNum;i++){
	            	for(int j=0;j<StockNum;j++){
	            		float MulVector[] = new float[DataLen];
	            		for(int m=0;m<DataLen;m++){
	            			MulVector[m] = EarnRateMatrix[i][m]*EarnRateMatrix[j][m];
	            		}
	            		CovMatrix[i][j] = MatrixOperation.Expectation(MulVector) - MatrixOperation.Expectation(EarnRateMatrix[i])*MatrixOperation.Expectation(EarnRateMatrix[j]);
	            	}
	            }
	            
	            //Print out Covariance Matrix
	            System.out.println("There are " + StockNum + " stocks in this Matrix."); 
	            System.out.println("Each stock contains " + DataLen + " price data.");
	            System.out.println("Covariance Matrix: ");
	            for(int i=0; i<CovMatrix.length; i++) {  
	                for(int j=0; j<CovMatrix[0].length; j++) {  
	                    System.out.print("  "+ CovMatrix[i][j]);
	                }  
	                System.out.println();  
	            }

	            
	        } catch (FileNotFoundException e) { 
	            // Catch exceptions when open data 
	            e.printStackTrace(); 
	        } catch (IOException e) { 
	            // Catch exceptions when close data
	            e.printStackTrace(); 
	        } 
	    } 
}

