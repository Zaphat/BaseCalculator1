package controller;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class AppController implements Initializable {
	
	//TAB: Base converter----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	 @FXML TextArea inputText, outputText;
	 @FXML ToggleGroup inGroup, outGroup, logicGroup;
	 @FXML Button swapButton, copyButton, resetButton;
	 @FXML Label copyLabel, inType, outType;
	 @FXML  RadioButton inDec, inBin, inHex, inGray, inOct, inBCD, outDec, outBin, outHex, outGray, outOct, outBCD; 
	 StringBuilder inValue = new StringBuilder(""), outValue = new StringBuilder(""), tmp = new StringBuilder("");	
	 
	 Map<String, String> binToHex = Map.ofEntries(
	         Map.entry("0000", "0"),
	         Map.entry("0001", "1"),
	         Map.entry("0010", "2"),
	         Map.entry("0011", "3"),
	         Map.entry("0100", "4"),
	         Map.entry("0101", "5"),
	         Map.entry("0110", "6"),
	         Map.entry("0111", "7"),
	         Map.entry("1000", "8"),
	         Map.entry("1001", "9"),
	         Map.entry("1010", "A"),
	         Map.entry("1011", "B"),
	         Map.entry("1100", "C"),
	         Map.entry("1101", "D"),
	         Map.entry("1110", "E"),
	         Map.entry("1111", "F")
	 );
	 Map<String, String> hexToBin = Map.ofEntries(
	         Map.entry("0", "0000"),
	         Map.entry("1", "0001"),
	         Map.entry("2", "0010"),
	         Map.entry("3", "0011"),
	         Map.entry("4", "0100"),
	         Map.entry("5", "0101"),
	         Map.entry("6", "0110"),
	         Map.entry("7", "0111"),
	         Map.entry("8", "1000"),
	         Map.entry("9", "1001"),
	         Map.entry("a", "1010"),
	         Map.entry("A", "1010"),
	         Map.entry("b", "1011"),
	         Map.entry("B", "1011"),
	         Map.entry("c", "1100"),
	         Map.entry("C", "1100"),
	         Map.entry("d", "1101"),
	         Map.entry("D", "1101"),
	         Map.entry("e", "1110"),
	         Map.entry("E", "1110"),
	         Map.entry("f", "1111"),
	         Map.entry("F", "1111")
	 );
	 Map<String, String> octToBin = Map.ofEntries(
	         Map.entry("0", "000"),
	         Map.entry("1", "001"),
	         Map.entry("2", "010"),
	         Map.entry("3", "011"),
	         Map.entry("4", "100"),
	         Map.entry("5", "101"),
	         Map.entry("6", "110"),
	         Map.entry("7", "111")        
	 );	
	 Map<String, String> binToOct = Map.ofEntries(
	         Map.entry("000", "0"),
	         Map.entry("001", "1"),
	         Map.entry("010", "2"),
	         Map.entry("011", "3"),
	         Map.entry("100", "4"),
	         Map.entry("101", "5"),
	         Map.entry("110", "6"),
	         Map.entry("111", "7")        
	 );
	 Map<String, String> bcdToDec = Map.ofEntries(
			 Map.entry("0000", "0"),
			Map.entry("0001","1"),
			Map.entry("0010","2"),
			Map.entry("0011","3"),
			Map.entry("0100","4"),
			Map.entry("0101","5"),
			Map.entry("0110","6"),
			Map.entry("0111","7"),
			Map.entry("1000","8"),
			Map.entry("1001","9")
			 );
	 Map<Character, String>decToBCD = Map.ofEntries(
			 Map.entry('0', "0000"),
			Map.entry('1',"0001"),
			Map.entry('2',"0010"),
			Map.entry('3',"0011"),
			Map.entry('4',"0100"),
			Map.entry('5',"0101"),
			Map.entry('6',"0110"),
			Map.entry('7',"0111"),
			Map.entry('8',"1000"),
			Map.entry('9',"1001")
			 );
	
	/*LIVE PROCESSING*/
	 public void liveConvert(StringProperty str, String oldValue, String newValue) { //performs new changes when user types new characters
		 tmp.setLength(0);
		 tmp.append(newValue.replaceAll("\\s+","")).trimToSize();
		 convert(tmp);
		 copyLabel.setVisible(false);
		 outputText.setText(outValue.toString());
	 }
	 public void convert(StringBuilder value) { //converts value into desired type based on selected radio buttons
		 if (inDec.isSelected()) {
			 decimalToBinary(value);
		 } else if (inBin.isSelected()) {
			 binaryToBinary(value);
		 } else if (inHex.isSelected()) {
			 hexadecimalToBinary(value);
		 } else if (inOct.isSelected()) {
			 octalToBinary(value);
		 } else if (inBCD.isSelected()) {
			 bcdToBinary(value);
		 } else if (inGray.isSelected()) {
			 graycodeToBinary(value);
		 }
		 if (outDec.isSelected()) {
				getDecimal();							
			} else if (outBin.isSelected()) {
				getBinary();				
			} else if (outHex.isSelected()) {
				getHexadecimal();				
			} else if (outGray.isSelected()) {
				getGraycode();				
			} else if (outOct.isSelected()) {
				getOctal();
			} else if (outBCD.isSelected()) {
				getBCD();
			}
	 }
	 /*EVENT HANDLERS*/
	public void swap(ActionEvent event) { //swaps between input and output bases
		StringBuilder in = new StringBuilder(outputText.getText()),out = new StringBuilder(inputText.getText().toUpperCase());
		if (!in.toString().matches("^[0-9A-Fa-f]+$")) {
			out.setLength(0);
			in.setLength(0);
		}
		if (inDec.isSelected()) {
			if (outBin.isSelected()) inBin.fire();
			if (outHex.isSelected()) inHex.fire();
			if (outGray.isSelected()) inGray.fire();
			if (outOct.isSelected()) inOct.fire();
			if(outBCD.isSelected()) inBCD.fire();
			outDec.fire();		
		} else if (inBin.isSelected()) {
			if (outDec.isSelected()) inDec.fire();
			if (outBin.isSelected()) inBin.fire();
			if (outHex.isSelected()) inHex.fire();
			if (outGray.isSelected()) inGray.fire();
			if (outOct.isSelected()) inOct.fire();
			if(outBCD.isSelected()) inBCD.fire();
			outBin.fire();
		} else if (inHex.isSelected()) {
			if (outDec.isSelected()) inDec.fire();
			if (outBin.isSelected()) inBin.fire();
			if (outGray.isSelected()) inGray.fire();
			if (outOct.isSelected()) inOct.fire();
			if(outBCD.isSelected()) inBCD.fire();
			outHex.fire();
		} else if (inOct.isSelected()) {
			if (outDec.isSelected()) inDec.fire();
			if (outBin.isSelected()) inBin.fire();
			if (outHex.isSelected()) inHex.fire();
			if (outGray.isSelected()) inGray.fire();
			if(outBCD.isSelected()) inBCD.fire();
			outOct.fire();
		}else if (inGray.isSelected()) {
			if (outDec.isSelected()) inDec.fire();
			if (outBin.isSelected()) inBin.fire();
			if (outHex.isSelected()) inHex.fire();		
			if (outOct.isSelected()) inOct.fire();
			if(outBCD.isSelected()) inBCD.fire();
			outGray.fire();
		} else if (inBCD.isSelected()) {
			if (outDec.isSelected()) inDec.fire();
			if (outBin.isSelected()) inBin.fire();
			if (outHex.isSelected()) inHex.fire();
			if (outGray.isSelected()) inGray.fire();
			if (outOct.isSelected()) inOct.fire();
			outBCD.fire();		
		}
		inputText.setText(in.toString());
		outputText.setText(out.toString());
		event.consume();
	}
	public void changeOutput(ActionEvent event) { //converts result to desired base and relabel output type label		
		if (outDec.isSelected()) 
		{
			outType.setText("0d");
			getDecimal();
						
		} else if (outBin.isSelected()) {
			outType.setText("0b");
			getBinary();
					
		} else if (outHex.isSelected()) {
			outType.setText("0x");
			getHexadecimal();
			
		} else if (outGray.isSelected()) {
			outType.setText("RBC");
			getGraycode();
			
		} else if (outOct.isSelected()) {
			outType.setText("0o");
			getOctal();
		} else if (outBCD.isSelected()) {
			outType.setText("BCD");
			getBCD();
		}
		copyLabel.setVisible(false);
		outputText.setText(outValue.toString());
		event.consume();
	}
	public void changeInput(ActionEvent event) { //resets input text area and relabel the input type label
		inputText.setText("");
		if (inDec.isSelected()) {
			inType.setText("0d");	
			outDec.setSelected(false);
		} else if (inHex.isSelected()) {
			inType.setText("0x");		
			outHex.setSelected(false);
		} else if (inGray.isSelected()) {
			inType.setText("RBC");
			outGray.setSelected(false);
		} else if (inBin.isSelected()) {
			inType.setText("0b");
			outBin.setSelected(false);
		} else if(inOct.isSelected()) {
			inType.setText("0o");
			outOct.setSelected(false);
		} else if (inBCD.isSelected()) {
			inType.setText("BCD");
			outBCD.setSelected(false);
		}
		event.consume();
	}
	public void copyToClipboard(ActionEvent event) {	//copies the converted result  to clipboard
		String myString = outputText.getText();
		StringSelection stringSelection = new StringSelection(myString);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
		copyLabel.setVisible(true);
		event.consume();		
	}
	public void selectAll(MouseEvent event) { //auto selects all when user clicks the result text area	
		outputText.selectAll();
		event.consume();
	}
	public void reset(ActionEvent event) { //reset input text area
		inputText.setText("");
		event.consume();
	}
	/*ARITHMETIC CALCULATIONS*/
	public StringBuilder dividedBy2(StringBuilder num) { //divides big number given as StringBuilder and return result as same type
		
		int tmp=0;
		StringBuilder res = new StringBuilder("");
		for (int i=0; i<num.length(); i++) {
			tmp = tmp*10 + Character.getNumericValue(num.charAt(i));
			res.append(String.valueOf((int)tmp/2));
			tmp %=2;
		 }
	    
		return new StringBuilder(res.toString().replaceFirst("^0+(?!$)", ""));

	}
	public StringBuilder addNumber(StringBuilder a, StringBuilder b) { //adds two big numbers given as StringBuilder and return result as same type
		
		StringBuilder res = new StringBuilder("");
		a.reverse(); b.reverse();
		while (a.length()<b.length()) a.append("0");
		while (b.length()<a.length()) b.append("0");
		int tmp = 0,carry =0;
		for (int i=0; i<a.length(); i++) {
			tmp = Character.getNumericValue(a.charAt(i)) + Character.getNumericValue(b.charAt(i))+carry;
			res.append(String.valueOf(tmp%10));
			carry =(int) tmp/10;
		}
		
		if (carry>0) res.append(String.valueOf(carry));
		res.reverse().trimToSize();
		return res;
		
	}
	public StringBuilder xMultiply2PowerOfN(int n, char x) { //returns result of '2 to the power of n' as StringBuilder
		if (x=='0') return new StringBuilder("0");
		 StringBuilder num = new StringBuilder("1");
		  for (int j=0; j<n; j++) {
			 int carry=0, tmp=0;
			 for (int i=0; i<num.length(); i++) {
				 tmp = carry + Character.getNumericValue(num.charAt(i))*2;
				 carry= (int) tmp/10;
				num.replace(i, i+1, String.valueOf(tmp%10));
			 }
			if (carry>0) num.append(carry); 
		  }
		  num.reverse().trimToSize();
		  return num;  
	  }
	/*INPUT PROCESSING*/
	public void decimalToBinary(StringBuilder decimal) { //converts decimal input to binary string & store at variable "inValue"
		
		if (decimal.isEmpty()) {
			inValue.setLength(0);
		} else if (!decimal.toString().matches("\\d+")) {
			inValue.setLength(0);
			inValue.append("Invalid decimal input").trimToSize();
		} else if (decimal.toString().equals("0")) {
			inValue.setLength(0);
			inValue.append("0").trimToSize();
		} else {
			int tmp =0;
			StringBuilder bin = new StringBuilder(""), str;
			while (!decimal.toString().equals("0")) {
				tmp = Character.getNumericValue(decimal.charAt(decimal.length()-1));
				if (tmp %2 ==0) bin.append("0");
				else bin.append("1");			
				str = new StringBuilder(decimal);
				decimal.setLength(0);
				decimal.append(dividedBy2(str)).trimToSize();;
		}
			bin.reverse();	
			inValue.setLength(0);
			inValue.append(bin).trimToSize();
		}
		}
	public void binaryToBinary(StringBuilder binary) { //stores binary input at variable "inValue"
		
		if (binary.isEmpty()) { 
			inValue.setLength(0);
			} else if (!binary.toString().matches("^[01]+$")) {
			inValue.setLength(0);
			inValue.append("Invalid binary input").trimToSize();
			}else {
			inValue.setLength(0);
			inValue.append(binary.toString().replaceFirst("^0+(?!$)", "")).trimToSize();
		}
	}
	public void hexadecimalToBinary(StringBuilder heximal) { //converts hexadecimal input to binary string & stores at variable "inValue"		
		if (heximal.isEmpty()) {
			inValue.setLength(0);
		} else if (!heximal.toString().matches("^[0-9A-Fa-f]+$")) {
			inValue.setLength(0);
			inValue.append("Invalid heximal input").trimToSize();
		} else {
			StringBuilder bin = new StringBuilder("");	      
	      for (int i=0; i<heximal.length(); i++)
	    	 bin.append(hexToBin.get(String.valueOf(heximal.charAt(i))));	     
	      inValue.setLength(0);
		  inValue.append(bin.toString().replaceFirst("^0+(?!$)", "")).trimToSize();
		}
		
	}
	public void octalToBinary(StringBuilder octal) {//converts gray code input to binary string & stores at variable "inValue"
		if (octal.isEmpty()) {
			inValue.setLength(0);
		} else if (!octal.toString().matches("^[0-7]+$")) {
			inValue.setLength(0);
			inValue.append("Invalid octal input").trimToSize();
		} else {
			StringBuilder bin = new StringBuilder("");
			for (int i=0; i<octal.length(); i++) {
				bin.append(octToBin.get(String.valueOf(octal.charAt(i))));
			}
			inValue.setLength(0);
			inValue.append(bin.toString().replaceFirst("^0+(?!$)", "")).trimToSize();
		}
	}
	public void graycodeToBinary(StringBuilder gray) { //converts gray code input to binary string & stores at variable "inValue"
		
		if (gray.isEmpty()) {
			inValue.setLength(0);
		} else if (!gray.toString().matches("^[01]+$")) { 
			inValue.setLength(0);
			inValue.append("Invalid graycode input");
			inValue.trimToSize();
		}
		else {
			inValue.setLength(0);
			inValue.append(gray.charAt(0));
			char tmp = gray.charAt(0);
			for (int i=1; i<gray.length(); i++) {				
				if (gray.charAt(i)!=tmp) tmp = '1';
				else tmp='0';
				inValue.append(tmp);
				
			}
			inValue.trimToSize();
		}
	}
	public void bcdToBinary(StringBuilder bcd) { //converts bcd input to binary string & stores at variable "inValue"
		if (bcd.isEmpty()) {
			inValue.setLength(0);
		} else if (!bcd.toString().matches("^[01]+$")) { 
			inValue.setLength(0);
			inValue.append("Invalid format input");
			inValue.trimToSize();
		} else {
		bcd.insert(0, "0".repeat(4-bcd.length()%4));
		StringBuilder bin = new StringBuilder("");
		for (int i=0; i<bcd.length();i+=4) {
			if (!bcdToDec.containsKey(bcd.substring(i, i+4))) {
				inValue.setLength(0);
				inValue.append("Invalid binary-coded decimal input");
				return;
			}
			bin.append(bcdToDec.get(bcd.substring(i, i+4)));
		}
		decimalToBinary(new StringBuilder(bin.toString().replaceFirst("^0+(?!$)", "")));
		}
	}
	/*OUTPUT PROCESSING*/	
	public void getDecimal() { //converts binary value "inValue" into decimal and stores at variable "outValue"
		if (inValue.isEmpty()) {
			outValue.setLength(0);
		} else if (!inValue.toString().matches("^[01]+$")) {
			outValue.setLength(0);
			outValue.append(inValue).trimToSize();
		}  else {			
			StringBuilder sum = new StringBuilder("0"), tmp = inValue.reverse();
			for (int i=0; i<tmp.length(); i++) {
				sum = addNumber(sum,xMultiply2PowerOfN(i,tmp.charAt(i)));
			}			
			inValue.reverse();
			outValue.setLength(0);
			outValue.append(sum).trimToSize();
			}
	}
	public void getBinary() { //converts binary value "inValue" into binary and stores at variable "outValue"
		outValue.setLength(0);
		outValue.append(inValue.toString().replaceFirst("^0+(?!$)", "")).trimToSize();
	}
	public void getHexadecimal() { //converts binary value "inValue" into hexadecimal and stores at variable "outValue"
		if (inValue.isEmpty()) {
			outValue.setLength(0);
		} else if (!inValue.toString().matches("^[01]+$")) {
			outValue.setLength(0);
			outValue.append(inValue).trimToSize();
		} else {
			StringBuilder hex = new StringBuilder(""), tmp = new StringBuilder(inValue);
			if (tmp.length()%4 !=0)
					tmp.insert(0, "0".repeat(4-tmp.length()%4));
			int index=0;
			while (index<tmp.length()) {
				hex.append(binToHex.get(tmp.substring(index,index+4)));
				index+=4;
			}
			outValue.setLength(0);
			outValue.append(hex.toString().replaceFirst("^0+(?!$)", "")).trimToSize();
		}
	}
	public void getOctal() { //converts binary value "inValue" into octal and stores at variable "outValue"
		if (inValue.isEmpty()) {
			outValue.setLength(0);
		} else if (!inValue.toString().matches("^[01]+$")) {
			outValue.setLength(0);
			outValue.append(inValue).trimToSize();
		} else {
			StringBuilder oct = new StringBuilder(""), tmp = new StringBuilder(inValue);
			if (tmp.length()%3 !=0)
				tmp.insert(0, "0".repeat(3-tmp.length()%3));
		int index=0;
		while (index<tmp.length()) {
			oct.append(binToOct.get(tmp.substring(index,index+3)));
			index+=3;
		}
		outValue.setLength(0);
		outValue.append(oct.toString().replaceFirst("^0+(?!$)", "")).trimToSize();
		}
	}
	public void getGraycode() { //converts binary value "inValue" into gray code and stores at variable "outValue"
		if (inValue.isEmpty()) {
			outValue.setLength(0);
		} else if (!inValue.toString().matches("^[01]+$")) {
			outValue.setLength(0);
			outValue.append(inValue).trimToSize();
		}  else {			
			StringBuilder gray = new StringBuilder("");
			gray.append(inValue.charAt(0));
			for (int i=1; i<inValue.length(); i++) {
				if (inValue.charAt(i)!=inValue.charAt(i-1)) gray.append('1');
				else gray.append('0');
			}
			outValue.setLength(0);
			outValue.append(gray).trimToSize();
		}
	}
	public void getBCD() { //converts binary value "inValue" into BCD and stores at variable "outValue"
		if (inValue.isEmpty()) {
			outValue.setLength(0);
		} else if (!inValue.toString().matches("^[01]+$")) {
			outValue.setLength(0);
			outValue.append(inValue).trimToSize();
		}  else {			
			getDecimal();
			StringBuilder bcd = new StringBuilder("");
			for (int i=0; i<outValue.length(); i++) {
				bcd.append(decToBCD.get(outValue.charAt(i)));
			}
			outValue.setLength(0);
			outValue.append(bcd).trimToSize();
		}
	}

	
	//TAB: Logic gates-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	@FXML ToggleButton not,and,or,xor,nor,nand;	
	@FXML ImageView imageView;
	Image notLogic = new Image(getClass().getResourceAsStream("/images/NOT.png")),
			  andLogic = new Image(getClass().getResourceAsStream("/images/AND.png")),
			  orLogic = new Image(getClass().getResourceAsStream("/images/OR.png")),
			  nandLogic = new Image(getClass().getResourceAsStream("/images/NAND.png")),
			  norLogic = new Image(getClass().getResourceAsStream("/images/NOR.png")),
			  xorLogic = new Image(getClass().getResourceAsStream("/images/XOR.png"));
	
	public void changeImage(ActionEvent event) { //change picture based on selected toggle button.
		if (not.isSelected()) {
			imageView.setImage(notLogic);
		} else if (and.isSelected()) {
			imageView.setImage(andLogic);
		} else if (or.isSelected()) {
			imageView.setImage(orLogic);
		} else if (nor.isSelected()) {
			imageView.setImage(norLogic);
		} else if (xor.isSelected()) {
			imageView.setImage(xorLogic);
		} else if (nand.isSelected()) {
			imageView.setImage(nandLogic);
		}
		event.consume();
	}

	//TAB: Graycode Generator-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	@FXML Button genButton,copyButton2,saveButton;
	@FXML ComboBox<Integer> cbBox;
	@FXML TextArea resultArea;
	@FXML ImageView check;
	ObservableList<Integer> nbits = FXCollections.observableArrayList(1,2,3,4,5,6,7,8,9,10,11,12);
	int n_bit,prevN=0;
	StringBuilder out = new StringBuilder("");
	
	@Override	
	public void initialize(URL arg0, ResourceBundle arg1) {
		cbBox.setItems(nbits);		
	}
	public void genGC(ActionEvent event) {	//generates n-bits Gray Code based on ComboBox value and appends to result Area
		n_bit = cbBox.getValue();	
		if (n_bit==prevN) return;
		resultArea.setText("");
		check.setVisible(false);
		prevN=n_bit;
		genButton.setDisable(true);
		for (int i=0; i<Math.pow(2, n_bit); i++) {
			out.setLength(0);
			out.append(Integer.toBinaryString(i^(i>>1)));
			if (out.length()%n_bit>0)
				out.insert(0, "0".repeat(n_bit-out.length()%n_bit));
			resultArea.appendText(out.toString()+"  ");
		}
		genButton.setDisable(false);
		event.consume();
	}
	public void copy2(ActionEvent event) {		//copies generated result to clipboard
		String myString = resultArea.getText();
		StringSelection stringSelection = new StringSelection(myString);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
		check.setVisible(true);
		event.consume();
	}
	public void saveAs(ActionEvent event) {	//opens Directory to save file
		FileChooser chooser = new FileChooser();
		
		chooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"),
																new ExtensionFilter("All Files", "*.*"));
		chooser.setTitle("Save As");
         File file =chooser.showSaveDialog(saveButton.getScene().getWindow());  
         if(file != null){
             SaveFile(resultArea.getText(), file);
         }
         event.consume();
        }
	public void SaveFile(String content, File file){	//copies the result to file
        try {
            FileWriter fileWriter;
             
            fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException ex) {}
           
	}
	
	}
	

