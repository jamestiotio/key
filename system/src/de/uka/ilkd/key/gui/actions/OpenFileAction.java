package de.uka.ilkd.key.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import de.uka.ilkd.key.gui.IconFactory;
import de.uka.ilkd.key.gui.KeYFileChooser;
import de.uka.ilkd.key.gui.MainWindow;
import de.uka.ilkd.key.util.GuiUtilities;

public class OpenFileAction extends MainWindowAction {
    
    /**
     * 
     */
    private static final long serialVersionUID = -8548805965130100236L;

    public OpenFileAction(MainWindow mainWindow) {
	super(mainWindow);
        setName("Load...");
        setIcon(IconFactory.openKeYFile(MainWindow.TOOLBAR_ICON_SIZE));
        setTooltip("Browse and load problem or proof files.");
        setAcceleratorLetter(KeyEvent.VK_O);
    }
    
    public void actionPerformed(ActionEvent e) {
        KeYFileChooser keYFileChooser = 
            GuiUtilities.getFileChooser("Select file to load proof or problem");
        
        boolean loaded = keYFileChooser.showOpenDialog(mainWindow);
        
        if (loaded) {
            File file = keYFileChooser.getSelectedFile();
            mainWindow.loadProblem(file);
        }
        
    }
}