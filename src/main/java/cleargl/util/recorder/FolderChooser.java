/*-
 * #%L
 * ClearGL facade API on top of JOGL.
 * %%
 * Copyright (C) 2014 - 2025 ClearVolume developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package cleargl.util.recorder;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class FolderChooser extends JFileChooser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private FolderChooser(String pChooserTitle, File pDefaultFolder) {
		super();
		setDialogTitle(pChooserTitle);
		setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		setAcceptAllFileFilterUsed(false);
		setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.isDirectory();
			}

			@Override
			public String getDescription() {
				return "Directories only";
			}

		});
		setCurrentDirectory(pDefaultFolder.getParentFile());
		setSelectedFile(pDefaultFolder);
	}

	public static File openFolderChooser(Component pParent,
			String pChooserTitle,
			File pDefaultFolder) {
		FolderChooser lFolderChooser = new FolderChooser(pChooserTitle,
				pDefaultFolder);

		if (lFolderChooser.showOpenDialog(pParent) == JFileChooser.APPROVE_OPTION) {
			return lFolderChooser.getSelectedFile();
		} else {
			return null;
		}
	}
}
