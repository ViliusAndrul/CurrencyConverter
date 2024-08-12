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
import java.util.Scanner;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
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
		
File file = new File("./currencyData.json");
		
        ObjectMapper objectMapper = new ObjectMapper();

        // Read JSON file and parse it into a Map
        Map<String, Object> mapFromJson = objectMapper.readValue(file, Map. class);

        // Convert the Map to a HashMap
        HashMap<String, Object> usdRates = new HashMap<>(mapFromJson);
		
        Set<String> currencyKeys = usdRates.keySet();
        List<String> currencyList = new ArrayList<>(currencyKeys);
        
		String[] currencies = currencyList.toArray(new String[0]);
		
		JPanel leftPanel = new JPanel();
		
		JTextField inputBox = new JTextField(10);
		inputBox.addKeyListener(new KeyAdapter() {
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
		
		JTextField answerBox = new JTextField(10);
		rightPanel.add(answerBox);
		
		JComboBox<String> currencyBox2 = new JComboBox<>(currencies); //second drop down list
		rightPanel.add(currencyBox2);
		mainPanel.add(rightPanel, BorderLayout.LINE_END);
		
		JButton convertButton = new JButton("Convert");
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputBox.getText();
                String givenType = (String) currencyBox1.getSelectedItem();
                String toConvertTo = (String) currencyBox2.getSelectedItem();
                
                BigDecimal amount = new BigDecimal((String) input);
                
                BigDecimal givenTypeRate = new BigDecimal((String) usdRates.get(givenType));
    			BigDecimal toConvertToRate = new BigDecimal((String) usdRates.get(toConvertTo));

    			BigDecimal amountInUsd = amount.divide(givenTypeRate, 6, RoundingMode.HALF_UP); // not great to be rounding up here
    			BigDecimal finalAmount = amountInUsd.multiply(toConvertToRate);

    			finalAmount = finalAmount.setScale(2, RoundingMode.HALF_UP);
    			String finalAmountToString = finalAmount.toString();
    			answerBox.setText(finalAmountToString);
            }
        });
        mainPanel.add(convertButton, BorderLayout.SOUTH);
		
		frame.add(mainPanel);
		frame.pack();
		frame.setVisible(true);
		
	}

	public static void main(String[] args) throws Exception {
		
		CurrencyScraping.updateCurrencyData();
		
		new GUI();
	}
}


//git(command line)+, clean up(clean code, check up on calculation), exe, github, leetcode, change comment color