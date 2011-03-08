/*
 *  DetailsTableModel.java
 *      
 *  Copyright 2010-2011 Kévin Subileau. 
 *
 *	This file is part of ENTDownloader.
 *    
 *  ENTDownloader is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  ENTDownloader is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; If not, see <http://www.gnu.org/licenses/>.
 */
package entDownloader.gui.Components.filesViews.detailview;

import static entDownloader.core.Misc.addZeroBefore;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import entDownloader.core.FS_Element;
import entDownloader.core.FS_File;

public class DetailsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 4563138001193938023L;
	private Object[] columnNames = new Object[] { "Nom", "Type", "Taille",
			"Date de création" };
	private Vector<Object> rowData = new Vector<Object>();

	public int getColumnIndex(Class<?> colClass) {
		if (colClass == null)
			return -1;
		for (int i = 0; i < columnNames.length; i++) {
			if (this.getColumnClass(i) == colClass)
				return i;
		}

		return -1;
	}

	/**
	 * Returns the {@link entDownloader.core.FS_Element FS_Element} 
	 * found at the specific index.
	 * 
	 * @return The {@link entDownloader.core.FS_Element FS_Element} 
	 * found at the specific index.
	 */
	public FS_Element getFileAt(int index) {
		FS_Element file = null;
		try {
			file = (FS_Element) getValueAt(index, 0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return file;
	}

	public void browseDirectory(List<FS_Element> dirContent) {
		clear(); //suppression du contenu précédent
		final Vector<FS_Element> allFiles = new Vector<FS_Element>();
		if (dirContent == null)
			return;

		for (FS_Element el : dirContent) {
			Vector<Object> v = new Vector<Object>();
			v.addElement(el);

			v.addElement(entDownloader.gui.Misc.getFileTypeDescription(el));

			if (el.isDirectory()) {
				v.addElement("");
			} else {
				v.addElement(FS_File.size_Formatted(((FS_File) el).getSize()));
			}
			GregorianCalendar dateModif = el.getDateModif();
			v.addElement(addZeroBefore(dateModif.get(Calendar.DATE)) + "/"
					+ addZeroBefore(dateModif.get(Calendar.MONTH) + 1) + "/"
					+ addZeroBefore(dateModif.get(Calendar.YEAR)) + " "
					+ addZeroBefore(dateModif.get(Calendar.HOUR_OF_DAY)) + ":"
					+ addZeroBefore(dateModif.get(Calendar.MINUTE)));
			addRow(v);
			allFiles.addElement(el);
		}
	}

	@Override
	public Class<? extends Object> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	/** Removes all rows from the data model. */
	public void clear() {
		int oldSize = rowData.size();
		rowData.clear();
		this.fireTableRowsDeleted(0, oldSize);
	}

	/** Add a row to the data model. */
	public void addRow(Vector<Object> newRow) {
		rowData.add(newRow);
		this.fireTableRowsInserted(rowData.size(), rowData.size());
	}

	/** Add a row to the data model. */
	public void addRow(Object[] newRow) {
		addRow(entDownloader.gui.Misc.convertToVector(newRow));
	}

	/** Returns the column name */
	@Override
	public String getColumnName(int column) {
		return columnNames[column].toString();
	}

	/** Returns the number of rows */
	@Override
	public int getRowCount() {
		return this.rowData.size();
	}

	/** Returns the number of columns */
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	/** Returns the element at the specified row and column */
	@Override
	@SuppressWarnings("unchecked")
	public Object getValueAt(int row, int column) {
		if (rowData.size() <= row)
			return null;
		return ((Vector<Object>) rowData.elementAt(row)).elementAt(column);
	}

	/** Returns whether the cell is editable. It isn't. */
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	/** Sets an object at the specified row and column */
	@Override
	@SuppressWarnings("unchecked")
	public void setValueAt(Object value, int row, int column) {
		((Vector<Object>) rowData.elementAt(row)).setElementAt(value, column);
		fireTableCellUpdated(row, column);
	}
}
