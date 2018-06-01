import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SwitchToItemListener implements ListSelectionListener {

    private JList<Item> itemJList;
    private Client frame;

    //basic listener for a list of items, switching to the frame of the item selected
    public void valueChanged(ListSelectionEvent e) {
        //if the index isn't -1
        if(itemJList.getSelectedIndex()!=-1) {
            //get the model from the JList, this means that the model, this listener is referencing doesn't need to be update each time
            DefaultListModel<Item> itemListModel = (DefaultListModel<Item>) itemJList.getModel();
            //get the selected index of the JList
            int selected = itemJList.getSelectedIndex();
            //tell the frame to switch to the panel of the selected Item
            frame.switchToItemPanel(itemListModel.get(selected));
        }
    }

    public SwitchToItemListener(Client frame, JList<Item> itemJList){
        this.frame=  frame;
        this.itemJList = itemJList;
    }
}
