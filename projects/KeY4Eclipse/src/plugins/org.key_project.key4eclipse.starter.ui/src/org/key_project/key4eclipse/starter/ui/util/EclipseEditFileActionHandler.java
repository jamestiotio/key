package org.key_project.key4eclipse.starter.ui.util;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.widgets.Display;
import org.key_project.key4eclipse.util.eclipse.ResourceUtil;
import org.key_project.key4eclipse.util.eclipse.WorkbenchUtil;

import de.uka.ilkd.key.gui.actions.EditMostRecentFileAction.EditFileActionHandler;

/**
 * Implementation of {@link EditFileActionHandler} that tries to open
 * files in Eclipse or to select folders in Eclipse if possible.
 * If no workspace {@link IResource} is available the default system
 * implementation is used to handle the operation.
 * @author Martin Hentschel
 */
public class EclipseEditFileActionHandler extends EditFileActionHandler {
    /**
     * {@inheritDoc}
     */
    @Override
    public void workWithFile(File file) throws IOException {
        final IResource[] resources = ResourceUtil.findResourceForLocation(file);
        if (resources.length == 1 && resources[0] != null) {
            // One resource found, handle it in Eclipse
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (resources[0] instanceof IFile) {
                            WorkbenchUtil.openEditor((IFile)resources[0]);
                        }
                        else {
                            WorkbenchUtil.selectAndReveal(resources[0].getProject());
                        }
                    }
                    catch (Exception e) {
                        LogUtil.getLogger().logError(e);
                        LogUtil.getLogger().openErrorDialog(null, e);
                    }
                }
            });
        }
        else {
            // None or multiple resources found, use default KeY implementation
            super.workWithFile(file);
        }
    }
}