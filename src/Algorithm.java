//import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class Algorithm {
    JFrame frame = new JFrame("Животные");
    DefaultComboBoxModel defaultComboBoxModel;

    private ArrayList<Pair<ArrayList<Attribute>, Attribute>> rules;
    private Stack<Pair<Integer, String> > goals;
    private ArrayList<Attribute> context;
    private TreeSet<String> possibleGoals;
    private HashMap<String, TreeSet<String>> possibleAnswers;
    
    private ArrayList<JTextField> keys;
    private ArrayList<JTextField> values;
    
    private JTextField resultKey;
    private JTextField resultValue;    
    
    public String goal;
    public Algorithm() throws IOException {
        rules = new ArrayList<Pair<ArrayList<Attribute>, Attribute>>();
        goals = new Stack<Pair<Integer, String>>();
        context = new ArrayList<Attribute>();
        
        readRules("src/rules.txt");
        initGUI();
    }

    public void initGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(500, 200));
        frame.setResizable(false);
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        

        JButton addRuleButton = new JButton("Add rule");
        initAddRules();
        addRuleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRule();
                //System.out.println(possibleAnswers.toString());
                //System.out.println(possibleGoals.toString());
                //System.out.println(rules.toString());
            }
        });
        frame.add(addRuleButton);
        
        JLabel label = new JLabel("Выберите цель: \n");
        label.setSize(300, 100);
        frame.add(label);

        final JComboBox goalBox = new JComboBox();
        defaultComboBoxModel = new DefaultComboBoxModel( possibleGoals.toArray() );
        goalBox.setModel(defaultComboBoxModel);
    
        goalBox.setSelectedIndex(0);
        frame.add(goalBox);

        JButton submitButton = new JButton("OK");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goal = (String)goalBox.getSelectedItem();
                make(goal);
                goals.clear();
                context.clear();
            }
        });
        frame.add(submitButton);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private void initAddRules(){
    	keys = new ArrayList<>();
    	values = new ArrayList<>();
    	for (int i = 0; i < 3; i++){
    		keys.add(new JTextField(20));
    		values.add(new JTextField(20));
    	}	
    	resultKey = new JTextField(20);
    	resultValue = new JTextField(20);
    }
    
    private void clearAddRules(){
    	for (int i = 0; i < 3; i++){
    		keys.get(i).setText("");
    		values.get(i).setText("");
    	}	
    	resultKey.setText("");
    	resultValue.setText("");
    }
    
    private void refreshRules(ArrayList<Attribute> key, Attribute value){
    	if (!possibleGoals.contains(value.name)){
    		possibleGoals.add(value.name);
    		defaultComboBoxModel.addElement(value.name);
    	}
        TreeSet<String> tmp = new TreeSet<String>();
        for (int j = 0; j < key.size(); j++) {
            if(possibleAnswers.containsKey(key.get(j).name)) {
                possibleAnswers.get(key.get(j).name).add(key.get(j).value);
            } else {
                tmp = new TreeSet<String>();
                tmp.add(key.get(j).value);
                possibleAnswers.put(key.get(j).name, new TreeSet<String>(tmp));
            }
        }
    }
    
    private void addRuleForm(final JDialog d){
        for (int i = 0; i< keys.size(); i++){
	    	if (i == 0)
	    		d.add(new JLabel("если"));
	    	else	
	    		d.add(new JLabel(""));
	        d.add(keys.get(i));
	        d.add(new JLabel(" = "));
	        d.add(values.get(i));
        } 
        d.add(new JLabel("то"));
        d.add(resultKey);
        d.add(new JLabel(" = "));
        d.add(resultValue);
        
        JButton insert = new JButton("Add");
        insert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if (resultKey.getText() == null || resultKey.getText().trim().isEmpty() ||
            			resultValue.getText() == null || resultValue.getText().trim().isEmpty()){
            		JOptionPane.showMessageDialog(d, "Пустое следствие", "Ошибка", JOptionPane.ERROR_MESSAGE);
            		return;
            	}
            	ArrayList<Attribute> arr = new ArrayList<Attribute>();
            	for (int i = 0; i< keys.size();++i){
            		boolean success = true;
            		if (keys.get(i).getText() == null || keys.get(i).getText().trim().isEmpty())
            				success = false;
            		if (values.get(i).getText() == null || values.get(i).getText().trim().isEmpty())
        				success = false;
            		if (success)
            			arr.add(new Attribute(keys.get(i).getText() + "=" + values.get(i).getText()));
            	}
            	if (arr.size() == 0){
            		JOptionPane.showMessageDialog(d, "Пустое условие", "Ошибка", JOptionPane.ERROR_MESSAGE);
            		return;
            	}
            	Attribute result = new Attribute(resultKey.getText() + "=" + resultValue.getText());
             	rules.add(new Pair<ArrayList<Attribute>, Attribute> (new ArrayList<Attribute>(arr), result));
            	refreshRules(arr, result); 
            	clearAddRules();
            	d.dispose(); 
            }
        });
        d.add(insert);
    }

    public void addRule(){
    	JDialog d1 = new JDialog(frame, "This is title", true);
        d1.setSize(400, 400);
        
        d1.setLayout(new GridLayout(5,4));
        
        addRuleForm(d1);
        d1.setVisible(true);
    }
    
    public void readRules(String fileUrl) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileUrl));
        ArrayList<Attribute> arr = new ArrayList<Attribute>();
        String temp;
        while((temp = reader.readLine()) != null) {
            if (temp.startsWith("если ")) {
                arr = new ArrayList<Attribute>();
                arr.add(new Attribute(temp.substring(5)));
            } else if (temp.startsWith("то ")) {
                rules.add(new Pair<ArrayList<Attribute>, Attribute>(new ArrayList<Attribute>(arr), new Attribute(temp.substring(3))));
            } else {
                arr.add(new Attribute(temp));
            }
        }
        initPossibleGoals();
        initPossibleAnswers();
    }

    private void initPossibleAnswers() {
        possibleAnswers = new HashMap<String, TreeSet<String>>();
        TreeSet<String> tmp = new TreeSet<String>();
        for (int i = 0; i < rules.size(); i++) {
            for (int j = 0; j < rules.get(i).getKey().size(); j++) {
                if(possibleAnswers.containsKey(rules.get(i).getKey().get(j).name)) {
                    possibleAnswers.get(rules.get(i).getKey().get(j).name).add(rules.get(i).getKey().get(j).value);
                } else {
                    tmp = new TreeSet<String>();
                    tmp.add(rules.get(i).getKey().get(j).value);
                    possibleAnswers.put(rules.get(i).getKey().get(j).name, new TreeSet<String>(tmp));
                }
            }
        }
    }

    private void initPossibleGoals() {
        possibleGoals = new TreeSet<String>();
        for (int i = 0; i < rules.size(); i++) {
            possibleGoals.add(rules.get(i).getValue().name);
        }
    }
    
    

    private boolean analyzeRule(int i){
        System.out.println("Rule #" + i);
        ArrayList<Attribute> ifClauses = rules.get(i).getKey();
        int firstNotFound = -1;
        boolean allFound = true;
        for(int j = 0; j < ifClauses.size(); ++j) {
            boolean isFound = false;
            for(Attribute result: context) {
                if(ifClauses.get(j).name.equals(result.name)){
                    isFound = true;
                    if(!ifClauses.get(j).value.equals(result.value)) {
                        System.out.println("Inappropriate rule");
                        return false;
                    }
                }
            }
            if(allFound && !isFound) {
                firstNotFound = j;
                allFound = false;
            }
        }
        if(!allFound) {
            System.out.println("Value not found: " + ifClauses.get(firstNotFound).name);
            goals.push(new Pair<Integer, String>(i, ifClauses.get(firstNotFound).name));
            return true;
        }
        System.out.println("Known attribute: name = " + rules.get(i).getValue().name + ", value = " + rules.get(i).getValue().value);
        context.add(0, rules.get(i).getValue());
        goals.pop();
        return true;
    }

    public void make(String goal) {
        goals.add(new Pair<Integer, String>(-1, goal));
        boolean flag = true;
        while(flag) {
            String currGoal = goals.peek().getValue();
            //int currRule = goals.peek().getKey();
            System.out.println("Goal: " + currGoal);
            boolean hasRule = false;
            boolean analysisResult = false;
            for (int i = 0; i < rules.size(); i++) {
                if(currGoal.equals(rules.get(i).getValue().name)) {
                    analysisResult = analyzeRule(i);
                    if(!analysisResult)
                        continue;
                    else {
                        hasRule = true;
                        if(goals.empty())
                            flag = false;
                        break;
                    }
                }
            }
            if(!hasRule){
                boolean isPossibleGoal = false;
                for(String s: possibleGoals) {
                    if(currGoal.equals(s))
                        isPossibleGoal = true;
                }
                if(!isPossibleGoal) {
                    askQuestion(currGoal);
                    goals.pop();
                }
                else
                    flag = false;
            }
        }
       
        for(Attribute attr: context) {
            if(attr.name.equals(goal)) {
                final Icon icon = new ImageIcon("src/"+attr.value+".jpg");
                JOptionPane.showMessageDialog(frame, goal + ":  " + attr.value, goal + ", что вы искали", JOptionPane.INFORMATION_MESSAGE, icon);
                flag = true;
                //JOptionPane.showMessageDialog(frame, goal + ":  " + attr.value, goal + ", что вы искали", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        if(!flag)
            JOptionPane.showMessageDialog(frame, "Ответ не найден", "Ответ", JOptionPane.ERROR_MESSAGE);
    }

    private void askQuestion(String currGoal) {
        Object[] possAns = possibleAnswers.get(currGoal).toArray();
        String answer = null;
        while (answer == null) {
            answer = (String) JOptionPane.showInputDialog(frame, currGoal + "?", "Дополнительный вопрос",
                    JOptionPane.PLAIN_MESSAGE, null, possAns, possAns[0]);
        }
        System.out.println("Asked attribute: name = " + currGoal + ", value = " + answer);
        context.add(0, new Attribute(currGoal, answer));
    }
}