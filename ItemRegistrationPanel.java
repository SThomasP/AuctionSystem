import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;

public class ItemRegistrationPanel extends JPanel {

    private JLabel nameLabel, categoryLabel, descriptionLabel, reservePriceLabel, runForLabel;
    private JTextField nameField;
    private JSpinner reservePriceSpinner, runForSpinner;
    private SpinnerNumberModel runForModel, reservePriceModel;
    private JTextArea descriptionTextArea;
    private JComboBox<String> categoryComboBox;
    private JButton registerButton;
    private Client frame;



    public void init(){
        registerButton  = new JButton("Register Item");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //get all the variables from their respective fields
                String itemName = nameField.getText();
                String itemCategory = (String) categoryComboBox.getSelectedItem();
                String itemDescription = descriptionTextArea.getText();
                LocalDateTime timeClosed = LocalDateTime.now().plusDays((Integer) runForSpinner.getValue());
                double reservePrice = (Double) reservePriceSpinner.getValue();
                int userID = frame.getUsersID();
                //create the item, then send it to the window,
                Item i = new Item(itemName, itemDescription, itemCategory, userID, reservePrice, timeClosed);
                frame.registerItem(i);
                clear();
            }
        });
        //Setting up the GUI for this panel, nothing really that interesting happens here
        setBorder(BorderFactory.createLineBorder(Color.black));
        nameLabel = new JLabel("Item Name:");
        categoryLabel = new JLabel("Item Category:");
        descriptionLabel = new JLabel("Item Description:");
        reservePriceLabel = new JLabel("Reserve Price:");
        runForLabel = new JLabel("Run For:");
        nameField = new JTextField();
        descriptionTextArea = new JTextArea();
        descriptionTextArea.setLineWrap(true);
        //combo box for the list of the categories
        categoryComboBox = new JComboBox<>(Item.categories);
        //spinners for the reserve price and for the close date
        reservePriceModel = new SpinnerNumberModel(1.99,0.01,100000.0,0.01);
        reservePriceSpinner = new JSpinner(reservePriceModel);
        reservePriceSpinner.setEditor( new JSpinner.NumberEditor(reservePriceSpinner,"£0.00"));
        //run for spinner, saying how long the auction will runn for
        runForModel = new SpinnerNumberModel(14,1,31,1);
        runForSpinner = new JSpinner(runForModel);
        runForSpinner.setEditor(new JSpinner.NumberEditor(runForSpinner,"00 days"));
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.ipady=10;
        constraints.anchor=GridBagConstraints.LINE_END;
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(nameLabel,constraints);
        constraints.gridy = 1;
        add(categoryLabel,constraints);
        constraints.gridy = 2;
        add(reservePriceLabel,constraints);
        constraints.gridy = 3;
        add(runForLabel,constraints);
        constraints.gridy = 4;
        add(descriptionLabel,constraints);
        constraints.gridx=1;
        constraints.gridy=0;
        constraints.weightx=1.0;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(nameField,constraints);
        constraints.gridy=1;
        constraints.fill = GridBagConstraints.NONE;
        add(categoryComboBox,constraints);
        constraints.gridy=2;
        add(reservePriceSpinner,constraints);
        constraints.gridy=3;
        add(runForSpinner,constraints);
        constraints.gridy=5;
        constraints.gridx=0;
        constraints.gridwidth=2;
        constraints.weighty=1.0;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        add(descriptionTextArea,constraints);
        constraints.weighty=0.0;
        constraints.weightx=0.0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.gridwidth=1;
        constraints.gridy=6;
        constraints.gridx=1;
        add(registerButton,constraints);

    }

    public void clear(){
        //reset every editable field to its default value
        runForSpinner.setValue(14);
        reservePriceSpinner.setValue(1.99);
        categoryComboBox.setSelectedIndex(0);
        nameField.setText("");
        descriptionTextArea.setText("");

    }

    public ItemRegistrationPanel(Client frame){
        this.frame = frame;
    }
}
