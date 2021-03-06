package sysolver;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.text.DecimalFormat;

/**
 * <!-- begin-user-doc -->
 * <!--  end-user-doc  -->
 * @generated
 */

public class WindowController {
	@FXML
	private Text actiontarget;
	@FXML
	private TextArea matrixTextArea;
	@FXML
	private TextArea vectorTextArea;

	@FXML
	private TextArea outputTextArea;

	private LUSolver myLuSolver = new LUSolver();
	// private LUSolver myLuSolver = new LUSolver();
	private Boolean isInputError = false;
	DecimalFormat df = new DecimalFormat("####0.00000");

	@FXML
	protected void handleLUButtonAction(ActionEvent event) {
		actiontarget.setText("LU Fact button pressed");
		myLuSolver.reinit(0);
		sendMatrixToSolver(myLuSolver);
		sendVectorToSolver(myLuSolver);
		if (isInputError) {
			outputTextArea.setText("");
		} else {
			String output = "";
			output += "LU Decomposition with scaled partial pivoting\n";
			output += "\n";

			String mat = matrixToString(myLuSolver.getMatrix());
			output += "Original matrix :\n";
			output += mat;
			output += "\n";

			String vect = vectorToString(myLuSolver.getbVector());
			output += "Original vector :\n";
			output += vect;
			output += "\n\n";

			myLuSolver.findPivot();
			myLuSolver.multiplyMatrixToPivot();
			myLuSolver.LUFact();

			String matL = matrixToString(myLuSolver.getLMatrix());
			output += "Lower matrix :\n";
			output += matL;
			output += "\n";

			String matU = matrixToString(myLuSolver.getUMatrix());
			output += "Upper matrix :\n";
			output += matU;
			output += "\n";

			String matP = matrixToString(myLuSolver.getPivot());
			output += "Pivot matrix :\n";
			output += matP;
			output += "\n";
			
			myLuSolver.solve();
			String res = vectorToString(myLuSolver.getxVector());
			output += "Result vector :\n";
			output += res;
			output += "\n\n";

			String det = myLuSolver.calculateDet().toString();
			output += "Determinant = " + det;

			outputTextArea.setText(output);
		}
	}

	@FXML
	protected void handleInvertButtonAction(ActionEvent event) {
		actiontarget.setText("Invert button pressed");
	}

	@FXML
	protected void handleClearButtonAction(ActionEvent event) {
		actiontarget.setText("Clear button pressed");
		matrixTextArea.setText("");
		vectorTextArea.setText("");
		outputTextArea.setText("");
		// mySolver.clear();
	}

	private void sendMatrixToSolver(Solver theSolver) {
		String matrix = matrixTextArea.getText();
		String[] rows = matrix.split("\n");
		int size = rows.length;
		Double[][] A = new Double[size][size];
		this.isInputError = false;
		if (size < 2) {
			isInputError = true;
			System.err.println("Input error : too small matrix (mini 2x2)");
		} else {
			int i = 0;
			for (String row : rows) {
				String[] lineValues = row.split(" ");
				if (lineValues.length != rows.length) {
					// tests if a matrix is square
					System.out.println("Input error : not square matrix");
					isInputError = true;
					break;
				} else {
					Double[] tempRow = new Double[size];
					int j = 0;
					for (String value : lineValues) {
						// For negative numbers
						if (value.startsWith("-")) {
							String tempValue = value.substring(1);
							if (tempValue.matches("\\d+")) {
								tempRow[j] = -(Double) Double.parseDouble(tempValue);
								j++;
							} else {
								// TODO : POPUP
								isInputError = true;
								System.out.println("Input error : matrix containing non digital characters");
								break;
							}
						} else {
							if (value.matches("\\d+")) {
								// tests if the input contains digits
								tempRow[j] = (Double) Double.parseDouble(value);
								j++;
							} else {
								// TODO : POPUP
								isInputError = true;
								System.out.println("Input error : matrix containing non digital characters");
								break;
							}
						}
					}
					if (!isInputError) {
						A[i] = tempRow;
					}
					i++;
				}
			}
		}
		if (!isInputError) {
			theSolver.setMatrix(A);
		}
	}

	private void sendVectorToSolver(Solver theSolver) {
		String vector = vectorTextArea.getText();
		String[] values = vector.split(" ");
		Double[] b = new Double[values.length];

		this.isInputError = false;
		if (vector.isEmpty()) {
			isInputError = true;
			System.out.println("Input error : no vector input");
		} else if (values.length != theSolver.getMatrixSize()) {
			isInputError = true;
			System.out.println("Input error : size of vector different from matrix size");
		} else {
			int i = 0;
			for (String val : values) {
				if (val.startsWith("-")){
					String tempValue = val.substring(1);
					if (tempValue.matches("\\d+")) {
						// tests if the input contains digits
						b[i] = - (Double) Double.parseDouble(tempValue);
						i++;
					} else {
						isInputError = true;
						System.out.println("Input error : vector containing non digital characters");
						break;
					}
				} else {
					if (val.matches("\\d+")) {
						// tests if the input contains digits
						b[i] = (Double) Double.parseDouble(val);
						i++;
					} else {
						isInputError = true;
						System.out.println("Input error : vector containing non digital characters");
						break;
					}
				}
			}
		}
		if (!isInputError) {
			theSolver.setVector(b);
		}
	}

	private String matrixToString(Double[][] matrix) {
		String stringMatrix = "";
		for (Double[] row : matrix) {
			for (Double val : row) {
				stringMatrix += df.format(val).toString() + "  ";
			}
			stringMatrix += "\n";
		}

		return stringMatrix;
	}

	private String vectorToString(Double[] vect) {
		String stringVect = "";
		for (Double val : vect) {
			stringVect += df.format(val).toString() + "  ";
		}
		return stringVect;
	}

}
