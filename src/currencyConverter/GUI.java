package currencyConverter;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GUI {
	
	public GUI() throws StreamReadException, DatabindException, IOException {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Currency converter");
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		try {
		File file = new File("./currencyData.json");
		
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> mapFromJson = objectMapper.readValue(file, Map. class); // Read JSON file and parse it into a Map
        HashMap<String, Object> usdRates = new HashMap<>(mapFromJson); // Convert the Map to a HashMap
		
        Set<String> currencyKeys = usdRates.keySet();
        List<String> currencyList = new ArrayList<>(currencyKeys);
		String[] currencies = currencyList.toArray(new String[0]); //putting all of the HashMap keys into an array
		
		JPanel leftPanel = new JPanel();
		JTextField inputBox = new JTextField(10); //user input text field
		inputBox.addKeyListener(new KeyAdapter() { //KeyListener only for numbers
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE) || (c == KeyEvent.VK_PERIOD))) {
                    Toolkit.getDefaultToolkit().beep();
                    e.consume();
                }
            }
        });
		leftPanel.add(inputBox);
		
		JComboBox<String> currencyBox1 = new JComboBox<>(currencies); //first drop down list
		leftPanel.add(currencyBox1);
		mainPanel.add(leftPanel, BorderLayout.LINE_START);
		
		JPanel rightPanel = new JPanel();
		JTextField answerBox = new JTextField(10); //answer text field
		answerBox.addKeyListener(new KeyAdapter() { //blocking all input
		    @Override
		    public void keyTyped(KeyEvent e) {
		        e.consume();
		    }

		    @Override
		    public void keyPressed(KeyEvent e) {
		        e.consume();
		    }

		    @Override
		    public void keyReleased(KeyEvent e) {
		        e.consume();
		    }
		});
		rightPanel.add(answerBox);
		
		JComboBox<String> currencyBox2 = new JComboBox<>(currencies); //second drop down list
		rightPanel.add(currencyBox2);
		mainPanel.add(rightPanel, BorderLayout.LINE_END);
		
		JButton convertButton = new JButton("Convert"); //calculation button
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputBox.getText(); //taking initial currency number
                String givenType = (String) currencyBox1.getSelectedItem(); //taking user selected initial currency type
                String toConvertToType = (String) currencyBox2.getSelectedItem(); //taking user selected final currency type
                
                BigDecimal amount = new BigDecimal((String) input);
                
                BigDecimal givenTypeRate = new BigDecimal((String) usdRates.get(givenType)); 
    			BigDecimal toConvertToRate = new BigDecimal((String) usdRates.get(toConvertToType));

    			BigDecimal amountInUsd = amount.divide(givenTypeRate, 20, RoundingMode.HALF_UP); //not great to round up here, temporary solution
    			BigDecimal finalAmount = amountInUsd.multiply(toConvertToRate);

    			finalAmount = finalAmount.setScale(2, RoundingMode.HALF_UP);
    			String finalAmountToString = finalAmount.toString();
    			answerBox.setText(finalAmountToString); //printing answer
            }
        });
        mainPanel.add(convertButton, BorderLayout.SOUTH);
		
		frame.add(mainPanel);
		frame.pack();
		frame.setVisible(true);
		} catch (IOException e) {
            System.err.println("Error reading or parsing currency data: " + e.getMessage());
            JOptionPane.showMessageDialog(frame, "Failed to load currency data.\nPlease ensure the currency data file is available and correctly formatted.", "Error Loading Currency Data", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args) throws Exception {
		
		CurrencyScraping.updateCurrencyData();
		
		new GUI();
	}
}