import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class ItemsPanel extends JPanel {
	public ItemsPanel() {
		Dimension size = getPreferredSize();
		size.width = 190;
		setPreferredSize(size);
		setBorder(BorderFactory.createTitledBorder("Items"));
	}

}